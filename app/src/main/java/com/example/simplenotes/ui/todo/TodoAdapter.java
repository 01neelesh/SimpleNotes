package com.example.simplenotes.ui.todo;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.simplenotes.R;
import com.example.simplenotes.data.local.entity.TodoItem;

import java.util.List;

public class TodoAdapter extends RecyclerView.Adapter<TodoAdapter.TodoViewHolder> {

    private List<TodoItem> todos;

    public TodoAdapter(List<TodoItem> todos) {
        this.todos = todos;
    }

    @NonNull
    @Override
    public TodoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_todo, parent, false);
        return new TodoViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull TodoViewHolder holder, int position) {
        TodoItem todo = todos.get(position);

        holder.tvNumber.setText(String.valueOf(position + 1)); // Set item number or dot as per your logic
        holder.tvNumber.setVisibility(todo.isDot() ? View.GONE : View.VISIBLE); // Adjust visibility based on dot

        holder.tvTodo.setText(todo.getTodoText()); // Set todo text
        holder.cbTodoDone.setChecked(todo.isDone()); // Set checkbox state
        holder.cbTodoDone.setVisibility(todo.isDot() ? View.GONE : View.VISIBLE); // Adjust visibility based on dot

        // Adjust visibility and logic based on your requirements
        if (!todo.isDot()) {
            holder.cbTodoDone.setOnCheckedChangeListener((buttonView, isChecked) -> {
                todo.setDone(isChecked); // Update todo item's done state
            });
        }
    }

    @Override
    public int getItemCount() {
        return todos.size();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setTodos(List<TodoItem> todos) {
        this.todos = todos;
        notifyDataSetChanged(); // Notify RecyclerView to update
    }

    public List<TodoItem> getTodos() {
        return todos;
    }

    public static class TodoViewHolder extends RecyclerView.ViewHolder {

        public TextView tvNumber;
        public TextView tvTodo;
        public CheckBox cbTodoDone;

        public TodoViewHolder(View itemView) {
            super(itemView);
            tvNumber = itemView.findViewById(R.id.tvNumber);
            tvTodo = itemView.findViewById(R.id.tvTodo);
            cbTodoDone = itemView.findViewById(R.id.cb_todo_done);
        }
    }
}
