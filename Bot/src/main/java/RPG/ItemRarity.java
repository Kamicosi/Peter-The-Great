package RPG;

import java.io.Serializable;

public enum ItemRarity implements Serializable {

	CRINGE(5, 40), COMMON(15, 40), RARE(30, 15), EPIC(50, 5);

	private int power;
	private int percentage;

	ItemRarity(int power, int percentage) {
		this.power = power;
		this.percentage = percentage;
	}

	public int getPower() {
		return power;
	}

	public int getPercentage() {
		return percentage;
	}

}
