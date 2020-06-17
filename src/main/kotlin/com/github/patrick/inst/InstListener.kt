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

package com.github.patrick.inst

import com.github.patrick.inst.task.InstLoopTask
import org.bukkit.Bukkit
import org.bukkit.FluidCollisionMode
import org.bukkit.SoundCategory
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.FoodLevelChangeEvent
import org.bukkit.event.player.PlayerInteractEvent

internal class InstListener : Listener {
    @EventHandler
    fun onInteract(event: PlayerInteractEvent) {
        event.item?.run {
            with(InstObject) {
                if (type != instMaterial)
                    return
                val player = event.player
                if (instPlayer != null && instPlayer != player && !instSupporter.contains(player))
                    return
                player.rayTraceBlocks(256.0, FluidCollisionMode.NEVER)?.hitBlock?.run {
                    instBoxSet.forEach { box ->
                        if (box.contains(this)) {
                            event.isCancelled = true
                            Bukkit.getOnlinePlayers().forEach player@{
                                if (it == null)
                                    return@player
                                it.playSound(it.location, instSound, SoundCategory.MASTER, 100F, box.pitch)
                            }
                            instScheduler?.run {
                                if (isPlaying) {
                                    val current = (instTask as InstLoopTask).remain
                                    Bukkit.getScheduler().runTaskLater(InstPlugin.instance, Runnable {
                                        music[current]?.put(instSound, box.pitch)
                                    }, (totalTicks / instBar).toLong())
                                }
                            }
                            return
                        }
                    }
                }
            }
        }
    }

    @EventHandler(ignoreCancelled = true)
    fun onFoodLevel(event: FoodLevelChangeEvent) {
        if (InstObject.instSchedulerTask != null)
            event.isCancelled = true
    }
}