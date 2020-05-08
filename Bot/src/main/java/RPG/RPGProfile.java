package RPG;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Random;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

public class RPGProfile implements Serializable {

	/**
	 *
	 */
	private static final long serialVersionUID = 4346439246860272670L;

	private final int LOOT_CHANCE = 180;
	private final int EMMY_CHANCE = 90;

	private String name, ID;

	private int XP;
	private int level;
	private int health, maxHealth;
	private int emmys;
	// 40 Levels
	public int[] XPThreshold;
	private int[] maxHealthAmount;

	public HashMap<RPGItems, Integer> items;
	private Random random;

	public void initialize() {
		random = new Random();

		XPThreshold = new int[] { 0, 50, 100, 150, 200, 300, 400, 500, 700, 900, 1200, 1500, 1800, 2200, 2600, 3000,
				3500, 4000, 4500, 5000, 6000, 7000, 8000, 9000, 10000, 15000, 20000, 30000, 40000, 50000, 60000, 70000,
				80000, 90000, 100000, 150000, 200000, 400000, 600000, 800000, 1000000 };
		maxHealthAmount = new int[] { 0, 10, 12, 14, 16, 18, 20, 24, 28, 32, 36, 40, 45, 50, 55, 60, 65, 70, 75, 80, 88,
				96, 104, 110, 120, 130, 140, 150, 160, 170, 180, 190, 200, 220, 240, 260, 280, 300, 350, 400, 500 };

		maxHealth = maxHealthAmount[level];

		for (RPGItems r : RPGItems.values()) {
			if (!items.containsKey(r)) {
				System.out.println("Putting new RPGItem " + r.getName() + " into " + name + "'s inventory");
				items.put(r, 0);
			}
		}

	}

	public RPGProfile(String name, String ID) {
		this.name = name;
		this.ID = ID;
		level = 1;
		XP = 0;

		items = new HashMap<RPGItems, Integer>();

		initialize();

		health = 10;
		maxHealth = 10;
		emmys = 0;
	}

	/* Main Method */
	public void handleEvent(GuildMessageReceivedEvent event) {
		XP++;
		if (event.getMessage().getContentRaw().contains("$")) {
			if (event.getMessage().getContentRaw().contains("XP$Grind")) {
				XP++;
			}
			XP++;
		}
		checkForLoot(event);
		checkIfLevelUp(event);
	}

	private void checkForLoot(GuildMessageReceivedEvent event) {
		/* 1 in LOOT_CHANCE change of getting any loot */
		if ((random.nextInt(LOOT_CHANCE)) < 1) {
			int rarity = random.nextInt(100);

			RPGItems item = null;
			if (rarity < ItemRarity.CRINGE.getPercentage()) {
				item = RPGItemsPool.cringeItems.get(random.nextInt(RPGItemsPool.cringeItems.size()));
			} else if (rarity < (ItemRarity.CRINGE.getPercentage() + ItemRarity.COMMON.getPercentage())) {
				item = RPGItemsPool.commonItems.get(random.nextInt(RPGItemsPool.commonItems.size()));
			} else if (rarity < (ItemRarity.CRINGE.getPercentage() + ItemRarity.COMMON.getPercentage()
			+ ItemRarity.RARE.getPercentage())) {
				item = RPGItemsPool.rareItems.get(random.nextInt(RPGItemsPool.rareItems.size()));
			} else {
				item = RPGItemsPool.epicItems.get(random.nextInt(RPGItemsPool.epicItems.size()));
			}

			System.out.println("Rarity% generated: " + rarity);
			System.out.println("Item given: " + item.getName());
			System.out.println("Item rarity: " + item.getRarity());

			System.out.println(item.getName());
			int count = items.get(item) + 1;
			items.put(item, count);

			EmbedBuilder foundLoot = new EmbedBuilder().setTitle(item.getRarity().getEmoji() + " " +
					event.getAuthor().getName() + " has found a  " + item.getEmoji() + "*" + item.getName() + "*!!");
			foundLoot.addField("", event.getAuthor().getName() + " now has " + count + " " + item.getName(), false);
			foundLoot.setColor(0x005420);
			event.getChannel().sendMessage(foundLoot.build()).queue();
		} else if (health > 0 && random.nextInt(EMMY_CHANCE) < 1) {
			emmys++;
			EmbedBuilder foundLoot = new EmbedBuilder()
					.setTitle("<:emmys:706229379620929566>" + event.getAuthor().getName() + " found an emmy!!");
			foundLoot.addField("", "<@" + event.getAuthor().getId() + "> now has  <:emmys:706229379620929566>" + emmys,
					false);
			foundLoot.setColor(0x005420);
			event.getChannel().sendMessage(foundLoot.build()).queue();
		}
	}

