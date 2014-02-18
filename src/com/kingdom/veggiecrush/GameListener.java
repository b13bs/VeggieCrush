package com.kingdom.veggiecrush;

public interface GameListener {

	public void onMove(); //d�placement d'un l�gume
	
	public void onCrush(int nbItemsCrushed); //crush des l�gumes, pour le score
	
	public void onChaine(); //une chaine r�alis�e
	
}
