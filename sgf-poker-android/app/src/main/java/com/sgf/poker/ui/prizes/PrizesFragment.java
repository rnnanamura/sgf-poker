package com.sgf.poker.ui.prizes;

import android.os.Bundle;
import android.view.*;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.sgf.poker.data.repository.LocalStorageGameRepository;
import com.sgf.poker.data.repository.LocalStoragePlayerRepository;
import com.sgf.poker.databinding.FragmentPrizesBinding;
import com.sgf.poker.domain.model.GamePlayer;
import com.sgf.poker.domain.model.PrizeRules;
import com.sgf.poker.usecases.game.FetchGamesUseCase;
import com.sgf.poker.usecases.player.FetchPlayersUseCase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

public class PrizesFragment extends Fragment {

    private FragmentPrizesBinding binding;
    private PrizesViewModel viewModel;
    private PrizesAdapter adapter;
    private String gameId;
    private List<PayedPlayer> payedPlayers = List.of();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        gameId = PrizesFragmentArgs.fromBundle(requireArguments()).getGameId();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        binding = FragmentPrizesBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(this).get(PrizesViewModel.class);

        // Load game data first so payedPlayers is ready before building the adapter
        loadAndCalculate();

        adapter = new PrizesAdapter(payedPlayers, viewModel);
        binding.recyclerPrizes.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.recyclerPrizes.setAdapter(adapter);

        observeViewModel();
    }

    private void loadAndCalculate() {
        var gameRepo   = new LocalStorageGameRepository(requireContext());
        var playerRepo = new LocalStoragePlayerRepository(requireContext());

        var game = new FetchGamesUseCase(gameRepo).execute().stream()
                .filter(g -> g.getId().equals(gameId))
                .findFirst().orElse(null);
        var players = new FetchPlayersUseCase(playerRepo).execute();

        if (game != null) {
            // Build a name lookup so we can label each payed GamePlayer
            var playerNameById = new HashMap<String, String>();
            for (var p : players) playerNameById.put(p.getId(), p.getName());

            payedPlayers = game.getPlayers().stream()
                    .filter(GamePlayer::isPayed)
                    .map(gp -> new PayedPlayer(
                            gp.getId(),
                            playerNameById.getOrDefault(gp.getPlayerId(), "Unknown")))
                    .collect(Collectors.toList());

            viewModel.calculate(game, players);
        }
    }

    private void observeViewModel() {
        viewModel.calculation.observe(getViewLifecycleOwner(), calc -> {
            if (calc == null) return;

            // Entry breakdown
            binding.textMemberFees.setText(String.format(Locale.US,
                    "%d × $%.0f = %s",
                    calc.memberCount(), PrizeRules.MEMBER_ENTRY_FEE,
                    viewModel.formatCurrency(calc.memberCount() * PrizeRules.MEMBER_ENTRY_FEE)));

            binding.textNonMemberFees.setText(String.format(Locale.US,
                    "%d × $%.0f = %s",
                    calc.nonMemberCount(), PrizeRules.NON_MEMBER_ENTRY_FEE,
                    viewModel.formatCurrency(calc.nonMemberCount() * PrizeRules.NON_MEMBER_ENTRY_FEE)));

            binding.textRebuys.setText(String.format(Locale.US,
                    "%d × $%.0f = %s",
                    calc.totalRebuys(), PrizeRules.REBUY_AMOUNT,
                    viewModel.formatCurrency(calc.totalRebuys() * PrizeRules.REBUY_AMOUNT)));

            // Pool
            binding.textTotalPool.setText(viewModel.formatCurrency(calc.totalPool()));
            binding.textBountyPool.setText(viewModel.formatCurrency(calc.bountyPool()) + "  (20%)");
            binding.textPrizePool.setText(viewModel.formatCurrency(calc.prizePool()) + "  (80%)");

            // Distribution label
            var distLabel = new StringBuilder();
            for (int i = 0; i < calc.distribution().size(); i++) {
                if (i > 0) distLabel.append("  ·  ");
                distLabel.append(ordinal(i + 1)).append(": ")
                        .append(viewModel.formatPercent(calc.distribution().get(i)));
            }
            binding.textDistribution.setText(
                    calc.presentCount() + " players  —  " + distLabel);

            // Build one PrizeLine per distribution slot, assigned or not
            var prizeByPosition = new HashMap<Integer, com.sgf.poker.domain.model.PlayerPrize>();
            for (var p : calc.prizes()) prizeByPosition.put(p.position(), p);

            var lines = new ArrayList<PrizeLine>();
            for (int i = 0; i < calc.distribution().size(); i++) {
                int pos = i + 1;
                double amount = calc.prizePool() * calc.distribution().get(i);
                var assigned = prizeByPosition.get(pos);
                lines.add(new PrizeLine(
                        pos, amount,
                        assigned != null ? assigned.gamePlayerId() : null,
                        assigned != null ? assigned.playerName() : null));
            }
            adapter.submitList(lines);

            // Unranked warning
            if (calc.unrankedCount() > 0) {
                binding.textUnranked.setVisibility(View.VISIBLE);
                binding.textUnranked.setText(
                        calc.unrankedCount() + " player(s) missing a final position.");
            } else {
                binding.textUnranked.setVisibility(View.GONE);
            }
        });

        viewModel.error.observe(getViewLifecycleOwner(), error -> {
            if (error != null) {
                Toast.makeText(requireContext(), error, Toast.LENGTH_LONG).show();
                viewModel.clearError();
            }
        });
    }

    private String ordinal(int n) {
        return switch (n) {
            case 1 -> "1st";
            case 2 -> "2nd";
            case 3 -> "3rd";
            default -> n + "th";
        };
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
