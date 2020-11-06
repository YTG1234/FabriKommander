package me.gserv.fabrikommander.data

import me.gserv.fabrikommander.utils.*
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.text.ClickEvent
import net.minecraft.text.HoverEvent
import net.minecraft.text.MutableText
import net.minecraft.util.Util

class TpaRequest(
    val source: ServerPlayerEntity,
    val target: ServerPlayerEntity,
    val availableForTicks: Int = 6000,
    val tpaHere: Boolean
) {
    fun apply() {
        if (tpaHere) target.teleport(source.serverWorld, source.x, source.y, source.z, source.yaw, source.pitch)
        source.teleport(target.serverWorld, target.x, target.y, target.z, target.yaw, target.pitch)
    }

    fun notifyTarget() {
        // Message will be configurable later
        val message = click(
            hover(
                source.displayName as MutableText + yellow(
                    " has requested " + when (tpaHere) {
                        true -> "you"
                        false -> "to"
                    } + " teleport to " + when (tpaHere) { // Will change to one when later, for now it's two
                        true -> "them"
                        false -> "you"
                    } + "."
                ),
                HoverEvent(
                    HoverEvent.Action.SHOW_TEXT,
                    yellow("Click to teleport!")
                )
            ),
            ClickEvent(
                ClickEvent.Action.RUN_COMMAND,
                "/tpaccept " + source.uuidAsString // Command not implemented yet
            )
        )

        target.sendSystemMessage(message, Util.NIL_UUID)
    }
}
