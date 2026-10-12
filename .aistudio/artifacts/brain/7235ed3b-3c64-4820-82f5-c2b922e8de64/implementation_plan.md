# Tool Pickup & Drop-Off Location Input Component

A dedicated location selection component enabling tool owners to designate pickup and return points via an interactive radar map pin, address autocomplete search, privacy-preserving fuzzy radius, and curated neighborhood community hubs.

## User Review & Critical Decisions

> [!IMPORTANT]
> The following user preferences were confirmed during the clarification interview:

- **Confirmed Interaction Mode**: Interactive pin on a neighborhood radar map paired with instant address and landmark autocomplete search.
- **Confirmed Address Privacy**: Approximate fuzzy zone (~0.25 mi radius) shown to borrowers on public listings until the owner accepts the borrow request.
- **Confirmed Hub Presets**: Curated community hubs including Community Gardens, Neighborhood Tool Sheds, Library lockers, and Porch Lockboxes alongside custom residential addresses.

---

## 1. Overview & Core Concept

- **What It Does**: Provides an intuitive location picker for tool owners to specify convenient, secure pickup and return points. Users can type an address, pick a community hub, or tap directly on an interactive map to move the location pin.
- **Target Audience / Persona**: Neighborhood lenders who want flexible pickup options (e.g. contactless porch pickup or trusted community spaces) while safeguarding their residential privacy.
- **Key Value**: Clear logistics for borrowers, zero unwanted door visits via fuzzy masking, and support for neutral neighborhood exchange hubs.

---

## 2. User Experience & Visual Design

### Key User Flows

