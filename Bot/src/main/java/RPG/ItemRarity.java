package RPG;

import java.io.Serializable;

public enum ItemRarity implements Serializable {

	CRINGE(40), COMMON(30), RARE(20), EPIC(10);

	private int percentage;

	ItemRarity(int percentage) {
		this.percentage = percentage;
	}

	public int getPercentage() {
		return percentage;
	}

}
