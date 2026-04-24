# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Build Commands

```bash
./gradlew assembleDebug          # Build debug APK
./gradlew build                  # Full build (debug + release)
./gradlew test                   # Run unit tests
./gradlew connectedAndroidTest   # Run instrumented tests (requires device/emulator)
./gradlew testDebugUnitTest --tests "com.sgf.poker.SomeTest"  # Run a single test class
```

**Environment**: Java 17, Kotlin 2.2.10, AGP 9.1.1, compileSdk 36, minSdk 26.

## Architecture

Clean Architecture with MVVM, implemented entirely in Java (except `MainActivity.kt`).

**Layers** (inner → outer):

1. **Domain** (`domain/model/`, `domain/error/`) — Pure Java POJOs, no Android dependencies. Models are immutable; mutations return new instances (e.g., `game.withUpdatedPlayer()`).

2. **Use Cases** (`usecases/`) — One class per operation. Each takes repository interfaces as constructor arguments and exposes a single `execute()` method. No Android dependencies.

3. **Data** (`data/repository/`) — Repository interfaces in `domain/`-adjacent packages; `LocalStorage*` implementations persist JSON to `context.getFilesDir()` via GSON. File I/O methods are `synchronized`. A custom `LocalDateAdapter` handles `java.time.LocalDate` serialization.

4. **UI** (`ui/`) — AndroidX Fragments + `AndroidViewModel`s wired via `ViewModelProvider`. State exposed as `MutableLiveData`. Navigation handled by Android Navigation Component (SafeArgs). Manual dependency wiring inside ViewModels (`new FooUseCase(new LocalStorageRepo(getApplication()))`).

**Screens** (nav_graph): Games list → Game detail → Prizes; Players; Settings (drawer).

## Domain Model Summary

- **Game**: A poker event (date, UUID, list of `GamePlayer`s).
- **Player**: League member (name, UUID, membership/founder status, points).
- **GamePlayer**: Join between `Game` and `Player`; tracks RSVP, attendance, buy-in count, rebuy count, final ranking.
- **PrizeRules**: Fixed constants — entry fee $25 member / $30 non-member, rebuy $25, payout tiers.
- **PrizeCalculation / PlayerPrize**: Output of `CalculatePrizesUseCase`.

## Key Business Rules

- One game per calendar month/year (enforced in `CreateGameUseCase`).
- Prize pool = Σ entry fees + Σ rebuy fees; split 80% prize / 20% bounty across top finishers.
- Game lifecycle: `coming` → player marked present → rankings assigned → `finished`.

## Testing

Unit tests use JUnit 5 (Jupiter) + Mockito. Instrumented tests use Espresso. Only placeholder tests currently exist — real tests are still needed.

## Dependency Injection

There is no DI framework. Repositories are instantiated directly inside ViewModels. When adding new use cases or repositories, follow the existing manual wiring pattern.