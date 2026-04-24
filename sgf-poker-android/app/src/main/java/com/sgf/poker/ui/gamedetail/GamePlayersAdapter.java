package com.sgf.poker.ui.gamedetail;

import android.view.*;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.sgf.poker.databinding.ItemGamePlayerBinding;
import com.sgf.poker.domain.model.Game;
import com.sgf.poker.domain.model.GamePlayer;

import java.util.ArrayList;
import java.util.List;

public class GamePlayersAdapter extends RecyclerView.Adapter<GamePlayersAdapter.ViewHolder> {

    private List<GamePlayer> gamePlayers = new ArrayList<>();
    private GameDetailViewModel viewModel;

    public GamePlayersAdapter(GameDetailViewModel viewModel) {
        this.viewModel = viewModel;
    }

    public void submitGame(Game game, GameDetailViewModel vm) {
        this.viewModel = vm;
        this.gamePlayers = vm.sortedPlayers(game.getPlayers());
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        var binding = ItemGamePlayerBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(gamePlayers.get(position));
    }

    @Override
    public int getItemCount() { return gamePlayers.size(); }

    class ViewHolder extends RecyclerView.ViewHolder {
        private final ItemGamePlayerBinding b;
        // Spinner entries: "—", 1..10
        private final String[] positions;

        ViewHolder(ItemGamePlayerBinding binding) {
            super(binding.getRoot());
            b = binding;
            positions = new String[11];
            positions[0] = "—";
            for (int i = 1; i <= 10; i++) positions[i] = String.valueOf(i);
        }

        void bind(GamePlayer gp) {
            b.textPlayerName.setText(viewModel.playerName(gp.getPlayerId()));
            b.iconMember.setVisibility(viewModel.playerIsMember(gp.getPlayerId()) ? View.VISIBLE : View.GONE);

            // Checkboxes — suppress listener during programmatic set
            b.checkComing.setOnCheckedChangeListener(null);
            b.checkPresent.setOnCheckedChangeListener(null);
            b.checkPayed.setOnCheckedChangeListener(null);

            b.checkComing.setChecked(gp.isComing());
            b.checkPresent.setChecked(gp.isPresent());
            b.checkPayed.setChecked(gp.isPayed());

            b.checkComing.setOnCheckedChangeListener((v, c) -> viewModel.toggleComing(gp.getId()));
            b.checkPresent.setOnCheckedChangeListener((v, c) -> viewModel.togglePresent(gp.getId()));
            b.checkPayed.setOnCheckedChangeListener((v, c) -> viewModel.togglePayed(gp.getId()));

            // Rebuy counter
            b.textRebuyCount.setText(String.valueOf(gp.getRebuyCount()));
            b.btnRebuyMinus.setEnabled(gp.getRebuyCount() > 0);
            b.btnRebuyMinus.setOnClickListener(v -> viewModel.decrementRebuy(gp.getId()));
            b.btnRebuyPlus.setOnClickListener(v -> viewModel.incrementRebuy(gp.getId()));

            // Position spinner
            var adapter = new ArrayAdapter<>(b.getRoot().getContext(),
                    android.R.layout.simple_spinner_item, positions);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            b.spinnerPosition.setAdapter(adapter);

            int spinnerIndex = gp.getFinalPosition() != null ? gp.getFinalPosition() : 0;
            b.spinnerPosition.setSelection(spinnerIndex, false);
            b.spinnerPosition.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                boolean firstCall = true;
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                    if (firstCall) { firstCall = false; return; } // skip initial programmatic set
                    Integer position = pos == 0 ? null : pos;
                    viewModel.setFinalPosition(gp.getId(), position);
                }
                @Override
                public void onNothingSelected(AdapterView<?> parent) {}
            });
        }
    }
}
