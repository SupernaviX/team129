package team129;

import battlecode.common.Direction;
import battlecode.common.GameConstants;
import battlecode.common.RobotController;
import battlecode.common.RobotType;
import battlecode.common.MapLocation;
import java.util.ArrayList;
import java.util.LinkedList;

/** The example funcs player is a player meant to demonstrate basic usage of the most common commands.
 * Robots will move around randomly, occasionally mining and writing useless messages.
 * The HQ will spawn soldiers continuously. 
 */

public class HoodPlayer implements Constants{

	
	public static void run(RobotController rc) {
		//int[][] map;
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
	public void orderCamps(MapLocation[][] camps){
		
	}
	
	//Next four methods are used in getBottleNecks to calculate bottlenecks
	public int checkXBottleNeck(MapLocation loc, RobotController rc, int[][] map){
		int top = loc.y, bot = loc.y, total = 0;
		while((map[loc.x][--top]!=NEUTRALMINE&&top>=0)&&total<=MAX_BOTTLENECK_CHECK)
			total++;
		while((map[loc.x][++bot]!=NEUTRALMINE&&bot<rc.getMapHeight())&&total<=MAX_BOTTLENECK_CHECK)
			total++;
		return total;
	}
	public int checkYBottleNeck(MapLocation loc, RobotController rc, int[][] map){
		int left = loc.x, right = loc.x, total = 0;
		while((map[--left][loc.y]!=NEUTRALMINE&&left>=0)&&total<=MAX_BOTTLENECK_CHECK)
			total++;
		while((map[++right][loc.y]!=NEUTRALMINE&&right<rc.getMapWidth())&&total<=MAX_BOTTLENECK_CHECK)
			total++;
		return total;
	}
	public int checkNAngleBottleNeck(MapLocation loc, RobotController rc, int[][] map){
		int left = loc.x, right = loc.x, total = 0, top = loc.y, bot = loc.y;
		while((map[--left][++top]!=NEUTRALMINE&&left>=0&&top<rc.getMapHeight())&&total<=MAX_BOTTLENECK_CHECK)
			total++;
		while(total<=MAX_BOTTLENECK_CHECK&&map[++right][--bot]!=NEUTRALMINE&&bot>=0&&right<rc.getMapWidth())
			++total;
		return total;
	}
	public int checkPAngleBottleNeck(MapLocation loc, RobotController rc, int[][] map){
		int left = loc.x, right = loc.x, total = 0, top = loc.y, bot = loc.y;
		while((map[--left][--top]!=NEUTRALMINE&&left>=0&&top>=0)&&total<=MAX_BOTTLENECK_CHECK)
			total++;
		while(total<=MAX_BOTTLENECK_CHECK&&map[++right][++bot]!=NEUTRALMINE&&bot<rc.getMapHeight()&&right<rc.getMapWidth())
			total++;
		return total;
	}
	//In the map array, the hundreds place of the number is the bottleneck value
	public ArrayList<MapLocation> getBottleNecks(ArrayList<MapLocation> path, RobotController rc, int[][] map){
		ArrayList<MapLocation> ret = new ArrayList<MapLocation>();
		for(int i = 2; i<path.size();++i){
			int val;
			if(path.get(i).x == path.get(i-2).x){
				  val = checkXBottleNeck(path.get(i-1), rc, map );
				map[path.get(i-1).x][path.get(i-1).y] +=100*val;
			}
			else if(path.get(i).y == path.get(i-2).y){
				  val = checkYBottleNeck(path.get(i-1), rc, map);
				map[path.get(i-1).x][path.get(i-1).y] +=100*val;
			}
			else if(path.get(i).y < path.get(i-2).y){
				  val = checkPAngleBottleNeck(path.get(i-1), rc, map);
				map[path.get(i-1).x][path.get(i-1).y] +=100*val;
			}
			else{
				  val = checkNAngleBottleNeck(path.get(i-1), rc, map);
				map[path.get(i-1).x][path.get(i-1).y] +=100*val;
			}
			//This method doesn't give a fuck about the last spot being a bottleneck
		}
		return ret;
	}
	/*
	 * Calculates the contiguous camps and saves the number of adjacent camps in the map array 
	 */
	public void contiguousCamps(MapLocation[] camps, RobotController rc, int[][] map){
		//LinkedList<MapLocation> ret = new LinkedList<MapLocation>();
		LinkedList<MapLocation> queue = new LinkedList<MapLocation>();
		LinkedList<MapLocation> contiguousBlock = new LinkedList<MapLocation>();
		for(int j = 0; j<camps.length;++j){
			queue.clear();
			queue.add(camps[j]);
			MapLocation camp;
			if(getField(map[queue.get(0).x][queue.get(0).y], FIRST_CHECK_FIELD)==1)
			{
				contiguousBlock.clear();
				contiguousBlock.add(queue.get(0));
				while(!queue.isEmpty()){
					camp = queue.poll();
					int x = camp.x, y = camp.y;
					for(int i = 1; i<9;++i){
						switch (i){
						case 1:
							if(y+1<rc.getMapHeight()&&
									getField(map[x][y+1], TYPE_FIELD)==CAMP&&
									getField(map[x][y+1], FIRST_CHECK_FIELD)!=1) {
									//ret.add(new MapLocation(x, y+1));
									map[x][y+1] = setField(map[x][y+1], FIRST_CHECK_FIELD, 1);
									contiguousBlock.add(camp);
							}
							break;
						case 2:
							if(x+1<rc.getMapWidth()&&y+1<rc.getMapHeight()&&
									getField(map[x+1][y+1], TYPE_FIELD)==CAMP&&
									getField(map[x+1][y+1], FIRST_CHECK_FIELD)!=1) {
									//ret.add(new MapLocation(x+1, y+1));
									map[x+1][y+1] = setField(map[x+1][y+1], FIRST_CHECK_FIELD, 1);
									contiguousBlock.add(camp);
							}
							break;
						case 3: 
							if(x!=0&&
								getField(map[x-1][y], TYPE_FIELD)==CAMP&&
								getField(map[x-1][y], FIRST_CHECK_FIELD)!=1) {
								//ret.add(new MapLocation(x-1, y));
								map[x-1][y] = setField(map[x-1][y], FIRST_CHECK_FIELD, 1);
								contiguousBlock.add(camp);
							}
							break;
						case 4:
							if(x+1<rc.getMapWidth()&&
									getField(map[x+1][y], TYPE_FIELD)==CAMP&&
									getField(map[x+1][y], FIRST_CHECK_FIELD)!=1) {
									//ret.add(new MapLocation(x+1, y));
									map[x+1][y] = setField(map[x+1][y], FIRST_CHECK_FIELD, 1);
									contiguousBlock.add(camp);
							}
							break;
						case 5: 
							if(x!=0&&y!=0&&
								getField(map[x-1][y-1], TYPE_FIELD)==CAMP&&
								getField(map[x-1][y-1], FIRST_CHECK_FIELD)!=1) {
								//ret.add(new MapLocation(x-1, y+1));
								map[x-1][y-1] = setField(map[x-1][y-1], FIRST_CHECK_FIELD, 1);
								contiguousBlock.add(camp);
							}
							break;
						case 6:
							if(y!=0&&
									getField(map[x][y-1], TYPE_FIELD)==CAMP&&
									getField(map[x][y-1], FIRST_CHECK_FIELD)!=1) {
									//ret.add(new MapLocation(x, y-1));
									map[x][y-1] = setField(map[x][y-1], FIRST_CHECK_FIELD, 1);
									contiguousBlock.add(camp);
							}
							break;
						case 7:
							if(x+1<rc.getMapWidth()&&y!=0&&
									getField(map[x+1][y-1], TYPE_FIELD)==CAMP&&
									getField(map[x+1][y-1], FIRST_CHECK_FIELD)!=1) {
									//ret.add(new MapLocation(x+1, y-1));
									map[x+1][y-1] = setField(map[x+1][y-1], FIRST_CHECK_FIELD, 1);
									contiguousBlock.add(camp);
							}
						case 8: 
							if(x!=0&&y+1<rc.getMapHeight()&&
								getField(map[x-1][y+1], TYPE_FIELD)==CAMP&&
								getField(map[x-1][y+1], FIRST_CHECK_FIELD)!=1) {
								//ret.add(new MapLocation(x-1, y+1));
								map[x-1][y+1] = setField(map[x-1][y+1], FIRST_CHECK_FIELD, 1);
								contiguousBlock.add(camp);
							}
							break;
						}
					}
				}
				int size = Math.min(contiguousBlock.size(), 15);
				while(!contiguousBlock.isEmpty()){
					camp = contiguousBlock.poll();
					map[camp.x][camp.y] = setField(map[camp.x][camp.y], NUM_CAMPS_FIELD, size);
				}
			}
			
		}
		//return ret;
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
	/*
	//FOR MAP USAGE!!!!!
	
	//Bits 0-3 Alliance: Unknown = 00 Neutral = 11 Allied = 01 Enemy = 10
	//Bits 4-7 Basic Type: Unknown = 000 Mine = 001 HQ = 010 Soldier = 011 Camp = 100
	
	
	//CAMPS ONLY:
	//Bits 8-11 Camp Type: Unknown = 000 Medbay = 001 Shield = 010 Supply = 011 Generator = 100 Artillery = 101
	//Bits 12-15 Number of Contiguous Camps: Unknown = 0000, if non-zero, it is the number of contiguous camps, where 1111 signifies >=15

	
	//Bits 16-19 Bottleneck size: Unknown = 000, if non-zero, it is simply the bottleneck width, where 111 signifies >=7
	
	//Bits 20-29 Unassigned control bits as of now
	//Bits 30/31 Check bits: Use FIRST_CHECK_FIELD and SECOND_CHECK_FIELD for the bits
	*/
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
	public static int[][][] emptygrid;
	public static int[][] clockwise = {{0,0},{1,0},{2,0}, {2, 1},{2, 2},{1,2},{0, 2},{0, 1}};
	//need to do this eventually
	public static void fill(int[][] map, int[][] grid3x3){
		
	}
	//temparrays[].length = 4 because you can have at most 4 distinct subsections in a 3x3 grid
	public static int[][][] process3x3(int[][] map, int[][][] temparrays, int current, int width, int height){
		//Needs modification for grids not 3x3
		
		//Curgrid is used JUST to label subregions for the map. This could probably be optimized to go straight to the map
		//instead of using curgrid as an intermediate step
		int[][] curgrid = new int[3][3];
		//Upper left corner of grid
		//curRegion is the current number of subsections
		int centerx = (current/width) * 3, centery = (current%width) * 3, curRegion = 0;
		//True == mine
		if(map[centerx+1][centery+1]!=NEUTRALMINE){
			//Every free space is in curRegion 1
			int[][][] ret = new int[3][3][1];
			for(int i = 0; i<3;++i){
				for(int j = 0; j<3;++j){
					if(map[centerx+i][centery+j]==NEUTRALMINE){
						//Because any mine reached from a center open point is 1
						ret[i][j][0] = 1;
					}
					else{
						//Because curRegion is 1
						curgrid[i][j] = 1;
					}
				}
			}
			return ret;
		}
		else{
			boolean lastspaceismine = true, currentspaceismine = true;
			//First position gets the coord pair
			//Hard case
			lastspaceismine = map[clockwise[7][0]+centerx][clockwise[7][1]+centery]==NEUTRALMINE;
			int i = 0;
			//Loop until you find first fresh segment
			for(;i<8;++i){
				currentspaceismine = map[clockwise[i][0]+centerx][clockwise[i][1]+centery]==NEUTRALMINE;
				if(lastspaceismine&&!currentspaceismine){
					i+=9;
				}
				else{
					lastspaceismine = currentspaceismine;
				}
			}
			i-=10;
			//placeholder for simple case to finish later
			int j = i;
			if(i==-2){
				return emptygrid;
			}
			else{
				do{
					currentspaceismine = map[clockwise[j][0]+centerx][clockwise[j][1]+centery]==NEUTRALMINE;
					if(!currentspaceismine){
						if(lastspaceismine){
							curgrid[clockwise[j][0]][clockwise[j][1]] = ++curRegion;
						}
						else{
							curgrid[clockwise[j][0]][clockwise[j][1]] = curRegion;
						}
						//Fills adjacent spaces for the corresponding subsection
						//Needs to be double checked for correctness
						switch(j){
						case 0:
							temparrays[curRegion-1][1][0]=1;
							temparrays[curRegion-1][0][1]=1;
							break;
						case 1:
							temparrays[curRegion-1][0][0]=1;
							temparrays[curRegion-1][2][0]=1;
							temparrays[curRegion-1][0][1]=1;
							temparrays[curRegion-1][2][1]=1;
							break;
						case 2:
							temparrays[curRegion-1][1][0]=1;
							temparrays[curRegion-1][2][1]=1;
							break;
						case 3:
							temparrays[curRegion-1][2][2]=1;
							temparrays[curRegion-1][2][0]=1;
							temparrays[curRegion-1][1][2]=1;
							temparrays[curRegion-1][1][0]=1;
							break;
						case 4:
							temparrays[curRegion-1][0][1]=1;
							temparrays[curRegion-1][1][2]=1;
							break;
						case 5:
							temparrays[curRegion-1][0][2]=1;
							temparrays[curRegion-1][2][2]=1;
							temparrays[curRegion-1][0][1]=1;
							temparrays[curRegion-1][2][1]=1;
							break;
						case 6:
							temparrays[curRegion-1][2][1]=1;
							temparrays[curRegion-1][1][2]=1;
							break;
						case 7:
							temparrays[curRegion-1][0][2]=1;
							temparrays[curRegion-1][0][0]=1;
							temparrays[curRegion-1][1][0]=1;
							temparrays[curRegion-1][1][2]=1;
							break;
						}
					}
					j = (j+1)%8;
				}
				while(j!=i);
				//At this point, all representations of all subsections are filled
				//Now to finish and return
				int[][][] ret = new int[3][3][curRegion];
				if(curRegion!=1){
					for(i = 0;i<8;++i){
						if(map[clockwise[i][0]+centerx][clockwise[i][1]+centery]==NEUTRALMINE){
							for(j = 0; j<curRegion;++j){
								if(temparrays[j][clockwise[i][0]][clockwise[i][1]]==1){
									ret[clockwise[i][0]][clockwise[i][1]][j] = 1;
								}
								else
								{
									ret[clockwise[i][0]][clockwise[i][1]][j] = 2;
								}
							}
						}
						else{
							//Remember, this is the subregion area
							int daRegion = curgrid[clockwise[i][0]][clockwise[i][1]];
							//Regions are from 1-4, stored from 0-3!
							j = daRegion-1;
							while(j!= daRegion){
								j= (j+1)%curRegion;
								ret[clockwise[i][0]][clockwise[i][1]][j] = 1;
							}
						}
						
					}
				}
				else{
					for(i = 0;i<8;++i){
						if(map[clockwise[i][0]+centerx][clockwise[i][1]+centery]==NEUTRALMINE){
							for(j = 0; j<curRegion+1;++j){
								if(temparrays[j][clockwise[i][0]][clockwise[i][1]]==1){
									ret[clockwise[i][0]][clockwise[i][1]][j] = 1;
								}
								else
								{
									ret[clockwise[i][0]][clockwise[i][1]][j] = 2;
								}
							}
						}
					}
				}
				return ret;
			}
		}
	}
}
