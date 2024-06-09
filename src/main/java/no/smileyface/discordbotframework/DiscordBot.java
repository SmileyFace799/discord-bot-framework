package no.smileyface.discordbotframework;

import java.nio.file.NoSuchFileException;
import java.util.Collection;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.requests.restaction.CommandListUpdateAction;
import net.dv8tion.jda.api.utils.cache.CacheFlag;
import no.smileyface.discordbotframework.entities.BotCommand;

/**
 * Utility class for creating the discord bot.
 */
public final class DiscordBot {
    private DiscordBot() {
        throw new IllegalStateException("Utility class");
    }

    /**
     * Creates the discord bot.
     *
     * @throws NoSuchFileException  If the token file for the active bot is not found
     * @throws InterruptedException If the bot is interrupted while starting
     */
    public static JDA create(
            BotListener botListener,
            Collection<BotCommand> commands
    ) throws NoSuchFileException, InterruptedException {
        commands = commands
                .stream()
                .flatMap(cmd -> cmd.getAllVariants().stream())
                .toList();
        botListener.initializeCommands(commands);
        JDA jda = JDABuilder
                .createDefault(TokenManager.getActiveBot(), GatewayIntent.GUILD_VOICE_STATES)
                .disableCache(CacheFlag.EMOJI, CacheFlag.STICKER, CacheFlag.SCHEDULED_EVENTS)
                .addEventListeners(botListener)
                .build();

        jda.awaitReady();
        CommandListUpdateAction cmds = jda.updateCommands();
        cmds.addCommands(commands.stream().map(BotCommand::getData).toList()).queue();

        return jda;
    }
}