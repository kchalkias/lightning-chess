package com.lightningchess.webserver

import net.corda.core.identity.CordaX500Name
import org.slf4j.LoggerFactory
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.net.URI

/**
 * Define your API endpoints here.
 */
val SERVICE_NAMES = listOf("Network Map Service")

@RestController
@CrossOrigin
@RequestMapping("/api")
class Controller(rpc: NodeRPCConnection) {

    companion object {
        private val logger = LoggerFactory.getLogger(RestController::class.java)
    }

    private val proxy = rpc.proxy
    private val myLegalName: CordaX500Name = proxy.nodeInfo().legalIdentities.first().name

    @GetMapping(value = "/me", produces = arrayOf("application/json"))
    fun me() = mapOf("me" to myLegalName)

    /**
     * Returns all parties registered with the [NetworkMapService]. These names can be used to look up identities
     * using the [IdentityService].
     */
    @GetMapping(value = "/peers", produces = arrayOf("application/json"))
    fun getPeers(): Map<String, List<CordaX500Name>> {
        val nodeInfo = proxy.networkMapSnapshot()
        return mapOf("peers" to nodeInfo
                .map { it.legalIdentities.first().name }
                //filter out myself, notary and eventual network map started by driver
                .filter { it.organisation !in (SERVICE_NAMES + myLegalName.organisation) })
    }

    @PostMapping(value = "/create-game", produces = arrayOf("application/json"))
    fun createGame(@RequestBody gameRequest: NewGameRequest): ResponseEntity<NewGameRequest> {
        print(gameRequest)

        return try {
            //val signedTx = proxy.startTrackedFlow(::IssueJournalStateFlow, ).returnValue.getOrThrow()

            //"Transaction id ${signedTx.id}

            return ResponseEntity.created(URI("")).body(gameRequest)
        } catch (ex: Throwable) {
            logger.error(ex.message, ex)
            return ResponseEntity.badRequest().build()
        }
    }
}