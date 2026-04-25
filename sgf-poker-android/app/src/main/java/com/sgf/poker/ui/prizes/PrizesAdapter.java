package com.sgf.poker.ui.prizes;

import android.graphics.Color;
import android.view.*;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.sgf.poker.databinding.ItemPrizeBinding;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class PrizesAdapter extends ListAdapter<PrizeLine, PrizesAdapter.ViewHolder> {

    private final List<PayedPlayer> payedPlayers;
    private final PrizesViewModel viewModel;

    public PrizesAdapter(List<PayedPlayer> payedPlayers, PrizesViewModel viewModel) {
        super(DIFF);
        this.payedPlayers = payedPlayers;
        this.viewModel = viewModel;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        var b = ItemPrizeBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(b);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder h, int position) {
        h.bind(getItem(position));
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private final ItemPrizeBinding b;
        private PrizeLine currentLine;
        private boolean isBinding = false;

        ViewHolder(ItemPrizeBinding binding) {
            super(binding.getRoot());
            b = binding;

            // Spinner: "— unassigned —" + one entry per payed player
            var names = new ArrayList<String>();
            names.add("— unassigned —");
            for (var pp : payedPlayers) names.add(pp.name());

            var spinnerAdapter = new ArrayAdapter<>(
                    b.getRoot().getContext(),
                    android.R.layout.simple_spinner_item,
                    names);
            spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            b.spinnerPlayer.setAdapter(spinnerAdapter);

            b.spinnerPlayer.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                    if (isBinding || currentLine == null) return;
                    if (pos == 0) {
                        viewModel.unassignPosition(currentLine.position());
                    } else {
                        viewModel.assignPosition(
                                payedPlayers.get(pos - 1).gamePlayerId(),
                                currentLine.position());
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {}
            });

            b.editPrizeAmount.setOnFocusChangeListener((v, hasFocus) -> {
                if (!hasFocus && !isBinding && currentLine != null
                        && currentLine.gamePlayerId() != null) {
                    commitAmount();
                }
            });

            b.editPrizeAmount.setOnEditorActionListener((v, actionId, event) -> {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    if (!isBinding && currentLine != null && currentLine.gamePlayerId() != null) {
                        commitAmount();
                    }
                    v.clearFocus();
                }
                return false;
            });
        }

        void bind(PrizeLine line) {
            isBinding = true;
            currentLine = line;

            b.textPosition.setText(String.valueOf(line.position()));

            int color = switch (line.position()) {
                case 1 -> Color.parseColor("#FFD700");
                case 2 -> Color.parseColor("#C0C0C0");
                case 3 -> Color.parseColor("#CD7F32");
                default -> Color.parseColor("#888888");
            };
            b.textPosition.setTextColor(color);

            b.editPrizeAmount.setText(String.format(Locale.US, "%.2f", line.amount()));
            b.editPrizeAmount.setEnabled(line.gamePlayerId() != null);

            int selectionIndex = 0;
            if (line.gamePlayerId() != null) {
                for (int i = 0; i < payedPlayers.size(); i++) {
                    if (payedPlayers.get(i).gamePlayerId().equals(line.gamePlayerId())) {
                        selectionIndex = i + 1;
                        break;
                    }
                }
            }
            b.spinnerPlayer.setSelection(selectionIndex);

            isBinding = false;
        }

        private void commitAmount() {
            try {
                double amount = Double.parseDouble(b.editPrizeAmount.getText().toString());
                viewModel.setOverridePrizeAmount(currentLine.gamePlayerId(), amount);
            } catch (NumberFormatException e) {
                b.editPrizeAmount.setText(String.format(Locale.US, "%.2f", currentLine.amount()));
            }
        }
    }

    private static final DiffUtil.ItemCallback<PrizeLine> DIFF =
            new DiffUtil.ItemCallback<>() {
                @Override
                public boolean areItemsTheSame(@NonNull PrizeLine a, @NonNull PrizeLine b) {
                    return a.position() == b.position();
                }

                @Override
                public boolean areContentsTheSame(@NonNull PrizeLine a, @NonNull PrizeLine b) {
                    return a.equals(b);
                }
            };
}