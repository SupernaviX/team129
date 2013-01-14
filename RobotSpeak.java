package team129;

import battlecode.common.Clock;
import battlecode.common.GameActionException;
import battlecode.common.RobotController;

class RobotSpeak
{
	/*
	 * Tessy, you probably want to write up most of the encryption in RobotPlayer.java.
	 * So the messages would be encoded BEFORE being sent to robotBroadcast.
	 * I think it should be done this way since we do not know how the messages
	 * would be handled by robotRead() and hqRead(), and we don't need to fatten up
	 * those funcs with irrelevant functionality.
	 * Maybe it would better to have a RobotEnc class? It's your show, Tessy. ;)
	 */
	
	public static void robotBroadcast(RobotController rc, int msg) throws GameActionException
	{
		/*
		 * The outbound channel utilized by the robots, exists on the robot bandwidth.
		 * TODO: Setup team authentication, to decide whether or not to overwrite it.
		 * If the channel is occupied by a friendly message, try to allocate it to the 'next' channel. 
		 */
		int channel = robotChanHash();
		/*
		int currentMsg = rc.readBroadcast(channel);
		while (currentMsg is one of ours)
		{
			check the next channel!
		}
		*/
		rc.broadcast(channel, msg);
		System.out.println("Grunt broadcasting " +msg+" to channel " + channel);
	}

	public static int robotRead(RobotController rc) throws GameActionException
	{
		/*
		 * The inbound channel utilized by the robots, exists on the HQ bandwidth.
		 * TODO: Setup team aunthentication.
		 * If it ain't ours, maybe we should fuck it up?
		 */
		int channel = hqChanHash();
		int currentMsg = rc.readBroadcast(channel);
		/*
		while (currentMsg doesn't apply to rc)
		{
			check the next channel!
		}
		
		do what must be done!
		*/
		System.out.println("Grunt hears " +currentMsg+ " at " + channel);
		return currentMsg;
	}
	
	public static void hqBroadcast(RobotController rc, int msg) throws GameActionException
	{
		/*
		 * The outbound channel utilized by HQ, exists on the HQ bandwidth.
		 * TODO: Setup team authentication.
		 */
		int channel = hqChanHash();
		/*
		int currentMsg = rc.readBroadcast(channel);
		while (currentMsg is one of ours)
		{
			check the next channel!
		}
		*/
		rc.broadcast(channel, msg);
		System.out.println("HQ broadcasting " +msg+ " to channel " + channel);
	}


	public static int hqRead(RobotController rc) throws GameActionException
	{	
		/*
		 * The inbound channel utilized by HQ, exists on the robot bandwidth.
		 * TODO: Setup team authentication.
		 */
		int channel = robotChanHash();
		int currentMsg = rc.readBroadcast(channel);
		/*
		while (currentMsg doesn't apply to rc)
		{
			check the next channel!
		}
		*/
		System.out.println("HQ hears " +currentMsg+ " at " + channel);
		return currentMsg;
	}
	
	private static int robotChanHash()
	{
		/*
		 * Messages 'persist' through int division, so the same couple of channels
		 * will be used for 'msgAge' turns.
		 * This hash will allocate the robot bandwidth to the lower range.
		 */
		int msgAge = 20;
		double x = Clock.getRoundNum();
		int channel = (int)Math.sin(x)*32767-32767-(int)x/msgAge*1000;
		while (channel < 0)
		{
			channel += 65545;
		}
		return channel;
	}
	
	private static int hqChanHash()
	{
		/*
		 * This hash will allocate the HQ bandwidth to the upper range.
		 */
		int msgAge = 20;
		double x = Clock.getRoundNum();
		int channel = (int)Math.sin(x)*32767+32767-(int)x/msgAge*1000;
		while (channel < 0)
		{
			channel += 65545;
		}
		return channel;
	}
}