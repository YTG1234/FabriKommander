package me.gserv.fabrikommander.commands

import com.mojang.brigadier.arguments.StringArgumentType
import mc.aegis.AegisCommandBuilder
import me.gserv.fabrikommander.data.PlayerDataManager
import me.gserv.fabrikommander.data.spec.Home
import me.gserv.fabrikommander.utils.Context
import me.gserv.fabrikommander.utils.Dispatcher
import me.gserv.fabrikommander.utils.aqua
import me.gserv.fabrikommander.utils.green
import me.gserv.fabrikommander.utils.plus

class SetHomeCommand(val dispatcher: Dispatcher) {
    fun register() {
        dispatcher.register(
            AegisCommandBuilder("sethome") {
                executes(::setHomeCommand)
                word("name") {
                    executes { setHomeCommand(it, StringArgumentType.getString(it, "name")) }
                }
            }.build()
        )
    }


    fun setHomeCommand(context: Context, name: String = "home"): Int {
        val player = context.source.player

        val home = Home(
            name = name,
            world = player.world.registryKey.value,

            x = player.x,
            y = player.y,
            z = player.z,

            pitch = player.pitch,
            yaw = player.yaw
        )

        PlayerDataManager.setHome(player.uuid, home)

        context.source.sendFeedback(
            green("Home created: ") + aqua(name),
            true
        )

        return 1
    }
}
