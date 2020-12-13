package me.gserv.fabrikommander.commands

import mc.aegis.AegisCommandBuilder
import me.gserv.fabrikommander.utils.Context
import me.gserv.fabrikommander.utils.Dispatcher
import me.gserv.fabrikommander.utils.green

class PingCommand(val dispatcher: Dispatcher) {
    fun register() {
        dispatcher.register(
            AegisCommandBuilder("ping") {
                executes(::pingCommand)
            }.build()
        )
    }

    fun pingCommand(context: Context): Int {
        context.source.sendFeedback(
            green("Pong!"),
            false
        )

        return 1
    }
}
