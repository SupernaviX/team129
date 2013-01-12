package team129;

import battlecode.common.Clock;
import battlecode.common.GameActionException;
import battlecode.common.RobotController;

class RobotSpeak
{
	/*
*	public static void robotBroadcast(RobotController rc, int cmdFlag, boolean squad)
*	{
*	}
	*/
	public static int robotRead(RobotController rc) throws GameActionException
	{
		int channel = hqChanHash();
		return rc.readBroadcast(channel);
	}
	
	public static void hqBroadcast(RobotController rc, int msg) throws GameActionException
	{
		int channel = hqChanHash();
		rc.broadcast(channel, msg);
	}

	/*
	public static int hqRead()
*	{	
*		int channel = robotChanHash();
*	}
	
	private static int robotChanHash()
	{
		int x = Clock.getRoundNum();
		int channel = (int)Math.sin(x)*5000-5000-10*(int)x;
		if (channel > 10000)
		{
			channel -= 10000;
		} 
		else if (channel < 0)
		{
			channel += 10000;
		}
		return channel;
	}
	*/
	private static int hqChanHash()
	{
		double x = Clock.getRoundNum();
		int channel = (int)Math.sin(x)*32767+32767-10*(int)x-10*(int)x;
		while (channel < 0)
		{
			channel += 65545;
		}
		return channel;
	}
}