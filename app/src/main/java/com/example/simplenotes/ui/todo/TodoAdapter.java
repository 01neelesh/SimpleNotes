package com.example.simplenotes.ui.todo;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.simplenotes.R;
import com.example.simplenotes.data.local.entity.TodoItem;
import java.util.ArrayList;
import java.util.List;

public class TodoAdapter extends RecyclerView.Adapter<TodoAdapter.TodoViewHolder> {
    private List<TodoItem> todos = new ArrayList<>();
    private OnTodoInteractionListener listener;

    public interface OnTodoInteractionListener {
        void onCheckChanged(TodoItem todo);
        void onReminderClicked(TodoItem todo);
        void onTimerClicked(TodoItem todo);
    }

    public void setOnInteractionListener(OnTodoInteractionListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public TodoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_todo, parent, false);
        return new TodoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TodoViewHolder holder, int position) {
        TodoItem todo = todos.get(position);
        holder.bind(todo);
    }

    @Override
    public int getItemCount() {
        return todos.size();
    }

    public void setTodos(List<TodoItem> todos) {
        this.todos = todos != null ? todos : new ArrayList<>();
        notifyDataSetChanged();
    }

    public TodoItem getTodoAt(int position) {
        return todos.get(position);
    }

    class TodoViewHolder extends RecyclerView.ViewHolder {
        private final CheckBox checkBox;
        private final TextView textViewTask;
        private final ImageButton reminderButton;
        private final ImageButton timerButton;

        public TodoViewHolder(@NonNull View itemView) {
            super(itemView);
            checkBox = itemView.findViewById(R.id.checkbox_todo);
            textViewTask = itemView.findViewById(R.id.text_view_task);
            reminderButton = itemView.findViewById(R.id.button_reminder);
            timerButton = itemView.findViewById(R.id.button_timer);

            checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if (listener != null) {
                    TodoItem todo = todos.get(getBindingAdapterPosition());
                    todo.setCompleted(isChecked);
                    listener.onCheckChanged(todo);
                }
            });

            reminderButton.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onReminderClicked(todos.get(getBindingAdapterPosition()));
                }
            });

            timerButton.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onTimerClicked(todos.get(getBindingAdapterPosition()));
                }
            });
        }

        public void bind(TodoItem todo) {
            checkBox.setChecked(todo.isCompleted());
            textViewTask.setText(todo.getTask());
            reminderButton.setEnabled(todo.getReminderTime() == 0 || !todo.isCompleted());
            timerButton.setEnabled(todo.getTimerDuration() == 0 || !todo.isCompleted());
        }
    }
}