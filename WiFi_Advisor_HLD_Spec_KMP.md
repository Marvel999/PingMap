**WiFi Advisor**

*Smart WiFi Selection for Everyone*

**High-Level Design & Complete Product Specification**

**Platform: Android (Kotlin) + iOS via Kotlin Multiplatform (KMP)**

Built with Cursor \| Version 2.0 \| March 2026

**1. Executive Summary**

WiFi Advisor is a mobile application built in Kotlin for Android
(primary platform) and shared via Kotlin Multiplatform (KMP) for iOS. It
removes all technical guesswork from choosing a WiFi network. Instead of
raw signal percentages, channel numbers, and encryption codes, it gives
every user --- from tech experts to first-time smartphone users --- a
single clear recommendation: which WiFi to connect to, and exactly why.

+-----------------------------------------------------------------------+
| **Core Promise**                                                      |
|                                                                       |
| Walk into any cafe, airport, hotel, or office. Open the app. See one  |
| green card: \"Connect to This Network --- It is Fast, Safe, and       |
| Uncrowded.\" No tech knowledge required.                              |
+-----------------------------------------------------------------------+

**Platform Strategy**

  -----------------------------------------------------------------------
  **Platform**    **Tech**                    **Status**
  --------------- --------------------------- ---------------------------
  Android         Kotlin + Jetpack Compose +  Full feature set ---
                  Android WiFi APIs           primary platform

  iOS             KMP shared logic + SwiftUI  Shared business logic;
                  native UI                   native UI layer in Swift

  Shared (KMP)    Scoring Engine, Data        100% shared --- write once,
                  Models, Repository, DB      run on both
  -----------------------------------------------------------------------

**2. Problem Statement**

**2.1 The Real-World Pain**

A non-technical traveler at a busy airport sees 15 WiFi networks and
faces three invisible risks they have no way to evaluate without deep
tech knowledge:

  ------------------------------------------------------------------------
  **Risk**     **What Happens**              **Consequence**
  ------------ ----------------------------- -----------------------------
  Security     Connecting to a rogue hotspot Passwords and banking data
               named \"Airport Free WiFi\"   stolen via MITM attack

  Speed        Picking a 2.4GHz overloaded   Videos buffer, video calls
               channel with 8 other networks drop, work is lost

  Congestion   All nearby networks share     Constant packet loss, high
               channel 1 or 13               latency, frustration
  ------------------------------------------------------------------------

**2.2 Why Existing Apps Fail Non-IT Users**

-   Display raw metrics (RSSI dBm, channel numbers, frequency bands)
    with no interpretation

-   Require users to know what WPA2 vs WPA3 vs WEP means in practice

-   No personalized recommendation --- just a list sorted by signal bar
    count

-   Designed for network engineers, not everyday professionals who
    travel for work

**3. Product Goals & Success Metrics**

**3.1 Goals**

  --------------------- -------------------------------------------------
  **Goal**              **Definition**

  Instant               Best network identified within 3 seconds of
  Recommendation        opening the app

  Plain Language Only   Zero technical jargon in the main UI --- all
                        explanations in everyday English

  Safety First          App never recommends an open or WEP-secured
                        network as safe

  One-Tap Connect       User connects to recommended network without
                        leaving the app (Android)

  Offline Scoring       All scanning and scoring runs 100% on-device ---
                        no internet required to advise
  --------------------- -------------------------------------------------

**3.2 KPIs**

  ------------------------------------------------------------------------
  **Metric**                     **3-Month Target**   **12-Month Target**
  ------------------------------ -------------------- --------------------
  Daily Active Users             5,000                50,000

  Recommendation Acceptance Rate \> 70%               \> 80%

  Google Play Rating             \> 4.2 stars         \> 4.5 stars

  Time to First Connection       \< 30 seconds        \< 20 seconds
  Decision                                            

  Crash-Free Sessions            \> 99%               \> 99.5%
  ------------------------------------------------------------------------

**4. Architecture --- KMP + Android**

**4.1 High-Level Architecture Diagram (Text)**

+-----------------------------------+-----------------------------------+
| **ANDROID**                       | **iOS**                           |
|                                   |                                   |
| Jetpack Compose UI                | SwiftUI Native UI                 |
|                                   |                                   |
| Android ViewModels                | Swift ViewModels (KMP-linked)     |
|                                   |                                   |
| WifiManager / WifiInfo APIs       | Core Location / NetworkExtension  |
+-----------------------------------+-----------------------------------+
| **SHARED --- Kotlin Multiplatform |                                   |
| (KMP)**                           |                                   |
|                                   |                                   |
| Scoring Engine \| Recommendation  |                                   |
| Engine \| Data Models \|          |                                   |
| Repository Layer \| SQLDelight DB |                                   |
+-----------------------------------+-----------------------------------+

