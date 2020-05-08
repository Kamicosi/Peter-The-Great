package main;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Scanner;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import RPG.BattleInfo;
import RPG.ItemType;
import RPG.ProfileSaver;
import RPG.RPGItems;
import RPG.RPGItemsPool;
import RPG.RPGProfile;
import RPG.Shop;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class TextCommands extends ListenerAdapter {

	private HashMap<String, RPGProfile> profiles;

	private boolean waitingForBattle;
	private String battleP1, battleP2;

	private Shop shop;

	public TextCommands(HashMap<String, RPGProfile> profiles) {
		resetBattle();

		this.profiles = profiles;

		for (RPGProfile p : profiles.values()) {
			p.initialize();
		}

		shop = new Shop(profiles);

		new Thread(new ProfileSaver(profiles)).start();
	}

	@Override
	public void onGuildMessageReceived(GuildMessageReceivedEvent event) {

		// add XP
		if (profiles.get(event.getAuthor().getId()) != null) {
			profiles.get(event.getAuthor().getId()).handleEvent(event);
		} else {
			System.out.println("Creating new profile...");
			profiles.put(event.getAuthor().getId(),
					new RPGProfile(event.getAuthor().getName(), event.getAuthor().getId()));
		}

		String[] args = event.getMessage().getContentRaw().split(" ");

		// Battle!
		if (waitingForBattle && event.getAuthor().getId().equals(battleP2)
				&& event.getMessage().getContentRaw().toLowerCase().equals("y")) {
			executeBattle(event);
		} else if (waitingForBattle && event.getAuthor().getId().equals(battleP2) // Decline battle
				&& event.getMessage().getContentRaw().toLowerCase().equals("n")) {
			event.getChannel().sendMessage("<@" + battleP2 + "> has declined the battle!").queue();
			resetBattle();
		}

		if (args[0].startsWith(Bot.prefix)) {
			System.out.println("Command received: " + args[0].substring(1).toLowerCase());
			switch (args[0].substring(1).toLowerCase()) {
			case "pog":
				if (!isDead(event)) {
					pog(event);
				}
				break;
			case "print":
				if (!isDead(event)) {
					print(event);
				}
				break;
			case "help":
				help(event);
				break;
			case "texthelp":
				textHelp(event);
				break;
			case "gif":
				if (!isDead(event)) {
					gif(event);
				}
				break;
			case "xp":
				xp(event);
				break;
			case "meme":
				if (!isDead(event)) {
					meme(event);
				}
				break;
			case "profile":
				sendProfile(event);
				break;
			case "battle":
				if (!isDead(event)) {
					waitForBattle(event);
				}
				break;
			case "use":
				use(event);
				break;
			case "image":
				image(event);
				break;
			case "shop":
				shop.handleShopStuff(event);
				break;

			}
		} else if (event.getMessage().getContentRaw().toLowerCase().contains("peter"))

		{
			event.getChannel().sendMessage("Did someone say my name?").queue();
		}

	}

	// Test command to make peter send poggers
	private void pog(GuildMessageReceivedEvent event) {
		event.getChannel().sendTyping();
		event.getChannel().sendMessage("***poggers!***").queue();
	}

	// Make peter say anyhting!
	private void print(GuildMessageReceivedEvent event) {
		if (!event.getAuthor().getId().equals("341259856872996866")) {
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

	// Send sonic and mario gif
	private void gif(GuildMessageReceivedEvent event) {
		event.getJDA().getTextChannelById("687462550589407272").sendMessage("!gif").queue();
	}

	private void xp(GuildMessageReceivedEvent event) {
		EmbedBuilder xp = new EmbedBuilder().setTitle("Level stats");
		for (RPGProfile p : profiles.values()) {
			xp.addField("Level " + p.getLevel(),
					p.getName() + "\n" + p.getXP() + "/" + p.XPThreshold[p.getLevel()] + " XP", true);
		}
		xp.setColor(Bot.COLOR);
		event.getChannel().sendMessage(xp.build()).queue();
	}

	private void sendProfile(GuildMessageReceivedEvent event) {
		profiles.get(event.getAuthor().getId()).sendProfile(event);
	}

	private void resetBattle() {
		waitingForBattle = false;
		battleP1 = "";
		battleP2 = "";
	}

	private void waitForBattle(GuildMessageReceivedEvent event) {
		try {
			battleP2 = getID(event);
			battleP1 = event.getAuthor().getId();
			if (!battleP1.equals(battleP2)) {
				if (profiles.get(battleP2) != null) {
					waitingForBattle = true;

					event.getChannel().sendMessage("<@" + battleP1 + "> has challenged <@" + battleP2
							+ "> to a battle! Will <@" + battleP2 + "> accept? (Y/N)").queue();
				} else {
					event.getChannel().sendMessage("<@" + battleP1 + ">, invalid battle request").queue();
				}
			} else {
				event.getChannel().sendMessage("<@" + battleP1 + ">, you cannot challenge yourself to a battle!!!")
				.queue();
				resetBattle();
			}
		} catch (IndexOutOfBoundsException e) {
			event.getJDA().getTextChannelById("703482913403961364").sendMessage(e.toString()).queue();
		}
	}

	private void executeBattle(GuildMessageReceivedEvent event) {
		try {
			// System.out.println("Battle method entered");
			RPGProfile r = profiles.get(battleP1);
			BattleInfo result = profiles.get(event.getAuthor().getId()).battle(r);

			EmbedBuilder info = new EmbedBuilder().setTitle(":game_die: Battle Result");
			info.addField("Player 1", "<@" + result.ID1 + "> rolled a " + result.roll1, true);
			info.addField("Player 2", "<@" + result.ID2 + "> rolled a " + result.roll2, true);
			if (result.tie) {
				info.addField("", "It's a tie!", false);
			} else {
				info.addField("", "Winner: <@" + result.winner + ">!", false);
				info.addField("", "<@" + result.loser + "> took " + result.difference + " points of damage!!", false);
				if (profiles.get(result.loser).takeDamage(result.difference) == 0) {
					event.getChannel().sendMessage("<@" + result.loser + "> has died!!").queue();
				}
			}
			info.setColor(0x005420);
			event.getChannel().sendMessage(info.build()).queue();

		} catch (Exception e) {
			event.getJDA().getTextChannelById("703482913403961364").sendMessage(e.toString()).queue();
			e.printStackTrace();
		}

		resetBattle();
	}

	@SuppressWarnings("unchecked")
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

	private void image(GuildMessageReceivedEvent event) {
		String searchTerm = event.getMessage().getContentRaw().substring(7);
		String imageUrl = returnBingImage(searchTerm);
		if (imageUrl == null) {

			event.getChannel().sendMessage("Idk what happened but there was an error").queue();
		} else if (imageUrl == "didnt find results") {
			event.getChannel().sendMessage("Didn't find results for " + searchTerm).queue();
		} else {
			EmbedBuilder image = new EmbedBuilder();
			System.out.println(imageUrl);
			image.setImage(imageUrl);

			image.setColor(0x005420);
			event.getChannel().sendMessage(image.build()).queue();

		}
	}

	private String returnBingImage(String term) {
		String searchTerm = term.strip().replace(" ", "+");
		try {
			URL url = new URL("https://www.bing.com/images/search?q=" + searchTerm + "&safesearch=Off&FORM=HDRSC2");
			Scanner scanner = new Scanner(url.openStream());
			StringBuffer pageContent = new StringBuffer();
			while (scanner.hasNext()) {
				String line = scanner.nextLine();
				pageContent.append(line);
			}
			scanner.close();
			// System.out.println(pageContent);
			int index = pageContent.indexOf("<a class=\"thumb\" target=\"_blank\" href=\"");
			if (index != -1) {
				String image = pageContent.substring(index + 39);
				image = image.substring(0, image.indexOf("h=") - 2);
				return image;
			} else {
				// Try again with bing near:
				url = new URL("https://www.bing.com/images/search?q=bing+near:" + searchTerm + "&safesearch=Off&FORM=HDRSC2");
				scanner = new Scanner(url.openStream());
				pageContent = new StringBuffer();
				while (scanner.hasNext()) {
					String line = scanner.nextLine();
					pageContent.append(line);
				}
				scanner.close();
				// System.out.println(pageContent);
				index = pageContent.indexOf("<a class=\"thumb\" target=\"_blank\" href=\"");
				if (index != -1) {
					String image = pageContent.substring(index + 39);
					image = image.substring(0, image.indexOf("h=") - 2);
					return image;
				} else {
					return "didnt find results";
				}
			}
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	private void use(GuildMessageReceivedEvent event) {
		String[] args = event.getMessage().getContentRaw().split(",");
		RPGItems item = RPGItemsPool.allItems.get(args[0].substring(5).strip());
		String userID = event.getMessage().getAuthor().getId();

		if (item != null && profiles.get(userID).items.get(item) > 0) {
			try {
				EmbedBuilder action = new EmbedBuilder();
				if (item.getType().equals(ItemType.HEAL)) {
					/* heal */
					profiles.get(userID).heal(item.getPower());

					/* Heal message */
					action.setTitle(event.getAuthor().getName() + " Healed!!");
					action.addField("", "<@" + userID + "> used a " + item.getName() + " and healed " + item.getPower()
					+ " points!!", false);
					action.addField("", "HP: " + profiles.get(userID).getHealth(), false);
				} else { // Type is DAMAGE
					/* take damage */
					String targetID = getID(event);
					profiles.get(targetID).takeDamage(item.getPower());
					if (profiles.get(targetID).getHP() == 0) {
						event.getChannel().sendMessage("<@" + targetID + "> has died!!").queue();
					}

					/* Attack message */
					action.setTitle(
							event.getAuthor().getName() + " Attacked " + profiles.get(targetID).getName() + "!!!");
					action.addField("", "<@" + userID + "> used a " + item.getName() + " and attacked <@" + targetID
							+ "> dealing " + item.getPower() + " damage!!", false);
					action.addField("", "<@" + targetID + ">'s HP: " + profiles.get(targetID).getHealth(), false);
				}
				/* decrease number of item */
				int count = (profiles.get(userID).items.get(item)) - 1;
				profiles.get(userID).items.put(item, count);

				action.setColor(0x005420);
				event.getChannel().sendMessage(action.build()).queue();
			} catch (NullPointerException e) {
				event.getChannel()
				.sendMessage(
						"<@" + event.getAuthor().getId() + ">, you need to @ someone to use an attack item")
				.queue();
			}
		} else {
			event.getChannel().sendMessage(
					"Could not find item *" + args[0].substring(5).strip() + "* in your inventory, <@" + userID + ">")
			.queue();
		}
	}

	private boolean isDead(GuildMessageReceivedEvent event) {
		if (profiles.get(event.getAuthor().getId()).getHP() > 0) {
			return false;
		} else {
			event.getChannel().sendMessage("Sorry <@" + event.getAuthor().getId()
					+ ">, you cannot use this command when you're dead. Use an item to heal or level up first then try again!")
			.queue();
			return true;
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
		help.setColor(Bot.COLOR);
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

		help.addField("image [term]", "Returns the top Bing image serach result for that term", true);

		help.addField("profile", "Show your current XP, level, and inventory", true);

		help.addField("xp", "Shows everybody's current XP and level", true);

		help.addField("battle [@member]", "Challenge someone to a battle!", true);

		help.addField("XP$Grind", "The dedicated grinding keyword", true);

		help.addField("use [item], [@member]", "Use an item. You will need to @ someone if it's an attack item", true);

		help.addField("shop list", "Shows Peter's shop", true);

		help.setColor(Bot.COLOR);
		help.setFooter("Precede each command with '" + Bot.prefix + "' to use it");
		event.getChannel().sendMessage(help.build()).queue();
		help.clear();
	}

	private String getID(GuildMessageReceivedEvent event) {
		return event.getMessage().getContentRaw().replaceAll("[^0-9]", "");
	}

}
