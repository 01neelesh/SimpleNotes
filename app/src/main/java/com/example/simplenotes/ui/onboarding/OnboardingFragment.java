package com.example.simplenotes.ui.onboarding;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.viewpager2.widget.ViewPager2;

import com.example.simplenotes.R;

public class OnboardingFragment extends Fragment {

    private ViewPager2 viewPager;
    private LinearLayout dotsLayout;
    private Button btnSkip, btnNext, btnGetStarted;
    private OnboardingAdapter adapter;
    // Updated layout references
    private int[] layouts = {
            R.layout.slide_welcome,
            R.layout.slide_swipe,
            R.layout.slide_features
    };
    private SharedPreferences prefs;
    private boolean isFirstLaunch;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_onboarding, container, false);
        prefs = requireActivity().getSharedPreferences("SimpleNotesPrefs", Context.MODE_PRIVATE);
        isFirstLaunch = prefs.getBoolean("isFirstLaunch", true);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (isFirstLaunch) {
            setupOnboarding(view);
        } else {
            navigateToNotes();
        }
    }

    private void setupOnboarding(View view) {
        viewPager = view.findViewById(R.id.view_pager_onboarding);
        dotsLayout = view.findViewById(R.id.dots_layout);
        btnSkip = view.findViewById(R.id.button_skip);
        btnNext = view.findViewById(R.id.button_next);
        btnGetStarted = view.findViewById(R.id.button_get_started);

        adapter = new OnboardingAdapter(requireActivity(), layouts);
        viewPager.setAdapter(adapter);
        addDots(0);
        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                addDots(position);
                if (position == layouts.length - 1) {
                    btnNext.setVisibility(View.GONE);
                    btnGetStarted.setVisibility(View.VISIBLE);
                } else {
                    btnNext.setVisibility(View.VISIBLE);
                    btnGetStarted.setVisibility(View.GONE);
                }
            }
        });

        btnNext.setOnClickListener(v -> viewPager.setCurrentItem(viewPager.getCurrentItem() + 1));
        btnSkip.setOnClickListener(v -> navigateToNotes());
        btnGetStarted.setOnClickListener(v -> navigateToNotes());
    }

    private void addDots(int position) {
        dotsLayout.removeAllViews();
        for (int i = 0; i < layouts.length; i++) {
            ImageView dot = new ImageView(requireContext());
            dot.setImageResource(R.drawable.ic_dot);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(20, 20);
            params.setMargins(8, 0, 8, 0);
            dot.setLayoutParams(params);
            dot.setEnabled(i != position);
            dotsLayout.addView(dot);
        }
    }

    private void navigateToNotes() {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean("isFirstLaunch", false);
        editor.apply();
        NavController navController = Navigation.findNavController(requireView());
        navController.navigate(R.id.action_onboardingFragment_to_notesFragment);
    }
}