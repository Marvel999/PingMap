# Network Analyzer Pro — HLD & LLD Architecture
> **Purpose:** Developer reference for building a Network Analyzer app from scratch using the same Android APIs.  
> **Use Case Focus:** Why people actually buy/download this app — and how to architect each one.

---

## Table of Contents
0. [UI/UX Guidelines (Summary)](#0-uiux-guidelines-summary)
1. [Why People Buy This App — Core Use Cases](#1-why-people-buy-this-app)
2. [High-Level Design (HLD)](#2-high-level-design)
3. [Low-Level Design (LLD) — Per Use Case](#3-low-level-design-per-use-case)
4. [Android API Reference Cheatsheet](#4-android-api-reference-cheatsheet)
5. [Data Models](#5-data-models)
6. [Project Structure](#6-project-structure)
7. [Permissions Reference](#7-permissions-reference)
8. [UI Design System — White Minimal Theme](#8-ui-design-system)
9. [Jetpack Compose — Screen by Screen](#9-jetpack-compose-screens)
10. [Dagger 2 Dependency Injection Setup](#10-dagger-2-dependency-injection)
11. [Navigation Graph](#11-navigation-graph)
12. [Graph & Data Visualization Guide](#12-graph-and-data-visualization)
13. [UX Principles & Non-Tech User Friendliness](#13-ux-principles)
14. [Project Plan for Implementation](#14-project-plan-for-implementation)

---

## 0. UI/UX Guidelines (Summary)

All UI and UX decisions in this app must follow these four pillars:

| # | Guideline | Description |
|---|-----------|-------------|
| **1** | **Simple white theme + soft colors** | Use a clean white base (`#FFFFFF`, `#F7F9FC`) and soft accent colors (e.g. Soft Blue, Soft Green, Soft Amber) — no harsh or saturated colors. See [§8 UI Design System](#8-ui-design-system). |
| **2** | **User-friendly** | Target non-technical users: plain English labels, one primary action per screen, friendly empty/loading states, and clear explanations for every metric. See [§13 UX Principles](#13-ux-principles). |
| **3** | **Interactive graphs** | Charts must be **user-interactive**: tap/click for tooltips, pinch-to-zoom where useful, clear legends, and smooth animations so users can explore data, not just view it. See [§12 Graph & Data Visualization](#12-graph-and-data-visualization). |
| **4** | **MVVM architecture** | Every screen is built with **MVVM**: ViewModel holds state and business logic; UI layer (Compose) only observes `StateFlow`/`Flow` and renders. No business logic in Composables. See [§2.1 Architecture](#21-architecture-overview) and [§9 Jetpack Compose Screens](#9-jetpack-compose-screens). |

---

## 1. Why People Buy This App

These are the **real reasons** users pay for network analyzer apps — ranked by frequency of use and review mentions:

| # | Use Case | User Problem | Willingness to Pay |
|---|----------|-------------|-------------------|
| 1 | **WiFi Troubleshooting** | "My WiFi is slow. Is it my router or my ISP?" | ⭐⭐⭐⭐⭐ |
| 2 | **Speed Test (No Ads)** | Tired of Speedtest.net ads. Want clean data. | ⭐⭐⭐⭐⭐ |
| 3 | **Who's on My Network?** | Detect unknown devices, catch WiFi thieves | ⭐⭐⭐⭐⭐ |
| 4 | **Dead Zone Mapping** | "Where exactly does my WiFi signal drop?" | ⭐⭐⭐⭐ |
| 5 | **Port Scanner / Security Audit** | IT admins checking their network exposure | ⭐⭐⭐⭐ |
| 6 | **Ping & Latency Diagnosis** | Gamers & WFH workers diagnosing lag spikes | ⭐⭐⭐⭐ |
| 7 | **Channel Congestion Check** | "Which WiFi channel should I set my router to?" | ⭐⭐⭐ |
| 8 | **ISP Accountability** | "I'm paying for 100Mbps — prove I'm getting it" | ⭐⭐⭐⭐⭐ |

> **Key insight:** The top 3 use cases alone account for ~70% of downloads. Build these first and build them well.

---

## 2. High-Level Design

### 2.1 Architecture Overview

```
┌──────────────────────────────────────────────────────┐
│                    UI LAYER (UI/UX Guideline 4: MVVM)│
│  Jetpack Compose Screens + ViewModels (MVVM)         │
│  HomeScreen │ ScanScreen │ SpeedScreen │ PingScreen  │
│  SignalMapScreen │ PortScanScreen │ DeviceListScreen │
└────────────────────────┬─────────────────────────────┘
                         │ StateFlow / Events
┌────────────────────────▼─────────────────────────────┐
│                  DOMAIN LAYER                        │
│  Use Cases (Pure Kotlin, no Android deps)            │
│  ScanWifiUseCase │ RunSpeedTestUseCase               │
│  PingHostUseCase │ ScanNetworkDevicesUseCase         │
│  RecordSignalStrengthUseCase │ ScanPortsUseCase      │
└────────────────────────┬─────────────────────────────┘
                         │ Repository interfaces
┌────────────────────────▼─────────────────────────────┐
│                   DATA LAYER                         │
│  Repository Implementations + Data Sources           │
│  WifiDataSource │ NetworkDataSource                  │
│  SpeedTestDataSource │ LocationDataSource            │
│  Room DB (history) │ DataStore (preferences)         │
└────────────────────────┬─────────────────────────────┘
                         │ System calls
┌────────────────────────▼─────────────────────────────┐
│              ANDROID SYSTEM LAYER                    │
│  WifiManager │ ConnectivityManager │ WifiRttManager  │
│  LocationManager │ TelephonyManager │ InetAddress    │
│  Socket │ NetworkCallback │ LinkProperties           │
└──────────────────────────────────────────────────────┘
```

### 2.2 App Modules (Multi-Module)

```
:app                          ← Navigation host, DI root, MainActivity
:core:ui                      ← Shared Compose components, theme
:core:network                 ← OkHttpClient, NetworkMonitor singleton
:core:permissions             ← Permission request flows
:data:local                   ← Room DB, DAOs, entities
:data:preferences             ← DataStore (user settings)
:feature:wifi-scanner         ← Use case 1 + 7 (scan + channels)
:feature:speed-test           ← Use case 2 + 8
:feature:device-discovery     ← Use case 3 (who's on my network)
:feature:signal-map           ← Use case 4
:feature:port-scanner         ← Use case 5
:feature:ping                 ← Use case 6
:service:monitor              ← Background network monitoring
```

### 2.3 Tech Stack

| Layer | Technology |
|-------|-----------|
| Language | Kotlin |
| UI | Jetpack Compose + Material 3 |
| Architecture | MVVM + Clean Architecture |
| DI | Dagger 2 |
| Async | Coroutines + Flow |
| Charts | Vico or MPAndroidChart |
| Maps | Google Maps SDK (Compose) |
| HTTP | OkHttp 4.x |
| Database | Room 2.x |
| Preferences | DataStore (Proto) |
| Background | WorkManager + ForegroundService |
| Testing | JUnit5 + MockK + Turbine |

---

## 3. Low-Level Design Per Use Case

---

### USE CASE 1 — WiFi Troubleshooting (Scan Nearby Networks)

**User problem:** "My WiFi is slow — is it my router, interference, or ISP?"

#### What to show the user
- All nearby SSIDs with signal strength (RSSI → quality %)
- Frequency band (2.4GHz vs 5GHz)
- Channel number — and how congested that channel is
- Security type (WPA2, WPA3, Open)
- Distance estimation from signal

#### HLD Flow
```
User taps "Scan" → ScanWifiUseCase → WifiDataSource.scan()
    → WifiManager.startScan()
    → BroadcastReceiver(SCAN_RESULTS_AVAILABLE_ACTION)
    → WifiManager.scanResults (List<ScanResult>)
    → map to WifiNetwork domain model
    → emit via Flow<List<WifiNetwork>>
    → WifiScanViewModel updates StateFlow<WifiScanState>
    → Compose UI recomposes
```

#### Key Android APIs

```kotlin
// Request scan
val wifiManager = context.getSystemService(WifiManager::class.java)
wifiManager.startScan() // Deprecated API 28+ but still works; use passive scan as fallback

// Receive results
val receiver = object : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val success = intent.getBooleanExtra(WifiManager.EXTRA_RESULTS_UPDATED, false)
        val results: List<ScanResult> = wifiManager.scanResults
        // parse results here
    }
}
context.registerReceiver(receiver, IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION))
```

#### Parsing ScanResult

```kotlin
data class WifiNetwork(
    val ssid: String,
    val bssid: String,
    val rssi: Int,               // Signal strength in dBm (e.g. -55)
    val frequency: Int,          // MHz (2412-2484 = 2.4GHz, 4915-5825 = 5GHz)
    val channel: Int,            // Derived from frequency
    val security: String,        // "WPA2", "WPA3", "Open"
    val signalQuality: Int,      // 0-100%, derived from RSSI
    val distanceMeters: Double   // Estimated from RSSI
)

fun ScanResult.toWifiNetwork(): WifiNetwork {
    return WifiNetwork(
        ssid = SSID.removePrefix("\"").removeSuffix("\""),
        bssid = BSSID,
        rssi = level,
        frequency = frequency,
        channel = frequencyToChannel(frequency),
        security = parseCapabilities(capabilities),
        signalQuality = rssiToQuality(level),
        distanceMeters = estimateDistance(frequency, level)
    )
}

// RSSI to quality (0–100%)
fun rssiToQuality(rssi: Int): Int {
    return when {
        rssi >= -50 -> 100
        rssi <= -100 -> 0
        else -> 2 * (rssi + 100)
    }
}

// Frequency to channel
fun frequencyToChannel(freq: Int): Int {
    return when {
        freq == 2484 -> 14
        freq in 2412..2472 -> (freq - 2407) / 5
        freq in 5170..5825 -> (freq - 5000) / 5
        else -> -1
    }
}

// Free-space path loss distance estimate
fun estimateDistance(frequency: Int, rssi: Int): Double {
    val exp = (27.55 - 20 * log10(frequency.toDouble()) + abs(rssi)) / 20.0
    return 10.0.pow(exp)
}
```

#### Channel Congestion Logic

```kotlin
// Group networks by channel, count overlap
fun computeChannelCongestion(networks: List<WifiNetwork>): Map<Int, Int> {
    return networks.groupBy { it.channel }.mapValues { it.value.size }
}
// Channels with count > 3 = congested → suggest switching to a less used channel
```

---

### USE CASE 2 — Speed Test (Clean, No Ads)

**User problem:** "I need accurate speed data without 15 ads and upsells."

#### What to show the user
- Download speed (Mbps) — live updating gauge
- Upload speed (Mbps)
- Ping / latency (ms)
- Jitter (ms) — variation in latency
- Historical graph: speed over time

#### HLD Flow
```
User taps "Start" → RunSpeedTestUseCase
    → Phase 1: Ping test (measure latency via InetAddress)
    → Phase 2: Download test (stream large file, measure throughput)
    → Phase 3: Upload test (POST data, measure throughput)
    → Emit Flow<SpeedTestProgress> with real-time Mbps
    → SpeedTestViewModel collects → LiveGauge UI updates
    → Save result to Room DB (SpeedResultEntity)
```

#### Key Implementation

```kotlin
// Download speed test
suspend fun measureDownloadSpeed(
    testUrl: String = "https://speed.cloudflare.com/__down?bytes=25000000",
    onProgress: (currentMbps: Double) -> Unit
): Double = withContext(Dispatchers.IO) {
    val client = OkHttpClient.Builder()
        .connectTimeout(10, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .build()

    val request = Request.Builder().url(testUrl).build()
    val startTime = System.currentTimeMillis()
    var totalBytes = 0L

    client.newCall(request).execute().use { response ->
        val body = response.body ?: return@withContext 0.0
        val buffer = ByteArray(8192)
        val inputStream = body.byteStream()

        while (true) {
            val bytesRead = inputStream.read(buffer)
            if (bytesRead == -1) break
            totalBytes += bytesRead
            val elapsed = (System.currentTimeMillis() - startTime) / 1000.0
            if (elapsed > 0) {
                val currentMbps = (totalBytes * 8) / (elapsed * 1_000_000)
                onProgress(currentMbps)
            }
        }
    }

    val totalSeconds = (System.currentTimeMillis() - startTime) / 1000.0
    (totalBytes * 8) / (totalSeconds * 1_000_000)
}

// Ping / latency measurement
suspend fun measurePing(host: String = "8.8.8.8", count: Int = 5): PingResult {
    val rtts = mutableListOf<Long>()
    repeat(count) {
        val start = System.currentTimeMillis()
        val reachable = InetAddress.getByName(host).isReachable(2000)
        if (reachable) rtts.add(System.currentTimeMillis() - start)
        delay(200)
    }
    return PingResult(
        min = rtts.minOrNull() ?: 0,
        max = rtts.maxOrNull() ?: 0,
        avg = rtts.average().toLong(),
        jitter = calculateJitter(rtts),
        packetLoss = ((count - rtts.size) * 100.0 / count).toInt()
    )
}

fun calculateJitter(rtts: List<Long>): Double {
    if (rtts.size < 2) return 0.0
    return rtts.zipWithNext { a, b -> abs(b - a).toDouble() }.average()
}
```

---

### USE CASE 3 — Who's On My Network (Device Discovery)

**User problem:** "I think someone is stealing my WiFi. I want to see every device connected."

#### What to show the user
- List of all devices: IP, MAC address, hostname
- Device type icon (phone, laptop, smart TV, IoT)
- Manufacturer name (from MAC OUI lookup)
- Whether device is new/unknown
- Alert when new device joins

#### HLD Flow
```
User taps "Scan Network" → ScanNetworkDevicesUseCase
    → Get local subnet from LinkProperties (e.g. 192.168.1.x/24)
    → Ping sweep: InetAddress.isReachable() for all 254 IPs (parallel coroutines)
    → For each reachable IP:
        → Read ARP cache (/proc/net/arp) to get MAC address
        → Lookup MAC OUI → manufacturer name (local DB)
        → Reverse DNS lookup → hostname
        → Classify device type from MAC prefix
    → Emit results as Flow<List<NetworkDevice>>
    → Save to Room DB for "new device" comparison
```

#### Key Implementation

```kotlin
// Get local subnet
fun getSubnet(context: Context): String? {
    val cm = context.getSystemService(ConnectivityManager::class.java)
    val network = cm.activeNetwork ?: return null
    val linkProperties = cm.getLinkProperties(network) ?: return null
    val addr = linkProperties.linkAddresses
        .firstOrNull { it.address is Inet4Address } ?: return null
    val ip = addr.address.hostAddress ?: return null
    return ip.substringBeforeLast(".") // e.g. "192.168.1"
}

// Parallel ping sweep
suspend fun discoverDevices(subnet: String): List<NetworkDevice> = coroutineScope {
    (1..254).map { host ->
        async(Dispatchers.IO) {
            val ip = "$subnet.$host"
            try {
                val address = InetAddress.getByName(ip)
                if (address.isReachable(500)) {
                    val mac = getMacFromArp(ip)
                    val hostname = tryReverseDns(ip)
                    val manufacturer = lookupManufacturer(mac)
                    NetworkDevice(ip, mac, hostname, manufacturer)
                } else null
            } catch (e: Exception) { null }
        }
    }.awaitAll().filterNotNull()
}

// Read ARP cache (no root required)
fun getMacFromArp(ip: String): String {
    return try {
        File("/proc/net/arp").readLines()
            .drop(1) // skip header
            .firstOrNull { it.startsWith(ip) }
            ?.split("\\s+".toRegex())
            ?.getOrNull(3) ?: "Unknown"
    } catch (e: Exception) { "Unknown" }
}

// Reverse DNS lookup
suspend fun tryReverseDns(ip: String): String = withContext(Dispatchers.IO) {
    try { InetAddress.getByName(ip).canonicalHostName } catch (e: Exception) { ip }
}
```

#### OUI Manufacturer Lookup

```kotlin
// Store a trimmed OUI database as assets/oui.csv (MAC prefix → manufacturer)
// Format: "00:1A:2B,Apple Inc."
class OuiLookup(context: Context) {
    private val ouiMap: Map<String, String> by lazy {
        context.assets.open("oui.csv").bufferedReader()
            .readLines()
            .associate {
                val parts = it.split(",")
                parts[0].uppercase() to parts[1]
            }
    }

    fun lookup(mac: String): String {
        val prefix = mac.uppercase().take(8) // "00:1A:2B"
        return ouiMap[prefix] ?: "Unknown"
    }
}
```

---

### USE CASE 4 — Signal Dead Zone Map

**User problem:** "I want to see exactly where my WiFi signal drops in my house."

#### What to show the user
- Overhead heatmap overlaid on Google Maps
- Color scale: Green (strong) → Yellow → Red (weak/dead)
- Recorded signal points with coordinates
- Export/share the map

#### HLD Flow
```
User starts recording → RecordSignalStrengthUseCase
    → FusedLocationProviderClient emits location updates (1-2s interval)
    → WifiManager.connectionInfo.rssi (current RSSI)
    → Combine: SignalPoint(lat, lng, rssi)
    → Store list of SignalPoints in memory + Room DB
    → On map render: interpolate points → HeatmapTileProvider
    → Google Maps SDK renders color-coded overlay
```

#### Key Implementation

```kotlin
data class SignalPoint(
    val lat: Double,
    val lng: Double,
    val rssi: Int,           // -30 (excellent) to -90 (very weak)
    val timestamp: Long = System.currentTimeMillis()
)

// Collect signal points
fun collectSignalPoints(
    context: Context,
    fusedLocationClient: FusedLocationProviderClient
): Flow<SignalPoint> = callbackFlow {
    val wifiManager = context.getSystemService(WifiManager::class.java)
    val request = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 1500).build()

    val callback = object : LocationCallback() {
        override fun onLocationResult(result: LocationResult) {
            val loc = result.lastLocation ?: return
            val rssi = wifiManager.connectionInfo.rssi
            trySend(SignalPoint(loc.latitude, loc.longitude, rssi))
        }
    }

    fusedLocationClient.requestLocationUpdates(request, callback, Looper.getMainLooper())
    awaitClose { fusedLocationClient.removeLocationUpdates(callback) }
}

// Render heatmap with Google Maps SDK (Compose)
@Composable
fun SignalHeatmap(points: List<SignalPoint>) {
    val heatmapData = points.map { point ->
        val weight = rssiToWeight(point.rssi) // normalize 0.0–1.0
        WeightedLatLng(LatLng(point.lat, point.lng), weight)
    }

    GoogleMap {
        if (heatmapData.isNotEmpty()) {
            val tileProvider = HeatmapTileProvider.Builder()
                .weightedData(heatmapData)
                .radius(40)
                .build()
            TileOverlay(tileOverlayState = rememberTileOverlayState(), tileProvider = tileProvider)
        }
    }
}

fun rssiToWeight(rssi: Int): Double {
    // Map -30 (best) to -90 (worst) → 1.0 to 0.0
    return ((rssi + 90).coerceIn(0, 60)) / 60.0
}
```

---

### USE CASE 5 — Port Scanner (Security Audit)

**User problem:** "I'm an IT admin. Which ports are exposed on devices in my network?"

#### What to show the user
- Open ports per IP/device
- Common service names (port 80 = HTTP, 443 = HTTPS, 22 = SSH, etc.)
- Scan status with progress indicator
- Export report as CSV

#### HLD Flow
```
User enters target IP + port range → ScanPortsUseCase
    → For each port: attempt Socket.connect(ip, port, timeout)
    → If connect succeeds → port is OPEN
    → Map port number to known service name
    → Emit results as Flow<PortScanProgress>
```

#### Key Implementation

```kotlin
data class PortResult(
    val port: Int,
    val isOpen: Boolean,
    val serviceName: String
)

suspend fun scanPorts(
    ipAddress: String,
    portRange: IntRange = 1..1024,
    timeoutMs: Int = 300,
    concurrency: Int = 50
): Flow<PortResult> = channelFlow {
    val semaphore = Semaphore(concurrency)
    portRange.map { port ->
        launch(Dispatchers.IO) {
            semaphore.withPermit {
                val isOpen = try {
                    Socket().use { socket ->
                        socket.connect(InetSocketAddress(ipAddress, port), timeoutMs)
                        true
                    }
                } catch (e: Exception) { false }

                send(PortResult(
                    port = port,
                    isOpen = isOpen,
                    serviceName = wellKnownServices[port] ?: "Unknown"
                ))
            }
        }
    }.joinAll()
}

val wellKnownServices = mapOf(
    21 to "FTP", 22 to "SSH", 23 to "Telnet", 25 to "SMTP",
    53 to "DNS", 80 to "HTTP", 110 to "POP3", 143 to "IMAP",
    443 to "HTTPS", 445 to "SMB", 3306 to "MySQL", 3389 to "RDP",
    5432 to "PostgreSQL", 8080 to "HTTP-Alt", 8443 to "HTTPS-Alt"
)
```

---

### USE CASE 6 — Ping & Latency Diagnosis

**User problem:** "My game lags. Is it my connection or the server?"

#### What to show the user
- RTT: min / avg / max in ms
- Packet loss percentage
- Jitter (ms)
- Live RTT graph over time
- Traceroute: which hop is causing delay

#### Traceroute Implementation

```kotlin
// Traceroute via shell command (no root needed for basic traceroute)
suspend fun runTraceroute(host: String): Flow<HopResult> = flow {
    val process = Runtime.getRuntime().exec(arrayOf("traceroute", "-n", "-m", "30", host))
    val reader = BufferedReader(InputStreamReader(process.inputStream))
    var hopNum = 0

    reader.forEachLine { line ->
        hopNum++
        val hop = parseTracerouteLine(line, hopNum)
        if (hop != null) emit(hop)
    }
    process.waitFor()
}

data class HopResult(
    val hopNumber: Int,
    val ipAddress: String,
    val hostname: String?,
    val rtt1: Double?,   // ms
    val rtt2: Double?,
    val rtt3: Double?,
    val isTimeout: Boolean
)

fun parseTracerouteLine(line: String, hopNum: Int): HopResult? {
    if (line.trim().isEmpty()) return null
    val isTimeout = line.contains("* * *")
    val ipRegex = Regex("""(\d{1,3}(?:\.\d{1,3}){3})""")
    val rttRegex = Regex("""(\d+\.?\d*)\s*ms""")

    val ip = ipRegex.find(line)?.value ?: "* * *"
    val rtts = rttRegex.findAll(line).map { it.groupValues[1].toDoubleOrNull() }.toList()

    return HopResult(
        hopNumber = hopNum,
        ipAddress = ip,
        hostname = null,
        rtt1 = rtts.getOrNull(0),
        rtt2 = rtts.getOrNull(1),
        rtt3 = rtts.getOrNull(2),
        isTimeout = isTimeout
    )
}
```

---

## 4. Android API Reference Cheatsheet

> Save this — these are the exact APIs you'll reuse across all use cases.

### WifiManager

```kotlin
val wifiManager = context.getSystemService(WifiManager::class.java)

wifiManager.startScan()                          // Trigger scan (requires CHANGE_WIFI_STATE)
wifiManager.scanResults                          // List<ScanResult> after scan
wifiManager.connectionInfo                       // WifiInfo (current connection)
wifiManager.connectionInfo.rssi                  // Current signal strength (dBm)
wifiManager.connectionInfo.ssid                  // Current SSID (quoted string)
wifiManager.connectionInfo.bssid                 // Current BSSID (router MAC)
wifiManager.connectionInfo.linkSpeed             // Link speed in Mbps
wifiManager.connectionInfo.frequency             // Frequency in MHz
wifiManager.dhcpInfo                             // Gateway, DNS, subnet mask (legacy)
WifiManager.calculateSignalLevel(rssi, 5)        // RSSI → 0-4 bars

// ScanResult fields
scanResult.SSID          // Network name
scanResult.BSSID         // MAC of access point
scanResult.level         // RSSI in dBm
scanResult.frequency     // MHz
scanResult.capabilities  // "[WPA2-PSK-CCMP][ESS]"
scanResult.channelWidth  // CHANNEL_WIDTH_20MHZ etc.
scanResult.timestamp     // Microseconds since boot
```

### ConnectivityManager

```kotlin
val cm = context.getSystemService(ConnectivityManager::class.java)

val network = cm.activeNetwork
val capabilities = cm.getNetworkCapabilities(network)
val linkProperties = cm.getLinkProperties(network)

// Network type check
capabilities?.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)     // Is WiFi?
capabilities?.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) // Is mobile data?
capabilities?.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)

// Link properties (IPs, DNS, gateway)
linkProperties?.linkAddresses        // List<LinkAddress> (IP + prefix length)
linkProperties?.dnsServers           // List<InetAddress>
linkProperties?.dhcpServerAddress    // Gateway IP
linkProperties?.domainName           // Domain suffix

// Network monitoring callback
val callback = object : ConnectivityManager.NetworkCallback() {
    override fun onAvailable(network: Network) { /* connected */ }
    override fun onLost(network: Network) { /* disconnected */ }
    override fun onCapabilitiesChanged(network: Network, caps: NetworkCapabilities) { }
    override fun onLinkPropertiesChanged(network: Network, props: LinkProperties) { }
}
cm.registerDefaultNetworkCallback(callback)
cm.unregisterNetworkCallback(callback) // Always unregister!
```

### TelephonyManager

```kotlin
val tm = context.getSystemService(TelephonyManager::class.java)

tm.networkOperatorName               // Carrier name (e.g. "Jio", "Airtel")
tm.networkType                       // TelephonyManager.NETWORK_TYPE_LTE etc.
tm.dataNetworkType                   // Current data network type
tm.isNetworkRoaming                  // Roaming status
tm.simCountryIso                     // SIM country code

// Network type to string
fun networkTypeString(type: Int): String = when (type) {
    TelephonyManager.NETWORK_TYPE_LTE  -> "LTE (4G)"
    TelephonyManager.NETWORK_TYPE_NR   -> "NR (5G)"
    TelephonyManager.NETWORK_TYPE_HSDPA, 
    TelephonyManager.NETWORK_TYPE_HSPA -> "HSPA (3G)"
    TelephonyManager.NETWORK_TYPE_EDGE -> "EDGE (2G)"
    else -> "Unknown"
}
```

### LocationManager / FusedLocationProviderClient

```kotlin
// Fused (recommended — more battery efficient)
val fusedClient = LocationServices.getFusedLocationProviderClient(context)

// Last known location (fast)
fusedClient.lastLocation.addOnSuccessListener { location: Location? -> }

// Continuous updates
val request = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 2000).build()
fusedClient.requestLocationUpdates(request, callback, Looper.getMainLooper())
```

### InetAddress (Ping)

```kotlin
// Simple reachability check
val addr = InetAddress.getByName("8.8.8.8")
val reachable = addr.isReachable(2000) // timeout ms

// Reverse DNS
val canonical = InetAddress.getByName("192.168.1.1").canonicalHostName

// NOTE: isReachable() uses ICMP if root, or TCP echo on port 7.
// For real ICMP ping, use Runtime.exec("ping -c 1 -W 1 $host")
```

### Runtime.exec (Real ICMP Ping / Traceroute)

```kotlin
// ICMP Ping (parses real RTT from ping output)
fun pingShell(host: String, count: Int = 4): List<Long> {
    val process = Runtime.getRuntime().exec(arrayOf("ping", "-c", count.toString(), host))
    val output = process.inputStream.bufferedReader().readText()
    process.waitFor()
    // Parse "time=23.4 ms" from each line
    return Regex("""time=(\d+\.?\d*)""")
        .findAll(output)
        .map { it.groupValues[1].toLong() }
        .toList()
}
```

### Socket (Port Scanner)

```kotlin
// Check if port is open
fun isPortOpen(ip: String, port: Int, timeoutMs: Int = 300): Boolean {
    return try {
        Socket().use { socket ->
            socket.connect(InetSocketAddress(ip, port), timeoutMs)
            true
        }
    } catch (e: IOException) { false }
}
```

### ARP Cache (Device MAC Discovery)

```kotlin
// Read /proc/net/arp — no root needed, works on all Android versions
fun getArpTable(): List<ArpEntry> {
    return File("/proc/net/arp").readLines()
        .drop(1) // skip header: "IP address HW type Flags HW address Mask Device"
        .mapNotNull { line ->
            val cols = line.trim().split("\\s+".toRegex())
            if (cols.size >= 4 && cols[3] != "00:00:00:00:00:00") {
                ArpEntry(ip = cols[0], mac = cols[3].uppercase(), iface = cols.lastOrNull() ?: "")
            } else null
        }
}
data class ArpEntry(val ip: String, val mac: String, val iface: String)
```

---

## 5. Data Models

```kotlin
// WiFi Network
data class WifiNetwork(
    val ssid: String,
    val bssid: String,
    val rssi: Int,
    val frequency: Int,
    val channel: Int,
    val channelWidth: Int,
    val security: String,
    val signalQuality: Int,     // 0–100
    val band: WifiBand,         // BAND_2_4GHZ, BAND_5GHZ, BAND_6GHZ
    val distanceMeters: Double,
    val scannedAt: Long = System.currentTimeMillis()
)

enum class WifiBand { BAND_2_4GHZ, BAND_5GHZ, BAND_6GHZ, UNKNOWN }

// Speed Test Result
data class SpeedTestResult(
    val id: Long = 0,
    val downloadMbps: Double,
    val uploadMbps: Double,
    val pingMs: Long,
    val jitterMs: Double,
    val packetLoss: Int,        // percentage
    val serverLocation: String,
    val isp: String,
    val networkType: String,    // "WiFi", "LTE", "5G"
    val timestamp: Long = System.currentTimeMillis()
)

// Network Device
data class NetworkDevice(
    val ipAddress: String,
    val macAddress: String,
    val hostname: String?,
    val manufacturer: String?,
    val deviceType: DeviceType,
    val isCurrentDevice: Boolean = false,
    val firstSeen: Long = System.currentTimeMillis(),
    val lastSeen: Long = System.currentTimeMillis()
)

enum class DeviceType { PHONE, LAPTOP, TABLET, SMART_TV, ROUTER, IOT, UNKNOWN }

// Signal Map Point
data class SignalPoint(
    val id: Long = 0,
    val sessionId: String,
    val lat: Double,
    val lng: Double,
    val rssi: Int,
    val ssid: String,
    val timestamp: Long = System.currentTimeMillis()
)

// Ping Result
data class PingResult(
    val host: String,
    val minMs: Long,
    val avgMs: Long,
    val maxMs: Long,
    val jitterMs: Double,
    val packetLoss: Int,
    val packets: List<Long>     // individual RTTs
)

// Port Scan Result
data class PortScanResult(
    val targetIp: String,
    val port: Int,
    val isOpen: Boolean,
    val serviceName: String,
    val banner: String? = null  // optional service banner grab
)

// Traceroute Hop
data class HopResult(
    val hopNumber: Int,
    val ipAddress: String,
    val hostname: String?,
    val rttMsList: List<Double>,
    val isTimeout: Boolean
)
```

---

## 6. Project Structure

```
app/
├── src/main/
│   ├── AndroidManifest.xml
│   └── kotlin/com/yourapp/netpulse/
│       └── MainActivity.kt
│       └── NavGraph.kt
│       └── di/AppModule.kt
│
core/
├── ui/
│   └── theme/, components/, utils/
├── network/
│   └── OkHttpProvider.kt
│   └── NetworkMonitor.kt
│   └── extensions/
└── permissions/
    └── PermissionHandler.kt

data/
├── local/
│   ├── NetPulseDatabase.kt
│   ├── dao/ (WifiDao, SpeedTestDao, DeviceDao, SignalPointDao)
│   └── entity/ (mirrors domain models with @Entity)
└── preferences/
    └── UserPreferencesDataStore.kt

feature/
├── wifi-scanner/
│   ├── data/WifiDataSource.kt, WifiRepositoryImpl.kt
│   ├── domain/WifiNetwork.kt, ScanWifiUseCase.kt
│   └── ui/WifiScanScreen.kt, WifiScanViewModel.kt
│
├── speed-test/
│   ├── data/SpeedTestDataSource.kt
│   ├── domain/SpeedTestResult.kt, RunSpeedTestUseCase.kt
│   └── ui/SpeedTestScreen.kt, SpeedTestViewModel.kt
│
├── device-discovery/
│   ├── data/ArpDataSource.kt, OuiLookup.kt
│   ├── domain/NetworkDevice.kt, ScanNetworkDevicesUseCase.kt
│   └── ui/DeviceListScreen.kt, DeviceDiscoveryViewModel.kt
│
├── signal-map/
│   ├── data/SignalRecordingDataSource.kt
│   ├── domain/SignalPoint.kt, RecordSignalUseCase.kt
│   └── ui/SignalMapScreen.kt, SignalMapViewModel.kt
│
├── port-scanner/
│   ├── domain/PortResult.kt, ScanPortsUseCase.kt
│   └── ui/PortScanScreen.kt, PortScanViewModel.kt
│
└── ping/
    ├── domain/PingResult.kt, HopResult.kt, PingHostUseCase.kt, TracerouteUseCase.kt
    └── ui/PingScreen.kt, PingViewModel.kt

service/
└── monitor/
    └── NetworkMonitorService.kt     ← ForegroundService
    └── NetworkCheckWorker.kt        ← WorkManager periodic check
```

---

## 7. Permissions Reference

Add to `AndroidManifest.xml`:

```xml
<!-- Required for WiFi scanning and connectivity info -->
<uses-permission android:name="android.permission.ACCESS_WIFI_STATE"/>
<uses-permission android:name="android.permission.CHANGE_WIFI_STATE"/>
<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE"/>
<uses-permission android:name="android.permission.INTERNET"/>

<!-- Required to read WiFi SSID and for signal map -->
<!-- Android requires fine location to read ANY WiFi scan results since API 29 -->
<uses-permission android:name="android.permission.ACCESS_FINE_LOCATION"/>
<uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION"/>

<!-- Only if background monitoring feature is enabled -->
<uses-permission android:name="android.permission.ACCESS_BACKGROUND_LOCATION"/>

<!-- Carrier/mobile data info -->
<uses-permission android:name="android.permission.READ_PHONE_STATE"/>

<!-- Background service -->
<uses-permission android:name="android.permission.FOREGROUND_SERVICE"/>
<uses-permission android:name="android.permission.FOREGROUND_SERVICE_CONNECTED_DEVICE"/>

<!-- Android 13+ notifications -->
<uses-permission android:name="android.permission.POST_NOTIFICATIONS"/>
```

### Runtime Permission Flow (Kotlin)

```kotlin
// In your Composable or Fragment
val locationPermissionLauncher = rememberLauncherForActivityResult(
    ActivityResultContracts.RequestMultiplePermissions()
) { permissions ->
    val fineGranted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true
    if (fineGranted) startWifiScan() else showPermissionRationale()
}

// Request
locationPermissionLauncher.launch(arrayOf(
    Manifest.permission.ACCESS_FINE_LOCATION,
    Manifest.permission.ACCESS_COARSE_LOCATION
))
```

### Important: Android Version Gotchas

| Android API | Gotcha |
|-------------|--------|
| API 26+ | `WifiManager.startScan()` throttled to 4 scans/2min per app |
| API 28+ | Must have Location permission to read `scanResults` |
| API 29+ | Cannot read WiFi SSID without fine location permission |
| API 30+ | Background location requires separate permission dialog |
| API 33+ | Must request `POST_NOTIFICATIONS` at runtime |
| API 34+ | New `FOREGROUND_SERVICE_CONNECTED_DEVICE` permission type needed |

---

*Last updated: 2026. All APIs verified against Android 14 (API 34).*
*This document is the single source of truth for building the app — keep it updated as APIs evolve.*

---

## 8. UI Design System

> **UI/UX Guideline 1:** Simple **white color theme** and **soft colors** only.  
> Theme philosophy: **White. Clean. Calm. Simple.**  
> Target users include non-tech people. Every screen should feel like a health app — not a terminal.

### 8.1 Color Palette

```kotlin
// NetPulseTheme.kt
object NetPulseColors {
    // Backgrounds
    val White         = Color(0xFFFFFFFF)   // Primary background
    val OffWhite      = Color(0xFFF7F9FC)   // Card / surface background
    val LightGray     = Color(0xFFF0F3F7)   // Dividers, chips, inactive tabs

    // Accent — soft, calming
    val SoftBlue      = Color(0xFF5B8DEF)   // Primary action, links, icons
    val LightBlue     = Color(0xFFE8EFFD)   // Button backgrounds, tags
    val SoftGreen     = Color(0xFF4CAF82)   // Good signal, open, success
    val SoftAmber     = Color(0xFFFFB547)   // Warning, moderate signal
    val SoftRed       = Color(0xFFEF5B5B)   // Poor signal, error, danger
    val LavenderPurple= Color(0xFF9B8DEF)   // Upload speed accent

    // Text
    val TextPrimary   = Color(0xFF1A1D23)   // Headlines, values
    val TextSecondary = Color(0xFF6B7280)   // Labels, subtitles
    val TextHint      = Color(0xFFB0B8C4)   // Placeholder, empty state

    // Signal quality shortcuts (use in all signal indicators)
    fun signalColor(quality: Int) = when {
        quality >= 70 -> SoftGreen
        quality >= 40 -> SoftAmber
        else          -> SoftRed
    }
}
```

### 8.2 Typography

```kotlin
// Type.kt
val NetPulseTypography = Typography(
    // Big numbers on speed test, ping, signal
    displayLarge = TextStyle(
        fontFamily = FontFamily(Font(R.font.inter_bold)),
        fontSize = 56.sp,
        fontWeight = FontWeight.Bold,
        color = NetPulseColors.TextPrimary
    ),
    // Screen titles
    headlineMedium = TextStyle(
        fontFamily = FontFamily(Font(R.font.inter_semibold)),
        fontSize = 22.sp,
        fontWeight = FontWeight.SemiBold,
        color = NetPulseColors.TextPrimary
    ),
    // Card titles, section headers
    titleMedium = TextStyle(
        fontFamily = FontFamily(Font(R.font.inter_medium)),
        fontSize = 15.sp,
        fontWeight = FontWeight.Medium,
        color = NetPulseColors.TextPrimary
    ),
    // Body copy
    bodyMedium = TextStyle(
        fontFamily = FontFamily(Font(R.font.inter_regular)),
        fontSize = 14.sp,
        color = NetPulseColors.TextSecondary
    ),
    // Labels, chips, small tags
    labelSmall = TextStyle(
        fontFamily = FontFamily(Font(R.font.inter_medium)),
        fontSize = 11.sp,
        letterSpacing = 0.5.sp,
        color = NetPulseColors.TextHint
    )
)
```

### 8.3 Shared Component Library

```kotlin
// ── NetPulseCard ───────────────────────────────────────────
@Composable
fun NetPulseCard(
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = NetPulseColors.OffWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp), // flat, no shadow
        border = BorderStroke(1.dp, NetPulseColors.LightGray)
    ) {
        Column(modifier = Modifier.padding(20.dp), content = content)
    }
}

// ── SignalStrengthBar ───────────────────────────────────────
// 5 vertical bars like phone signal icon — non-tech friendly
@Composable
fun SignalStrengthBar(quality: Int, modifier: Modifier = Modifier) {
    val color = NetPulseColors.signalColor(quality)
    val filledBars = when {
        quality >= 80 -> 5
        quality >= 60 -> 4
        quality >= 40 -> 3
        quality >= 20 -> 2
        else          -> 1
    }
    Row(modifier = modifier, horizontalArrangement = Arrangement.spacedBy(3.dp), verticalAlignment = Alignment.Bottom) {
        (1..5).forEach { bar ->
            Box(
                modifier = Modifier
                    .width(5.dp)
                    .height((bar * 5 + 5).dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(if (bar <= filledBars) color else NetPulseColors.LightGray)
            )
        }
    }
}

// ── StatusChip ─────────────────────────────────────────────
@Composable
fun StatusChip(label: String, color: Color, backgroundColor: Color) {
    Surface(
        shape = RoundedCornerShape(20.dp),
        color = backgroundColor
    ) {
        Text(
            text = label,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
            style = MaterialTheme.typography.labelSmall,
            color = color
        )
    }
}

// ── SectionHeader ──────────────────────────────────────────
@Composable
fun SectionHeader(title: String, subtitle: String? = null) {
    Column(modifier = Modifier.padding(bottom = 12.dp)) {
        Text(title, style = MaterialTheme.typography.headlineMedium)
        if (subtitle != null)
            Text(subtitle, style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(top = 2.dp))
    }
}

// ── PrimaryButton ──────────────────────────────────────────
@Composable
fun NetPulseButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    isLoading: Boolean = false,
    icon: ImageVector? = null
) {
    Button(
        onClick = onClick,
        modifier = modifier.height(52.dp),
        shape = RoundedCornerShape(14.dp),
        colors = ButtonDefaults.buttonColors(containerColor = NetPulseColors.SoftBlue),
        enabled = !isLoading
    ) {
        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.size(20.dp),
                color = Color.White, strokeWidth = 2.dp
            )
        } else {
            if (icon != null) Icon(icon, null, modifier = Modifier.size(18.dp))
            Spacer(Modifier.width(6.dp))
            Text(text, style = MaterialTheme.typography.titleMedium, color = Color.White)
        }
    }
}

// ── MetricTile ─────────────────────────────────────────────
// A single metric: "23 ms" with label "Ping" — used on speed test result
@Composable
fun MetricTile(value: String, unit: String, label: String, color: Color = NetPulseColors.SoftBlue) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Row(verticalAlignment = Alignment.Bottom) {
            Text(value, style = MaterialTheme.typography.displayLarge.copy(color = color))
            Spacer(Modifier.width(4.dp))
            Text(unit, style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(bottom = 10.dp))
        }
        Text(label.uppercase(), style = MaterialTheme.typography.labelSmall)
    }
}
```

### 8.4 Bottom Navigation

```kotlin
// 5 tabs — simple icons, no text overload
enum class NavTab(val icon: ImageVector, val label: String, val route: String) {
    HOME     (Icons.Outlined.Home,           "Home",     "home"),
    WIFI     (Icons.Outlined.Wifi,           "WiFi",     "wifi_scan"),
    SPEED    (Icons.Outlined.Speed,          "Speed",    "speed_test"),
    DEVICES  (Icons.Outlined.DevicesOther,   "Devices",  "devices"),
    TOOLS    (Icons.Outlined.Build,          "Tools",    "tools")
}

@Composable
fun NetPulseBottomNav(currentRoute: String, onTabSelected: (NavTab) -> Unit) {
    NavigationBar(
        containerColor = NetPulseColors.White,
        tonalElevation = 0.dp
    ) {
        NavTab.values().forEach { tab ->
            val selected = currentRoute == tab.route
            NavigationBarItem(
                selected = selected,
                onClick = { onTabSelected(tab) },
                icon = { Icon(tab.icon, contentDescription = tab.label) },
                label = { Text(tab.label, style = MaterialTheme.typography.labelSmall) },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = NetPulseColors.SoftBlue,
                    selectedTextColor = NetPulseColors.SoftBlue,
                    unselectedIconColor = NetPulseColors.TextHint,
                    indicatorColor = NetPulseColors.LightBlue
                )
            )
        }
    }
}
```

---

## 9. Jetpack Compose Screens

> **UI/UX Guideline 4 (MVVM):** Every screen follows the same pattern: **ViewModel emits StateFlow → Composable collects → renders UI**. No business logic in Composables.  
> No login. App launches directly to Home.

---

### SCREEN 1 — Home Screen (`HomeScreen.kt`)

**Purpose:** Single-glance network health summary. The user should understand their connection in 3 seconds.

```
┌──────────────────────────────────┐
│  Good Morning 👋                 │  ← friendly, human
│  Your network is healthy         │  ← plain English status
│                                  │
│  ┌─────────────────────────────┐ │
│  │ 🔵 Connected to "MyWiFi"   │ │  ← current network card
│  │    ▂▄▆█ Excellent  -52 dBm │ │
│  │    5 GHz · WPA3            │ │
│  └─────────────────────────────┘ │
│                                  │
│  ┌──────────┐  ┌──────────┐      │
│  │ ⬇ 98.4  │  │ ⬆ 41.2  │      │  ← last speed test
│  │   Mbps   │  │   Mbps   │      │
│  └──────────┘  └──────────┘      │
│                                  │
│  ┌─────────────────────────────┐ │
│  │ Quick Actions               │ │
│  │  [Run Speed Test]           │ │
│  │  [Scan WiFi]  [Ping]        │ │
│  └─────────────────────────────┘ │
│                                  │
│  ● 6 devices on this network     │  ← device count summary
└──────────────────────────────────┘
```

```kotlin
@Composable
fun HomeScreen(viewModel: HomeViewModel = hiltViewModel()) {
    val state by viewModel.state.collectAsState()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(NetPulseColors.White)
            .padding(horizontal = 20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item { Spacer(Modifier.height(16.dp)) }

        // Greeting
        item {
            Text("Your Network", style = MaterialTheme.typography.headlineMedium)
            Text(
                state.networkStatusMessage, // "Everything looks good" / "Slow connection detected"
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(top = 4.dp)
            )
        }

        // Current connection card
        item {
            NetPulseCard {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Outlined.Wifi, null,
                        tint = NetPulseColors.signalColor(state.signalQuality),
                        modifier = Modifier.size(32.dp))
                    Spacer(Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(state.ssid, style = MaterialTheme.typography.titleMedium)
                        Text("${state.band} · ${state.security}",
                            style = MaterialTheme.typography.bodyMedium)
                    }
                    SignalStrengthBar(state.signalQuality)
                }
                Spacer(Modifier.height(12.dp))
                // IP / Gateway row
                Row(horizontalArrangement = Arrangement.spacedBy(24.dp)) {
                    LabeledValue("IP", state.localIp)
                    LabeledValue("Gateway", state.gateway)
                    LabeledValue("DNS", state.dns)
                }
            }
        }

        // Last speed test summary
        item {
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                SpeedMiniCard("Download", state.lastDownload, "Mbps",
                    NetPulseColors.SoftBlue, Modifier.weight(1f))
                SpeedMiniCard("Upload", state.lastUpload, "Mbps",
                    NetPulseColors.LavenderPurple, Modifier.weight(1f))
            }
        }

        // Quick actions
        item {
            NetPulseCard {
                Text("Quick Actions", style = MaterialTheme.typography.titleMedium)
                Spacer(Modifier.height(12.dp))
                NetPulseButton("Run Speed Test", onClick = { /* navigate */ },
                    modifier = Modifier.fillMaxWidth(),
                    icon = Icons.Outlined.Speed)
                Spacer(Modifier.height(8.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedButton(onClick = { /* navigate */ }, modifier = Modifier.weight(1f)) {
                        Text("Scan WiFi")
                    }
                    OutlinedButton(onClick = { /* navigate */ }, modifier = Modifier.weight(1f)) {
                        Text("Ping Test")
                    }
                }
            }
        }

        // Device count
        item {
            NetPulseCard {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Outlined.DevicesOther, null, tint = NetPulseColors.SoftBlue)
                    Spacer(Modifier.width(10.dp))
                    Text("${state.deviceCount} devices on this network",
                        style = MaterialTheme.typography.titleMedium)
                    Spacer(Modifier.weight(1f))
                    TextButton(onClick = { /* navigate to devices */ }) { Text("See all") }
                }
            }
        }

        item { Spacer(Modifier.height(80.dp)) } // bottom nav clearance
    }
}

// HomeUiState
data class HomeUiState(
    val ssid: String = "Not connected",
    val signalQuality: Int = 0,
    val band: String = "",
    val security: String = "",
    val localIp: String = "-",
    val gateway: String = "-",
    val dns: String = "-",
    val networkStatusMessage: String = "Checking...",
    val lastDownload: String = "--",
    val lastUpload: String = "--",
    val deviceCount: Int = 0
)
```

---

### SCREEN 2 — WiFi Scanner Screen (`WifiScanScreen.kt`)

**Purpose:** Show all nearby WiFi networks ranked by signal strength. Non-tech users should immediately understand which networks are strong vs. weak.

```
┌──────────────────────────────────┐
│  ← WiFi Networks                │
│  14 networks found               │
│                          [Scan ↻]│
│                                  │
│  YOUR NETWORK                    │
│  ┌─────────────────────────────┐ │
│  │ ✓ MyWiFi         ████ 92%  │ │  ← connected, green
│  │   5 GHz · Ch 36 · WPA3     │ │
│  └─────────────────────────────┘ │
│                                  │
│  NEARBY NETWORKS                 │
│  ┌─────────────────────────────┐ │
│  │ Neighbor_5G      ███░ 71%  │ │  ← amber
│  │   5 GHz · Ch 149 · WPA2    │ │
│  ├─────────────────────────────┤ │
│  │ AndroidAP        █░░░ 28%  │ │  ← red
│  │   2.4 GHz · Ch 6 · Open ⚠ │ │
│  └─────────────────────────────┘ │
│                                  │
│  CHANNEL CONGESTION              │
│  Ch 6  ████████ 4 networks       │  ← visual bar chart
│  Ch 11 ███░░░░░ 2 networks       │
│  Ch 36 █░░░░░░░ 1 network ✓best │
└──────────────────────────────────┘
```

```kotlin
@Composable
fun WifiScanScreen(viewModel: WifiScanViewModel = hiltViewModel()) {
    val state by viewModel.state.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(NetPulseColors.White)
    ) {
        // Top bar with scan button
        TopBar(
            title = "WiFi Networks",
            subtitle = if (state.isScanning) "Scanning..." else "${state.networks.size} networks found",
            action = {
                IconButton(onClick = viewModel::startScan, enabled = !state.isScanning) {
                    if (state.isScanning)
                        CircularProgressIndicator(Modifier.size(20.dp), strokeWidth = 2.dp)
                    else
                        Icon(Icons.Outlined.Refresh, "Scan")
                }
            }
        )

        LazyColumn(
            contentPadding = PaddingValues(horizontal = 20.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Connected network at top
            state.connectedNetwork?.let { network ->
                item { SectionLabel("Your Network") }
                item { WifiNetworkCard(network, isConnected = true) }
            }

            // Other networks
            item { SectionLabel("Nearby Networks") }
            items(state.otherNetworks, key = { it.bssid }) { network ->
                WifiNetworkCard(network, isConnected = false)
            }

            // Channel congestion chart
            item { SectionLabel("Channel Congestion") }
            item { ChannelCongestionChart(state.channelCongestion) }
        }
    }
}

@Composable
fun WifiNetworkCard(network: WifiNetwork, isConnected: Boolean) {
    val signalColor = NetPulseColors.signalColor(network.signalQuality)
    val qualityLabel = when {
        network.signalQuality >= 70 -> "Strong"
        network.signalQuality >= 40 -> "Fair"
        else -> "Weak"
    }

    NetPulseCard {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(network.ssid, style = MaterialTheme.typography.titleMedium)
                    if (isConnected) {
                        Spacer(Modifier.width(6.dp))
                        StatusChip("Connected", NetPulseColors.SoftGreen, Color(0xFFE8F7F0))
                    }
                    if (network.security == "Open") {
                        Spacer(Modifier.width(6.dp))
                        StatusChip("⚠ Open", NetPulseColors.SoftAmber, Color(0xFFFFF3E0))
                    }
                }
                Spacer(Modifier.height(4.dp))
                Text("${network.band} · Ch ${network.channel} · ${network.security}",
                    style = MaterialTheme.typography.bodyMedium)
            }
            Column(horizontalAlignment = Alignment.End) {
                SignalStrengthBar(network.signalQuality)
                Spacer(Modifier.height(4.dp))
                Text(qualityLabel, style = MaterialTheme.typography.labelSmall,
                    color = signalColor)
            }
        }
    }
}
```

---

### SCREEN 3 — Speed Test Screen (`SpeedTestScreen.kt`)

**Purpose:** Run a clean, honest speed test. Big numbers. Simple verdict. No noise.

```
┌──────────────────────────────────┐
│  Speed Test                      │
│  Test your internet speed        │
│                                  │
│         ┌───────────┐            │
│         │   98.4    │ ← big num  │
│         │   Mbps    │            │
│         │  Download │            │
│         └───────────┘            │
│                                  │
│   ┌───────────┬───────────┐      │
│   │  23 ms   │  ±4 ms    │      │
│   │   Ping   │  Jitter   │      │
│   ├───────────┼───────────┤      │
│   │  41.2    │   0%      │      │
│   │  Upload  │ Pkt Loss  │      │
│   └───────────┴───────────┘      │
│                                  │
│  📶 WiFi · Jio Fiber             │
│  ✅ Great — you're good to go    │  ← plain English verdict
│                                  │
│    [     Run Speed Test     ]    │
│                                  │
│  ─── History ──────────────────  │
│  Today 3:14 PM   ⬇94  ⬆39  23ms │
│  Today 10:20 AM  ⬇88  ⬆37  28ms │
└──────────────────────────────────┘
```

```kotlin
@Composable
fun SpeedTestScreen(viewModel: SpeedTestViewModel = hiltViewModel()) {
    val state by viewModel.state.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(NetPulseColors.White)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp)
    ) {
        Spacer(Modifier.height(16.dp))
        SectionHeader("Speed Test", "Test your internet connection")

        Spacer(Modifier.height(24.dp))

        // Big animated speed number
        SpeedGauge(
            value = state.currentSpeedMbps,
            phase = state.phase,    // IDLE, PINGING, DOWNLOADING, UPLOADING, DONE
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )

        Spacer(Modifier.height(32.dp))

        // Result metrics 2x2 grid
        if (state.result != null) {
            val result = state.result!!
            ResultMetricsGrid(result)
            Spacer(Modifier.height(16.dp))
            // Verdict card — plain English
            VerdictCard(result)
        }

        Spacer(Modifier.height(24.dp))

        NetPulseButton(
            text = if (state.isRunning) "Running..." else "Run Speed Test",
            isLoading = state.isRunning,
            onClick = viewModel::startTest,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(32.dp))

        // History list
        if (state.history.isNotEmpty()) {
            Text("History", style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(8.dp))
            state.history.forEach { SpeedHistoryRow(it) }
        }

        Spacer(Modifier.height(80.dp))
    }
}

@Composable
fun SpeedGauge(value: Double, phase: TestPhase, modifier: Modifier = Modifier) {
    val animatedValue by animateFloatAsState(
        targetValue = value.toFloat(),
        animationSpec = tween(300), label = "speed"
    )
    Column(modifier = modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = if (value > 0) "%.1f".format(animatedValue) else "--",
            style = MaterialTheme.typography.displayLarge.copy(fontSize = 64.sp,
                color = NetPulseColors.SoftBlue)
        )
        Text("Mbps", style = MaterialTheme.typography.bodyMedium)
        Spacer(Modifier.height(4.dp))
        Text(
            text = when (phase) {
                TestPhase.IDLE -> "Ready"
                TestPhase.PINGING -> "Measuring ping..."
                TestPhase.DOWNLOADING -> "↓ Download"
                TestPhase.UPLOADING -> "↑ Upload"
                TestPhase.DONE -> "Complete"
            },
            style = MaterialTheme.typography.labelSmall
        )
    }
}

@Composable
fun VerdictCard(result: SpeedTestResult) {
    // Plain English — non-tech users love this
    val (emoji, verdict, color) = when {
        result.downloadMbps >= 50 -> Triple("🚀", "Great — fast enough for streaming, gaming, and video calls", NetPulseColors.SoftGreen)
        result.downloadMbps >= 25 -> Triple("✅", "Good — enough for most everyday tasks", NetPulseColors.SoftGreen)
        result.downloadMbps >= 10 -> Triple("⚡", "Fair — OK for browsing, may struggle with HD video", NetPulseColors.SoftAmber)
        else                      -> Triple("🐌", "Slow — try moving closer to your router", NetPulseColors.SoftRed)
    }
    NetPulseCard {
        Row(verticalAlignment = Alignment.Top) {
            Text(emoji, fontSize = 24.sp)
            Spacer(Modifier.width(12.dp))
            Text(verdict, style = MaterialTheme.typography.bodyMedium.copy(color = color))
        }
    }
}

enum class TestPhase { IDLE, PINGING, DOWNLOADING, UPLOADING, DONE }

data class SpeedTestUiState(
    val isRunning: Boolean = false,
    val phase: TestPhase = TestPhase.IDLE,
    val currentSpeedMbps: Double = 0.0,
    val result: SpeedTestResult? = null,
    val history: List<SpeedTestResult> = emptyList()
)
```

---

### SCREEN 4 — Device Discovery Screen (`DeviceListScreen.kt`)

**Purpose:** Show who's on the network. Simple list. Flag unknown devices visually.

```
┌──────────────────────────────────┐
│  Devices on Network              │
│  6 found on 192.168.1.0/24      │
│                       [Scan ↻]  │
│                                  │
│  ┌─────────────────────────────┐ │
│  │ 📱 Your Phone     ← badge  │ │
│  │    192.168.1.5              │ │
│  │    Xiaomi · A4:3C:23:...   │ │
│  ├─────────────────────────────┤ │
│  │ 🖥 DESKTOP-PC               │ │
│  │    192.168.1.2              │ │
│  │    Intel · 3C:A0:67:...    │ │
│  ├─────────────────────────────┤ │
│  │ ❓ Unknown Device     ⚠    │ │  ← highlighted
│  │    192.168.1.9              │ │
│  │    Unknown manufacturer     │ │
│  └─────────────────────────────┘ │
└──────────────────────────────────┘
```

```kotlin
@Composable
fun DeviceListScreen(viewModel: DeviceDiscoveryViewModel = hiltViewModel()) {
    val state by viewModel.state.collectAsState()

    Column(Modifier.fillMaxSize().background(NetPulseColors.White)) {
        TopBar(
            title = "Devices on Network",
            subtitle = "${state.devices.size} found · ${state.subnet}",
            action = {
                IconButton(onClick = viewModel::startScan, enabled = !state.isScanning) {
                    if (state.isScanning)
                        CircularProgressIndicator(Modifier.size(20.dp), strokeWidth = 2.dp)
                    else Icon(Icons.Outlined.Refresh, "Scan")
                }
            }
        )

        // Progress bar during scan
        if (state.isScanning) {
            LinearProgressIndicator(
                progress = state.scanProgress,
                modifier = Modifier.fillMaxWidth().height(2.dp),
                color = NetPulseColors.SoftBlue,
                trackColor = NetPulseColors.LightGray
            )
        }

        LazyColumn(
            contentPadding = PaddingValues(horizontal = 20.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(state.devices, key = { it.ipAddress }) { device ->
                DeviceCard(device)
            }
        }
    }
}

@Composable
fun DeviceCard(device: NetworkDevice) {
    val isUnknown = device.manufacturer == null || device.manufacturer == "Unknown"
    val borderColor = if (isUnknown) NetPulseColors.SoftAmber else NetPulseColors.LightGray

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = NetPulseColors.OffWhite),
        border = BorderStroke(1.dp, borderColor)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Device type icon
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(NetPulseColors.LightBlue),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = device.deviceType.toIcon(),
                    contentDescription = null,
                    tint = NetPulseColors.SoftBlue
                )
            }
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(device.hostname ?: device.ipAddress,
                        style = MaterialTheme.typography.titleMedium)
                    if (device.isCurrentDevice) {
                        Spacer(Modifier.width(6.dp))
                        StatusChip("You", NetPulseColors.SoftBlue, NetPulseColors.LightBlue)
                    }
                }
                Text(device.ipAddress, style = MaterialTheme.typography.bodyMedium)
                Text(device.manufacturer ?: "Unknown manufacturer",
                    style = MaterialTheme.typography.bodyMedium)
            }
            if (isUnknown && !device.isCurrentDevice) {
                Icon(Icons.Outlined.Warning, "Unknown",
                    tint = NetPulseColors.SoftAmber, modifier = Modifier.size(20.dp))
            }
        }
    }
}

fun DeviceType.toIcon() = when (this) {
    DeviceType.PHONE   -> Icons.Outlined.PhoneAndroid
    DeviceType.LAPTOP  -> Icons.Outlined.LaptopMac
    DeviceType.TABLET  -> Icons.Outlined.TabletAndroid
    DeviceType.ROUTER  -> Icons.Outlined.Router
    DeviceType.SMART_TV-> Icons.Outlined.Tv
    DeviceType.IOT     -> Icons.Outlined.DeviceHub
    DeviceType.UNKNOWN -> Icons.Outlined.DeviceUnknown
}
```

---

### SCREEN 5 — Signal Map Screen (`SignalMapScreen.kt`)

**Purpose:** Walk around your space and see where signal is strong or dead.

```kotlin
@Composable
fun SignalMapScreen(viewModel: SignalMapViewModel = hiltViewModel()) {
    val state by viewModel.state.collectAsState()

    Box(modifier = Modifier.fillMaxSize()) {
        // Google Maps fills the screen
        GoogleMap(modifier = Modifier.fillMaxSize()) {
            // Heatmap overlay applied via TileOverlay (see LLD Section 3)
        }

        // Floating top card
        NetPulseCard(
            modifier = Modifier
                .padding(16.dp)
                .align(Alignment.TopCenter)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                SignalStrengthBar(state.currentQuality)
                Spacer(Modifier.width(10.dp))
                Column {
                    Text("${state.currentRssi} dBm",
                        style = MaterialTheme.typography.titleMedium)
                    Text(state.ssid, style = MaterialTheme.typography.bodyMedium)
                }
                Spacer(Modifier.weight(1f))
                Text("${state.pointsRecorded} pts",
                    style = MaterialTheme.typography.labelSmall)
            }
        }

        // Record / Stop FAB
        ExtendedFloatingActionButton(
            onClick = if (state.isRecording) viewModel::stopRecording else viewModel::startRecording,
            modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 24.dp),
            containerColor = if (state.isRecording) NetPulseColors.SoftRed else NetPulseColors.SoftBlue,
            contentColor = Color.White
        ) {
            Icon(if (state.isRecording) Icons.Outlined.Stop else Icons.Outlined.FiberManualRecord, null)
            Spacer(Modifier.width(8.dp))
            Text(if (state.isRecording) "Stop Recording" else "Start Recording")
        }
    }
}
```

---

### SCREEN 6 — Tools Screen (`ToolsScreen.kt` → Ping + Traceroute + Port Scanner)

**Purpose:** Hub for advanced tools. Non-tech users see a clean menu; tech users get powerful tools.

```kotlin
@Composable
fun ToolsScreen(onNavigate: (String) -> Unit) {
    Column(
        Modifier
            .fillMaxSize()
            .background(NetPulseColors.White)
            .padding(horizontal = 20.dp)
    ) {
        Spacer(Modifier.height(16.dp))
        SectionHeader("Tools", "Network diagnostics")
        Spacer(Modifier.height(16.dp))

        ToolCard(
            icon = Icons.Outlined.PingInProgress,
            title = "Ping Test",
            subtitle = "Check if a host is reachable and how fast",
            onClick = { onNavigate("ping") }
        )
        Spacer(Modifier.height(10.dp))
        ToolCard(
            icon = Icons.Outlined.Route,
            title = "Traceroute",
            subtitle = "See every network hop to a destination",
            onClick = { onNavigate("traceroute") }
        )
        Spacer(Modifier.height(10.dp))
        ToolCard(
            icon = Icons.Outlined.Security,
            title = "Port Scanner",
            subtitle = "Find open ports on any device",
            onClick = { onNavigate("port_scan") }
        )
        Spacer(Modifier.height(10.dp))
        ToolCard(
            icon = Icons.Outlined.Map,
            title = "Signal Map",
            subtitle = "Walk and record WiFi coverage",
            onClick = { onNavigate("signal_map") }
        )
    }
}

@Composable
fun ToolCard(icon: ImageVector, title: String, subtitle: String, onClick: () -> Unit) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = NetPulseColors.OffWhite),
        border = BorderStroke(1.dp, NetPulseColors.LightGray)
    ) {
        Row(
            modifier = Modifier.padding(18.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(NetPulseColors.LightBlue),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, null, tint = NetPulseColors.SoftBlue)
            }
            Spacer(Modifier.width(14.dp))
            Column(Modifier.weight(1f)) {
                Text(title, style = MaterialTheme.typography.titleMedium)
                Text(subtitle, style = MaterialTheme.typography.bodyMedium)
            }
            Icon(Icons.Outlined.ChevronRight, null, tint = NetPulseColors.TextHint)
        }
    }
}
```

---

### SCREEN 7 — Ping Screen (`PingScreen.kt`)

```kotlin
@Composable
fun PingScreen(viewModel: PingViewModel = hiltViewModel()) {
    val state by viewModel.state.collectAsState()

    Column(
        Modifier
            .fillMaxSize()
            .background(NetPulseColors.White)
            .padding(horizontal = 20.dp)
    ) {
        Spacer(Modifier.height(16.dp))
        SectionHeader("Ping Test", "Check connection to any address")
        Spacer(Modifier.height(16.dp))

        // Host input
        OutlinedTextField(
            value = state.host,
            onValueChange = viewModel::setHost,
            label = { Text("Hostname or IP") },
            placeholder = { Text("e.g. google.com or 8.8.8.8") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            singleLine = true
        )
        Spacer(Modifier.height(12.dp))
        NetPulseButton(
            text = if (state.isRunning) "Pinging..." else "Start Ping",
            isLoading = state.isRunning,
            onClick = viewModel::startPing,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(24.dp))

        // Live results
        if (state.result != null) {
            val r = state.result!!
            Row(horizontalArrangement = Arrangement.SpaceEvenly, modifier = Modifier.fillMaxWidth()) {
                MetricTile("${r.minMs}", "ms", "Min", NetPulseColors.SoftGreen)
                MetricTile("${r.avgMs}", "ms", "Avg", NetPulseColors.SoftBlue)
                MetricTile("${r.maxMs}", "ms", "Max", NetPulseColors.SoftAmber)
                MetricTile("${r.packetLoss}%", "", "Loss",
                    if (r.packetLoss == 0) NetPulseColors.SoftGreen else NetPulseColors.SoftRed)
            }

            Spacer(Modifier.height(24.dp))

            // RTT line chart (see Section 12)
            PingLineChart(packets = r.packets, modifier = Modifier.fillMaxWidth().height(160.dp))
        }
    }
}
```

---

## 10. Dagger 2 Dependency Injection

> Using **Dagger 2** (not Hilt). Clean component/module hierarchy. All singletons live in `AppComponent`.

### 10.1 AppComponent

```kotlin
// AppComponent.kt
@Singleton
@Component(
    modules = [
        AppModule::class,
        NetworkModule::class,
        DatabaseModule::class,
        WifiModule::class,
        LocationModule::class,
        RepositoryModule::class
    ]
)
interface AppComponent {
    fun inject(app: NetPulseApp)
    fun inject(activity: MainActivity)

    // Feature component factories
    fun wifiComponentFactory(): WifiComponent.Factory
    fun speedTestComponentFactory(): SpeedTestComponent.Factory
    fun deviceComponentFactory(): DeviceComponent.Factory
    fun pingComponentFactory(): PingComponent.Factory
}
```

### 10.2 AppModule

```kotlin
@Module
class AppModule(private val app: Application) {

    @Provides
    @Singleton
    fun provideApplication(): Application = app

    @Provides
    @Singleton
    fun provideContext(): Context = app.applicationContext

    @Provides
    @Singleton
    fun provideCoroutineScope(): CoroutineScope =
        CoroutineScope(SupervisorJob() + Dispatchers.Default)
}
```

### 10.3 NetworkModule

```kotlin
@Module
class NetworkModule {

    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient =
        OkHttpClient.Builder()
            .connectTimeout(10, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS)
            .addInterceptor(HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BASIC
            })
            .build()

    @Provides
    @Singleton
    fun provideNetworkMonitor(
        context: Context,
        scope: CoroutineScope
    ): NetworkMonitor = NetworkMonitor(context, scope)
}
```

### 10.4 DatabaseModule

```kotlin
@Module
class DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(context: Context): NetPulseDatabase =
        Room.databaseBuilder(context, NetPulseDatabase::class.java, "netpulse.db")
            .fallbackToDestructiveMigration()
            .build()

    @Provides fun provideWifiDao(db: NetPulseDatabase): WifiDao = db.wifiDao()
    @Provides fun provideSpeedTestDao(db: NetPulseDatabase): SpeedTestDao = db.speedTestDao()
    @Provides fun provideDeviceDao(db: NetPulseDatabase): DeviceDao = db.deviceDao()
    @Provides fun provideSignalPointDao(db: NetPulseDatabase): SignalPointDao = db.signalPointDao()
}
```

### 10.5 WifiModule

```kotlin
@Module
class WifiModule {

    @Provides
    @Singleton
    fun provideWifiManager(context: Context): WifiManager =
        context.getSystemService(WifiManager::class.java)

    @Provides
    @Singleton
    fun provideConnectivityManager(context: Context): ConnectivityManager =
        context.getSystemService(ConnectivityManager::class.java)

    @Provides
    @Singleton
    fun provideTelephonyManager(context: Context): TelephonyManager =
        context.getSystemService(TelephonyManager::class.java)
}
```

### 10.6 LocationModule

```kotlin
@Module
class LocationModule {

    @Provides
    @Singleton
    fun provideFusedLocationClient(context: Context): FusedLocationProviderClient =
        LocationServices.getFusedLocationProviderClient(context)
}
```

### 10.7 RepositoryModule

```kotlin
@Module
class RepositoryModule {

    @Provides
    @Singleton
    fun provideWifiRepository(
        wifiManager: WifiManager,
        context: Context,
        wifiDao: WifiDao
    ): WifiRepository = WifiRepositoryImpl(wifiManager, context, wifiDao)

    @Provides
    @Singleton
    fun provideSpeedTestRepository(
        okHttpClient: OkHttpClient,
        speedTestDao: SpeedTestDao
    ): SpeedTestRepository = SpeedTestRepositoryImpl(okHttpClient, speedTestDao)

    @Provides
    @Singleton
    fun provideDeviceRepository(
        connectivityManager: ConnectivityManager,
        deviceDao: DeviceDao,
        context: Context
    ): DeviceRepository = DeviceRepositoryImpl(connectivityManager, deviceDao, context)

    @Provides
    @Singleton
    fun provideSignalRepository(
        fusedLocationClient: FusedLocationProviderClient,
        wifiManager: WifiManager,
        signalPointDao: SignalPointDao
    ): SignalRepository = SignalRepositoryImpl(fusedLocationClient, wifiManager, signalPointDao)
}
```

### 10.8 Feature Subcomponents

```kotlin
// Feature-scoped components — created and destroyed with the screen lifecycle

@FeatureScope
@Subcomponent(modules = [WifiFeatureModule::class])
interface WifiComponent {
    fun inject(viewModel: WifiScanViewModel)

    @Subcomponent.Factory
    interface Factory {
        fun create(): WifiComponent
    }
}

@FeatureScope
@Subcomponent(modules = [SpeedTestFeatureModule::class])
interface SpeedTestComponent {
    fun inject(viewModel: SpeedTestViewModel)

    @Subcomponent.Factory
    interface Factory {
        fun create(): SpeedTestComponent
    }
}

// Custom scope for feature components
@Scope
@Retention(AnnotationRetention.RUNTIME)
annotation class FeatureScope
```

### 10.9 ViewModelFactory (Dagger-compatible)

```kotlin
// Since Dagger 2 doesn't auto-inject ViewModels like Hilt, use a factory
class ViewModelFactory @Inject constructor(
    private val creators: Map<Class<out ViewModel>, @JvmSuppressWildcards Provider<ViewModel>>
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        val creator = creators[modelClass]
            ?: creators.entries.firstOrNull { modelClass.isAssignableFrom(it.key) }?.value
            ?: throw IllegalArgumentException("Unknown ViewModel: $modelClass")
        return creator.get() as T
    }
}

// In each ViewModel:
class WifiScanViewModel @Inject constructor(
    private val scanWifiUseCase: ScanWifiUseCase
) : ViewModel() { ... }

// Bind in module:
@Module
abstract class WifiFeatureModule {
    @Binds
    @IntoMap
    @ViewModelKey(WifiScanViewModel::class)
    abstract fun bindWifiScanViewModel(vm: WifiScanViewModel): ViewModel
}

// ViewModelKey annotation
@MustBeDocumented
@Target(AnnotationTarget.FUNCTION, AnnotationTarget.PROPERTY_GETTER, AnnotationTarget.PROPERTY_SETTER)
@Retention(AnnotationRetention.RUNTIME)
@MapKey
annotation class ViewModelKey(val value: KClass<out ViewModel>)
```

### 10.10 Application & Activity Setup

```kotlin
// NetPulseApp.kt
class NetPulseApp : Application() {
    lateinit var appComponent: AppComponent
        private set

    override fun onCreate() {
        super.onCreate()
        appComponent = DaggerAppComponent.factory().create(this)
    }
}

// MainActivity.kt
class MainActivity : ComponentActivity() {

    @Inject lateinit var viewModelFactory: ViewModelFactory

    override fun onCreate(savedInstanceState: Bundle?) {
        (application as NetPulseApp).appComponent.inject(this)
        super.onCreate(savedInstanceState)
        setContent {
            NetPulseTheme {
                NetPulseApp(viewModelFactory)
            }
        }
    }
}
```

---

## 11. Navigation Graph

```kotlin
// NavGraph.kt — No login. Launches directly to Home.
@Composable
fun NetPulseNavGraph(
    navController: NavHostController = rememberNavController(),
    viewModelFactory: ViewModelProvider.Factory
) {
    val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route

    Scaffold(
        bottomBar = {
            // Only show bottom nav on top-level routes
            val topLevelRoutes = setOf("home", "wifi_scan", "speed_test", "devices", "tools")
            if (currentRoute in topLevelRoutes) {
                NetPulseBottomNav(currentRoute ?: "home") { tab ->
                    navController.navigate(tab.route) {
                        popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            }
        }
    ) { padding ->
        NavHost(
            navController = navController,
            startDestination = "home",
            modifier = Modifier.padding(padding)
        ) {
            composable("home") {
                HomeScreen(viewModel = viewModel(factory = viewModelFactory))
            }
            composable("wifi_scan") {
                WifiScanScreen(viewModel = viewModel(factory = viewModelFactory))
            }
            composable("speed_test") {
                SpeedTestScreen(viewModel = viewModel(factory = viewModelFactory))
            }
            composable("devices") {
                DeviceListScreen(viewModel = viewModel(factory = viewModelFactory))
            }
            composable("tools") {
                ToolsScreen(onNavigate = { navController.navigate(it) })
            }
            composable("ping") {
                PingScreen(viewModel = viewModel(factory = viewModelFactory))
            }
            composable("traceroute") {
                TracerouteScreen(viewModel = viewModel(factory = viewModelFactory))
            }
            composable("port_scan") {
                PortScanScreen(viewModel = viewModel(factory = viewModelFactory))
            }
            composable("signal_map") {
                SignalMapScreen(viewModel = viewModel(factory = viewModelFactory))
            }
        }
    }
}
```

---

## 12. Graph and Data Visualization

> **UI/UX Guideline 3:** Make **user-interactive** graphs — tap for tooltips, zoom where helpful, clear legends, smooth animations.  
> **Rule:** Every graph must be readable by a non-tech person in under 5 seconds.  
> Use **Vico** library for all charts. It is Compose-native, lightweight, and produces beautiful clean charts.

### 12.1 Library Setup

```kotlin
// build.gradle (feature module)
dependencies {
    implementation("com.patrykandpatrick.vico:compose:1.13.0")
    implementation("com.patrykandpatrick.vico:compose-m3:1.13.0")
    implementation("com.patrykandpatrick.vico:core:1.13.0")
}
```

### 12.2 Speed History Line Chart

Used on Speed Test screen to show download/upload over the last 7 tests.

```kotlin
@Composable
fun SpeedHistoryChart(
    downloadHistory: List<Float>,
    uploadHistory: List<Float>,
    modifier: Modifier = Modifier
) {
    // Build dataset: 2 lines — download (blue) and upload (purple)
    val downloadEntries = downloadHistory.mapIndexed { i, v -> entryOf(i.toFloat(), v) }
    val uploadEntries   = uploadHistory.mapIndexed   { i, v -> entryOf(i.toFloat(), v) }

    val chartEntryModel = ChartEntryModelProducer(
        listOf(downloadEntries, uploadEntries)
    ).getModel() ?: return

    // Vico line chart — soft colors, no grid noise
    Chart(
        chart = lineChart(
            lines = listOf(
                lineSpec(lineColor = NetPulseColors.SoftBlue,   lineThickness = 2.dp, pointSize = 6.dp),
                lineSpec(lineColor = NetPulseColors.LavenderPurple, lineThickness = 2.dp, pointSize = 6.dp)
            )
        ),
        model = chartEntryModel,
        modifier = modifier,
        startAxis = rememberStartAxis(
            label = rememberAxisLabelComponent(
                color = NetPulseColors.TextSecondary,
                textSize = 11.sp
            ),
            axis = null, guideline = null,  // remove grid lines — cleaner look
            itemPlacer = AxisItemPlacer.Vertical.default(maxItemCount = 4)
        ),
        bottomAxis = rememberBottomAxis(
            label = rememberAxisLabelComponent(
                color = NetPulseColors.TextSecondary,
                textSize = 11.sp
            ),
            axis = null, guideline = null
        )
    )
    // Legend (manual — simpler than Vico legend)
    Row(modifier = Modifier.padding(top = 8.dp), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
        LegendItem(color = NetPulseColors.SoftBlue, label = "Download")
        LegendItem(color = NetPulseColors.LavenderPurple, label = "Upload")
    }
}

@Composable
fun LegendItem(color: Color, label: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(Modifier.size(10.dp).clip(CircleShape).background(color))
        Spacer(Modifier.width(6.dp))
        Text(label, style = MaterialTheme.typography.labelSmall)
    }
}
```

### 12.3 Ping RTT Line Chart

Shows ping values over time. Green zone = fast, Red zone = slow.

```kotlin
@Composable
fun PingLineChart(packets: List<Long>, modifier: Modifier = Modifier) {
    if (packets.isEmpty()) return

    val entries = packets.mapIndexed { i, v -> entryOf(i.toFloat(), v.toFloat()) }
    val model = ChartEntryModelProducer(listOf(entries)).getModel() ?: return

    Chart(
        chart = lineChart(
            lines = listOf(
                lineSpec(
                    lineColor = NetPulseColors.SoftBlue,
                    lineThickness = 2.dp,
                    pointSize = 5.dp,
                    // Soft fill under the line
                    lineBackgroundShader = DynamicShaders.fromBrush(
                        Brush.verticalGradient(
                            listOf(NetPulseColors.SoftBlue.copy(alpha = 0.15f), Color.Transparent)
                        )
                    )
                )
            )
        ),
        model = model,
        modifier = modifier,
        startAxis = rememberStartAxis(
            title = "ms",
            label = rememberAxisLabelComponent(color = NetPulseColors.TextSecondary, textSize = 11.sp),
            axis = null, guideline = null
        ),
        bottomAxis = rememberBottomAxis(axis = null, guideline = null, label = null)
    )
}
```

### 12.4 Channel Congestion Bar Chart

Shows how many networks are on each WiFi channel. Bar length = number of networks.

```kotlin
@Composable
fun ChannelCongestionChart(congestion: Map<Int, Int>, modifier: Modifier = Modifier) {
    val maxCount = congestion.values.maxOrNull() ?: 1
    val sortedChannels = congestion.entries.sortedBy { it.key }

    NetPulseCard(modifier = modifier) {
        Text("Channel Congestion", style = MaterialTheme.typography.titleMedium)
        Spacer(Modifier.height(4.dp))
        Text("Fewer networks = better performance",
            style = MaterialTheme.typography.bodyMedium)
        Spacer(Modifier.height(16.dp))

        sortedChannels.forEach { (channel, count) ->
            val isBest = count == congestion.values.minOrNull()
            val barColor = when {
                count >= 4  -> NetPulseColors.SoftRed
                count >= 2  -> NetPulseColors.SoftAmber
                else        -> NetPulseColors.SoftGreen
            }
            Column(modifier = Modifier.padding(bottom = 10.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("Ch $channel", style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.width(44.dp))
                    Box(
                        modifier = Modifier
                            .weight(count.toFloat() / maxCount)
                            .height(20.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .background(barColor)
                    )
                    Spacer(Modifier.width(8.dp))
                    Text("$count network${if (count != 1) "s" else ""}",
                        style = MaterialTheme.typography.bodyMedium)
                    if (isBest) {
                        Spacer(Modifier.width(6.dp))
                        StatusChip("Best", NetPulseColors.SoftGreen, Color(0xFFE8F7F0))
                    }
                }
            }
        }
    }
}
```

### 12.5 Signal Quality Radial Indicator (Home Screen / WiFi card)

A large, friendly signal arc — like a speedometer. Non-tech users understand this instantly.

```kotlin
@Composable
fun SignalArc(quality: Int, modifier: Modifier = Modifier) {
    val color = NetPulseColors.signalColor(quality)
    val animatedSweep by animateFloatAsState(
        targetValue = quality * 1.8f, // 0–100 → 0–180 degrees
        animationSpec = tween(800, easing = FastOutSlowInEasing), label = "arc"
    )
    val label = when {
        quality >= 70 -> "Excellent"
        quality >= 40 -> "Fair"
        else          -> "Poor"
    }

    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        Canvas(modifier = Modifier.size(120.dp)) {
            val strokeWidth = 14.dp.toPx()
            val startAngle = 180f
            // Background arc
            drawArc(color = Color(0xFFEDF0F5), startAngle = startAngle, sweepAngle = 180f,
                useCenter = false, style = Stroke(strokeWidth, cap = StrokeCap.Round))
            // Foreground arc
            drawArc(color = color, startAngle = startAngle, sweepAngle = animatedSweep,
                useCenter = false, style = Stroke(strokeWidth, cap = StrokeCap.Round))
        }
        Column(horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.offset(y = 12.dp)) {
            Text("$quality%", style = MaterialTheme.typography.headlineMedium.copy(color = color))
            Text(label, style = MaterialTheme.typography.labelSmall)
        }
    }
}
```

### 12.6 Graph Design Rules (Summary)

| Rule | Why |
|------|-----|
| No grid lines | Reduces visual noise for non-tech users |
| Soft fill under line charts | Makes trends easier to read at a glance |
| Max 2 lines per chart | More lines = confusion |
| Always include a plain-English label below every graph | e.g. "Fewer networks = better" |
| Use color consistently | Green = good, Amber = fair, Red = bad — every screen |
| Animate chart entry | Feels polished, draws attention to key values |
| Avoid decimals on axes | "23 ms" not "23.47 ms" on axis labels |
| Show a verdict/summary below every chart | "Your ping is excellent" — not just raw numbers |

### 12.7 Interactive Graph Behavior (User-Interactive Graphs)

To satisfy **UI/UX Guideline 3**, every chart must support at least one form of interaction:

| Chart type | Recommended interactivity |
|------------|---------------------------|
| **Line charts** (speed, ping) | Tap/hover on a point to show tooltip with exact value (e.g. "Test 3: 45 Mbps"); optional pinch-to-zoom for time range. |
| **Bar charts** (channel congestion) | Tap a bar to highlight and show "Ch 6 — 2 networks (recommended)". |
| **Signal arc / radial** | Tap to expand or show short explanation ("Based on your current RSSI"). |
| **Maps** (signal heatmap) | Tap a region to show signal strength at that point; pan and zoom with standard map gestures. |

Implementation notes:

- Use Vico's `ChartEntryModelProducer` and compose state to drive tooltips from pointer/tap position.
- Keep tooltips in plain English (e.g. "Ping: 23 ms — Excellent") and use the same soft palette (e.g. `NetPulseColors`).
- Animate transitions (e.g. `animateFloatAsState` for arc, chart entry animations) so graphs feel responsive and engaging.

---

## 13. UX Principles

> **UI/UX Guideline 2:** Make the app **user-friendly** — plain English, one primary action per screen, friendly empty/loading states, and clear explanations. See table below.

### 13.1 Core Rules for Non-Tech User Friendliness

| Principle | Implementation |
|-----------|---------------|
| **No login** | App opens directly to Home — zero friction |
| **Plain English everywhere** | "Strong signal" not "-52 dBm". "Slow — move closer to router" not "RSSI threshold exceeded" |
| **One primary action per screen** | Home → Run Speed Test. Devices → Scan. WiFi → Scan. Never two CTAs competing |
| **Status in color + text** | Never rely on color alone. Always "Strong" + green together |
| **Friendly empty states** | "No networks found yet — tap Scan to start" with an illustration, not a blank screen |
| **Progress for everything** | Scanning takes time. Always show progress bar or animated indicator |
| **Minimal settings** | No complex configuration. Smart defaults. Power users can dig deeper |
| **Explain what it means** | Every metric has a plain-English tooltip or subtitle |

### 13.2 Permission Rationale UX

Never show a cold permission dialog. Always show a warm-up card first.

```kotlin
@Composable
fun LocationPermissionRationale(onAllow: () -> Unit, onDismiss: () -> Unit) {
    NetPulseCard {
        Row {
            Icon(Icons.Outlined.LocationOn, null,
                tint = NetPulseColors.SoftBlue, modifier = Modifier.size(28.dp))
            Spacer(Modifier.width(12.dp))
            Column {
                Text("Location needed for WiFi scan",
                    style = MaterialTheme.typography.titleMedium)
                Spacer(Modifier.height(4.dp))
                Text(
                    "Android requires location access to read nearby WiFi networks. " +
                    "We don't store your location.",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
        Spacer(Modifier.height(16.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedButton(onClick = onDismiss, modifier = Modifier.weight(1f)) {
                Text("Not now")
            }
            NetPulseButton("Allow", onClick = onAllow, modifier = Modifier.weight(1f))
        }
    }
}
```

### 13.3 Empty States

```kotlin
@Composable
fun EmptyState(
    icon: ImageVector,
    title: String,
    message: String,
    actionLabel: String? = null,
    onAction: (() -> Unit)? = null
) {
    Column(
        modifier = Modifier.fillMaxWidth().padding(40.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(72.dp)
                .clip(RoundedCornerShape(20.dp))
                .background(NetPulseColors.LightBlue),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, null, tint = NetPulseColors.SoftBlue, modifier = Modifier.size(36.dp))
        }
        Spacer(Modifier.height(16.dp))
        Text(title, style = MaterialTheme.typography.titleMedium,
            textAlign = TextAlign.Center)
        Spacer(Modifier.height(8.dp))
        Text(message, style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center, color = NetPulseColors.TextSecondary)
        if (actionLabel != null && onAction != null) {
            Spacer(Modifier.height(20.dp))
            NetPulseButton(actionLabel, onClick = onAction)
        }
    }
}
```

### 13.4 Loading States

```kotlin
// Skeleton loading — never show a spinner alone for data loading
@Composable
fun ShimmerCard(modifier: Modifier = Modifier) {
    val infiniteTransition = rememberInfiniteTransition(label = "shimmer")
    val alpha by infiniteTransition.animateFloat(
        initialValue = 0.3f, targetValue = 0.7f,
        animationSpec = infiniteRepeatable(tween(800), RepeatMode.Reverse),
        label = "alpha"
    )
    Card(
        modifier = modifier.fillMaxWidth().height(80.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = NetPulseColors.LightGray.copy(alpha = alpha))
    ) {}
}
```

### 13.5 Screen Entry Animations

```kotlin
// Consistent enter animation across all screens — subtle, not distracting
@Composable
fun AnimatedScreen(content: @Composable () -> Unit) {
    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { visible = true }
    AnimatedVisibility(
        visible = visible,
        enter = fadeIn(tween(250)) + slideInVertically(tween(250)) { it / 20 }
    ) {
        content()
    }
}
```

### 13.6 Spacing & Layout Constants

```kotlin
object Spacing {
    val xs  = 4.dp
    val sm  = 8.dp
    val md  = 16.dp
    val lg  = 24.dp
    val xl  = 32.dp
    val screenPadding = 20.dp
    val cardPadding   = 20.dp
    val bottomNavClearance = 80.dp
}
```

---

## 14. Project Plan for Implementation

This section defines a phased implementation order and a checklist of **edge cases** to handle so the app is robust across devices, permissions, and network conditions.

---

### 14.1 Implementation Phases (Order of Work)

| Phase | Scope | Deliverables | Depends On |
|-------|--------|--------------|------------|
| **P0 — Foundation** | App shell, DI, theme, navigation, permissions | Gradle multi-module setup; `:app`, `:core:ui`, `:core:network`, `:core:permissions`; `NetPulseTheme`; `NavGraph` with placeholder screens; `ViewModelFactory` + Dagger modules; permission rationale flows | — |
| **P1 — Data & persistence** | Local DB and preferences | `:data:local` (Room + DAOs + entities); `:data:preferences` (DataStore); `DatabaseModule`, `RepositoryModule` stubs | P0 |
| **P2 — Top 3 use cases (≈70% value)** | WiFi scan, Speed test, Device discovery | `:feature:wifi-scanner` (scan + channel congestion); `:feature:speed-test` (run test + history); `:feature:device-discovery` (ARP + device list); Home screen with real data | P1 |
| **P3 — Remaining features** | Ping, Port scan, Signal map, Tools | `:feature:ping`, `:feature:port-scanner`, `:feature:signal-map`; Tools screen with navigation to Ping / Port Scan / Signal Map | P2 |
| **P4 — Charts & interactivity** | All graphs + UX polish | Vico charts (speed history, ping RTT, channel congestion, signal arc); tooltips/zoom per §12.7; loading/empty states everywhere | P2, P3 |
| **P5 — Background & polish** | Monitoring, notifications, edge-case hardening | `:service:monitor` (ForegroundService + WorkManager); notification permission (API 33+); full edge-case handling (see §14.2); QA pass | P4 |

**Suggested sprint cadence:** P0 (1 sprint) → P1 (0.5–1) → P2 (2) → P3 (1.5) → P4 (1) → P5 (1). Adjust based on team size.

---

### 14.2 Edge Cases & Error Handling

Handle these cases explicitly in code and UX so the app behaves predictably for all users and devices.

---

#### 14.2.1 Permissions

| Edge case | Handling |
|-----------|----------|
| **Location denied** | Before any WiFi scan: check `ACCESS_FINE_LOCATION`. If denied, show warm rationale card (§13.2); on "Allow" launch system request. Never show scan UI as if it will work without permission. |
| **Location "Don't ask again" (permanently denied)** | After request, if still denied, show a non-dismissible card: "Location is required for WiFi scan. Open Settings to enable." with a button that calls `Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)` to app settings. |
| **Post-notifications denied (API 33+)** | If using foreground/background features: request `POST_NOTIFICATIONS`. If denied, either disable background monitoring or show a single in-app message that "Notifications are off — background checks won't notify you." |
| **Background location (API 30+)** | Only request when user enables "background monitoring." Use separate rationale and request; handle denial by disabling background feature and explaining in-app. |
| **Permission request during config change** | Use `rememberLauncherForActivityResult` and store result in ViewModel or saved state so that granting permission after rotation doesn’t lose state or re-trigger unnecessary requests. |

---

#### 14.2.2 Network & Connectivity

| Edge case | Handling |
|-----------|----------|
| **No internet** | Before speed test / ping / port scan: use `ConnectivityManager` (or `NetworkMonitor`) to detect active network. If none or no internet capability, show friendly message: "No internet connection. Connect to Wi‑Fi or mobile data and try again." Disable or hide "Run test" until connected. |
| **WiFi disabled** | For WiFi scan and SSID display: if `WifiManager.isWifiEnabled == false`, show empty state: "Turn on Wi‑Fi to scan nearby networks" with optional deep link to WiFi settings. |
| **Airplane mode / no connectivity** | Same as no internet for tests; for device discovery, show "Connect to a Wi‑Fi network to see devices." |
| **Network switch during operation** | Speed test / ping: cancel in-flight operations on network loss; show "Connection lost — try again." Device list: clear or mark list as stale and offer "Scan again." |
| **Captive portal / limited connectivity** | Speed test may complete with very low throughput. Show result with a short note: "Network may be limited (e.g. login page)." Optional: detect captive portal via HTTP response and show "Complete network login first." |

---

#### 14.2.3 WiFi Scan Throttling & System Limits

| Edge case | Handling |
|-----------|----------|
| **Scan throttling (API 26+: 4 scans / 2 min)** | After `startScan()`, if results don’t update or scan fails, don’t spam the user. Show "Scan in progress" and disable Scan button for a cooldown (e.g. 30 s) with countdown. Optionally show: "Scan limit reached — try again in a minute." |
| **Passive scan fallback** | If `startScan()` is deprecated or restricted, use `WifiManager.startScan()` with awareness that on some devices passive/background scan may be the only option; document in code and handle empty or delayed results. |
| **Empty scan results** | Possible due to permission, throttling, or no networks. Show empty state: "No networks found. Move closer to a router or try again in a minute." Do not show a blank list. |
| **Location off (GPS disabled)** | On API 28+, reading `scanResults` may return empty if location is off. Check location enabled; if off, show: "Turn on device location to see Wi‑Fi networks." with link to location settings. |

---

#### 14.2.4 Speed Test & Ping

| Edge case | Handling |
|-----------|----------|
| **Timeout** | Speed test: enforce connect/read timeouts (e.g. 10s / 60s). On timeout: show "Test timed out — slow or unstable connection" and allow retry. Ping: per-packet timeout; show "Request timed out" for lost packets and still show RTT for successful ones. |
| **Invalid or unreachable URL** | If speed test URL is unreachable or returns non-2xx, show "Couldn’t reach test server. Check connection or try again later." Log host for debugging. |
| **Zero or negative throughput** | If calculated download/upload is ≤ 0, show "Test failed" and do not persist; offer retry. |
| **Very high latency (e.g. >5s)** | Show value but add subtitle: "Very high latency — connection may be unstable." |
| **User cancels test** | Cancel coroutine / job when user taps "Stop"; set state to Idle and show last partial result only if meaningful (e.g. "Test stopped."). |

---

#### 14.2.5 Device Discovery & ARP

| Edge case | Handling |
|-----------|----------|
| **Empty ARP / no devices** | Show empty state: "No other devices found on this network. Tap Scan again." Consider that on some networks ARP may be restricted or empty. |
| **Missing or unknown MAC OUI** | Use OUI lookup with fallback: if prefix not in DB, show "Unknown" manufacturer; never crash. Lazy-load OUI from assets and catch parse errors. |
| **Reverse DNS / hostname lookup failure** | `InetAddress.getByName(ip).canonicalHostName` can throw or block. Use try/catch and timeout; on failure show IP only. |
| **Different subnet / VPN** | Device list is typically for current WiFi subnet. If on VPN or different network, show a note: "Showing devices on current Wi‑Fi network." and do not claim "no devices" without a scan. |

---

#### 14.2.6 Port Scanner & Ping Input

| Edge case | Handling |
|-----------|----------|
| **Invalid IP or hostname** | Validate input (e.g. regex or `InetAddress.getByName` in try/catch). Show inline error: "Enter a valid IP or hostname." Don’t start scan until valid. |
| **Invalid port range** | If user can enter range (e.g. 1–65535), clamp and validate. On invalid: "Enter ports between 1 and 65535." |
| **Reserved / loopback** | Optionally warn when scanning 127.0.0.1 or host’s own IP: "Scanning your own device." Allow but inform. |
| **Scan cancelled** | Stop scanning on user cancel; show "Scan stopped." and partial results if any. |
| **No open ports found** | Show "No open ports found in range" instead of empty list with no message. |

---

#### 14.2.7 Signal Map & Location

| Edge case | Handling |
|-----------|----------|
| **Location permission denied** | Cannot record location for heatmap. Show rationale; if denied, disable "Record" and show "Location required to map signal." |
| **Location unavailable (e.g. indoors)** | If last location is null or very old, show "Waiting for GPS…" and optionally timeout after 15–20 s with "Location unavailable. Move to a place with better GPS or try again." |
| **No WiFi / no current SSID** | Don’t record a point without SSID; show "Connect to a Wi‑Fi network to map its signal." |
| **Heatmap with no points** | If `heatmapData.isEmpty()`, show empty state with CTA to walk and record; do not show an empty map. |
| **Background location for continuous mapping** | Only request when user starts "continuous mapping"; handle denial by falling back to "record on tap only" and explain. |

---

#### 14.2.8 Data & Persistence

| Edge case | Handling |
|-----------|----------|
| **Room migration** | Use versioned migrations for schema changes when possible; keep `fallbackToDestructiveMigration()` only as last resort and document that it clears data. |
| **DataStore read failure** | On first read or corruption, catch exception and use default preferences; optionally clear DataStore and re-apply defaults. |
| **DB disk full / IO error** | Catch when inserting history; show "Couldn’t save result" and optionally offer to clear old history. Avoid crash. |
| **Empty history** | Speed test / ping history: show "No tests yet" or "Run your first test" with primary CTA; never show a broken chart. |

---

#### 14.2.9 Lifecycle & Configuration

| Edge case | Handling |
|-----------|----------|
| **Screen rotation during scan/test** | ViewModel survives rotation; keep scan/test job in ViewModel scope so it continues. Re-collect StateFlow in Composable so UI reflects current state after rotation. |
| **Process death** | Persist minimal state (e.g. last speed test result) in SavedStateHandle or DB if needed for "last result" UI; otherwise accept that in-progress operations are lost and show Idle/Empty. |
| **App in background** | Foreground service only if user explicitly started monitoring. Otherwise cancel or pause long operations when app goes to background to save battery; resume or re-run when app returns if appropriate. |

---

#### 14.2.10 API Level & Device

| Edge case | Handling |
|-----------|----------|
| **API &lt; 26** | If minSdk &lt; 26, guard all API-26+ calls (e.g. throttling message, notification channels). Consider minSdk 24 or 26 for simplicity. |
| **API 28–29** | Location required for `scanResults`; same permission flow as above. |
| **API 30+** | Background location separate; POST_NOTIFICATIONS on 33+; FOREGROUND_SERVICE_CONNECTED_DEVICE on 34+ for relevant service type. |
| **No Google Play Services** | If using FusedLocationProviderClient, handle missing Google Play Services (e.g. some devices/regions) with fallback to `LocationManager` or show "Location not available." |
| **Low memory** | Avoid loading huge lists into memory; use Paging for very long history if needed. Chart data: cap points (e.g. last 50) for line charts. |

---

### 14.3 Edge-Case Checklist (Quick Reference)

Before release, ensure:

- [ ] All permission flows: grant, deny, "don’t ask again" → Settings deep link.
- [ ] No internet / WiFi off: clear message and no crash.
- [ ] Scan throttling: cooldown UI and friendly message.
- [ ] Empty states for every list and chart (networks, devices, history, heatmap).
- [ ] Speed test / ping: timeout, cancel, invalid URL/host, zero throughput.
- [ ] Port scan / ping: invalid IP and port range validation.
- [ ] Signal map: no location, no WiFi, no points.
- [ ] Rotation and process death: no crash; state or graceful reset.
- [ ] API 26, 28, 29, 30, 33, 34: permission and service behavior correct.
- [ ] DataStore and Room: read/write failures handled; migrations or fallback documented.

---

*Last updated: 2026. All APIs verified against Android 14 (API 34).*
*This document is the single source of truth for building the app — keep it updated as APIs evolve.*
