package com.github.patrick.inst.command

import com.github.noonmaru.kommand.KommandBuilder
import com.github.noonmaru.kommand.KommandContext
import com.github.noonmaru.kommand.argument.integer
import com.github.noonmaru.kommand.argument.player
import com.github.noonmaru.kommand.argument.string
import com.github.patrick.inst.InstObject
import com.github.patrick.inst.InstPlugin
import com.github.patrick.inst.task.InstScheduler
import com.google.gson.Gson
import com.google.gson.internal.LinkedTreeMap
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

object InstCommand {
    private val gson = Gson()

    internal fun register(builder: KommandBuilder) {
        builder.run {
            then("item") {
                require { isOp }
                then("itemType" to InstArguments.material()) {
                    executes {
                        it.parseOrNullArgument<Material>("itemType")?.run {
                            InstObject.instMaterial = this
                            it.send("Inst item is now $name")
                        }?: it.send("Invalid material")
                    }
                }
            }
            then("sound") {
                require { isOp || (this is Player && this == InstObject.instPlayer) }
                then("type") {
                    then("soundType" to InstArguments.sound()) {
                        executes {
                            it.parseOrNullArgument<Sound>("soundType")?.run {
                                InstObject.instSound = this
                                it.send("Inst sound is now ${name.removePrefix("BLOCK_NOTE_BLOCK_")}")
                            }?: it.send("Invalid sound")
                        }
                    }
                }
                then("bpm") {
                    then("bpmCount" to integer()) {
                        executes {
                            val bpmCount = it.getArgument("bpmCount").toInt()
                            if (bpmCount in 1..1000) {
                                InstObject.instBpm = bpmCount
                                it.sender.sendMessage("Inst BPM is now $bpmCount")
                            } else
                                throw IllegalArgumentException("bpm count invalid")
                        }
                    }
                }
                then("perBar") {
                    then("perBarCount" to integer()) {
                        executes {
                            val perBarCount = it.getArgument("perBarCount").toInt()
                            if (perBarCount in 1..16) {
                                InstObject.instPerBar = perBarCount
                                it.sender.sendMessage("Inst per-bar is now $perBarCount")
                            } else
                                throw IllegalArgumentException("per-bar count invalid")
                        }
                    }
                }
                then("bar") {
                    then("barCount" to integer()) {
                        executes {
                            val barCount = it.getArgument("barCount").toInt()
                            if (barCount in 1..32) {
                                InstObject.instBar = barCount
                                it.sender.sendMessage("Inst total bars are now $barCount")
                            } else
                                throw IllegalArgumentException("total bar count invalid")
                        }
                    }
                }
            }
            then("record") {
                require { isOp }
                then("start") {
                    then("player" to player()) {
                        executes {
                            if (InstObject.instSchedulerTask != null) {
                                it.sender.sendMessage("recording in progress")
                                return@executes
                            }
                            val player = Bukkit.getPlayerExact(it.getArgument("player"))
                            player?.run {
                                InstObject.instPlayer = this
                                InstObject.instScheduler = InstScheduler()
                                InstObject.instSchedulerTask = Bukkit.getServer().scheduler.runTaskTimer(InstPlugin.instance, Runnable {
                                    InstObject.instScheduler?.run()
                                }, 0, 1)
                                foodLevel = 20
                                gameMode = GameMode.ADVENTURE
                                allowFlight = true
                                isFlying = true
                                it.sender.sendMessage("Inst recorder is now ${player.displayName}")
                            }?: throw IllegalArgumentException("unknown player: ${it.getArgument("player")}")
                        }
                    }
                    executes {
                        require { this is Player }
                        if (InstObject.instSchedulerTask != null) {
                            it.sender.sendMessage("recording in progress")
                            return@executes
                        }
                        val player = it.sender as Player
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
                        player.sendMessage("Inst recorder is now ${player.displayName}")
                    }
                }
                then("stop") {
                    executes {
                        if (InstObject.instSchedulerTask == null) {
                            it.sender.sendMessage("recording not in progress")
                            return@executes
                        }
                        InstObject.instPlayer = null
                        InstObject.instScheduler?.stop()
                        it.sender.sendMessage("Inst recorder is now stopped")
                    }
                }
                then("load") {
                    then("name" to InstArguments.loadFile()) {
                        executes {
                            if (InstObject.instSchedulerTask != null) {
                                it.send("recording in progress")
                                return@executes
                            }
                            InstObject.instPlayer = null
                            it.parseOrNullArgument<File>("name")?.let { file ->
                                try {
                                    @Suppress("UNCHECKED_CAST") val content = FileInputStream(file).use { stream ->
                                        gson.fromJson(String(stream.readBytes()), HashMap::class.java)
                                    } as HashMap<String, LinkedTreeMap<String, String>>
                                    println("total ${content.count()}")
                                    val task = Bukkit.getScheduler().runTaskTimer(InstPlugin.instance, object : Runnable {
                                        private val total = content.count()
                                        private var count = 0
                                        override fun run() {
                                            if (count <= total) {
                                                println("$count : ${content[count.toString()] == null}")
                                                Bukkit.getOnlinePlayers().forEach { player ->
                                                    content[count.toString()]?.entries?.forEach { entry ->
                                                        player.playSound(player.location, Sound.valueOf(entry.key), SoundCategory.MASTER, 100F, entry.value.toFloatOrNull()?: 1F)
                                                    }
                                                }
                                                count++
                                            }
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
                }
                then("save") {
                    then("name" to string()) {
                        executes {
                            if (InstObject.instSchedulerTask == null) {
                                it.sender.sendMessage("recording not in progress")
                                return@executes
                            }
                            InstObject.instPlayer = null
                            val name = it.parseArgument<String>("name")
                            InstObject.instScheduler?.run {
                                try {
                                    val map = HashMap<String, HashMap<String, String>>().apply {
                                        music.forEach { entry ->
                                            put(entry.key.toString(), HashMap<String, String>().apply {
                                                entry.value.forEach { sound ->
                                                    put(sound.key.name, sound.value.toString())
                                                }
                                            })
                                        }
                                    }
                                    FileOutputStream(File(InstPlugin.instance.dataFolder, "$name.json")).use { file ->
                                        file.write(Gson().toJson(map).toByteArray())
                                    }
                                } catch (exception: IOException) {
                                    exception.printStackTrace()
                                }
                                stop()
                            }
                            it.sender.sendMessage("Inst record is now saved at $name.json")
                        }
                    }
                }
            }
            then("player") {
                require { isOp }
                then("set") {
                    then("player" to player()) {
                        executes {
                            val player = Bukkit.getPlayerExact(it.getArgument("player"))
                            player?.run {
                                InstObject.instPlayer = this
                                it.sender.sendMessage("Inst player is now ${player.displayName}")
                            }?: throw IllegalArgumentException("unknown player: ${it.getArgument("player")}")
                        }
                    }
                    executes {
                        require { this is Player }
                        val player = it.sender as Player
                        InstObject.instPlayer = player
                        player.sendMessage("Inst player is now ${player.displayName}")
                    }
                }
                then("add") {
                    then("player" to player()) {
                        executes {
                            val player = Bukkit.getPlayerExact(it.getArgument("player"))
                            player?.run {
                                InstObject.instSupporter.add(this)
                                it.sender.sendMessage("${player.displayName} is now part of Inst supporters")
                            }?: throw IllegalArgumentException("unknown player: ${it.getArgument("player")}")
                        }
                    }
                    executes {
                        require { this is Player }
                        val player = it.sender as Player
                        InstObject.instSupporter.add(player)
                        player.sendMessage("${player.displayName} is now part of Inst supporters")
                    }
                }
                then("remove") {
                    then("player" to player()) {
                        executes {
                            val player = Bukkit.getPlayerExact(it.getArgument("player"))
                            player?.run {
                                InstObject.instSupporter.remove(this)
                                it.sender.sendMessage("${player.displayName} is no longer part of Inst supporters")
                            }?: throw IllegalArgumentException("unknown player: ${it.getArgument("player")}")
                        }
                    }
                    executes {
                        require { this is Player }
                        val player = it.sender as Player
                        InstObject.instSupporter.remove(player)
                        player.sendMessage("${player.displayName} is no longer part of Inst supporters")
                    }
                }
                then("clear") {
                    executes {
                        InstObject.instPlayer = null
                        InstObject.instSupporter.clear()
                        it.sender.sendMessage("Inst player is now null")
                    }
                }
            }
        }
    }

    private fun KommandContext.send(message: String) = sender.sendMessage(message)
}