**4.2 Module Breakdown**

  -----------------------------------------------------------------------------
  **Module**          **Platform**   **Responsibility**
  ------------------- -------------- ------------------------------------------
  shared/scoring      KMP            ScoringEngine.kt --- pure Kotlin, no
                                     Android dependency

  shared/models       KMP            WiFiNetwork data class, ScanResult,
                                     Recommendation

  shared/repository   KMP            WiFiRepository interface --- expect/actual
                                     pattern for scanning

  shared/db           KMP            SQLDelight schema for scan history, cached
                                     scores

  androidApp/wifi     Android        WifiManager scanner implementation
                                     (actual), permissions

  androidApp/ui       Android        Jetpack Compose screens, ViewModels,
                                     Navigation

  iosApp/wifi         iOS            Core Location / NetworkExtension (actual),
                                     Swift wrapper

  iosApp/ui           iOS            SwiftUI screens consuming shared KMP
                                     ViewModels via \@ObservableObject
  -----------------------------------------------------------------------------

**4.3 KMP expect/actual Pattern for WiFi Scanning**

WiFi APIs are completely different between Android and iOS. KMP\'s
expect/actual pattern solves this cleanly:

+-----------------------------------------------------------------------+
| // shared/src/commonMain/kotlin/WiFiScanner.kt                        |
|                                                                       |
| expect class WiFiScanner {                                            |
|                                                                       |
| fun scanNetworks(): List\<WiFiNetwork\>                               |
|                                                                       |
| fun getCurrentNetwork(): WiFiNetwork?                                 |
|                                                                       |
| }                                                                     |
|                                                                       |
| // androidApp/src/main/kotlin/WiFiScannerImpl.kt (actual)             |
|                                                                       |
| actual class WiFiScanner(private val context: Context) {              |
|                                                                       |
| actual fun scanNetworks(): List\<WiFiNetwork\> {                      |
|                                                                       |
| val wifiManager = context.getSystemService(WifiManager::class.java)   |
|                                                                       |
| return wifiManager.scanResults.map { it.toWiFiNetwork() }             |
|                                                                       |
| }                                                                     |
|                                                                       |
| actual fun getCurrentNetwork(): WiFiNetwork? {                        |
|                                                                       |
| val wifiInfo = wifiManager.connectionInfo                             |
|                                                                       |
| return wifiInfo.toWiFiNetwork()                                       |
|                                                                       |
| }                                                                     |
|                                                                       |
| }                                                                     |
|                                                                       |
| // iosApp/src/iosMain/kotlin/WiFiScannerImpl.kt (actual)              |
|                                                                       |
| actual class WiFiScanner {                                            |
|                                                                       |
| actual fun scanNetworks(): List\<WiFiNetwork\> {                      |
|                                                                       |
| // iOS: limited to current network via NEHotspotNetwork               |
|                                                                       |
| return listOf(getCurrentNetwork()).filterNotNull()                    |
|                                                                       |
| }                                                                     |
|                                                                       |
| actual fun getCurrentNetwork(): WiFiNetwork? {                        |
|                                                                       |
| // Calls Swift interop via CInterop to read SSID                      |
|                                                                       |
| return IOSWiFiHelper.getCurrentNetwork()                              |
|                                                                       |
| }                                                                     |
|                                                                       |
| }                                                                     |
+-----------------------------------------------------------------------+

**5. Scoring Engine --- The Core Intelligence**

The ScoringEngine lives entirely in the KMP shared module. It has zero
Android or iOS dependencies --- pure Kotlin. This means it can be
unit-tested on any machine with no emulator needed.

+-----------------------------------------------------------------------+
| **Scoring Formula**                                                   |
|                                                                       |
| WiFi Score = (Security Score × 0.40) + (Speed Score × 0.35) +         |
| (Congestion Score × 0.25) → Result 0--100                             |
+-----------------------------------------------------------------------+

**5.1 Security Score (40% weight)**

  -------------------------------------------------------------------------
  **Security Type**     **Score**   **Plain English     **User Message**
                                    Label**             
  --------------------- ----------- ------------------- -------------------
  WPA3                  100         Very Secure         Bank-level
                                                        protection

  WPA2-Enterprise       90          Very Secure         Corporate-grade
  (802.1X)                                              protection

  WPA2-Personal         70          Secure              Standard
  (AES/CCMP)                                            home/office
                                                        protection

  WPA/WPA2 Mixed        50          Okay                Not the safest ---
                                                        use with care

  WPA (TKIP only)       30          Weak                Older encryption
                                                        --- can be hacked

  WEP                   10          Dangerous           Anyone nearby can
                                                        steal your data

  Open (no password)    0           Unsafe              No protection ---
                                                        all data is visible
  -------------------------------------------------------------------------

