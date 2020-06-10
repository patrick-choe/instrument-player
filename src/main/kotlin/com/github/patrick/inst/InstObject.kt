package com.github.patrick.inst

import com.github.patrick.inst.util.InstBox
import org.bukkit.Material
import org.bukkit.Sound
import org.bukkit.entity.Player

object InstObject {
    val instBoxSet = HashSet<InstBox>()
    var instPlayer: Player? = null
    val instSupporter = HashSet<Player>()
    var instSound = Sound.BLOCK_NOTE_BLOCK_XYLOPHONE
    var instMaterial = Material.STICK
    var instBpm = 100
    var instPerBar = 4
    var instBar = 16
}