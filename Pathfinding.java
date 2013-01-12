package team129;

import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.PriorityQueue;
import java.util.Queue;

import battlecode.common.MapLocation;

public class Pathfinding implements Constants {

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
			PathfindingNode newNode;
			if (endExplored[top.x][top.y] != null) { //The start and end search areas have met
				LinkedList<MapLocation> result = new LinkedList<MapLocation>();
				PathfindingNode otherEnd = endExplored[top.x][top.y];
				while (top != null) {
					result.addFirst(new MapLocation(top.x, top.y));
					top = top.previous;
				}
				while (otherEnd != null) {
					result.addLast(new MapLocation(otherEnd.x, otherEnd.y));
					otherEnd = otherEnd.previous;
				}
				for (int j = 0; j < endExplored[0].length; ++j) {
					String thing = "";
					for (int i = 0; i < endExplored.length; ++i) {
						if (start.equals(new MapLocation(i, j)) || end.equals(new MapLocation(i, j)))
							thing += "0 ";
						else {
							if (endExplored[i][j] == null && startExplored[i][j] != null)
								thing += "S ";
							else if (endExplored[i][j] != null && startExplored[i][j] == null)
								thing += "E ";
							else if (endExplored[i][j] != null && startExplored[i][j] != null)
								thing += "X ";
							else
								thing += "- ";
						}
					}
					System.out.println(thing);
				}
				return result;
			}
			if (top.x > 0) {
				if (top.y > 0 && startExplored[top.x - 1][top.y - 1] == null && map[top.x - 1][top.y - 1] != NEUTRALMINE) {
					newNode = new PathfindingNode(top.x - 1, top.y - 1, top, end);//, map[top.x - 1][top.y - 1] == NEUTRALMINE);
					startQueue.add(newNode);
					startExplored[top.x - 1][top.y - 1] = newNode;
				}
				if (startExplored[top.x - 1][top.y] == null && map[top.x - 1][top.y] != NEUTRALMINE) {
					newNode = new PathfindingNode(top.x - 1, top.y, top, end);//, map[top.x - 1][top.y] == NEUTRALMINE);
					startQueue.add(newNode);
					startExplored[top.x - 1][top.y] = newNode;
				}
				if (top.y < map[0].length - 1 && startExplored[top.x - 1][top.y + 1] == null && map[top.x - 1][top.y + 1] != NEUTRALMINE) {
					newNode = new PathfindingNode(top.x - 1, top.y + 1, top, end);//, map[top.x - 1][top.y + 1] == NEUTRALMINE);
					startQueue.add(newNode);
					startExplored[top.x - 1][top.y + 1] = newNode;
				}
			}
			if (top.y > 0 && startExplored[top.x][top.y - 1] == null && map[top.x][top.y - 1] != NEUTRALMINE) {
				newNode = new PathfindingNode(top.x, top.y - 1, top, end);//, map[top.x][top.y - 1] == NEUTRALMINE);
				startQueue.add(newNode);
				startExplored[top.x][top.y - 1] = newNode;
			}
			if (top.y < map[0].length - 1 && startExplored[top.x][top.y + 1] == null && map[top.x][top.y + 1] != NEUTRALMINE) {
				newNode = new PathfindingNode(top.x, top.y + 1, top, end);//, map[top.x][top.y + 1] == NEUTRALMINE);
				startQueue.add(newNode);
				startExplored[top.x][top.y + 1] = newNode;
			}
			if (top.x < map.length - 1) {
				if (top.y > 0 && startExplored[top.x + 1][top.y - 1] == null && map[top.x + 1][top.y - 1] != NEUTRALMINE) {
					newNode = new PathfindingNode(top.x + 1, top.y - 1, top, end);//, map[top.x + 1][top.y - 1] == NEUTRALMINE);
					startQueue.add(newNode);
					startExplored[top.x + 1][top.y - 1] = newNode;
				}
				if (startExplored[top.x + 1][top.y] == null && map[top.x + 1][top.y] != NEUTRALMINE) {
					newNode = new PathfindingNode(top.x + 1, top.y, top, end);//, map[top.x + 1][top.y] == NEUTRALMINE);
					startQueue.add(newNode);
					startExplored[top.x + 1][top.y] = newNode;
				}
				if (top.y < map[0].length - 1 && startExplored[top.x + 1][top.y + 1] == null && map[top.x + 1][top.y + 1] != NEUTRALMINE) {
					newNode = new PathfindingNode(top.x + 1, top.y + 1, top, end);//, map[top.x + 1][top.y + 1] == NEUTRALMINE);
					startQueue.add(newNode);
					startExplored[top.x + 1][top.y + 1] = newNode;
				}
			}

