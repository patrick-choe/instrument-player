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
import org.bukkit.Material

internal class MaterialArgument : KommandArgument<Material> {
    override val parseFailMessage: String
        get() = "${KommandArgument.TOKEN} <-- 알 수 없는 아이템 입니다."

    override fun parse(context: KommandContext, param: String): Material? {
        return try {
            Material.valueOf(param)
        } catch (exception: IllegalArgumentException) {
            null
        }
    }

    override fun listSuggestion(context: KommandContext, target: String): Collection<String> {
        return Material.values().toList().suggestions(target) { it.name }
    }

    companion object {
        internal val instance by lazy {
            MaterialArgument()
        }
    }
}