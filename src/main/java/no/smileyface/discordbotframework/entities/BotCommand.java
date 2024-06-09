package no.smileyface.discordbotframework.entities;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.callbacks.IReplyCallback;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import no.smileyface.discordbotframework.checks.Check;
import no.smileyface.discordbotframework.checks.ChecksFailedException;
import no.smileyface.discordbotframework.misc.MultiTypeMap;

/**
 * Represents a basic bot command. All bot commands are slash commands.
 */
public abstract class BotCommand {
    private final SlashCommandData data;
    private final Collection<Check> checks;
    private final Collection<String> nicknames;

    /**
     * Makes a bot command.
     *
     * @param data The command data that specifies how it should be implemented into the bot.
     */
    protected BotCommand(SlashCommandData data) {
        this(data, null, null);
    }

    protected BotCommand(SlashCommandData data, Check... checks) {
        this(data, Arrays.stream(checks).collect(Collectors.toSet()), null);
    }

    protected BotCommand(SlashCommandData data, String... nicknames) {
        this(data, null, Arrays.stream(nicknames).collect(Collectors.toSet()));
    }

    /**
     * Makes a bot command.
     *
     * @param data      The command data that specifies how it should be implemented into the bot.
     * @param nicknames Alternative nicknames for the command.
     */
    protected BotCommand(
            SlashCommandData data,
            Collection<Check> checks,
            Collection<String> nicknames
    ) {
        this.data = data;
        this.checks = checks == null ? Set.of() : checks;
        this.nicknames = nicknames == null ? Set.of() : nicknames;
    }

    public SlashCommandData getData() {
        return data;
    }

    /**
     * Get all variants of the command.
     * These variants will be identical to the original command, except the name.
     *
     * @return A list of command variants, one for each nickname in the `nicknames`-collection
     *         passed to the constructor.
     *         The returned collection will also include this command itself
     */
    public Collection<BotCommand> getAllVariants() {
        Collection<BotCommand> variations = new HashSet<>();
        variations.add(this);
        nicknames.forEach(nickname -> {
            SlashCommandData commandData = getData();
            variations.add(new BotCommand(Commands
                    .slash(nickname, "Shortcut for /" + commandData.getName())
                    .addOptions(commandData.getOptions())
                    .setGuildOnly(commandData.isGuildOnly())
                    .setDefaultPermissions(commandData.getDefaultPermissions())
                    .setNSFW(commandData.isNSFW())
            ) {
                @Override
                public MultiTypeMap<SlashArgKey> getSlashArgs(SlashCommandInteractionEvent event) {
                    return BotCommand.this.getSlashArgs(event);
                }

                @Override
                protected void execute(IReplyCallback event, MultiTypeMap<SlashArgKey> slashArgs) {
                    BotCommand.this.execute(event, slashArgs);
                }
            });
        });
        return variations;
    }

    /**
     * Commands can override this to organize slash command options into a map.
     * <p>
     * This map can be passed alongside the event to {@link #execute(IReplyCallback, MultiTypeMap)},
     * which can otherwise not access slash command arguments,
     * due to maintaining compatibility with buttons & modals.
     * </p><p>
     * This returns an empty map by default.
     * </p>
     *
     * @param event The command event containing contextual information on the executed command
     * @return Slash command arguments, organized into a map.
     *         There is no guarantee that this map is modifiable
     */
    public MultiTypeMap<SlashArgKey> getSlashArgs(SlashCommandInteractionEvent event) {
        return new MultiTypeMap<>();
    }

    /**
     * The code to execute when the command is run.
     *
     * @param event The command event containing contextual information on the executed command
     */
    protected abstract void execute(IReplyCallback event, MultiTypeMap<SlashArgKey> slashArgs);

    private void runChecks(IReplyCallback event) throws ChecksFailedException {
        for (Check check : checks) {
            check.check(event);
        }
    }

    /**
     * Runs the command.
     * <p>
     *     Running the command consists of 2 steps: Checking & Executing.
     *     Checking checks if the command can be executed in the invoked context,
     *     and Executing executes the command if the checking process did not yield any exceptions.
     * </p>
     *
     * @param event The {@link IReplyCallback} containing the command's invocation context
     * @param slashArgs Any additional arguments passed by the user in the invocation process
     */
    public final void run(IReplyCallback event, MultiTypeMap<SlashArgKey> slashArgs) {
        try {
            runChecks(event);
            execute(event, slashArgs);
        } catch (ChecksFailedException cfe) {
            if (event.isAcknowledged()) {
                event.getHook().sendMessage(cfe.getMessage()).queue();
            } else {
                event.reply(cfe.getMessage()).setEphemeral(true).queue();
            }
        }
    }

    public final void run(IReplyCallback event) {
        run(event, new MultiTypeMap<>());
    }

    /**
     * A generic interface for slash arg keys. Primarily exists so extending classes can
     * create an enum of keys, and have the enum class implement this.
     */
    public interface SlashArgKey {
        default String str() {
            return this.toString().replace("_", "").toLowerCase();
        }
    }
}
