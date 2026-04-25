package com.sgf.poker.ui.prizes;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.sgf.poker.data.repository.LocalStorageGameRepository;
import com.sgf.poker.domain.model.Game;
import com.sgf.poker.domain.model.Player;
import com.sgf.poker.usecases.prizes.CalculatePrizesUseCase;

import java.util.List;
import java.util.Locale;

public class PrizesViewModel extends AndroidViewModel {

    private final MutableLiveData<com.sgf.poker.domain.model.PrizeCalculation> _calculation =
            new MutableLiveData<>();
    public final LiveData<com.sgf.poker.domain.model.PrizeCalculation> calculation = _calculation;

    private final MutableLiveData<String> _error = new MutableLiveData<>(null);
    public final LiveData<String> error = _error;

    private final CalculatePrizesUseCase calculatePrizes = new CalculatePrizesUseCase();
    private final LocalStorageGameRepository gameRepo;

    private Game currentGame;
    private List<Player> currentPlayers;

    public PrizesViewModel(@NonNull Application application) {
        super(application);
        gameRepo = new LocalStorageGameRepository(application);
    }

    public void calculate(Game game, List<Player> players) {
        this.currentGame = game;
        this.currentPlayers = players;
        _calculation.setValue(calculatePrizes.execute(game, players));
    }

    /**
     * Assigns the given payed player to an award position.
     * Clears the position from whoever previously held it, persists, and recalculates.
     */
    public void assignPosition(String gamePlayerId, int position) {
        if (currentGame == null) return;
        try {
            Game game = currentGame;

            // Clear this position from any other player currently holding it
            for (var gp : game.getPlayers()) {
                if (!gp.getId().equals(gamePlayerId)
                        && Integer.valueOf(position).equals(gp.getFinalPosition())) {
                    game = game.withUpdatedPlayer(gp.withFinalPosition(null));
                }
            }

            // Assign the position to the target player (replaces any position they held before)
            var target = game.getPlayers().stream()
                    .filter(gp -> gp.getId().equals(gamePlayerId))
                    .findFirst()
                    .orElseThrow();
            game = game.withUpdatedPlayer(target.withFinalPosition(position));

            gameRepo.save(game);
            currentGame = game;
            _calculation.setValue(calculatePrizes.execute(game, currentPlayers));
        } catch (Exception e) {
            _error.setValue(e.getMessage());
        }
    }

    public void setOverridePrizeAmount(String gamePlayerId, double amount) {
        if (currentGame == null) return;
        try {
            var target = currentGame.getPlayers().stream()
                    .filter(gp -> gp.getId().equals(gamePlayerId))
                    .findFirst()
                    .orElseThrow();
            var updated = currentGame.withUpdatedPlayer(target.withOverridePrizeAmount(amount));
            gameRepo.save(updated);
            currentGame = updated;
            _calculation.setValue(calculatePrizes.execute(updated, currentPlayers));
        } catch (Exception e) {
            _error.setValue(e.getMessage());
        }
    }

    /** Clears whatever player is currently assigned to the given award position. */
    public void unassignPosition(int position) {
        if (currentGame == null) return;
        try {
            Game game = currentGame;
            for (var gp : game.getPlayers()) {
                if (Integer.valueOf(position).equals(gp.getFinalPosition())) {
                    game = game.withUpdatedPlayer(gp.withFinalPosition(null));
                }
            }
            gameRepo.save(game);
            currentGame = game;
            _calculation.setValue(calculatePrizes.execute(game, currentPlayers));
        } catch (Exception e) {
            _error.setValue(e.getMessage());
        }
    }

    public String formatCurrency(double amount) {
        return String.format(Locale.US, "$%.2f", amount);
    }

    public String formatPercent(double value) {
        return String.format(Locale.US, "%d%%", (int) (value * 100));
    }

    public void clearError() { _error.setValue(null); }
}
