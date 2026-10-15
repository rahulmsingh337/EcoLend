# Google Maps Neighborhood Explorer Implementation Plan

Integrate an interactive Google Maps experience for EcoLend that displays available tools and equipment across the user's neighborhood, incorporating privacy-first fuzzy radius circles, category filters, distance controls, and floating quick-borrow preview cards.

---

## User Review & Confirmed Decisions

> [!IMPORTANT]
> The following product requirements were selected during the interactive design interview:
>
> - **Privacy Protection**: Fuzzy privacy circles (~0.3–0.5 mile radius) representing neighborhood pickup zones, strictly protecting exact homeowner porch addresses.
> - **Marker Interaction**: Tapping any item pin opens a floating bottom preview card with photo, condition, deposit, and a 1-tap "Request Borrow" action.
> - **Map Controls**: Top category filter chips, dynamic search radius distance slider (1–10 miles), and an animated "Recenter to My Location" floating button.

---

## 1. Architecture & Component Structure

```
┌────────────────────────────────────────────────────────┐
│  [📍 Oakridge Woods, Sector 4]     [Active Items: 8]   │
│  [All] [Power Tools] [Lawn & Garden] [Cleaning] [DIY]  │
├────────────────────────────────────────────────────────┤
│  Radius Slider: 3.5 miles (●═══════════)               │
├────────────────────────────────────────────────────────┤
│                                                        │
│                    🟢 [Drill Circle]                   │
│                                                        │
│             🔵 (My Location)                           │
│                                                        │
│                           🟢 [Mower Circle]            │
│                                                        │
│                                        [🎯 Recenter]   │
│                                                        │
├────────────────────────────────────────────────────────┤
│  ┌──────────────────────────────────────────────────┐  │
│  │ 🛠️ DeWalt 20V Cordless Brushless Drill           │  │
│  │ Alex Green · 4.95 ★ · 0.4 mi away · $35 deposit   │  │
│  │ [ View Full Details ]    [ Request Borrow 🤝 ]   │  │
│  └──────────────────────────────────────────────────┘  │
└────────────────────────────────────────────────────────┘
```

### Key Components

1. **`GoogleNeighborhoodMap` (`com.example.ui.components.GoogleNeighborhoodMap.kt`)**:
   - Integrates Google Maps Compose with custom map styling matching EcoLend's emerald/sage theme.
   - Renders fuzzy privacy circles (`Circle`) around equipment drop-off zones with translucent fill (`#2E7D32` with 15% alpha) and eco-green borders, preventing precise home location identification.
   - Renders custom markers (`Marker` / `MarkerComposable`) showing item category icon and availability status.
   - Robust fallback mechanism: If Google Play Services or an external API key is unconfigured, seamlessly displays an interactive vector radar map view so the app runs reliably without crashes in any emulator environment.

2. **Map Controls Overlay**:
   - **Category Filter Chips**: Horizontal scrollable `FilterChip` row filtering map items in real-time.
   - **Distance Radius Slider**: Smooth slider updating `viewModel.maxDistanceMiles` and dynamically adjusting map zoom / camera bounds.
   - **Recenter FAB**: One-tap camera animation returning to user neighborhood center.

3. **Floating Item Preview Card (`MapItemPreviewCard`)**:
   - Slides up with smooth spring animation from the bottom when any marker is clicked.
   - Displays item photo/icon, title, owner name with verified trust badge, deposit amount, and return condition.
   - Actions: "Request Borrow" (gates guests via Neighborhood Trust Pass, then opens deposit authorization) and "View Details".

---

## 2. Dependencies & Permissions

1. **Gradle Dependencies**:
   - `com.google.maps.android:maps-compose`
   - `com.google.android.gms:play-services-maps`
   - `com.google.android.gms:play-services-location`

2. **Manifest Declarations**:
   - `<uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" />`
   - `<uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION" />`
   - `<uses-permission android:name="android.permission.INTERNET" />`
   - `<meta-data android:name="com.google.android.geo.API_KEY" android:value="${MAPS_API_KEY}" />`

---

## 3. Implementation Steps

1. **Configure Dependencies & Manifest**:
   - Add `maps-compose` and `play-services-maps` to `gradle/libs.versions.toml` and `app/build.gradle.kts`.
   - Update `AndroidManifest.xml` with permissions and Maps metadata tag.

2. **Build `GoogleNeighborhoodMap.kt`**:
   - Implement Compose `GoogleMap` container with camera position state centered on neighborhood coordinates (`37.7749, -122.4194` / Oakridge Woods).
   - Calculate deterministic coordinates for each `LendItem` based on `distanceMiles` and bearing.
   - Render `Circle` overlays for fuzzy address privacy.
   - Render interactive markers with selected state highlights.

3. **Enhance `ExploreMapScreen.kt`**:
   - Integrate top search & category bar, radius distance slider, recenter FAB, and floating item preview card.
   - Connect "Request Borrow" to `viewModel.startBorrowFlow(item)` with automatic authentication gating.

4. **Compile & Test**:
   - Run `compile_applet` to verify compilation.
   - Run unit & Robolectric test suite (`gradle :app:testDebugUnitTest`) to ensure zero regressions.
