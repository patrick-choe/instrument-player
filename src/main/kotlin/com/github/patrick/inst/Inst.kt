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

internal val INST_SOUND_MAP = requireNotNull(ImmutableMap
        .builder<String, Sound>()
        .put("BANJO", Sound.BLOCK_NOTE_BLOCK_BANJO)
        .put("BASEDRUM", Sound.BLOCK_NOTE_BLOCK_BASEDRUM)
        .put("BASS", Sound.BLOCK_NOTE_BLOCK_BASS)
        .put("BELL", Sound.BLOCK_NOTE_BLOCK_BELL)
        .put("BIT", Sound.BLOCK_NOTE_BLOCK_BIT)
        .put("CHIME", Sound.BLOCK_NOTE_BLOCK_CHIME)
        .put("COW_BELL", Sound.BLOCK_NOTE_BLOCK_COW_BELL)
        .put("DIDGERIDOO", Sound.BLOCK_NOTE_BLOCK_DIDGERIDOO)
        .put("FLUTE", Sound.BLOCK_NOTE_BLOCK_FLUTE)
        .put("GUITAR", Sound.BLOCK_NOTE_BLOCK_GUITAR)
        .put("HARP", Sound.BLOCK_NOTE_BLOCK_HARP)
        .put("HAT", Sound.BLOCK_NOTE_BLOCK_HAT)
        .put("IRON_XYLOPHONE", Sound.BLOCK_NOTE_BLOCK_IRON_XYLOPHONE)
        .put("PLING", Sound.BLOCK_NOTE_BLOCK_PLING)
        .put("SNARE", Sound.BLOCK_NOTE_BLOCK_SNARE)
        .put("XYLOPHONE", Sound.BLOCK_NOTE_BLOCK_XYLOPHONE)
        .build()
)