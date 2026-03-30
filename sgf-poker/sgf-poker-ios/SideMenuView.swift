// Presentation/Views/SideMenuView.swift

import SwiftUI

/// Slide-in side menu triggered by the hamburger button.
struct SideMenuView: View {

    @Binding var isShowing: Bool

    var body: some View {
        ZStack(alignment: .leading) {
            // Dimmed backdrop — tap to close
            if isShowing {
                Color.black.opacity(0.35)
                    .ignoresSafeArea()
                    .onTapGesture { close() }
                    .transition(.opacity)
            }

            // Drawer panel
            if isShowing {
                drawerPanel
                    .transition(.move(edge: .leading))
            }
        }
        .animation(.easeInOut(duration: 0.26), value: isShowing)
    }

    // MARK: – Drawer

    private var drawerPanel: some View {
        VStack(alignment: .leading, spacing: 0) {
            drawerHeader
            Divider()
            menuItems
            Spacer()
        }
        .frame(width: 280)
        .frame(maxHeight: .infinity)
        .background(Color(.systemGroupedBackground))
        .ignoresSafeArea(edges: .vertical)
    }

    private var drawerHeader: some View {
        VStack(alignment: .leading, spacing: 4) {
            Text("SGF - Poker 2026")
                .font(.title3)
                .fontWeight(.bold)
            Text("Menu")
                .font(.caption)
                .foregroundStyle(.secondary)
        }
        .padding(.horizontal, 24)
        .padding(.top, 64)
        .padding(.bottom, 20)
    }

    private var menuItems: some View {
        VStack(spacing: 0) {
            menuItem(
                title: "Manage Players",
                icon: "person.2.fill",
                destination: ManagePlayersView()
            )
            Divider().padding(.leading, 56)
            menuItem(
                title: "Settings",
                icon: "gearshape.fill",
                destination: SettingsView()
            )
        }
        .padding(.top, 8)
    }

    private func menuItem<Destination: View>(
        title: String,
        icon: String,
        destination: Destination
    ) -> some View {
        NavigationLink(destination: destination) {
            HStack(spacing: 16) {
                Image(systemName: icon)
                    .font(.body)
                    .foregroundStyle(.tint)
                    .frame(width: 24)

                Text(title)
                    .font(.body)
                    .foregroundStyle(.primary)

                Spacer()

                Image(systemName: "chevron.right")
                    .font(.caption)
                    .foregroundStyle(.tertiary)
            }
            .padding(.horizontal, 24)
            .padding(.vertical, 16)
        }
        .simultaneousGesture(TapGesture().onEnded { close() })
    }

    // MARK: – Helpers

    private func close() {
        withAnimation(.easeInOut(duration: 0.26)) {
            isShowing = false
        }
    }
}
