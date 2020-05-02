package main;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.VoiceChannel;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.managers.AudioManager;

public class VoiceCommands extends ListenerAdapter {

	private static AudioPlayerManager playerManager;
	private static AudioPlayer player;
	private static TrackScheduler trackScheduler;
	public static AudioManager audioManager;

	public static TextChannel mostRecentTextChannel;

	private final int MAX_COMMAND_LENGTH = 100;

	public VoiceCommands() {
		playerManager = new DefaultAudioPlayerManager();
		AudioSourceManagers.registerRemoteSources(playerManager);
		AudioSourceManagers.registerLocalSource(playerManager);

		player = playerManager.createPlayer();
		trackScheduler = new TrackScheduler(player);
		player.addListener(trackScheduler);
	}

	@Override
	public void onGuildMessageReceived(GuildMessageReceivedEvent event) {
		String[] args = event.getMessage().getContentRaw().split(" ");

		if (args[0].startsWith(Bot.prefix)) {
			if (event.getMessage().getContentRaw().length() < MAX_COMMAND_LENGTH) {
				mostRecentTextChannel = event.getChannel();
				switch (args[0].substring(1).toLowerCase()) {
				case "add":
					try {
						playURL(event, event.getMessage().getContentRaw().substring(5));
					} catch (IndexOutOfBoundsException e) {
						event.getJDA().getTextChannelById("703482913403961364").sendMessage(e.toString()).queue();
						event.getChannel().sendMessage(event.getAuthor().getAsMention()
								+ ", you wanker!! You have to supply a valid SoundCloud/YouTube URL or search term!!!")
						.queue();
					}
					break;
				case "stop":
					stop();
					break;
				case "queue":
					trackScheduler.sendQueue(event, true);
					break;
				case "skip":
					trackScheduler.skipTrack(event);
					// trackScheduler.sendQueue(event, false);
					break;
				case "voicehelp":
					voiceHelp(event);
					break;
				case "p":
					handlePlaylistStuff(event, args);
					break;
				}
			} else {
				event.getChannel().sendMessage("That command is too bloody long and I refuse to abide by it");
			}
		}
	}

	// Play song
	private void playURL(GuildMessageReceivedEvent event, String url) {

		VoiceChannel channel = event.getMember().getVoiceState().getChannel();
		// Member is not in a voice channel
		if (channel == null) {
			event.getChannel().sendMessage("You're not in a bloody voice channel, wanker!").queue();
			return;
		}
		// If peter already hasn't gotten the audioManager
		if (audioManager == null) {
			audioManager = event.getGuild().getAudioManager();
		}
		// Peter is in the middle of connecting
		if (audioManager.isAttemptingToConnect()) {
			event.getChannel().sendMessage("Hold on, Peter is bloody joining!! Wait a minute!").queue();
			return;
		}
		// Peter is not already connected
		if (!audioManager.isConnected()) {
			audioManager.openAudioConnection(channel);

			audioManager.setSendingHandler(new PeterAudioSendHandler(player));
		}

		if (!isUrl(url)) {
			url = "ytsearch:" + url;
		}

		playerManager.loadItem(url, new AudioLoadResultHandler() {

			@Override
			public void trackLoaded(AudioTrack track) {
				trackScheduler.queue(track);
				EmbedBuilder result = new EmbedBuilder();
				result.setColor(0x005420);
				result.setTitle("Song Queued");
				result.addField("", "*" + track.getInfo().title + "* has been added to the queue", false);
				result.setFooter("Use $queue to view the queue");
				event.getChannel().sendMessage(result.build()).queue();
			}

			@Override
			public void playlistLoaded(AudioPlaylist playlist) {
				trackScheduler.queue(playlist.getTracks().get(0));
				EmbedBuilder result = new EmbedBuilder();
				result.setColor(0x005420);
				result.setTitle("Song Queued");
				result.addField("", "*" + playlist.getTracks().get(0).getInfo().title + "* has been added to the queue",
						false);
				result.setFooter("Use $queue to view the queue");
				event.getChannel().sendMessage(result.build()).queue();
			}

			@Override
			public void noMatches() {

				event.getChannel().sendMessage("I did not find anything on YouTube for that").queue();

			}

			@Override
			public void loadFailed(FriendlyException exception) {
				event.getChannel().sendMessage("Failed to load file").queue();
				event.getJDA().getTextChannelById("703482913403961364").sendMessage(exception.toString()).queue();
				exception.printStackTrace();
			}

		});
		player.setVolume(40);
	}