*Android API mapping: ScanResult.capabilities string is parsed to detect
\[WPA3\], \[WPA2\], \[WPA\], \[WEP\], or empty (open). Kotlin
when-expression handles all cases.*

**5.2 Speed Score (35% weight)**

Speed is predicted from signal level (RSSI dBm), frequency band, and
channel width. If a live speed test has been run on this BSSID, that
result overrides the prediction.

  -------------------------------------------------------------------------
  **Signal Condition**     **Score**   **Label**         **User Message**
  ------------------------ ----------- ----------------- ------------------
  5 GHz, RSSI \> -55 dBm   100         Very Fast         Great for HD video
                                                         calls and
                                                         downloads

  5 GHz, RSSI -55 to -67   80          Fast              Good for most work
  dBm                                                    tasks

  5 GHz, RSSI -67 to -75   60          Okay              May slow on heavy
  dBm                                                    uploads

  2.4 GHz, RSSI \> -60 dBm 55          Moderate          Fine for email and
                                                         light browsing

  2.4 GHz, RSSI -60 to -70 35          Slow              Video calls will
  dBm                                                    struggle

  Any band, RSSI \< -75    10          Very Slow         Too far from the
  dBm                                                    router
  -------------------------------------------------------------------------

*Android API: ScanResult.level (RSSI in dBm), ScanResult.frequency (MHz
--- below 3000 = 2.4GHz, above = 5GHz), ScanResult.channelWidth.*

**5.3 Congestion Score (25% weight)**

Counts how many other detected networks share the same WiFi channel.
Channels 1, 6, 11 (2.4GHz) and 36, 40, 44, 48, 149, 153, 157, 161 (5GHz)
are compared.

  -------------------------------------------------------------------------
  **Networks on Same    **Score**   **Label**         **User Message**
  Channel**                                           
  --------------------- ----------- ----------------- ---------------------
  1 --- only this       100         Uncrowded         You have this channel
  network                                             to yourself

  2 networks            80          Lightly Used      Very little
                                                      competition for
                                                      bandwidth

  3 networks            60          Some Competition  Still usable for most
                                                      work

  4 networks            40          Crowded           Expect occasional
                                                      slowdowns

  5+ networks           10          Very Crowded      Avoid if a better
                                                      option exists
  -------------------------------------------------------------------------

**5.4 Final Recommendation Badges**

  -------------------------------------------------------------------------
  **Score**    **Badge**       **Color**        **Recommendation Text**
  ------------ --------------- ---------------- ---------------------------
  80--100      BEST CHOICE     Green            Connect to this --- Fast,
                                                safe, and uncrowded

  60--79       GOOD            Blue             Solid option --- Should
                                                work well for most tasks

  40--59       AVERAGE         Amber            Usable but not ideal ---
                                                connect only if nothing
                                                better

  20--39       POOR            Orange           Likely slow or unreliable
                                                --- use with caution

  0--19        AVOID           Red              Do not connect --- Unsafe
                                                or extremely slow
  -------------------------------------------------------------------------

+-----------------------------------------------------------------------+
| // shared/src/commonMain/kotlin/ScoringEngine.kt                      |
|                                                                       |
| data class WiFiScore(                                                 |
|                                                                       |
| val total: Int,                                                       |
|                                                                       |
| val securityScore: Int,                                               |
|                                                                       |
| val speedScore: Int,                                                  |
|                                                                       |
| val congestionScore: Int,                                             |
|                                                                       |
| val badge: Badge,                                                     |
|                                                                       |
| val headline: String, // \"Fast, Safe, Uncrowded\"                    |
|                                                                       |
| val securityLabel: String, // \"Secure\"                              |
|                                                                       |
| val speedLabel: String, // \"Fast\"                                   |
|                                                                       |
| val congestionLabel: String // \"Uncrowded\"                          |
|                                                                       |
| )                                                                     |
|                                                                       |
| enum class Badge { BEST_CHOICE, GOOD, AVERAGE, POOR, AVOID }          |
|                                                                       |
| class ScoringEngine {                                                 |
|                                                                       |
| fun score(network: WiFiNetwork, allNetworks: List\<WiFiNetwork\>):    |
| WiFiScore {                                                           |
|                                                                       |
| val security = scoreSecure(network.capabilities)                      |
|                                                                       |
| val speed = scoreSpeed(network.rssi, network.frequency)               |
|                                                                       |
| val congestion = scoreCongestion(network.channel, allNetworks)        |
|                                                                       |
| val total = (security \* 0.40 + speed \* 0.35 + congestion \*         |
| 0.25).toInt()                                                         |
|                                                                       |
| return WiFiScore(total, security, speed, congestion,                  |
|                                                                       |
| badge = toBadge(total), headline = buildHeadline(security, speed,     |
| congestion),                                                          |
|                                                                       |
| securityLabel = securityLabel(security),                              |
|                                                                       |
| speedLabel = speedLabel(speed),                                       |
|                                                                       |
| congestionLabel = congestionLabel(congestion))                        |
|                                                                       |
| }                                                                     |
|                                                                       |
| }                                                                     |
+-----------------------------------------------------------------------+

