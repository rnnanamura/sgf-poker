// Presentation/Views/GamesListView.swift

import SwiftUI

/// Root screen — shows the game list and a button to add a new game.
struct GamesListView: View {

    @State private var viewModel = GamesViewModel.makeDefault()
    @State private var editMode: EditMode = .inactive
    @State private var isMenuShowing = false

    var body: some View {
        NavigationStack {
            ZStack(alignment: .leading) {
                content
                    .navigationBarTitleDisplayMode(.inline)
                    .environment(\.editMode, $editMode)
                    .toolbar {
                        centeredTitle
                        hamburgerButton
                        editButton
                        addButton
                    }
                    .sheet(isPresented: $viewModel.isShowingCreateSheet) {
                        CreateGameSheet(viewModel: viewModel)
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

                SideMenuView(isShowing: $isMenuShowing)
            }
        }
    }

    // MARK: – Sub-views

    @ViewBuilder
    private var content: some View {
        if viewModel.games.isEmpty {
            EmptyStateView(onAdd: viewModel.didTapCreateGame)
        } else {
            gameList
        }
    }

    private var gameList: some View {
        List {
            ForEach(Array(viewModel.games.enumerated()), id: \.element.id) { index, game in
                if editMode == .active {
                    // Edit mode: plain row with trash button, no navigation
                    GameRowView(game: game) {
                        viewModel.didRequestDelete(at: IndexSet(integer: index))
                    }
                } else {
                    // Normal mode: tappable NavigationLink, no delete button in label
                    NavigationLink {
                        GameDetailView(game: game) {
                            viewModel.didRequestDelete(at: IndexSet(integer: index))
                        }
                    } label: {
                        GameRowView(game: game, onDelete: nil)
                    }
                }
            }
            .onDelete(perform: viewModel.didRequestDelete)
        }
        .listStyle(.insetGrouped)
    }

    // MARK: – Toolbar

    private var centeredTitle: some ToolbarContent {
        ToolbarItem(placement: .principal) {
            Text("SGF - Poker 2026")
                .font(.headline)
                .fontWeight(.semibold)
        }
    }

    private var hamburgerButton: some ToolbarContent {
        ToolbarItem(placement: .topBarLeading) {
            Button {
                withAnimation(.easeInOut(duration: 0.26)) {
                    isMenuShowing.toggle()
                }
            } label: {
                Image(systemName: "line.3.horizontal")
                    .imageScale(.large)
            }
        }
    }

    private var editButton: some ToolbarContent {
        ToolbarItem(placement: .topBarTrailing) {
            Button(editMode == .active ? "Done" : "Edit") {
                editMode = editMode == .active ? .inactive : .active
            }
        }
    }

    private var addButton: some ToolbarContent {
        ToolbarItem(placement: .primaryAction) {
            Button(action: viewModel.didTapCreateGame) {
                Label("New Game", systemImage: "plus")
            }
        }
    }
}
