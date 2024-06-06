package Module4;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.requests.GatewayIntent;

import net.dv8tion.jda.api.requests.restaction.CommandListUpdateAction;

import javax.security.auth.login.LoginException;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class DiscordBot extends ListenerAdapter {
    private static final String BOT_TOKEN = "add you bot token";
    private Map<String, Game2048> games = new HashMap<>();

    public static void main(String[] args) throws LoginException {
        JDA jda = JDABuilder.createDefault(BOT_TOKEN)
                .enableIntents(GatewayIntent.MESSAGE_CONTENT, GatewayIntent.GUILD_MESSAGES)
                .addEventListeners(new DiscordBot())
                .build();
        CommandListUpdateAction commands = jda.updateCommands();
        commands.addCommands(
                Commands.slash("startgame", "Starts a new game of 2048")
        ).queue();
    }

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        if (event.getName().equals("startgame")) {
            startGame(event);
        }
    }

    private void startGame(SlashCommandInteractionEvent event) {
        User user = event.getUser();
        String userId = user.getId();
        Game2048 game = new Game2048();
        games.put(userId, game);
        event.replyEmbeds(renderBoard(game.getBoard(), userId)).queue();
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        if (event.getAuthor().isBot()) return;

        String userId = event.getAuthor().getId();
        Game2048 game = games.get(userId);
        if (game == null) return;

        String message = event.getMessage().getContentRaw().toLowerCase();
        boolean moved = false;
        switch (message) {
            case "w":
                moved = game.move(Game2048.Direction.UP);
                break;
            case "s":
                moved = game.move(Game2048.Direction.DOWN);
                break;
            case "a":
                moved = game.move(Game2048.Direction.LEFT);
                break;
            case "d":
                moved = game.move(Game2048.Direction.RIGHT);
                break;
        }

        if (moved) {
            event.getChannel().sendMessageEmbeds(renderBoard(game.getBoard(), userId)).queue();
        }
    }

    private MessageEmbed renderBoard(int[][] board, String userId) {
        EmbedBuilder eb = new EmbedBuilder();
        eb.setTitle("2048 Game");
        eb.setDescription("```\n" + renderBoardString(board) + "\n```");
        eb.setColor(Color.ORANGE);
        eb.setFooter("Player: " + userId);
        return eb.build();
    }

    private String renderBoardString(int[][] board) {
        StringBuilder sb = new StringBuilder();
        int maxDigits = 1;
        // Calculate the maximum number of digits
        for (int[] row : board) {
            for (int cell : row) {
                int digits = String.valueOf(cell).length();
                if (digits > maxDigits) {
                    maxDigits = digits;
                }
            }
        }
        // Render the board with fixed-width cells
        for (int[] row : board) {
            for (int cell : row) {
                if (cell == 0) {
                    sb.append(String.format("%" + maxDigits + "s", "⬜")).append(" ");
                } else {
                    sb.append(String.format("%" + maxDigits + "d", cell)).append(" ");
                }
            }
            sb.append("\n");
        }
        return sb.toString();
    }
}
