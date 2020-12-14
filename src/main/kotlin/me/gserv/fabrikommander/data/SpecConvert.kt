package me.gserv.fabrikommander.data

import me.gserv.fabrikommander.data.spec.Home
import me.gserv.fabrikommander.data.spec.Player
import me.gserv.fabrikommander.data.spec.Pos
import me.gserv.fabrikommander.data.spec.old.OldHome
import me.gserv.fabrikommander.data.spec.old.OldPlayer

fun OldHome.toNewHome() = Home(
    name = this.name,
    pos = Pos(
        world = this.world,

        x = this.x,
        y = this.y,
        z = this.z,

        yaw = this.yaw,
        pitch = this.pitch,
    )
)

fun OldPlayer.tonNewPlayer(): Player {
    val newHomes: MutableList<Home> = mutableListOf()
    this.homes.forEach {
        newHomes.add(it.toNewHome())
    }
    return Player(
        name = this.name,
        homes = newHomes,
        backPos = null,
    )
}

// region Why would you ever want this
fun Home.toOldHome() = OldHome(
    name = this.name,
    world = this.pos.world,

    x = this.pos.x,
    y = this.pos.y,
    z = this.pos.z,

    yaw = this.pos.yaw,
    pitch = this.pos.pitch
)

fun Player.toOldPlayer(): OldPlayer {
    // Warning: Will lose backPos data!
    val oldHomes: MutableList<OldHome> = mutableListOf()
    this.homes.forEach {
        oldHomes.add(it.toOldHome())
    }
    return OldPlayer(
        name = this.name,
        homes = oldHomes,
    )
}
// endregion
