package xyz.dragoteryx.droll.commands

import dev.kord.common.Locale
import dev.kord.core.behavior.interaction.respondEphemeral
import dev.kord.core.behavior.interaction.respondPublic
import dev.kord.core.entity.interaction.ChatInputCommandInteraction
import dev.kord.rest.builder.interaction.MultiApplicationCommandBuilder
import dev.kord.rest.builder.interaction.boolean
import dev.kord.rest.builder.interaction.input
import dev.kord.rest.builder.interaction.string
import org.slf4j.LoggerFactory

private val logger = LoggerFactory.getLogger("check")

fun MultiApplicationCommandBuilder.check() {
	input("check", "Roll a skill check") {
		description(Locale.FRENCH, "Lancer un test de compétence")
		name(Locale.FRENCH, "test")
		string("effect", "Roll with advantage or disadvantage") {
			description(Locale.FRENCH, "Lancer avec un avantage ou un désavantage")
			name(Locale.FRENCH, "effet")
			choice("Advantage", "advantage") { name(Locale.FRENCH, "Avantage") }
			choice("Disadvantage", "disadvantage") { name(Locale.FRENCH, "Désavantage") }
			required = false
		}
		boolean("hidden", "Hide the result") {
			description(Locale.FRENCH, "Cacher le résultat")
			name(Locale.FRENCH, "caché")
			required = false
		}
	}
}

suspend fun ChatInputCommandInteraction.check() {
	val hidden = command.booleans["hidden"] ?: false
	val effect = command.strings["effect"] ?: ""
	val advantage = effect == "advantage"
	val disadvantage = effect == "disadvantage"

	val roll1 = (1..100).random()
	val roll2 = (1..10).random()

	val eff = roll2 % 10
	val value = if ((advantage && eff * 10 < roll1) || (disadvantage && eff * 10 > roll1)) {
		if (roll1 % 10 == 0 && eff == 0) 10
		else roll1 % 10 + eff * 10
	} else {
		roll1
	}

	val message = if (advantage) {
		logger.info("User '${user.username}' rolling with advantage -> $roll1, $roll2")
		"<:roll:1307377035471224892> | $roll1 (⬇$roll2) = **$value**"
	} else if (disadvantage) {
		logger.info("User '${user.username}' rolling with disadvantage -> $roll1, $eff")
		"<:roll:1307377035471224892> | $roll1 (⬆$roll2) = **$value**"
	} else {
		logger.info("User '${user.username}' rolling -> $roll1")
		"<:roll:1307377035471224892> | **$value**"
	}

	if (hidden) {
		respondEphemeral { content = message }
	} else {
		respondPublic { content = message }
	}
}