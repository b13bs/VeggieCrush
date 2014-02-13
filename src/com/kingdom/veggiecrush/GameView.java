package com.kingdom.veggiecrush;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.Toast;

public class GameView extends SurfaceView implements SurfaceHolder.Callback {
	
	Handler mHandler = new Handler();
	GameViewThread mThread;

	Veggie[][] grid = new Veggie[8][8];
	
	public GameView(Context context, AttributeSet attrs) {
		super(context, attrs);
		
		getHolder().addCallback(this);
		
	}
	
	@Override
	public void surfaceChanged(SurfaceHolder arg0, int arg1, int arg2, int arg3) {
		// rien
	}

	@Override
	public void surfaceCreated(SurfaceHolder arg0) {
		mThread = new GameViewThread(this);
		mThread.setRunning(true);
		mThread.start();
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder arg0) {
		boolean success = false;
		mThread.setRunning(false);
		while (!success)
		{
			try
			{
				mThread.join();
				success = true;
			}
			catch (InterruptedException e)
			{
				System.err.println("Meh..");
			}
		}
	}
	
	@Override 
    public void onDraw(Canvas canvas) {
		if (canvas != null)
		{
			canvas.drawColor(Color.GREEN);
			
			//TODO: dessiner la grille de légumes
		}
    }
	
	@Override
	public boolean onTouchEvent(MotionEvent e)
	{
		if (e.getAction() == MotionEvent.ACTION_DOWN)
		{
			Toast.makeText(getContext(), "Can't touch this!", Toast.LENGTH_SHORT).show();
			
			//TODO: stuff
		}
		return false;
	}
	
	private Veggie getVeggieFromPos(int posX, int posY)
	{
		//TODO:
		return null;
	}
	
	private void getPosFromIndex(int i, int j)
	{
		//TODO: retourner la position
	}
	
}
