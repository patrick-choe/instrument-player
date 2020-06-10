package com.github.patrick.inst

import org.bukkit.Bukkit
import org.bukkit.FluidCollisionMode
import org.bukkit.SoundCategory
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerInteractEvent

class InstListener : Listener {
    @EventHandler
    fun onInteract(event: PlayerInteractEvent) {
        event.item?.run {
            if (type != InstObject.instMaterial)
                return
            event.player.run {
                if (InstObject.instPlayer != this && !InstObject.instSupporter.contains(this))
                    return
                this.rayTraceBlocks(256.0, FluidCollisionMode.NEVER)?.hitBlock?.run {
                    InstObject.instBoxSet.forEach { box ->
                        if (box.contains(this)) {
                            event.isCancelled = true
                            Bukkit.getOnlinePlayers().forEach player@{
                                if (it == null)
                                    return@player
                                it.playSound(it.location, InstObject.instSound, SoundCategory.MASTER, 100F, box.pitch)
                            }
                            return
                        }
                    }
                }
            }
        }
    }
}