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

import com.github.patrick.inst.InstObject
import com.github.patrick.inst.InstPlugin
import org.bukkit.Bukkit
import org.bukkit.NamespacedKey
import org.bukkit.SoundCategory
import org.bukkit.boss.BarColor
import org.bukkit.boss.BarStyle

class InstLoopTask(private val scheduler: InstScheduler) : InstTask {
    val bar = Bukkit.createBossBar(NamespacedKey(InstPlugin.instance, "bar"), InstObject.instPlayer?.name, BarColor.PURPLE, BarStyle.SOLID)
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
                    val multiplied = bar.progress * InstObject.instBar
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