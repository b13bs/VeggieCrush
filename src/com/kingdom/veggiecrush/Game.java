package com.kingdom.veggiecrush;

import com.kingdom.veggiecrush.VeggieGrid.Direction;
import com.kingdom.veggiecrush.R.string;
import com.kingdom.veggiecrush.Settings.GameMode;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.Display;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Point;

public class Game extends Activity implements OnClickListener, MoveListener
{
	private GameMode mode = null;
	
	private GameTimer timer = null;
	private boolean timerPaused = false;
	private boolean gameOver = false;
	
	private TextView txtScore;
	private TextView txtRestant;
	private TextView txtChaines;
	
	private int score = 0;
	private int restant = 0;
	private int chaines = 0;
	
	private VeggieGrid veggieGrid;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_game);
		
		// On enregistre les listener pour bouton quitter et nouvelle partie
		Button btnExitGame = (Button) findViewById(R.id.btnExitGame);
		btnExitGame.setOnClickListener(this);
		Button btnNewGame = (Button) findViewById(R.id.btnNewGame);
		btnNewGame.setOnClickListener(this);
		
		// On v�rifie de quel mode de jeu il s'agit
		mode = (GameMode) getIntent().getExtras().get(Settings.EXTRA_GAME_MODE);
		String name = (String) getIntent().getExtras().get(Settings.EXTRA_PLAYER_NAME);
		
		// On affiche le bon texte en cons�quences
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
			txtTexteRestant.setText("D�plac. restants");
		}
		
		// On trouve les zones de texte
		txtRestant = (TextView) findViewById(R.id.txtRestant);		
		txtScore = (TextView) findViewById(R.id.txtScore);		
		txtChaines = (TextView) findViewById(R.id.txtChaines);
		
		// Redimensionner la zone de jeu pour �tre carr� et remplir la largeur de l'�cran
		Display display = getWindowManager().getDefaultDisplay(); 
		int width = display.getWidth();
		int gameSize = (int)Math.floor((width - 40) / 8.0) * 8; // on veut une taille qui se divise enti�rement par 8 :)
		int gameMargin = (width - gameSize) / 2;
		
		GameView gv = (GameView) findViewById(R.id.gameView);
		LayoutParams params = (LayoutParams) gv.getLayoutParams();
		params.width = gameSize;
		params.height = gameSize;
		params.bottomMargin = gameMargin;
		gv.addMoveListener(this);
		
		// On cr�e la grille de l�gumes
		veggieGrid = new VeggieGrid(8, 8, gameSize, gameSize);
		
		// On l'assigne au GameView
		gv.setGameGrid(veggieGrid);
		
		// On reset le tout!
		resetGame();
	}
	
	
	@Override
	public void onPause()
	{
		super.onPause();
		if (timer != null)
		{
			timer.cancel();
			timerPaused = true;
		}
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
		promptExit();
	}
	
	
	// Affiche un dialogue de confirmation pour fermer le jeu
	private void promptExit()
	{
		// Pause le timer!
		if (timer != null)
		{
			timer.cancel();
			timerPaused = true;
		}
		
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
				// On red�marre le timer
			    if (mode == GameMode.TIME_ATTACK && timerPaused)
			    {
			    	timer = new GameTimer(restant * 1000, 1000);
			    	timer.start();
			    	timerPaused = false;
			    }
				dialog.cancel();
			}
		});
	    builder.show();	
	}
	
	
	// Affiche un dialogue de confirmation pour remettre � z�ro la partie
	private void promptReset()
	{
		// Pause le timer!
		if (timer != null)
		{
			timer.cancel();
			timerPaused = true;
		}
		
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
				// On red�marre le timer
			    if (mode == GameMode.TIME_ATTACK && timerPaused)
			    {
			    	timer = new GameTimer(restant * 1000, 1000);
			    	timer.start();
			    	timerPaused = false;
			    }
				dialog.cancel();
			}
		});
	    builder.show();	
	}
	
	
	// Remet � z�ro la partie courante
	private void resetGame()
	{
		// On reset la grille de jeu
		veggieGrid.resetVeggieGrid(getApplicationContext());

		// On reset les zones de textes et les stats
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
			restant = 60;
			timer = new GameTimer(restant * 1000, 1000);
			timer.start();
		}
		score = 0;
		chaines = 0;
		txtScore.setText("" + score);
		txtRestant.setText("" + restant);
		txtChaines.setText("" + chaines);
	}
	
	
	// Affiche un dialogue indiquant que la partie est termin�e et redirige vers l'�cran des scores
	private void gameOver()
	{
		gameOver = true;
		
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("Game over!").setMessage("Your score is: " + score);
		builder.setCancelable(false);
		builder.setPositiveButton("Okay", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				Intent intentScores = new Intent(getApplicationContext(), Highscores.class);
				//intentScores.putExtra(Settings.EXTRA_GAME_MODE, Settings.GameMode.TIME_ATTACK);
				intentScores.putExtra(Settings.EXTRA_GAME_MODE, mode);
				intentScores.putExtra(Settings.EXTRA_PLAYER_NAME, (String) getIntent().getExtras().get(Settings.EXTRA_PLAYER_NAME));
				intentScores.putExtra(Settings.EXTRA_SOURCE, Settings.Source.GAME);
				startActivity(intentScores);
				finish();
			}
		});
	    builder.show();
	}
	
	
	@Override
	public void onSwipe(Direction d, Point p)
	{
		if (gameOver)
		{
			return;
		}
		
		// On transforme la position source en pixels en une position de la grille
		Point index = veggieGrid.getIndexFromPos(p.x, p.y);
		
		// On v�rifie que le mouvement est valide (pour les bordures)
		if ( (index.x == 0                        && d == Direction.LEFT)  ||
		     (index.x == veggieGrid.nbColumns - 1 && d == Direction.RIGHT) ||
		     (index.y == 0                        && d == Direction.UP)    ||
		     (index.y == veggieGrid.nbRows - 1    && d == Direction.DOWN)  )
		{
			//d�placement invalide!
			return;
		}
		
		// On d�termine quelle autre case est impliqu�e
		Point index2;
		switch (d)
		{
			case DOWN:
				index2 = new Point(index.x, index.y + 1);
				break;
			case LEFT:
				index2 = new Point(index.x - 1, index.y);
				break;
			case RIGHT:
				index2 = new Point(index.x + 1, index.y);
				break;
			case UP:
				index2 = new Point(index.x, index.y - 1);
				break;
			default:
				return;
		}
		
		// On v�rifie si le d�placement est valide (au moins 3 en ligne)
		// TODO: !!!
		if (true)
		{
			// On change de place
			veggieGrid.switchPlace(index, index2);
			
			// On d�cr�mente le compteur de d�placements
			if (mode == GameMode.BLITZ)
			{
				txtRestant.setText("" + (--restant));
				if (restant <= 0)
				{
					// Partie termin�e :(
					gameOver();
				}
			}
			
			// TODO: badaboom crusher toute
		}
	}
	
	
	// Fonction qui augmente le score selon le nombre d'items combin�s
	public void onCrush(int nbItemsCrushed)
	{
		// 100 points pour chaine de 3 + 50 points par pierre suppl�mentaire
		score += 100;
		if (nbItemsCrushed > 3)
		{
			score += 50 * (nbItemsCrushed - 3);
		}
		txtScore.setText("" + score);
	}

	// Fontion qui incr�mente le nombre de chaines r�alis�es
	public void onChaine()
	{
		txtChaines.setText("" + (++chaines));
	}	
	
	
	
	
	private class GameTimer extends CountDownTimer
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
			// Partie termin�e :(
			gameOver();
        }

        @Override
        public void onTick(long millisUntilFinished) {
        	restant -= intervalSec;
        	txtRestant.setText("" + restant);
		}
    }
}
