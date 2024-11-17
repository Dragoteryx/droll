package xyz.dragoteryx.droll

import dev.kord.core.Kord
import dev.kord.core.event.gateway.ReadyEvent
import dev.kord.core.event.interaction.ChatInputCommandInteractionCreateEvent
import dev.kord.core.on
import io.github.cdimascio.dotenv.dotenv
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import xyz.dragoteryx.droll.commands.*

private val logger: Logger = LoggerFactory.getLogger("Droll")

suspend fun main() {
	val dotenv = dotenv {
		ignoreIfMalformed = true
		ignoreIfMissing = true
	}

	val token = dotenv["DROLL_DISCORD_TOKEN"]
	val droll = Kord(token)

	droll.on<ReadyEvent> {
		logger.info("Logged in as ${self.username}")

		droll.createGlobalApplicationCommands {
			check()
			roll()
		}
	}

	droll.on<ChatInputCommandInteractionCreateEvent> {
		when (interaction.invokedCommandName) {
			"check" -> interaction.check()
			"roll" -> interaction.roll()
		}
	}

	droll.login()
}