			//Run A* going from the end position to the start position
			top = endQueue.poll();
			if (startExplored[top.x][top.y] != null) { //The end and start search areas have met
				LinkedList<MapLocation> result = new LinkedList<MapLocation>();
				PathfindingNode otherEnd = startExplored[top.x][top.y];
				while (top != null) {
					result.addFirst(new MapLocation(top.x, top.y));
					top = top.previous;
				}
				while (otherEnd != null) {
					result.addLast(new MapLocation(otherEnd.x, otherEnd.y));
					otherEnd = otherEnd.previous;
				}
				for (int j = 0; j < endExplored[0].length; ++j) {
					String thing = "";
					for (int i = 0; i < endExplored.length; ++i) {
						if (start.equals(new MapLocation(i, j)) || end.equals(new MapLocation(i, j)))
							thing += "0 ";
						else {
							if (endExplored[i][j] == null && startExplored[i][j] != null)
								thing += "S ";
							else if (endExplored[i][j] != null && startExplored[i][j] == null)
								thing += "E ";
							else if (endExplored[i][j] != null && startExplored[i][j] != null)
								thing += "X ";
							else
								thing += "- ";
						}
					}
					System.out.println(thing);
				}
				return result;
			}
			if (top.x > 0) {
				if (top.y > 0 && endExplored[top.x - 1][top.y - 1] == null && map[top.x - 1][top.y - 1] != NEUTRALMINE) {
					newNode = new PathfindingNode(top.x - 1, top.y - 1, top, start);//, map[top.x - 1][top.y - 1] == NEUTRALMINE);
					endQueue.add(newNode);
					endExplored[top.x - 1][top.y - 1] = newNode;
				}
				if (endExplored[top.x - 1][top.y] == null && map[top.x - 1][top.y] != NEUTRALMINE) {
					newNode = new PathfindingNode(top.x - 1, top.y, top, start);//, map[top.x - 1][top.y] == NEUTRALMINE);
					endQueue.add(newNode);
					endExplored[top.x - 1][top.y] = newNode;
				}
				if (top.y < map[0].length - 1 && endExplored[top.x - 1][top.y + 1] == null && map[top.x - 1][top.y + 1] != NEUTRALMINE) {
					newNode = new PathfindingNode(top.x - 1, top.y + 1, top, start);//, map[top.x - 1][top.y + 1] == NEUTRALMINE);
					endQueue.add(newNode);
					endExplored[top.x - 1][top.y + 1] = newNode;
				}
			}
			if (top.y > 0 && endExplored[top.x][top.y - 1] == null && map[top.x][top.y - 1] != NEUTRALMINE) {
				newNode = new PathfindingNode(top.x, top.y - 1, top, start);//, map[top.x][top.y - 1] == NEUTRALMINE);
				endQueue.add(newNode);
				endExplored[top.x][top.y - 1] = newNode;
			}
			if (top.y < map[0].length - 1 && endExplored[top.x][top.y + 1] == null && map[top.x][top.y + 1] != NEUTRALMINE) {
				newNode = new PathfindingNode(top.x, top.y + 1, top, start);//, map[top.x][top.y + 1] == NEUTRALMINE);
				endQueue.add(newNode);
				endExplored[top.x][top.y + 1] = newNode;
			}
			if (top.x < map.length - 1) {
				if (top.y > 0 && endExplored[top.x + 1][top.y - 1] == null && map[top.x + 1][top.y - 1] != NEUTRALMINE) {
					newNode = new PathfindingNode(top.x + 1, top.y - 1, top, start);//, map[top.x + 1][top.y - 1] == NEUTRALMINE);
					endQueue.add(newNode);
					endExplored[top.x + 1][top.y - 1] = newNode;
				}
				if (endExplored[top.x + 1][top.y] == null && map[top.x + 1][top.y] != NEUTRALMINE) {
					newNode = new PathfindingNode(top.x + 1, top.y, top, start);//, map[top.x + 1][top.y] == NEUTRALMINE);
					endQueue.add(newNode);
					endExplored[top.x + 1][top.y] = newNode;
				}
				if (top.y < map[0].length - 1 && endExplored[top.x + 1][top.y + 1] == null && map[top.x + 1][top.y + 1] != NEUTRALMINE) {
					newNode = new PathfindingNode(top.x + 1, top.y + 1, top, start);//, map[top.x + 1][top.y + 1] == NEUTRALMINE);
					endQueue.add(newNode);
					endExplored[top.x + 1][top.y + 1] = newNode;
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
				queue.add(new PathfindingNode(loc.x, loc.y));
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
							PathfindingNode newNode = new PathfindingNode(top.x - 1, top.y - 1, top, end);//, map[top.x - 1][top.y - 1] == NEUTRALMINE);
							queue.add(newNode);
							explored[top.x - 1][top.y - 1] = newNode;
						}
						if (explored[top.x - 1][top.y] == null) {
							PathfindingNode newNode = new PathfindingNode(top.x - 1, top.y, top, end);//, map[top.x - 1][top.y] == NEUTRALMINE);
							queue.add(newNode);
							explored[top.x - 1][top.y] = newNode;
						}
						if (top.y < map[0].length - 1 && explored[top.x - 1][top.y + 1] == null) {
							PathfindingNode newNode = new PathfindingNode(top.x - 1, top.y + 1, top, end);//, map[top.x - 1][top.y + 1] == NEUTRALMINE);
							queue.add(newNode);
							explored[top.x - 1][top.y + 1] = newNode;
						}
					}
					if (top.y > 0 && explored[top.x][top.y - 1] == null) {
						PathfindingNode newNode = new PathfindingNode(top.x, top.y - 1, top, end);//, map[top.x][top.y - 1] == NEUTRALMINE);
						queue.add(newNode);
						explored[top.x][top.y - 1] = newNode;
					}
					if (top.y < map[0].length - 1 && explored[top.x][top.y + 1] == null) {
						PathfindingNode newNode = new PathfindingNode(top.x, top.y + 1, top, end);//, map[top.x][top.y + 1] == NEUTRALMINE);
						queue.add(newNode);
						explored[top.x][top.y + 1] = newNode;
					}
					if (top.x < map.length - 1) {
						if (top.y > 0 && explored[top.x + 1][top.y - 1] == null) {
							PathfindingNode newNode = new PathfindingNode(top.x + 1, top.y - 1, top, end);//, map[top.x + 1][top.y - 1] == NEUTRALMINE);
							queue.add(newNode);
							explored[top.x + 1][top.y - 1] = newNode;
						}
						if (explored[top.x + 1][top.y] == null) {
							PathfindingNode newNode = new PathfindingNode(top.x + 1, top.y, top, end);//, map[top.x + 1][top.y] == NEUTRALMINE);
							queue.add(newNode);
							explored[top.x + 1][top.y] = newNode;
						}
						if (top.y < map[0].length - 1 && explored[top.x + 1][top.y + 1] == null) {
							PathfindingNode newNode = new PathfindingNode(top.x + 1, top.y + 1, top, end);//, map[top.x + 1][top.y + 1] == NEUTRALMINE);
							queue.add(newNode);
							explored[top.x + 1][top.y + 1] = newNode;
						}
					}
				}
			}
		}
	}

	//Finds the distance from a single point to every other point on the map
	public static int[][] calculateDistanceMap(int[][] map, MapLocation start) {
		int[][] distances = new int[map.length][map[0].length];
		Queue<PathfindingNode> queue = new LinkedList<PathfindingNode>();
		queue.add(new PathfindingNode(start.x, start.y));
		while (!queue.isEmpty()) {
			PathfindingNode current = queue.poll();
			distances[current.x][current.y] = current.travelCost;
			if (current.x > 0) {
				if (current.y > 0 && distances[current.x - 1][current.y - 1] == 0) {
					PathfindingNode newNode = new PathfindingNode(current.x - 1, current.y - 1, current, map[current.x - 1][current.y - 1] == NEUTRALMINE);
					queue.add(newNode);
					distances[newNode.x][newNode.y] = newNode.travelCost; 
				}
				if (distances[current.x - 1][current.y] == 0) {
					PathfindingNode newNode = new PathfindingNode(current.x - 1, current.y, current, map[current.x - 1][current.y] == NEUTRALMINE);
					queue.add(newNode);
					distances[newNode.x][newNode.y] = newNode.travelCost; 
				}
				if (current.y < distances[0].length - 1 && distances[current.x - 1][current.y + 1] == 0) {
					PathfindingNode newNode = new PathfindingNode(current.x - 1, current.y + 1, current, map[current.x - 1][current.y + 1] == NEUTRALMINE);
					queue.add(newNode);
					distances[newNode.x][newNode.y] = newNode.travelCost; 
				}
			}
			if (current.y > 0 && distances[current.x][current.y - 1] == 0) {
				PathfindingNode newNode = new PathfindingNode(current.x, current.y - 1, current, map[current.x][current.y - 1] == NEUTRALMINE);
				queue.add(newNode);
				distances[newNode.x][newNode.y] = newNode.travelCost; 
			}
			if (current.y < distances[0].length - 1 && distances[current.x][current.y + 1] == 0) {
				PathfindingNode newNode = new PathfindingNode(current.x, current.y + 1, current, map[current.x][current.y + 1] == NEUTRALMINE);
				queue.add(newNode);
				distances[newNode.x][newNode.y] = newNode.travelCost; 
			}
			if (current.x < distances.length - 1) {
				if (current.y > 0 && distances[current.x + 1][current.y - 1] == 0) {
					PathfindingNode newNode = new PathfindingNode(current.x + 1, current.y - 1, current, map[current.x + 1][current.y - 1] == NEUTRALMINE);
					queue.add(newNode);
					distances[newNode.x][newNode.y] = newNode.travelCost; 
				}
				if (distances[current.x + 1][current.y] == 0) {
					PathfindingNode newNode = new PathfindingNode(current.x + 1, current.y, current, map[current.x + 1][current.y] == NEUTRALMINE);
					queue.add(newNode);
					distances[newNode.x][newNode.y] = newNode.travelCost; 
				}
				if (current.y < distances[0].length - 1 && distances[current.x + 1][current.y + 1] == 0) {
					PathfindingNode newNode = new PathfindingNode(current.x + 1, current.y + 1, current, map[current.x + 1][current.y + 1] == NEUTRALMINE);
					queue.add(newNode);
					distances[newNode.x][newNode.y] = newNode.travelCost; 
				}
			}
		}
		distances[start.x][start.y] = 0;
		return distances;
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
	}
	public PathfindingNode(int myX, int myY, PathfindingNode prev, boolean currentSpaceIsMined) {
		x = myX;
		y = myY;
		previous = prev;
		if (currentSpaceIsMined)
			travelCost = prev.travelCost + 13;
		else
			travelCost = prev.travelCost + 1;
		priority = travelCost;
	}
	public PathfindingNode(int myX, int myY, PathfindingNode prev, MapLocation target) {
		x = myX;
		y = myY;
		previous = prev;
		//if (currentSpaceIsMined)
			//travelCost = prev.travelCost + 5;
		//else
			travelCost = prev.travelCost + 1;
		priority = travelCost + 1.005 * Math.max(Math.abs(target.x - x), Math.abs(target.y - y));
	}
	public boolean equals(MapLocation other) {
		return (x == other.x && y == other.y);
	}
	@Override
	public String toString() {
		return "(" + x + ", " + y + ")";
	}
	
	public int x;
	public int y;
	public PathfindingNode previous;
	public int travelCost;
	public double priority;
}