	/* Regain full health and set XP to 0 */
	private void checkIfLevelUp(GuildMessageReceivedEvent event) {

		if (XP > XPThreshold[level] && level < 41) {
			level++;
			XP = 0;

			health+= 5;
			maxHealth = maxHealthAmount[level];

			/* Level up message */
			EmbedBuilder lvlUp = new EmbedBuilder();
			lvlUp.setTitle("Level Up!");
			lvlUp.addField("",
					"<@" + event.getAuthor().getId() + "> has leveled up to level *" + level + "*!! Poggers!!", false);
			lvlUp.setColor(0x005420);
			event.getChannel().sendMessage(lvlUp.build()).queue();

		}
	}

	/*
	 * Return a BattleInfo object, that contains all relevant info like winner,
	 * loser, wether it's a tie, etc
	 */
	public BattleInfo battle(RPGProfile other) {
		return new BattleInfo(random.nextInt(other.level) + 1, random.nextInt(level) + 1, other.getID(), ID);
	}

	public void sendXP(GuildMessageReceivedEvent event) {
		/* XP message */
		EmbedBuilder xp = new EmbedBuilder();
		xp.setTitle(event.getAuthor().getName() + "'s XP");
		xp.addField("Level " + level, XP + "/" + XPThreshold[level] + " XP", false);
		xp.setColor(0x005420);
		event.getChannel().sendMessage(xp.build()).queue();
	}

	public void heal(int amount) {
		health = Math.min(health + amount, maxHealth);
	}

	public int takeDamage(int amount) {
		health = Math.max(0, health - amount);
		return health;
	}

	public void gainEmmys(int amount) {
		emmys += amount;
	}

	/* List all stats and inventory */
	public void sendProfile(GuildMessageReceivedEvent event) {
		EmbedBuilder profile = new EmbedBuilder().setTitle(event.getAuthor().getName() + "'s Profile");
		profile.addField("Health", health + "/" + maxHealth + " HP", true);
		profile.addField("Level " + level, XP + "/" + XPThreshold[level] + " XP", true);
		profile.addField("Emmys", "<:emmys:706229379620929566>" + String.valueOf(emmys), true);
		profile.addField("", event.getAuthor().getName() + "'s Inventory:", false);

		profile.addField("", ":white_circle: Cringe", false);

		String healItems = "";
		String fightItems = "";
		for (RPGItems r : RPGItemsPool.cringeItems) {
			if (items.get(r) > 0) {
				if (r.getType().equals(ItemType.HEAL)) {
					healItems += items.get(r) + " - " + r.getName() + "\n";
				} else if (r.getType().equals(ItemType.DAMAGE)) {
					fightItems += items.get(r) + " - " + r.getName() + "\n";
				}
			}
		}
		if (healItems.length() > 0 || fightItems.length() > 0) {
			profile.addField(":sparkling_heart:", healItems, true);
			profile.addField(":crossed_swords:", fightItems, true);
		} else {
			profile.addField("", "No cringe items!", true);
		}

		healItems = "";
		fightItems = "";
		profile.addField("", ":green_circle: Common", false);
		for (RPGItems r : RPGItemsPool.commonItems) {
			if (items.get(r) > 0) {
				if (r.getType().equals(ItemType.HEAL)) {
					healItems += items.get(r) + " - " + r.getName() + "\n";
				} else if (r.getType().equals(ItemType.DAMAGE)) {
					fightItems += items.get(r) + " - " + r.getName() + "\n";
				}
			}
		}
		if (healItems.length() > 0 || fightItems.length() > 0) {
			profile.addField(":sparkling_heart:", healItems, true);
			profile.addField(":crossed_swords:", fightItems, true);
		} else {
			profile.addField("", "No common items!", true);
		}

		healItems = "";
		fightItems = "";
		profile.addField("", ":blue_circle: Rare", false);
		for (RPGItems r : RPGItemsPool.rareItems) {
			if (items.get(r) > 0) {
				if (r.getType().equals(ItemType.HEAL)) {
					healItems += items.get(r) + " - " + r.getName() + "\n";
				} else if (r.getType().equals(ItemType.DAMAGE)) {
					fightItems += items.get(r) + " - " + r.getName() + "\n";
				}
			}
		}
		if (healItems.length() > 0 || fightItems.length() > 0) {
			profile.addField(":sparkling_heart:", healItems, true);
			profile.addField(":crossed_swords:", fightItems, true);
		} else {
			profile.addField("", "No rare items!", true);
		}

		healItems = "";
		fightItems = "";
		profile.addField("", ":yellow_circle: Epic", false);
		for (RPGItems r : RPGItemsPool.epicItems) {
			if (items.get(r) > 0) {
				if (r.getType().equals(ItemType.HEAL)) {
					healItems += items.get(r) + " - " + r.getName() + "\n";
				} else if (r.getType().equals(ItemType.DAMAGE)) {
					fightItems += items.get(r) + " - " + r.getName() + "\n";
				}
			}
		}
		if (healItems.length() > 0 || fightItems.length() > 0) {
			profile.addField(":sparkling_heart:", healItems, true);
			profile.addField(":crossed_swords:", fightItems, true);
		} else {
			profile.addField("", "No epic items!", true);
		}
		profile.setColor(0x005420);
		event.getChannel().sendMessage(profile.build()).queue();
	}

