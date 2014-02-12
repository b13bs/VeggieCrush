package com.kingdom.veggiecrush;

import android.annotation.SuppressLint;
import android.graphics.Canvas;

public class GameViewThread extends Thread {
	
	private GameView mView;
	private boolean mRunning = false;

	public GameViewThread(GameView view) {
		mView = view;
	}

	@Override
	public void start() {
		mRunning = true;
		super.start();
	}
	
	public void setRunning(boolean run) {
		mRunning = run;
	}

	@SuppressLint("WrongCall")
	@Override
	public void run() {
		long startTime, sleepTime;
		while (mRunning) {
			Canvas c = null;
			startTime = System.currentTimeMillis();
			
			try
			{
				c = mView.getHolder().lockCanvas();
				synchronized (mView.getHolder()) {
					mView.onDraw(c);
				}
			}
			finally
			{
				if (c != null)
				{
					mView.getHolder().unlockCanvasAndPost(c);
				}
			}
			
			sleepTime = startTime - System.currentTimeMillis();
			if (sleepTime > 0 )
			{
				try { Thread.sleep(16); } catch (Exception e) { e.printStackTrace(); }
			}
		}
	}
}
