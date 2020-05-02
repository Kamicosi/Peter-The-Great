package RPG;

public class BattleInfo {
	public int roll1, roll2, difference;
	public String ID1, ID2, winner, loser;
	public boolean tie;

	public BattleInfo(int roll1, int roll2, String ID1, String ID2) {
		this.ID1 = ID1;
		this.ID2 = ID2;
		this.roll1 = roll1;
		this.roll2 = roll2;
		difference = Math.abs(roll1 - roll2);
		if (roll1 == roll2) {
			tie = true;
		} else {
			tie = false;
			if (roll1 < roll2) {
				winner = ID2;
				loser = ID1;
			} else {
				winner = ID1;
				loser = ID2;
			}
		}
	}

}
