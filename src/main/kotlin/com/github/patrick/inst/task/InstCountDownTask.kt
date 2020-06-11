package com.github.patrick.inst.task

import com.github.patrick.inst.InstObject
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.Sound
import org.bukkit.SoundCategory

class InstCountDownTask(private val scheduler: InstScheduler) : InstTask {
    private val period = (1200.0 / InstObject.instBpm).toInt()
    private var ticks = period * InstObject.instPerBar
    private var countdown = Int.MAX_VALUE
    override fun execute(): InstTask? {
        if (ticks > 0) {
            val count = (ticks + period) / period
            if (countdown > count) {
                Bukkit.getOnlinePlayers().forEach {
                    it?.run {
                        sendTitle("${ChatColor.YELLOW}$count", null, 0, (period * 1.5).toInt(), 0)
                        playSound(location, Sound.BLOCK_ANVIL_LAND, SoundCategory.MASTER, 100F, 1F)
                    }
                }
                countdown = count
            }
            ticks--
            return this
        }
        return InstLoopTask(scheduler)
    }
}