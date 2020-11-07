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

class TpDenyCommand(val dispatcher: Dispatcher) {
    fun register() {
        dispatcher.register(
            literal("tpdeny").then(
                argument("source", EntityArgumentType.player()).executes(this::tpDenyCommand)
            )
        )
    }

    fun tpDenyCommand(context: Context): Int {
        val source = EntityArgumentType.getPlayer(context, "source")
        if (TeleportRequest.ACTIVE_REQUESTS[source.uuidAsString + context.source.player.uuidAsString] == null) {
            context.source.sendError(
                red("No active teleport request from ") + source.displayName
            )
            return 0
        }
        TeleportRequest.ACTIVE_REQUESTS[source.uuidAsString + context.source.player.uuidAsString]!!.notifySourceOfDeny()
        TeleportRequest.ACTIVE_REQUESTS.remove(source.uuidAsString + context.source.player.uuidAsString)
        context.source.sendFeedback(
            aqua("Teleport request from ") + source.displayName + aqua(" was denied"),
            true
        )
        return 1
    }
}
