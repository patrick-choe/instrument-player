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
import com.github.patrick.inst.command.EXTENSION
import com.github.patrick.inst.command.FOLDER

internal class NonexistentFileArgument : KommandArgument<String> {
    override val parseFailMessage: String
        get() = "${KommandArgument.TOKEN}.$EXTENSION <-- 이미 존재하는 파일 입니다."

    override fun parse(context: KommandContext, param: String): String? {
        return FOLDER.listFiles {
            file -> file.nameWithoutExtension == param && file.extension == EXTENSION
        }?.run {
            if (isEmpty()) param else null
        }
    }

    companion object {
        internal val instance by lazy {
            NonexistentFileArgument()
        }
    }
}