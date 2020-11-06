package me.gserv.fabrikommander.commands

import me.gserv.fabrikommander.data.TpaRequest
import me.gserv.fabrikommander.utils.Context
import me.gserv.fabrikommander.utils.Dispatcher
import net.minecraft.command.argument.EntityArgumentType
import net.minecraft.server.command.CommandManager.argument
import net.minecraft.server.command.CommandManager.literal

class TpaHereCommand(val dispatcher: Dispatcher) {
    fun register() {
        dispatcher.register(
            literal("tpahere").then(
                argument("target", EntityArgumentType.player()).executes(this::tpaHereCommand)
            )
        )
    }

    fun tpaHereCommand(context: Context): Int {
        val target = EntityArgumentType.getPlayer(context, "target")
        val source = context.source.player
        val request = TpaRequest(source = source, target = target, tpaHere = true)
        request.notifyTarget()
        return 1
    }
}
