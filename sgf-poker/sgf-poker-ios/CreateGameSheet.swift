// Presentation/Views/CreateGameSheet.swift

import SwiftUI

/// Modal sheet for picking the event date of a new game.
/// Contains explicit Cancel and Submit buttons inside the form.
struct CreateGameSheet: View {

    @Bindable var viewModel: GamesViewModel

    var body: some View {
        VStack(spacing: 0) {
            header
            Divider()
            datePicker
            Divider()
            actionButtons
        }
        .presentationDetents([.height(460)])
        .presentationDragIndicator(.visible)
        .presentationCornerRadius(16)
    }

    // MARK: – Sub-views

    private var header: some View {
        Text("Select Event Date")
            .font(.headline)
            .frame(maxWidth: .infinity)
            .padding(.vertical, 16)
    }

    private var datePicker: some View {
        DatePicker(
            "Event Date",
            selection: $viewModel.newEventDate,
            displayedComponents: [.date]
        )
        .datePickerStyle(.graphical)
        .labelsHidden()
        .padding(.horizontal, 8)
    }

    private var actionButtons: some View {
        HStack(spacing: 12) {
            Button(action: viewModel.cancelCreateGame) {
                Text("Cancel")
                    .frame(maxWidth: .infinity)
                    .padding(.vertical, 14)
                    .background(Color(.secondarySystemGroupedBackground))
                    .clipShape(RoundedRectangle(cornerRadius: 12))
            }
            .foregroundStyle(.primary)

            Button(action: viewModel.confirmCreateGame) {
                Text("Submit")
                    .fontWeight(.semibold)
                    .frame(maxWidth: .infinity)
                    .padding(.vertical, 14)
                    .background(Color.accentColor)
                    .foregroundStyle(.white)
                    .clipShape(RoundedRectangle(cornerRadius: 12))
            }
        }
        .padding(.horizontal, 16)
        .padding(.vertical, 16)
    }
}
