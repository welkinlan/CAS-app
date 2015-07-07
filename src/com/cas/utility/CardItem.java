/*
 * The model class for a card in the memory game
 */
package com.cas.utility;

import android.view.View;


// TODO: Auto-generated Javadoc
/**
 * The Class CardItem for memory game.
 */
public class CardItem {

	/** The id. */
	public int id;
	
	/** The text. */
	public String text;
	
	/** The view. */
	public View view;
	
	/**
	 * Instantiates a new card item.
	 *
	 * @param iid the id
	 * @param v the view
	 */
	public CardItem(int iid, View v){
		this.id=iid;
		this.view = v;
	}
}
