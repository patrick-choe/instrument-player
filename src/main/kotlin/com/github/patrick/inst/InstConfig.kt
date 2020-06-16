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

import com.github.patrick.inst.util.InstBlock
import com.github.patrick.inst.util.InstBox
import org.bukkit.Material
import org.bukkit.Sound
import org.bukkit.configuration.ConfigurationSection
import org.bukkit.configuration.file.YamlConfiguration
import java.io.File
import kotlin.math.pow

internal class InstConfig(private val file: File) : Runnable {
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
            InstObject.instBoxSet.clear()
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
                    val pitch = getInt("pitch")
                    InstObject.instBoxSet.add(InstBox(blockA, blockB, 2F.pow((pitch - 12).toFloat() / 12)))
                }
            }
            InstObject.instSound = sound
            InstObject.instMaterial = material
            println("Inst config reloaded")
        }
    }

}