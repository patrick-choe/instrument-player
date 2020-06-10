package com.github.patrick.inst.command

import com.github.noonmaru.kommand.KommandContext
import com.github.noonmaru.kommand.argument.KommandArgument
import com.github.noonmaru.kommand.argument.suggestions
import com.github.patrick.inst.InstObject
import org.bukkit.Material
import org.bukkit.Sound

object InstArguments {
    class MaterialArgument internal constructor() : KommandArgument<Material> {
        override val parseFailMessage: String
            get() = "Material ${KommandArgument.TOKEN} not found"

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

    fun material(): MaterialArgument {
        return MaterialArgument.instance
    }

    class SoundArgument internal constructor() : KommandArgument<Sound> {
        override val parseFailMessage: String
            get() = "Sound ${KommandArgument.TOKEN} not found"

        override fun parse(context: KommandContext, param: String): Sound? {
            return InstObject.instSoundMap[param]
        }

        override fun listSuggestion(context: KommandContext, target: String): Collection<String> {
            return InstObject.instSoundMap.keys.suggestions(target)
        }

        companion object {
            internal val instance by lazy {
                SoundArgument()
            }
        }
    }

    fun sound(): SoundArgument {
        return SoundArgument.instance
    }
}