**6. Full Technology Stack**

**6.1 Android Stack**

  -------------------------------------------------------------------------
  **Category**       **Library / Tool**      **Purpose**
  ------------------ ----------------------- ------------------------------
  Language           Kotlin 2.0              Primary language for all
                                             Android code

  UI                 Jetpack Compose +       Declarative UI, theming, dark
                     Material 3              mode

  Architecture       MVVM + Clean            ViewModel, UseCase, Repository
                     Architecture            pattern

  DI                 Hilt                    Dependency injection,
                                             ViewModel injection

  Navigation         Navigation Compose      Type-safe screen routing

  Async              Kotlin Coroutines +     WiFi scans, speed tests, DB
                     Flow                    queries as flows

  WiFi APIs          WifiManager, WifiInfo,  Network list, signal, channel,
                     ScanResult              security, BSSID

  Charts             Vico (compose-charts)   Score ring, channel bar chart,
                                             speed gauge

  Connect            WifiNetworkSuggestion   One-tap WiFi connection flow
                     API (Android 10+)       in-app
  -------------------------------------------------------------------------

**6.2 KMP Shared Module Stack**

  --------------------------------------------------------------------------
  **Category**       **Library / Tool**       **Purpose**
  ------------------ ------------------------ ------------------------------
  Language           Kotlin Multiplatform 2.0 Shared logic targets JVM
                                              (Android) + Native (iOS)

  Database           SQLDelight 2.x           Type-safe SQL, generates
                                              Kotlin DAOs, KMP-compatible

  Networking         Ktor Client              Optional: speed benchmark API,
                                              threat DB sync

  Serialization      kotlinx.serialization    Network models, local cache,
                                              config JSON

  Settings/Prefs     multiplatform-settings   User preferences (shared
                                              across platforms)

  Datetime           kotlinx-datetime         Scan timestamps, history
                                              grouping

  Testing            kotlin.test + Turbine    Unit tests for scoring engine,
                                              flow testing
  --------------------------------------------------------------------------

**6.3 iOS Stack**

  ------------------------------------------------------------------------
  **Category**       **Library / Tool**     **Purpose**
  ------------------ ---------------------- ------------------------------
  Language           Swift 5.9 + KMP        UI in Swift, business logic
                     framework              from KMP .xcframework

  UI                 SwiftUI                Native iOS screens using KMP
                                            ViewModels as
                                            \@ObservableObject

  WiFi Scanning      Core Location +        Current SSID, signal (limited
                     NEHotspotNetwork       by Apple APIs)

  KMP Integration    cocoapods-kotlin / SPM Embed shared KMP module in
                     .xcframework           Xcode project
  ------------------------------------------------------------------------

**7. Project Folder Structure**

