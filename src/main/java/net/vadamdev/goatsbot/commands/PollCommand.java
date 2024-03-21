package net.vadamdev.goatsbot.commands;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.text.TextInput;
import net.dv8tion.jda.api.interactions.components.text.TextInputStyle;
import net.dv8tion.jda.api.interactions.modals.Modal;
import net.vadamdev.goatsbot.poll.EventPoll;
import net.vadamdev.goatsbot.utils.GoatsEmbed;
import net.vadamdev.goatsbot.utils.Utils;
import net.vadamdev.jdautils.commands.Command;
import net.vadamdev.jdautils.commands.ISlashCommand;
import net.vadamdev.jdautils.commands.data.ICommandData;
import net.vadamdev.jdautils.commands.data.SlashCmdData;

import javax.annotation.Nonnull;
import java.text.ParseException;
import java.util.Date;

/**
 * @author VadamDev
 * @since 17/03/2024
 */
public class PollCommand extends Command implements ISlashCommand {
    public PollCommand() {
        super("poll");
    }

    @Override
    public void execute(@Nonnull Member sender, @Nonnull ICommandData commandData) {
        final SlashCommandInteractionEvent event = commandData.castOrNull(SlashCmdData.class).getEvent();

        switch(event.getSubcommandName()) {
            case "event":
                final String link = event.getOption("lien", OptionMapping::getAsString);
                if(!Utils.isURL(link)) {
                    event.replyEmbeds(new GoatsEmbed()
                            .setTitle("G.O.A.T.S Helper - Sondage")
                            .setDescription("Le lien fournis n'est pas un lien valide !")
                            .setColor(GoatsEmbed.ERROR_COLOR).build()).setEphemeral(true).queue();

                    break;
                }

                final Date date = event.getOption("date", null, mapping -> Utils.parseDate(mapping.getAsString()));

                new EventPoll(link, date).open(event);

                break;
            case "custom":
                event.replyModal(
                        Modal.create("GoatsBot-CustomPollModal", "Sondage Custom")
                                .addComponents(
                                        ActionRow.of(TextInput.create("title", "Nom", TextInputStyle.SHORT)
                                                .setRequiredRange(1, 48)
                                                .build()),

                                        ActionRow.of(TextInput.create("description", "Description", TextInputStyle.PARAGRAPH)
                                                .setRequiredRange(1, 2048)
                                                .build()),

                                        ActionRow.of(TextInput.create("date", "Date", TextInputStyle.SHORT)
                                                .setRequiredRange(16, 16)
                                                .setPlaceholder(Utils.formatDate(new Date()))
                                                .setRequired(false)
                                                .build())
                                ).build()
                ).queue();

                break;
            default:
                break;
        }
    }

    @Nonnull
    @Override
    public SlashCommandData createSlashCommand() {
        return Commands.slash(name, "PLACEHOLDER")
                .addSubcommands(
                        new SubcommandData("event", "Créé un sondage lié a un évènement, pour connaitre la présence de chaque membre")
                                .addOptions(
                                        new OptionData(OptionType.STRING, "lien", "Lien vers le training/évènement sur le discord de la CDR")
                                                .setRequired(true),
                                        new OptionData(OptionType.STRING, "date", "Date ou a lieu l'événement (Exemple: 19/01/2038 03:14)")
                                ),
                        new SubcommandData("custom", "Créé un sondage custom, supporte actuellement que des questions à base de oui/non")
                );
    }
}
