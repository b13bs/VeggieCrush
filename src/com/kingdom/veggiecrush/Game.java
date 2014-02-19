package com.kingdom.veggiecrush;

import com.kingdom.veggiecrush.R.string;
import com.kingdom.veggiecrush.Settings.GameMode;

import android.os.Bundle;
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
	
	private TextView txtScore;
	private TextView txtDeplacement;
	private TextView txtChaines;
	
	private int score = 0;
	private int deplacements = 0;
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
		if(mode == GameMode.TIME_ATTACK)
			txtMode.setText(string.time_Attack);
		else
			txtMode.setText(string.blitz);
		
		txtDeplacement = (TextView) findViewById(R.id.txtDeplacement);		
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
	public void onClick(View v)
	{
		switch (v.getId())
		{
			case R.id.btnExitGame:
				promptExit();
				break;
			case R.id.btnNewGame:
				//TODO: confirmation?
				resetGame();
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
		score = 0;
		deplacements = 0;
		chaines = 0;
		txtScore.setText("0");
		txtDeplacement.setText("0");
		txtChaines.setText("0");
	}
	
	@Override
	public void onMove()
	{
		txtDeplacement.setText("" + (++deplacements));
	}

	@Override
	public void onCrush(int nbItemsCrushed)
	{
		// TODO: determiner un score en fonction du nb d'items crushed
	}

	@Override
	public void onChaine()
	{
		txtChaines.setText("" + (++chaines));
	}
}
