package com.sgf.poker.ui.games;

import android.view.*;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.sgf.poker.databinding.ItemGameBinding;
import com.sgf.poker.domain.model.Game;

import java.time.format.DateTimeFormatter;
import java.util.function.Consumer;

public class GamesAdapter extends ListAdapter<Game, GamesAdapter.ViewHolder> {

    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("d MMM yyyy");

    private final Consumer<Game> onItemClick;
    private final Consumer<Game> onDeleteClick;

    public GamesAdapter(Consumer<Game> onItemClick, Consumer<Game> onDeleteClick) {
        super(DIFF_CALLBACK);
        this.onItemClick = onItemClick;
        this.onDeleteClick = onDeleteClick;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        var binding = ItemGameBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(getItem(position));
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private final ItemGameBinding b;

        ViewHolder(ItemGameBinding binding) {
            super(binding.getRoot());
            b = binding;
        }

        void bind(Game game) {
            b.textGameName.setText(game.getName());
            b.textGameDate.setText(DATE_FMT.format(game.getEventDate()));
            b.textPlayerCount.setText(game.getPlayers().size() + " players");
            b.getRoot().setOnClickListener(v -> onItemClick.accept(game));
            b.btnDelete.setOnClickListener(v -> onDeleteClick.accept(game));
        }
    }

    private static final DiffUtil.ItemCallback<Game> DIFF_CALLBACK =
            new DiffUtil.ItemCallback<>() {
                @Override
                public boolean areItemsTheSame(@NonNull Game a, @NonNull Game b) {
                    return a.getId().equals(b.getId());
                }
                @Override
                public boolean areContentsTheSame(@NonNull Game a, @NonNull Game b) {
                    return a.equals(b) && a.getName().equals(b.getName());
                }
            };
}
