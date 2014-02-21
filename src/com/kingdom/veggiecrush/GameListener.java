package com.kingdom.veggiecrush;

public interface GameListener {

	public void onMove(); //déplacement d'un légume
	
	public void onCrush(int nbItemsCrushed); //crush des légumes, pour le score
	
	public void onChaine(); //une chaine réalisée
	
}
