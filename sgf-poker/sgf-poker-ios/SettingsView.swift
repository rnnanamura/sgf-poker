// Presentation/Views/SettingsView.swift

import SwiftUI

/// Placeholder screen for app settings.
struct SettingsView: View {

    var body: some View {
        ContentUnavailableView {
            Label("Settings", systemImage: "gearshape.fill")
        } description: {
            Text("Settings coming soon.")
        }
        .navigationTitle("Settings")
        .navigationBarTitleDisplayMode(.inline)
    }
}
