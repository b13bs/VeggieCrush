package com.kingdom.veggiecrush;

import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class MainMenu extends Activity implements OnClickListener, OnCheckedChangeListener
{
	private Switch switchSound;
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main_menu);
		
		// On charge tous les bitmaps pour l'application
		if (!Settings.bitmapsLoaded)
		{
			Settings.loadBitmaps(this);
		}
		
		// On enregistre tous les contr�les au listener de l'activit�
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
		
		// On v�rifie l'�tat du son dans les param�tres sauvegard�s et ajuste la switch en cons�quence
		switchSound.setChecked(Settings.isSoundOn(this));

		// Si les scores ne sont pas initialis�s, on le fait
		if (!alreadyInitHighscores())
		{
			initHighscores();
		}
	}


	// Appel� lors d'un clic sur un composant enregistr�
	@Override
	public void onClick(View v)
	{
		switch (v.getId())
		{
			case R.id.btnTimeAttack:
				// On demande le nom de joueur et une confirmation
				// on passe en argument l'intent de l'activit� qu'on d�sire d�marrer
				Intent intentTimeAttack = new Intent(this, Game.class);
				intentTimeAttack.putExtra(Settings.EXTRA_GAME_MODE, Settings.GameMode.TIME_ATTACK);
				promptPlayerNameDialog(intentTimeAttack);
				break;
			
			case R.id.btnBlitz:
				// On demande le nom de joueur et une confirmation
				// on passe en argument l'intent de l'activit� qu'on d�sire d�marrer
				Intent intentBlitz = new Intent(this, Game.class);
				intentBlitz.putExtra(Settings.EXTRA_GAME_MODE, Settings.GameMode.BLITZ);
				promptPlayerNameDialog(intentBlitz);
				break;
				
			case R.id.btnScores:
				// On d�marrte l'activit� des scores
				Intent intentScores = new Intent(this, Highscores.class);
				intentScores.putExtra(Settings.EXTRA_SOURCE, Settings.Source.MENU);
				this.startActivity(intentScores);
				break;
				
			case R.id.btnExit:
				// On quitte l'application
				finish();
				break;
		}
	}
	
	
	// Appel� lorsqu'un 'toggle' enregistr� est modifi�
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
	
	
	// Montre un dialogue pour entrer le nom du joueur, sur confirmation on part l'intent recu en argument, sur cancel on annule
	private void promptPlayerNameDialog(final Intent targetIntent)
	{
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		final EditText input = new EditText(this);
		input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_WORDS);
	    builder.setTitle("Enter player name")
	           .setView(input)
	           .setPositiveButton("OK", new DialogInterface.OnClickListener() { 
				    @Override
				    public void onClick(DialogInterface dialog, int which) {
				    	// On lis le nom du joueur
				    	String playerName = input.getText().toString();
				    	
				    	// On v�rifie s'il correspond � une longueur minimale
				    	if (playerName.length() < 2)
				    	{
				    		InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
				    		imm.hideSoftInputFromWindow(input.getWindowToken(), 0);
				    		Toast.makeText(getApplicationContext(), "Please enter a minimum of 2 caracters", Toast.LENGTH_SHORT).show();
				    	}
				    	else
				    	{
				    		// Tout est OK: on d�marre l'activit� et cache le clavier
				    		targetIntent.putExtra(Settings.EXTRA_PLAYER_NAME, playerName);
				    		startActivity(targetIntent);
				    		
				    		InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
				    		imm.hideSoftInputFromWindow(input.getWindowToken(), 0);
				    	}
				    	dialog.dismiss();
				    }
				});
				builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
				    @Override
				    public void onClick(DialogInterface dialog, int which) {
				    	// On annule
				        dialog.cancel();
				    }
				});
	    final Dialog d = builder.create();
	    d.show();
	}
	
	// Initialise les meilleurs scores
	public void initHighscores() {
		SharedPreferences prefs = this.getSharedPreferences("com.kingdom.veggiecrush", Context.MODE_PRIVATE);
		Editor editor = prefs.edit();
		editor.putString("player1_name", "empty_entry_1");
		editor.putString("player1_score", "-1");
		editor.putString("player2_name", "empty_entry_2");
		editor.putString("player2_score", "-2");
		editor.putString("player3_name", "empty_entry_3");
		editor.putString("player3_score", "-3");
		editor.putString("player4_name", "empty_entry_4");
		editor.putString("player4_score", "-4");
		editor.putString("player5_name", "empty_entry_5");
		editor.putString("player5_score", "-5");
		editor.commit();
	}
	
	// Fonction qui v�rifie si les meilleurs scores sont vides ou pas
	public boolean alreadyInitHighscores() {
		SharedPreferences sharedPref = this.getSharedPreferences("com.kingdom.veggiecrush", Context.MODE_PRIVATE);
    	String name = sharedPref.getString("player1_name", null);
    	String score = sharedPref.getString("player1_score", null);
    	if((name == null && score == null) || (name == "empty_entry_1" && score == "-1")) {
    		return false;
    	} else {
    		return true;
    	}		
	}

}
