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
import com.google.android.material.snackbar.Snackbar;

import java.util.Calendar;
import java.util.HashSet;
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
    private TextView pointsTextView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_todo, container, false);

        RecyclerView recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        adapter = new TodoAdapter();
        recyclerView.setAdapter(adapter);

        lottieAnimation = view.findViewById(R.id.lottie_animation);
        pointsTextView = view.findViewById(R.id.points_text_view);
        notificationHelper = new NotificationHelper(requireContext());
        alarmManager = (AlarmManager) requireContext().getSystemService(Context.ALARM_SERVICE);
        prefs = requireContext().getSharedPreferences("SimpleNotesPrefs", Context.MODE_PRIVATE);
        updatePointsDisplay();

        // Schedule expiration worker
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
                        // Cancel timer/reminder before deleting
                        cancelTimerAndReminder(todo);
                        viewModel.delete(todo);
                        Snackbar.make(view, "To-Do deleted", Snackbar.LENGTH_SHORT).show();
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
                    addPoints(10); // Add 10 points for completing a task
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

        // Request focus and show keyboard
        editTextTask.requestFocus();
        InputMethodManager imm = (InputMethodManager) requireContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);

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
                // Hide keyboard
                imm.hideSoftInputFromWindow(editTextTask.getWindowToken(), 0);
            }
        });

        buttonCancel.setOnClickListener(v -> {
            dialog.dismiss();
            // Hide keyboard
            imm.hideSoftInputFromWindow(editTextTask.getWindowToken(), 0);
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
                    long duration = which == 0 ? 5 * 60 * 1000  // 5 minutes
                            : which == 1 ? 10 * 60 * 1000  // 10 minutes
                            : which == 2 ? 15 * 60 * 1000  // 15 minutes
                            : which == 3 ? 30 * 60 * 1000  // 30 minutes
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
                Snackbar.make(requireView(), "Please enable exact alarms for reminders", Snackbar.LENGTH_LONG).show();
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
            Snackbar.make(requireView(), "Cannot set reminder: Exact alarm permission denied", Snackbar.LENGTH_LONG).show();
        }
    }

    private void startTimer(TodoItem todo) {
        new CountDownTimer(todo.getTimerDuration(), 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                // Update UI with remaining time (handled in TodoAdapter)
            }

            @Override
            public void onFinish() {
                notificationHelper.showNotification(todo.getId(), "Timer Finished", "Time's up for: " + todo.getTask());
                todo.setCompleted(false);
                viewModel.update(todo);
                animatedTodoIds.remove(todo.getId());
                showAnimationAndAlert(false, false);
            }
        }.start();
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
        Snackbar.make(requireView(), message, Snackbar.LENGTH_SHORT).show();
    }

    private void cancelTimerAndReminder(TodoItem todo) {
        // Cancel alarm for reminder
        if (todo.getReminderTime() > 0) {
            Intent intent = new Intent(requireContext(), ReminderReceiver.class);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(
                    requireContext(),
                    todo.getId(),
                    intent,
                    PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
            );
            alarmManager.cancel(pendingIntent);
        }
        // Cancel notification
//        notificationHelper.cancelNotification(todo.getId());
        // Note: Timer cancellation will be handled by stopping the CountDownTimer (to be implemented)
    }

    private void addPoints(int points) {
        int currentPoints = prefs.getInt("user_points", 0);
        currentPoints += points;
        prefs.edit().putInt("user_points", currentPoints).apply();
        updatePointsDisplay();
    }

    private void updatePointsDisplay() {
        int points = prefs.getInt("user_points", 0);
        pointsTextView.setText("Points: " + points);
    }
}