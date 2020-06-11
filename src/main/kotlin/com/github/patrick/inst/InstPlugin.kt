package com.github.patrick.inst

import com.github.noonmaru.kommand.kommand
import com.github.patrick.inst.command.InstCommand
import org.bukkit.plugin.java.JavaPlugin
import java.io.File

@Suppress("unused")
class InstPlugin : JavaPlugin() {
    companion object {
        lateinit var instance: InstPlugin
            private set
    }

    @Suppress("UsePropertyAccessSyntax")
    override fun onEnable() {
        saveDefaultConfig()
        server.pluginManager.registerEvents(InstListener(), this)
        kommand {
            register("inst") {
                InstCommand.register(this)
            }
        }
        server.scheduler.runTaskTimer(this, InstConfig(File(dataFolder, "config.yml")), 0, 1)
        logger.info("Inst Plugin v0.1-SNAPSHOT")
        instance = this
    }
}