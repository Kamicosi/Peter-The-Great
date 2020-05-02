package main;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.HashMap;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import RPG.BattleInfo;
import RPG.ProfileSaver;
import RPG.RPGProfile;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class TextCommands extends ListenerAdapter {

	private static HashMap<String, RPGProfile> profiles;

	private boolean waitingForBattle;
	private String battleP1, battleP2;

	public TextCommands() {
		waitingForBattle = false;
		battleP1 = "";
		battleP2 = "";

		profiles = RPGProfile.readProfilesFromFile();

		for (RPGProfile p : profiles.values()) {
			p.initialize();
		}

		Thread saveProfilesThread = new Thread(new ProfileSaver(profiles));
		saveProfilesThread.start();
	}

	@Override
	public void onGuildMessageReceived(GuildMessageReceivedEvent event) {

		// add XP
		if (profiles.get(event.getAuthor().getName()) != null) {
			profiles.get(event.getAuthor().getName()).handleEvent(event);
		} else {
			RPGProfile newProfile = null;
			String oldName = "";
			for (RPGProfile r : profiles.values()) {
				if (event.getAuthor().getId().equals(r.getID())) {
					oldName = r.getName();
					r.setName(event.getAuthor().getName());
					newProfile = r;
					break;
				}
			}
			if (newProfile != null) {
				profiles.remove(oldName);
				profiles.put(event.getAuthor().getName(), newProfile);
			} else {
				System.out.println("Creating new profile...");
				profiles.put(event.getAuthor().getName(),
						new RPGProfile(event.getAuthor().getName(), event.getAuthor().getId()));
			}
		}

		String[] args = event.getMessage().getContentRaw().split(" ");

		// Battle!
		if (waitingForBattle && event.getAuthor().getName().equals(battleP2)
				&& event.getMessage().getContentRaw().equals("Y")) {
			executeBattle(event);
		} else if (waitingForBattle && event.getAuthor().getName().equals(battleP2) // Decline battle
				&& event.getMessage().getContentRaw().equals("N")) {
			event.getChannel().sendMessage(battleP2 + " has declined the battle!").queue();
			waitingForBattle = false;
			battleP1 = "";
			battleP2 = "";
		}

		if (args[0].startsWith(Bot.prefix)) {
			System.out.println("Command received: " + args[0].substring(1).toLowerCase());
			switch (args[0].substring(1).toLowerCase()) {
			case "pog":
				pog(event);
				break;
			case "print":
				print(event);
				break;
			case "help":
				help(event);
				break;
			case "texthelp":
				textHelp(event);
				break;
			case "gif":
				gif(event);
				break;
			case "xp":
				try {
					if (args[1].toLowerCase().equals("all")) {
						EmbedBuilder xp = new EmbedBuilder().setTitle("Level stats");
						for (RPGProfile p : profiles.values()) {
							xp.addField("Level " + p.getLevel(),
									p.getName() + "\n" + p.getXP() + "/" + p.XPThreshold[p.getLevel()] + " XP", true);
						}
						xp.setColor(0x005420);
						event.getChannel().sendMessage(xp.build()).queue();
					}
				} catch (IndexOutOfBoundsException e) {
					event.getJDA().getTextChannelById("703482913403961364").sendMessage(e.toString()).queue();
				}
				break;
			case "meme":
				meme(event);
				break;
			case "profile":
				profiles.get(event.getAuthor().getName()).sendProfile(event);
				break;
			case "battle":
				try {
					battleP2 = event.getMessage().getContentDisplay().substring(9);
					battleP1 = event.getAuthor().getName();
					if (!battleP1.equals(battleP2)) {
						waitingForBattle = true;

						event.getChannel().sendMessage(battleP1 + " has challenged " + battleP2 + " to a battle! Will "
								+ battleP2 + " accept? (Y/N)").queue();
					} else {
						waitingForBattle = false;
						battleP1 = "";
						battleP2 = "";
					}
				} catch (IndexOutOfBoundsException e) {
					event.getJDA().getTextChannelById("703482913403961364").sendMessage(e.toString()).queue();
				}
				break;

			}
		} else if (event.getMessage().getContentRaw().toLowerCase().contains("peter"))

		{
			event.getChannel().sendMessage("Did someone say my name?").queue();
		}

	}

	private void executeBattle(GuildMessageReceivedEvent event) {
		try {
			// System.out.println("Battle method entered");
			RPGProfile r = profiles.get(battleP1);
			BattleInfo result = profiles.get(event.getAuthor().getName()).battle(r);

			EmbedBuilder info = new EmbedBuilder().setTitle("Battle Result");
			info.addField("", battleP1 + " rolled a " + result.roll1, false);
			info.addField("", battleP2 + " rolled a " + result.roll2, false);
			if (result.tie) {
				info.addField("", "It's a tie!", false);
			} else {
				info.addField("", result.winner + " won!", false);
				info.addField("", result.loser + " took " + result.difference + " points of damage!!", false);
				profiles.get(result.loser).takeDamage(result.difference);
			}

			info.setColor(0x005420);
			event.getChannel().sendMessage(info.build()).queue();

		} catch (Exception e) {
			event.getJDA().getTextChannelById("703482913403961364").sendMessage(e.toString()).queue();
			e.printStackTrace();
		}

		waitingForBattle = false;
		battleP1 = "";
		battleP2 = "";
	}

	// Test command to make peter send poggers
	private void pog(GuildMessageReceivedEvent event) {
		event.getChannel().sendTyping();
		event.getChannel().sendMessage("***poggers!***").queue();
	}

	private void gif(GuildMessageReceivedEvent event) {
		event.getJDA().getTextChannelById("687462550589407272").sendMessage("!gif").queue();
	}

	// Make peter say anyhting!
	private void print(GuildMessageReceivedEvent event) {
		if (event.getAuthor().getId() != "341259856872996866") {
			event.getChannel().sendTyping();
			try {
				event.getChannel().sendMessage(event.getMessage().getContentRaw().substring(7)).queue();
			} catch (IndexOutOfBoundsException e) {
				// Errors channel
				event.getJDA().getTextChannelById("703482913403961364").sendMessage(e.toString()).queue();
				event.getChannel().sendMessage(
						"Are you trying to bloody print an *empty string*, " + event.getAuthor().getAsMention() + "?")
						.queue();
			}
		}
	}

	private void meme(GuildMessageReceivedEvent event) {
		try {
			JSONParser parser = new JSONParser();

			URL memeURL = new URL("https://meme-api.herokuapp.com/gimme");
			BufferedReader bufferedReader = new BufferedReader(
					new InputStreamReader(memeURL.openConnection().getInputStream()));

			String lines;

			while ((lines = bufferedReader.readLine()) != null) {
				JSONArray array = new JSONArray();
				array.add(parser.parse(lines));

				for (Object o : array) {
					JSONObject jsonObject = (JSONObject) o;

					String postLink = (String) jsonObject.get("postLink");
					String title = (String) jsonObject.get("title");
					String url = (String) jsonObject.get("url");

					EmbedBuilder meme = new EmbedBuilder();
					meme.setTitle(title);
					meme.setImage(url);
					meme.setColor(0x005420);
					event.getChannel().sendMessage(meme.build()).queue();
				}
			}

			bufferedReader.close();

		} catch (Exception e) {
			event.getChannel().sendMessage("Uh-oh, the meme obbied. Try again in a sec").queue();
			e.printStackTrace();
		}
	}

	// Command info
	private void help(GuildMessageReceivedEvent event) {
		EmbedBuilder help = new EmbedBuilder();

		help.setTitle("General info");

		help.addField("", "Hey everybody, I'm your favorite hard-working average american dad! Use '" + Bot.prefix
				+ "' followed by these commands to see all the cool things I can do!", false);
		help.addField("texthelp", "Display all the text-related commands", true);
		help.addField("voicehelp", "Display all voice-related commands", true);
		help.setColor(0x005420);
		help.setFooter("Created by Cosimos Cendo in Spring 2020");

		event.getChannel().sendMessage(help.build()).queue();
		help.clear();
	}

	private void textHelp(GuildMessageReceivedEvent event) {
		EmbedBuilder help = new EmbedBuilder();

		help.setTitle("Text Commands");
		help.addField("pog", "Make Peter say poggers!", true);

		help.addField("print [arg]", "Make peter say anything!", true);

		help.addField("gif", "Assist ZeBot in posting the mario and sonic gif through epic bot collaboration!", true);

		help.addField("meme", "Send a random meme!", true);

		help.addField("Profile", "Show your current XP, level, and inventory", true);

		help.addField("xp all", "Shows everybody's current XP and level", true);

		help.addField("battle [@member]", "Challenge someone to a battle!", true);

		help.setColor(0x005420);
		help.setFooter("Precede each command with '" + Bot.prefix + "' to use it");
		event.getChannel().sendMessage(help.build()).queue();
		help.clear();
	}

}