+-----------------------------------------------------------------------+
| wifi-advisor/                                                         |
|                                                                       |
| ├── shared/ ← KMP module (shared across platforms)                    |
|                                                                       |
| │ ├── src/                                                            |
|                                                                       |
| │ │ ├── commonMain/kotlin/                                            |
|                                                                       |
| │ │ │ ├── model/                                                      |
|                                                                       |
| │ │ │ │ ├── WiFiNetwork.kt ← Data class: ssid, bssid, rssi, freq,     |
| channel, security                                                     |
|                                                                       |
| │ │ │ │ ├── WiFiScore.kt ← Score result with labels + badge           |
|                                                                       |
| │ │ │ │ └── ScanHistory.kt ← Historical scan entry                    |
|                                                                       |
| │ │ │ ├── engine/                                                     |
|                                                                       |
| │ │ │ │ ├── ScoringEngine.kt ← Pure logic, no platform dependency     |
|                                                                       |
| │ │ │ │ └── RecommendationEngine.kt ← Picks best, builds              |
| plain-English text                                                    |
|                                                                       |
| │ │ │ ├── repository/                                                 |
|                                                                       |
| │ │ │ │ ├── WiFiRepository.kt ← Interface (expect/actual for          |
| scanning)                                                             |
|                                                                       |
| │ │ │ │ └── HistoryRepository.kt ← SQLDelight history ops             |
|                                                                       |
| │ │ │ └── db/                                                         |
|                                                                       |
| │ │ │ └── WifiAdvisor.sq ← SQLDelight schema                          |
|                                                                       |
| │ │ ├── androidMain/kotlin/                                           |
|                                                                       |
| │ │ │ └── repository/WiFiRepositoryAndroid.kt ← actual impl using     |
| WifiManager                                                           |
|                                                                       |
| │ │ └── iosMain/kotlin/                                               |
|                                                                       |
| │ │ └── repository/WiFiRepositoryIOS.kt ← actual impl using CInterop  |
|                                                                       |
| │ └── build.gradle.kts                                                |
|                                                                       |
| │                                                                     |
|                                                                       |
| ├── androidApp/ ← Android-only code                                   |
|                                                                       |
| │ ├── src/main/kotlin/                                                |
|                                                                       |
| │ │ ├── ui/                                                           |
|                                                                       |
| │ │ │ ├── home/                                                       |
|                                                                       |
| │ │ │ │ ├── HomeScreen.kt ← Compose: recommendation card + network    |
| list                                                                  |
|                                                                       |
| │ │ │ │ └── HomeViewModel.kt ← Hilt ViewModel                         |
|                                                                       |
| │ │ │ ├── detail/                                                     |
|                                                                       |
| │ │ │ │ ├── NetworkDetailScreen.kt                                    |
|                                                                       |
| │ │ │ │ └── NetworkDetailViewModel.kt                                 |
|                                                                       |
| │ │ │ ├── speedtest/                                                  |
|                                                                       |
| │ │ │ │ ├── SpeedTestScreen.kt                                        |
|                                                                       |
| │ │ │ │ └── SpeedTestViewModel.kt                                     |
|                                                                       |
| │ │ │ ├── history/HistoryScreen.kt                                    |
|                                                                       |
| │ │ │ └── settings/SettingsScreen.kt                                  |
|                                                                       |
| │ │ ├── di/AppModule.kt ← Hilt modules                                |
|                                                                       |
| │ │ └── MainActivity.kt                                               |
|                                                                       |
| │ └── build.gradle.kts                                                |
|                                                                       |
| │                                                                     |
|                                                                       |
| ├── iosApp/ ← iOS-only code                                           |
|                                                                       |
| │ ├── WiFiAdvisor/                                                    |
|                                                                       |
| │ │ ├── Views/                                                        |
|                                                                       |
| │ │ │ ├── HomeView.swift                                              |
|                                                                       |
| │ │ │ ├── NetworkDetailView.swift                                     |
|                                                                       |
| │ │ │ └── SpeedTestView.swift                                         |
|                                                                       |
| │ │ └── Helpers/IOSWiFiHelper.swift                                   |
|                                                                       |
| │ └── WiFiAdvisor.xcodeproj                                           |
|                                                                       |
| │                                                                     |
|                                                                       |
| └── build.gradle.kts (root)                                           |
+-----------------------------------------------------------------------+

**8. Screen-by-Screen Specification**

**8.1 Onboarding (3 Screens --- First Launch Only)**

  --------------------- -------------------------------------------------
  **Screen**            **Content**

  Screen 1 --- Value    \"WiFi Advisor picks the best WiFi for you.\"
  Prop                  Illustration of traveler at airport. Skip + Next
                        buttons.

  Screen 2 --- How It   \"We check 3 things: Is it safe? Is it fast? Is
  Works                 it crowded?\" Animated 3-pillar icons.
                        Color-coded preview.

  Screen 3 ---          \"To scan WiFi near you, we need Location
  Permissions           access.\" Plain explanation. Permission request
                        button. Note: data stays on your phone.
  --------------------- -------------------------------------------------

**8.2 Home Screen --- The Advisor**

+-----------------------------------------------------------------------+
| **Design Rule**                                                       |
|                                                                       |
| This is the only screen most users will ever need. It must answer one |
| question within 3 seconds: What should I connect to right now?        |
+-----------------------------------------------------------------------+

**Compose Layout Spec (top to bottom)**

1.  TopAppBar: App name, current connection dot (green/red), scan
    refresh icon button

