package com.github.patrick.inst

import com.github.patrick.inst.util.InstBlock
import com.github.patrick.inst.util.InstBox
import org.bukkit.Material
import org.bukkit.Sound
import org.bukkit.configuration.ConfigurationSection
import org.bukkit.configuration.file.YamlConfiguration
import java.io.File
import kotlin.math.sign

class InstConfig(private val file: File) : Runnable {
    private var lastModified: Long = 0
    override fun run() {
        val last = file.lastModified()
        if (last != lastModified) {
            lastModified = last
            val config = YamlConfiguration.loadConfiguration(file)
            val sound = try {
                Sound.valueOf(requireNotNull(config.getString("sound")))
            } catch (e: IllegalArgumentException) {
                throw IllegalArgumentException("invalid sound")
            }
            val material = try {
                Material.valueOf(requireNotNull(config.getString("item")))
            } catch (e: IllegalArgumentException) {
                throw IllegalArgumentException("invalid item")
            }
            config.getValues(false).forEach { entry ->
                if (setOf("sound", "item").contains(entry.key)) return@forEach
                (entry.value as ConfigurationSection).run {
                    val blockA: InstBlock
                    val blockB: InstBlock
                    getIntegerList("blockA").let {
                        if (it.count() != 3) throw IllegalArgumentException("invalid ${entry.key}: blockA")
                        blockA = InstBlock(it[0], it[1], it[2])
                    }
                    getIntegerList("blockB").let {
                        if (it.count() != 3) throw IllegalArgumentException("invalid ${entry.key}: blockB")
                        blockB = InstBlock(it[0], it[1], it[2])
                    }
                    val pitch = getDouble("pitch").run {
                        if (sign != 1.0) 1.0 else this
                    }
                    InstObject.instBoxSet.add(
                        InstBox(
                            blockA,
                            blockB,
                            pitch.toFloat()
                        )
                    )
                }
            }
            InstObject.instSound = sound
            InstObject.instMaterial = material
            println("Inst Config reloaded")
        }
    }

}