// Presentation/ViewModels/PrizesViewModel.swift

import Foundation
import Observation

@Observable
final class PrizesViewModel {

    // MARK: – State
    var calculation: PrizeCalculation?

    private let game: Game
    private let players: [Player]
    private let calculatePrizes = CalculatePrizesUseCase()

    init(game: Game, players: [Player]) {
        self.game    = game
        self.players = players
    }

    // MARK: – Lifecycle

    func onAppear() {
        recalculate()
    }

    // MARK: – Intents

    func recalculate() {
        calculation = calculatePrizes.execute(game: game, players: players)
    }

    // MARK: – Formatting helpers

    func formatted(_ amount: Double) -> String {
        let f = NumberFormatter()
        f.numberStyle = .currency
        f.currencySymbol = "$"
        f.maximumFractionDigits = 2
        return f.string(from: NSNumber(value: amount)) ?? "$\(amount)"
    }

    func percent(_ value: Double) -> String {
        "\(Int(value * 100))%"
    }
}
