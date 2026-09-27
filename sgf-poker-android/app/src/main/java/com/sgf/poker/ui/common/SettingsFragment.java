package com.sgf.poker.ui.common;

import android.os.Bundle;
import android.view.*;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.sgf.poker.R;
import com.sgf.poker.databinding.FragmentSettingsBinding;

public class SettingsFragment extends Fragment {

    private FragmentSettingsBinding binding;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        binding = FragmentSettingsBinding.inflate(inflater, container, false);
        setupThemeSelector();
        return binding.getRoot();
    }

    private void setupThemeSelector() {
        binding.radioThemeMode.check(radioIdFor(ThemePreference.get(requireContext())));

        binding.radioThemeMode.setOnCheckedChangeListener((group, checkedId) -> {
            var mode = modeFor(checkedId);
            if (mode == ThemePreference.get(requireContext())) {
                return; // nothing to do; avoids a needless activity recreation
            }
            // Recreates the activity with the new night mode applied.
            ThemePreference.set(requireContext(), mode);
        });
    }

    private int radioIdFor(ThemePreference.Mode mode) {
        return switch (mode) {
            case LIGHT -> R.id.radioThemeLight;
            case DARK -> R.id.radioThemeDark;
            case SYSTEM -> R.id.radioThemeSystem;
        };
    }

    private ThemePreference.Mode modeFor(int radioId) {
        if (radioId == R.id.radioThemeLight) {
            return ThemePreference.Mode.LIGHT;
        }
        if (radioId == R.id.radioThemeDark) {
            return ThemePreference.Mode.DARK;
        }
        return ThemePreference.Mode.SYSTEM;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
