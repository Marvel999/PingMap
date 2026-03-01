package com.marvel999.pingmap.feature.portscanner.data

import com.marvel999.pingmap.feature.portscanner.domain.PortResult
import com.marvel999.pingmap.feature.portscanner.domain.PortScannerRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.sync.Semaphore
import kotlinx.coroutines.sync.withPermit
import kotlinx.coroutines.withContext
import java.net.InetSocketAddress
import java.net.Socket
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope

private val WELL_KNOWN_SERVICES = mapOf(
    21 to "FTP", 22 to "SSH", 23 to "Telnet", 25 to "SMTP",
    53 to "DNS", 80 to "HTTP", 110 to "POP3", 143 to "IMAP",
    443 to "HTTPS", 445 to "SMB", 3306 to "MySQL", 3389 to "RDP",
    5432 to "PostgreSQL", 8080 to "HTTP-Alt", 8443 to "HTTPS-Alt"
)

class PortScannerRepositoryImpl : PortScannerRepository {

    override fun scan(host: String, portRange: IntRange, timeoutMs: Int): Flow<PortResult> = callbackFlow {
        val semaphore = Semaphore(50)
        withContext(Dispatchers.IO) {
            coroutineScope {
                portRange.map { port ->
                    async {
                        semaphore.withPermit {
                            val isOpen = try {
                                Socket().use { socket ->
                                    socket.connect(InetSocketAddress(host, port), timeoutMs)
                                    true
                                }
                            } catch (_: Exception) {
                                false
                            }
                            trySend(PortResult(
                                port = port,
                                isOpen = isOpen,
                                serviceName = WELL_KNOWN_SERVICES[port] ?: "Unknown"
                            ))
                        }
                    }
                }.awaitAll()
            }
        }
        close()
        awaitClose { }
    }.flowOn(Dispatchers.IO)
}
