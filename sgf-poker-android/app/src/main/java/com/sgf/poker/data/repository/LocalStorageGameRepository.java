package com.sgf.poker.data.repository;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.sgf.poker.domain.error.GameException;
import com.sgf.poker.domain.model.Game;
import com.sgf.poker.domain.model.GamePlayer;

import java.io.*;
import java.lang.reflect.Type;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Persists games as a JSON file in the app's internal files directory.
 * Uses a custom Gson adapter for LocalDate serialization.
 */
public class LocalStorageGameRepository implements GameRepository {

    private static final String FILE_NAME = "games.json";
    private static final Type LIST_TYPE = new TypeToken<List<GameDto>>() {}.getType();

    private final File file;
    private final Gson gson;

    public LocalStorageGameRepository(Context context) {
        this.file = new File(context.getFilesDir(), FILE_NAME);
        this.gson = new GsonBuilder()
                .registerTypeAdapter(LocalDate.class, new LocalDateAdapter())
                .create();
    }

    @Override
    public synchronized List<Game> fetchAll() {
        return loadAll().stream()
                .map(GameDto::toDomain)
                .sorted(Comparator.comparing(Game::getEventDate).reversed())
                .collect(Collectors.toList());
    }

    @Override
    public synchronized void save(Game game) {
        var all = loadAll();

        // Duplicate check: same year + month
        var duplicate = all.stream().anyMatch(dto -> {
            var existing = dto.toDomain();
            return !existing.getId().equals(game.getId())
                    && existing.getEventYear()  == game.getEventYear()
                    && existing.getEventMonth() == game.getEventMonth();
        });
        if (duplicate) throw GameException.duplicateDate(game.getName());

        // Replace existing or append
        var updated = all.stream()
                .map(dto -> dto.toDomain().getId().equals(game.getId()) ? GameDto.from(game) : dto)
                .collect(Collectors.toCollection(ArrayList::new));
        if (updated.stream().noneMatch(dto -> dto.toDomain().getId().equals(game.getId()))) {
            updated.add(GameDto.from(game));
        }

        persist(updated);
    }

    @Override
    public synchronized void delete(String id) {
        var all = loadAll();
        var removed = all.removeIf(dto -> dto.toDomain().getId().equals(id));
        if (!removed) throw GameException.notFound(id);
        persist(all);
    }

    // ── Private ──────────────────────────────────────────────────────────────

    private List<GameDto> loadAll() {
        if (!file.exists()) return new ArrayList<>();
        try (var reader = new FileReader(file)) {
            List<GameDto> list = gson.fromJson(reader, LIST_TYPE);
            return list != null ? new ArrayList<>(list) : new ArrayList<>();
        } catch (IOException e) {
            throw GameException.storageFailed(e.getMessage());
        }
    }

    private void persist(List<GameDto> list) {
        try (var writer = new FileWriter(file)) {
            gson.toJson(list, writer);
        } catch (IOException e) {
            throw GameException.storageFailed(e.getMessage());
        }
    }

    // ── DTO ──────────────────────────────────────────────────────────────────

    /** Flat DTO for JSON serialization — avoids LocalDate Gson issues. */
    private static class GameDto {
        String id;
        String eventDate;   // ISO-8601 e.g. "2025-01-01"
        String createdAt;
        List<GamePlayerDto> players;

        static GameDto from(Game g) {
            var dto = new GameDto();
            dto.id = g.getId();
            dto.eventDate = g.getEventDate().toString();
            dto.createdAt = g.getCreatedAt().toString();
            dto.players = g.getPlayers().stream().map(GamePlayerDto::from).collect(Collectors.toList());
            return dto;
        }

        Game toDomain() {
            var gamePlayers = players == null ? List.<GamePlayer>of()
                    : players.stream().map(GamePlayerDto::toDomain).collect(Collectors.toList());
            return new Game(id, LocalDate.parse(eventDate), LocalDate.parse(createdAt), gamePlayers);
        }
    }

    private static class GamePlayerDto {
        String id, playerId;
        boolean coming, present, payed;
        int rebuyCount;
        Integer finalPosition;
        Double overridePrizeAmount;

        static GamePlayerDto from(GamePlayer gp) {
            var dto = new GamePlayerDto();
            dto.id = gp.getId(); dto.playerId = gp.getPlayerId();
            dto.coming = gp.isComing(); dto.present = gp.isPresent();
            dto.payed = gp.isPayed(); dto.rebuyCount = gp.getRebuyCount();
            dto.finalPosition = gp.getFinalPosition();
            dto.overridePrizeAmount = gp.getOverridePrizeAmount();
            return dto;
        }

        GamePlayer toDomain() {
            return new GamePlayer(id, playerId, coming, present, payed, rebuyCount, finalPosition, overridePrizeAmount);
        }
    }

    private static class LocalDateAdapter implements com.google.gson.JsonSerializer<LocalDate>,
            com.google.gson.JsonDeserializer<LocalDate> {
        @Override
        public com.google.gson.JsonElement serialize(LocalDate src, java.lang.reflect.Type t,
                com.google.gson.JsonSerializationContext ctx) {
            return new com.google.gson.JsonPrimitive(src.toString());
        }
        @Override
        public LocalDate deserialize(com.google.gson.JsonElement json, java.lang.reflect.Type t,
                com.google.gson.JsonDeserializationContext ctx) {
            return LocalDate.parse(json.getAsString());
        }
    }
}
