# 📱 Social Loyalty & Prediction App

> **The First Fully Open-Source Social Prediction Platform for Coiners
> and Non-Coiners.**\
> Open code. Open markets. Open participation.

------------------------------------------------------------------------

## 🚀 Overview

This Android app is part of a fully open-source Social Loyalty and
Prediction ecosystem.

Users can:

-   📲 Register anonymously via QR scan\
-   🪙 Receive initial voting tokens\
-   🗳 Participate in in-store prediction markets\
-   💰 Win tokens based on market outcomes\
-   🔁 Reuse tokens for future markets

The entire infrastructure --- **mobile app + backend** --- is fully open
sourced to ensure transparency, auditability, and community trust.

------------------------------------------------------------------------

## 🔓 Why Open Source?

We believe prediction markets and loyalty systems should be:

-   Transparent\
-   Verifiable\
-   Community-driven\
-   Trust-minimized

All token logic, distribution mechanisms, and resolution flows are
publicly available for review.

No hidden mechanics. No black boxes.

------------------------------------------------------------------------

## 🧠 How the Token Model Works

### 1️⃣ Initial Distribution

-   Users scan a QR code in-store.
-   They receive a one-time initial token allocation.
-   Tokens are stored in their wallet.

### 2️⃣ Voting

-   Users stake tokens on a prediction outcome.
-   Tokens are locked until market resolution.

### 3️⃣ Market Resolution

-   🔹 Winner
    -   Receives their staked tokens back
    -   Receives the tokens from losing participants
-   🔹 Loser
    -   Loses the staked tokens
    -   Remaining unstaked tokens stay in wallet

### Example

  Participant   Initial   Staked   Remaining   After Resolve
  ------------- --------- -------- ----------- ---------------
  Winner        100       60       40          140 total
  Loser         100       100      0           0 total

The winner receives: - 60 tokens (stake returned) - 100 tokens (loser's
stake) = +100 net gain

------------------------------------------------------------------------

## 🛠 Tech Stack (Android)

-   Java
-   Android SDK
-   MVVM Architecture
-   REST API backend
-   Secure token handling
-   Modular SettingsFragment (accessible via top-right gear icon)
-   QR Scanner integration
-   Persistent local storage (Room / SharedPreferences)

------------------------------------------------------------------------

## 🏗 Architecture Overview

The Android client:

-   Handles QR-based onboarding\
-   Manages local wallet state\
-   Communicates with backend via REST API\
-   Displays active markets\
-   Submits staking transactions\
-   Reflects resolved outcomes

The backend:

-   Creates and manages markets\
-   Locks staked tokens\
-   Resolves outcomes\
-   Distributes rewards\
-   Maintains transparent accounting

------------------------------------------------------------------------

## 🏪 Designed for Real-World Use

-   Built for physical store environments\
-   Simple QR onboarding\
-   No prior crypto knowledge required\
-   Accessible for both coiners and non-coiners

------------------------------------------------------------------------

## 🔗 Backend Repository

The backend powering market creation, token accounting, and resolution
logic is also fully open source:

👉 \[Link to Backend Repository\]

------------------------------------------------------------------------

## 📜 Transparency & Fairness

-   All market logic is deterministic\
-   Token flows are auditable\
-   No hidden inflation\
-   No centralized manual manipulation

------------------------------------------------------------------------

## 🌍 Vision

We aim to bridge Web2 retail environments with Web3 incentive logic ---
without forcing users into complex crypto onboarding.

Prediction markets should be:

-   Social\
-   Local\
-   Transparent\
-   Inclusive

------------------------------------------------------------------------

## 🤝 Contributing

We welcome:

-   Code contributions\
-   Security reviews\
-   UX improvements\
-   Market mechanism suggestions

Please open an issue or submit a pull request.

------------------------------------------------------------------------

## 📄 License

This project is licensed under the MIT License (or your chosen license).
