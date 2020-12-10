package me.gserv.fabrikommander.commands

import com.mojang.brigadier.arguments.StringArgumentType
import me.gserv.fabrikommander.data.PlayerDataManager
import me.gserv.fabrikommander.data.spec.Pos
import me.gserv.fabrikommander.utils.Context
import me.gserv.fabrikommander.utils.Dispatcher
import me.gserv.fabrikommander.utils.aqua
import me.gserv.fabrikommander.utils.green
import me.gserv.fabrikommander.utils.identifierToWorldName
import me.gserv.fabrikommander.utils.plus
import me.gserv.fabrikommander.utils.red
import me.gserv.fabrikommander.utils.yellow
import net.minecraft.server.command.CommandManager
import net.minecraft.util.registry.Registry
import net.minecraft.util.registry.RegistryKey

class HomeCommand(val dispatcher: Dispatcher) {
    fun register() {
        dispatcher.register(
            CommandManager.literal("home")
                .executes { homeCommand(it) }
                .then(
                    CommandManager.argument("name", StringArgumentType.word())
                        .executes { homeCommand(it, StringArgumentType.getString(it, "name")) }
                        .suggests { context, builder ->
                            PlayerDataManager.getHomes(context.source.player.uuid)?.forEach {
                                builder.suggest(it.name)
                            }

                            builder.buildFuture()
                        }
                )
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
            val world = player.server.getWorld(RegistryKey.of(Registry.DIMENSION, home.pos.world))

            if (world == null) {
                context.source.sendFeedback(
                    red("Home ") +
                            aqua(name) +
                            red(" is in a world ") +
                            yellow("(") +
                            aqua(identifierToWorldName(home.pos.world)) +
                            yellow(") ") +
                            red("that no longer exists."),
                    false
                )
            } else {
                PlayerDataManager.setBackPos(
                    player.uuid,
                    Pos(
                        x = player.x,
                        y = player.y,
                        z = player.z,
                        world = player.world.registryKey.value,
                        yaw = player.yaw,
                        pitch = player.pitch
                    )
                )
                player.teleport(world, home.pos.x, home.pos.y, home.pos.z, home.pos.yaw, home.pos.pitch)

                context.source.sendFeedback(
                    green("Teleported to home: ") + aqua(name),
                    true
                )
            }
        }

        return 1
    }
}
