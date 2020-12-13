package me.gserv.fabrikommander.commands

import com.mojang.brigadier.arguments.StringArgumentType
import mc.aegis.AegisCommandBuilder
import me.gserv.fabrikommander.data.PlayerDataManager
import me.gserv.fabrikommander.utils.Context
import me.gserv.fabrikommander.utils.Dispatcher
import me.gserv.fabrikommander.utils.aqua
import me.gserv.fabrikommander.utils.green
import me.gserv.fabrikommander.utils.plus
import me.gserv.fabrikommander.utils.red
import net.minecraft.server.network.ServerPlayerEntity

class DelHomeCommand(val dispatcher: Dispatcher) {
    fun register() {
        dispatcher.register(
            AegisCommandBuilder("delhome") {
                // Below left as an example for when we have admin commands
                /*
                 * custom(CommandManager.argument("player", EntityArgumentType.player())) {
                 *     requires { it.hasPermissionLevel(2) }
                 *     suggests { context, builder ->
                 *        context.source.minecraftServer.playerNames.forEach(builder::suggest)
                 *
                 *        builder.buildFuture()
                 *     }
                 *     word("name") {
                 *         suggests { context, builder ->
                 *             PlayerDataManager.getHomes(EntityArgumentType.getPlayer(context, "player").uuid)
                 *                 ?.forEach {
                 *                     builder.suggest(it.name)
                 *                 }
                 *
                 *                 builder.buildFuture()
                 *         }
                 *         executes {
                 *             delHomeCommand(
                 *                 it,
                 *                 StringArgumentType.getString(it, "name"),
                 *                 EntityArgumentType.getPlayer(it, "player")
                 *             )
                 *         }
                 *     }
                 * }
                 */

                word("name") {
                    suggests { context, builder ->
                        PlayerDataManager.getHomes(context.source.player.uuid)?.forEach {
                            builder.suggest(it.name)
                        }

                        builder.buildFuture()
                    }
                    executes {
                        delHomeCommand(
                            it,
                            StringArgumentType.getString(it, "name"),
                            it.source.player
                        )
                    }
                }
            }.build()
        )
    }

    fun delHomeCommand(context: Context, name: String, player: ServerPlayerEntity): Int {
        val home = PlayerDataManager.getHome(player.uuid, name)

        if (home == null) {
            context.source.sendFeedback(
                red("Unknown home: ") + aqua(name),
                false
            )
        } else {
            PlayerDataManager.deleteHome(player.uuid, name)

            context.source.sendFeedback(
                green("Home deleted: ") + aqua(name),
                true
            )
        }

        return 1
    }
}
