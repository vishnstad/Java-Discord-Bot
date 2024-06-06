package Module2;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.requests.GatewayIntent;

public class bot {

    public static void main(String[] args) {

            JDA jda = JDABuilder.createDefault("add your bot token")
                    .enableIntents(GatewayIntent.MESSAGE_CONTENT)
                    .setActivity(Activity.watching("course schedules"))
                    .build();



    }
}

