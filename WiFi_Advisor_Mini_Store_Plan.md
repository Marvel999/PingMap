# WiFi Advisor — Mini Store Project Plan (Features Only)

*Screen-by-screen specification and features only. No technical spec. No KMP. For accommodating into a small app.*

---

## Core product promise

- **One question, one answer:** “Which WiFi should I connect to right now?” — answered in plain language within a few seconds.
- **Plain language only:** No jargon (no RSSI, channel numbers, encryption codes) in main UI.
- **Safety first:** Never recommend open or WEP as “safe.”
- **One-tap connect:** User can connect to the recommended network from the app (where platform allows).

**App-wide UX rule — keep current screen for “more details”:**  
Anywhere in the app, when the user taps to see more details (e.g. a network, a history session, a past speed test), show that detail on the **current screen** (e.g. bottom sheet, expandable card, or inline). Do **not** navigate to a separate full screen. The user stays where they are; details overlay or expand in place.

---

## 1. Onboarding (first launch only) — 3 screens

| Screen | Feature / content |
|--------|--------------------|
| **1 — Value** | Message: “WiFi Advisor picks the best WiFi for you.” Illustration (e.g. traveler at airport). Skip + Next. |
| **2 — How it works** | Message: “We check 3 things: Is it safe? Is it fast? Is it crowded?” Simple 3-pillar visual. Color-coded preview. |
| **3 — Permissions** | Message: “To scan WiFi near you, we need Location access.” Plain explanation. Single permission request. Note: data stays on device. |

---

## 2. Home screen — “The Advisor”

**Design rule:** This is the main screen. It must answer “What should I connect to right now?” quickly.

| Area | Feature |
|------|---------|
| **Top bar** | App name, current connection indicator (e.g. green/red dot), scan/refresh action. |
| **Current connection card** (if connected) | Network name (large, bold), score badge, three mini-pills: Safety · Speed · Crowding (plain English), “Connected” indicator. |
| **Best recommendation banner** (if a better network exists) | Prominent card, “BEST CHOICE” badge, network name, one-line reason (e.g. “Fast, secure, and only 1 other network on this channel”), “Connect” primary button. |
| **All networks list** | List of networks: name + colored score badge + one-line summary. Sorted by score (best first). Tap → show network detail on current screen (per app-wide rule). |
| **Bottom nav** | Home · Scan · Speed Test · History · Settings. |

---

## 3. Network detail (shown on current screen)

Used whenever the user taps “more details” for a network (from Home, or anywhere else). Shown on the current screen per app-wide rule (e.g. bottom sheet or expandable card).

| Section | Feature |
|---------|---------|
| **Header** | Network name (large), overall score (e.g. ring/chart), badge (BEST CHOICE / GOOD / AVERAGE / POOR / AVOID). |
| **Safety row** | Lock icon, plain-English security label, expandable “What does this mean?” explainer. |
| **Speed row** | Signal-style icon, estimated speed in plain language, band description (e.g. “Uses 5GHz — better for video calls”). |
| **Crowding row** | People icon, “X other networks sharing this channel,” simple bar comparison. |
| **Actions** | “Connect to this network” (primary), “Not now” (secondary). |
| **Expert details** (optional, off by default) | Raw SSID, BSSID, channel, RSSI, frequency, security string — expandable for power users. |

---

## 4. Speed test screen

- Animated gauge or progress during test.
- Result in plain language (e.g. “Your speed is 66 Mbps — more than enough for HD video calls”).
- Context bar: e.g. “Netflix HD needs 5 Mbps · Zoom needs 3 Mbps · WhatsApp Video needs 1 Mbps.”
- List of last ~10 tests with timestamps. Tap a past test → show its details on the current screen (per app-wide rule), no new screen.
- *Feature:* Store speed result for current network (BSSID) and use it to improve future “Speed” advice for that network.

---

## 5. History screen

- Past scan sessions, grouped by date.
- Each session: location name (e.g. from GPS reverse geocode), number of networks found, top network name, top score badge.
- Tap a session → show session details (e.g. networks in that scan) on the current screen (per app-wide rule), no new screen.
- Use case: “Last time at this cafe the WiFi scored 82, today it scores 44 — something changed.”

---

## 6. Settings screen

| Setting | Default | Description |
|---------|---------|-------------|
| **Auto-scan on open** | ON | Scan automatically when app is opened. |
| **Warn if unsafe network** | ON | Notify if connected network score is below 20 (unsafe). |
| **Show expert details** | OFF | Show raw technical panel on Network Detail. |
| **Save scan history** | ON | Store scan results locally. |
| **Run speed test auto** | OFF | Auto-run speed test after every scan (may use data). |
| **Language** | English | English in v1; more languages later if needed. |

---

## 7. Scoring / recommendation (product behavior only)

- **Score = Safety (40%) + Speed (35%) + Crowding (25%)**, 0–100.
- **Badges:** 80–100 = BEST CHOICE (green), 60–79 = GOOD (blue), 40–59 = AVERAGE (amber), 20–39 = POOR (orange), 0–19 = AVOID (red).
- All scoring and recommendation logic can stay on-device; no technical implementation specified here.

---

## Mini store project plan (feature phases)

Use this to plan a small app release without KMP or heavy technical scope.

| Phase | Scope | Features |
|-------|--------|----------|
| **Phase 1 — Core advisor** | Minimum viable “which WiFi?” | Onboarding (3 screens). Home: scan, recommendation card, all-networks list, bottom nav. Network detail on current screen (3 pillars, connect, expert details). App-wide: more details = on current screen (see rule above). |
| **Phase 2 — Speed & history** | Trust and context | Speed Test screen (gauge, plain-language result, context bar, last 10 tests; tap past test → details on current screen). History screen (sessions by date, location, top network + badge; tap session → details on current screen). |
| **Phase 3 — Polish & control** | UX and safety | Settings: auto-scan, warn unsafe, expert details, save history, auto speed test, language. One-tap connect from Home/detail where supported. Unsafe-network notification. |

---

*Source: Screen-by-Screen Specification and feature content from WiFi_Advisor_HLD_Spec_KMP.md — technical spec and KMP sections omitted.*
