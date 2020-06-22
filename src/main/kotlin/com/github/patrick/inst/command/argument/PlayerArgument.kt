package com.github.patrick.inst.command.argument

import com.github.noonmaru.kommand.KommandContext
import com.github.noonmaru.kommand.argument.KommandArgument
import com.github.noonmaru.kommand.argument.suggestions
import org.bukkit.Bukkit
import org.bukkit.entity.Player

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