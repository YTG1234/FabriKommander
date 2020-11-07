package me.gserv.fabrikommander.commands

import me.gserv.fabrikommander.data.TeleportRequest
import me.gserv.fabrikommander.utils.Context
import me.gserv.fabrikommander.utils.Dispatcher
import me.gserv.fabrikommander.utils.plus
import me.gserv.fabrikommander.utils.red
import net.minecraft.command.argument.EntityArgumentType
import net.minecraft.server.command.CommandManager.argument
import net.minecraft.server.command.CommandManager.literal

// I'm not sure how I should capitalise this
class TpAcceptCommand(val dispatcher: Dispatcher) {
    fun register() {
        dispatcher.register(
            literal("tpaccept").then(
                argument("source", EntityArgumentType.player()).executes(this::tpAcceptCommand)
            )
        )
    }

    fun tpAcceptCommand(context: Context): Int {
        val source = EntityArgumentType.getPlayer(context, "source")
        if (TeleportRequest.ACTIVE_REQUESTS[source.uuidAsString + context.source.player.uuidAsString] == null) {
            context.source.sendError(
                red("No active teleport request from ") + source.displayName
            )
            return 0
        }
        TeleportRequest.ACTIVE_REQUESTS[source.uuidAsString + context.source.player.uuidAsString]!!.apply()
        TeleportRequest.ACTIVE_REQUESTS.remove(source.uuidAsString + context.source.player.uuidAsString)
        return 1
    }
}
