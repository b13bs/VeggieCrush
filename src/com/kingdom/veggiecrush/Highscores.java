package com.kingdom.veggiecrush;

import java.util.Comparator;
import java.util.Iterator;
import java.util.PriorityQueue;
import org.json.JSONException;
import org.json.JSONObject;

import com.kingdom.veggiecrush.Settings.GameMode;
import com.kingdom.veggiecrush.Settings.Source;
 
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class Highscores extends Activity implements OnClickListener {

	private GameMode modePrecedent = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_highscores);
		
		Source activityPrecedent = (Source) getIntent().getExtras().get(Settings.EXTRA_SOURCE);
				
		if(activityPrecedent == Settings.Source.GAME) {
			Button btnPlayAgain = (Button) findViewById(R.id.btnPlayAgain);
			btnPlayAgain.setOnClickListener(this);
			Button btnOtherMode = (Button) findViewById(R.id.btnOtherMode);
			btnOtherMode.setOnClickListener(this);
			
		} else if(activityPrecedent == Settings.Source.MENU) {
			Button btnPlayAgain = (Button) findViewById(R.id.btnPlayAgain);
			btnPlayAgain.setVisibility(View.INVISIBLE);
			Button btnOtherMode = (Button) findViewById(R.id.btnOtherMode);
			btnOtherMode.setVisibility(View.INVISIBLE);
		}
		
		Button btnExit = (Button) findViewById(R.id.btnQuit);
		btnExit.setOnClickListener(this);
		
		hardCodeSharedPref();
		
		try {
			populateHighScores();
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId())
		{
			case R.id.btnPlayAgain:
				Intent intentPlayAgain = new Intent(this, Game.class);
				modePrecedent = (GameMode) getIntent().getExtras().get(Settings.EXTRA_GAME_MODE);
				String namePlayer = (String) getIntent().getExtras().get(Settings.EXTRA_PLAYER_NAME);
				intentPlayAgain.putExtra(Settings.EXTRA_GAME_MODE, modePrecedent);
				intentPlayAgain.putExtra(Settings.EXTRA_PLAYER_NAME, namePlayer);
				startActivity(intentPlayAgain);
				finish();
				break;
			
			case R.id.btnOtherMode:
				Intent intentOtherMode = new Intent(this, Game.class);
				modePrecedent = (GameMode) getIntent().getExtras().get(Settings.EXTRA_GAME_MODE);
				if (modePrecedent == GameMode.TIME_ATTACK) {
					intentOtherMode.putExtra(Settings.EXTRA_GAME_MODE, Settings.GameMode.BLITZ);
				} else if (modePrecedent == GameMode.BLITZ) {
					intentOtherMode.putExtra(Settings.EXTRA_GAME_MODE, Settings.GameMode.TIME_ATTACK);
				}
				intentOtherMode.putExtra(Settings.EXTRA_PLAYER_NAME, (String) getIntent().getExtras().get(Settings.EXTRA_PLAYER_NAME));
				startActivity(intentOtherMode);
				finish();
				break;
				
			case R.id.btnQuit:
				finish();
				break;
		}
	}
	
	public void hardCodeSharedPref() {
		SharedPreferences prefs = this.getSharedPreferences("com.kingdom.veggiecrush", Context.MODE_PRIVATE);
		Editor editor = prefs.edit();
		editor.putString("highScores","{\"player5\":{\"name\":\"joueurA\",\"score\":\"200\"},\"player4\":{\"name\":\"joueurB\",\"score\":\"400\"},\"player3\":{\"name\":\"joueurC\",\"score\":\"300\"},\"player2\":{\"name\":\"joueurD\",\"score\":\"100\"},\"player1\":{\"name\":\"joueurE\",\"score\":\"5000\"}}");
		editor.commit();
	}
	
	public void populateHighScores() throws JSONException {
		SharedPreferences sharedPref = this.getSharedPreferences("com.kingdom.veggiecrush", Context.MODE_PRIVATE);
		String strJson = sharedPref.getString("highScores", null);
		
		Comparator<PlayerScore> comparator = new PlayerScoreComparator();
        PriorityQueue<PlayerScore> queue = new PriorityQueue<PlayerScore>(5, comparator);
        
		JSONObject jsonData = new JSONObject(strJson);
		
		String[] players = {"player1", "player2", "player3", "player4", "player5"};
		
		for(int i = 0; i < jsonData.length(); i++) {
			PlayerScore ps = new PlayerScore(jsonData.getJSONObject(players[i]).getString("name"), jsonData.getJSONObject(players[i]).getString("score"));
			queue.add(ps);	
		}
		
		Iterator<PlayerScore> it = queue.iterator();
		TextView nameView;
		
		int idNames[] = {R.id.scoresName1, R.id.scoresName2, R.id.scoresName3, R.id.scoresName4, R.id.scoresName5};
		int idScores[] = {R.id.scoresScore1, R.id.scoresScore2, R.id.scoresScore3, R.id.scoresScore4, R.id.scoresScore5};
		
		int cpt = 0;
		//while (it.hasNext()) {
		while(!queue.isEmpty()) {
			//PlayerScore ps = (PlayerScore) it.next();
			PlayerScore ps = queue.poll();
			nameView = (TextView) findViewById(idNames[cpt]);
			nameView.setText(ps.name);
			nameView = (TextView) findViewById(idScores[cpt]);
			nameView.setText(ps.score);
			cpt++;
	    }
	}

	public class PlayerScore {

	    private String name;
	    private String score;

	    public PlayerScore(String name, String score) {
	    	this.name = name; 
		    this.score = score;
	    }
	}
	
	

	public class PlayerScoreComparator implements Comparator<PlayerScore>{
	    @Override
	    public int compare(PlayerScore x, PlayerScore y) {
	    	//int x_int = Integer.parseInt(x.score);
	    	//int y_int = Integer.parseInt(y.score);
	    	return Integer.valueOf(Integer.parseInt(y.score)).compareTo(Integer.parseInt(x.score));
	        /*if (x_int < y_int) {
	            return -1;
	        }
	        if (x_int > y_int) {
	            return 1;
	        }
	        return 0;*/
	    }
	}
	
	
	
}
