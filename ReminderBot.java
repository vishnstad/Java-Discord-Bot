package Module1;

import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.utils.FileUpload;

import javax.security.auth.login.LoginException;
import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class ReminderBot extends ListenerAdapter {

    private final JDA jda;
    //variables
    private final ConcurrentHashMap<String, List<Event>> userEvents = new ConcurrentHashMap<>(); // small storage part,  implementation of the List interface in Java.
    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor(); //schedule commands to run after a given delay or to execute periodically
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
    private final String reminderChannelId;
    private final File reminderImageFile = new File("add the path of your image you want");

    public static void main(String[] args) {
            String token = "add your bot token";
            String channelId ="channel id where the bot should remind";
            JDA jda = JDABuilder.createDefault(token).build();
            System.out.println("JDA is ready.");
            ReminderBot reminderBot = new ReminderBot(jda, channelId);
            jda.addEventListener(reminderBot);
            jda.upsertCommand("add_event", "Add a new event to be reminded")
                    .addOption(OptionType.STRING, "event_name", "The name of the event", true)
                    .addOption(OptionType.STRING, "date", "The date of the event (YYYY-MM-DD)", true)
                    .addOption(OptionType.STRING, "time", "The time of the event (HH:mm)", true)
                    .queue();
            System.out.println("Command registered.");
    }

    public ReminderBot(JDA jda, String reminderChannelId) {
        this.jda = jda;
        this.reminderChannelId = reminderChannelId;
    }

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        if (event.getName().equals("add_event")) {
            String eventName = Objects.requireNonNull(event.getOption("event_name")).getAsString();
            String dateString = Objects.requireNonNull(event.getOption("date")).getAsString();
            String timeString = Objects.requireNonNull(event.getOption("time")).getAsString();
            try {
                long scheduledTime = parseDateTime(dateString, timeString);
                List<Event> events = userEvents.computeIfAbsent(event.getUser().getId(), k -> new ArrayList<>());
                if (events.stream().noneMatch(e -> e.name.equals(eventName) && e.scheduledTime == scheduledTime)) { //checking pre existing events and time
                    events.add(new Event(eventName, scheduledTime));
                    scheduleReminder(event.getUser().getId(), eventName, scheduledTime);
                    event.reply("Event added successfully!").queue();
                } else {
                    event.reply("This event is already scheduled.").queue();
                }
            } catch (ParseException e) {
                event.reply("Invalid date or time format. Please use YYYY-MM-DD HH:mm format for date and time.").queue();
            }
        }
    }

    private long parseDateTime(String dateString, String timeString) throws ParseException {
        String dateTimeString = dateString + " " + timeString;
        return dateFormat.parse(dateTimeString).getTime();
    }

    private void scheduleReminder(String userId, String eventName, long scheduledTime) {
        long delayInMillis = scheduledTime - System.currentTimeMillis();
        if (delayInMillis <= 0) {
            notifyUser(userId, eventName, true);
            return;
        }
        scheduler.schedule(() -> notifyUser(userId, eventName, false), delayInMillis, TimeUnit.MILLISECONDS);
    }

    private void notifyUser(String userId, String eventName, boolean isPast) {
        MessageChannel channel = jda.getTextChannelById(reminderChannelId);
        if (channel != null) {
            String message = isPast
                    ? "Hey <@" + userId + ">, the scheduled time for your event: " + eventName + " has already passed."
                    : "Hey <@" + userId + ">, reminder for your event: " + eventName;

            if (reminderImageFile.exists()) {
                channel.sendMessage(message).addFiles(FileUpload.fromData(reminderImageFile)).queue();
            } else {
                channel.sendMessage(message + " (Image not found)").queue();
                System.err.println("Failed to send reminder: Image file not found.");
            }
        } else {
            System.err.println("Failed to send reminder: Channel not found.");
        }
    }

    private static class Event {
        public final String name;
        public final long scheduledTime;

        public Event(String name, long scheduledTime) {
            this.name = name;
            this.scheduledTime = scheduledTime;
        }
    }
}