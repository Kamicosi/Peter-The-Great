package RPG;

public class BattleInfo {
	public int roll1, roll2, difference;
	public String name1, name2, winner, loser;
	public boolean tie;

	public BattleInfo(int roll1, int roll2, String name1, String name2) {
		this.name1 = name1;
		this.name2 = name2;
		this.roll1 = roll1;
		this.roll2 = roll2;
		difference = Math.abs(roll1 - roll2);
		if (roll1 == roll2) {
			tie = true;
		} else {
			tie = false;
			if (roll1 < roll2) {
				winner = name2;
				loser = name1;
			} else {
				winner = name1;
				loser = name2;
			}
		}
	}

}
