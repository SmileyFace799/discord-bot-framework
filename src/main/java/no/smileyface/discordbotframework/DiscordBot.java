package no.smileyface.discordbotframework;

import java.nio.file.NoSuchFileException;
import java.util.Collection;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.managers.Presence;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.requests.restaction.CommandListUpdateAction;
import net.dv8tion.jda.api.utils.cache.CacheFlag;
import no.smileyface.discordbotframework.entities.BotCommand;

/**
 * Top-level class for the discord bot.
 */
public final class DiscordBot {
    private final JDA jda;

    /**
     * Creates the discord bot.
     *
     * @throws NoSuchFileException  If the token file for the active bot is not found
     * @throws InterruptedException If the bot is interrupted while starting
     */
    public DiscordBot(
            BotListener botListener,
            Collection<BotCommand> commands
    ) throws NoSuchFileException, InterruptedException {
        commands = commands
                .stream()
                .flatMap(cmd -> cmd.getAllVariants().stream())
                .toList();
        botListener.initializeCommands(commands);
        jda = JDABuilder
                .createDefault(TokenManager.getActiveBot(), GatewayIntent.GUILD_VOICE_STATES)
                .disableCache(CacheFlag.EMOJI, CacheFlag.STICKER, CacheFlag.SCHEDULED_EVENTS)
                .addEventListeners(botListener)
                .build();

        jda.awaitReady();
        CommandListUpdateAction cmds = jda.updateCommands();
        cmds.addCommands(commands.stream().map(BotCommand::getData).toList()).queue();

        Presence botPresence = jda.getPresence();
        botPresence.setActivity(Activity.playing("/status"));
    }

    public JDA getJda() {
        return jda;
    }
}