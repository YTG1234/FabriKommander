package me.gserv.fabrikommander.data

import com.charleskorn.kaml.UnknownPropertyException
import com.charleskorn.kaml.Yaml
import me.gserv.fabrikommander.data.spec.Home
import me.gserv.fabrikommander.data.spec.Player
import me.gserv.fabrikommander.data.spec.Pos
import me.gserv.fabrikommander.data.spec.old.OldPlayer
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.util.WorldSavePath
import org.apache.logging.log4j.LogManager
import java.nio.file.Path
import java.util.*
import kotlin.NoSuchElementException

object PlayerDataManager {
    private val logger = LogManager.getLogger(this::class.java)

    private var cache: MutableMap<UUID, Player> = mutableMapOf()

    private lateinit var dataDir: Path

    fun setup() {
        ServerLifecycleEvents.SERVER_STARTING.register {
            cache.clear()
            dataDir = it.getSavePath(WorldSavePath.ROOT).resolve("FabriKommander")

            dataDir.toFile().mkdir()

            logger.info("Data directory: $dataDir")
        }

        ServerLifecycleEvents.SERVER_STOPPING.register {
            shutdown()
        }
    }

    fun playerJoined(player: ServerPlayerEntity) {
        val uuid = player.uuid

        cache[uuid] = loadData(player)
    }

    fun playerLeft(player: ServerPlayerEntity) {
        val uuid = player.uuid
        val data = cache[uuid]

        if (data != null) {
            saveData(uuid)
            cache.remove(uuid)
        }
    }

    fun loadData(player: ServerPlayerEntity): Player {
        val uuid = player.uuid
        val playerFile = dataDir.resolve("$uuid.yaml").toFile()

        if (!playerFile.exists()) {
            playerFile.createNewFile()

            val data = Player(name = player.gameProfile.name)

            playerFile.writeText(
                Yaml.default.encodeToString(Player.serializer(), data)
            )

            return data
        }

        return try {
            // Trying to deserialize the Player object normally
            val string = playerFile.readText()
            Yaml.default.decodeFromString(Player.serializer(), string)
        } catch (propError: UnknownPropertyException) {
            // That failed
            logger.error("Unknown properties found, attempting to convert from old format...")
            try {
                // Trying to deserialize from the old format and use the extension function to convert to a new player
                val oldString = playerFile.readText()
                val newPlayer = Yaml.default.decodeFromString(OldPlayer.serializer(), oldString).tonNewPlayer()
                // If we got to this line, it didn't fail
                logger.warn("File " + playerFile.name + " was using the old format.")
                // Returning the correct thing
                newPlayer
            } catch (oldPropError: UnknownPropertyException) {
                // It's not in the old format either 🤷
                logger.fatal("File " + playerFile.name + " is using an invalid format!")
                throw oldPropError
            }
        }
    }

    fun saveData(uuid: UUID) {
        val playerFile = dataDir.resolve("$uuid.yaml").toFile()
        val data = cache[uuid]
            ?: throw NoSuchElementException("No cached data found for player: ($uuid)")

        if (!playerFile.exists()) {
            playerFile.createNewFile()
        }

        playerFile.writeText(
            Yaml.default.encodeToString(Player.serializer(), data)
        )
    }

    fun getHomes(uuid: UUID): List<Home>? {
        return cache[uuid]?.homes
    }

    fun getHome(uuid: UUID, name: String): Home? {
        return cache[uuid]?.homes
            ?.firstOrNull { it.name.equals(name, ignoreCase = true) }
    }

    fun setHome(uuid: UUID, home: Home): Boolean? {
        val existing = getHome(uuid, home.name)

        if (existing != null) {
            cache[uuid]?.homes?.remove(home)
        }

        val result = cache[uuid]?.homes?.add(home)

        saveData(uuid)

        return result
    }

    fun deleteHome(uuid: UUID, name: String): Boolean? {
        val result = cache[uuid]?.homes?.removeIf { it.name.equals(name, ignoreCase = true) }

        saveData(uuid)

        return result
    }

    fun getBackPos(uuid: UUID): Pos? = cache[uuid]?.backPos

    fun setBackPos(uuid: UUID, newPos: Pos) {
        cache[uuid]?.backPos = newPos
        saveData(uuid)
    }

    fun shutdown() {
        for (uuid in cache.keys) {
            saveData(uuid)
        }
    }
}
