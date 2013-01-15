package team129;

import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.PriorityQueue;
import java.util.Set;

import battlecode.common.Direction;
import battlecode.common.MapLocation;

public class Pathfinding implements Constants {

	//Use an A* algorithm to find an efficient path
	public static Direction[][] findPath(int[][] map, MapLocation start, MapLocation end) {
		int width = map.length, height = map[0].length;
		Direction[][] result = new Direction[width][height];
		MapLocation loc, next;
		int x, y, westX, eastX, northY, southY;
		PathfindingNodeComparator pnc = new PathfindingNodeComparator();
		PriorityQueue<PathfindingNode> queue = new PriorityQueue<PathfindingNode>(128, pnc);
		Set<MapLocation> explored = new HashSet<MapLocation>();
		
		queue.add(new PathfindingNode(start));
		int count = 0;
		
		while (!queue.isEmpty()) {
			//Run A* going from the start position to the end position
			PathfindingNode top = queue.poll();
			count++;
			if (top.equals(end)) {
				System.out.println(count);
				while (top != null) {
					result[top.loc.x][top.loc.y] = top.dir;
					top = top.prev;
				}
				return result;
			}
			if (top.equals(null))
				return result;
			loc = top.loc;
			x = loc.x;
			y = loc.y;
			westX = x - 1;
			eastX = x + 1;
			northY = y - 1;
			southY = y + 1;
			boolean northSafe = y > 0, southSafe = southY < height;
			if (x > 0) {
				if (northSafe && map[westX][northY] != NEUTRALMINE) {
					next = new MapLocation(westX, northY);
					if (!explored.contains(next)) {
						queue.add(new PathfindingNode(next, top, Direction.NORTH_WEST, end));
						explored.add(next);
					}
				}
				if (map[westX][y] != NEUTRALMINE) {
					next = new MapLocation(westX, y);
					if (!explored.contains(next)) {
						queue.add(new PathfindingNode(next, top, Direction.WEST, end));
						explored.add(next);
					}
				}
				if (southSafe && map[westX][southY] != NEUTRALMINE) {
					next = new MapLocation(westX, southY);
					if (!explored.contains(next)) {
						queue.add(new PathfindingNode(next, top, Direction.SOUTH_WEST, end));
						explored.add(next);
					}
				}
			}
			if (northSafe && map[x][northY] != NEUTRALMINE) {
				next = new MapLocation(x, northY);
				if (!explored.contains(next)) {
					queue.add(new PathfindingNode(next, top, Direction.NORTH, end));
					explored.add(next);
				}
			}
			if (southSafe && map[x][southY] != NEUTRALMINE) {
				next = new MapLocation(x, southY);
				if (!explored.contains(next)) {
					queue.add(new PathfindingNode(next, top, Direction.SOUTH, end));
					explored.add(next);
				}
			}
			if (eastX < width) {
				if (northSafe && map[eastX][northY] != NEUTRALMINE) {
					next = new MapLocation(eastX, northY);
					if (!explored.contains(next)) {
						queue.add(new PathfindingNode(next, top, Direction.NORTH_EAST, end));
						explored.add(next);
					}
				}
				if (map[eastX][y] != NEUTRALMINE) {
					next = new MapLocation(eastX, y);
					if (!explored.contains(next)) {
						queue.add(new PathfindingNode(next, top, Direction.EAST, end));
						explored.add(next);
					}
				}
				if (southSafe && map[eastX][southY] != NEUTRALMINE) {
					next = new MapLocation(eastX, southY);
					if (!explored.contains(next)) {
						queue.add(new PathfindingNode(next, top, Direction.SOUTH_EAST, end));
						explored.add(next);
					}
				}
			}
		}
		return result;
	}
	
