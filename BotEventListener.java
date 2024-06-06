package Module2;

import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class BotEventListener extends ListenerAdapter {

    private final List<String> schedules = new ArrayList<>();
    private final List<String> assignments = new ArrayList<>();
    private final List<String> exams = new ArrayList<>();
    private final List<String> materials = new ArrayList<>();
    private final List<String> pastAssignments = new ArrayList<>();
    private final List<String> pastExams = new ArrayList<>();
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        if (event.getAuthor().isBot()) return;

        String content = event.getMessage().getContentRaw().trim();

        if (!content.startsWith("!")) return;

        String[] message = content.split("\\s+", 2);  // Split into command and rest of the message
        String command = message[0].toLowerCase();

        switch (command) {
            case "!schedule":
                handleScheduleCommand(event, message.length > 1 ? message[1] : "");
                break;
            case "!addschedule":
                handleAddScheduleCommand(event, message.length > 1 ? message[1] : "");
                break;
            case "!assignment":
                handleAssignmentCommand(event, message.length > 1 ? message[1] : "");
                break;
            case "!addassignment":
                handleAddAssignmentCommand(event, message.length > 1 ? message[1] : "");
                break;
            case "!exam":
                handleExamCommand(event, message.length > 1 ? message[1] : "");
                break;
            case "!addexam":
                handleAddExamCommand(event, message.length > 1 ? message[1] : "");
                break;
            case "!materials":
                handleMaterialsCommand(event);
                break;
            case "!addmaterial":
                handleAddMaterialCommand(event, message.length > 1 ? message[1] : "");
                break;
            case "!pastassignments":
                handlePastAssignmentsCommand(event);
                break;
            case "!pastexams":
                handlePastExamsCommand(event);
                break;
            default:
                event.getChannel().sendMessage("Unknown command. Please use !schedule, !addschedule, !assignment, !addassignment, !exam, !addexam, !materials, !addmaterial, !pastassignments, or !pastexams.").queue();
        }

        checkAndMoveCompletedTasks();
    }

    private void handleScheduleCommand(MessageReceivedEvent event, String args) {
        if (schedules.isEmpty()) {
            event.getChannel().sendMessage("No schedules available.").queue();
        } else {
            StringBuilder response = new StringBuilder("Class Schedule:\n");
            for (String schedule : schedules) {
                response.append("- ").append(schedule).append("\n");
            }
            event.getChannel().sendMessage(response.toString()).queue();
        }
    }

    private void handleAddScheduleCommand(MessageReceivedEvent event, String args) {
        if (args.isEmpty() || !args.contains("|")) {
            event.getChannel().sendMessage("Please `provide the schedule in the format: <schedule> | <time in 'yyyy-MM-dd HH:mm' format>. Usage: !addschedule <schedule> | <time>").queue();
        } else {
            String[] parts = args.split("\\|", 2);
            String schedule = parts[0].trim();
            String time = parts[1].trim();
            schedules.add(schedule + " at " + time);
            event.getChannel().sendMessage("Schedule added: " + schedule + " at " + time).queue();
            scheduleNotifications(event.getChannel(), "Schedule: " + schedule, time);
        }
    }

    private void handleAssignmentCommand(MessageReceivedEvent event, String args) {
        if (assignments.isEmpty()) {
            event.getChannel().sendMessage("No assignments available.").queue();
        } else {
            StringBuilder response = new StringBuilder("Assignments:\n");
            for (String assignment : assignments) {
                response.append("- ").append(assignment).append("\n");
            }
            event.getChannel().sendMessage(response.toString()).queue();
        }
    }

    private void handleAddAssignmentCommand(MessageReceivedEvent event, String args) {
        if (args.isEmpty() || !args.contains("|")) {
            event.getChannel().sendMessage("Please provide the assignment in the format: <assignment> | <time in 'yyyy-MM-dd HH:mm' format>. Usage: !addassignment <assignment> | <time>").queue();
        } else {
            String[] parts = args.split("\\|", 2);
            String assignment = parts[0].trim();
            String time = parts[1].trim();
            assignments.add(assignment + " due at " + time);
            event.getChannel().sendMessage("Assignment added: " + assignment + " due at " + time).queue();
            scheduleNotifications(event.getChannel(), "Assignment: " + assignment, time);
        }
    }

    private void handleExamCommand(MessageReceivedEvent event, String args) {
        if (exams.isEmpty()) {
            event.getChannel().sendMessage("No exams available.").queue();
        } else {
            StringBuilder response = new StringBuilder("Exams:\n");
            for (String exam : exams) {
                response.append("- ").append(exam).append("\n");
            }
            event.getChannel().sendMessage(response.toString()).queue();
        }
    }

    private void handleAddExamCommand(MessageReceivedEvent event, String args) {
        if (args.isEmpty() || !args.contains("|")) {
            event.getChannel().sendMessage("Please provide the exam in the format: <exam> | <time in 'yyyy-MM-dd HH:mm' format>. Usage: !addexam <exam> | <time>").queue();
        } else {
            String[] parts = args.split("\\|", 2);
            String exam = parts[0].trim();
            String time = parts[1].trim();
            exams.add(exam + " at " + time);
            event.getChannel().sendMessage("Exam added: " + exam + " at " + time).queue();
            scheduleNotifications(event.getChannel(), "Exam: " + exam, time);
        }
    }

    private void handleMaterialsCommand(MessageReceivedEvent event) {
        if (materials.isEmpty()) {
            event.getChannel().sendMessage("No materials available.").queue();
        } else {
            StringBuilder response = new StringBuilder("Course Materials: \n");
            for (String material : materials) {
                response.append(material).append("\n");
            }
            event.getChannel().sendMessage(response.toString()).queue();
        }
    }

    private void handleAddMaterialCommand(MessageReceivedEvent event, String args) {
        if (args.isEmpty() || !args.contains("|")) {
            event.getChannel().sendMessage("Please provide the material in the format: <title> | <link>. Usage: !addmaterial <title> | <link>").queue();
        } else {
            String[] parts = args.split("\\|", 2);
            String title = parts[0].trim();
            String link = parts[1].trim();
            materials.add(String.format("- [%s](%s)", title, link));
            event.getChannel().sendMessage("Material added: " + title).queue();
        }
    }

    private void handlePastAssignmentsCommand(MessageReceivedEvent event) {
        if (pastAssignments.isEmpty()) {
            event.getChannel().sendMessage("No past assignments available.").queue();
        } else {
            StringBuilder response = new StringBuilder("Past Assignments:\n");
            for (String assignment : pastAssignments) {
                response.append("- ").append(assignment).append("\n");
            }
            event.getChannel().sendMessage(response.toString()).queue();
        }
    }

    private void handlePastExamsCommand(MessageReceivedEvent event) {
        if (pastExams.isEmpty()) {
            event.getChannel().sendMessage("No past exams available.").queue();
        } else {
            StringBuilder response = new StringBuilder("Past Exams:\n");
            for (String exam : pastExams) {
                response.append("- ").append(exam).append("\n");
            }
            event.getChannel().sendMessage(response.toString()).queue();
        }
    }

    private void scheduleNotifications(MessageChannel channel, String eventTitle, String time) {
        try {
            Date eventDate = dateFormat.parse(time);
            long currentTime = System.currentTimeMillis();
            long eventTime = eventDate.getTime();


            long delay12Hours = eventTime - TimeUnit.HOURS.toMillis(12) - currentTime;
            long delay1Hour = eventTime - TimeUnit.HOURS.toMillis(1) - currentTime;
            long delay10Minutes = eventTime - TimeUnit.MINUTES.toMillis(10) - currentTime;


            if (delay12Hours > 0) {
                scheduleNotification(channel, eventTitle, "in 12 hours", delay12Hours);
            }
            if (delay1Hour > 0) {
                scheduleNotification(channel, eventTitle, "in 1 hour", delay1Hour);
            }
            if (delay10Minutes > 0) {
                scheduleNotification(channel, eventTitle, "in 10 minutes", delay10Minutes);
            }
        } catch (Exception e) {
            channel.sendMessage("Error scheduling notifications: " + e.getMessage()).queue();
        }
    }


    private void scheduleNotification(MessageChannel channel, String eventTitle, String timeBefore, long delay) {
        scheduler.schedule(() -> channel.sendMessage("Reminder: " + eventTitle + " is happening " + timeBefore + "!").queue(), delay, TimeUnit.MILLISECONDS);
    }

    private void checkAndMoveCompletedTasks() {
        long currentTime = System.currentTimeMillis();

        assignments.removeIf(assignment -> {
            String time = assignment.split(" due at ")[1];
            try {
                Date assignmentDate = dateFormat.parse(time);
                if (currentTime > assignmentDate.getTime()) {
                     pastAssignments.add(assignment);
                    return true;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return false;
        });

        exams.removeIf(exam -> {
            String time = exam.split(" at ")[1];
            try {
                Date examDate = dateFormat.parse(time);
                if (currentTime > examDate.getTime()) {
                    pastExams.add(exam);
                    return true;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return false;
        });
    }
}
