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
import java.util.List;
import java.util.ListIterator;
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
				findPath(map, rc.senseHQLocation(), rc.senseEnemyHQLocation());
				System.out.println("Algorithm Finished");
				rc.yield();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	//Use a bidirectional A* algorithm to find an efficient path
	public static LinkedList<MapLocation> findPath(int[][] map, MapLocation start, MapLocation end) {
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
				LinkedList<MapLocation> result = new LinkedList<MapLocation>();
				PathfindingNode otherEnd = endExplored[top.y][top.x];
				while (top != null) {
					result.addFirst(new MapLocation(top.x, top.y));
					top = top.previous;
				}
				while (otherEnd != null) {
					result.addLast(new MapLocation(otherEnd.x, otherEnd.y));
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
			if (top.x > 0) {
				if (top.y > 0 && startExplored[top.y - 1][top.x - 1] == null) {
					PathfindingNode newNode = new PathfindingNode(top.x - 1, top.y - 1, top, end, map[top.y - 1][top.x - 1] == NEUTRALMINE);
					startQueue.add(newNode);
					startExplored[top.y - 1][top.x - 1] = newNode;
				}
				if (startExplored[top.y][top.x - 1] == null) {
					PathfindingNode newNode = new PathfindingNode(top.x - 1, top.y, top, end, map[top.y][top.x - 1] == NEUTRALMINE);
					startQueue.add(newNode);
					startExplored[top.y][top.x - 1] = newNode;
				}
				if (top.y < map.length - 1 && startExplored[top.y + 1][top.x - 1] == null) {
					PathfindingNode newNode = new PathfindingNode(top.x - 1, top.y + 1, top, end, map[top.y + 1][top.x - 1] == NEUTRALMINE);
					startQueue.add(newNode);
					startExplored[top.y + 1][top.x - 1] = newNode;
				}
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
			if (top.x < map.length - 1) {
				if (top.y > 0 && startExplored[top.y - 1][top.x + 1] == null) {
					PathfindingNode newNode = new PathfindingNode(top.x + 1, top.y - 1, top, end, map[top.y - 1][top.x + 1] == NEUTRALMINE);
					startQueue.add(newNode);
					startExplored[top.y - 1][top.x + 1] = newNode;
				}
				if (startExplored[top.y][top.x + 1] == null) {
					PathfindingNode newNode = new PathfindingNode(top.x - 1, top.y, top, end, map[top.y][top.x + 1] == NEUTRALMINE);
					startQueue.add(newNode);
					startExplored[top.y][top.x + 1] = newNode;
				}
				if (top.y < map.length - 1 && startExplored[top.y + 1][top.x + 1] == null) {
					PathfindingNode newNode = new PathfindingNode(top.x - 1, top.y + 1, top, end, map[top.y + 1][top.x + 1] == NEUTRALMINE);
					startQueue.add(newNode);
					startExplored[top.y + 1][top.x + 1] = newNode;
				}
			}

			//Run A* going from the end position to the start position
			top = endQueue.poll();
			if (startExplored[top.y][top.x] != null) { //The end and start search areas have met
				LinkedList<MapLocation> result = new LinkedList<MapLocation>();
				PathfindingNode otherEnd = startExplored[top.y][top.x];
				while (top != null) {
					result.addFirst(new MapLocation(top.x, top.y));
					top = top.previous;
				}
				while (otherEnd != null) {
					result.addLast(new MapLocation(otherEnd.x, otherEnd.y));
					otherEnd = otherEnd.previous;
				}
				/*for (int i = 0; i < explored.length; ++i) {
					String thing = "";
					for (int j = 0; j < explored[0].length; ++j) {
						if (start.equals(new MapLocation(j, i)) || end.equals(new MapLocation(j, i)))
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
			if (top.x > 0) {
				if (top.y > 0 && endExplored[top.y - 1][top.x - 1] == null) {
					PathfindingNode newNode = new PathfindingNode(top.x - 1, top.y - 1, top, start, map[top.y - 1][top.x - 1] == NEUTRALMINE);
					endQueue.add(newNode);
					endExplored[top.y - 1][top.x - 1] = newNode;
				}
				if (endExplored[top.y][top.x - 1] == null) {
					PathfindingNode newNode = new PathfindingNode(top.x - 1, top.y, top, start, map[top.y][top.x - 1] == NEUTRALMINE);
					endQueue.add(newNode);
					endExplored[top.y][top.x - 1] = newNode;
				}
				if (top.y < map.length - 1 && endExplored[top.y + 1][top.x - 1] == null) {
					PathfindingNode newNode = new PathfindingNode(top.x - 1, top.y + 1, top, start, map[top.y + 1][top.x - 1] == NEUTRALMINE);
					endQueue.add(newNode);
					endExplored[top.y + 1][top.x - 1] = newNode;
				}
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
			if (top.x < map.length - 1) {
				if (top.y > 0 && endExplored[top.y - 1][top.x + 1] == null) {
					PathfindingNode newNode = new PathfindingNode(top.x + 1, top.y - 1, top, start, map[top.y - 1][top.x + 1] == NEUTRALMINE);
					endQueue.add(newNode);
					endExplored[top.y - 1][top.x + 1] = newNode;
				}
				if (endExplored[top.y][top.x + 1] == null) {
					PathfindingNode newNode = new PathfindingNode(top.x - 1, top.y, top, start, map[top.y][top.x + 1] == NEUTRALMINE);
					endQueue.add(newNode);
					endExplored[top.y][top.x + 1] = newNode;
				}
				if (top.y < map.length - 1 && endExplored[top.y + 1][top.x + 1] == null) {
					PathfindingNode newNode = new PathfindingNode(top.x - 1, top.y + 1, top, start, map[top.y + 1][top.x + 1] == NEUTRALMINE);
					endQueue.add(newNode);
					endExplored[top.y + 1][top.x + 1] = newNode;
				}
			}
		}
	}
	
	//Modify a previously calculated path to account for a new obstacle at the given location
	public static void fixPath(int[][] map, LinkedList<MapLocation> path, MapLocation obstacle) {
		for (ListIterator<MapLocation> i = path.listIterator(); i.hasNext();) {
			MapLocation loc = i.next();
			if (loc.x == obstacle.x && loc.y == obstacle.y) {
				boolean[][] targetSquares = new boolean[map.length][map[0].length];
				PathfindingNodeComparator pc = new PathfindingNodeComparator();
				MapLocation end = path.getLast();
				PriorityQueue<PathfindingNode> queue = new PriorityQueue<PathfindingNode>(128, pc);
				PathfindingNode[][] explored = new PathfindingNode[map.length][map[0].length];
				ListIterator<MapLocation> restOfList = path.listIterator(i.nextIndex() - 1);
				while (restOfList.hasNext()) {
					MapLocation target = restOfList.next();
					targetSquares[target.y][target.x] = true;
				}
				queue.add(new PathfindingNode(loc.x, loc.y));
				while (!queue.isEmpty()) {
					PathfindingNode top = queue.poll();
					if (targetSquares[top.y][top.x]) {
						while (true) {
							MapLocation last = restOfList.previous();
							if (top.equals(last))
								continue;
							List<MapLocation> endOfPath = path.subList(i.nextIndex(), path.size());
							LinkedList<MapLocation> middleOfPath = new LinkedList<MapLocation>();
							while (top.previous != null) {
								middleOfPath.addFirst(new MapLocation(top.x, top.y));
								top = top.previous;
							}
							path = (LinkedList<MapLocation>) path.subList(0, i.previousIndex() + 1);
							path.addAll(middleOfPath);
							path.addAll(endOfPath);
						}
					}
					if (top.x > 0) {
						if (top.y > 0 && explored[top.y - 1][top.x - 1] == null) {
							PathfindingNode newNode = new PathfindingNode(top.x - 1, top.y - 1, top, end, map[top.y - 1][top.x - 1] == NEUTRALMINE);
							queue.add(newNode);
							explored[top.y - 1][top.x - 1] = newNode;
						}
						if (explored[top.y][top.x - 1] == null) {
							PathfindingNode newNode = new PathfindingNode(top.x - 1, top.y, top, end, map[top.y][top.x - 1] == NEUTRALMINE);
							queue.add(newNode);
							explored[top.y][top.x - 1] = newNode;
						}
						if (top.y < map.length - 1 && explored[top.y + 1][top.x - 1] == null) {
							PathfindingNode newNode = new PathfindingNode(top.x - 1, top.y + 1, top, end, map[top.y + 1][top.x - 1] == NEUTRALMINE);
							queue.add(newNode);
							explored[top.y + 1][top.x - 1] = newNode;
						}
					}
					if (top.y > 0 && explored[top.y - 1][top.x] == null) {
						PathfindingNode newNode = new PathfindingNode(top.x, top.y - 1, top, end, map[top.y - 1][top.x] == NEUTRALMINE);
						queue.add(newNode);
						explored[top.y - 1][top.x] = newNode;
					}
					if (top.y < map.length - 1 && explored[top.y + 1][top.x] == null) {
						PathfindingNode newNode = new PathfindingNode(top.x, top.y + 1, top, end, map[top.y + 1][top.x] == NEUTRALMINE);
						queue.add(newNode);
						explored[top.y + 1][top.x] = newNode;
					}
					if (top.x < map.length - 1) {
						if (top.y > 0 && explored[top.y - 1][top.x + 1] == null) {
							PathfindingNode newNode = new PathfindingNode(top.x + 1, top.y - 1, top, end, map[top.y - 1][top.x + 1] == NEUTRALMINE);
							queue.add(newNode);
							explored[top.y - 1][top.x + 1] = newNode;
						}
						if (explored[top.y][top.x + 1] == null) {
							PathfindingNode newNode = new PathfindingNode(top.x - 1, top.y, top, end, map[top.y][top.x + 1] == NEUTRALMINE);
							queue.add(newNode);
							explored[top.y][top.x + 1] = newNode;
						}
						if (top.y < map.length - 1 && explored[top.y + 1][top.x + 1] == null) {
							PathfindingNode newNode = new PathfindingNode(top.x - 1, top.y + 1, top, end, map[top.y + 1][top.x + 1] == NEUTRALMINE);
							queue.add(newNode);
							explored[top.y + 1][top.x + 1] = newNode;
						}
					}
				}
			}
		}
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
		priority = travelCost + 1.005 * Math.max(target.x - x, target.y - y);
	}
	public boolean equals(MapLocation other) {
		return (x == other.x && y == other.y);
	}
	
	public int x;
	public int y;
	public PathfindingNode previous;
	public int travelCost;
	public double priority;
}

