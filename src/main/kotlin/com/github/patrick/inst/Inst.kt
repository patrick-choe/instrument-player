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

import com.github.patrick.inst.task.InstScheduler
import com.github.patrick.inst.util.InstBox
import com.google.common.collect.BiMap
import com.google.common.collect.HashBiMap
import com.google.common.collect.ImmutableMap
import org.bukkit.Material
import org.bukkit.Sound
import org.bukkit.entity.Player
import org.bukkit.scheduler.BukkitTask

internal val INST_BOX_SET = HashSet<InstBox>()
internal var INST_PLAYER: Player? = null
internal var INST_SOUND = Sound.BLOCK_NOTE_BLOCK_XYLOPHONE
internal var INST_MATERIAL = Material.STICK
internal var INST_BPM = 120
internal var INST_PER_BAR = 4
internal var INST_BAR = 4

internal var INST_SCHEDULER: InstScheduler? = null
internal var INST_TASK: BukkitTask? = null

private val ORIGINAL = requireNotNull(HashBiMap.create(HashMap<Int, Sound>().apply {
    put(1, Sound.BLOCK_NOTE_BLOCK_BANJO)
    put(2, Sound.BLOCK_NOTE_BLOCK_BASEDRUM)
    put(3, Sound.BLOCK_NOTE_BLOCK_BASS)
    put(4, Sound.BLOCK_NOTE_BLOCK_BELL)
    put(5, Sound.BLOCK_NOTE_BLOCK_BIT)
    put(6, Sound.BLOCK_NOTE_BLOCK_CHIME)
    put(7, Sound.BLOCK_NOTE_BLOCK_COW_BELL)
    put(8, Sound.BLOCK_NOTE_BLOCK_DIDGERIDOO)
    put(9, Sound.BLOCK_NOTE_BLOCK_FLUTE)
    put(10, Sound.BLOCK_NOTE_BLOCK_GUITAR)
    put(11, Sound.BLOCK_NOTE_BLOCK_HARP)
    put(12, Sound.BLOCK_NOTE_BLOCK_HAT)
    put(13, Sound.BLOCK_NOTE_BLOCK_IRON_XYLOPHONE)
    put(14, Sound.BLOCK_NOTE_BLOCK_PLING)
    put(15, Sound.BLOCK_NOTE_BLOCK_SNARE)
    put(16, Sound.BLOCK_NOTE_BLOCK_XYLOPHONE)
}))

internal val INST_INT_SOUND_MAP = ImmutableMap.copyOf(ORIGINAL)

internal val INST_SOUND_INT_MAP = ImmutableMap.copyOf(ORIGINAL.inverse())