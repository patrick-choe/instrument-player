package com.github.patrick.inst.util

import org.bukkit.block.Block

class InstBox(blockA: InstBlock, blockB: InstBlock, val pitch: Float) {
    private val minX = blockA.x.coerceAtMost(blockB.x)
    private val minY = blockA.y.coerceAtMost(blockB.y)
    private val minZ = blockA.z.coerceAtMost(blockB.z)
    private val maxX = blockA.x.coerceAtLeast(blockB.x)
    private val maxY = blockA.y.coerceAtLeast(blockB.y)
    private val maxZ = blockA.z.coerceAtLeast(blockB.z)

    fun contains(block: Block): Boolean {
        return block.x in minX..maxX && block.y in minY..maxY && block.z in minZ..maxZ
    }
}