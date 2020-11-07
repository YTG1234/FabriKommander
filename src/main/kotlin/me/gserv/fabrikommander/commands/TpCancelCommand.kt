package me.gserv.fabrikommander.commands

import me.gserv.fabrikommander.data.TeleportRequest
import me.gserv.fabrikommander.utils.Context
import me.gserv.fabrikommander.utils.Dispatcher
import me.gserv.fabrikommander.utils.aqua
import me.gserv.fabrikommander.utils.plus
import me.gserv.fabrikommander.utils.red
import net.minecraft.command.argument.EntityArgumentType
import net.minecraft.server.command.CommandManager.argument
import net.minecraft.server.command.CommandManager.literal

class TpCancelCommand(val dispatcher: Dispatcher) {
    fun register() {
        dispatcher.register(
            literal("tpcancel").then(
                argument("target", EntityArgumentType.player()).executes(this::tpCancelCommand)
            )
        )
    }

    fun tpCancelCommand(context: Context): Int {
        val target = EntityArgumentType.getPlayer(context, "target")
        if (TeleportRequest.ACTIVE_REQUESTS[context.source.player.uuidAsString + target.uuidAsString] == null) {
            context.source.sendError(
                red("No active teleport request to ") + target.displayName
            )
            return 0
        }
        TeleportRequest.ACTIVE_REQUESTS.remove(context.source.player.uuidAsString + target.uuidAsString)
        context.source.sendFeedback(aqua(
            "Teleport request to "
        ) + target.displayName + aqua(" was cancelled."), true)
        return 1
    }
}
