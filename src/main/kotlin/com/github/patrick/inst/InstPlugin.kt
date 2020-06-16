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

import com.github.noonmaru.kommand.kommand
import com.github.patrick.inst.command.FOLDER
import com.github.patrick.inst.command.InstCommand
import org.bukkit.plugin.java.JavaPlugin
import java.io.File

class InstPlugin : JavaPlugin() {
    companion object {
        lateinit var instance: InstPlugin
            private set
    }

    @Suppress("UsePropertyAccessSyntax")
    override fun onEnable() {
        instance = this
        saveDefaultConfig()
        server.pluginManager.registerEvents(InstListener(), this)
        kommand {
            register("inst") {
                InstCommand.register(this)
            }
        }
        server.scheduler.runTaskTimer(this, InstConfig(File(dataFolder, "config.yml")), 0, 1)
        FOLDER.mkdir()
        logger.info("Inst Plugin v0.2.2-SNAPSHOT")
    }
}