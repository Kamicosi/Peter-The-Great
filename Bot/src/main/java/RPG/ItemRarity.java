package RPG;

import java.io.Serializable;

public enum ItemRarity implements Serializable {

	CRINGE(50, 5, ":white_circle:"), COMMON(35, 15, ":green_circle:"), RARE(10, 35, ":blue_circle:"), EPIC(5, 60, ":yellow_circle:");

	private int percentage;
	private int power;
	private String emoji;

	ItemRarity(int percentage, int power, String emoji) {
		this.percentage = percentage;
		this.power = power;
		this.emoji = emoji;
	}

	public int getPercentage() {
		return percentage;
	}

	public int getPower() {
		return power;
	}

	public String getEmoji() {
		return emoji;
	}

}
