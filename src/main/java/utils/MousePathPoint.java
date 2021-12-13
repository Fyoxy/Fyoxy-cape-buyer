// https://osbot.org/forum/topic/143732-mouse-trail-and-cursor-classes/

package utils;

import java.awt.Point;

public class MousePathPoint extends Point{
	
	private long finishTime;

	public MousePathPoint(int x, int y, int lastingTime){
		super(x,y);
		finishTime= System.currentTimeMillis() + lastingTime;
	}
	
	public boolean isUp(){
		return System.currentTimeMillis() > finishTime;
	}
}