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
import com.github.patrick.inst.INST_BAR
import com.github.patrick.inst.INST_BPM
import com.github.patrick.inst.INST_MATERIAL
import com.github.patrick.inst.INST_PER_BAR
import com.github.patrick.inst.INST_PLAYER
import com.github.patrick.inst.INST_SCHEDULER
import com.github.patrick.inst.INST_SOUND
import com.github.patrick.inst.INST_SOUND_MAP
import com.github.patrick.inst.INST_TASK
import com.github.patrick.inst.plugin.InstPlugin
import com.github.patrick.inst.task.InstScheduler
import com.google.gson.Gson
import org.bukkit.Bukkit
import org.bukkit.ChatColor
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

private const val PREFIX = "BLOCK_NOTE_BLOCK_"
private val GSON = Gson()

internal fun register(builder: KommandBuilder) {
    builder.run {
        then("item") {
            require { isOp }
            then("type" to material()) {
                executes {
                    it.parseOrWarnArgument<Material>("type")?.run {
                        INST_MATERIAL = this
                        it.send("아이템이 ${name.toLowerCase().capitalize()}로 설정되었습니다.")
                    }
                }
            }
            executes {
                it.send("현재 아이템: ${INST_MATERIAL.name.toLowerCase().capitalize()}")
            }
        }
        then("sound") {
            require { isOp || this == INST_PLAYER }
            then("type") {
                then("type" to sound()) {
                    executes {
                        it.parseOrWarnArgument<Sound>("type")?.run {
                            INST_SOUND = this
                            it.send("악기가 ${name.removePrefix(PREFIX).toLowerCase().split("_").map(String::capitalize).toList().joinToString(separator = " ")}로 설정되었습니다.")
                        }
                    }
                }
                executes {
                    it.send("현재 악기: ${INST_SOUND.name.removePrefix(PREFIX).toLowerCase().split("_").map(String::capitalize).toList().joinToString(separator = " ")}")
                }
            }
            then("bpm") {
                require { INST_TASK == null }
                then("count" to rangedInt(1..1000)) {
                    executes {
                        it.parseOrWarnArgument<Int>("count")?.run {
                            INST_BPM = this
                            it.send("템포가 ${this}로 설정되었습니다.")
                        }
                    }
                }
                executes {
                    it.send("현재 템포: $INST_BPM")
                }
            }
            then("perBar") {
                require { INST_TASK == null }
                then("count" to rangedInt(1..32)) {
                    executes {
                        it.parseOrWarnArgument<Int>("count")?.run {
                            INST_PER_BAR = this
                            it.send("마디 당 박자 수가 ${this}로 설정되었습니다.")
                        }
                    }
                }
                executes {
                    it.send("현재 마디 당 박자 수: $INST_PER_BAR")
                }
            }
            then("bar") {
                require { INST_TASK == null }
                then("count" to rangedInt(1..32)) {
                    executes {
                        it.parseOrWarnArgument<Int>("count")?.run {
                            INST_BAR = this
                            it.send("마디 수가 ${this}로 설정되었습니다.")
                        }
                    }
                }
                executes {
                    it.send("현재 마디 수: $INST_BAR")
                }
            }
        }
        then("record") {
            require { isOp }
            then("start") {
                require { INST_TASK == null }
                then("player" to player()) {
                    executes {
                        it.parseOrWarnArgument<Player>("player")?.run {
                            it.startRecord(this)
                        }
                    }
                }
                executes {
                    it.runIfPlayer {
                        it.startRecord(this)
                    }
                }
            }
            then("stop") {
                require { INST_TASK != null }
                executes {
                    INST_SCHEDULER?.stop()
                    it.send("녹음이 중지되었습니다.")
                }
            }
            then("load") {
                require { INST_TASK == null }
                then("name" to existentFile()) {
                    executes {
                        it.parseOrWarnArgument<File>("name")?.let { file ->
                            try {
                                @Suppress("UNCHECKED_CAST")
                                val content = FileInputStream(file).use { stream ->
                                    GSON.fromJson(String(stream.readBytes()), Map::class.java)
                                } as Map<String, Map<String, String>>
                                val task = Bukkit.getScheduler().runTaskTimer(InstPlugin.INSTANCE, object : Runnable {
                                    private var count = 0

                                    override fun run() {
                                        Bukkit.getOnlinePlayers().forEach { player ->
                                            content[count.toString()]?.entries?.forEach { entry ->
                                                player.playSound(player.location, INST_SOUND_MAP.getOrDefault(entry.key, INST_SOUND), SoundCategory.MASTER, 100F, entry.value.toFloatOrNull()?: 1F)
                                            }
                                        }
                                        count++
                                    }
                                }, 0, 1)
                                Bukkit.getScheduler().runTaskLater(InstPlugin.INSTANCE, Runnable {
                                    task.cancel()
                                }, content.count().toLong())
                                it.send("${file.nameWithoutExtension} 파일의 녹음을 재생합니다.")
                            } catch (exception: IOException) {
                                exception.printStackTrace()
                            }
                        }
                    }
                }
            }
            then("save") {
                then("name" to nonexistentFile()) {
                    require { INST_TASK != null }
                    executes {
                        INST_SCHEDULER?.run {
                            it.send("파일 저장 기다리는 중...")
                            Bukkit.getScheduler().runTaskLater(InstPlugin.INSTANCE, Runnable {
                                it.parseOrNullArgument<String>("name")?.let { name ->
                                    val map = HashMap<String, HashMap<String, String>>().apply {
                                        music.forEach { entry ->
                                            put(entry.key.toString(), HashMap<String, String>().apply {
                                                entry.value.forEach { sound ->
                                                    put(sound.key.name.removePrefix(PREFIX), sound.value.toString())
                                                }
                                            })
                                        }
                                    }
                                    try {
                                        FileOutputStream(File(FOLDER, "$name.$EXTENSION")).use { file ->
                                            file.write(Gson().toJson(map).toByteArray())
                                            it.send("녹음이 $name.$EXTENSION 파일에 저장되었습니다.")
                                        }
                                    } catch (exception: IOException) {
                                        exception.printStackTrace()
                                    }
                                    stop()
                                }
                            }, singleTicks)
                        }
                    }
                }
            }
        }
        then("player") {
            require { isOp }
            then("set") {
                require { INST_TASK == null }
                then("player" to player()) {
                    executes {
                        it.parseOrWarnArgument<Player>("player")?.run {
                            it.setPlayer(this)
                        }
                    }
                }
                executes {
                    it.runIfPlayer {
                        it.setPlayer(this)
                    }
                }
            }
            then("clear") {
                executes {
                    if (INST_SCHEDULER == null) {
                        INST_PLAYER = null
                        it.send("악기 연주자를 초기화 했습니다.")
                    } else {
                        it.send("연주 중에는 초기화가 불가능합니다.")
                    }
                }
            }
            executes {
                if (INST_PLAYER != null) {
                    it.send("현재 연주자: ${INST_PLAYER?.displayName}")
                } else {
                    it.send("연주자가 없습니다.")
                }
            }
        }
    }
}

internal fun KommandContext.send(message: String) = sender.sendMessage(message)

private fun KommandContext.startRecord(player: Player) {
    Bukkit.getOnlinePlayers().forEach { online ->
        online.run {
            foodLevel = 20
            gameMode = GameMode.ADVENTURE
            allowFlight = true
            isFlying = true
        }
    }
    INST_PLAYER = player
    INST_SCHEDULER = InstScheduler()
    INST_TASK = Bukkit.getServer().scheduler.runTaskTimer(InstPlugin.INSTANCE, Runnable {
        INST_SCHEDULER?.run()
    }, 0, 1)
    send("${player.displayName}의 녹음이 시작되었습니다.")
}

private fun KommandContext.setPlayer(player: Player) {
    INST_PLAYER = player
    send("악기 연주자를 ${player.displayName}로 설정했습니다.")
}

private fun KommandContext.runIfPlayer(block: Player.() -> Unit) {
    if (sender is Player) {
        block.invoke(sender as Player)
    } else {
        send("${ChatColor.RED}알 수 없는 명령입니다.")
    }
}