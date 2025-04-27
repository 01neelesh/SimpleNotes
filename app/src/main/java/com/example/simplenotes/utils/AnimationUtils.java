package com.example.simplenotes.utils;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Handler;
import android.os.Looper;
import android.os.Vibrator;
import android.view.View;
import android.widget.Button;

import com.airbnb.lottie.LottieAnimationView;
import com.example.simplenotes.R;

public class AnimationUtils {
    public interface OnDeleteConfirmedListener {
        void onDeleteConfirmed();
    }

    public interface OnCreationConfirmedListener {
        void onCreationConfirmed();
    }

    public static void showDeleteAnimation(Context context, OnDeleteConfirmedListener listener) {
        Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.dialog_delete_animation);
        dialog.setCancelable(false);

        LottieAnimationView animationView = dialog.findViewById(R.id.lottie_animation);
        // START OF MODIFIED SNIPPET
        animationView.setAnimation(R.raw.bin_animation);
        animationView.setColorFilter(Color.parseColor("#FF5555"), PorterDuff.Mode.SRC_ATOP); // Apply a red tint
        animationView.setRepeatCount(0); // Play once
        animationView.playAnimation();
        animationView.setContentDescription("Trash bin animation playing with red tint");
        // END OF MODIFIED SNIPPET

        Button btnCancel = dialog.findViewById(R.id.btn_cancel);
        btnCancel.setOnClickListener(v -> dialog.dismiss());

        Vibrator vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        if (vibrator != null) {
            vibrator.vibrate(100);
        }

        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            if (dialog.isShowing()) {
                dialog.dismiss();
                listener.onDeleteConfirmed();
            }
        }, 2000);

        dialog.show();
    }

    public static void showCheckboxFeedback(View view, boolean isCompleted) {
        LottieAnimationView animationView = view.findViewById(R.id.lottie_feedback);
        if (animationView != null) {
            int animationRes = isCompleted ? R.raw.wow : R.raw.crosseyeman;
            animationView.setAnimation(animationRes);
            animationView.setRepeatCount(0); // Play once
            animationView.setVisibility(View.VISIBLE);
            animationView.playAnimation();
            animationView.setContentDescription(isCompleted ? "Wow animation for completed task" : "Crosseyeman animation for incomplete task");

            new Handler(Looper.getMainLooper()).postDelayed(() -> {
                animationView.setVisibility(View.GONE);
            }, 1000); // Hide after 1 second
        }
    }
}