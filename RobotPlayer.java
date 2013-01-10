package team129;

import battlecode.common.Direction;
import battlecode.common.GameConstants;
import battlecode.common.RobotController;
import battlecode.common.RobotType;
import battlecode.common.MapLocation;
import java.util.ArrayList;
import java.util.Iterator;
/** The example funcs player is a player meant to demonstrate basic usage of the most common commands.
 * Robots will move around randomly, occasionally mining and writing useless messages.
 * The HQ will spawn soldiers continuously. 
 */

public class RobotPlayer implements Constants{
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
	public int checkXBottleNeck(MapLocation loc, RobotController rc){
		int top = loc.y, bot = loc.y, total = 0;
		while((map[loc.x][--top]!=Sprite.NEUTRALMINE.getVal()&&top>=0)||(map[loc.x][++bot]!=Sprite.NEUTRALMINE.getVal()&&bot<rc.getMapHeight())&&total<=MAX_BOTTLNECK_CHECK)
			total++;
		return total;
	}
	public ArrayList<MapLocation> getBottleNecks(ArrayList<MapLocation> path, RobotController rc){
		ArrayList<MapLocation> ret = new ArrayList<MapLocation>();
		for(int i = 2; i<path.size();++i){
			if(path.get(i).x == path.get(i-2).x){
				int  val = checkXBottleNeck(path.get(i-1), rc);
			}
		}
		return ret;
	}
	public int[][] mapRepresentation(RobotController rc){
		int[][] ret = new int[rc.getMapWidth()][rc.getMapHeight()];
		try{
			MapLocation[] locs = rc.senseMineLocations(new MapLocation(rc.getMapWidth()/2, rc.getMapHeight()/2), rc.getMapWidth()/2+rc.getMapHeight()/2, null);
			for (MapLocation m: locs){
				ret[m.x][m.y] = Sprite.NEUTRALMINE.getVal();
			}
			locs = rc.senseAllEncampmentSquares();
			for (MapLocation m: locs){
				ret[m.x][m.y] = Sprite.NEUTRALCAMP.getVal();
			}
			locs[0] = rc.senseHQLocation();
			ret[locs[0].x][locs[0].y] = Sprite.ALLIEDHQ.getVal();
			locs[0] = rc.senseEnemyHQLocation();
			ret[locs[0].x][locs[0].y] = Sprite.ENEMYHQ.getVal();
		}
		catch(Exception e){
			e.printStackTrace();
		}
		return ret;
	}
}
