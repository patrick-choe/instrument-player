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

package com.github.patrick.inst.command.argument

import com.github.noonmaru.kommand.KommandContext
import com.github.noonmaru.kommand.argument.KommandArgument
import com.github.noonmaru.kommand.argument.suggestions
import com.github.patrick.inst.InstPlugin
import com.github.patrick.inst.command.EXTENSION
import java.io.File

internal class ExistentFileArgument : KommandArgument<File> {
    override val parseFailMessage: String
        get() = "File ${KommandArgument.TOKEN}.$EXTENSION not found"

    override fun parse(context: KommandContext, param: String): File? {
        return InstPlugin.instance.dataFolder.listFiles {
            file -> file.nameWithoutExtension == param && file.extension == EXTENSION
        }?.run {
            if (isNotEmpty()) first() else null
        }
    }

    override fun listSuggestion(context: KommandContext, target: String): Collection<String> {
        return (InstPlugin.instance.dataFolder.listFiles {
            file -> file.extension == EXTENSION
        }?: emptyArray()).toList().suggestions(target) { it.nameWithoutExtension }
    }

    companion object {
        internal val instance by lazy {
            ExistentFileArgument()
        }
    }
}