package com.sgf.poker.ui.games;

import android.os.Bundle;
import android.view.*;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.sgf.poker.databinding.FragmentGamesBinding;

import java.time.Instant;
import java.time.ZoneId;

public class GamesFragment extends Fragment {

    private FragmentGamesBinding binding;
    private GamesViewModel viewModel;
    private GamesAdapter adapter;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        binding = FragmentGamesBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(this).get(GamesViewModel.class);

        setupRecyclerView();
        setupFab();
        observeViewModel();

        viewModel.loadGames();
    }

    private void setupRecyclerView() {
        adapter = new GamesAdapter(
                game -> {
                    // Navigate to detail
                    var action = GamesFragmentDirections.actionGamesToDetail(game.getId());
                    Navigation.findNavController(requireView()).navigate(action);
                },
                game -> {
                    // Confirm delete
                    new MaterialAlertDialogBuilder(requireContext())
                            .setTitle("Delete Game")
                            .setMessage("Delete " + game.getName() + "?")
                            .setPositiveButton("Delete", (d, w) -> viewModel.deleteGame(game.getId()))
                            .setNegativeButton("Cancel", null)
                            .show();
                }
        );
        binding.recyclerGames.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.recyclerGames.setAdapter(adapter);
    }

    private void setupFab() {
        binding.fabAddGame.setOnClickListener(v -> showDatePicker());
    }

    private void showDatePicker() {
        var picker = MaterialDatePicker.Builder.datePicker()
                .setTitleText("Select Event Date")
                .build();
        picker.addOnPositiveButtonClickListener(selection -> {
            var date = Instant.ofEpochMilli(selection)
                    .atZone(ZoneId.systemDefault()).toLocalDate();
            viewModel.createGame(date);
        });
        picker.show(getParentFragmentManager(), "date_picker");
    }

    private void observeViewModel() {
        viewModel.games.observe(getViewLifecycleOwner(), games -> {
            adapter.submitList(games);
            binding.textEmpty.setVisibility(games.isEmpty() ? View.VISIBLE : View.GONE);
            binding.recyclerGames.setVisibility(games.isEmpty() ? View.GONE : View.VISIBLE);
        });

        viewModel.error.observe(getViewLifecycleOwner(), error -> {
            if (error != null) {
                Toast.makeText(requireContext(), error, Toast.LENGTH_LONG).show();
                viewModel.clearError();
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
