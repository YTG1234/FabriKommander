package me.gserv.fabrikommander.commands

import me.gserv.fabrikommander.data.TeleportRequest
import me.gserv.fabrikommander.utils.Context
import me.gserv.fabrikommander.utils.Dispatcher
import net.minecraft.command.argument.EntityArgumentType
import net.minecraft.server.command.CommandManager.argument
import net.minecraft.server.command.CommandManager.literal

class TpaCommand(val dispatcher: Dispatcher) {
    fun register() {
        dispatcher.register(
            literal("tpa").then(
                argument("target", EntityArgumentType.player()).executes(this::tpaCommand)
            )
        )
    }

    fun tpaCommand(context: Context): Int {
        val target = EntityArgumentType.getPlayer(context, "target")
        val source = context.source.player
        val request = TeleportRequest(source = source, target = target, inverted = false)
        request.notifyTarget()
        return 1
    }
}
