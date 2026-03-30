// Presentation/Views/PrizesView.swift

import SwiftUI

struct PrizesView: View {

    @State private var viewModel: PrizesViewModel

    init(game: Game, players: [Player]) {
        self._viewModel = State(initialValue: PrizesViewModel(game: game, players: players))
    }

    var body: some View {
        ScrollView {
            if let calc = viewModel.calculation {
                VStack(spacing: 16) {
                    breakdownSection(calc)
                    poolSection(calc)
                    distributionSection(calc)
                    if !calc.prizes.isEmpty {
                        prizesSection(calc)
                    }
                    if calc.unrankedCount > 0 {
                        unrankedWarning(calc)
                    }
                }
                .padding(.horizontal, 16)
                .padding(.vertical, 12)
            }
        }
        .background(Color(.systemGroupedBackground))
        .navigationTitle("Prizes")
        .navigationBarTitleDisplayMode(.inline)
        .onAppear { viewModel.onAppear() }
    }

    // MARK: – Breakdown

    private func breakdownSection(_ calc: PrizeCalculation) -> some View {
        VStack(alignment: .leading, spacing: 0) {
            sectionHeader("Entry breakdown")
            VStack(spacing: 0) {
                infoRow(
                    label: "Members  (\(calc.memberCount) × $\(Int(PrizeRules.memberEntryFee)))",
                    value: viewModel.formatted(Double(calc.memberCount) * PrizeRules.memberEntryFee)
                )
                Divider().padding(.leading, 16)
                infoRow(
                    label: "Non-members  (\(calc.nonMemberCount) × $\(Int(PrizeRules.nonMemberEntryFee)))",
                    value: viewModel.formatted(Double(calc.nonMemberCount) * PrizeRules.nonMemberEntryFee)
                )
                Divider().padding(.leading, 16)
                infoRow(
                    label: "Rebuys  (\(calc.totalRebuys) × $\(Int(PrizeRules.rebuyAmount)))",
                    value: viewModel.formatted(Double(calc.totalRebuys) * PrizeRules.rebuyAmount)
                )
            }
            .background(Color(.secondarySystemGroupedBackground))
            .clipShape(RoundedRectangle(cornerRadius: 12))
        }
    }

    // MARK: – Pool

    private func poolSection(_ calc: PrizeCalculation) -> some View {
        VStack(alignment: .leading, spacing: 0) {
            sectionHeader("Pool")
            VStack(spacing: 0) {
                infoRow(label: "Total collected", value: viewModel.formatted(calc.totalPool))
                Divider().padding(.leading, 16)
                highlightRow(
                    label: "Bounty pool  (20%)",
                    value: viewModel.formatted(calc.bountyPool),
                    color: .orange
                )
                Divider().padding(.leading, 16)
                highlightRow(
                    label: "Prize pool  (80%)",
                    value: viewModel.formatted(calc.prizePool),
                    color: .accentColor
                )
            }
            .background(Color(.secondarySystemGroupedBackground))
            .clipShape(RoundedRectangle(cornerRadius: 12))
        }
    }

    // MARK: – Distribution

    private func distributionSection(_ calc: PrizeCalculation) -> some View {
        VStack(alignment: .leading, spacing: 0) {
            sectionHeader("Prize split  ·  \(calc.presentCount) players")
            VStack(spacing: 0) {
                ForEach(Array(calc.distribution.enumerated()), id: \.offset) { index, pct in
                    if index > 0 { Divider().padding(.leading, 16) }
                    infoRow(
                        label: positionLabel(index + 1),
                        value: "\(viewModel.percent(pct))  ·  \(viewModel.formatted(calc.prizePool * pct))"
                    )
                }
            }
            .background(Color(.secondarySystemGroupedBackground))
            .clipShape(RoundedRectangle(cornerRadius: 12))
        }
    }

    // MARK: – Prizes

    private func prizesSection(_ calc: PrizeCalculation) -> some View {
        VStack(alignment: .leading, spacing: 0) {
            sectionHeader("Winners")
            VStack(spacing: 0) {
                ForEach(Array(calc.prizes.enumerated()), id: \.element.id) { index, prize in
                    if index > 0 { Divider().padding(.leading, 16) }
                    prizeRow(prize: prize)
                }
            }
            .background(Color(.secondarySystemGroupedBackground))
            .clipShape(RoundedRectangle(cornerRadius: 12))
        }
    }

    private func prizeRow(prize: PlayerPrize) -> some View {
        HStack(spacing: 14) {
            positionBadge(prize.position)
            Text(prize.playerName)
                .font(.body)
                .lineLimit(1)
            Spacer()
            Text(viewModel.formatted(prize.prizeAmount))
                .font(.body.bold())
                .foregroundStyle(Color.accentColor)
        }
        .padding(.horizontal, 16)
        .padding(.vertical, 12)
    }

    // MARK: – Unranked warning

    private func unrankedWarning(_ calc: PrizeCalculation) -> some View {
        HStack(spacing: 10) {
            Image(systemName: "exclamationmark.triangle.fill")
                .foregroundStyle(.orange)
            Text("\(calc.unrankedCount) present player\(calc.unrankedCount == 1 ? "" : "s") still missing a final position.")
                .font(.footnote)
                .foregroundStyle(.secondary)
        }
        .padding(14)
        .frame(maxWidth: .infinity, alignment: .leading)
        .background(Color.orange.opacity(0.08))
        .clipShape(RoundedRectangle(cornerRadius: 10))
    }

    // MARK: – Row helpers

    private func infoRow(label: String, value: String) -> some View {
        HStack {
            Text(label)
                .font(.body)
                .foregroundStyle(.secondary)
            Spacer()
            Text(value)
                .font(.body)
                .foregroundStyle(.primary)
        }
        .padding(.horizontal, 16)
        .padding(.vertical, 12)
    }

    private func highlightRow(label: String, value: String, color: Color) -> some View {
        HStack {
            Text(label)
                .font(.body)
                .foregroundStyle(.primary)
            Spacer()
            Text(value)
                .font(.body.bold())
                .foregroundStyle(color)
        }
        .padding(.horizontal, 16)
        .padding(.vertical, 12)
    }

    private func positionBadge(_ position: Int) -> some View {
        ZStack {
            Circle()
                .fill(badgeColor(position).opacity(0.15))
                .frame(width: 32, height: 32)
            Text("\(position)")
                .font(.system(size: 13, weight: .bold, design: .rounded))
                .foregroundStyle(badgeColor(position))
        }
    }

    private func badgeColor(_ position: Int) -> Color {
        switch position {
        case 1: return .yellow
        case 2: return Color(.systemGray)
        case 3: return .orange
        default: return .secondary
        }
    }

    private func positionLabel(_ position: Int) -> String {
        switch position {
        case 1: return "1st place"
        case 2: return "2nd place"
        case 3: return "3rd place"
        default: return "\(position)th place"
        }
    }

    private func sectionHeader(_ title: String) -> some View {
        Text(title.uppercased())
            .font(.caption)
            .foregroundStyle(.secondary)
            .padding(.leading, 4)
            .padding(.bottom, 6)
    }
}
