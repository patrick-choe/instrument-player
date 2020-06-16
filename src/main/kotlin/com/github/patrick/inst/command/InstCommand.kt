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

package com.github.patrick.inst.command

import com.github.noonmaru.kommand.KommandBuilder
import com.github.noonmaru.kommand.KommandContext
import com.github.noonmaru.kommand.argument.player
import com.github.patrick.inst.InstObject
import com.github.patrick.inst.InstPlugin
import com.github.patrick.inst.task.InstScheduler
import com.google.gson.Gson
import org.bukkit.Bukkit
import org.bukkit.GameMode
import org.bukkit.Material
import org.bukkit.Sound
import org.bukkit.SoundCategory
import org.bukkit.entity.Player
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import kotlin.collections.HashMap
import kotlin.streams.toList

object InstCommand {
    private val gson = Gson()

    internal fun register(builder: KommandBuilder) {
        builder.run {
            then("item") {
                require { isOp }
                then("type" to material()) {
                    executes {
                        it.parseArgument<Material>("type").run {
                            InstObject.instMaterial = this
                            it.send("Inst item is now $name")
                        }
                    }
                }
            }
            then("sound") {
                require { isOp || (this is Player && this == InstObject.instPlayer) }
                then("type") {
                    then("type" to noteSound()) {
                        executes {
                            it.parseArgument<Sound>("soundType").run {
                                InstObject.instSound = this
                                it.send("Inst sound is now ${name.removePrefix(PREFIX).toLowerCase().split("_").stream().map(String::capitalize).toList().joinToString(separator = " ")}")
                            }
                        }
                    }
                }
                then("bpm") {
                    require { InstObject.instSchedulerTask == null }
                    then("count" to rangedInt(1..1000)) {
                        executes {
                            it.parseArgument<Int>("count").run {
                                InstObject.instBpm = this
                                it.send("Inst BPM is now $this")
                            }
                        }
                    }
                }
                then("perBar") {
                    require { InstObject.instSchedulerTask == null }
                    then("count" to rangedInt(1..32)) {
                        executes {
                            it.parseArgument<Int>("count").run {
                                InstObject.instPerBar = this
                                it.send("Inst per-bar is now $this")
                            }
                        }
                    }
                }
                then("bar") {
                    require { InstObject.instSchedulerTask == null }
                    then("count" to rangedInt(1..32)) {
                        executes {
                            it.parseArgument<Int>("count").run {
                                InstObject.instPerBar = this
                                it.send("Inst total bar is now $this")
                            }
                        }
                    }
                }
            }
            then("record") {
                require { isOp }
                then("start") {
                    require { InstObject.instSchedulerTask == null }
                    then("player" to player()) {
                        executes {
                            it.startRecord(it.parseArgument("player"))
                        }
                    }
                    executes {
                        require { this is Player }
                        it.startRecord(it.sender as Player)
                    }
                }
                then("stop") {
                    require { InstObject.instSchedulerTask != null }
                    executes {
                        InstObject.instScheduler?.stop()
                        it.send("Inst recording is now stopped")
                    }
                }
                then("load") {
                    require { InstObject.instSchedulerTask == null }
                    then("name" to existentFile()) {
                        executes {
                            val file = it.parseArgument<File>("name")
                            try {
                                @Suppress("UNCHECKED_CAST")
                                val content = FileInputStream(file).use { stream ->
                                    gson.fromJson(String(stream.readBytes()), Map::class.java)
                                } as Map<String, Map<String, String>>
                                val task = Bukkit.getScheduler().runTaskTimer(InstPlugin.instance, object : Runnable {
                                    private var count = 0
                                    override fun run() {
                                        Bukkit.getOnlinePlayers().forEach { player ->
                                            content[count.toString()]?.entries?.forEach { entry ->
                                                player.playSound(player.location, InstObject.instSoundMap.getOrDefault(entry.key, InstObject.instSound), SoundCategory.MASTER, 100F, entry.value.toFloatOrNull()?: 1F)
                                            }
                                        }
                                        count++
                                    }
                                }, 0, 1)
                                Bukkit.getScheduler().runTaskLater(InstPlugin.instance, Runnable {
                                    task.cancel()
                                }, content.count().toLong())
                                it.send("Inst now playing ${file.nameWithoutExtension}")
                            } catch (exception: IOException) {
                                exception.printStackTrace()
                            }
                        }
                    }
                }
                then("save") {
                    then("name" to nonexistentFile()) {
                        require { InstObject.instSchedulerTask != null }
                        executes {
                            InstObject.instScheduler?.run {
                                it.send("Waiting for saving...")
                                Bukkit.getScheduler().runTaskLater(InstPlugin.instance, Runnable {
                                    val name = it.parseArgument<String>("name")
                                    try {
                                        val map = HashMap<String, HashMap<String, String>>().apply {
                                            music.forEach { entry ->
                                                put(entry.key.toString().removePrefix(PREFIX), HashMap<String, String>().apply {
                                                    entry.value.forEach { sound ->
                                                        put(sound.key.name, sound.value.toString())
                                                    }
                                                })
                                            }
                                        }
                                        FileOutputStream(File(InstPlugin.instance.dataFolder, "$name.$EXTENSION")).use { file ->
                                            file.write(Gson().toJson(map).toByteArray())
                                            it.send("Inst record is now saved at $name.$EXTENSION")
                                        }
                                    } catch (exception: IOException) {
                                        exception.printStackTrace()
                                    }
                                    stop()
                                }, (totalTicks / InstObject.instBar).toLong())
                            }
                        }
                    }
                }
            }
            then("player") {
                require { isOp }
                then("set") {
                    require { InstObject.instSchedulerTask == null }
                    then("player" to player()) {
                        executes {
                            it.setPlayer(it.parseArgument("player"))
                        }
                    }
                    executes {
                        require { this is Player }
                        it.setPlayer(it.sender as Player)
                    }
                }
                then("add") {
                    then("player" to player()) {
                        executes {
                            it.addPlayer(it.parseArgument("player"))
                        }
                    }
                    executes {
                        require { this is Player }
                        it.addPlayer(it.sender as Player)
                    }
                }
                then("remove") {
                    then("player" to player()) {
                        executes {
                            it.removePlayer(it.parseArgument("player"))
                        }
                    }
                    executes {
                        require { this is Player }
                        it.removePlayer(it.sender as Player)
                    }
                }
                then("clear") {
                    executes {
                        if (InstObject.instScheduler == null) {
                            InstObject.instPlayer = null
                            it.send("Inst player is now null")
                        }
                        InstObject.instSupporter.clear()
                        it.send("Inst supporter is now null")
                    }
                }
            }
        }
    }

    private fun KommandContext.startRecord(player: Player) {
        InstObject.instPlayer = player
        Bukkit.getOnlinePlayers().forEach { online ->
            online.foodLevel = 20
            online.gameMode = GameMode.ADVENTURE
            online.allowFlight = true
            online.isFlying = true
        }
        InstObject.instScheduler = InstScheduler()
        InstObject.instSchedulerTask = Bukkit.getServer().scheduler.runTaskTimer(InstPlugin.instance, Runnable {
            InstObject.instScheduler?.run()
        }, 0, 1)
        send("Inst recorder is now ${player.displayName}")
    }

    private fun KommandContext.setPlayer(player: Player) {
        InstObject.instPlayer = player
        send("Inst player is now ${player.displayName}")
    }

    private fun KommandContext.addPlayer(player: Player) {
        InstObject.instSupporter.add(player)
        send("${player.displayName} is now part of Inst supporters")
    }

    private fun KommandContext.removePlayer(player: Player) {
        InstObject.instSupporter.remove(player)
        send("${player.displayName} is no longer part of Inst supporters")
    }

    private fun KommandContext.send(message: String) = sender.sendMessage(message)
}