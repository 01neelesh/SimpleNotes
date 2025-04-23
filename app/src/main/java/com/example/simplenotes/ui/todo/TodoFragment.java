package com.example.simplenotes.ui.todo;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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

import com.airbnb.lottie.LottieAnimationView;
import com.example.simplenotes.R;
import com.example.simplenotes.data.local.entity.TodoItem;
import com.example.simplenotes.utils.NotificationHelper;
import com.example.simplenotes.viewmodel.TodoViewModel;
import com.google.android.material.snackbar.Snackbar;

import java.util.Calendar;

public class TodoFragment extends Fragment {
    private TodoViewModel viewModel;
    private TodoAdapter adapter;
    private NavController navController;
    private LottieAnimationView lottieAnimation;
    private NotificationHelper notificationHelper;
    private AlarmManager alarmManager;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_todo, container, false);

        RecyclerView recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        adapter = new TodoAdapter();
        recyclerView.setAdapter(adapter);

        lottieAnimation = view.findViewById(R.id.lottie_animation);
        notificationHelper = new NotificationHelper(requireContext());
        alarmManager = (AlarmManager) requireContext().getSystemService(Context.ALARM_SERVICE);

        ItemTouchHelper.SimpleCallback itemTouchCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getBindingAdapterPosition();
                TodoItem todo = adapter.getTodoAt(position);
                if (direction == ItemTouchHelper.LEFT) {
                    navController.navigate(R.id.action_todoFragment_to_notesFragment);
                } else if (direction == ItemTouchHelper.RIGHT) {
                    new AlertDialog.Builder(requireContext())
                            .setTitle("Delete To-Do")
                            .setMessage("Are you sure you want to delete this to-do?")
                            .setPositiveButton("Delete", (dialog, which) -> {
                                viewModel.delete(todo);
                                Snackbar.make(view, "To-Do deleted", Snackbar.LENGTH_SHORT).show();
                            })
                            .setNegativeButton("Cancel", (dialog, which) -> adapter.notifyItemChanged(position))
                            .show();
                }
            }
        };
        new ItemTouchHelper(itemTouchCallback).attachToRecyclerView(recyclerView);

        viewModel = new ViewModelProvider(this, new ViewModelProvider.AndroidViewModelFactory(requireActivity().getApplication())).get(TodoViewModel.class);
        int noteId = getArguments() != null ? getArguments().getInt("noteId", -1) : -1;
        if (noteId == -1) {
            viewModel.getAllTodos().observe(getViewLifecycleOwner(), todos -> adapter.setTodos(todos));
        } else {
            viewModel.getTodosByNoteId(noteId).observe(getViewLifecycleOwner(), todos -> adapter.setTodos(todos));
        }

        view.findViewById(R.id.fab_add_task).setOnClickListener(v -> showAddTodoDialog(noteId));

        adapter.setOnInteractionListener(new TodoAdapter.OnTodoInteractionListener() {
            @Override
            public void onCheckChanged(TodoItem todo) {
                viewModel.update(todo);
                showAnimationAndAlert(todo.isCompleted());
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
        TextView textReminder = dialogView.findViewById(R.id.text_reminder);
        TextView textTimer = dialogView.findViewById(R.id.text_timer);
        Button buttonSave = dialogView.findViewById(R.id.button_save);
        Button buttonCancel = dialogView.findViewById(R.id.button_cancel);

        TodoItem todo = new TodoItem();
        todo.setNoteId(noteId == -1 ? 0 : noteId);

        textReminder.setOnClickListener(v -> showReminderPicker(todo));
        textTimer.setOnClickListener(v -> showTimerPicker(todo));

        AlertDialog dialog = builder.create();

        buttonSave.setOnClickListener(v -> {
            String task = editTextTask.getText().toString().trim();
            if (!task.isEmpty()) {
                todo.setTask(task);
                todo.setCompleted(false);
                viewModel.insert(todo);
                showAnimationAndAlert(true, true);
                dialog.dismiss();
            }
        });

        buttonCancel.setOnClickListener(v -> dialog.dismiss());

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
                .setItems(new String[]{"15 min", "30 min", "1 hour"}, (dialog, which) -> {
                    long duration = which == 0 ? 15 * 60 * 1000 : which == 1 ? 30 * 60 * 1000 : 60 * 60 * 1000;
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
                // Prompt user to grant permission (Android 13+)
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
            public void onTick(long millisUntilFinished) {}

            @Override
            public void onFinish() {
                notificationHelper.showNotification(todo.getId(), "Timer Finished", "Time's up for: " + todo.getTask());
            }
        }.start();
    }

    private void showAnimationAndAlert(boolean isCompleted) {
        showAnimationAndAlert(isCompleted, false);
    }

    private void showAnimationAndAlert(boolean isCompleted, boolean isCreation) {
        lottieAnimation.setAnimation(isCreation ? R.raw.target : isCompleted ? R.raw.wow : R.raw.crosseyeman);
        lottieAnimation.setVisibility(View.VISIBLE);
        lottieAnimation.playAnimation();
        lottieAnimation.addAnimatorListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                lottieAnimation.setVisibility(View.GONE);
            }
        });

        String message = isCreation ? "To-Do created!" : isCompleted ? "Task done! ✅" : "Task not done! ❌";
        Snackbar.make(requireView(), message + " Want to delete todo? Just swipe 🫱 right", Snackbar.LENGTH_LONG).show();
    }
}