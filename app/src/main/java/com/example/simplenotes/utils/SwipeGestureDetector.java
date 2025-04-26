package com.example.simplenotes.utils;

import android.content.Context;
import android.view.GestureDetector;
import android.view.MotionEvent;
import androidx.navigation.NavController;

public class SwipeGestureDetector extends GestureDetector.SimpleOnGestureListener {
    private final NavController navController;

    public SwipeGestureDetector(Context context, NavController navController) {
        this.navController = navController;
    }

    @Override
    public boolean onDown(MotionEvent e) {
        return false; // Minimal implementation for touch handling
    }
}