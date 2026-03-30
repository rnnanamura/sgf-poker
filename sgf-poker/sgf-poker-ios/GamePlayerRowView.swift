// Presentation/Views/GamePlayerRowView.swift

import SwiftUI

/// A row representing a single GamePlayer inside GameDetailView.
struct GamePlayerRowView: View {

    let name: String
    let gamePlayer: GamePlayer
    let onToggleComing:   () -> Void
    let onTogglePresent:  () -> Void
    let onTogglePayed:    () -> Void
    let onIncrementRebuy: () -> Void
    let onDecrementRebuy: () -> Void
    let onSetPosition:    (Int?) -> Void

    var body: some View {
        VStack(alignment: .leading, spacing: 10) {
            // Name + checkboxes on the same line
            HStack(spacing: 16) {
                Text(name)
                    .font(.body)
                    .fontWeight(.semibold)
                    .lineLimit(1)

                Spacer()

                checkBox(label: "Coming",  isOn: gamePlayer.coming,  action: onToggleComing)
                checkBox(label: "Present", isOn: gamePlayer.present, action: onTogglePresent)
                checkBox(label: "Payed",   isOn: gamePlayer.payed,   action: onTogglePayed)
            }

            // Rebuy counter + position picker
            HStack(spacing: 16) {
                rebuyCounter
                Spacer()
                positionPicker
            }
        }
        .padding(.horizontal, 16)
        .padding(.vertical, 12)
    }

    // MARK: – Checkbox

    private func checkBox(label: String, isOn: Bool, action: @escaping () -> Void) -> some View {
        Button(action: action) {
            VStack(spacing: 3) {
                Image(systemName: isOn ? "checkmark.square.fill" : "square")
                    .font(.system(size: 20))
                    .foregroundStyle(isOn ? Color.accentColor : .secondary)
                Text(label)
                    .font(.caption2)
                    .foregroundStyle(.secondary)
            }
        }
        .buttonStyle(.borderless)
    }

    // MARK: – Rebuy counter

    private var rebuyCounter: some View {
        HStack(spacing: 0) {
            Text("Rebuys")
                .font(.caption)
                .foregroundStyle(.secondary)
                .padding(.trailing, 8)

            Button(action: onDecrementRebuy) {
                Image(systemName: "minus.circle.fill")
                    .font(.system(size: 22))
                    .foregroundStyle(gamePlayer.rebuyCount > 0 ? Color.accentColor : .secondary)
            }
            .buttonStyle(.borderless)
            .disabled(gamePlayer.rebuyCount == 0)

            Text("\(gamePlayer.rebuyCount)")
                .font(.system(size: 17, weight: .semibold, design: .rounded))
                .frame(minWidth: 28)
                .multilineTextAlignment(.center)

            Button(action: onIncrementRebuy) {
                Image(systemName: "plus.circle.fill")
                    .font(.system(size: 22))
                    .foregroundStyle(Color.accentColor)
            }
            .buttonStyle(.borderless)
        }
    }

    // MARK: – Position picker

    private var positionPicker: some View {
        HStack(spacing: 8) {
            Text("Position")
                .font(.caption)
                .foregroundStyle(.secondary)

            Menu {
                Button("—  None") { onSetPosition(nil) }
                Divider()
                ForEach(1...10, id: \.self) { pos in
                    Button("\(pos)") { onSetPosition(pos) }
                }
            } label: {
                HStack(spacing: 4) {
                    Text(gamePlayer.finalPosition.map { "\($0)" } ?? "—")
                        .font(.system(size: 15, weight: .medium, design: .rounded))
                        .foregroundStyle(.primary)
                        .frame(minWidth: 24)
                    Image(systemName: "chevron.up.chevron.down")
                        .font(.caption2)
                        .foregroundStyle(.secondary)
                }
                .padding(.horizontal, 10)
                .padding(.vertical, 6)
                .background(Color(.tertiarySystemGroupedBackground))
                .clipShape(RoundedRectangle(cornerRadius: 8))
            }
        }
    }
}
