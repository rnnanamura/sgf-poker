// Presentation/Views/AddPlayerSheet.swift

import SwiftUI

/// Modal sheet for adding a new player.
struct AddPlayerSheet: View {

    @Bindable var viewModel: PlayersViewModel
    @FocusState private var isNameFocused: Bool

    var body: some View {
        VStack(spacing: 0) {
            header
            Divider()
            form
            Divider()
            actionButtons
        }
        .presentationDetents([.height(340)])
        .presentationDragIndicator(.visible)
        .presentationCornerRadius(16)
    }

    // MARK: – Sub-views

    private var header: some View {
        Text("New Player")
            .font(.headline)
            .frame(maxWidth: .infinity)
            .padding(.vertical, 16)
    }

    private var form: some View {
        VStack(spacing: 0) {
            TextField("Player name", text: $viewModel.newPlayerName)
                .focused($isNameFocused)
                .submitLabel(.done)
                .padding(.horizontal, 20)
                .padding(.vertical, 14)

            Divider().padding(.leading, 20)

            Toggle("Member", isOn: $viewModel.newPlayerIsMember)
                .padding(.horizontal, 20)
                .padding(.vertical, 12)

            Divider().padding(.leading, 20)

            Toggle("Founder", isOn: $viewModel.newPlayerIsFounder)
                .padding(.horizontal, 20)
                .padding(.vertical, 12)
        }
        .background(Color(.secondarySystemGroupedBackground))
        .onAppear { isNameFocused = true }
    }

    private var actionButtons: some View {
        HStack(spacing: 12) {
            Button(action: viewModel.cancelAddPlayer) {
                Text("Cancel")
                    .frame(maxWidth: .infinity)
                    .padding(.vertical, 14)
                    .background(Color(.secondarySystemGroupedBackground))
                    .clipShape(RoundedRectangle(cornerRadius: 12))
            }
            .foregroundStyle(.primary)

            Button(action: viewModel.confirmAddPlayer) {
                Text("Add")
                    .fontWeight(.semibold)
                    .frame(maxWidth: .infinity)
                    .padding(.vertical, 14)
                    .background(viewModel.isAddConfirmEnabled ? Color.accentColor : Color.gray.opacity(0.3))
                    .foregroundStyle(viewModel.isAddConfirmEnabled ? .white : .secondary)
                    .clipShape(RoundedRectangle(cornerRadius: 12))
            }
            .disabled(!viewModel.isAddConfirmEnabled)
        }
        .padding(.horizontal, 16)
        .padding(.vertical, 16)
    }
}
