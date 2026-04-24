package com.sgf.poker.ui.players;

import android.view.*;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.sgf.poker.databinding.ItemPlayerBinding;
import com.sgf.poker.domain.model.Player;

import java.util.function.Consumer;

public class PlayersAdapter extends ListAdapter<Player, PlayersAdapter.ViewHolder> {

    private final Consumer<Player> onEdit;
    private final Consumer<String> onToggleMember;
    private final Consumer<String> onToggleFounder;
    private final Consumer<String> onDelete;

    public PlayersAdapter(Consumer<Player> onEdit,
                          Consumer<String> onToggleMember,
                          Consumer<String> onToggleFounder,
                          Consumer<String> onDelete) {
        super(DIFF);
        this.onEdit          = onEdit;
        this.onToggleMember  = onToggleMember;
        this.onToggleFounder = onToggleFounder;
        this.onDelete        = onDelete;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        var b = ItemPlayerBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(b);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder h, int position) {
        h.bind(getItem(position));
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private final ItemPlayerBinding b;

        ViewHolder(ItemPlayerBinding binding) {
            super(binding.getRoot());
            b = binding;
        }

        void bind(Player player) {
            b.textPlayerName.setText(player.getName());
            b.textPoints.setText(player.getCurrentPoints() + " pts");

            // Badges
            b.badgeFounder.setVisibility(player.isFounder() ? View.VISIBLE : View.GONE);
            b.badgeMember.setVisibility(player.isMember()   ? View.VISIBLE : View.GONE);
            b.badgeGuest.setVisibility((!player.isMember() && !player.isFounder()) ? View.VISIBLE : View.GONE);

            b.getRoot().setOnClickListener(v -> onEdit.accept(player));
            b.btnToggleMember.setOnClickListener(v -> onToggleMember.accept(player.getId()));
            b.btnToggleFounder.setOnClickListener(v -> onToggleFounder.accept(player.getId()));
            b.btnDelete.setOnClickListener(v -> onDelete.accept(player.getId()));
        }
    }

    private static final DiffUtil.ItemCallback<Player> DIFF = new DiffUtil.ItemCallback<>() {
        @Override
        public boolean areItemsTheSame(@NonNull Player a, @NonNull Player b) {
            return a.getId().equals(b.getId());
        }
        @Override
        public boolean areContentsTheSame(@NonNull Player a, @NonNull Player b) {
            return a.getName().equals(b.getName())
                    && a.isMember() == b.isMember()
                    && a.isFounder() == b.isFounder()
                    && a.getCurrentPoints() == b.getCurrentPoints();
        }
    };
}
