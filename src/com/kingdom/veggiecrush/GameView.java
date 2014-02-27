package com.kingdom.veggiecrush;

import java.util.ArrayList;

import com.kingdom.veggiecrush.VeggieGrid.Direction;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.GestureDetector.SimpleOnGestureListener;

public class GameView extends SurfaceView implements SurfaceHolder.Callback
{
	public static final int REFRESH_RATE_MS = 16;
	
	private ArrayList<MoveListener> listeners = new ArrayList<MoveListener>();
	
	private GameViewThread refreshThread;
	private GestureDetector gestDetector;
	private Paint paint;
	
	private boolean actionEnCours = false;
	
	private VeggieGrid vaggieGrid;
	
	public GameView(Context context, AttributeSet attrs)
	{
		super(context, attrs);
		
		gestDetector = new GestureDetector(context, new GestureListener());
		paint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG);
		
		getHolder().addCallback(this);
	}
	
	public void setGameGrid(VeggieGrid g)
	{
		vaggieGrid = g;
	}
	
	public void addMoveListener(MoveListener l)
	{
		listeners.add(l);
	}
	
	public void removeMoveListener(MoveListener l)
	{
		listeners.remove(l);
	}
	
	@Override
	public void surfaceChanged(SurfaceHolder arg0, int arg1, int arg2, int arg3)
	{
		// On ne s'en occupe pas
	}

	@Override
	public void surfaceCreated(SurfaceHolder arg0)
	{
		// On crée et enregistre le thread pour rafraichir la surface de jeu
		refreshThread = new GameViewThread(this, REFRESH_RATE_MS);
		refreshThread.setRunning(true);
		refreshThread.start();
	}
	
	@Override
	public void surfaceDestroyed(SurfaceHolder arg0)
	{
		// On arrête le thread de rafraichissement
		boolean success = false;
		refreshThread.setRunning(false);
		while (!success)
		{
			try
			{
				refreshThread.join();
				success = true;
			}
			catch (InterruptedException e)
			{
				e.printStackTrace();
			}
		}
	}
	
	
	// Fonction dans laquelle on effectue l'affichage de la zone de jeu
	@Override 
    public void onDraw(Canvas canvas)
	{
		if (canvas != null)
		{
			// On dessine l'arrière plan
			canvas.drawColor(Color.rgb(46, 97, 0));
			
			// On affiche la grille avec les légumes
			vaggieGrid.drawVeggiesToCanvas(canvas, paint);
		}
    }
	
	
	// Fonction qui est appelée lorsque l'utilisateur touche l'écran
	@Override
	public boolean onTouchEvent(MotionEvent e)
	{
		// On redirige l'événement vers le détecteur de gestes
		if (!actionEnCours)
		{
			gestDetector.onTouchEvent(e);
		}
		return true;
	}
	

	// Classe privée dont on se sert pour détecter les gestes de l'utilisateur
	private class GestureListener extends SimpleOnGestureListener
	{
	    private static final int SWIPE_MIN_VELOCITY = 70;
		
	    // 'fling' est un 'swipe'
        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY)
        {
        	final int SWIPE_MIN_DISTANCE = (int)(0.9 * vaggieGrid.getLargeurCase());
        	
        	int srcX = (int)e1.getX();
        	int srcY = (int)e1.getY();
        	int dstX = (int)e2.getX();
        	int dstY = (int)e2.getY();
        	
        	// En fonction de la position de départ et d'arrivé, on détermine la direction du 'swipe'
        	// En fonction de la distance et de la vélocité, on détermine si le 'swipe' est accepté
            if (srcX - dstX > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_MIN_VELOCITY)
            {
            	actionEnCours = true;
            	for (MoveListener l : listeners)
            	{
            		l.onSwipe(Direction.LEFT, new Point(srcX, srcY));
            	}
            	actionEnCours = false;
                return true;
            } 
            else if (dstX - srcX > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_MIN_VELOCITY)
            {
            	actionEnCours = true;
            	for (MoveListener l : listeners)
            	{
            		l.onSwipe(Direction.RIGHT, new Point(srcX, srcY));
            	}
            	actionEnCours = false;
                return true;
            }

            if (srcY - dstY > SWIPE_MIN_DISTANCE && Math.abs(velocityY) > SWIPE_MIN_VELOCITY)
            {
            	actionEnCours = true;
            	for (MoveListener l : listeners)
            	{
            		l.onSwipe(Direction.UP, new Point(srcX, srcY));
            	}
            	actionEnCours = false;
                return true;
            }
            else if (dstY - srcY > SWIPE_MIN_DISTANCE && Math.abs(velocityY) > SWIPE_MIN_VELOCITY)
            {
            	actionEnCours = true;
            	for (MoveListener l : listeners)
            	{
            		l.onSwipe(Direction.DOWN, new Point(srcX, srcY));
            	}
            	actionEnCours = false;
                return true;
            }
            
            return false;
        }
    }
	
	
}
