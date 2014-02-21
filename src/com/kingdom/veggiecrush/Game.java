package com.kingdom.veggiecrush;

import com.kingdom.veggiecrush.R.string;
import com.kingdom.veggiecrush.Settings.GameMode;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.Display;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;

public class Game extends Activity implements OnClickListener, GameListener
{

	private GameMode mode = null;
	
	private GameTimer timer = null;
	private boolean timerPaused = false;
	
	private TextView txtScore;
	private TextView txtRestant;
	private TextView txtChaines;
	
	private int score = 0;
	private int restant = 0;
	private int chaines = 0;
	
	private int gameSize = 0;
	private int gameMargin = 0;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_game);
		
		Button btnExitGame = (Button) findViewById(R.id.btnExitGame);
		btnExitGame.setOnClickListener(this);
		Button btnNewGame = (Button) findViewById(R.id.btnNewGame);
		btnNewGame.setOnClickListener(this);
		
		mode = (GameMode) getIntent().getExtras().get(Settings.EXTRA_GAME_MODE);
		
		TextView txtMode = (TextView) findViewById(R.id.TxtMode);
		TextView txtTexteRestant = (TextView) findViewById(R.id.txtTexteRestant);
		if(mode == GameMode.TIME_ATTACK)
		{
			txtMode.setText(string.time_Attack);
			txtTexteRestant.setText("Sec. restantes");
		}
		else
		{
			txtMode.setText(string.blitz);
			txtTexteRestant.setText("Déplac. restants");
		}
		
		txtRestant = (TextView) findViewById(R.id.txtRestant);		
		txtScore = (TextView) findViewById(R.id.txtScore);		
		txtChaines = (TextView) findViewById(R.id.txtChaines);
		resetStats();
		
		// Redimensionner la zone de jeu pour être carré et remplir la largeur de l'écran
		Display display = getWindowManager().getDefaultDisplay(); 
		int width = display.getWidth();
		gameSize = (int)Math.floor((width - 40) / 8.0) * 8; // on veut une taille qui se divise entièrement par 8 :)
		gameMargin = (width - gameSize) / 2;
		
		GameView gv = (GameView) findViewById(R.id.gameView);
		LayoutParams params = (LayoutParams) gv.getLayoutParams();
		params.width = gameSize;
		params.height = gameSize;
		params.bottomMargin = gameMargin;
		gv.addGameListener(this);
	}
	
	@Override
	public void onPause()
	{
		super.onPause();
		timer.cancel();
		timerPaused = true;
	}
	
	public void onResume()
	{
	    super.onResume();
	    if (mode == GameMode.TIME_ATTACK && timerPaused)
	    {
	    	timer = new GameTimer(restant * 1000, 1000);
	    	timer.start();
	    	timerPaused = false;
	    }
	}
	
	@Override
	public void onClick(View v)
	{
		switch (v.getId())
		{
			case R.id.btnExitGame:
				promptExit();
				break;
			case R.id.btnNewGame:
				promptReset();
				break;
		}
	}
	
	@Override
	public void onBackPressed()
	{
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("Exit the game").setMessage("Do you really want to quit the game?");
		builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				finish();
			}
		});
		builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.cancel();
			}
		});
	    builder.show();	
	}
	
	private void promptExit()
	{
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("Exit the game").setMessage("Do you really want to quit the game?");
		builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				Game.this.finish();
			}
		});
		builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.cancel();
			}
		});
	    builder.show();	
	}
	
	private void promptReset()
	{
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("Reset the game").setMessage("Do you really want to reset the current game?");
		builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				resetGame();
			}
		});
		builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.cancel();
			}
		});
	    builder.show();	
	}
	
	private void resetGame()
	{
		GameView gvToDelete = (GameView) findViewById(R.id.gameView);
		gvToDelete.removeGameListener(this);
		
		int id = gvToDelete.getId();
		GameView newGv = new GameView(getApplicationContext(), null);
		newGv.setId(id);
		RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(gameSize, gameSize);
		params.bottomMargin = gameMargin;
		params.leftMargin = gameMargin;
		params.rightMargin = gameMargin;
		params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
		params.addRule(RelativeLayout.CENTER_HORIZONTAL);
		newGv.setLayoutParams(params);
		
		RelativeLayout gameLayout = (RelativeLayout) findViewById(R.id.gameRelativeLayout);
		gameLayout.removeView( gvToDelete );
		gameLayout.addView(newGv);
		
		newGv.addGameListener(this);
		
		resetStats();
	}
	
	private void resetStats()
	{
		if (mode == GameMode.BLITZ)
		{
			restant = 10;
		}
		else
		{
			if (timer != null)
			{
				timer.cancel();
			}
			restant = 15;
			timer = new GameTimer(restant * 1000, 1000);
			timer.start();
		}
		score = 0;
		chaines = 0;
		txtScore.setText("" + score);
		txtRestant.setText("" + restant);
		txtChaines.setText("" + chaines);
	}
	
	@Override
	public void onMove()
	{
		if (mode == GameMode.BLITZ)
		{
			txtRestant.setText("" + (--restant));
			if (restant <= 0)
			{
				// TODO: partie terminée
			}
		}
	}

	@Override
	public void onCrush(int nbItemsCrushed)
	{
		// 100 points pour chaine de 3 + 50 points par pierre supplémentaire
		score += 100;
		if (nbItemsCrushed > 3)
		{
			score += 50 * (nbItemsCrushed - 3);
		}
		txtScore.setText("" + score);
	}

	@Override
	public void onChaine()
	{
		txtChaines.setText("" + (++chaines));
	}
	
	
	
	public class GameTimer extends CountDownTimer
    {
		int intervalSec;
		
        public GameTimer(long millisInFuture, long countDownInterval) {
			super(millisInFuture, countDownInterval);
			intervalSec = (int)(countDownInterval / 1000);
		}

        @Override
        public void onFinish() {
        	restant = 0;
			txtRestant.setText("0");
			// TODO: partie terminée
        }

        @Override
        public void onTick(long millisUntilFinished) {
        	restant -= intervalSec;
        	txtRestant.setText("" + restant);
		}
    }
}
