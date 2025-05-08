package com.example.simplenotes.ui.todo;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import com.airbnb.lottie.LottieAnimationView;
import com.example.simplenotes.R;
import com.example.simplenotes.data.local.entity.TodoItem;
import com.example.simplenotes.utils.AnimationUtils;
import com.example.simplenotes.utils.NotificationHelper;
import com.example.simplenotes.viewmodel.TodoViewModel;
import com.example.simplenotes.workers.TodoExpirationWorker;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;

import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.TimeUnit;

public class TodoFragment extends Fragment {
    private TodoViewModel viewModel;
    private TodoAdapter adapter;
    private NavController navController;
    private LottieAnimationView lottieAnimation;
    private NotificationHelper notificationHelper;
    private AlarmManager alarmManager;
    private Set<Integer> animatedTodoIds = new HashSet<>();
    private int currentNoteId = -1;
    private SharedPreferences prefs;
    private TextView statsTextView;
    private Map<Integer, CountDownTimer> timerMap = new HashMap<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_todo, container, false);

        RecyclerView recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        adapter = new TodoAdapter();
        recyclerView.setAdapter(adapter);

        lottieAnimation = view.findViewById(R.id.lottie_animation);
        statsTextView = view.findViewById(R.id.stats_text_view);
        notificationHelper = new NotificationHelper(requireContext());
        alarmManager = (AlarmManager) requireContext().getSystemService(Context.ALARM_SERVICE);
        prefs = requireContext().getSharedPreferences("SimpleNotesPrefs", Context.MODE_PRIVATE);
        updateStatsDisplay();

        PeriodicWorkRequest expirationWorkRequest = new PeriodicWorkRequest.Builder(TodoExpirationWorker.class, 15, TimeUnit.MINUTES)
                .build();
        WorkManager.getInstance(requireContext()).enqueue(expirationWorkRequest);

        ItemTouchHelper.SimpleCallback itemTouchCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getBindingAdapterPosition();
                TodoItem todo = adapter.getTodoAt(position);
                if (direction == ItemTouchHelper.RIGHT) {
                    AnimationUtils.showDeleteAnimation(requireContext(), () -> {
                        cancelTimerAndReminder(todo);
                        viewModel.delete(todo);
                        Toast.makeText(requireContext(), "To-Do deleted", Toast.LENGTH_SHORT).show();
                    });
                }
            }
        };
        new ItemTouchHelper(itemTouchCallback).attachToRecyclerView(recyclerView);

        viewModel = new ViewModelProvider(this, new ViewModelProvider.AndroidViewModelFactory(requireActivity().getApplication())).get(TodoViewModel.class);
        currentNoteId = getArguments() != null ? getArguments().getInt("noteId", -1) : -1;
        if (currentNoteId == -1) {
            viewModel.getAllTodos().observe(getViewLifecycleOwner(), todos -> adapter.setTodos(todos));
        } else {
            viewModel.getTodosByNoteId(currentNoteId).observe(getViewLifecycleOwner(), todos -> adapter.setTodos(todos));
        }

        view.findViewById(R.id.fab_add_task).setOnClickListener(v -> showAddTodoDialog(currentNoteId));

        adapter.setOnInteractionListener(new TodoAdapter.OnTodoInteractionListener() {
            @Override
            public void onCheckChanged(TodoItem todo) {
                todo.setCompleted(!todo.isCompleted());
                viewModel.update(todo);
                if (todo.isCompleted()) {
                    recordTaskCompletion();
                    cancelTimerAndReminder(todo);
                }
                if (!animatedTodoIds.contains(todo.getId())) {
                    showAnimationAndAlert(todo.isCompleted(), false);
                    animatedTodoIds.add(todo.getId());
                }
            }

            @Override
            public void onReminderClicked(TodoItem todo) {
                showReminderPicker(todo);
            }

            @Override
            public void onTimerClicked(TodoItem todo) {
                showTimerPicker(todo);
            }
        });

        navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment);

        return view;
    }

    private void showAddTodoDialog(int noteId) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        View dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_add_edit_todo, null);
        builder.setView(dialogView);

        EditText editTextTask = dialogView.findViewById(R.id.edit_text_task);
        MaterialCardView cardReminder = dialogView.findViewById(R.id.card_reminder);
        MaterialCardView cardTimer = dialogView.findViewById(R.id.card_timer);
        MaterialButton buttonSave = dialogView.findViewById(R.id.button_save);
        MaterialButton buttonCancel = dialogView.findViewById(R.id.button_cancel_todo);

        editTextTask.requestFocus();
        InputMethodManager imm = (InputMethodManager) requireContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(editTextTask, InputMethodManager.SHOW_IMPLICIT);

        TodoItem todo = new TodoItem();
        todo.setNoteId(noteId == -1 ? null : noteId);

        cardReminder.setOnClickListener(v -> showReminderPicker(todo));
        cardTimer.setOnClickListener(v -> showTimerPicker(todo));

        AlertDialog dialog = builder.create();

        buttonSave.setOnClickListener(v -> {
            String task = editTextTask.getText().toString().trim();
            if (!task.isEmpty()) {
                todo.setTask(task);
                todo.setCompleted(false);
                viewModel.insert(todo);
                showAnimationAndAlert(true, true);
                dialog.dismiss();
                // Hide keyboard reliably
                imm.hideSoftInputFromWindow(editTextTask.getWindowToken(), 0);
                editTextTask.clearFocus();
            }
        });

        buttonCancel.setOnClickListener(v -> {
            dialog.dismiss();
            imm.hideSoftInputFromWindow(editTextTask.getWindowToken(), 0);
            editTextTask.clearFocus();
        });

        dialog.show();
    }

    private void showReminderPicker(TodoItem todo) {
        Calendar calendar = Calendar.getInstance();
        new DatePickerDialog(requireContext(), (view, year, month, dayOfMonth) -> {
            calendar.set(year, month, dayOfMonth);
            new TimePickerDialog(requireContext(), (view1, hourOfDay, minute) -> {
                calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                calendar.set(Calendar.MINUTE, minute);
                todo.setReminderTime(calendar.getTimeInMillis());
                setReminder(todo);
                viewModel.update(todo);
            }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true).show();
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();
    }

    private void showTimerPicker(TodoItem todo) {
        new AlertDialog.Builder(requireContext())
                .setTitle("Set Timer Duration")
                .setItems(new String[]{"5 min", "10 min", "15 min", "30 min", "1 hour"}, (dialog, which) -> {
                    long duration = which == 0 ? 5 * 60 * 1000
                            : which == 1 ? 10 * 60 * 1000
                            : which == 2 ? 15 * 60 * 1000
                            : which == 3 ? 30 * 60 * 1000
                            : 60 * 60 * 1000;
                    todo.setTimerDuration(duration);
                    startTimer(todo);
                    viewModel.update(todo);
                })
                .show();
    }

    private void setReminder(TodoItem todo) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (alarmManager.canScheduleExactAlarms()) {
                scheduleExactAlarm(todo);
            } else {
                Intent intent = new Intent(android.provider.Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM);
                startActivity(intent);
                Toast.makeText(requireContext(), "Please enable exact alarms for reminders", Toast.LENGTH_LONG).show();
            }
        } else {
            scheduleExactAlarm(todo);
        }
    }

    private void scheduleExactAlarm(TodoItem todo) {
        Intent intent = new Intent(requireContext(), ReminderReceiver.class);
        intent.putExtra("todoId", todo.getId());
        intent.putExtra("task", todo.getTask());
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                requireContext(),
                todo.getId(),
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );
        try {
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, todo.getReminderTime(), pendingIntent);
        } catch (SecurityException e) {
            Toast.makeText(requireContext(), "Cannot set reminder: Exact alarm permission denied", Toast.LENGTH_LONG).show();
        }
    }

    private void startTimer(TodoItem todo) {
        if (timerMap.containsKey(todo.getId())) {
            timerMap.get(todo.getId()).cancel();
            timerMap.remove(todo.getId());
        }

        CountDownTimer timer = new CountDownTimer(todo.getTimerDuration(), 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                // Update the todo in the database
                todo.setTimerDuration(millisUntilFinished);
                viewModel.update(todo); // This triggers the LiveData observer to update the adapter
            }

            @Override
            public void onFinish() {
                notificationHelper.showNotification(todo.getId(), "Timer Finished", "Time's up for: " + todo.getTask());
                todo.setCompleted(false);
                todo.setTimerDuration(0);
                viewModel.update(todo);
                timerMap.remove(todo.getId());
                animatedTodoIds.remove(todo.getId());
                showAnimationAndAlertWithQuote(false, false);
            }
        };
        timer.start();
        timerMap.put(todo.getId(), timer);
    }

    private void showAnimationAndAlert(boolean isCompleted, boolean isCreation) {
        int animationRes = isCreation ? R.raw.target : isCompleted ? R.raw.wow : R.raw.crosseyeman;
        lottieAnimation.setAnimation(animationRes);
        lottieAnimation.setRepeatCount(0);
        lottieAnimation.setVisibility(View.VISIBLE);
        lottieAnimation.playAnimation();
        lottieAnimation.setContentDescription(isCreation ? "Target animation for new todo creation" : isCompleted ? "Wow animation for completed task" : "Crosseyeman animation for incomplete task");
        lottieAnimation.addAnimatorListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                lottieAnimation.setVisibility(View.GONE);
            }
        });

        String message = isCreation ? "To-Do created!" : isCompleted ? "Task done! ✅" : "Task not done! ❌";
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show();
    }

    private void showAnimationAndAlertWithQuote(boolean isCompleted, boolean isCreation) {
        int animationRes = isCreation ? R.raw.target : isCompleted ? R.raw.wow : R.raw.crosseyeman;
        lottieAnimation.setAnimation(animationRes);
        lottieAnimation.setRepeatCount(0);
        lottieAnimation.setVisibility(View.VISIBLE);
        lottieAnimation.playAnimation();
        lottieAnimation.setContentDescription(isCreation ? "Target animation for new todo creation" : isCompleted ? "Wow animation for completed task" : "Crosseyeman animation for incomplete task");
        lottieAnimation.addAnimatorListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                lottieAnimation.setVisibility(View.GONE);
            }
        });

        String message;
        if (!isCompleted && !isCreation) {
            String[] quotes = getResources().getStringArray(R.array.motivational_quotes);
            message = quotes[new Random().nextInt(quotes.length)];
        } else {
            message = isCreation ? "To-Do created!" : "Task done! ✅";
        }
        Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show();
    }

    private void cancelTimerAndReminder(TodoItem todo) {
        if (todo.getReminderTime() > 0) {
            Intent intent = new Intent(requireContext(), ReminderReceiver.class);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(
                    requireContext(),
                    todo.getId(),
                    intent,
                    PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
            );
            alarmManager.cancel(pendingIntent);
            todo.setReminderTime(0);
        }

        if (timerMap.containsKey(todo.getId())) {
            timerMap.get(todo.getId()).cancel();
            timerMap.remove(todo.getId());
            todo.setTimerDuration(0);
        }

        notificationHelper.cancelNotification(todo.getId());
    }

    private void recordTaskCompletion() {
        long currentTime = System.currentTimeMillis();
        long firstCompletionTime = prefs.getLong("first_completion_time", 0);
        int completedTasks = prefs.getInt("completed_tasks", 0);

        if (firstCompletionTime == 0) {
            prefs.edit().putLong("first_completion_time", currentTime).apply();
        }

        completedTasks++;
        prefs.edit().putInt("completed_tasks", completedTasks).apply();
        updateStatsDisplay();
    }

    private void updateStatsDisplay() {
        long firstCompletionTime = prefs.getLong("first_completion_time", 0);
        int completedTasks = prefs.getInt("completed_tasks", 0);

        if (completedTasks == 0) {
            statsTextView.setText("Complete your first task to start! 🚀");
        } else {
            long currentTime = System.currentTimeMillis();
            long days = TimeUnit.MILLISECONDS.toDays(currentTime - firstCompletionTime) + 1;
            String message = String.format("You’ve completed %d task%s in %d day%s! 💪",
                    completedTasks, completedTasks == 1 ? "" : "s", days, days == 1 ? "" : "s");
            statsTextView.setText(message);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        for (CountDownTimer timer : timerMap.values()) {
            timer.cancel();
        }
        timerMap.clear();
    }
}