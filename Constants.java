package team129;

public interface Constants {
	//Add these codes to get the value you want.
	//EX: You want a neutral mine?
	//NEUTRAL + MINE = NEUTRALMINE
	//Note: Any field that is unknown is automatically zero
	public static final  int FIELD_SIZE = 4;
	public static final  int FIELD_SIZEx2 = 8;
	
	public static final  int NEUTRAL = 0;
	public static final  int ALLIED = 1;
	public static final  int ENEMY = 2;
	public static final  int UNKNOWN = 0;
	public static final  int CAMP = 8 << 4;
	public static final  int MINE = 1<<4;
	public static final  int HQ = 2<<4;
	public static final  int SOLDIER = 3 << 4;
	public static final  int ARTILLERY = 5 << 8;
	public static final  int MEDBAY = 1 << 8;
	public static final  int SHIELD = 2 << 8;
	public static final  int SUPPLY = 3 << 8;
	public static final  int GENERATOR = 4 << 8;
	
	public static final  int ALLIANCE_FIELD = 0;
	public static final  int TYPE_FIELD = 1;
	public static final  int CAMP_TYPE_FIELD = 2;
	public static final  int NUM_CAMPS_FIELD = 3;
	public static final  int BOTTLENECK_SIZE_FIELD = 4;
	public static final  int FIRST_CHECK_FIELD = 7;
	public static final  int SECOND_CHECK_FIELD = 8;
	
	
	//Check bits, FOR USE BY THE GETTERS AND SETTERS ONLY
	public static final int FIRST_CHECK = 1<<30;
	public static final int SECOND_CHECK = 1<<31;
	
	public static final int MAX_BOTTLENECK_CHECK = 5;
	public static final int NEUTRALMINE = 7;
	public static final int NEUTRALCAMP = 3;
	public static final int ENEMYHQ = 10;
	public static final int ALLIEDHQ = 9;
	public static final int BOTTLENECKCODE  = 10;
	public static final int INFOCODE = 11;
	public static final int CHECKEDCODE = 12;
}
