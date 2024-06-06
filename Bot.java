package Module3;

import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.requests.GatewayIntent;
import java.util.List;

public class Bot extends ListenerAdapter {
    public static void main(String[] args) {
        String BOT_TOKEN = "add your bot token";

        JDABuilder builder = JDABuilder.createDefault(BOT_TOKEN)
                .enableIntents(GatewayIntent.GUILD_MESSAGES, GatewayIntent.MESSAGE_CONTENT)
                .addEventListeners(new Bot());

        builder.build();
    }
    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        if (event.getAuthor().isBot()) return;

        String[] message = event.getMessage().getContentRaw().split(" ", 2);
        TextChannel channel = event.getChannel().asTextChannel();
        String str1 = message[0];
        if (!str1.startsWith("!")) {
            return;
        }
        switch (message[0].toLowerCase()) {
            case "!play":
                if (message.length > 1) {
                    PlayerManager.getInstance().loadAndPlay(channel, message[1]);
                }
                break;
            case "!pause":
                PlayerManager.getInstance().pause(event.getGuild());
                channel.sendMessage("Playback paused.").queue();
                break;
            case "!resume":
                PlayerManager.getInstance().resume(event.getGuild());
                channel.sendMessage("Playback resumed.").queue();
                break;
            case "!skip":
                PlayerManager.getInstance().skip(event.getGuild());
                channel.sendMessage("Skipped to the next track.").queue();
                break;
            case "!queue":
                List<String> queue = PlayerManager.getInstance().getQueue(event.getGuild());
                if (queue.isEmpty()) {
                    channel.sendMessage("The queue is currently empty.").queue();
                } else {
                    StringBuilder sb = new StringBuilder("Current Queue:\n");
                    for (int i = 0; i < queue.size(); i++) {
                        sb.append(i + 1).append(". ").append(queue.get(i)).append("\n");
                    }
                    channel.sendMessage(sb.toString()).queue();
                }
                break;
            case "!playdir":
                if (message.length > 1) {
                    PlayerManager.getInstance().loadAndPlayDirectory(channel, message[1]);
                }
                break;
            case "!lofi":
                PlayerManager.getInstance().loadAndPlay(channel, "https://www.youtube.com/watch?v=5qap5aO4i9A"); // Lofi Hip Hop Radio
                break;
            default:
                channel.sendMessage("Unknown message.").queue();
                break;
        }
    }
    }