2.  Current Connection Card (if connected): Network name large and
    bold + score badge + 3 mini-pills (Safety \| Speed \| Crowding) in
    plain English + \"Connected\" indicator

3.  Best Recommendation Banner (if better network found): Large green
    Card + \"BEST CHOICE\" badge + network name + one-line reason
    (\"Fast, secure, and only 1 other network on this channel\") +
    \"Connect\" filled button

4.  All Networks LazyColumn: Each item = name + colored score badge +
    one-line summary. Sorted by score desc. Tap → Network Detail screen.

5.  BottomNavigationBar: Home · Scan · Speed Test · History · Settings

**8.3 Network Detail Screen**

  --------------------- -------------------------------------------------
  **Section**           **Content**

  Header Card           Network name (large) + overall score ring chart +
                        badge

  Safety Row            Lock icon + plain-English security label +
                        expandable \"What does this mean?\" explainer
                        text

  Speed Row             Signal bars icon + estimated speed label + band
                        plain description (\"Uses 5GHz --- better for
                        video calls\")

  Crowding Row          People icon + \"X other networks sharing this
                        channel\" + horizontal bar comparison chart

  Action Buttons        \"Connect to this Network\" (primary filled) +
                        \"Not Now\" (outlined)

  Expert Details        Raw SSID, BSSID, Channel number, RSSI dBm,
  (collapsed)           Frequency, Security string --- hidden by default,
                        expandable for IT users
  --------------------- -------------------------------------------------

**8.4 Speed Test Screen**

-   Animated arc gauge during test (Compose Canvas or Vico)

-   Results in plain language: \"Your speed is 66 Mbps --- more than
    enough for HD video calls\"

-   Context bar: Netflix HD needs 5 Mbps · Zoom needs 3 Mbps · WhatsApp
    Video needs 1 Mbps

-   Last 10 tests shown in a simple list with timestamps

-   Speed result is stored against the BSSID and used to override
    predicted Speed Score on future scans

**8.5 History Screen**

-   List of past scan sessions grouped by date

-   Each session shows: location name (from GPS reverse geocode) +
    number of networks found + top network name + top score badge

-   Useful for: \"Last time at this cafe the WiFi scored 82, today it
    scores 44 --- something changed\"

**8.6 Settings Screen**

  --------------------- -------------------------------------------------
  **Setting**           **Default + Description**

  Auto-Scan on Open     ON --- Automatically scan when app is launched

  Warn if Unsafe        ON --- Notification if your connected network
  Network               scores below 20

  Show Expert Details   OFF --- Exposes raw technical panel on Network
                        Detail screen

  Save Scan History     ON --- Stores results locally in SQLDelight DB

  Run Speed Test Auto   OFF --- Auto-run speed test after every scan
                        (uses mobile data)

  Language              English (v1). Hindi, Arabic, Spanish planned for
                        v2.
  --------------------- -------------------------------------------------

**9. Android Permissions & API Notes**

**9.1 Required Permissions**

+-----------------------------------------------------------------------+
| \<!\-- AndroidManifest.xml \--\>                                      |
|                                                                       |
| \<uses-permission                                                     |
| android:name=\"android.permission.ACCESS_WIFI_STATE\" /\>             |
|                                                                       |
| \<uses-permission                                                     |
| android:name=\"android.permission.CHANGE_WIFI_STATE\" /\>             |
|                                                                       |
| \<uses-permission                                                     |
| android:name=\"android.permission.ACCESS_FINE_LOCATION\" /\> \<!\--   |
| Required for scan results \--\>                                       |
|                                                                       |
| \<uses-permission                                                     |
| android:name=\"android.permission.ACCESS_COARSE_LOCATION\" /\>        |
|                                                                       |
| \<uses-permission                                                     |
| android:name=\"android.permission.ACCESS_NETWORK_STATE\" /\>          |
|                                                                       |
| \<uses-permission android:name=\"android.permission.INTERNET\" /\>    |
| \<!\-- Speed test only \--\>                                          |
+-----------------------------------------------------------------------+

**9.2 Android WiFi API Version Matrix**

  -------------------------------------------------------------------------------
  **Android     **API**                   **Notes**
  Version**                               
  ------------- ------------------------- ---------------------------------------
  Android 9     WifiManager.startScan()   Throttled to 4 scans per 2 min --- must
  (API 28)                                cache results

  Android 10    WifiNetworkSuggestion     In-app connect flow --- replaces old
  (API 29)                                enableNetwork()

  Android 12    ScanResult.channelWidth   80MHz/160MHz channel width detection
  (API 31)                                

  Android 13    NEARBY_WIFI_DEVICES       New granular permission --- request at
  (API 33)      permission                runtime

  All versions  WifiInfo.getRssi()        RSSI in dBm --- use
                                          WifiManager.calculateSignalLevel() to
                                          normalize
  -------------------------------------------------------------------------------

**9.3 One-Tap Connect Flow (Android 10+)**

+-----------------------------------------------------------------------+
| // HomeViewModel.kt                                                   |
|                                                                       |
| fun connectToNetwork(network: WiFiNetwork) {                          |
|                                                                       |
| val suggestion = WifiNetworkSuggestion.Builder()                      |
|                                                                       |
| .setSsid(network.ssid)                                                |
|                                                                       |
| .setWpa2Passphrase(network.savedPassword ?: return                    |
| openPasswordDialog(network))                                          |
|                                                                       |
| .build()                                                              |
|                                                                       |
| val suggestions = listOf(suggestion)                                  |
|                                                                       |
| val wifiManager = context.getSystemService(WifiManager::class.java)   |
|                                                                       |
| val status = wifiManager.addNetworkSuggestions(suggestions)           |
|                                                                       |
| if (status == WifiManager.STATUS_NETWORK_SUGGESTIONS_SUCCESS) {       |
|                                                                       |
| \_uiState.update { it.copy(connectingTo = network.ssid) }             |
|                                                                       |
| }                                                                     |
|                                                                       |
| }                                                                     |
+-----------------------------------------------------------------------+

**10. Cursor Implementation Guide**

**10.1 Project Bootstrap Commands**

+-----------------------------------------------------------------------+
| \# 1. Create KMP project (use KMP Wizard or manually)                 |
|                                                                       |
| \# Recommended: Use JetBrains KMP Wizard at kmp.jetbrains.com         |
|                                                                       |
| \# Select: Android + iOS + Shared module                              |
|                                                                       |
| \# 2. Or clone this starter template in terminal                      |
|                                                                       |
| git clone https://github.com/JetBrains/KMP-App-Template wifi-advisor  |
|                                                                       |
| cd wifi-advisor                                                       |
|                                                                       |
| \# 3. Add dependencies to shared/build.gradle.kts                     |
|                                                                       |
| \# SQLDelight, Ktor, kotlinx-serialization, multiplatform-settings    |
|                                                                       |
| \# 4. Sync and open in Android Studio (also handles KMP)              |
|                                                                       |
| ./gradlew :shared:build                                               |
+-----------------------------------------------------------------------+

**10.2 Key Cursor Prompt Templates**

Paste these directly into Cursor when building each module:

+-----------------------------------------------------------------------+
| **Prompt 1 --- Scoring Engine**                                       |
|                                                                       |
| \"Create ScoringEngine.kt in the KMP shared/commonMain module. Input: |
| WiFiNetwork data class with fields ssid, bssid, rssi (Int dBm),       |
| frequency (Int MHz), channel (Int), capabilities (String), and a list |
| of all nearby networks. Output: WiFiScore data class with total       |
| (0-100), securityScore, speedScore, congestionScore, badge (enum:     |
| BEST_CHOICE/GOOD/AVERAGE/POOR/AVOID), headline (String),              |
| securityLabel (String), speedLabel (String), congestionLabel          |
| (String). All scoring logic from the spec document. Write unit tests  |
| using kotlin.test.\"                                                  |
+-----------------------------------------------------------------------+

+-----------------------------------------------------------------------+
| **Prompt 2 --- Home Screen Compose**                                  |
|                                                                       |
| \"Create HomeScreen.kt using Jetpack Compose and Material 3. Show:    |
| (1) a large green Card at the top with the best-scored WiFiNetwork    |
| --- display name, BEST CHOICE badge, and 3 horizontal Chips for       |
| Safety/Speed/Crowding labels, plus a filled Connect button. (2) A     |
| LazyColumn below listing all networks sorted by score descending,     |
| each row showing name + colored Badge + one-line summary. Use         |
| HomeViewModel with StateFlow\<HomeUiState\>. Inject via Hilt.\"       |
+-----------------------------------------------------------------------+

+-----------------------------------------------------------------------+
| **Prompt 3 --- WiFi Scanner (Android actual)**                        |
|                                                                       |
| \"Create WiFiRepositoryAndroid.kt as the Android actual               |
| implementation of the expect WiFiRepository interface. Use            |
| WifiManager to call startScan() and parse scanResults into            |
| List\<WiFiNetwork\>. Handle Android 13 NEARBY_WIFI_DEVICES            |
| permission. Handle the Android 9+ scan throttle by returning cached   |
| results if last scan was less than 30 seconds ago. Return results as  |
| Flow\<ScanState\> using Kotlin Coroutines.\"                          |
+-----------------------------------------------------------------------+

+-----------------------------------------------------------------------+
| **Prompt 4 --- SQLDelight History DB**                                |
|                                                                       |
| \"Create WifiAdvisor.sq SQLDelight schema in the shared module with   |
| tables: scan_session (id, timestamp, location_name, network_count)    |
| and scan_result (id, session_id, ssid, bssid, total_score,            |
| security_score, speed_score, congestion_score, badge). Generate       |
| HistoryRepository.kt with functions: insertSession, insertResult,     |
| getSessionsGroupedByDate, getResultsForSession. Target both Android   |
| (SQLiteDriver) and iOS (NativeSqliteDriver).\"                        |
+-----------------------------------------------------------------------+

**11. Testing Strategy**

**11.1 Unit Tests --- KMP Shared Module**

-   ScoringEngine: Test all security type combinations, RSSI edge cases,
    congestion counts

-   RecommendationEngine: Verify correct network selected from a list of
    10 with mixed scores

-   Run with: ./gradlew :shared:testDebugUnitTest --- no emulator
    needed, pure JVM

**11.2 Integration Tests --- Android**

-   WifiRepository: Instrumented test using MockWifiManager to simulate
    15 networks

-   HomeViewModel: Test StateFlow emissions using Turbine library

-   Run with: ./gradlew :androidApp:connectedAndroidTest

**11.3 UI Tests --- Compose**

-   HomeScreen: ComposeTestRule to verify recommendation card shows
    correct badge

-   NetworkDetailScreen: Verify expert panel hidden by default, shown
    after tap

-   Use createComposeRule() with fake ViewModel injected via Hilt
    testing

**12. Delivery Phases**

**Phase 1 --- Android MVP (Weeks 1--6)**

-   KMP project setup with shared module structure

-   ScoringEngine + RecommendationEngine (shared, fully tested)

-   Android WiFi scanner with WifiManager + permission handling

-   Home Screen (Compose): recommendation card + network list

-   Network Detail Screen: 3-pillar plain-language breakdown

-   Speed Test Screen: live test + plain-language results

-   Basic onboarding (3 screens + runtime permissions)

**Phase 2 --- Android Polish + KMP iOS Foundation (Weeks 7--10)**

-   Scan history with SQLDelight + History Screen

-   Settings screen with all toggles

-   One-tap connect via WifiNetworkSuggestion API

-   Notification for unsafe network detection

-   iOS: KMP shared module integrated in Xcode via xcframework

-   iOS: Basic SwiftUI Home + Detail screens consuming KMP logic

**Phase 3 --- Launch + iOS Full (Weeks 11--16)**

-   Google Play Store submission + Android production release

-   iOS full SwiftUI app completion + App Store submission

-   Hindi + Spanish localization (shared strings via KMP)

-   Optional cloud backend: anonymized speed benchmarks via Ktor

-   Android home screen widget (Glance API) showing current connection
    score

**13. Risks & Mitigation**

  ---------------------------------------------------------------------------
  **Risk**                **Impact**             **Mitigation**
  ----------------------- ---------------------- ----------------------------
  Android scan throttle   Max 4 scans per 2 min  Cache last scan, show
  (API 28+)               --- stale results      timestamp, allow manual
                                                 refresh

  iOS WiFi API            Can only see current   iOS: analyze current +
  restrictions            network (not full      advise via speed test. Full
                          list)                  list in v2 via
                                                 NEHotspotHelper entitlement.

  KMP iOS interop         Kotlin suspend fns     Use SKIE library or wrapper
  complexity              don\'t export cleanly  classes to expose Flows as
                          to Swift               Swift AsyncStream

  WifiNetworkSuggestion   Android shows system   Brief explanation overlay
  UX friction             dialog --- not fully   before triggering dialog:
                          in-app                 \"Android will ask for
                                                 confirmation\"

  Location permission     Cannot access scan     Onboarding explains exactly
  refusal                 results without it     why location is needed ---
                                                 stored on device only, never
                                                 shared

  Score inaccuracy        Wrong recommendation   Conservative scoring ---
  damaging trust          loses user confidence  when data is limited, show
                                                 \'Limited Data\' badge and
                                                 explain why
  ---------------------------------------------------------------------------

*End of Document \| WiFi Advisor \| Android Kotlin + KMP \| HLD &
Product Specification v2.0*
