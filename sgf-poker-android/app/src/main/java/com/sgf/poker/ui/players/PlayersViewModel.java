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

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class PlayersViewModel extends AndroidViewModel {

    public enum SortOrder { BY_NAME, BY_POINTS, BY_ROLE }

    private final MutableLiveData<List<Player>> _players = new MutableLiveData<>(List.of());
    public final LiveData<List<Player>> players = _players;

    private final MutableLiveData<String> _error = new MutableLiveData<>(null);
    public final LiveData<String> error = _error;

    private List<Player> rawPlayers = List.of();
    private SortOrder sortOrder = SortOrder.BY_NAME;

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
            rawPlayers = fetchPlayers.execute();
            _players.setValue(sorted(rawPlayers));
        } catch (Exception e) {
            _error.setValue(e.getMessage());
        }
    }

    public void setSortOrder(SortOrder order) {
        sortOrder = order;
        _players.setValue(sorted(rawPlayers));
    }

    public SortOrder getSortOrder() { return sortOrder; }

    private List<Player> sorted(List<Player> list) {
        Comparator<Player> byName = Comparator.comparing(Player::getName, String.CASE_INSENSITIVE_ORDER);
        Comparator<Player> cmp = switch (sortOrder) {
            case BY_POINTS -> Comparator.comparingInt(Player::getCurrentPoints).reversed().thenComparing(byName);
            case BY_ROLE -> {
                Comparator<Player> byRole = Comparator.comparingInt(
                        (Player p) -> p.isFounder() ? 0 : p.isMember() ? 1 : 2);
                yield byRole.thenComparing(byName);
            }
            default -> byName;
        };
        return list.stream().sorted(cmp).collect(Collectors.toList());
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

    public void editPlayer(String playerId, String name, boolean isMember, boolean isFounder, int points) {
        try {
            updatePlayer.execute(playerId, p -> p.withName(name).withMember(isMember).withFounder(isFounder).withPoints(points));
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
