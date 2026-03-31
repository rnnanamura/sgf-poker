package com.sgf.poker.data.repository;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.sgf.poker.domain.error.PlayerException;
import com.sgf.poker.domain.model.Player;

import java.io.*;
import java.lang.reflect.Type;
import java.util.*;
import java.util.stream.Collectors;

/** Persists players as a JSON file in the app's internal files directory. */
public class LocalStoragePlayerRepository implements PlayerRepository {

    private static final String FILE_NAME = "players.json";
    private static final Type LIST_TYPE = new TypeToken<List<PlayerDto>>() {}.getType();

    private final File file;
    private final Gson gson = new Gson();

    public LocalStoragePlayerRepository(Context context) {
        this.file = new File(context.getFilesDir(), FILE_NAME);
    }

    @Override
    public synchronized List<Player> fetchAll() {
        return loadAll().stream()
                .map(PlayerDto::toDomain)
                .sorted(Comparator.comparing(p -> p.getName().toLowerCase()))
                .collect(Collectors.toList());
    }

    @Override
    public synchronized void save(Player player) {
        var all = loadAll();

        var duplicate = all.stream().anyMatch(dto ->
                !dto.id.equals(player.getId()) &&
                dto.name.equalsIgnoreCase(player.getName()));
        if (duplicate) throw PlayerException.duplicateName(player.getName());

        var updated = all.stream()
                .map(dto -> dto.id.equals(player.getId()) ? PlayerDto.from(player) : dto)
                .collect(Collectors.toCollection(ArrayList::new));
        if (updated.stream().noneMatch(dto -> dto.id.equals(player.getId()))) {
            updated.add(PlayerDto.from(player));
        }

        persist(updated);
    }

    @Override
    public synchronized void delete(String id) {
        var all = loadAll();
        var removed = all.removeIf(dto -> dto.id.equals(id));
        if (!removed) throw PlayerException.notFound(id);
        persist(all);
    }

    // ── Private ──────────────────────────────────────────────────────────────

    private List<PlayerDto> loadAll() {
        if (!file.exists()) return new ArrayList<>();
        try (var reader = new FileReader(file)) {
            List<PlayerDto> list = gson.fromJson(reader, LIST_TYPE);
            return list != null ? new ArrayList<>(list) : new ArrayList<>();
        } catch (IOException e) {
            throw PlayerException.storageFailed(e.getMessage());
        }
    }

    private void persist(List<PlayerDto> list) {
        try (var writer = new FileWriter(file)) {
            gson.toJson(list, writer);
        } catch (IOException e) {
            throw PlayerException.storageFailed(e.getMessage());
        }
    }

    // ── DTO ──────────────────────────────────────────────────────────────────

    private static class PlayerDto {
        String id, name;
        boolean isMember, isFounder;
        int currentPoints;

        static PlayerDto from(Player p) {
            var dto = new PlayerDto();
            dto.id = p.getId(); dto.name = p.getName();
            dto.isMember = p.isMember(); dto.isFounder = p.isFounder();
            dto.currentPoints = p.getCurrentPoints();
            return dto;
        }

        Player toDomain() {
            return new Player(id, name, isMember, isFounder, currentPoints);
        }
    }
}
