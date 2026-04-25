package com.sgf.poker.ui.gamedetail;

import android.os.Bundle;
import android.view.*;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.sgf.poker.databinding.FragmentGameDetailBinding;

public class GameDetailFragment extends Fragment {

    private FragmentGameDetailBinding binding;
    private GameDetailViewModel viewModel;
    private GamePlayersAdapter adapter;
    private String gameId;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        gameId = GameDetailFragmentArgs.fromBundle(requireArguments()).getGameId();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        binding = FragmentGameDetailBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(this).get(GameDetailViewModel.class);
        viewModel.init(gameId);

        setupRecyclerView();
        setupSortDropdown();
        setupPrizesButton();
        observeViewModel();
    }

    private void setupRecyclerView() {
        adapter = new GamePlayersAdapter(viewModel);
        binding.recyclerGamePlayers.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.recyclerGamePlayers.setAdapter(adapter);
    }

    private static final String[] SORT_LABELS = {"Name", "Position"};

    private void setupSortDropdown() {
        var sortAdapter = new ArrayAdapter<>(requireContext(),
                android.R.layout.simple_spinner_item, SORT_LABELS);
        sortAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.spinnerSortPlayers.setAdapter(sortAdapter);
        binding.spinnerSortPlayers.setSelection(viewModel.getSortOrder().ordinal());
        binding.spinnerSortPlayers.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                viewModel.setSortOrder(GameDetailViewModel.SortOrder.values()[pos]);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    private void setupPrizesButton() {
        binding.btnPrizes.setOnClickListener(v -> {
            var game = viewModel.game.getValue();
            if (game == null) return;
            var action = GameDetailFragmentDirections.actionDetailToPrizes(gameId);
            Navigation.findNavController(requireView()).navigate(action);
        });
        binding.btnSyncPlayers.setOnClickListener(v -> viewModel.syncNewPlayers());
    }

    private void observeViewModel() {
        viewModel.game.observe(getViewLifecycleOwner(), game -> {
            if (game == null) return;
            requireActivity().setTitle(game.getName());
            binding.textComingCount.setText(game.getConfirmedPlayers().size() + " coming");
            binding.textPresentCount.setText(game.getPresentPlayers().size() + " present");
            int paidCount = (int) game.getPlayers().stream().filter(gp -> gp.isPayed()).count();
            binding.textPaidCount.setText(paidCount + " paid");
            adapter.submitGame(game, viewModel);
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
