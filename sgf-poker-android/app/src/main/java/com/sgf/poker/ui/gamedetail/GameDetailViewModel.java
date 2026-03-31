package com.sgf.poker.ui.gamedetail;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.sgf.poker.data.repository.LocalStorageGameRepository;
import com.sgf.poker.data.repository.LocalStoragePlayerRepository;
import com.sgf.poker.domain.model.Game;
import com.sgf.poker.domain.model.Player;
import com.sgf.poker.usecases.game.FetchGamesUseCase;
import com.sgf.poker.usecases.game.UpdateGamePlayerUseCase;
import com.sgf.poker.usecases.player.FetchPlayersUseCase;

import java.util.List;

public class GameDetailViewModel extends AndroidViewModel {

    private final MutableLiveData<Game> _game = new MutableLiveData<>();
    public final LiveData<Game> game = _game;

    private final MutableLiveData<List<Player>> _players = new MutableLiveData<>(List.of());
    public final LiveData<List<Player>> players = _players;

    private final MutableLiveData<String> _error = new MutableLiveData<>(null);
    public final LiveData<String> error = _error;

    private final FetchGamesUseCase fetchGames;
    private final UpdateGamePlayerUseCase updateGamePlayer;
    private final FetchPlayersUseCase fetchPlayers;

    private String gameId;

    public GameDetailViewModel(@NonNull Application application) {
        super(application);
        var gameRepo   = new LocalStorageGameRepository(application);
        var playerRepo = new LocalStoragePlayerRepository(application);
        fetchGames       = new FetchGamesUseCase(gameRepo);
        updateGamePlayer = new UpdateGamePlayerUseCase(gameRepo);
        fetchPlayers     = new FetchPlayersUseCase(playerRepo);
    }

    public void init(String gameId) {
        this.gameId = gameId;
        reloadGame();
        loadPlayers();
    }

    // ── Player name resolution ────────────────────────────────────────────────

    public String playerName(String playerId) {
        if (_players.getValue() == null) return "Unknown";
        return _players.getValue().stream()
                .filter(p -> p.getId().equals(playerId))
                .map(Player::getName)
                .findFirst()
                .orElse("Unknown");
    }

    // ── GamePlayer intents ────────────────────────────────────────────────────

    public void toggleComing(String gamePlayerId) {
        mutate(gamePlayerId, gp -> gp.withComing(!gp.isComing()));
    }

    public void togglePresent(String gamePlayerId) {
        mutate(gamePlayerId, gp -> gp.withPresent(!gp.isPresent()));
    }

    public void togglePayed(String gamePlayerId) {
        mutate(gamePlayerId, gp -> gp.withPayed(!gp.isPayed()));
    }

    public void incrementRebuy(String gamePlayerId) {
        mutate(gamePlayerId, gp -> gp.withRebuyCount(gp.getRebuyCount() + 1));
    }

    public void decrementRebuy(String gamePlayerId) {
        mutate(gamePlayerId, gp -> gp.withRebuyCount(Math.max(0, gp.getRebuyCount() - 1)));
    }

    public void setFinalPosition(String gamePlayerId, Integer position) {
        mutate(gamePlayerId, gp -> gp.withFinalPosition(position));
    }

    // ── Private ───────────────────────────────────────────────────────────────

    private void mutate(String gamePlayerId,
            java.util.function.UnaryOperator<com.sgf.poker.domain.model.GamePlayer> mutation) {
        try {
            var updated = updateGamePlayer.execute(gameId, gamePlayerId, mutation);
            _game.setValue(updated);
        } catch (Exception e) {
            _error.setValue(e.getMessage());
        }
    }

    private void reloadGame() {
        try {
            fetchGames.execute().stream()
                    .filter(g -> g.getId().equals(gameId))
                    .findFirst()
                    .ifPresent(_game::setValue);
        } catch (Exception e) {
            _error.setValue(e.getMessage());
        }
    }

    private void loadPlayers() {
        try {
            _players.setValue(fetchPlayers.execute());
        } catch (Exception e) {
            _error.setValue(e.getMessage());
        }
    }

    public void clearError() { _error.setValue(null); }
}
