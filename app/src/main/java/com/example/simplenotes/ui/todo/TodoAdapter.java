package com.example.simplenotes.ui.todo;

import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.example.simplenotes.R;
import com.example.simplenotes.data.local.entity.TodoItem;

import java.util.ArrayList;
import java.util.List;

public class TodoAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int TYPE_ACTIVE = 0;
    private static final int TYPE_COMPLETED = 1;
    private static final int TYPE_HEADER = 2;

    private List<Object> items = new ArrayList<>();
    private List<TodoItem> activeTodos = new ArrayList<>();
    private List<TodoItem> completedTodos = new ArrayList<>();
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
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == TYPE_HEADER) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_header, parent, false);
            return new HeaderViewHolder(view);
        } else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_todo, parent, false);
            return new TodoViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof HeaderViewHolder) {
            HeaderViewHolder headerHolder = (HeaderViewHolder) holder;
            headerHolder.headerText.setText(items.get(position).equals("Active") ? "Active Tasks" : "Completed Tasks");
        } else if (holder instanceof TodoViewHolder) {
            TodoViewHolder todoHolder = (TodoViewHolder) holder;
            TodoItem todo = (TodoItem) items.get(position);
            todoHolder.bind(todo);
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (items.get(position) instanceof String) {
            return TYPE_HEADER;
        }
        TodoItem todo = (TodoItem) items.get(position);
        return todo.isCompleted() ? TYPE_COMPLETED : TYPE_ACTIVE;
    }

    public void setTodos(List<TodoItem> todos) {
        List<Object> newItems = new ArrayList<>();
        List<TodoItem> newActiveTodos = new ArrayList<>();
        List<TodoItem> newCompletedTodos = new ArrayList<>();

        if (todos != null) {
            for (TodoItem todo : todos) {
                if (todo.isCompleted()) {
                    newCompletedTodos.add(todo);
                } else {
                    newActiveTodos.add(todo);
                }
            }
        }

        if (!newActiveTodos.isEmpty()) {
            newItems.add("Active");
            newItems.addAll(newActiveTodos);
        }
        if (!newCompletedTodos.isEmpty()) {
            newItems.add("Completed");
            newItems.addAll(newCompletedTodos);
        }

        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(new ItemsDiffCallback(this.items, newItems));
        this.items = newItems;
        this.activeTodos = newActiveTodos;
        this.completedTodos = newCompletedTodos;
        diffResult.dispatchUpdatesTo(this);
    }

    public TodoItem getTodoAt(int position) {
        Object item = items.get(position);
        if (item instanceof TodoItem) {
            return (TodoItem) item;
        }
        return null;
    }

    class TodoViewHolder extends RecyclerView.ViewHolder {
        private final CheckBox checkBox;
        private final TextView textViewTask;
        private final TextView reminderTimeText;
        private final TextView timerDurationText;

        public TodoViewHolder(@NonNull View itemView) {
            super(itemView);
            checkBox = itemView.findViewById(R.id.checkbox_todo);
            textViewTask = itemView.findViewById(R.id.text_view_task);
            reminderTimeText = itemView.findViewById(R.id.text_reminder_time);
            timerDurationText = itemView.findViewById(R.id.text_timer_duration);

            checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if (listener != null) {
                    TodoItem todo = getTodoAt(getBindingAdapterPosition());
                    if (todo != null) {
                        listener.onCheckChanged(todo);
                    }
                }
            });
        }

        public void bind(TodoItem todo) {
            checkBox.setChecked(todo.isCompleted());
            textViewTask.setText(todo.getTask());
            if (todo.isCompleted()) {
                textViewTask.setPaintFlags(textViewTask.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            } else {
                textViewTask.setPaintFlags(textViewTask.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
            }

            // Show reminder time if set
            if (todo.getReminderTime() > 0) {
                reminderTimeText.setVisibility(View.VISIBLE);
                reminderTimeText.setText(DateUtils.formatDateTime(itemView.getContext(), todo.getReminderTime(), DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_SHOW_DATE));
                Drawable reminderDrawable = ContextCompat.getDrawable(itemView.getContext(), R.drawable.notification);
                if (reminderDrawable != null) {
                    reminderDrawable.setBounds(0, 0, 40, 40); // Set size to 40x40 pixels
                    reminderDrawable.setTint(ContextCompat.getColor(itemView.getContext(), R.color.accent_pink));
                    reminderTimeText.setCompoundDrawables(reminderDrawable, null, null, null);
                    reminderTimeText.setCompoundDrawablePadding(4);
                }
            } else {
                reminderTimeText.setVisibility(View.GONE);
                reminderTimeText.setCompoundDrawables(null, null, null, null);
            }

            // Show timer duration if set
            if (todo.getTimerDuration() > 0) {
                timerDurationText.setVisibility(View.VISIBLE);
                timerDurationText.setText(formatDuration(todo.getTimerDuration()));
                Drawable timerDrawable = ContextCompat.getDrawable(itemView.getContext(), R.drawable.stopwatch);
                if (timerDrawable != null) {
                    timerDrawable.setBounds(0, 0, 40, 40); // Set size to 40x40 pixels
                    timerDrawable.setTint(ContextCompat.getColor(itemView.getContext(), R.color.accent_pink));
                    timerDurationText.setCompoundDrawables(timerDrawable, null, null, null);
                    timerDurationText.setCompoundDrawablePadding(4);
                }
            } else {
                timerDurationText.setVisibility(View.GONE);
                timerDurationText.setCompoundDrawables(null, null, null, null);
            }

            // Show metadata container if either reminder or timer is set
            itemView.findViewById(R.id.task_metadata_container).setVisibility(
                    todo.getReminderTime() > 0 || todo.getTimerDuration() > 0 ? View.VISIBLE : View.GONE);
        }

        private String formatDuration(long duration) {
            long minutes = (duration / 1000) / 60;
            long seconds = (duration / 1000) % 60;
            return String.format("%dm %ds", minutes, seconds);
        }
    }

    class HeaderViewHolder extends RecyclerView.ViewHolder {
        private final TextView headerText;

        public HeaderViewHolder(@NonNull View itemView) {
            super(itemView);
            headerText = itemView.findViewById(R.id.header_text);
        }
    }

    static class ItemsDiffCallback extends DiffUtil.Callback {
        private final List<Object> oldList;
        private final List<Object> newList;

        public ItemsDiffCallback(List<Object> oldList, List<Object> newList) {
            this.oldList = oldList;
            this.newList = newList;
        }

        @Override
        public int getOldListSize() {
            return oldList.size();
        }

        @Override
        public int getNewListSize() {
            return newList.size();
        }

        @Override
        public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
            Object oldItem = oldList.get(oldItemPosition);
            Object newItem = newList.get(newItemPosition);
            if (oldItem instanceof String && newItem instanceof String) {
                return oldItem.equals(newItem);
            } else if (oldItem instanceof TodoItem && newItem instanceof TodoItem) {
                return ((TodoItem) oldItem).getId() == ((TodoItem) newItem).getId();
            }
            return false;
        }

        @Override
        public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
            Object oldItem = oldList.get(oldItemPosition);
            Object newItem = newList.get(newItemPosition);
            if (oldItem instanceof String && newItem instanceof String) {
                return oldItem.equals(newItem);
            } else if (oldItem instanceof TodoItem && newItem instanceof TodoItem) {
                TodoItem oldTodo = (TodoItem) oldItem;
                TodoItem newTodo = (TodoItem) newItem;
                return oldTodo.isCompleted() == newTodo.isCompleted() &&
                        oldTodo.getTask().equals(newTodo.getTask()) &&
                        oldTodo.getReminderTime() == newTodo.getReminderTime() &&
                        oldTodo.getTimerDuration() == newTodo.getTimerDuration();
            }
            return false;
        }
    }
}