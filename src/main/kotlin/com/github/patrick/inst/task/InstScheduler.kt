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