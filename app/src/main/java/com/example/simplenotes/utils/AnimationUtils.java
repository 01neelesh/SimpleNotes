package com.example.simplenotes.utils;

import android.app.Dialog;
import android.content.Context;
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

    public static void showDeleteAnimation(Context context, OnDeleteConfirmedListener listener) {
        Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.dialog_delete_animation);
        dialog.setCancelable(false);

        LottieAnimationView animationView = dialog.findViewById(R.id.lottie_animation);
        animationView.setAnimation(R.raw.bin_animation);
        animationView.playAnimation();
        animationView.setContentDescription("Trash bin animation playing");

        Button btnCancel = dialog.findViewById(R.id.btn_cancel);
        btnCancel.setOnClickListener(v -> dialog.dismiss());

        // Add haptic feedback
        Vibrator vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        if (vibrator != null) {
            vibrator.vibrate(100);
        }

        // Auto-confirm after animation finishes (adjust duration based on your animation)
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            if (dialog.isShowing()) {
                dialog.dismiss();
                listener.onDeleteConfirmed();
            }
        }, 2000); // Assuming animation is ~2 seconds

        dialog.show();
    }
}