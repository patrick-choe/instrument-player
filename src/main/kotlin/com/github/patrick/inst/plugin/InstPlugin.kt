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

package com.github.patrick.inst.plugin

import com.github.noonmaru.kommand.kommand
import com.github.patrick.inst.INST_BOX_SET
import com.github.patrick.inst.INST_MATERIAL
import com.github.patrick.inst.INST_PLAYER
import com.github.patrick.inst.INST_SCHEDULER
import com.github.patrick.inst.INST_SOUND
//import com.github.patrick.inst.command.FOLDER
import com.github.patrick.inst.command.register
import com.github.patrick.inst.task.InstScheduler
import com.github.patrick.inst.util.InstBlock
import com.github.patrick.inst.util.InstBox
import org.bukkit.Bukkit
import org.bukkit.FluidCollisionMode
import org.bukkit.Material
import org.bukkit.Sound
import org.bukkit.SoundCategory
import org.bukkit.World
import org.bukkit.configuration.ConfigurationSection
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.plugin.java.JavaPlugin
import java.io.File

class InstPlugin : JavaPlugin() {
    companion object {
        lateinit var INSTANCE: InstPlugin
            private set
    }

    override fun onEnable() {
        INSTANCE = this
//        FOLDER.mkdir()
        saveDefaultConfig()
        server.scheduler.runTaskTimer(this, InstConfig(File(dataFolder, "config.yml")), 0, 1)
        server.pluginManager.registerEvents(InstListener(), this)
        kommand {
            register("inst") {
                register(this)
            }
        }
        logger.info("Inst Plugin v0.4-SNAPSHOT")
    }

    private inner class InstConfig(private val file: File) : Runnable {
        private var lastModified = 0L

        override fun run() {
            val last = file.lastModified()
            if (last != lastModified) {
                lastModified = last
                INST_BOX_SET.clear()
                val config = YamlConfiguration.loadConfiguration(file)
                val general = setOf("sound", "item")
                config.getValues(false).forEach { entry ->
                    if (general.contains(entry.key)) {
                        return@forEach
                    }
                    (entry.value as ConfigurationSection).run {
                        val blockA: InstBlock
                        val blockB: InstBlock
                        getIntegerList("blockA").let {
                            if (it.count() != 3) {
                                throw IllegalArgumentException("유효하지 않는 구역 ${entry.key}: blockA")
                            }
                            blockA = InstBlock(it[0], it[1], it[2])
                        }
                        getIntegerList("blockB").let {
                            if (it.count() != 3) {
                                throw IllegalArgumentException("유효하지 않는 구역 ${entry.key}: blockB")
                            }
                            blockB = InstBlock(it[0], it[1], it[2])
                        }
                        val pitch = getInt("pitch")
                        INST_BOX_SET.add(InstBox(blockA, blockB, pitch))
                    }
                }
                INST_SOUND = try {
                    Sound.valueOf(requireNotNull(config.getString("sound")))
                } catch (e: IllegalArgumentException) {
                    throw IllegalArgumentException("유효하지 않는 악기입니다.")
                }
                INST_MATERIAL = try {
                    Material.valueOf(requireNotNull(config.getString("item")))
                } catch (e: IllegalArgumentException) {
                    throw IllegalArgumentException("유효하지 않는 아이템 입니다.")
                }
                INSTANCE.logger.info("설정을 불러왔습니다.")
            }
        }
    }

    private inner class InstListener : Listener {
        @EventHandler
        fun onInteract(event: PlayerInteractEvent) {
            val player = event.player
            if (player.world.environment != World.Environment.NORMAL)
                return
            event.item?.run {
                if (type != INST_MATERIAL || (INST_PLAYER != null && INST_PLAYER != player)) {
                    return
                }
                player.rayTraceBlocks(256.0, FluidCollisionMode.NEVER)?.hitBlock?.run {
                    INST_BOX_SET.forEach { box ->
                        if (box.contains(this)) {
                            event.isCancelled = true
                            Bukkit.getOnlinePlayers().filterNotNull().forEach {
                                it.playSound(it.location, INST_SOUND, SoundCategory.MASTER, 100F, box.pitch)
                            }
                            INST_SCHEDULER?.run {
                                if (isPlaying) {
                                    Bukkit.getScheduler().runTaskLater(INSTANCE, Runnable {
                                        music[(instTask as InstScheduler.InstLoopTask).remain]?.put(INST_SOUND, box.pitch)
                                    }, singleTicks)
                                }
                            }
                            return
                        }
                    }
                }
            }
        }
    }
}
