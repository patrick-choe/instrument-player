/*
 * Copyright (C) 2020 PatrickKR
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 * Contact me on <mailpatrickkr@gmail.com>
 */

package com.github.patrick.inst.task

import com.github.patrick.inst.INST_BAR
import com.github.patrick.inst.INST_BPM
import com.github.patrick.inst.INST_PER_BAR
import com.github.patrick.inst.INST_PLAYER
import com.github.patrick.inst.INST_SCHEDULER
import com.github.patrick.inst.INST_TASK
import com.github.patrick.inst.plugin.InstPlugin
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.NamespacedKey
import org.bukkit.Sound
import org.bukkit.SoundCategory
import org.bukkit.boss.BarColor
import org.bukkit.boss.BarStyle
import java.util.EnumMap

internal class InstScheduler : Runnable {
    var instTask: InstTask? = null
        private set

    internal val totalTicks = (1200.0 * INST_PER_BAR * INST_BAR / INST_BPM).toInt()
    internal val singleTicks = (totalTicks / INST_BAR).toLong()

    internal val music = HashMap<Int, EnumMap<Sound, Float>>()

    internal var isPlaying = false

    init {
        instTask = InstCountDownTask(this)
        for (i in 0 until totalTicks) {
            music[i] = EnumMap(org.bukkit.Sound::class.java)
        }
    }

    override fun run() {
        instTask = instTask?.execute()
        if (instTask == null || INST_PLAYER == null) {
            stop()
        }
    }

    internal fun stop() {
        (instTask as? InstLoopTask)?.bar?.removeAll()
        music.clear()
        isPlaying = false
        INST_TASK?.cancel()
        INST_TASK = null
        INST_SCHEDULER = null
        INST_PLAYER = null
    }

    internal abstract class InstTask {
        abstract fun execute(): InstTask?
    }

    private inner class InstCountDownTask(private val scheduler: InstScheduler) : InstTask() {
        private val period = (1200.0 / INST_BPM).toInt()
        private var ticks = period * INST_PER_BAR
        private var countdown = Int.MAX_VALUE
        override fun execute(): InstTask? {
            if (ticks > 0) {
                val count = (ticks + period) / period
                if (countdown > count) {
                    Bukkit.getOnlinePlayers().forEach {
                        it?.run {
                            sendTitle("${ChatColor.YELLOW}$count", null, 0, (period * 1.5).toInt(), 0)
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

    internal inner class InstLoopTask(private val scheduler: InstScheduler) : InstTask() {
        val bar = Bukkit.createBossBar(NamespacedKey(InstPlugin.INSTANCE, "bar"), INST_PLAYER?.name, BarColor.PURPLE, BarStyle.SOLID)
        private val total = scheduler.totalTicks
        private var ticks = total
        var remain = 0
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
                remain = total - ticks
                bar.progress = remain.toDouble() / total
                Bukkit.getOnlinePlayers().forEach {
                    it?.run {
                        val multiplied = bar.progress * INST_BAR
                        exp = multiplied.run { this - toInt() }.toFloat()
                        level = multiplied.toInt() + 1
                        scheduler.music[remain]?.forEach { entry ->
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
}