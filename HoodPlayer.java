package team129;

import battlecode.common.Direction;
import battlecode.common.GameConstants;
import battlecode.common.RobotController;
import battlecode.common.RobotType;
import battlecode.common.MapLocation;
import battlecode.common.Clock;
import java.util.ArrayList;
import java.util.LinkedList;

/** The example funcs player is a player meant to demonstrate basic usage of the most common commands.
 * Robots will move around randomly, occasionally mining and writing useless messages.
 * The HQ will spawn soldiers continuously. 
 */

public class HoodPlayer implements Constants{
	private int[][] map;
	
	public static void run(RobotController rc) {
		while (true) {
			try {
				if (rc.getType() == RobotType.HQ) {
					if (rc.isActive()) {
						// Spawn a soldier
						Direction dir = rc.getLocation().directionTo(rc.senseEnemyHQLocation());
						if (rc.canMove(dir))
							rc.spawn(dir);
					}
				} else if (rc.getType() == RobotType.SOLDIER) {
					if (rc.isActive()) {
						if (Math.random()<0.005) {
							// Lay a mine 
							if(rc.senseMine(rc.getLocation())==null)
								rc.layMine();
						} else { 
							// Choose a random direction, and move that way if possible
							Direction dir = Direction.values()[(int)(Math.random()*8)];
							if(rc.canMove(dir)) {
								rc.move(dir);
								rc.setIndicatorString(0, "Last direction moved: "+dir.toString());
							}
						}
					}
					
					if (Math.random()<0.01 && rc.getTeamPower()>5) {
						// Write the number 5 to a position on the message board corresponding to the robot's ID
						rc.broadcast(rc.getRobot().getID()%GameConstants.BROADCAST_MAX_CHANNELS, 5);
					}
				}

				// End turn
				rc.yield();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	//Next four methods are used in getBottleNecks to calculate bottlenecks
	public int checkXBottleNeck(MapLocation loc, RobotController rc){
		int top = loc.y, bot = loc.y, total = 0;
		while((map[loc.x][--top]!=NEUTRALMINE&&top>=0)&&total<=MAX_BOTTLENECK_CHECK)
			total++;
		while((map[loc.x][++bot]!=NEUTRALMINE&&bot<rc.getMapHeight())&&total<=MAX_BOTTLENECK_CHECK)
			total++;
		return total;
	}
	public int checkYBottleNeck(MapLocation loc, RobotController rc){
		int left = loc.x, right = loc.x, total = 0;
		while((map[--left][loc.y]!=NEUTRALMINE&&left>=0)&&total<=MAX_BOTTLENECK_CHECK)
			total++;
		while((map[++right][loc.y]!=NEUTRALMINE&&right<rc.getMapWidth())&&total<=MAX_BOTTLENECK_CHECK)
			total++;
		return total;
	}
	public int checkNAngleBottleNeck(MapLocation loc, RobotController rc){
		int left = loc.x, right = loc.x, total = 0, top = loc.y, bot = loc.y;
		while((map[--left][++top]!=NEUTRALMINE&&left>=0&&top<rc.getMapHeight())&&total<=MAX_BOTTLENECK_CHECK)
			total++;
		while(total<=MAX_BOTTLENECK_CHECK&&map[++right][--bot]!=NEUTRALMINE&&bot>=0&&right<rc.getMapWidth())
			++total;
		return total;
	}
	public int checkPAngleBottleNeck(MapLocation loc, RobotController rc){
		int left = loc.x, right = loc.x, total = 0, top = loc.y, bot = loc.y;
		while((map[--left][--top]!=NEUTRALMINE&&left>=0&&top>=0)&&total<=MAX_BOTTLENECK_CHECK)
			total++;
		while(total<=MAX_BOTTLENECK_CHECK&&map[++right][++bot]!=NEUTRALMINE&&bot<rc.getMapHeight()&&right<rc.getMapWidth())
			total++;
		return total;
	}
	//In the map array, the hundreds place of the number is the bottleneck value
	public ArrayList<MapLocation> getBottleNecks(ArrayList<MapLocation> path, RobotController rc){
		ArrayList<MapLocation> ret = new ArrayList<MapLocation>();
		for(int i = 2; i<path.size();++i){
			int val;
			if(path.get(i).x == path.get(i-2).x){
				  val = checkXBottleNeck(path.get(i-1), rc);
				map[path.get(i-1).x][path.get(i-1).y] +=100*val;
			}
			else if(path.get(i).y == path.get(i-2).y){
				  val = checkYBottleNeck(path.get(i-1), rc);
				map[path.get(i-1).x][path.get(i-1).y] +=100*val;
			}
			else if(path.get(i).y < path.get(i-2).y){
				  val = checkPAngleBottleNeck(path.get(i-1), rc);
				map[path.get(i-1).x][path.get(i-1).y] +=100*val;
			}
			else{
				  val = checkNAngleBottleNeck(path.get(i-1), rc);
				map[path.get(i-1).x][path.get(i-1).y] +=100*val;
			}
			//This method doesn't give a fuck about the last spot being a bottleneck
		}
		return ret;
	}
	public LinkedList<MapLocation> pointsOfInterest(MapLocation[] camps){
		LinkedList<MapLocation> ret = new LinkedList<MapLocation>();
		for (MapLocation camp: camps){
			
		}
		return ret;
	}
	public int[][] mapRepresentation(RobotController rc){
		int[][] ret = new int[rc.getMapWidth()][rc.getMapHeight()];
		try{
			MapLocation[] locs = rc.senseMineLocations(new MapLocation(rc.getMapWidth()/2, rc.getMapHeight()/2), rc.getMapWidth()/2+rc.getMapHeight()/2, null);
			for (MapLocation m: locs){
				ret[m.x][m.y] = NEUTRALMINE;
			}
			locs = rc.senseAllEncampmentSquares();
			for (MapLocation m: locs){
				ret[m.x][m.y] = NEUTRALCAMP;
			}
			locs[0] = rc.senseHQLocation();
			ret[locs[0].x][locs[0].y] = ALLIEDHQ;
			locs[0] = rc.senseEnemyHQLocation();
			ret[locs[0].x][locs[0].y] = ENEMYHQ;
		}
		catch(Exception e){
			e.printStackTrace();
		}
		return ret;
	}

	//FOR MAP USAGE!!!!!
	
	//Bits 0-3 Alliance: Unknown = 00 Neutral = 11 Allied = 01 Enemy = 10
	//Bits 4-7 Basic Type: Unknown = 000 Mine = 001 HQ = 010 Soldier = 011 Camp = 100
	
	
	//CAMPS ONLY:
	//Bits 8-11 Camp Type: Unknown = 000 Medbay = 001 Shield = 010 Supply = 011 Generator = 100 Artillery = 101
	//Bits 12-15 Number of Contiguous Camps: Unknown = 0000, if non-zero, it is the number of contiguous camps, where 1111 signifies >=15

	
	//Bits 16-19 Bottleneck size: Unknown = 000, if non-zero, it is simply the bottleneck width, where 111 signifies >=7
	
	//Bits 20-29 Unassigned control bits as of now
	//Bits 30/31 Check bits: Use FIRST_CHECK_FIELD and SECOND_CHECK_FIELD for the bits

	public static int setField(int input, int field, int value){
		input = clearedField(input,field);
		return input + value<<FIELD_SIZE*field;
	}
	public static int clearedField(int input, int field){
		
		if(field ==0){
			return input - input % FIELD_SIZEx2;
		}
		else if(field <7){
			int firstChunk, secondChunk;
			firstChunk = input % FIELD_SIZEx2*field;
			secondChunk = input - (input %FIELD_SIZEx2*(field+1));
			return firstChunk + secondChunk;
		}

		else if (field==FIRST_CHECK_FIELD&&(input >> 30)%2==1){
			return input -FIRST_CHECK;
		}
		else if(input <0)
			return input - SECOND_CHECK;
		else 
			return input;
	}
	public static int getField(int input, int field){
		if(field<7){
			return (input >> FIELD_SIZE*field) % FIELD_SIZEx2;
		}
		else if (field==FIRST_CHECK_FIELD){
			return (input >> 30)%2;
		}
		else
			return (input >>31)%2;
	}

}
