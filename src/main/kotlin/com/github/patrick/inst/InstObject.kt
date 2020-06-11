package com.github.patrick.inst

import com.github.patrick.inst.task.InstScheduler
import com.github.patrick.inst.util.InstBox
import org.bukkit.Material
import org.bukkit.Sound
import org.bukkit.entity.Player
import org.bukkit.scheduler.BukkitTask

object InstObject {
    val instBoxSet = HashSet<InstBox>()
    val instSupporter = HashSet<Player>()
    var instPlayer: Player? = null
        internal set
    var instSound = Sound.BLOCK_NOTE_BLOCK_XYLOPHONE
        internal set
    var instMaterial = Material.STICK
        internal set
    var instBpm = 120
        internal set
    var instPerBar = 4
        internal set
    var instBar = 8
        internal set

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

    var instSchedulerTask: BukkitTask? = null
        internal set

    var instScheduler: InstScheduler? = null
        internal set
}