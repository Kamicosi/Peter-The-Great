package RPG;

import java.io.Serializable;

public enum RPGItems implements Serializable {

	ANTIMAYAN_SCRIPTURE("Antimayan Scripture", ItemRarity.COMMON, ItemType.DAMAGE),
	BRICK("Brick", ItemRarity.CRINGE, ItemType.DAMAGE),
	BRUH_MOMENT("Bruh Moment", ItemRarity.RARE, ItemType.DAMAGE),
	BUNKER_BED("Bunker Bed", ItemRarity.COMMON, ItemType.HEAL),
	CUM_ZONE("Cum Zone", ItemRarity.RARE, ItemType.HEAL),
	EDMODO_ASSIGNMENT("Edmodo Assignment", ItemRarity.COMMON, ItemType.DAMAGE),
	EMMYS("Emmys", ItemRarity.COMMON, ItemType.HEAL),
	GAMER_CAVE_BATHTUB("Gamer Cave Bathtub", ItemRarity.RARE, ItemType.DAMAGE),
	GAY_ASS("Gay Ass", ItemRarity.CRINGE, ItemType.HEAL),
	GINGA_GUNGA("Ginga Gunga", ItemRarity.RARE, ItemType.DAMAGE),
	HYPE_MAN("Hype Man", ItemRarity.COMMON, ItemType.DAMAGE),
	JAM_MAN("Jam Man", ItemRarity.EPIC, ItemType.HEAL),
	NIGGA_CAT("Nigga Cat", ItemRarity.EPIC, ItemType.DAMAGE),
	OBBY("Obby", ItemRarity.RARE, ItemType.HEAL),
	OK_BOOMER_GIRL("Ok Boomer Girl", ItemRarity.EPIC, ItemType.DAMAGE),
	PERPLEXITY("Perplexity", ItemRarity.COMMON, ItemType.DAMAGE),
	PICKLE_RICK_SHOWER_HEAD("Pickle Rick Shower Head", ItemRarity.EPIC, ItemType.DAMAGE),
	PISSCANNON("Pisscannon", ItemRarity.RARE, ItemType.DAMAGE),
	POOP_STICK("Poop Stick", ItemRarity.CRINGE, ItemType.DAMAGE),
	PUDDING_MAN("Pudding Man", ItemRarity.CRINGE, ItemType.HEAL),
	RAT_LINE("Rat Line", ItemRarity.COMMON, ItemType.DAMAGE),
	ROCK("Rock", ItemRarity.CRINGE, ItemType.DAMAGE),
	SONIC_AND_MARIO_FUCKING_GIF("Sonic and Mario Fucking Gif", ItemRarity.RARE, ItemType.HEAL),
	SOUNDCLOUD_TRACK("Soundcloud Track", ItemRarity.RARE, ItemType.HEAL),
	TENOR_SAXOPHONE("Tenor Saxophone", ItemRarity.RARE, ItemType.HEAL),
	TRUMPET("Trumpet", ItemRarity.COMMON, ItemType.DAMAGE),
	WENDYS_NEW_BACON_SWISS_BURGER("Wendy's New Bacon Swiss Burger™", ItemRarity.EPIC, ItemType.HEAL);

	private String name;
	private ItemRarity rarity;
	private ItemType type;

	RPGItems(String name, ItemRarity rarity, ItemType type) {
		this.name = name;
		this.rarity = rarity;
		this.type = type;
	}

	public String getName() {
		return name;
	}

	public ItemRarity getRarity() {
		return rarity;
	}

	public ItemType getType() {
		return type;
	}

}
