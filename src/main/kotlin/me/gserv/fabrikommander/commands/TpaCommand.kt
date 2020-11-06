package me.gserv.fabrikommander.commands

import me.gserv.fabrikommander.data.TpaRequest
import me.gserv.fabrikommander.utils.Context
import me.gserv.fabrikommander.utils.Dispatcher
import net.minecraft.command.argument.EntityArgumentType
import net.minecraft.server.command.CommandManager.argument
import net.minecraft.server.command.CommandManager.literal

class TpaCommand(val dispatcher: Dispatcher) {
    fun register() {
        dispatcher.register(
            literal("tpa").then(
                argument("target", EntityArgumentType.player()).executes {
                    tpaCommand(it, false)
                }
            )
        )

        dispatcher.register(
            literal("tpahere").then(
                argument("target", EntityArgumentType.player()).executes {
                    tpaCommand(it, true)
                }
            )
        )
    }

    fun tpaCommand(context: Context, tpaHere: Boolean): Int {
        val target = EntityArgumentType.getPlayer(context, "target")
        val source = context.source.player
        val request = TpaRequest(source = source, target = target, tpaHere = tpaHere)
        request.notifyTarget()
        return 1
    }
}
