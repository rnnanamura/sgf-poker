package com.sgf.poker.ui.gamedetail;

import android.os.Bundle;
import android.view.*;
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
        setupPrizesButton();
        observeViewModel();
    }

    private void setupRecyclerView() {
        adapter = new GamePlayersAdapter(viewModel);
        binding.recyclerGamePlayers.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.recyclerGamePlayers.setAdapter(adapter);
    }

    private void setupPrizesButton() {
        binding.btnPrizes.setOnClickListener(v -> {
            var game = viewModel.game.getValue();
            if (game == null) return;
            var action = GameDetailFragmentDirections.actionDetailToPrizes(gameId);
            Navigation.findNavController(requireView()).navigate(action);
        });
    }

    private void observeViewModel() {
        viewModel.game.observe(getViewLifecycleOwner(), game -> {
            if (game == null) return;
            requireActivity().setTitle(game.getName());
            binding.textPlayerCount.setText(game.getPlayers().size() + " players");
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