	//Use a bidirectional A* algorithm to find a hard point to reach on the efficient path
	/*public static MapLocation findKeyPoint(int[][] map, MapLocation start, MapLocation end) {
		PathfindingNodeComparator pnc = new PathfindingNodeComparator();
		PriorityQueue<PathfindingNode> startQueue = new PriorityQueue<PathfindingNode>(128, pnc);
		PriorityQueue<PathfindingNode> endQueue = new PriorityQueue<PathfindingNode>(128, pnc);
		boolean[][] startExplored = new boolean[map.length][map[0].length];
		boolean[][] endExplored = new boolean[map.length][map[0].length];		
		
		startQueue.add(new PathfindingNode(start.x, start.y));
		endQueue.add(new PathfindingNode(end.x, end.y));
		
		while (true) { //There are no unreachable spaces on the map, this will always return a path
			//Run A* going from the start position to the end position
			PathfindingNode top = startQueue.poll();
			if (endExplored[top.x][top.y])
				return new MapLocation(top.x, top.y);
			if (top.x > 0) {
				if (top.y > 0 && !startExplored[top.x - 1][top.y - 1]) {// && map[top.x - 1][top.y - 1] != NEUTRALMINE) {
					startQueue.add(new PathfindingNode(top.x - 1, top.y - 1, top, end, map[top.x - 1][top.y - 1] == NEUTRALMINE));
					startExplored[top.x - 1][top.y - 1] = true;
				}
				if (!startExplored[top.x - 1][top.y]) {// && map[top.x - 1][top.y] != NEUTRALMINE) {
					startQueue.add(new PathfindingNode(top.x - 1, top.y, top, end, map[top.x - 1][top.y] == NEUTRALMINE));
					startExplored[top.x - 1][top.y] = true;
				}
				if (top.y < map[0].length - 1 && !startExplored[top.x - 1][top.y + 1]) {// && map[top.x - 1][top.y + 1] != NEUTRALMINE) {
					startQueue.add(new PathfindingNode(top.x - 1, top.y + 1, top, end, map[top.x - 1][top.y + 1] == NEUTRALMINE));
					startExplored[top.x - 1][top.y + 1] = true;
				}
			}
			if (top.y > 0 && !startExplored[top.x][top.y - 1]) {// && map[top.x][top.y - 1] != NEUTRALMINE) {
				startQueue.add(new PathfindingNode(top.x, top.y - 1, top, end, map[top.x][top.y - 1] == NEUTRALMINE));
				startExplored[top.x][top.y - 1] = true;
			}
			if (top.y < map[0].length - 1 && !startExplored[top.x][top.y + 1]) {// && map[top.x][top.y + 1] != NEUTRALMINE) {
				startQueue.add(new PathfindingNode(top.x, top.y + 1, top, end, map[top.x][top.y + 1] == NEUTRALMINE));
				startExplored[top.x][top.y + 1] = true;
			}
			if (top.x < map.length - 1) {
				if (top.y > 0 && !startExplored[top.x + 1][top.y - 1]) {// && map[top.x + 1][top.y - 1] != NEUTRALMINE) {
					startQueue.add(new PathfindingNode(top.x + 1, top.y - 1, top, end, map[top.x + 1][top.y - 1] == NEUTRALMINE));
					startExplored[top.x + 1][top.y - 1] = true;
				}
				if (!startExplored[top.x + 1][top.y]) {// && map[top.x + 1][top.y] != NEUTRALMINE) {
					startQueue.add(new PathfindingNode(top.x + 1, top.y, top, end, map[top.x + 1][top.y] == NEUTRALMINE));
					startExplored[top.x + 1][top.y] = true;
				}
				if (top.y < map[0].length - 1 && !startExplored[top.x + 1][top.y + 1]) {// && map[top.x + 1][top.y + 1] != NEUTRALMINE) {
					startQueue.add(new PathfindingNode(top.x + 1, top.y + 1, top, end, map[top.x + 1][top.y + 1] == NEUTRALMINE));
					startExplored[top.x + 1][top.y + 1] = true;
				}
			}

			//Run A* going from the end position to the start position
			top = endQueue.poll();
			if (startExplored[top.x][top.y])
				return new MapLocation(top.x, top.y);
			if (top.x > 0) {
				if (top.y > 0 && !endExplored[top.x - 1][top.y - 1]) {// && map[top.x - 1][top.y - 1] != NEUTRALMINE) {
					endQueue.add(new PathfindingNode(top.x - 1, top.y - 1, top, start, map[top.x - 1][top.y - 1] == NEUTRALMINE));
					endExplored[top.x - 1][top.y - 1] = true;
				}
				if (!endExplored[top.x - 1][top.y]) {// && map[top.x - 1][top.y] != NEUTRALMINE) {
					endQueue.add(new PathfindingNode(top.x - 1, top.y, top, start, map[top.x - 1][top.y] == NEUTRALMINE));
					endExplored[top.x - 1][top.y] = true;
				}
				if (top.y < map[0].length - 1 && !endExplored[top.x - 1][top.y + 1]) {// && map[top.x - 1][top.y + 1] != NEUTRALMINE) {
					endQueue.add(new PathfindingNode(top.x - 1, top.y + 1, top, start, map[top.x - 1][top.y + 1] == NEUTRALMINE));
					endExplored[top.x - 1][top.y + 1] = true;
				}
			}
			if (top.y > 0 && !endExplored[top.x][top.y - 1]) {// && map[top.x][top.y - 1] != NEUTRALMINE) {
				endQueue.add(new PathfindingNode(top.x, top.y - 1, top, start, map[top.x][top.y - 1] == NEUTRALMINE));
				endExplored[top.x][top.y - 1] = true;
			}
			if (top.y < map[0].length - 1 && !endExplored[top.x][top.y + 1]) {// && map[top.x][top.y + 1] != NEUTRALMINE) {
				endQueue.add(new PathfindingNode(top.x, top.y + 1, top, start, map[top.x][top.y + 1] == NEUTRALMINE));
				endExplored[top.x][top.y + 1] = true;
			}
			if (top.x < map.length - 1) {
				if (top.y > 0 && !endExplored[top.x + 1][top.y - 1]) {// && map[top.x + 1][top.y - 1] != NEUTRALMINE) {
					endQueue.add(new PathfindingNode(top.x + 1, top.y - 1, top, start, map[top.x + 1][top.y - 1] == NEUTRALMINE));
					endExplored[top.x + 1][top.y - 1] = true;
				}
				if (!endExplored[top.x + 1][top.y]) {// && map[top.x + 1][top.y] != NEUTRALMINE) {
					endQueue.add(new PathfindingNode(top.x + 1, top.y, top, start, map[top.x + 1][top.y] == NEUTRALMINE));
					endExplored[top.x + 1][top.y] = true;
				}
				if (top.y < map[0].length - 1 && !endExplored[top.x + 1][top.y + 1]) {// && map[top.x + 1][top.y + 1] != NEUTRALMINE) {
					endQueue.add(new PathfindingNode(top.x + 1, top.y + 1, top, start, map[top.x + 1][top.y + 1] == NEUTRALMINE));
					endExplored[top.x + 1][top.y + 1] = true;
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
					targetSquares[target.x][target.y] = true;
				}
				queue.add(new PathfindingNode(loc));
				while (!queue.isEmpty()) {
					PathfindingNode top = queue.poll();
					if (targetSquares[top.x][top.y]) {
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
						if (top.y > 0 && explored[top.x - 1][top.y - 1] == null) {
							PathfindingNode newNode = new PathfindingNode(top.x - 1, top.y - 1, top, end, map[top.x - 1][top.y - 1] == NEUTRALMINE);
							queue.add(newNode);
							explored[top.x - 1][top.y - 1] = newNode;
						}
						if (explored[top.x - 1][top.y] == null) {
							PathfindingNode newNode = new PathfindingNode(top.x - 1, top.y, top, end, map[top.x - 1][top.y] == NEUTRALMINE);
							queue.add(newNode);
							explored[top.x - 1][top.y] = newNode;
						}
						if (top.y < map[0].length - 1 && explored[top.x - 1][top.y + 1] == null) {
							PathfindingNode newNode = new PathfindingNode(top.x - 1, top.y + 1, top, end, map[top.x - 1][top.y + 1] == NEUTRALMINE);
							queue.add(newNode);
							explored[top.x - 1][top.y + 1] = newNode;
						}
					}
					if (top.y > 0 && explored[top.x][top.y - 1] == null) {
						PathfindingNode newNode = new PathfindingNode(top.x, top.y - 1, top, end, map[top.x][top.y - 1] == NEUTRALMINE);
						queue.add(newNode);
						explored[top.x][top.y - 1] = newNode;
					}
					if (top.y < map[0].length - 1 && explored[top.x][top.y + 1] == null) {
						PathfindingNode newNode = new PathfindingNode(top.x, top.y + 1, top, end, map[top.x][top.y + 1] == NEUTRALMINE);
						queue.add(newNode);
						explored[top.x][top.y + 1] = newNode;
					}
					if (top.x < map.length - 1) {
						if (top.y > 0 && explored[top.x + 1][top.y - 1] == null) {
							PathfindingNode newNode = new PathfindingNode(top.x + 1, top.y - 1, top, end, map[top.x + 1][top.y - 1] == NEUTRALMINE);
							queue.add(newNode);
							explored[top.x + 1][top.y - 1] = newNode;
						}
						if (explored[top.x + 1][top.y] == null) {
							PathfindingNode newNode = new PathfindingNode(top.x + 1, top.y, top, end, map[top.x + 1][top.y] == NEUTRALMINE);
							queue.add(newNode);
							explored[top.x + 1][top.y] = newNode;
						}
						if (top.y < map[0].length - 1 && explored[top.x + 1][top.y + 1] == null) {
							PathfindingNode newNode = new PathfindingNode(top.x + 1, top.y + 1, top, end, map[top.x + 1][top.y + 1] == NEUTRALMINE);
							queue.add(newNode);
							explored[top.x + 1][top.y + 1] = newNode;
						}
					}
				}
			}
		}
	}*/


