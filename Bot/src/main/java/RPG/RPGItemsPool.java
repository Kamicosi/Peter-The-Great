package RPG;

import java.util.ArrayList;

public class RPGItemsPool {
	public static ArrayList<RPGItems> cringeItems = new ArrayList<RPGItems>();
	public static ArrayList<RPGItems> commonItems = new ArrayList<RPGItems>();
	public static ArrayList<RPGItems> rareItems = new ArrayList<RPGItems>();
	public static ArrayList<RPGItems> epicItems = new ArrayList<RPGItems>();

	static {
		for (RPGItems r : RPGItems.values()) {
			if (r.getRarity().equals(ItemRarity.CRINGE)) {
				cringeItems.add(r);
			} else if (r.getRarity().equals(ItemRarity.COMMON)) {
				commonItems.add(r);
			} else if (r.getRarity().equals(ItemRarity.RARE)) {
				rareItems.add(r);
			} else if (r.getRarity().equals(ItemRarity.EPIC)) {
				epicItems.add(r);
			}
		}
	}
}
