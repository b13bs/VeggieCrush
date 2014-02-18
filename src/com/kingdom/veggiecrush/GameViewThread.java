package com.kingdom.veggiecrush;

import android.annotation.SuppressLint;
import android.graphics.Canvas;

public class GameViewThread extends Thread {
	
	private GameView view;
	private boolean running = false;
	private int refreshRateMS = 0;

	public GameViewThread(GameView view, int refreshRateMS) {
		this.view = view;
		this.refreshRateMS = refreshRateMS;
	}

	@Override
	public void start() {
		running = true;
		super.start();
	}
	
	public void setRunning(boolean run) {
		running = run;
	}

	@SuppressLint("WrongCall")
	@Override
	public void run() {
		long startTime, sleepTime;
		while (running) {
			Canvas c = null;
			startTime = System.currentTimeMillis();
			
			try
			{
				c = view.getHolder().lockCanvas();
				synchronized (view.getHolder()) {
					view.onDraw(c);
				}
			}
			finally
			{
				if (c != null)
				{
					view.getHolder().unlockCanvasAndPost(c);
				}
			}
			
			sleepTime = refreshRateMS - (System.currentTimeMillis() - startTime);
			if (sleepTime > 0 )
			{
				try { Thread.sleep(sleepTime); } catch (Exception e) { e.printStackTrace(); }
			}
		}
	}
}
