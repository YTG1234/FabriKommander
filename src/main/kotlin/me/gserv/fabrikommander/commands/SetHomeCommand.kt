package me.gserv.fabrikommander.commands

import com.mojang.brigadier.arguments.StringArgumentType
import me.gserv.fabrikommander.data.PlayerDataManager
import me.gserv.fabrikommander.data.spec.Home
import me.gserv.fabrikommander.data.spec.Pos
import me.gserv.fabrikommander.utils.Context
import me.gserv.fabrikommander.utils.Dispatcher
import me.gserv.fabrikommander.utils.aqua
import me.gserv.fabrikommander.utils.green
import me.gserv.fabrikommander.utils.plus
import net.minecraft.server.command.CommandManager

class SetHomeCommand(val dispatcher: Dispatcher) {
    fun register() {
        dispatcher.register(
            CommandManager.literal("sethome")
                .executes { setHomeCommand(it) }
                .then(
                    CommandManager.argument("name", StringArgumentType.word())
                        .executes { setHomeCommand(it, StringArgumentType.getString(it, "name")) }
                )
        )
    }

    fun setHomeCommand(context: Context, name: String = "home"): Int {
        val player = context.source.player

        val home = Home(
            name = name,

            pos = Pos(
                x = player.x,
                y = player.y,
                z = player.z,
                yaw = player.yaw,
                pitch = player.pitch,
                world = player.world.registryKey.value
            ),
        )

        PlayerDataManager.setHome(player.uuid, home)

        context.source.sendFeedback(
            green("Home created: ") + aqua(name),
            true
        )

        return 1
    }
}