	public RPGItems purchaseItem(int amount, ItemRarity rarity) {
		if (emmys - amount >= 0 && rarity != null) {
			emmys-=amount;
			RPGItems item;
			switch (rarity) {
			case CRINGE:
				item = RPGItemsPool.cringeItems.get(random.nextInt(RPGItemsPool.cringeItems.size()));
				break;
			case COMMON:
				item = RPGItemsPool.commonItems.get(random.nextInt(RPGItemsPool.commonItems.size()));
				break;
			case RARE:
				item = RPGItemsPool.rareItems.get(random.nextInt(RPGItemsPool.rareItems.size()));
				break;
			case EPIC:
				item = RPGItemsPool.epicItems.get(random.nextInt(RPGItemsPool.epicItems.size()));
				break;
			default:
				item = null;
				break;
			}
			int count = items.get(item) + 1;
			items.put(item, count);
			return item;
		} else {
			return null;
		}
	}


	/* Write the HashMap of RPGProfiles to profiles.txt */

	public static void writeProfilesToFile(HashMap<String, RPGProfile> profiles) {
		File f = new File("profiles.txt");

		try {
			FileOutputStream fos = new FileOutputStream(f);
			ObjectOutputStream oos = new ObjectOutputStream(fos);

			oos.writeObject(profiles);

			System.out.println("Profiles saved");
			oos.close();

		} catch (FileNotFoundException e) {
			System.out.println("Profile saving failed");
			e.printStackTrace();
		} catch (IOException e) {
			System.out.println("Profile saving failed");
			e.printStackTrace();
		}
	}

	/* Read the HashMap of RPGProfiles from profiles.txt */

	@SuppressWarnings("unchecked")
	public static HashMap<String, RPGProfile> readProfilesFromFile() {
		File f = new File("profiles.txt");

		try {
			FileInputStream fis = new FileInputStream(f);
			ObjectInputStream ois = new ObjectInputStream(fis);

			HashMap<String, RPGProfile> profiles = (HashMap<String, RPGProfile>) ois.readObject();

			System.out.println("Profiles read");
			ois.close();

			return profiles;
		} catch (FileNotFoundException e) {
			System.out.println("Profiles reading failed");
			e.printStackTrace();
		} catch (IOException e) {
			System.out.println("Profiles reading failed");
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public String toString() {
		return "[" + name + "\nID: " + ID + "\nLevel: " + level + "]";
	}

	/* Getters and Setters */
	public String getName() {
		return name;
	}

	public String getID() {
		return ID;
	}

	public int getXP() {
		return XP;
	}

	public int getLevel() {
		return level;
	}

	public String getHealth() {
		return health + "/" + maxHealthAmount[level];
	}

	public int getHP() {
		return health;
	}

	public HashMap<RPGItems, Integer> getItems() {
		return items;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setXP(int xP) {
		XP = xP;
	}

	public void setLevel(int level) {
		this.level = level;
	}

	public void setItems(HashMap<RPGItems, Integer> items) {
		this.items = items;
	}
}
