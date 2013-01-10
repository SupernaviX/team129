package team129;

public enum Sprite {
	NEUTRALCAMP(0), ALLIEDCAMP(1), ENEMYCAMP(2), NEUTRALMINE(3), ALLIEDMINE(4), ENEMYMINE(5),
	ALLIEDHQ(6), ENEMYHQ(7), ALLIEDSOLDIER(8), ENEMYSOLDIER(9),
	ALLIEDARTILLERY(10), ALLIEDSUPPLY(11), ALLIEDGENERATOR(12), ALLIEDSHIELD(13), ALLIEDMEDBAY(14),
	ENEMYARTILLERY(20), ENEMYSUPPLY(22), ENEMYGENERATOR(22), ENEMYSHIELD(23), ENEMYMEDBAY(24);
	private int value;
	private Sprite(int value){
		this.value=value;
	}
	public int getVal(){
		return value;
	}

}