1. **Selecting Location in Item Form (Step 3: Availability & Logistics)**
   - Displays an **Exchange & Pickup Point** card showing the currently selected location (defaulting to the lender's home neighborhood with fuzzy masking).
   - "Change Location or Hub" opens the full interactive location picker dialog/sheet.
2. **Interacting with Map & Search (`ToolPickupLocationPicker`)**
   - **Address & Hub Autocomplete Search Bar**: Type to filter residential street addresses or select curated hubs (Maplewood Community Garden, Westside Tool Shed, Porch Lockbox, Eco Café Hub).
   - **Interactive Radar Map Pin**: High-fidelity Canvas radar map with distance rings (0.2 mi, 0.5 mi, 1.0 mi) and a draggable/tappable pin. Tapping any position updates coordinates and neighborhood distance.
   - **Fuzzy Privacy Zone Toggle**: Visualizes a glowing green privacy disc around the pin, confirming to the owner that their exact house number remains hidden until booking confirmation.
   - **Pickup Instructions Note**: Quick input for exchange instructions (e.g. "Porch bench pickup", "Ask for locker #3 at garden shed").
3. **Reviewing on Step 4 (Live Preview Card)**
   - The live feed preview card displays the selected pickup point and privacy status:
     - `📍 Maplewood Heights (Fuzzy Zone ~0.2 mi) • Porch Lockbox`
4. **Publishing & Data Persistence**
   - The chosen pickup point name, address, latitude offset, and fuzzy privacy flag are persisted with the tool in the Room database.

### Visual Identity & Theme
- **Theme & Colors**: Deep forest green (`#1B5E20`), vibrant radar accents (`#2E7D32`), soft glowing privacy rings (`#81C784` with 25% opacity), and amber badges for secure lockboxes.
- **Map Aesthetics**: Clean circular radar sweeps, concentric distance markers, active coordinate crosshairs, and animated pin drops.

---

## 3. Key Product Decisions & Trade-Offs

- **Decision 1: Custom Canvas Radar Map with Direct Tap-to-Pin**
  - *Chosen Approach*: Build on the project's existing custom Compose Canvas radar architecture (`detectTapGestures`) rather than introducing heavy external map SDK dependencies that require proprietary API keys.
  - *Why*: Fast, zero-API-key overhead, responsive on all devices, completely offline-compatible, and tightly integrated into the app's eco theme.
- **Decision 2: Hybrid Search (Street Addresses + Community Hubs)**
  - *Chosen Approach*: Autocomplete search combines neighborhood streets with recognized public exchange hubs.
  - *Why*: Allows both residential porch lenders and community volunteers sharing tools at community gardens to select their pickup point with one tap.
- **Decision 3: Embedded Pickup Card with Interactive Modal Picker**
  - *Chosen Approach*: Keep Step 3 clean with a compact summary card while providing a full-screen/dialog map picker when the user chooses to customize.
  - *Why*: Keeps the form fast for default users while providing an immersive map experience for users wanting custom locations.

---

## 4. Technical Architecture & Data Strategy

### Component Hierarchy Diagram

```
┌─────────────────────────────────────────────────────────────┐
│                 AddItemFormComponent (Step 3)               │
│   ┌─────────────────────────────────────────────────────┐   │
│   │              Pickup Location Summary Card           │   │
│   │   [📍 Maplewood Community Garden (Locker #4)]       │   │
│   │   [Shield: Approximate Fuzzy Radius Protected]      │   │
│   │   [Button: "Customize Location on Map"]             │   │
│   └──────────────────────────┬──────────────────────────┘   │
└──────────────────────────────┼──────────────────────────────┘
                               │ (Tap customize)
                               ▼
┌─────────────────────────────────────────────────────────────┐
│              ToolPickupLocationPickerDialog                 │
│   ┌─────────────────────────────────────────────────────┐   │
│   │ Address & Hub Autocomplete Search Bar               │   │
│   │   [Search query: "Garden" -> Autocomplete list]    │   │
│   └─────────────────────────────────────────────────────┘   │
│   ┌─────────────────────────────────────────────────────┐   │
│   │ Interactive Radar Map Canvas                        │   │
│   │   - Concentric distance rings (0.2 mi, 0.5 mi, 1 mi)│   │
│   │   - Tap-to-move pin & crosshair                     │   │
│   │   - Glowing Fuzzy Privacy Circle (~0.25 mi radius)  │   │
│   └─────────────────────────────────────────────────────┘   │
│   ┌─────────────────────────────────────────────────────┐   │
│   │ Quick Community Hub Selector Chips                  │   │
│   │   [Community Garden] [Tool Shed] [Porch Lockbox]    │   │
│   └─────────────────────────────────────────────────────┘   │
│   ┌─────────────────────────────────────────────────────┐   │
│   │ Privacy Switch & Special Pickup Notes Input         │   │
│   │   [✓ Mask exact address until confirmed]            │   │
│   └─────────────────────────────────────────────────────┘   │
│   ┌─────────────────────────────────────────────────────┐   │
│   │ Actions: [Cancel]  [Confirm Pickup Point]           │   │
│   └─────────────────────────────────────────────────────┘   │
└──────────────────────────────┬──────────────────────────────┘
                               │ (Confirm location)
                               ▼
┌─────────────────────────────────────────────────────────────┐
│                      EcoLendViewModel                       │
│    addNewCommunityItem(..., pickupLocation, pickupAddress)  │
└──────────────────────────────┬──────────────────────────────┘
                               │
                               ▼
┌─────────────────────────────────────────────────────────────┐
│                 LendItem & Room Database                    │
│   (neighborhood, distanceMiles, pickupLocationName, etc.)   │
└─────────────────────────────────────────────────────────────┘
```

### Data Model & State Mapping
- `pickupLocationName: String` (e.g. "Maplewood Community Garden", "Porch Lockbox")
- `pickupAddress: String` (e.g. "452 Maple Street")
- `pickupDistanceMiles: Double` (Calculated distance from neighborhood center)
- `isFuzzyPrivacyEnabled: Boolean` (Default `true`)
- `pickupInstructions: String` (e.g. "Locker box on porch side; code provided upon approval")
