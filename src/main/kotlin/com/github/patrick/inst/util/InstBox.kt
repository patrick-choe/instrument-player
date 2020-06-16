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