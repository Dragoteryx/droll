package xyz.dragoteryx.droll.commands

import dev.kord.common.Locale
import dev.kord.core.behavior.interaction.respondEphemeral
import dev.kord.core.behavior.interaction.respondPublic
import dev.kord.core.entity.interaction.ChatInputCommandInteraction
import dev.kord.rest.builder.interaction.MultiApplicationCommandBuilder
import dev.kord.rest.builder.interaction.boolean
import dev.kord.rest.builder.interaction.input
import dev.kord.rest.builder.interaction.integer
import org.slf4j.LoggerFactory

private val logger = LoggerFactory.getLogger("roll")

fun MultiApplicationCommandBuilder.roll() {
	input("roll", "Roll one or more dice") {
		description(Locale.FRENCH, "Lancer un ou plusieurs dés")
		integer("size", "How many sides the dice has") {
			description(Locale.FRENCH, "Nombre de côtés du dé")
			name(Locale.FRENCH, "taille")
			choice("d4", 4)
			choice("d6", 6)
			choice("d8", 8)
			choice("d10", 10)
			choice("d12", 12)
			choice("d20", 20)
			choice("d100", 100)
			required = true
		}
		boolean("hidden", "Hide the result") {
			description(Locale.FRENCH, "Cacher le résultat")
			name(Locale.FRENCH, "caché")
			required = false
		}
		integer("count", "How many dice to roll") {
			description(Locale.FRENCH, "Nombre de dés lancés")
			name(Locale.FRENCH, "nombre")
			required = false
			maxValue = 10
			minValue = 1
		}
		integer("bonus", "Add a bonus (or penalty) to the roll") {
			description(Locale.FRENCH, "Ajouter un bonus (ou malus) au lancer")
			name(Locale.FRENCH, "bonus")
			required = false
			minValue = -100
			maxValue = 100
		}
	}
}

suspend fun ChatInputCommandInteraction.roll() {
	val size = command.integers["size"]!!
	val count = command.integers["count"] ?: 1
	val bonus = command.integers["bonus"] ?: 0
	val hidden = command.booleans["hidden"] ?: false

	val rolls = (1..count).map { (1..size).random() }
	val total = rolls.sum() + bonus
	val roll = if (bonus > 0) {
		"${count}d$size+$bonus"
	} else if (bonus < 0) {
		"${count}d$size-${-bonus}"
	} else {
		"${count}d$size"
	}

	logger.info("User '${user.username}' rolling $roll -> $total")

	var message = "<:roll:1307377035471224892> ($roll) | "
	if (bonus != 0L || rolls.size > 1) {
		message += rolls.joinToString(" + ")
		if (bonus > 0) message += " (+ $bonus)"
		if (bonus < 0) message += " (- ${-bonus})"
		message += " = "
	}
	message += "**$total**"

	if (hidden) {
		respondEphemeral { content = message }
	} else {
		respondPublic { content = message }
	}
}