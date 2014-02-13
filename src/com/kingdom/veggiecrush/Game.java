package com.kingdom.veggiecrush;

import com.kingdom.veggiecrush.R.string;
import com.kingdom.veggiecrush.Settings.GameMode;

import android.os.Bundle;
import android.view.Display;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;

public class Game extends Activity implements OnClickListener
{

	private GameMode mode = null;
	
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
		
		TextView txtCountdown = (TextView) findViewById(R.id.txtCountdown);
		txtCountdown.setText("0");
		
		// Redimensionner la zone de jeu pour être carré et remplir la largeur de l'écran
		Display display = getWindowManager().getDefaultDisplay(); 
		int width = display.getWidth();
		int squareSize = (int)Math.floor((width - 40) / 8.0) * 8; // on veut une taille qui se divise entièrement par 8 :)
		int margin = (width - squareSize) / 2;
		
		GameView gv = (GameView) findViewById(R.id.gameView);
		LayoutParams params = (LayoutParams) gv.getLayoutParams();
		params.width = squareSize;
		params.height = squareSize;
		params.bottomMargin = margin;
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
				//TODO : Refresh the gameplay
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
}
