package com.cas.utility;

import android.view.View;


public class CardItem {

	public int id;
	public String text;
	public View view;
	public CardItem(int iid, View v){
		this.id=iid;
		this.view = v;
	}
}
