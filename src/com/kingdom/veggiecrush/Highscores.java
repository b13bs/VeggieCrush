package com.kingdom.veggiecrush;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.app.Activity;
import android.content.Intent;

public class Highscores extends Activity implements OnClickListener {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_highscores);
		
		Button btnPlayAgain = (Button) findViewById(R.id.btnPlayAgain);
		btnPlayAgain.setOnClickListener(this);
		Button btnOtherMode = (Button) findViewById(R.id.btnOtherMode);
		btnOtherMode.setOnClickListener(this);
		Button btnExit = (Button) findViewById(R.id.btnQuit);
		btnExit.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId())
		{
			case R.id.btnPlayAgain:
				Intent intentPlayAgain = new Intent(this, Game.class);
				// TODO: switcher sur le mode de jeu precedent
				//intentPlayAgain.putExtra(Settings.EXTRA_GAME_MODE, Settings.GAME_MODE.TIME_ATTACK);
				break;
			
			case R.id.btnOtherMode:
				Intent intentOtherMode = new Intent(this, Game.class);
				//intentOtherMode.putExtra(Settings.EXTRA_GAME_MODE, Settings.GAME_MODE.BLITZ);
				break;
				
			case R.id.btnQuit:
				Intent intentQuit = new Intent(this, MainMenu.class);
				this.startActivity(intentQuit);
				break;
		}
		
	}

	
	
}
