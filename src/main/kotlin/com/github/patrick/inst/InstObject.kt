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

    val instSoundMap = HashMap<String, Sound>().apply {
        put("BASEDRUM", Sound.BLOCK_NOTE_BLOCK_BASEDRUM)
        put("BASS", Sound.BLOCK_NOTE_BLOCK_BASS)
        put("BELL", Sound.BLOCK_NOTE_BLOCK_BELL)
        put("CHIME", Sound.BLOCK_NOTE_BLOCK_CHIME)
        put("FLUTE", Sound.BLOCK_NOTE_BLOCK_FLUTE)
        put("GUITAR", Sound.BLOCK_NOTE_BLOCK_GUITAR)
        put("HARP", Sound.BLOCK_NOTE_BLOCK_HARP)
        put("HAT", Sound.BLOCK_NOTE_BLOCK_HAT)
        put("PLING", Sound.BLOCK_NOTE_BLOCK_PLING)
        put("SNARE", Sound.BLOCK_NOTE_BLOCK_SNARE)
        put("XYLOPHONE", Sound.BLOCK_NOTE_BLOCK_XYLOPHONE)
    }
}