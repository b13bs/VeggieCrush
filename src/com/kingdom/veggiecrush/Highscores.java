package com.kingdom.veggiecrush;

import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.kingdom.veggiecrush.Settings.GameMode;
import com.kingdom.veggiecrush.Settings.Source;

public class Highscores extends Activity implements OnClickListener {

	private GameMode modePrecedent = null;
	
	@SuppressWarnings("unused")
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
			
			// LOG HIGHSCORES
			Integer scoreGamePrec = Integer.parseInt((String) getIntent().getExtras().get(Settings.EXTRA_PLAYER_SCORE));
			String playerName = (String) getIntent().getExtras().get(Settings.EXTRA_PLAYER_NAME);
			
			SharedPreferences sharedPref = this.getSharedPreferences("com.kingdom.veggiecrush", Context.MODE_PRIVATE);
			
			Map<String, Integer> map = new TreeMap<String, Integer>();
			
	        for(Integer i = 1; i <= 5; i++) {
	        	String name = sharedPref.getString("player" + i.toString() + "_name", null);
	        	Integer score = Integer.parseInt(sharedPref.getString("player" + i.toString() + "_score", null));
	        	map.put(name, score);
	        }	        
		    
	       
	        boolean newName = false;
	        if(map.containsKey(playerName)) {
	        	if(map.get(playerName) < scoreGamePrec) {
	        		map.remove(playerName);
	        		map.put(playerName, scoreGamePrec);
	        	}
	        } else {
	        	newName = true;
	        	map.put(playerName, scoreGamePrec);
	        }
	        
	        Map<String, Integer> sortedMap = sortByValue(map);
	        
	        if(newName) {
	        	Iterator<Map.Entry<String, Integer>> iterator = sortedMap.entrySet().iterator();
	            Map.Entry<String, Integer> lastElement = null;
	            while (iterator.hasNext()) {
	                lastElement = iterator.next();
	            }
	        	sortedMap.remove(lastElement.getKey());
	        }
	        
	        SharedPreferences prefs = this.getSharedPreferences("com.kingdom.veggiecrush", Context.MODE_PRIVATE);
			Editor editor = prefs.edit();
	        
			Integer i = 1;
	        Iterator<Map.Entry<String, Integer>> iterator = sortedMap.entrySet().iterator();
	        Map.Entry<String, Integer> element = null;
	        while (iterator.hasNext()) {
	        	element = iterator.next();
	        	editor.putString("player" + i.toString() + "_name", element.getKey());
	        	editor.putString("player" + i.toString() + "_score", element.getValue().toString());
                i += 1;
            }
	        editor.commit();
			
		} else if(activityPrecedent == Settings.Source.MENU) {
			Button btnPlayAgain = (Button) findViewById(R.id.btnPlayAgain);
			btnPlayAgain.setVisibility(View.INVISIBLE);
			Button btnOtherMode = (Button) findViewById(R.id.btnOtherMode);
			btnOtherMode.setVisibility(View.INVISIBLE);
		}
		
		Button btnExit = (Button) findViewById(R.id.btnQuit);
		btnExit.setOnClickListener(this);
		
		populateHighScores();
	}
	
	@SuppressWarnings("all")
	static Map sortByValue(Map map) {
	     List list = new LinkedList(map.entrySet());
	     Collections.sort(list, new Comparator() {
	          public int compare(Object o1, Object o2) {
	               return ((Comparable) ((Map.Entry) (o2)).getValue())
	              .compareTo(((Map.Entry) (o1)).getValue());
	          }
	     });

	    Map result = new LinkedHashMap();
	    for (Iterator it = list.iterator(); it.hasNext();) {
	        Map.Entry entry = (Map.Entry)it.next();
	        result.put(entry.getKey(), entry.getValue());
	    }
	    return result;
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
	
	
	
	
	
	
	
	public void populateHighScores() {    
		SharedPreferences sharedPref = this.getSharedPreferences("com.kingdom.veggiecrush", Context.MODE_PRIVATE);
		
		Map<String, Integer> map = new TreeMap<String, Integer>();
        for(Integer i = 1; i <= 5; i++) {
        	String name = sharedPref.getString("player" + i.toString() + "_name", null);
        	Integer score = Integer.parseInt(sharedPref.getString("player" + i.toString() + "_score", null));
        	map.put(name, score);
        }	
        
        int idNames[] = {R.id.scoresName1, R.id.scoresName2, R.id.scoresName3, R.id.scoresName4, R.id.scoresName5};
		int idScores[] = {R.id.scoresScore1, R.id.scoresScore2, R.id.scoresScore3, R.id.scoresScore4, R.id.scoresScore5};
		
		TextView nameView;
		
		Map<String, Integer> sortedMap = sortByValue(map);
		
		Iterator<Map.Entry<String, Integer>> iterator = sortedMap.entrySet().iterator();
		Map.Entry<String, Integer> element = null;
		int cpt = 0;
		while (iterator.hasNext()) {
			element = iterator.next();
			
			nameView = (TextView) findViewById(idNames[cpt]);
			String abc = element.getKey().substring(0,element.getKey().length()-2);
			if(abc.equals("empty_entry")) {
				nameView.setText("");
			} else {
				nameView.setText(element.getKey());
			}
			
			nameView = (TextView) findViewById(idScores[cpt]);
			if(element.getValue() < 0) {
				nameView.setText("");
			} else {
				nameView.setText(element.getValue().toString());
			}
			
			++cpt;
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
	    	return Integer.valueOf(Integer.parseInt(y.score)).compareTo(Integer.parseInt(x.score));
	    }
	}
	
}
