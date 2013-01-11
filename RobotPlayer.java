package team129;

import battlecode.common.Clock;
import battlecode.common.Direction;
import battlecode.common.GameConstants;
import battlecode.common.RobotController;
import battlecode.common.RobotType;
import battlecode.common.MapLocation;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Set;
/** The example funcs player is a player meant to demonstrate basic usage of the most common commands.
 * Robots will move around randomly, occasionally mining and writing useless messages.
 * The HQ will spawn soldiers continuously. 
 */

public class RobotPlayer implements Constants{
	private static int[][] map;
	public static void run(RobotController rc) {
		map = mapRepresentation(rc);
		while (true) {
			try {
				if (rc.getType() == RobotType.HQ) {
					if (rc.isActive()) {
					}
				} else if (rc.getType() == RobotType.SOLDIER) {
					if (rc.isActive()) {
					}
				}
				System.out.println("Algorithm Started");
				aStar(map, rc.senseHQLocation(), rc.senseEnemyHQLocation());
				System.out.println("Algorithm Finished");
				rc.yield();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public static LinkedList<PathfindingNode> twoStars(int[][] map, MapLocation start, MapLocation end) {
		PathfindingNodeComparator pnc = new PathfindingNodeComparator();
		PriorityQueue<PathfindingNode> startQueue = new PriorityQueue<PathfindingNode>(128, pnc);
		PriorityQueue<PathfindingNode> endQueue = new PriorityQueue<PathfindingNode>(128, pnc);
		PathfindingNode[][] startExplored = new PathfindingNode[map.length][map[0].length];
		PathfindingNode[][] endExplored = new PathfindingNode[map.length][map[0].length];		
		
		startQueue.add(new PathfindingNode(start.x, start.y));
		endQueue.add(new PathfindingNode(end.x, end.y));
		
		while (true) { //There are no unreachable spaces on the map, this will always return a path
			
			//Run A* going from the start position to the end position
			PathfindingNode top = startQueue.poll();
			if (endExplored[top.y][top.x] != null) { //The start and end search areas have met
				LinkedList<PathfindingNode> result = new LinkedList<PathfindingNode>();
				PathfindingNode otherEnd = endExplored[top.y][top.x];
				while (top != null) {
					result.addFirst(top);
					top = top.previous;
				}
				while (otherEnd != null) {
					result.addLast(otherEnd);
					otherEnd = otherEnd.previous;
				}
				/*for (int i = 0; i < explored.length; ++i) {
					String thing = "";
					for (int j = 0; j < explored[0].length; ++j) {
						if (end.equals(new MapLocation(j, i)) || start.equals(new MapLocation(j, i)))
							thing += "O ";
						else if (explored[i][j] == true)
							thing += "X ";
						else
							thing += "  ";
					}
					System.out.println(thing);
				}*/
				return result;
			}
			if (top.x > 0 && startExplored[top.y][top.x - 1] == null) {
				PathfindingNode newNode = new PathfindingNode(top.x - 1, top.y, top, end, map[top.y][top.x - 1] == NEUTRALMINE);
				startQueue.add(newNode);
				startExplored[top.y][top.x - 1] = newNode;
			}
			if (top.x < map[0].length - 1 && startExplored[top.y][top.x + 1] == null) {
				PathfindingNode newNode = new PathfindingNode(top.x + 1, top.y, top, end, map[top.y][top.x + 1] == NEUTRALMINE);
				startQueue.add(newNode);
				startExplored[top.y][top.x + 1] = newNode;
			}
			if (top.y > 0 && startExplored[top.y - 1][top.x] == null) {
				PathfindingNode newNode = new PathfindingNode(top.x, top.y - 1, top, end, map[top.y - 1][top.x] == NEUTRALMINE);
				startQueue.add(newNode);
				startExplored[top.y - 1][top.x] = newNode;
			}
			if (top.y < map.length - 1 && startExplored[top.y + 1][top.x] == null) {
				PathfindingNode newNode = new PathfindingNode(top.x, top.y + 1, top, end, map[top.y + 1][top.x] == NEUTRALMINE);
				startQueue.add(newNode);
				startExplored[top.y + 1][top.x] = newNode;
			}

			//Run A* going from the end position to the start position
			top = endQueue.poll();
			if (startExplored[top.y][top.x] != null) { //The start and end search areas have met
				LinkedList<PathfindingNode> result = new LinkedList<PathfindingNode>();
				PathfindingNode otherEnd = startExplored[top.y][top.x];
				while (top != null) {
					result.addLast(top);
					top = top.previous;
				}
				while (otherEnd != null) {
					result.addFirst(otherEnd);
					otherEnd = otherEnd.previous;
				}
				/*for (int i = 0; i < explored.length; ++i) {
					String thing = "";
					for (int j = 0; j < explored[0].length; ++j) {
						if (end.equals(new MapLocation(j, i)) || start.equals(new MapLocation(j, i)))
							thing += "O ";
						else if (explored[i][j] == true)
							thing += "X ";
						else
							thing += "  ";
					}
					System.out.println(thing);
				}*/
				return result;
			}
			if (top.x > 0 && endExplored[top.y][top.x - 1] == null) {
				PathfindingNode newNode = new PathfindingNode(top.x - 1, top.y, top, start, map[top.y][top.x - 1] == NEUTRALMINE);
				endQueue.add(newNode);
				endExplored[top.y][top.x - 1] = newNode;
			}
			if (top.x < map[0].length - 1 && endExplored[top.y][top.x + 1] == null) {
				PathfindingNode newNode = new PathfindingNode(top.x + 1, top.y, top, start, map[top.y][top.x + 1] == NEUTRALMINE);
				endQueue.add(newNode);
				endExplored[top.y][top.x + 1] = newNode;
			}
			if (top.y > 0 && endExplored[top.y - 1][top.x] == null) {
				PathfindingNode newNode = new PathfindingNode(top.x, top.y - 1, top, start, map[top.y - 1][top.x] == NEUTRALMINE);
				endQueue.add(newNode);
				endExplored[top.y - 1][top.x] = newNode;
			}
			if (top.y < map.length - 1 && endExplored[top.y + 1][top.x] == null) {
				PathfindingNode newNode = new PathfindingNode(top.x, top.y + 1, top, start, map[top.y + 1][top.x] == NEUTRALMINE);
				endQueue.add(newNode);
				endExplored[top.y + 1][top.x] = newNode;
			}

		}
	}

	public static LinkedList<PathfindingNode> aStar(int[][] map, MapLocation start, MapLocation end) {
		PathfindingNodeComparator pnc = new PathfindingNodeComparator();
		PriorityQueue<PathfindingNode> queue = new PriorityQueue<PathfindingNode>(100, pnc);
		boolean[][] explored = new boolean[map.length][map[0].length];
		queue.add(new PathfindingNode(start.x, start.y));
		while (!queue.isEmpty()) {
			PathfindingNode top = queue.poll();
			explored[top.y][top.x] = true;
			if (top.equals(end)) {
				LinkedList<PathfindingNode> result = new LinkedList<PathfindingNode>();
				while (top != null) {
					result.addFirst(top);
					top = top.previous;
				}
				/*for (int i = 0; i < explored.length; ++i) {
					String thing = "";
					for (int j = 0; j < explored[0].length; ++j) {
						if (end.equals(new MapLocation(j, i)) || start.equals(new MapLocation(j, i)))
							thing += "O ";
						else if (explored[i][j] == true)
							thing += "X ";
						else
							thing += "  ";
					}
					System.out.println(thing);
				}*/
				return result;
			}
			if (top.x > 0 && !explored[top.y][top.x - 1]) {
				queue.add(new PathfindingNode(top.x - 1, top.y, top, end, map[top.y][top.x - 1] == NEUTRALMINE));
				explored[top.y][top.x - 1] = true;
			}
			if (top.x < map[0].length - 1 && !explored[top.y][top.x + 1]) {
				queue.add(new PathfindingNode(top.x + 1, top.y, top, end, map[top.y][top.x + 1] == NEUTRALMINE));
				explored[top.y][top.x + 1] = true;
			}
			if (top.y > 0 && !explored[top.y - 1][top.x]) {
				queue.add(new PathfindingNode(top.x, top.y - 1, top, end, map[top.y - 1][top.x] == NEUTRALMINE));
				explored[top.y - 1][top.x] = true;
			}
			if (top.y < map.length - 1 && !explored[top.y + 1][top.x]) {
				queue.add(new PathfindingNode(top.x, top.y + 1, top, end, map[top.y + 1][top.x] == NEUTRALMINE));
				explored[top.y + 1][top.x] = true;
			}
		}
		return null;
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

class PathfindingNodeComparator implements Comparator<PathfindingNode> {
	@Override
	public int compare(PathfindingNode o1, PathfindingNode o2) {
		if (o1.priority < o2.priority)
			return -1;
		else if (o1.priority > o2.priority)
			return 1;
		else
			return 0;
	}
}

class PathfindingNode {
	public PathfindingNode(int myX, int myY) {
		x = myX;
		y = myY;
		previous = null;
		travelCost = 0;
	}
	public PathfindingNode(int myX, int myY, PathfindingNode prev, MapLocation target, boolean currentSpaceIsMined) {
		x = myX;
		y = myY;
		previous = prev;
		if (currentSpaceIsMined)
			travelCost = prev.travelCost + 13;
		else
			travelCost = prev.travelCost + 1;
		priority = travelCost + 12 * (target.x - x + target.y - y);
	}
	public boolean equals(MapLocation other) {
		return (x == other.x && y == other.y);
	}
	public int x;
	public int y;
	public PathfindingNode previous;
	public int travelCost;
	public int priority;
}

