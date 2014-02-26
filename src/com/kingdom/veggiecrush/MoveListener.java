package com.kingdom.veggiecrush;

import com.kingdom.veggiecrush.VeggieGrid.Direction;

import android.graphics.Point;

public interface MoveListener {

	public void onSwipe(Direction d, Point p);
	
}
