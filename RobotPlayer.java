package team129;

import battlecode.common.RobotController;
import battlecode.common.RobotType;
import battlecode.common.MapLocation;
import battlecode.common.Team;

import java.util.ArrayList;
/** The example funcs player is a player meant to demonstrate basic usage of the most common commands.
 * Robots will move around randomly, occasionally mining and writing useless messages.
 * The HQ will spawn soldiers continuously. 
 */

public class RobotPlayer implements Constants{
	private static int[][] map;
	public static void run(RobotController rc) {
		map = mapRepresentation(rc);
		//while (true) {
			try {
				if (rc.getType() == RobotType.HQ) {
					if (rc.isActive()) {
					}
				} else if (rc.getType() == RobotType.SOLDIER) {
					if (rc.isActive()) {
					}
				}
				System.out.println("Algorithm Started");
				if (rc.getTeam() == Team.A)
					Pathfinding.findPath(map, rc.senseHQLocation(), rc.senseEnemyHQLocation());
				else
					while (true) {}
				System.out.println("Algorithm Finished");
				rc.yield();
			} catch (Exception e) {
				e.printStackTrace();
			}
		//}
	}

	public static int checkXBottleNeck(MapLocation loc, RobotController rc){
		int top = loc.y, bot = loc.y, total = 0;
		while((map[loc.x][--top]!=Sprite.NEUTRALMINE.getVal()&&top>=0)||(map[loc.x][++bot]!=Sprite.NEUTRALMINE.getVal()&&bot<rc.getMapHeight())&&total<=MAX_BOTTLENECK_CHECK)
			total++;
		return total;
	}
	public static ArrayList<MapLocation> getBottleNecks(ArrayList<MapLocation> path, RobotController rc){
		ArrayList<MapLocation> ret = new ArrayList<MapLocation>();
		for(int i = 2; i<path.size();++i){
			if(path.get(i).x == path.get(i-2).x){
				int  val = checkXBottleNeck(path.get(i-1), rc);
			}
		}
		return ret;
	}
	public static int[][] mapRepresentation(RobotController rc){
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