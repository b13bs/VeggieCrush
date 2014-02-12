package com.kingdom.veggiecrush;

import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;

public class MainMenu extends Activity implements OnClickListener, OnCheckedChangeListener
{
	private Switch switchSound;
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main_menu);
		
		Button btnTimeAttack = (Button) findViewById(R.id.btnTimeAttack);
		btnTimeAttack.setOnClickListener(this);
		Button btnBlitz = (Button) findViewById(R.id.btnBlitz);
		btnBlitz.setOnClickListener(this);
		Button btnScores = (Button) findViewById(R.id.btnScores);
		btnScores.setOnClickListener(this);
		Button btnExit = (Button) findViewById(R.id.btnExit);
		btnExit.setOnClickListener(this);
		switchSound = (Switch) findViewById(R.id.switchSound);
		switchSound.setOnCheckedChangeListener(this);
		switchSound.setChecked(Settings.isSoundOn(this));
	}

	@Override
	public void onClick(View v)
	{
		switch (v.getId())
		{
			case R.id.btnTimeAttack:
				Intent intentTimeAttack = new Intent(this, Game.class);
				intentTimeAttack.putExtra(Settings.EXTRA_GAME_MODE, Settings.GAME_MODE.TIME_ATTACK);
				promptPlayerNameDialog(intentTimeAttack);
				break;
			
			case R.id.btnBlitz:
				Intent intentBlitz = new Intent(this, Game.class);
				intentBlitz.putExtra(Settings.EXTRA_GAME_MODE, Settings.GAME_MODE.BLITZ);
				promptPlayerNameDialog(intentBlitz);
				break;
				
			case R.id.btnScores:
				Intent intentScores = new Intent(this, Highscores.class);
				this.startActivity(intentScores);
				break;
				
			case R.id.btnExit:
				finish();
				break;
		}
	}
	
	
	@Override
	public void onCheckedChanged(CompoundButton arg0, boolean isOn)
	{
		if (isOn)
		{
			Settings.setSoundOn(getApplicationContext(), true);
		}
		else
		{
			Settings.setSoundOn(getApplicationContext(), false);
		}
	}
	
	
	// Montre un dialogue pour entrer le nom du joueur, sur confirmation on part l'intent, sur cancel on annule
	private void promptPlayerNameDialog(final Intent targetIntent)
	{
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		final EditText input = new EditText(this);
		input.setInputType(InputType.TYPE_CLASS_TEXT);
	    builder.setTitle("Enter player name")
	           .setView(input)
	           .setPositiveButton("OK", new DialogInterface.OnClickListener() { 
				    @Override
				    public void onClick(DialogInterface dialog, int which) {
				    	String playerName = input.getText().toString();
				    	if (playerName.length() < 2)
				    	{
				    		Toast.makeText(getApplicationContext(), "Please enter a minimum of 2 caracters", Toast.LENGTH_SHORT).show();
				    	}
				    	else
				    	{
				    		targetIntent.putExtra(Settings.EXTRA_PLAYER_NAME, playerName);
				    		startActivity(targetIntent);
				    	}
				    	dialog.dismiss();
				    }
				});
				builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
				    @Override
				    public void onClick(DialogInterface dialog, int which) {
				        dialog.cancel();
				    }
				});
	    final Dialog d = builder.create();
	    d.show();
	}

}
