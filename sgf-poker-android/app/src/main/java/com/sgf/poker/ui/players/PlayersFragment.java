package com.sgf.poker.ui.players;

import android.os.Bundle;
import android.view.*;
import android.widget.*;

import java.time.LocalDate;

import com.sgf.poker.ui.players.PlayersViewModel.SortOrder;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.sgf.poker.databinding.FragmentPlayersBinding;
import com.sgf.poker.domain.model.Player;

public class PlayersFragment extends Fragment {

    private FragmentPlayersBinding binding;
    private PlayersViewModel viewModel;
    private PlayersAdapter adapter;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        binding = FragmentPlayersBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(this).get(PlayersViewModel.class);

        setupRecyclerView();
        setupSortDropdown();
        setupFab();
        observeViewModel();

        viewModel.loadPlayers();
    }

    private void setupRecyclerView() {
        adapter = new PlayersAdapter(
                player -> showEditPlayerDialog(player),
                id -> viewModel.toggleMember(id),
                id -> viewModel.toggleFounder(id),
                id -> viewModel.deletePlayer(id)
        );
        binding.recyclerPlayers.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.recyclerPlayers.setAdapter(adapter);
    }

    private static final String[] SORT_LABELS = {"Name", "Points", "Founders & Members"};

    private void setupSortDropdown() {
        var sortAdapter = new ArrayAdapter<>(requireContext(),
                android.R.layout.simple_spinner_item, SORT_LABELS);
        sortAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.spinnerSort.setAdapter(sortAdapter);
        binding.spinnerSort.setSelection(viewModel.getSortOrder().ordinal());
        binding.spinnerSort.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                viewModel.setSortOrder(SortOrder.values()[pos]);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    private void setupFab() {
        binding.fabAddPlayer.setOnClickListener(v -> showAddPlayerDialog());
    }

    private void showAddPlayerDialog() {
        var dialogView = LayoutInflater.from(requireContext())
                .inflate(com.sgf.poker.R.layout.dialog_add_player, null);
        var etName    = dialogView.<EditText>findViewById(com.sgf.poker.R.id.etPlayerName);
        var cbMember  = dialogView.<CheckBox>findViewById(com.sgf.poker.R.id.cbMember);
        var cbFounder = dialogView.<CheckBox>findViewById(com.sgf.poker.R.id.cbFounder);

        new MaterialAlertDialogBuilder(requireContext())
                .setTitle("New Player")
                .setView(dialogView)
                .setPositiveButton("Add", (d, w) -> {
                    var name = etName.getText().toString();
                    viewModel.addPlayer(name, cbMember.isChecked(), cbFounder.isChecked());
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void showEditPlayerDialog(Player player) {
        var dialogView = LayoutInflater.from(requireContext())
                .inflate(com.sgf.poker.R.layout.dialog_add_player, null);
        var etName    = dialogView.<EditText>findViewById(com.sgf.poker.R.id.etPlayerName);
        var cbMember  = dialogView.<CheckBox>findViewById(com.sgf.poker.R.id.cbMember);
        var cbFounder = dialogView.<CheckBox>findViewById(com.sgf.poker.R.id.cbFounder);
        View tilPoints = dialogView.findViewById(com.sgf.poker.R.id.tilPoints);
        var etPoints  = dialogView.<EditText>findViewById(com.sgf.poker.R.id.etPoints);

        etName.setText(player.getName());
        cbMember.setChecked(player.isMember());
        cbFounder.setChecked(player.isFounder());
        tilPoints.setVisibility(View.VISIBLE);
        etPoints.setText(String.valueOf(player.getCurrentPoints()));

        new MaterialAlertDialogBuilder(requireContext())
                .setTitle("Edit Player")
                .setView(dialogView)
                .setPositiveButton("Save", (d, w) -> {
                    var name = etName.getText().toString().trim();
                    if (!name.isEmpty()) {
                        int points;
                        try {
                            points = Integer.parseInt(etPoints.getText().toString().trim());
                        } catch (NumberFormatException e) {
                            points = player.getCurrentPoints();
                        }
                        viewModel.editPlayer(player.getId(), name,
                                cbMember.isChecked(), cbFounder.isChecked(), points);
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void observeViewModel() {
        viewModel.players.observe(getViewLifecycleOwner(), players -> {
            adapter.submitList(players);
            binding.textEmpty.setVisibility(players.isEmpty() ? View.VISIBLE : View.GONE);
            binding.recyclerPlayers.setVisibility(players.isEmpty() ? View.GONE : View.VISIBLE);

            int year = LocalDate.now().getYear();
            long members = players.stream().filter(Player::isMember).count();
            requireActivity().setTitle(year + " - " + players.size() + " players (" + members + " members)");
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
