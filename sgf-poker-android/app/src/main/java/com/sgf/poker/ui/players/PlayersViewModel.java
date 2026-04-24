package com.sgf.poker.ui.players;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.sgf.poker.data.repository.LocalStoragePlayerRepository;
import com.sgf.poker.domain.model.Player;
import com.sgf.poker.usecases.player.CreatePlayerUseCase;
import com.sgf.poker.usecases.player.DeletePlayerUseCase;
import com.sgf.poker.usecases.player.FetchPlayersUseCase;
import com.sgf.poker.usecases.player.UpdatePlayerUseCase;

import java.util.List;

public class PlayersViewModel extends AndroidViewModel {

    private final MutableLiveData<List<Player>> _players = new MutableLiveData<>(List.of());
    public final LiveData<List<Player>> players = _players;

    private final MutableLiveData<String> _error = new MutableLiveData<>(null);
    public final LiveData<String> error = _error;

    private final FetchPlayersUseCase fetchPlayers;
    private final CreatePlayerUseCase createPlayer;
    private final DeletePlayerUseCase deletePlayer;
    private final UpdatePlayerUseCase updatePlayer;

    public PlayersViewModel(@NonNull Application application) {
        super(application);
        var repo = new LocalStoragePlayerRepository(application);
        fetchPlayers = new FetchPlayersUseCase(repo);
        createPlayer = new CreatePlayerUseCase(repo);
        deletePlayer = new DeletePlayerUseCase(repo);
        updatePlayer = new UpdatePlayerUseCase(repo);
    }

    public void loadPlayers() {
        try {
            _players.setValue(fetchPlayers.execute());
        } catch (Exception e) {
            _error.setValue(e.getMessage());
        }
    }

    public void addPlayer(String name, boolean isMember, boolean isFounder) {
        try {
            createPlayer.execute(name, isMember, isFounder);
            loadPlayers();
        } catch (Exception e) {
            _error.setValue(e.getMessage());
        }
    }

    public void deletePlayer(String playerId) {
        try {
            deletePlayer.execute(playerId);
            loadPlayers();
        } catch (Exception e) {
            _error.setValue(e.getMessage());
        }
    }

    public void toggleMember(String playerId) {
        try {
            var player = getPlayer(playerId);
            if (player == null) return;
            updatePlayer.execute(playerId, p -> p.withMember(!p.isMember()));
            loadPlayers();
        } catch (Exception e) {
            _error.setValue(e.getMessage());
        }
    }

    public void editPlayer(String playerId, String name, boolean isMember, boolean isFounder) {
        try {
            updatePlayer.execute(playerId, p -> p.withName(name).withMember(isMember).withFounder(isFounder));
            loadPlayers();
        } catch (Exception e) {
            _error.setValue(e.getMessage());
        }
    }

    public void toggleFounder(String playerId) {
        try {
            updatePlayer.execute(playerId, p -> p.withFounder(!p.isFounder()));
            loadPlayers();
        } catch (Exception e) {
            _error.setValue(e.getMessage());
        }
    }

    private Player getPlayer(String id) {
        if (_players.getValue() == null) return null;
        return _players.getValue().stream()
                .filter(p -> p.getId().equals(id))
                .findFirst().orElse(null);
    }

    public void clearError() { _error.setValue(null); }
}
