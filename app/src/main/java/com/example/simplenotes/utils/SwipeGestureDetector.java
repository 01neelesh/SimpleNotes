package com.example.simplenotes.utils;

import static com.example.simplenotes.utils.Constants.SWIPE_THRESHOLD;
import static com.example.simplenotes.utils.Constants.SWIPE_VELOCITY_THRESHOLD;

import android.content.Context;
import android.view.GestureDetector;
import android.view.MotionEvent;

import androidx.navigation.NavController;

import com.example.simplenotes.R;

public class SwipeGestureDetector extends GestureDetector.SimpleOnGestureListener {

    private final NavController navController;

    public SwipeGestureDetector(Context context, NavController navController) {
        this.navController = navController;
    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        float diffX = e2.getX() - e1.getX();
        float diffY = e2.getY() - e1.getY();

        if (Math.abs(diffX) > Math.abs(diffY)) {
            if (Math.abs(diffX) > SWIPE_THRESHOLD && Math.abs(velocityX) > SWIPE_VELOCITY_THRESHOLD) {
                if (diffX > 0) {
                    // Swipe right
                    if (navController.getCurrentDestination().getId() == R.id.ledgerDetailFragment) {
                        navController.navigate(R.id.action_ledgerDetailFragment_to_ledgerFragment);
                    } else if (navController.getCurrentDestination().getId() == R.id.todoFragment) {
                        navController.navigate(R.id.action_todoFragment_to_notesFragment);
                    }
                } else {
                    // Swipe left
                    if (navController.getCurrentDestination().getId() == R.id.notesFragment) {
                        // Handled by ItemTouchHelper in NotesFragment
                    }
                }
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean onDown(MotionEvent e) {
        return true; // Allow other gestures to be detected
    }
}