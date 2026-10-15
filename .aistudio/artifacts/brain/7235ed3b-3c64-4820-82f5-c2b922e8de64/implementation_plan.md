# Authentication System (Google Sign-In & Mobile Phone OTP)

A secure authentication architecture for EcoLend that integrates **Google Sign-In via Jetpack Credential Manager** backed by Firebase Authentication and a **Mobile Phone OTP Verification flow** with SMS code simulation. Users can freely browse community equipment in guest mode, with authentication smoothly gating actions that require trust (borrowing tools, listing equipment, sending messages, setting availability alerts).

## User Review & Critical Decisions

> [!IMPORTANT]
> The following user preferences were confirmed during the interactive interview:
>
> - **Confirmed Sign-In Methods**:
>   - **Google Sign-In** via Jetpack `CredentialManager` (`GetSignInWithGoogleOption`)
>   - **Mobile Phone OTP** (enter mobile phone number, receive 6-digit OTP code, verify and authenticate)
> - **Confirmed Access Gating**:
>   - **Guest Browsing Allowed**: Visitors can explore the equipment feed, view radar map, and read reviews freely.
>   - **Action-Gated Authentication**: Tapping "Borrow", "Add Listing", "Chat with Lender", or managing personal alerts prompts a stylish Auth Gate modal / screen.
> - **Confirmed Identity & Reputation Linking**:
>   - Logged-in credentials link directly with the user's `UserProfile`, preserving accumulated Karma Score (+420), diversion metrics (42.6 kg waste saved), and verified badge flairs.

---

## 1. Overview & Core Concept

- **What It Does**: Provides neighborhood accountability and secure identity verification. Neighbors can authenticate using their Google account or verify their mobile phone number with a 6-digit OTP.
- **Why Both**: Google Sign-In gives 1-tap simplicity, while phone verification establishes verified local trust for porch pickups and tool custody.
- **Guest Mode Support**: Lowers friction by letting users discover neighborhood tools before committing to sign-in.

---

## 2. User Experience & Visual Design

### Key User Flows

1. **Sign-In / Auth Dialog & Screen (`AuthGateSheet` / `SignInDialog`)**
   - Triggered either manually from the top app bar / Profile screen or when a guest tries to borrow or list an item.
   - **Tab 1: Google Sign-In (Recommended)**:
     - Prominent Google button with standard branding: *"Continue with Google"*.
     - One-tap sign in through Jetpack Credential Manager.
   - **Tab 2: Mobile Phone OTP**:
     - Step 1: Input country code (+1) and 10-digit mobile phone number.
     - Step 2: Tap *"Send Verification Code"*. A realistic OTP transmission trigger dispatches a 6-digit verification code with a toast / push preview.
     - Step 3: Enter 6-digit OTP with auto-focus inputs, resend timer (30s), and *"Verify & Sign In"* button.

2. **Action Gating Flow**
   - When a guest taps **"Borrow"** on an item card or detail screen:
     - Auth prompt appears: *"Sign in to complete your borrow request & authorize deposit hold"*.
     - Upon successful authentication, automatically proceeds directly to the Payment / Borrow sheet.
   - When a guest taps **"Add Listing"**:
     - Prompts sign-in to associate the tool listing with their verified neighborhood profile.

3. **Profile & Account Management**
   - Profile screen displays authenticated status:
     - Connected Google account email / verified mobile phone badge.
     - One-tap **"Sign Out"** or **"Switch Account"** action.
     - Verified Trust badge reflecting the authenticated state.

---

## 3. Technical Architecture & Implementation Steps

### Phase 1: Firebase Backend Provisioning (MANDATORY Handshake)
- Run `show_aistudio_ui` with `ui_type: "firebase_setup"` and `requested_products: ["firestore"]`.
- Halt turn and await user consent in the platform UI.
- Verify terms via `GetFirebaseTos` RPC, then invoke `ProvisionFirebase` with:
  - `package_name`: `com.aistudio.ecolend.npxjkm`
  - `sha1_certificate`: `C2:D8:74:E7:83:5C:C8:7C:63:E2:79:2F:63:1A:C3:11:72:61:23:BB`
  - `sha256_certificate`: `6A:1A:56:49:46:1B:06:A3:8A:69:2D:15:D8:0D:0A:77:2F:70:52:B1:BF:20:68:FE:2C:3B:94:40:2A:0C:19:A2`
  - `requested_products`: `["firestore"]`

### Phase 2: Dependencies & Authentication Layer
- Enable required dependencies in `app/build.gradle.kts`:
  - `libs.firebase.auth`
  - `libs.androidx.credentials`
  - `libs.androidx.credentials.play.services`
  - `libs.googleid`
- Implement `AuthRepository` & state in `EcoLendRepository`:
  - `AuthState`: `Unauthenticated` / `Guest`, `Authenticated(userId, email, phone, displayName, provider)`
  - Google Sign-In helper using `CredentialManager.getCredential()` and `GetSignInWithGoogleOption`
  - Phone OTP verification manager (phone input, OTP generation, validation)

### Phase 3: Auth UI & Gating Components
- Create `AuthenticationDialog.kt`:
  - Tabbed interface: **"Google Sign-In"** & **"Mobile Phone OTP"**
  - Phone number formatter and 6-digit OTP code input boxes
  - Visual security badges: *"256-bit encrypted community identity"*
- Add Auth Gate interceptor in `EcoLendViewModel`:
  - `isAuthenticated: StateFlow<Boolean>`
  - `currentUserAccount: StateFlow<UserAccount?>`
  - `requireAuth(action: () -> Unit)` to trigger sign-in dialog if unauthenticated
- Update TopAppBar and Profile screen with login/logout status and avatar badge.

### Phase 4: Compilation & Verification
- Execute `compile_applet` to verify compilation and dependency resolution.
