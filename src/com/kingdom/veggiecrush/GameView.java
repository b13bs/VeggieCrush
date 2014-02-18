package com.kingdom.veggiecrush;

import java.util.Random;

import com.kingdom.veggiecrush.Veggie.VeggieKind;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.widget.Toast;

public class GameView extends SurfaceView implements SurfaceHolder.Callback {
	
	private static enum Direction { LEFT, RIGHT, UP, DOWN };
	
	private GameViewThread mThread;
	private GestureDetector gestDetector;
	private Paint paint;
	
	private final int GRID_SIZE = 8;
	private Veggie[][] grid = new Veggie[GRID_SIZE][GRID_SIZE];
	
	public GameView(Context context, AttributeSet attrs) {
		super(context, attrs);
		
		gestDetector = new GestureDetector(context, new GestureListener());
		paint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG);
		
		getHolder().addCallback(this);
		
		Settings.loadBitmaps(getContext());
		initGameGrid();
	}
	
	@Override
	public void surfaceChanged(SurfaceHolder arg0, int arg1, int arg2, int arg3) {
		// rien
	}

	@Override
	public void surfaceCreated(SurfaceHolder arg0) {
		// On crée et enregistre le thread pour rafraichir la surface de jeu
		mThread = new GameViewThread(this);
		mThread.setRunning(true);
		mThread.start();
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder arg0) {
		// On arrête le thread de rafraichissement
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
	
	public void initGameGrid()
	{
		// On remplie la grille pour commencer une nouvelle partie
		Random rand = new Random();
		for (int j = 0; j < GRID_SIZE; ++j)
		{
			for (int i = 0; i < GRID_SIZE; ++i)
			{
				// TODO: Verifier qu'il n'y a pas 3 légumes pareils en ligne..
				// + autres règles pour le grille initiale?
				grid[i][j] = new Veggie(getContext(), VeggieKind.values()[rand.nextInt(VeggieKind.values().length)]);
			}
		}
	}
	
	@Override 
    public void onDraw(Canvas canvas) {
		if (canvas != null)
		{
			// On dessine l'arrière plan
			canvas.drawColor(Color.rgb(46, 97, 0));
			
			// On affiche la grille avec les légumes
			for (int j = 0; j < GRID_SIZE; ++j)
			{
				for (int i = 0; i < GRID_SIZE; ++i)
				{
					if (grid[i][j] != null)
					{
						canvas.drawBitmap(grid[i][j].bitmap, grid[i][j].bitmapRect, getRectFromIndex(i, j), paint);
					}
				}
			}
		}
    }
	
	@Override
	public boolean onTouchEvent(MotionEvent e)
	{
		gestDetector.onTouchEvent(e);
		return true;
	}
	
	private void onSwipe(Direction d, Point p)
	{
		Point index = getIndexFromPos(p.x, p.y);
		Toast.makeText(getContext(), d + " i=" + index.x + " j=" + index.y, Toast.LENGTH_SHORT).show();
		// TODO: déterminer c'est quoi l'autre case selon case recue et direction
		//if estDeplacementValide(...)
		//switchPlace(...);
		
		crush();
	}
	
	private boolean estDeplacementValide()
	{
		// TODO: vérifier si ca donne une ligne d'au moins 3
		return false;
	}
	
	private void crush()
	{
		// TODO: détruire les lignes de 3 ou +
	}
	
	private void switchPlace(Point p1, Point p2)
	{
		// TODO: inverser avec belle petite animation si possible :)
	}
	
	// Retourne l'indice du tableau en fonction d'une position en pixel
	private Point getIndexFromPos(int x, int y)
	{
		int i = (int)((x / (float)getWidth()) * GRID_SIZE);
		int j = (int)((y / (float)getHeight()) * GRID_SIZE);
		return new Point(i, j);
	}
	
	// Retourne la position en pixel dans la vue en fonction de l'indice du tableau
	private Rect getRectFromIndex(int i, int j)
	{
		int itemSize = getWidth() / GRID_SIZE;
		return new Rect(itemSize * i, itemSize * j, itemSize * (i+1), itemSize * (j+1));
	}
	
	
	
	
	
	private class GestureListener extends SimpleOnGestureListener
	{
		private static final int SWIPE_MIN_DISTANCE = 100;
	    private static final int SWIPE_THRESHOLD_VELOCITY = 100;
		
        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY)
        {
        	int srcX = (int)e1.getX();
        	int srcY = (int)e1.getY();
        	int dstX = (int)e2.getX();
        	int dstY = (int)e2.getY();
        	
            if (srcX - dstX > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY)
            {
            	onSwipe(Direction.LEFT, new Point(srcX, srcY));
                return true; // Right to left
            } 
            else if (dstX - srcX > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY)
            {
            	onSwipe(Direction.RIGHT, new Point(srcX, srcY));
                return true; // Left to right
            }

            if (srcY - dstY > SWIPE_MIN_DISTANCE && Math.abs(velocityY) > SWIPE_THRESHOLD_VELOCITY)
            {
            	onSwipe(Direction.UP, new Point(srcX, srcY));
                return true; // Bottom to top
            }
            else if (dstY - srcY > SWIPE_MIN_DISTANCE && Math.abs(velocityY) > SWIPE_THRESHOLD_VELOCITY)
            {
            	onSwipe(Direction.DOWN, new Point(srcX, srcY));
                return true; // Top to bottom
            }
            
            return false;
        }
    }
}
