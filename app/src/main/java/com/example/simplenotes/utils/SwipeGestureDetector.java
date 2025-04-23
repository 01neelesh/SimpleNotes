package com.example.simplenotes.utils;

import android.content.Context;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.widget.Toast;
import androidx.navigation.NavController;
import com.example.simplenotes.R;

public class SwipeGestureDetector extends GestureDetector.SimpleOnGestureListener {
    private final NavController navController;
    private final Context context;

    public SwipeGestureDetector(Context context, NavController navController) {
        this.context = context;
        this.navController = navController;
    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        try {
            float diffY = e2.getY() - e1.getY();
            float diffX = e2.getX() - e1.getX();
            if (Math.abs(diffX) > Math.abs(diffY)) {
                if (Math.abs(diffX) > Constants.SWIPE_THRESHOLD && Math.abs(velocityX) > Constants.SWIPE_VELOCITY_THRESHOLD) {
                    if (diffX > 0) {
                        try {
                            navController.navigate(R.id.action_notesFragment_to_ledgerFragment);
                        } catch (IllegalArgumentException e) {
                            Toast.makeText(context, "Ledger not implemented yet", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Bundle bundle = new Bundle();
                        bundle.putInt("noteId", -1);
                        navController.navigate(R.id.action_notesFragment_to_todoFragment, bundle);
                    }
                    return true;
                }
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        return false;
    }
}