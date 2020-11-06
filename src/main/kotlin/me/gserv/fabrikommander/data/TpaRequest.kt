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
        val message =
            source.displayName as MutableText + yellow(
                " has requested " + when (tpaHere) {
                    true -> "you"
                    false -> "to"
                } + " teleport to " + when (tpaHere) { // Will change to one when later, for now it's two
                    true -> "them"
                    false -> "you"
                }
            ) + reset(". [") + click(
                hover(
                    aqua("Accept"),
                    HoverEvent(
                        HoverEvent.Action.SHOW_TEXT,
                        green("Click here to accept the request.")
                    )
                ),
                ClickEvent(
                    ClickEvent.Action.RUN_COMMAND,
                    "/tpaccept " + source.uuidAsString // There can be multiple active requests
                )
            ) + reset(" / ") + hover(
                click(
                    aqua("Deny"),
                    ClickEvent(
                        ClickEvent.Action.RUN_COMMAND,
                        "/tpdeny " + source.uuidAsString // There can be multiple active requests
                    )
                ),
                HoverEvent(
                    HoverEvent.Action.SHOW_TEXT,
                    red("Click here to deny the request.")
                )
            ) + reset("]")

        target.sendSystemMessage(message, Util.NIL_UUID)
    }
}
