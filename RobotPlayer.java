package team129;

import battlecode.common.Clock;
import battlecode.common.Direction;
import battlecode.common.GameConstants;
import battlecode.common.RobotController;
import battlecode.common.RobotType;

/** The example funcs player is a player meant to demonstrate basic usage of the most common commands.
 * Robots will move around randomly, occasionally mining and writing useless messages.
 * The HQ will spawn soldiers continuously. 
 */
public class RobotPlayer {
    public static void run(RobotController rc) {
    while (true) {
      try {
        if (rc.getType() == RobotType.HQ) {
          /*
           * For turn 100 and every 5th turn after, issue global suicide order.
           * Do not do this if the robots say '1'.
           * For some reason, the HQ will always read '0', even if it is reading the correct channel.
           * Perhaps HQ has become self-aware?
           */
          if (Clock.getRoundNum() > 100 && Clock.getRoundNum() % 5 == 0 && RobotSpeak.hqRead(rc) != 1)
          {
      		  RobotSpeak.hqBroadcast(rc, 1);
          }
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
          //Past turn 200, tell HQ to stop killing us. Hope HQ is complaint..
          if (Clock.getRoundNum() > 200)
          {
        	  RobotSpeak.robotBroadcast(rc, 1);
          }
          //This part should be obvious.
          if (RobotSpeak.robotRead(rc) == 1)
          {
			rc.suicide();
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
}
