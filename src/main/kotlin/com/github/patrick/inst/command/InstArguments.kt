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

package com.github.patrick.inst.command

import com.github.patrick.inst.command.argument.ExistentFileArgument
import com.github.patrick.inst.command.argument.MaterialArgument
import com.github.patrick.inst.command.argument.NonexistentFileArgument
import com.github.patrick.inst.command.argument.NoteSoundArgument
import com.github.patrick.inst.command.argument.RangedIntegerArgument

internal const val EXTENSION = "json"
internal const val PREFIX = "BLOCK_NOTE_BLOCK_"

internal fun rangedInt(range: IntRange): RangedIntegerArgument {
    return RangedIntegerArgument(range)
}

internal fun material(): MaterialArgument {
    return MaterialArgument.instance
}

internal fun noteSound(): NoteSoundArgument {
    return NoteSoundArgument.instance
}

internal fun existentFile(): ExistentFileArgument {
    return ExistentFileArgument.instance
}

internal fun nonexistentFile(): NonexistentFileArgument {
    return NonexistentFileArgument.instance
}