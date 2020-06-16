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
import org.bukkit.ChatColor

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