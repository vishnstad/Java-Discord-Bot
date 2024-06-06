package Module1;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.requests.restaction.CommandListUpdateAction;
import java.util.EnumSet;
import java.util.concurrent.TimeUnit;
import static net.dv8tion.jda.api.interactions.commands.OptionType.*;

public class Slash extends ListenerAdapter
{
    public static void main(String[] args)
    {
        JDA jda = JDABuilder.createLight("add your bot token", EnumSet.noneOf(GatewayIntent.class))
                .addEventListeners(new Slash())
                .build();
        CommandListUpdateAction commands = jda.updateCommands();
        commands.addCommands(
                Commands.slash("ban", "Ban a user from this server. Requires permission to ban users.")
                        .addOptions(new OptionData(USER, "user", "The user to ban")
                                .setRequired(true))
                        .addOptions(new OptionData(STRING, "reason", "The ban reason to use (default: Banned by <user>)"))
                        .setGuildOnly(true) //server
                        .setDefaultPermissions(DefaultMemberPermissions.enabledFor(Permission.BAN_MEMBERS))
        );
        commands.addCommands(
                Commands.slash("say", "Makes the bot say what you tell it to")
                        .addOption(STRING, "content", "What the bot should say", true)
        );

        commands.addCommands(
                Commands.slash("leave", "Make the bot leave the server")
                        .setGuildOnly(true)
                        .setDefaultPermissions(DefaultMemberPermissions.DISABLED)
        );

        commands.addCommands(
                Commands.slash("prune", "Prune messages from this channel")
                        .addOption(INTEGER, "amount", "How many messages to prune (Default 100)")
                        .setGuildOnly(true)
                        .setDefaultPermissions(DefaultMemberPermissions.enabledFor(Permission.MESSAGE_MANAGE))
        );
        commands.queue();
    }
    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event){
        if (event.getGuild() == null)
            return;
        switch (event.getName())
        {
            case "ban":
                Member member = event.getOption("user").getAsMember();
                User user = event.getOption("user").getAsUser();
                ban(event, user, member);
                break;
            case "say":
                say(event, event.getOption("content").getAsString());
                break;
            case "leave":
                leave(event);
                break;
            case "prune":
                prune(event);
                break;
            default:
                event.reply("I can't handle that command right now :(").setEphemeral(true).queue();
        }
    }
    @Override
    public void onButtonInteraction(ButtonInteractionEvent event)
    {
        String[] id = event.getComponentId().split(":");
        String authorId = id[0];
        String type = id[1];
        if (!authorId.equals(event.getUser().getId()))
            return;
        event.deferEdit().queue();
        MessageChannel channel = event.getChannel();
        switch (type)
        {
            case "prune":
                int amount = Integer.parseInt(id[2]);
                event.getChannel().getIterableHistory()
                        .skipTo(event.getMessageIdLong())
                        .takeAsync(amount)
                        .thenAccept(channel::purgeMessages);
            case "delete":
                event.getHook().deleteOriginal().queue();
        }
    }
    public void ban(SlashCommandInteractionEvent event, User user, Member member)
    {
        event.deferReply(true).queue();
        InteractionHook hook = event.getHook();
        hook.setEphemeral(true);
        if (!event.getMember().hasPermission(Permission.BAN_MEMBERS))
        {
            hook.sendMessage("You do not have the required permissions to ban users from this server.").queue();
            return;
        }
        Member selfMember = event.getGuild().getSelfMember();
        if (!selfMember.hasPermission(Permission.BAN_MEMBERS))
        {
            hook.sendMessage("I don't have the required permissions to ban users from this server.").queue();
            return;
        }
        if (member != null && !selfMember.canInteract(member))
        {
            hook.sendMessage("This user is too powerful for me to ban.").queue();
            return;
        }
        int delDays = event.getOption("del_days", 0, OptionMapping::getAsInt);
        String reason = event.getOption("reason",
                () -> "Banned by " + event.getUser().getName(),OptionMapping::getAsString);
        event.getGuild().ban(user, delDays, TimeUnit.DAYS)
                .reason(reason)
                .flatMap(v -> hook.sendMessage("Banned user " + user.getName()))
                .queue();
    }
    public void say(SlashCommandInteractionEvent event, String content)
    {
        event.reply(content).queue();
    }
    public void leave(SlashCommandInteractionEvent event)
    {
        if (!event.getMember().hasPermission(Permission.KICK_MEMBERS))
            event.reply("You do not have permissions to kick me.").setEphemeral(true).queue();
        else
            event.reply("Leaving the server... :wave:")
                    .flatMap(v -> event.getGuild().leave())
                    .queue();
    }
    public void prune(SlashCommandInteractionEvent event)
    {
        OptionMapping amountOption = event.getOption("amount");
        int amount = amountOption == null
                ? 100
                : (int) Math.min(200, Math.max(2, amountOption.getAsLong()));
        String userId = event.getUser().getId();
        event.reply("This will delete " + amount + " messages.\nAre you sure?")
                .addActionRow(
                        Button.secondary(userId + ":delete", "Nevermind!"),
                        Button.danger(userId + ":prune:" + amount, "Yes!"))
                .queue();
    }
}