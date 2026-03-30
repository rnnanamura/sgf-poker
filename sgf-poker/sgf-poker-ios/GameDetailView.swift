// Presentation/Views/GameDetailView.swift

import SwiftUI

/// Detail screen for a single game entry.
struct GameDetailView: View {

    let onDelete: () -> Void

    @State private var viewModel: GameDetailViewModel
    @Environment(\.dismiss) private var dismiss

    init(game: Game, onDelete: @escaping () -> Void) {
        self.onDelete = onDelete
        self._viewModel = State(initialValue: GameDetailViewModel.make(for: game))
    }

    var body: some View {
        ScrollView {
            LazyVStack(spacing: 0) {
                if viewModel.game.players.isEmpty {
                    emptyPlayersView
                } else {
                    ForEach(viewModel.game.players) { gp in
                        GamePlayerRowView(
                            name: viewModel.playerName(for: gp),
                            gamePlayer: gp,
                            onToggleComing:   { viewModel.toggleComing(for: gp.id) },
                            onTogglePresent:  { viewModel.togglePresent(for: gp.id) },
                            onTogglePayed:    { viewModel.togglePayed(for: gp.id) },
                            onIncrementRebuy: { viewModel.incrementRebuy(for: gp.id) },
                            onDecrementRebuy: { viewModel.decrementRebuy(for: gp.id) },
                            onSetPosition:    { viewModel.setFinalPosition($0, for: gp.id) }
                        )

                        if gp.id != viewModel.game.players.last?.id {
                            Divider().padding(.leading, 16)
                        }
                    }
                }
            }
            .background(Color(.secondarySystemGroupedBackground))
            .clipShape(RoundedRectangle(cornerRadius: 12))
            .padding(.horizontal, 16)
            .padding(.vertical, 12)
        }
        .background(Color(.systemGroupedBackground))
        .navigationTitle(viewModel.game.name)
        .navigationBarTitleDisplayMode(.inline)
        .toolbar {
            prizesButton
            deleteButton
        }
        .alert(
            "Something went wrong",
            isPresented: Binding(
                get: { viewModel.errorMessage != nil },
                set: { if !$0 { viewModel.errorMessage = nil } }
            ),
            actions: { Button("OK", role: .cancel) {} },
            message: { Text(viewModel.errorMessage ?? "") }
        )
        .onAppear { viewModel.onAppear() }
    }

    // MARK: – Empty state

    private var emptyPlayersView: some View {
        VStack(spacing: 8) {
            Image(systemName: "person.slash")
                .font(.system(size: 36))
                .foregroundStyle(.secondary)
            Text("No players in this game.")
                .font(.subheadline)
                .foregroundStyle(.secondary)
        }
        .frame(maxWidth: .infinity)
        .padding(.vertical, 40)
    }

    // MARK: – Toolbar
    private var prizesButton: some ToolbarContent {
        ToolbarItem(placement: .topBarTrailing) {
            NavigationLink {
                PrizesView(game: viewModel.game, players: viewModel.players)
            } label: {
                Label("Prizes", systemImage: "trophy.fill")
            }
        }
    }
    
    private var deleteButton: some ToolbarContent {
        ToolbarItem(placement: .destructiveAction) {
            Button(role: .destructive) {
                onDelete()
                dismiss()
            } label: {
                Label("Delete", systemImage: "trash")
            }
        }
    }
}
