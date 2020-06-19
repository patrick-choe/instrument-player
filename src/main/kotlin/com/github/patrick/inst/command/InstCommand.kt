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
import org.bukkit.*
import org.bukkit.entity.Player
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import kotlin.collections.HashMap
import kotlin.streams.toList

object InstCommand {
    private const val PREFIX = "BLOCK_NOTE_BLOCK_"
    private val GSON = Gson()

    internal fun register(builder: KommandBuilder) {
        with(InstObject) {
            builder.run {
                then("item") {
                    require { isOp }
                    then("type" to material()) {
                        executes {
                            it.parseOrWarnArgument<Material>("type")?.run {
                                instMaterial = this
                                it.send("아이템이 ${name}로 설정되었습니다.")
                            }
                        }
                    }
                }
                then("sound") {
                    require { isOp || this == instPlayer }
                    then("type") {
                        then("type" to noteSound()) {
                            executes {
                                it.parseOrWarnArgument<Sound>("type")?.run {
                                    instSound = this
                                    it.send("악기가 ${name.removePrefix(PREFIX).toLowerCase().split("_").stream().map(String::capitalize).toList().joinToString(separator = " ")}로 설정되었습니다.")
                                }
                            }
                        }
                        executes {
                            it.send("현재 악기: ${instSound.name.removePrefix(PREFIX).toLowerCase().split("_").stream().map(String::capitalize).toList().joinToString(separator = " ")}")
                        }
                    }
                    then("bpm") {
                        require { instSchedulerTask == null }
                        then("count" to rangedInt(1..1000)) {
                            executes {
                                it.parseOrWarnArgument<Int>("count")?.run {
                                    instBpm = this
                                    it.send("템포가 ${this}로 설정되었습니다.")
                                }
                            }
                        }
                        executes {
                            it.send("현재 템포: $instBpm")
                        }
                    }
                    then("perBar") {
                        require { instSchedulerTask == null }
                        then("count" to rangedInt(1..32)) {
                            executes {
                                it.parseOrWarnArgument<Int>("count")?.run {
                                    instPerBar = this
                                    it.send("마디 당 박자 수가 ${this}로 설정되었습니다.")
                                }
                            }
                        }
                        executes {
                            it.send("현재 마디 당 박자 수: $instPerBar")
                        }
                    }
                    then("bar") {
                        require { instSchedulerTask == null }
                        then("count" to rangedInt(1..32)) {
                            executes {
                                it.parseOrWarnArgument<Int>("count")?.run {
                                    instBar = this
                                    it.send("마디 수가 ${this}로 설정되었습니다.")
                                }
                            }
                        }
                        executes {
                            it.send("현재 마디 수: $instBar")
                        }
                    }
                }
                then("record") {
                    require { isOp }
                    then("start") {
                        require { instSchedulerTask == null }
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
                        require { instSchedulerTask != null }
                        executes {
                            instScheduler?.stop()
                            it.send("녹음이 중지되었습니다.")
                        }
                    }
                    then("load") {
                        require { instSchedulerTask == null }
                        then("name" to existentFile()) {
                            executes {
                                it.parseOrWarnArgument<File>("name")?.let { file ->
                                    try {
                                        @Suppress("UNCHECKED_CAST")
                                        val content = FileInputStream(file).use { stream ->
                                            GSON.fromJson(String(stream.readBytes()), Map::class.java)
                                        } as Map<String, Map<String, String>>
                                        val task = Bukkit.getScheduler().runTaskTimer(InstPlugin.instance, object : Runnable {
                                            private var count = 0

                                            override fun run() {
                                                Bukkit.getOnlinePlayers().forEach { player ->
                                                    content[count.toString()]?.entries?.forEach { entry ->
                                                        player.playSound(player.location, instSoundMap.getOrDefault(entry.key, instSound), SoundCategory.MASTER, 100F, entry.value.toFloatOrNull()?: 1F)
                                                    }
                                                }
                                                count++
                                            }
                                        }, 0, 1)
                                        Bukkit.getScheduler().runTaskLater(InstPlugin.instance, Runnable {
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
                            require { instSchedulerTask != null }
                            executes {
                                instScheduler?.run {
                                    it.send("파일 저장 기다리는 중...")
                                    Bukkit.getScheduler().runTaskLater(InstPlugin.instance, Runnable {
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
                                    }, (totalTicks / instBar).toLong())
                                }
                            }
                        }
                    }
                }
                then("player") {
                    require { isOp }
                    then("set") {
                        require { instSchedulerTask == null }
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
                    then("add") {
                        then("player" to player()) {
                            executes {
                                it.parseOrWarnArgument<Player>("player")?.run {
                                    it.addPlayer(this)
                                }
                            }
                        }
                        executes {
                            it.runIfPlayer {
                                it.addPlayer(this)
                            }
                        }
                    }
                    then("remove") {
                        then("player" to player()) {
                            executes {
                                it.parseOrWarnArgument<Player>("player")?.run {
                                    it.removePlayer(this)
                                }
                            }
                        }
                        executes {
                            it.runIfPlayer {
                                it.removePlayer(this)
                            }
                        }
                    }
                    then("clear") {
                        executes {
                            if (instScheduler == null)
                                instPlayer = null
                            instSupporter.clear()
                            it.send("악기 연주자와 사용자를 초기화 했습니다.")
                        }
                    }
                    executes {
                        instPlayer?.run {
                            it.send("현재 연주자: $displayName")
                        }
                        if (instSupporter.isNotEmpty())
                            it.send("현재 사용자: ${instSupporter.joinToString()}")
                    }
                }
            }
        }
    }

    private fun KommandContext.startRecord(player: Player) {
        Bukkit.getOnlinePlayers().forEach { online ->
            online.run {
                foodLevel = 20
                gameMode = GameMode.ADVENTURE
                allowFlight = true
                isFlying = true
            }
        }
        InstObject.run {
            instPlayer = player
            instScheduler = InstScheduler()
            instSchedulerTask = Bukkit.getServer().scheduler.runTaskTimer(InstPlugin.instance, Runnable {
                instScheduler?.run()
            }, 0, 1)
            send("${player.displayName}의 녹음이 시작되었습니다.")
        }
    }

    private fun KommandContext.setPlayer(player: Player) {
        InstObject.run {
            instPlayer = player
            instSupporter.remove(player)
        }
        send("악기 연주자를 ${player.displayName}로 설정했습니다.")
    }

    private fun KommandContext.addPlayer(player: Player) {
        InstObject.instSupporter.add(player)
        send("악기 사용자에 ${player.displayName}를 추가했습니다.")
    }

    private fun KommandContext.removePlayer(player: Player) {
        InstObject.run {
            if (instPlayer == player && instScheduler == null)
                instPlayer = null
            instSupporter.remove(player)
        }
        send("악기 사용자에서 ${player.displayName}를 삭제했습니다.")
    }

    private fun KommandContext.send(message: String) = sender.sendMessage(message)

    private fun KommandContext.runIfPlayer(block: Player.() -> Unit) {
        if (sender is Player)
            block.invoke(sender as Player)
        else
            send("${ChatColor.RED}알 수 없는 명령입니다.")
    }
}