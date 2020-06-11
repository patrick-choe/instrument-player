package com.github.patrick.inst.task

import com.github.patrick.inst.InstObject
import org.bukkit.Bukkit
import org.bukkit.Sound
import java.util.EnumMap

class InstScheduler : Runnable {
    var instTask: InstTask? = null
        private set

    val totalTicks = ((1200.0 / InstObject.instBpm).toInt() * InstObject.instPerBar * InstObject.instBar)

    val music = HashMap<Int, EnumMap<Sound, Float>>()

    var isPlaying = false
        internal set

    init {
        instTask = InstCountDownTask(this)
        for (i in 0 until totalTicks)
            music[i] = EnumMap(org.bukkit.Sound::class.java)
    }

    override fun run() {
        instTask = instTask?.execute()
        if (instTask == null || InstObject.instPlayer == null)
            stop()
    }

    internal fun stop() {
        if (instTask is InstLoopTask) {
            (instTask as InstLoopTask).bar.run {
                Bukkit.getOnlinePlayers().forEach { removePlayer(it) }
            }
        }
        music.clear()
        isPlaying = false
        InstObject.instSchedulerTask?.cancel()
        InstObject.instSchedulerTask = null
        InstObject.instPlayer = null
        InstObject.instSupporter.clear()
    }
}