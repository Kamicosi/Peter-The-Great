package main;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.TreeMap;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

public class TrackScheduler extends AudioEventAdapter {

	private AudioPlayer player;
	private BlockingQueue<AudioTrack> queue;

	private TreeMap<String, ArrayList<Song>> playlists;

	public TrackScheduler(AudioPlayer player) {
		this.player = player;
		queue = new LinkedBlockingQueue<>();
		readPlaylistsFromFile();
	}

	public void queue(AudioTrack track) {
		if (!player.startTrack(track, true)) {
			queue.offer(track);
		}
	}

	public void sendQueue(GuildMessageReceivedEvent event, boolean displayFullQueue) {
		if (player.getPlayingTrack() != null) {
			EmbedBuilder queueMsg = new EmbedBuilder();

			queueMsg.setTitle("Queue");
			queueMsg.addField("Currently playing:", player.getPlayingTrack().getInfo().title, false);

			if (!queue.isEmpty() && displayFullQueue) {
				int i = 2;
				for (AudioTrack a : queue) {
					queueMsg.addField((i == 2) ? "Next:" : "#" + i, a.getInfo().title, true);
					i++;
					if (i > 12) {
						break;
					}
				}
			}
			queueMsg.setFooter("Use $skip to skip the current track or $stop to stop playback and clear the queue");
			queueMsg.setColor(0x005420);
			event.getChannel().sendMessage(queueMsg.build()).queue();
		} else {
			EmbedBuilder queueMsg = new EmbedBuilder();

			queueMsg.setTitle("Queue Empty");
			queueMsg.setFooter("Use $skip to skip the current track or $stop to stop playback and clear the queue");
			queueMsg.setColor(0x005420);
			event.getChannel().sendMessage(queueMsg.build()).queue();
		}

	}

	public void destroyQueue() {
		queue.clear();
		player.stopTrack();
	}

	public void nextTrack() {
		if (!queue.isEmpty()) {
			player.startTrack(queue.poll(), false);

			EmbedBuilder queueMsg = new EmbedBuilder();

			// queueMsg.setTitle("Track skipped");
			queueMsg.addField("Currently playing:", player.getPlayingTrack().getInfo().title, true);
			if (!queue.isEmpty()) {
				queueMsg.addField("After this:", queue.peek().getInfo().title, true);
			}
			// queueMsg.addField(player.getPlayingTrack().getInfo().author, "", false);
			queueMsg.setFooter("Use $queue to view the full queue");
			queueMsg.setColor(0x005420);
			VoiceCommands.mostRecentTextChannel.sendMessage(queueMsg.build()).queue();
		} else {
			player.stopTrack();
			VoiceCommands.audioManager.closeAudioConnection();
		}
	}

	public void skipTrack(GuildMessageReceivedEvent event) {
		if (!queue.isEmpty()) {
			player.startTrack(queue.poll(), false);

			EmbedBuilder queueMsg = new EmbedBuilder();

			queueMsg.setTitle("Track skipped");
			queueMsg.addField("Currently playing:", player.getPlayingTrack().getInfo().title, true);
			if (!queue.isEmpty()) {
				queueMsg.addField("After this:", queue.peek().getInfo().title, true);
			}
			// queueMsg.addField(player.getPlayingTrack().getInfo().author, "", false);
			queueMsg.setFooter("Use $queue to view the full queue");
			queueMsg.setColor(0x005420);
			event.getChannel().sendMessage(queueMsg.build()).queue();
		} else {
			player.stopTrack();
			try {
				VoiceCommands.audioManager.closeAudioConnection();
			} catch (NullPointerException e) {
				event.getJDA().getTextChannelById("703482913403961364").sendMessage(e.toString()).queue();
			}
		}
	}

	@Override
	public void onPlayerPause(AudioPlayer player) {

	}

	@Override
	public void onPlayerResume(AudioPlayer player) {

	}

	@Override
	public void onTrackStart(AudioPlayer player, AudioTrack track) {

	}

	@Override
	public void onTrackEnd(AudioPlayer player, AudioTrack track, AudioTrackEndReason endReason) {
		if (endReason == AudioTrackEndReason.FINISHED) {
			nextTrack();
		}

	}

	@Override
	public void onTrackException(AudioPlayer player, AudioTrack track, FriendlyException exception) {

	}

	@Override
	public void onTrackStuck(AudioPlayer player, AudioTrack track, long thresholdMs) {

	}

	// Playlist methods
	public void writePlaylistsToFile() {
		File f = new File("playlists.txt");

		try {
			FileOutputStream fos = new FileOutputStream(f);
			ObjectOutputStream oos = new ObjectOutputStream(fos);

			oos.writeObject(playlists);

			System.out.println("Playlists saved");
			oos.close();

		} catch (FileNotFoundException e) {
			System.out.println("Playlist saving failed");
			e.printStackTrace();
		} catch (IOException e) {
			System.out.println("Playlist saving failed");
			e.printStackTrace();
		}
	}

	@SuppressWarnings("unchecked")
	private void readPlaylistsFromFile() {
		File f = new File("playlists.txt");

		try {
			FileInputStream fis = new FileInputStream(f);
			ObjectInputStream ois = new ObjectInputStream(fis);

			playlists = (TreeMap<String, ArrayList<Song>>) ois.readObject();

			System.out.println("Data read");
			ois.close();

		} catch (FileNotFoundException e) {
			System.out.println("File reading failed");
			e.printStackTrace();
		} catch (IOException e) {
			System.out.println("File reading failed");
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}

	public void addSongToPlaylist(String name, AudioTrack track, GuildMessageReceivedEvent event) {
		EmbedBuilder result = new EmbedBuilder();
		result.setColor(0x005420);
		if (playlists.get(name) != null) {
			playlists.get(name).add(new Song(track.getInfo().title, track.getInfo().uri));

			result.setTitle("Song Added");
			result.addField("", "*" + track.getInfo().title + "* has been added to *" + name + "*", false);

			writePlaylistsToFile();
		} else {
			createPlaylist(name, track);
			result.setTitle("Playlist Created");
			result.addField("", "Playlist *" + name + "* has been created with the song *" + track.getInfo().title
					+ "* added to it", false);

		}
		event.getChannel().sendMessage(result.build()).queue();

	}

	public void createPlaylist(String name, AudioTrack track) {
		ArrayList<Song> result = new ArrayList<Song>();
		result.add(new Song(track.getInfo().title, track.getInfo().uri));
		playlists.put(name, result);
		writePlaylistsToFile();
	}

	public void deletePlaylist(String name, GuildMessageReceivedEvent event) {
		if (playlists.remove(name) != null) {
			event.getChannel().sendMessage("\"" + name + "\" successfully deleted").queue();
			writePlaylistsToFile();
		} else {
			event.getChannel().sendMessage("Could not find playlist \"" + name + "\"").queue();
		}
	}

	public void sendPlaylists(GuildMessageReceivedEvent event) {
		EmbedBuilder result = new EmbedBuilder();
		result.setTitle("All Playlists");
		for (String name : playlists.keySet()) {
			result.addField("", name, false);
		}
		result.setFooter("Use $p play [arg] to add the specified playlist to the queue");
		result.setColor(0x005420);
		event.getChannel().sendMessage(result.build()).queue();
	}

	public ArrayList<Song> getSongsFromPlaylist(String name) {
		if (playlists.get(name) != null) {
			return playlists.get(name);
		} else {
			return null;
		}
	}

	public void deleteSongFromPlaylist(String name, int songIndex) {
		playlists.get(name).remove(songIndex);
		writePlaylistsToFile();
	}

}
