package com.example.simplenotes;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class TodoFragment extends Fragment {

    private TodoAdapter adapter;
    private final List<TodoItem> todoItemList = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_todo, container, false);

        RecyclerView recyclerView = rootView.findViewById(R.id.todoRecyclerView);
        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 2));

        adapter = new TodoAdapter(todoItemList);
        recyclerView.setAdapter(adapter);

        return rootView;
    }

    @SuppressLint("NotifyDataSetChanged")
    public void updateTodos(List<TodoItem> todos) {
        todoItemList.clear();
        todoItemList.addAll(todos);
        adapter.notifyDataSetChanged();
    }
}
