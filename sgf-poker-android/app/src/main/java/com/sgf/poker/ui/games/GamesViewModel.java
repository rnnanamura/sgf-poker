package com.sgf.poker.ui.games;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.sgf.poker.data.repository.LocalStorageGameRepository;
import com.sgf.poker.data.repository.LocalStoragePlayerRepository;
import com.sgf.poker.domain.model.Game;
import com.sgf.poker.usecases.game.CreateGameUseCase;
import com.sgf.poker.usecases.game.DeleteGameUseCase;
import com.sgf.poker.usecases.game.FetchGamesUseCase;

import java.time.LocalDate;
import java.util.List;

public class GamesViewModel extends AndroidViewModel {

    // ── LiveData ──────────────────────────────────────────────────────────────
    private final MutableLiveData<List<Game>> _games = new MutableLiveData<>(List.of());
    public final LiveData<List<Game>> games = _games;

    private final MutableLiveData<String> _error = new MutableLiveData<>(null);
    public final LiveData<String> error = _error;

    // ── Use cases ─────────────────────────────────────────────────────────────
    private final FetchGamesUseCase fetchGames;
    private final CreateGameUseCase createGame;
    private final DeleteGameUseCase deleteGame;

    public GamesViewModel(@NonNull Application application) {
        super(application);
        var gameRepo   = new LocalStorageGameRepository(application);
        var playerRepo = new LocalStoragePlayerRepository(application);
        fetchGames = new FetchGamesUseCase(gameRepo);
        createGame = new CreateGameUseCase(gameRepo, playerRepo);
        deleteGame = new DeleteGameUseCase(gameRepo);
    }

    // ── Intents ───────────────────────────────────────────────────────────────

    public void loadGames() {
        try {
            _games.setValue(fetchGames.execute());
        } catch (Exception e) {
            _error.setValue(e.getMessage());
        }
    }

    public void createGame(LocalDate eventDate) {
        try {
            createGame.execute(eventDate);
            loadGames();
        } catch (Exception e) {
            _error.setValue(e.getMessage());
        }
    }

    public void deleteGame(String gameId) {
        try {
            deleteGame.execute(gameId);
            loadGames();
        } catch (Exception e) {
            _error.setValue(e.getMessage());
        }
    }

    public void clearError() { _error.setValue(null); }
}