	// Check if string is a valid url
	private boolean isUrl(String url) {
		try {
			@SuppressWarnings("unused")
			URL isUrl = new URL(url);
			return true;
		} catch (MalformedURLException e) {
			return false;
		}
	}

	// Stop Peter
	public static void stop() {
		trackScheduler.destroyQueue();
		if (audioManager != null && audioManager.isConnected()) {
			audioManager.closeAudioConnection();
		}
	}

	/* Playlist commands */
	private void handlePlaylistStuff(GuildMessageReceivedEvent event, String[] args) {
		try {
			switch (args[1]) {
			case "list":
				list(event);
				break;
			case "add":
				addSongToPlaylist(event);
				break;
			case "delete":
				try {
					delete(event);
				} catch (IndexOutOfBoundsException e) {
					event.getChannel()
					.sendMessage(
							"You bloody imbecile!!! You have to give me the name of a playlist to delete it!!!")
					.queue();
				}
				break;
			case "play":
				play(event);
				break;
			}
		} catch (IndexOutOfBoundsException e) {
			System.out.println("Caught indexoutofboundsexception in handlePlaylistStuff");
		}
	}

	// Sends all playlists if no argument, otherwise lists songs in specified
	// playlist
	private void list(GuildMessageReceivedEvent event) {
		try {
			EmbedBuilder result = new EmbedBuilder();
			String playlist = event.getMessage().getContentRaw().substring(8).strip();
			result.setTitle(playlist);
			int i = 1;
			for (Song s : trackScheduler.getSongsFromPlaylist(playlist)) {
				result.addField("#" + i, s.title, true);
				i++;
			}
			result.setFooter("Use $p play " + playlist + " to add this playlist to the queue");
			result.setColor(0x005420);
			event.getChannel().sendMessage(result.build()).queue();
		} catch (IndexOutOfBoundsException e) {
			trackScheduler.sendPlaylists(event);
		} catch (NullPointerException e) {
			event.getChannel()
			.sendMessage(
					"Could not find playlist *" + event.getMessage().getContentRaw().substring(8).strip() + "*")
			.queue();
		}
	}

	// Adds song to playlist, or creates one if the playlist doesn't exist
	private void addSongToPlaylist(GuildMessageReceivedEvent event) {
		if (event.getMessage().getContentRaw().contains(",")) {
			try {
				String[] args = event.getMessage().getContentRaw().split(",");
				String song = args[0].substring(7).strip();
				String playlist = args[1].strip();

				System.out.println("Song: " + song);
				System.out.println("Playlist: " + playlist);

				if (!isUrl(song)) {
					song = "ytsearch:" + song;
				}

				final String playlistFinal = playlist;

				playerManager.loadItem(song, new AudioLoadResultHandler() {

					@Override
					public void trackLoaded(AudioTrack track) {
						trackScheduler.addSongToPlaylist(playlistFinal, track, event);
						System.out.println("Track Loaded! :)");
					}

					@Override
					public void playlistLoaded(AudioPlaylist playlist) {
						trackScheduler.addSongToPlaylist(playlistFinal, playlist.getTracks().get(0), event);

					}

					@Override
					public void noMatches() {
						event.getChannel().sendMessage("I did not find anything on YouTube for that").queue();

					}

					@Override
					public void loadFailed(FriendlyException exception) {
						System.out.println("Loading track failed in add method");
						exception.printStackTrace();

					}

				});
			} catch (IndexOutOfBoundsException e) {
				System.out.println("Caught OutOfBoundsException in add command");
				e.printStackTrace();
			}
		}
	}

	// Deletes playlist or song from playlist
	public void delete(GuildMessageReceivedEvent event) {
		String message = event.getMessage().getContentRaw();
		if (message.contains(",")) {
			String[] args = message.split(",");
			String playlist = args[0].substring(10).strip();

			int song = Integer.parseInt(args[1].replaceAll("[^0-9]", "")) - 1;

			try {
				String removedSong = trackScheduler.getSongsFromPlaylist(playlist).get(song).title;
				trackScheduler.getSongsFromPlaylist(playlist).remove(song);
				trackScheduler.writePlaylistsToFile();
				event.getChannel().sendMessage("*" + removedSong + "* has been removed from *" + playlist + "*")
				.queue();
			} catch (NumberFormatException e) {
				event.getChannel().sendMessage("You didn't provide me a valid song number, idiot!!").queue();
			}
		} else {
			trackScheduler.deletePlaylist(message.substring(10).strip(), event);
		}
	}

