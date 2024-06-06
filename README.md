# Java-Discord-Bot
This is a Java-Discord-Bot Developed for Second Semester project
Java Discord bot is built with IntelliJ and Maven.
Java programming already makes programming interesting when creating a Discord bot to implement a real-time interaction and automation solution on the popular chat platform Discord. Build, manage, and quickly deploy your new bot using the powerful development environment, IntelliJ IDEA, and project management and comprehension from Maven.
What is Maven?
Maven is quite like a build automation tool within the Java world—just that it makes the whole process easier and abstracted from the user. It does so by using a pom.xml file, wherein structure, metadata, dependencies, and even build instructions for the project are described. This way, the settings of the project are kept in a consistent state, and at the same time, the way of managing the dependency on other project libraries is quite effective.
Understanding pom.xml:
The pom.xml is the heart of a Maven project. It contains all of the project information that Maven uses to build the project, plus some other configuration options. Key elements include:
•	Project Coordinates: Define the project's unique identity using groupId, artifactId, and version.
•	Dependencies: List other libraries that the project uses, and therefore, Maven will be able to download automatically and include them in the building.
•	Build Configuration: Specify the build plugins and configurations that help modify the build process.
Developers can handle dependencies with less pain using Maven and ensure their project configuration is portable and reproducible in different environments.


Dependencies used (in pom.xml file):
<dependencies>
    <dependency>
        <groupId>com.github.ben-manes.caffeine</groupId>
        <artifactId>caffeine</artifactId>
        <version>3.1.1</version>
    </dependency>
    <dependency>
        <groupId>com.sun.mail</groupId>
        <artifactId>javax.mail</artifactId>
        <version>1.6.2</version>
    </dependency>
    <dependency>
        <groupId>com.sedmelluq</groupId>
        <artifactId>lavaplayer</artifactId>
        <version>1.3.78</version>
    </dependency>
    <dependency>
        <groupId>net.dv8tion</groupId>
        <artifactId>JDA</artifactId>
        <version>5.0.0-beta.22</version>
    </dependency>
    <dependency>
        <groupId>de.goldendeveloper</groupId>
        <artifactId>DCB-Core</artifactId>
        <version>1.9.0</version>
    </dependency>
    <dependency>
        <groupId>com.github.ben-manes.caffeine</groupId>
        <artifactId>caffeine</artifactId>
        <version>3.1.1</version> </dependency>
    <dependency>
        <groupId>javax.annotation</groupId>
        <artifactId>javax.annotation-api</artifactId>
        <version>1.3.2</version>
        <scope>compile</scope>
    </dependency>
    <dependency>
        <groupId>de.goldendeveloper</groupId>
        <artifactId>DCB-Core</artifactId>
        <version>1.9.0</version>
    </dependency>
    <dependency>
        <groupId>org.openjfx</groupId>
        <artifactId>javafx-controls</artifactId>
        <version>17.0.1</version>
    </dependency>
</dependencies> 


MODULE 1
Moderation and personal reminder:
Below are the classes of the module:
1.	Bot
2.	Slash
3.	ReminderBot

   
1.	Bot
The following code is the first test for the bot to come online. The unique bot token is taken from the Discord Developer’s portal after creating a bot.

2.	Slash:
This class is for moderation purposes.

3.	Reminder Bot:
The class adds a slash command for adding events and reminding at a specific time.


MODULE 2
Course management:
Below is the classes of the module:
1.	bot
2.	BotEventListener 
3.	Scheduledannouncements
  
1.	bot:
It receives the messages and triggers the bot to come online.
2.BotEventListener:
The bot manages the links for the subject repository.

MODULE 3
Music manager:
Below is the classes of the module:
1.	Bot
2.	Player Manager
3.	Track Scheduler
4.	Guild music manager
5.	Audio player send handler

This module is for listening to music. 


MODULE 4

Developing and Integrating the 2048
Game on Discord:
Below is the classes of the module:
1.	DiscordBot
2.   Game2048
3.   Game2048GUI

1. DiscordBot
We first import all the necessary libraries required for this project. Mainly imports for using Java Discord API (JDA) . DiscordBot.java is where the main method is being run. It handles commands to start a game, processes player movements via messages, and updates the game board displayed to the user. The game board is formatted to ensure cells are aligned correctly in the embedded message sent to the Discord channel.
2. Game2048
It manages the state and logic of a 2048 game, including:
•	Initializing the game board and adding random tiles.
•	Handling moves in four directions (up, down, left, right).
•	Merging tiles according to game rules.
•	Adding new tiles after a move.

3. Game2048GUI
The Game2048GUI class acts as an interface between the game's logic (Game2048 class) and a graphical user interface (GUI). It does not handle any actual rendering or interaction logic but serves to keep the current state of the game board that can be displayed or updated in the GUI.
