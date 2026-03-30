// Presentation/Views/ManagePlayersView.swift

import SwiftUI

struct ManagePlayersView: View {

    @State private var viewModel = PlayersViewModel.makeDefault()
    @State private var editMode: EditMode = .inactive

    var body: some View {
        content
            .navigationTitle("Manage Players")
            .navigationBarTitleDisplayMode(.inline)
            .environment(\.editMode, $editMode)
            .toolbar {
                editButton
                addButton
            }
            .sheet(isPresented: $viewModel.isShowingAddSheet) {
                AddPlayerSheet(viewModel: viewModel)
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

    // MARK: – Content

    @ViewBuilder
    private var content: some View {
        if viewModel.players.isEmpty {
            emptyState
        } else {
            playerList
        }
    }

    private var emptyState: some View {
        ContentUnavailableView {
            Label("No Players Yet", systemImage: "person.2.fill")
        } description: {
            Text("Add your first player to get started.")
        } actions: {
            Button("Add Player", action: viewModel.didTapAddPlayer)
                .buttonStyle(.borderedProminent)
        }
    }

    private var playerList: some View {
        List {
            ForEach(Array(viewModel.players.enumerated()), id: \.element.id) { index, player in
                PlayerRowView(
                    player: player,
                    onMakeMember:  { viewModel.makePlayerMember(id: player.id) },
                    onMakeFounder: { viewModel.makePlayerFounder(id: player.id) },
                    onDelete:      { viewModel.deletePlayer(at: IndexSet(integer: index)) }
                )
            }
            .onDelete { viewModel.deletePlayer(at: $0) }
        }
        .listStyle(.insetGrouped)
    }

    // MARK: – Toolbar

    private var editButton: some ToolbarContent {
        ToolbarItem(placement: .topBarTrailing) {
            Button(editMode == .active ? "Done" : "Edit") {
                editMode = editMode == .active ? .inactive : .active
            }
        }
    }

    private var addButton: some ToolbarContent {
        ToolbarItem(placement: .primaryAction) {
            Button(action: viewModel.didTapAddPlayer) {
                Label("Add Player", systemImage: "plus")
            }
        }
    }
}
