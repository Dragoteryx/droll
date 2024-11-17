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

	val roll = (1..100).random()
	val eff = (0..9).random()
	val value = if (advantage && eff * 10 < roll) {
		maxOf(1, roll % 10 + eff * 10)
	} else if (disadvantage && eff * 10 > roll) {
		maxOf(1, roll % 10 + eff * 10)
	} else {
		roll
	}

	val message = if (advantage) {
		logger.info("User '${user.username}' rolling with advantage -> $roll, $eff")
		"<:roll:1307377035471224892> | $roll & ⬇$eff = **$value**"
	} else if (disadvantage) {
		logger.info("User '${user.username}' rolling with disadvantage -> $roll, $eff")
		"<:roll:1307377035471224892> | $roll & ⬆$eff = **$value**"
	} else {
		logger.info("User '${user.username}' rolling -> $roll")
		"<:roll:1307377035471224892> | **$value**"
	}

	if (hidden) {
		respondEphemeral { content = message }
	} else {
		respondPublic { content = message }
	}
}