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

import com.github.noonmaru.kommand.KommandContext
import com.github.noonmaru.kommand.argument.KommandArgument
import com.github.patrick.inst.InstPlugin
import com.github.patrick.inst.command.argument.*
import com.github.patrick.inst.command.argument.ExistentFileArgument
import com.github.patrick.inst.command.argument.MaterialArgument
import com.github.patrick.inst.command.argument.NonexistentFileArgument
import com.github.patrick.inst.command.argument.NoteSoundArgument
import com.github.patrick.inst.command.argument.RangedIntegerArgument
import org.bukkit.ChatColor
import java.io.File
import java.lang.reflect.Field
import java.lang.reflect.Modifier

internal const val EXTENSION = "json"
internal val FOLDER = File(InstPlugin.INSTANCE.dataFolder, "records")

internal fun rangedInt(range: IntRange): RangedIntegerArgument {
    return RangedIntegerArgument(range)
}

internal fun player(): PlayerArgument {
    return PlayerArgument.instance
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

internal fun <T> KommandContext.parseOrWarnArgument(name: String) : T? {
    parseOrNullArgument<T>(name)?.run {
        return this
    }

    val field = javaClass.getDeclaredField("argumentsByName")
    field.isAccessible = true

    val modifiersField = Field::class.java.getDeclaredField("modifiers")
    modifiersField.isAccessible = true
    modifiersField.setInt(field, field.modifiers and Modifier.FINAL.inv())

    @Suppress("UNCHECKED_CAST")
    (field.get(this) as Map<String, Pair<String, KommandArgument<*>>>).run {
        get(name)?.run {
            sender.sendMessage("${ChatColor.RED}WARN: ${second.parseFailMessage.replace(KommandArgument.TOKEN, first)}")
        }?: throw IllegalArgumentException("[$name] is unknown argument name")
    }
    return null
}