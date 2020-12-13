package me.gserv.fabrikommander.commands

import com.mojang.brigadier.arguments.StringArgumentType
import mc.aegis.AegisCommandBuilder
import me.gserv.fabrikommander.data.PlayerDataManager
import me.gserv.fabrikommander.utils.Context
import me.gserv.fabrikommander.utils.Dispatcher
import me.gserv.fabrikommander.utils.aqua
import me.gserv.fabrikommander.utils.green
import me.gserv.fabrikommander.utils.plus
import me.gserv.fabrikommander.utils.red
import me.gserv.fabrikommander.utils.yellow

class GetHomeCommand(val dispatcher: Dispatcher) {
    fun register() {
        dispatcher.register(
            AegisCommandBuilder("gethome") {
                executes(::getHome)
                word("name") {
                    suggests { context, builder ->
                        PlayerDataManager.getHomes(context.source.player.uuid)
                            ?.forEach {
                                builder.suggest(it.name)
                            }

                        builder.buildFuture()
                    }
                    executes { getHome(it, StringArgumentType.getString(it, "name")) }
                }
            }.build()
        )
    }

    fun getHome(context: Context, name: String = "home"): Int {
        val player = context.source.player
        val home = PlayerDataManager.getHome(player.uuid, name)

        if (home == null) {
            context.source.sendFeedback(
                red("Unknown home: ") + aqua(name),
                false
            )
        } else {
            context.source.sendFeedback(
                yellow("Home ") + aqua(name) + yellow(": [") +
                        green("World: ") + aqua(home.world.toString()) + yellow(", ") +
                        green("X: ") + aqua(home.x.toString()) + yellow(", ") +
                        green("Y: ") + aqua(home.y.toString()) + yellow(", ") +
                        green("Z: ") + aqua(home.z.toString()) + yellow(", ") +
                        green("Pitch: ") + aqua(home.pitch.toString()) + yellow(", ") +
                        green("Yaw: ") + aqua(home.yaw.toString()) + yellow("]"),
                false
            )
        }

        return 1
    }
}
