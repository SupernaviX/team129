package team129;

import java.util.Random;
import java.util.Vector;
import battlecode.common.Direction;
import battlecode.common.GameConstants;
import battlecode.common.RobotController;
import battlecode.common.RobotType;

/** The example funcs player is a player meant to demonstrate basic usage of the most common commands.
 * Robots will move around randomly, occasionally mining and writing useless messages.
 * The HQ will spawn soldiers continuously. 
 */
public class RobotPlayer {
	private static long[] lastRoundMemory;
	public static void run(RobotController rc) {
		lastRoundMemory = rc.getTeamMemory();
	}
	private void writeData(RobotController rc, int data){
		for(int i = 0; i<4;++i){
			try{
				rc.broadcast((int)lastRoundMemory[i]+offset(i), data);
				rc.broadcast((int)lastRoundMemory[i]+1+offset(i), data<<1);
				rc.broadcast((int)lastRoundMemory[i]+2+offset(i), data<<2);
			}
			catch (Exception e){
				e.printStackTrace();
			}
		}
	}
	//Calculate offset that changes over time?
	private int offset(int i){
		return 0;
	}
	//HQ moves data based on new offset
	private void moveData(){
		
	}
	//Returns true if the instruction returned is valid
	private boolean validInstruction(int instruction){
		return true;
	}
	private int readData(RobotController rc){
		Vector<Integer> instructions;
		for(int i = 0; i<4;++i){
			try{
				int temp = rc.readBroadcast((int)lastRoundMemory[i]+offset(i)), j;
				for(j = 1; j<3;++j){
					if( rc.readBroadcast((int)lastRoundMemory[i]+offset(i)+j)>>j!=temp){
						j = 4;
						i++;
					}
				}
				if(j ==3)
					return temp;			
			}
			catch (Exception e){
				e.printStackTrace();
			}
		}
		//Add logic for if all four blocks are ruined
		return 0;
	}
	
}
