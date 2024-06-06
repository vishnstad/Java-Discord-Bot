package Module3;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel;
import net.dv8tion.jda.api.managers.AudioManager;
import java.io.File;
import java.util.List;

public class PlayerManager {
    private static PlayerManager INSTANCE;
    private final AudioPlayerManager playerManager;
    private GuildMusicManager musicManager;

    private PlayerManager() {
        this.playerManager = new DefaultAudioPlayerManager();
        AudioSourceManagers.registerRemoteSources(playerManager);
        AudioSourceManagers.registerLocalSource(playerManager);
    }

    public synchronized GuildMusicManager getGuildMusicManager(Guild guild) {
        if (musicManager == null) {
            musicManager = new GuildMusicManager(playerManager);
            guild.getAudioManager().setSendingHandler(musicManager.getSendHandler());
        }
        return musicManager;
    }

    public void loadAndPlay(TextChannel channel, String trackUrl) {
        GuildMusicManager musicManager = getGuildMusicManager(channel.getGuild());

        playerManager.loadItem(trackUrl, new AudioLoadResultHandler() {
            @Override
            public void trackLoaded(AudioTrack track) {
                channel.sendMessage("Adding to queue " + track.getInfo().title).queue();
                play(channel.getGuild(), musicManager, track);
            }

            @Override
            public void playlistLoaded(AudioPlaylist playlist) {
                AudioTrack firstTrack = playlist.getSelectedTrack();
                if (firstTrack == null) {
                    firstTrack = playlist.getTracks().get(0);
                }
                channel.sendMessage("Adding to queue " + firstTrack.getInfo().title).queue();
                play(channel.getGuild(), musicManager, firstTrack);
            }

            @Override
            public void noMatches() {
                channel.sendMessage("Cannot find anything by " + trackUrl).queue();
            }

            @Override
            public void loadFailed(FriendlyException exception) {
                channel.sendMessage("Could not play: " + exception.getMessage()).queue();
                exception.printStackTrace();
            }
        });
    }
    public void loadAndPlayDirectory(TextChannel channel, String directoryPath) {
        File directory = new File(directoryPath);
        if (!directory.isDirectory()) {
            channel.sendMessage("The specified path is not a directory.").queue();
            return;
        }

        File[] files = directory.listFiles();
        if (files == null) {
            channel.sendMessage("Could not read the directory.").queue();
            return;
        }

        for (File file : files) {
            if (file.isFile() && file.canRead()) {
                String trackUrl = file.getAbsolutePath();
                playerManager.loadItem(trackUrl, new AudioLoadResultHandler() {
                    @Override
                    public void trackLoaded(AudioTrack track) {
                        channel.sendMessage("Adding to queue " + track.getInfo().title).queue();
                        play(channel.getGuild(), getGuildMusicManager(channel.getGuild()), track);
                    }

                    @Override
                    public void playlistLoaded(AudioPlaylist playlist) {
                        for (AudioTrack track : playlist.getTracks()) {
                            channel.sendMessage("Adding to queue " + track.getInfo().title).queue();
                            play(channel.getGuild(), getGuildMusicManager(channel.getGuild()), track);
                        }
                    }

                    @Override
                    public void noMatches() {
                        channel.sendMessage("Cannot find anything by " + trackUrl).queue();
                    }

                    @Override
                    public void loadFailed(FriendlyException exception) {
                        channel.sendMessage("Could not play: " + exception.getMessage()).queue();
                    }
                });
            }
        }
    }

    private void play(Guild guild, GuildMusicManager musicManager, AudioTrack track) {
        connectToFirstVoiceChannel(guild.getAudioManager());
        musicManager.scheduler.queue(track);
    }

    private void connectToFirstVoiceChannel(AudioManager audioManager) {
        if (!audioManager.isConnected()) {
            for (VoiceChannel voiceChannel : audioManager.getGuild().getVoiceChannels()) {
                audioManager.openAudioConnection(voiceChannel);
                break;
            }
        }
    }

    public static synchronized PlayerManager getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new PlayerManager();
        }
        return INSTANCE;
    }

    public void pause(Guild guild) {
        GuildMusicManager musicManager = getGuildMusicManager(guild);
        musicManager.player.setPaused(true);
    }

    public void resume(Guild guild) {
        GuildMusicManager musicManager = getGuildMusicManager(guild);
        musicManager.player.setPaused(false);
    }
    public List<String> getQueue(Guild guild) {
        GuildMusicManager musicManager = getGuildMusicManager(guild);
        return musicManager.scheduler.getQueue();
    }

    public void skip(Guild guild) {
        GuildMusicManager musicManager = getGuildMusicManager(guild);
        musicManager.scheduler.nextTrack();
    }
}