	//A failed but potentially salvageable prototype. Finish it if Garwin's idea doesn't work.
	public static LinkedList<MapLocation> tunneler(int[][] map, MapLocation start, MapLocation end) {
		LinkedList<MapLocation> path = new LinkedList<MapLocation>();
		MapLocation currentLoc, tempDest;
		path.add(start);
		while (true) {
			currentLoc = path.peekLast();
			while (map[currentLoc.x][currentLoc.y] != NEUTRALMINE) {
				currentLoc.add(currentLoc.directionTo(end));
				if (currentLoc.equals(end)) { //VICTORY
					path.add(end);
					return path;
				}
			}
			//a mine has been reached
			tempDest = currentLoc;
			while (map[tempDest.x][tempDest.y] == NEUTRALMINE)
				tempDest.add(tempDest.directionTo(end));
			//Path from one to another
			//If a node is found in the given distance, save it
			//If not, it's tunneling time
		}
	}

}

class PathfindingNodeComparator implements Comparator<PathfindingNode> {
	@Override
	public int compare(PathfindingNode o1, PathfindingNode o2) {
		/*if (o1.priority < o2.priority)
			return -1;
		else if (o1.priority > o2.priority)
			return 1;
		else
			return 0;*/
		return o1.priority - o2.priority;
	}
}

class PathfindingNode {
	public PathfindingNode(MapLocation location) {
		loc = location;
	}
	public PathfindingNode(MapLocation location, PathfindingNode previous, boolean currentSpaceIsMined) {
		loc = location;
		prev = previous;
		if (currentSpaceIsMined)
			travelCost = prev.travelCost + 13;
		else
			travelCost = prev.travelCost + 1;
		priority = travelCost;
	}
	public PathfindingNode(MapLocation location, PathfindingNode previous, Direction direction, MapLocation target) {
		loc = location;
		prev = previous;
		dir = direction;
		travelCost = ++prev.travelCost;
		priority = travelCost + 2 * loc.distanceSquaredTo(target);
	}
	public boolean equals(MapLocation other) {
		return loc.equals(other);
	}
	@Override
	public String toString() {
		return loc.toString();
	}
	
	public MapLocation loc;
	public PathfindingNode prev;
	public Direction dir;
	public int travelCost;
	public int priority;
}
