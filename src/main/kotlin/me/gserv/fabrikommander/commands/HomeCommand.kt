package me.gserv.fabrikommander.commands

import com.mojang.brigadier.arguments.StringArgumentType
import mc.aegis.AegisCommandBuilder
import me.gserv.fabrikommander.data.PlayerDataManager
import me.gserv.fabrikommander.utils.Context
import me.gserv.fabrikommander.utils.Dispatcher
import me.gserv.fabrikommander.utils.aqua
import me.gserv.fabrikommander.utils.green
import me.gserv.fabrikommander.utils.identifierToWorldName
import me.gserv.fabrikommander.utils.plus
import me.gserv.fabrikommander.utils.red
import me.gserv.fabrikommander.utils.yellow
import net.minecraft.util.registry.Registry
import net.minecraft.util.registry.RegistryKey

class HomeCommand(val dispatcher: Dispatcher) {
    fun register() {
        dispatcher.register(
            AegisCommandBuilder("home") {
                executes(::homeCommand)
                word("name") {
                    suggests { context, builder ->
                        PlayerDataManager.getHomes(context.source.player.uuid)?.forEach {
                            builder.suggest(it.name)
                        }
                        builder.buildFuture()
                    }
                    executes { homeCommand(it, StringArgumentType.getString(it, "name")) }
                }
            }.build()
        )
    }

    fun homeCommand(context: Context, name: String = "home"): Int {
        val player = context.source.player
        val home = PlayerDataManager.getHome(player.uuid, name)

        if (home == null) {
            context.source.sendFeedback(
                red("Unknown home: ") + aqua(name),
                false
            )
        } else {
            val world = player.server.getWorld(RegistryKey.of(Registry.DIMENSION, home.world))

            if (world == null) {
                context.source.sendFeedback(
                    red("Home ") +
                            aqua(name) +
                            red(" is in a world ") +
                            yellow("(") +
                            aqua(identifierToWorldName(home.world)) +
                            yellow(") ") +
                            red("that no longer exists."),
                    false
                )
            } else {
                player.teleport(world, home.x, home.y, home.z, home.yaw, home.pitch)

                context.source.sendFeedback(
                    green("Teleported to home: ") + aqua(name),
                    true
                )
            }
        }

        return 1
    }
}