	// Adds playlist to queue
	private void play(GuildMessageReceivedEvent event) {
		try {

			String playlistName = event.getMessage().getContentRaw().substring(8);
			ArrayList<Song> songs = trackScheduler.getSongsFromPlaylist(playlistName);
			if (songs != null) {
				connectToVc(event);
				@SuppressWarnings("unchecked")
				ArrayList<Song> songsShuffled = (ArrayList<Song>) songs.clone();
				Collections.shuffle(songsShuffled);

				for (Song song : songsShuffled) {
					playerManager.loadItem(song.url, new AudioLoadResultHandler() {

						@Override
						public void trackLoaded(AudioTrack track) {
							trackScheduler.queue(track);
						}

						@Override
						public void playlistLoaded(AudioPlaylist playlist) {
							trackScheduler.queue(playlist.getTracks().get(0));
						}

						@Override
						public void noMatches() {

							event.getChannel().sendMessage("I did not find anything on YouTube for that").queue();

						}

						@Override
						public void loadFailed(FriendlyException exception) {
							event.getChannel().sendMessage("Failed to load file").queue();
							event.getJDA().getTextChannelById("703482913403961364").sendMessage(exception.toString())
							.queue();
							exception.printStackTrace();
						}

					});
				}

				event.getChannel().sendMessage("Added playlist " + playlistName + " to queue").queue();
			} else {
				event.getChannel().sendMessage("I could not find playlist *" + playlistName + "*").queue();
			}

		} catch (IndexOutOfBoundsException e) {
			event.getChannel()
			.sendMessage(
					"Oh my goodness!! You have to bloody give me the name of the playlist you want to play!!!")
			.queue();
		}
	}

	private void connectToVc(GuildMessageReceivedEvent event) {
		VoiceChannel channel = event.getMember().getVoiceState().getChannel();
		// If peter already hasn't gotten the audioManager
		if (audioManager == null) {
			audioManager = event.getGuild().getAudioManager();
		}
		// Peter is in the middle of connecting
		if (audioManager.isAttemptingToConnect()) {
			event.getChannel().sendMessage("Hold on, Peter is bloody joining!! Wait a minute!").queue();
			return;
		}
		// Peter is not already connected
		if (!audioManager.isConnected()) {
			audioManager.openAudioConnection(channel);

			audioManager.setSendingHandler(new PeterAudioSendHandler(player));
		}
		// Member is not in a voice channel
		if (channel == null) {
			event.getChannel().sendMessage("You're not in a bloody voice channel, wanker!").queue();
			return;
		}

	}

	// Displays commands
	private void voiceHelp(GuildMessageReceivedEvent event) {
		EmbedBuilder help = new EmbedBuilder();

		help.setTitle("Voice Commands");

		help.addField("add [arg]",
				"Adds the specified YouTube/SoundCloud URL to the queue, or searches YouTube if not a valid URL", true);
		help.addField("skip", "Skips the current song in the queue", true);
		help.addField("stop", "Stops playback, clears the queue, and disconnects from the voice chat", true);
		help.addField("queue", "Displays the current queue", true);

		help.addBlankField(false);
		help.addField("p list", "Lists all playlists", true);
		help.addField("p list [playlist]", "Lists all songs in the [playlist]", true);
		help.addField("p delete [playlist], [song #]",
				"Deletes the song from the playlist. If no number specified, the entire playlist will be deleted",
				true);
		help.addField("p play [playlist]", "Adds the [playlist] to the queue and shuffles it", true);
		help.addField("p add [song], [playlist]", "Adds the [song] to the [playlist], **seperated by a comma**. "
				+ "Creates new playlist if the specified playlist doesn't exist", true);

		help.setColor(0x005420);

		help.setFooter("Precede each command with '" + Bot.prefix + "' to use it");

		event.getChannel().sendMessage(help.build()).queue();
		help.clear();
	}

}
