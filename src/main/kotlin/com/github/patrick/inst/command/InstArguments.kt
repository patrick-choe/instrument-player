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
import com.github.noonmaru.kommand.argument.suggestions
//import com.github.patrick.inst.plugin.InstPlugin
import com.google.common.collect.ImmutableMap
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.Sound
import org.bukkit.entity.Player
//import java.io.File
import java.lang.reflect.Field
import java.lang.reflect.Modifier

//internal const val EXTENSION = "json"
//internal val FOLDER = File(InstPlugin.INSTANCE.dataFolder, "records")

internal fun rangedInt(range: IntRange): RangedIntArgument {
    return RangedIntArgument(range)
}

internal fun player(): PlayerArgument {
    return PlayerArgument.instance
}

internal fun material(): MaterialArgument {
    return MaterialArgument.instance
}

internal fun sound(): SoundArgument {
    return SoundArgument.instance
}

//internal fun existentFile(): ExistentFileArgument {
//    return ExistentFileArgument.instance
//}
//
//internal fun nonexistentFile(): NonexistentFileArgument {
//    return NonexistentFileArgument.instance
//}

internal fun <T> KommandContext.parseOrWarnArgument(name: String) : T? {
    parseOrNullArgument<T>(name)?.run {
        return this
    }

    val field = javaClass.getDeclaredField("argumentsByName").apply {
        isAccessible = true
    }

    Field::class.java.getDeclaredField("modifiers").apply {
        isAccessible = true
        setInt(field, field.modifiers and Modifier.FINAL.inv())
    }

    @Suppress("UNCHECKED_CAST")
    (field.get(this) as Map<String, Pair<String, KommandArgument<*>>>).run {
        get(name)?.run {
            send("${ChatColor.RED}WARN: ${second.parseFailMessage.replace(KommandArgument.TOKEN, first)}")
        }?: throw IllegalArgumentException("[$name] is unknown argument name")
    }
    return null
}

internal class RangedIntArgument(private val range: IntRange = Int.MIN_VALUE..Int.MAX_VALUE) : KommandArgument<Int> {
    override val parseFailMessage: String
        get() = "${KommandArgument.TOKEN} <-- ${range.first.coerceAtMost(range.last)} ~ ${range.first.coerceAtLeast(range.last)} 범위에 없는 정수 입니다."

    override fun parse(context: KommandContext, param: String): Int? {
        return param.toIntOrNull()?.run {
            if (this in range) this else null
        }
    }
}

internal class PlayerArgument : KommandArgument<Player> {
    override val parseFailMessage: String
        get() = "${KommandArgument.TOKEN} <-- 찾을 수 없는 플레이어 입니다."

    override fun parse(context: KommandContext, param: String): Player? {
        return Bukkit.getPlayerExact(param)
    }

    override fun listSuggestion(context: KommandContext, target: String): Collection<String> {
        return Bukkit.getOnlinePlayers().suggestions(target) { it.name }
    }

    companion object {
        internal val instance by lazy {
            PlayerArgument()
        }
    }
}

internal class SoundArgument : KommandArgument<Sound> {
    override val parseFailMessage: String
        get() = "${KommandArgument.TOKEN} <-- 찾을 수 없는 악기 입니다."

    override fun parse(context: KommandContext, param: String): Sound? {
        return map[param]
    }

    override fun listSuggestion(context: KommandContext, target: String): Collection<String> {
        return map.keys.suggestions(target)
    }

    private val map = requireNotNull(ImmutableMap
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

    companion object {
        internal val instance by lazy {
            SoundArgument()
        }
    }
}

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

//internal class ExistentFileArgument : KommandArgument<File> {
//    override val parseFailMessage: String
//        get() = "${KommandArgument.TOKEN}.$EXTENSION <-- 찾을 수 없는 파일 입니다."
//
//    override fun parse(context: KommandContext, param: String): File? {
//        return FOLDER.listFiles {
//            file -> file.nameWithoutExtension == param && file.extension == EXTENSION
//        }?.run {
//            if (isNotEmpty()) first() else null
//        }
//    }
//
//    override fun listSuggestion(context: KommandContext, target: String): Collection<String> {
//        return (FOLDER.listFiles {
//            file -> file.extension == EXTENSION
//        }?: emptyArray()).toList().suggestions(target) { it.nameWithoutExtension }
//    }
//
//    companion object {
//        internal val instance by lazy {
//            ExistentFileArgument()
//        }
//    }
//}
//
//internal class NonexistentFileArgument : KommandArgument<String> {
//    override val parseFailMessage: String
//        get() = "${KommandArgument.TOKEN}.$EXTENSION <-- 이미 존재하는 파일 입니다."
//
//    override fun parse(context: KommandContext, param: String): String? {
//        return FOLDER.listFiles {
//            file -> file.nameWithoutExtension == param && file.extension == EXTENSION
//        }?.run {
//            if (isEmpty()) param else null
//        }
//    }
//
//    companion object {
//        internal val instance by lazy {
//            NonexistentFileArgument()
//        }
//    }
//}