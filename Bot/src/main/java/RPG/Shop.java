package RPG;

import java.util.HashMap;

import main.Bot;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

public class Shop {

	private HashMap<String, RPGProfile> profiles;
	private static final int RANDOM_CRINGE = 5, RANDOM_COMMON = 20, RANDOM_RARE = 35, RANDOM_EPIC = 50;

	public Shop(HashMap<String, RPGProfile> profiles) {
		this.profiles = profiles;
	}

	public void handleShopStuff(GuildMessageReceivedEvent event) {
		String[] args = event.getMessage().getContentRaw().split(" ");
		try {
			switch(args[1].toLowerCase()) {
			case "buy":
				switch(args[2].toLowerCase()) {
				case "cringe":
					buyCringe(event);
					break;
				case "common":
					buyCommon(event);
					break;
				case "rare":
					buyRare(event);
					break;
				case "epic":
					buyEpic(event);
					break;
				}
				break;
			}
		} catch (IndexOutOfBoundsException e) {
			sendShop(event);
		}
	}

	private void buyCringe(GuildMessageReceivedEvent event) {
		RPGItems item = profiles.get(event.getAuthor().getId()).purchaseItem(RANDOM_CRINGE, ItemRarity.CRINGE);
		if (item != null) {
			EmbedBuilder receipt = new EmbedBuilder().setTitle(event.getAuthor().getName() + " bought a random cringe item!");
			receipt.addField("", "<@" + event.getAuthor().getId() + "> got a " + item.getEmoji() + " " + item.getName(), false);
			receipt.setColor(Bot.COLOR);
			event.getChannel().sendMessage(receipt.build()).queue();
		} else {
			event.getChannel().sendMessage("<@" + event.getAuthor().getId() + ">, you do not have enough emmys for this transaction!").queue();
		}
	}

	private void buyCommon(GuildMessageReceivedEvent event) {
		RPGItems item = profiles.get(event.getAuthor().getId()).purchaseItem(RANDOM_COMMON, ItemRarity.COMMON);
		if (item != null) {
			EmbedBuilder receipt = new EmbedBuilder().setTitle(event.getAuthor().getName() + " bought a random common item!");
			receipt.addField("", "<@" + event.getAuthor().getId() + "> got a " + item.getEmoji() + " " + item.getName(), false);
			receipt.setColor(Bot.COLOR);
			event.getChannel().sendMessage(receipt.build()).queue();
		} else {
			event.getChannel().sendMessage("<@" + event.getAuthor().getId() + ">, you do not have enough emmys for this transaction!").queue();
		}
	}

	private void buyRare(GuildMessageReceivedEvent event) {
		RPGItems item = profiles.get(event.getAuthor().getId()).purchaseItem(RANDOM_RARE, ItemRarity.RARE);
		if (item != null) {
			EmbedBuilder receipt = new EmbedBuilder().setTitle(event.getAuthor().getName() + " bought a random rare item!");
			receipt.addField("", "<@" + event.getAuthor().getId() + "> got a " + item.getEmoji() + " " + item.getName(), false);
			receipt.setColor(Bot.COLOR);
			event.getChannel().sendMessage(receipt.build()).queue();
		} else {
			event.getChannel().sendMessage("<@" + event.getAuthor().getId() + ">, you do not have enough emmys for this transaction!").queue();
		}
	}

	private void buyEpic(GuildMessageReceivedEvent event) {
		RPGItems item = profiles.get(event.getAuthor().getId()).purchaseItem(RANDOM_EPIC, ItemRarity.EPIC);
		if (item != null) {
			EmbedBuilder receipt = new EmbedBuilder().setTitle(event.getAuthor().getName() + " bought a random epic item!");
			receipt.addField("", "<@" + event.getAuthor().getId() + "> got a " + item.getEmoji() + " " + item.getName(), false);
			receipt.setColor(Bot.COLOR);
			event.getChannel().sendMessage(receipt.build()).queue();
		} else {
			event.getChannel().sendMessage("<@" + event.getAuthor().getId() + ">, you do not have enough emmys for this transaction!").queue();
		}
	}

	private void sendShop(GuildMessageReceivedEvent event) {
		EmbedBuilder shop = new EmbedBuilder().setTitle("Peter the Great's Shop");
		shop.addField("<:emmys:706229379620929566> " + RANDOM_CRINGE, "Random *Cringe* item", true);
		shop.addField("<:emmys:706229379620929566> " + RANDOM_COMMON, "Random *Common* item", true);
		shop.addField("<:emmys:706229379620929566> " + RANDOM_RARE, "Random *Rare* item", true);
		shop.addField("<:emmys:706229379620929566> " + RANDOM_EPIC, "Random *Epic* item", true);
		shop.setFooter("use $shop buy [rarity] to buy a random item");
		shop.setColor(Bot.COLOR);
		event.getChannel().sendMessage(shop.build()).queue();
	}
}
