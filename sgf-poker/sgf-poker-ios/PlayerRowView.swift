// Presentation/Views/PlayerRowView.swift

import SwiftUI

struct PlayerRowView: View {

    let player: Player
    let onMakeMember:  () -> Void
    let onMakeFounder: () -> Void
    let onDelete:      () -> Void

    @Environment(\.editMode) private var editMode

    private var isEditing: Bool {
        editMode?.wrappedValue == .active
    }

    var body: some View {
        HStack(spacing: 14) {
            avatar
            info
            Spacer()
            if isEditing {
                contextActions
            } else {
                pointsBadge
            }
        }
        .padding(.vertical, 4)
    }

    // MARK: – Sub-views

    private var avatar: some View {
        ZStack {
            Circle()
                .fill(avatarColor.opacity(0.15))
                .frame(width: 40, height: 40)
            Text(initials)
                .font(.system(size: 15, weight: .semibold))
                .foregroundStyle(avatarColor)
        }
    }

    private var info: some View {
        VStack(alignment: .leading, spacing: 3) {
            Text(player.name)
                .font(.body)
                .fontWeight(.medium)

            HStack(spacing: 6) {
                if player.isFounder {
                    badge(text: "Founder", color: .purple)
                }
                if player.isMember {
                    badge(text: "Member", color: .blue)
                }
                if !player.isFounder && !player.isMember {
                    Text("Guest")
                        .font(.caption2)
                        .foregroundStyle(.tertiary)
                }
            }
        }
    }

    private var pointsBadge: some View {
        VStack(spacing: 1) {
            Text("\(player.currentPoints)")
                .font(.system(size: 17, weight: .bold, design: .rounded))
                .foregroundStyle(.primary)
            Text("pts")
                .font(.caption2)
                .foregroundStyle(.secondary)
        }
        .frame(minWidth: 36)
    }

    private var contextActions: some View {
        HStack(spacing: 4) {
            actionButton(
                icon: player.isFounder ? "star.fill" : "star",
                color: .purple,
                action: onMakeFounder
            )
            actionButton(
                icon: player.isMember ? "person.fill.checkmark" : "person.badge.plus",
                color: .blue,
                action: onMakeMember
            )
            actionButton(
                icon: "trash",
                color: .red,
                action: onDelete
            )
        }
    }

    // MARK: – Helpers

    private func badge(text: String, color: Color) -> some View {
        Text(text)
            .font(.caption2)
            .fontWeight(.medium)
            .padding(.horizontal, 6)
            .padding(.vertical, 2)
            .background(color.opacity(0.12))
            .foregroundStyle(color)
            .clipShape(Capsule())
    }

    private func actionButton(icon: String, color: Color, action: @escaping () -> Void) -> some View {
        Button(action: action) {
            Image(systemName: icon)
                .font(.system(size: 15))
                .foregroundStyle(color)
                .frame(width: 32, height: 32)
        }
        .buttonStyle(.borderless)
    }

    private var initials: String {
        player.name
            .split(separator: " ")
            .prefix(2)
            .compactMap { $0.first.map(String.init) }
            .joined()
            .uppercased()
    }

    private var avatarColor: Color {
        player.isFounder ? .purple : player.isMember ? .blue : .gray
    }
}
