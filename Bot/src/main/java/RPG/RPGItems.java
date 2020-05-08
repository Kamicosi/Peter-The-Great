package RPG;

import java.io.Serializable;

public enum RPGItems implements Serializable {

	ANTIMAYAN_SCRIPTURE("Antimayan Scripture",":scroll:",  ItemRarity.COMMON, ItemType.HEAL),
	BAT("Bat","<:bat:692184939009015819>",  ItemRarity.CRINGE, ItemType.DAMAGE),
	BRICK("Brick", "<:bricks:706228336602382376>", ItemRarity.CRINGE, ItemType.DAMAGE),
	BRUH_MOMENT("Bruh Moment","<:bruh:706228762110328844>", ItemRarity.RARE, ItemType.DAMAGE),
	BUNKER_BED("Bunker Bed", ":bed:", ItemRarity.COMMON, ItemType.HEAL),
	CHIKEN_NUGGET("Chicken Nugget",":chicken:", ItemRarity.CRINGE, ItemType.HEAL, 1),
	CIGARETTE("Cigarette",":smoking:", ItemRarity.CRINGE, ItemType.HEAL, 0),
	COUCH_AND_LAMP("Couch and Lamp",":couch:", ItemRarity.CRINGE, ItemType.HEAL),
	CUM_ZONE("Cum Zone","<:weebshit:706234993986502730>", ItemRarity.RARE, ItemType.HEAL),
	DIAMOND("Diamond",":gem:", ItemRarity.RARE, ItemType.HEAL),
	EDMODO_ASSIGNMENT("Edmodo Assignment","<:edmodo:706229211567882311>", ItemRarity.COMMON, ItemType.DAMAGE),
	FIREBALL("Fireball",":fire:", ItemRarity.COMMON, ItemType.DAMAGE),
	GAMER_CAVE_BATHTUB("Gamer Cave Bathtub",":bath:", ItemRarity.RARE, ItemType.DAMAGE),
	GAY_ASS("Gay Ass",":rainbow_flag:", ItemRarity.CRINGE, ItemType.HEAL),
	GINGA_GUNGA("Ginga Gunga",":baby:", ItemRarity.RARE, ItemType.DAMAGE),
	HYPE_MAN("Hype Man","<:hypeman:706233138128486420>", ItemRarity.COMMON, ItemType.HEAL),
	JAM_MAN("Jam Man","<:jamman:706233198669070417>", ItemRarity.EPIC, ItemType.HEAL),
	JAPAN("Japan",":japanese_castle:", ItemRarity.COMMON, ItemType.DAMAGE),
	MAN_IN_MOTORIZED_WHEELCHAIR("Man in Motorized Wheelchair",":man_in_motorized_wheelchair:", ItemRarity.COMMON, ItemType.DAMAGE),
	MOCHOU("Mochou","<:mochouwallposter:706233407255871549>", ItemRarity.RARE, ItemType.HEAL),
	NIGGA_CAT("Nigga Cat","<:DumbassCatThatEverybodyLikes:706237169282318506>", ItemRarity.EPIC, ItemType.DAMAGE),
	OBBY("Obby","<:obby:706229764972871760>", ItemRarity.RARE, ItemType.HEAL),
	OK_BOOMER_GIRL("Ok Boomer Girl","<:simp:692183213308772393>", ItemRarity.EPIC, ItemType.HEAL),
	PAPERCLIP("Paperclip",":paperclip:", ItemRarity.CRINGE, ItemType.DAMAGE, 1),
	PERPLEXITY("Perplexity",":question:", ItemRarity.COMMON, ItemType.DAMAGE),
	PICKAXE("Pickaxe",":pick:", ItemRarity.RARE, ItemType.DAMAGE),
	PICKLE_RICK_SHOWER_HEAD("Pickle Rick Shower Head","<:HolyFuck:706227929956483113>", ItemRarity.EPIC, ItemType.DAMAGE),
	PISSCANNON("Pisscannon","<:pisscannon:706230590181212181>", ItemRarity.RARE, ItemType.DAMAGE),
	POOP_STICK("Poop Stick",":chopsticks:", ItemRarity.CRINGE, ItemType.DAMAGE),
	PUDDING_MAN("Pudding Man","<:puddingman:706233165060243517>", ItemRarity.CRINGE, ItemType.HEAL),
	RAT_LINE("Rat Line","<:buffrat:706234803875479623>", ItemRarity.COMMON, ItemType.DAMAGE),
	ROCK("Rock","<:rock:706234588589981717>", ItemRarity.CRINGE, ItemType.DAMAGE),
	ROCKET("Rocket",":rocket:", ItemRarity.RARE, ItemType.DAMAGE),
	SHIT("Shit",":shit:", ItemRarity.CRINGE, ItemType.DAMAGE),
	SIMP_LICENSE("Simp License",":credit_card:", ItemRarity.CRINGE, ItemType.DAMAGE),
	SONIC_AND_MARIO_FUCKING_GIF("Sonic and Mario Fucking Gif","<:marioandsonicfuckingemoji:706233352247836742>", ItemRarity.RARE, ItemType.DAMAGE),
	SOUNDCLOUD_TRACK("Soundcloud Track","<:soundcloud:706234434906751026>", ItemRarity.RARE, ItemType.HEAL),
	SPONGEBOB_MOVIE("Spongebob Movie","<:SpongebobMovie:706579086125826140>", ItemRarity.CRINGE, ItemType.HEAL),
	TENOR_SAXOPHONE("Tenor Saxophone","<:tenor:706233290658414702>", ItemRarity.RARE, ItemType.DAMAGE),
	TRUMPET("Trumpet","<:trumpet:706234420859764879>", ItemRarity.COMMON, ItemType.HEAL),
	WENDYS_NEW_BACON_SWISS_BURGER("Wendy's New Bacon Swiss Burger","<:wendy:706234404602642443>", ItemRarity.EPIC, ItemType.HEAL),
	WELL_DONE("Well Done","<:WellDone:692188733541187635>", ItemRarity.EPIC, ItemType.HEAL),
	ZECHOGGERS("ZeChoggers",":ZeChoggers:", ItemRarity.COMMON, ItemType.HEAL),
	ZE_SUPER_SAIYAN("Ze Super Saiyan","<:ZeSuperSaiyan:706572491803590748>", ItemRarity.EPIC, ItemType.DAMAGE, 150);

	private String name;
	private ItemRarity rarity;
	private ItemType type;
	private String emoji;
	private int power;

	RPGItems(String name, String emoji, ItemRarity rarity, ItemType type, int power) {
		this.name = name;
		this.emoji = emoji;
		this.rarity = rarity;
		this.type = type;
		this.power = power;
	}


	RPGItems(String name, String emoji, ItemRarity rarity, ItemType type) {
		this.name = name;
		this.emoji = emoji;
		this.rarity = rarity;
		this.type = type;
		this.power = rarity.getPower();
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

	public String getEmoji() {
		return emoji;
	}

	public int getPower() {
		return power;
	}

}
