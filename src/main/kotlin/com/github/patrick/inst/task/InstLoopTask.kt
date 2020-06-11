package com.github.patrick.inst.task

import com.github.patrick.inst.InstObject
import com.github.patrick.inst.InstPlugin
import org.bukkit.Bukkit
import org.bukkit.NamespacedKey
import org.bukkit.SoundCategory
import org.bukkit.boss.BarColor
import org.bukkit.boss.BarStyle
import kotlin.math.roundToInt

class InstLoopTask(private val scheduler: InstScheduler) : InstTask {
    val bar = Bukkit.createBossBar(NamespacedKey(InstPlugin.instance, "bar"), InstObject.instPlayer?.name, BarColor.PURPLE, BarStyle.SOLID)
    private val total = scheduler.totalTicks
    private var ticks = total
    var rev = 0
        private set

    init {
        scheduler.isPlaying = true
        Bukkit.getOnlinePlayers().forEach {
            it?.run { bar.addPlayer(this) }
        }
        bar.isVisible = true
    }

    override fun execute(): InstTask? {
        if (ticks > 0) {
            rev = total - ticks
            bar.progress = rev.toDouble() / total
            Bukkit.getOnlinePlayers().forEach {
                it?.run {
                    val multiplied = bar.progress * InstObject.instBar
                    exp = multiplied.run { this - toInt() }.toFloat()
                    level = (bar.progress * InstObject.instBar).toInt() + 1
                    scheduler.music[rev]?.forEach { entry ->
                        playSound(location, entry.key, SoundCategory.MASTER, 100F, entry.value)
                    }
                }
            }
            ticks--
        } else
            ticks = total
        return this

    }
}