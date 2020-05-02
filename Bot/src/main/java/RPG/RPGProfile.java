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
import java.util.HashSet;
import java.util.Random;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed.Field;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

public class RPGProfile implements Serializable {

	/**
	 *
	 */
	private static final long serialVersionUID = 4346439246860272670L;

	private final int LOOT_CHANCE = 200;

	private String name, ID;

	private int XP;
	private int level;
	private int health, maxHealth;
	// 40 Levels
	public int[] XPThreshold;
	private int[] maxHealthAmount;

	private HashMap<RPGItems, Integer> items;
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

	}

	/* Main Method */
	public void handleEvent(GuildMessageReceivedEvent event) {
		XP++;
		if (event.getMessage().getContentRaw().contains("$")) {
			XP++;
		}
		checkForLoot(event);
		checkIfLevelUp(event);
	}

	private void checkForLoot(GuildMessageReceivedEvent event) {
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

			System.out.println(item.getName());
			int count = items.get(item) + 1;
			items.put(item, count);

			EmbedBuilder foundLoot = new EmbedBuilder()
					.setTitle(event.getAuthor().getName() + " has found a " + item.getEmoji() + "*" + item.getName() + "*!!");
			foundLoot.addField("", event.getAuthor().getName() + " has found one *" + item.getName()
			+ "* for a total of *" + count + "*. This is a pog moment!!", false);
			foundLoot.setColor(0x005420);
			event.getChannel().sendMessage(foundLoot.build()).queue();
		}
	}

	private void checkIfLevelUp(GuildMessageReceivedEvent event) {

		if (XP > XPThreshold[level] && level < 41) {
			level++;
			XP = 0;

			health = maxHealthAmount[level];
			maxHealth = maxHealthAmount[level];

			EmbedBuilder lvlUp = new EmbedBuilder();
			lvlUp.setTitle("Level Up!");
			lvlUp.addField("", event.getAuthor().getName() + " has leveled up to level *" + level + "*!! Poggers!!",
					false);
			lvlUp.setColor(0x005420);
			event.getChannel().sendMessage(lvlUp.build()).queue();

		}
	}

	public BattleInfo battle(RPGProfile other) {
		// System.out.println("Challenger's level " + other.level);
		// System.out.println("Other person's level " + level);
		return new BattleInfo(random.nextInt(other.level) + 1, random.nextInt(level) + 1, other.getID(), ID);
	}

	public void sendXP(GuildMessageReceivedEvent event) {
		EmbedBuilder xp = new EmbedBuilder();
		xp.setTitle(event.getAuthor().getName() + "'s XP");
		xp.addField("Level " + level, XP + "/" + XPThreshold[level] + " XP", false);
		xp.setColor(0x005420);
		event.getChannel().sendMessage(xp.build()).queue();
	}

	public void heal(int amount) {
		health = Math.min(health + amount, maxHealth);
	}

	public void takeDamage(int amount) {

		health = Math.max(0, health - amount);

	}

	public void sendProfile(GuildMessageReceivedEvent event) {
		EmbedBuilder profile = new EmbedBuilder().setTitle(event.getAuthor().getName() + "'s Profile");
		profile.addField("Health", health + "/" + maxHealth + " HP", true);
		profile.addField("Level " + level, XP + "/" + XPThreshold[level] + " XP", true);
		profile.addField("", event.getAuthor().getName() + "'s Inventory:", false);

		HashSet<Field> cringeItems = new HashSet<Field>();
		HashSet<Field> commonItems = new HashSet<Field>();
		HashSet<Field> rareItems = new HashSet<Field>();
		HashSet<Field> epicItems = new HashSet<Field>();

		for (RPGItems r : items.keySet()) {
			if (items.get(r) > 0) {
				switch (r.getRarity()) {
				case CRINGE:
					cringeItems.add(new Field(items.get(r).toString(), r.getName(), true));
					break;
				case COMMON:
					commonItems.add(new Field(items.get(r).toString(), r.getName(), true));
					break;
				case RARE:
					rareItems.add(new Field(items.get(r).toString(), r.getName(), true));
					break;
				case EPIC:
					epicItems.add(new Field(items.get(r).toString(), r.getName(), true));
					break;
				}
			}
		}

		profile.addField("", ":white_circle: Cringe", false);
		for (Field f : cringeItems) {
			profile.addField(f);
		}

		profile.addField("", ":green_circle: Common", false);
		for (Field f : commonItems) {
			profile.addField(f);
		}

		profile.addField("", ":blue_circle: Rare", false);
		for (Field f : rareItems) {
			profile.addField(f);
		}

		profile.addField("", ":yellow_circle: Epic", false);
		for (Field f : epicItems) {
			profile.addField(f);
		}
		profile.setColor(0x005420);
		event.getChannel().sendMessage(profile.build()).queue();
	}

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
