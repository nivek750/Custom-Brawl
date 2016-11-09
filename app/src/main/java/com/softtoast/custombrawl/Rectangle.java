package com.softtoast.custombrawl;

public class Rectangle {
	public int xPos;
	public int yPos;
	public int width;
	public int height;
	
	Rectangle(int i, int j){
		xPos = i;
		yPos = j;
		width = 32;
		height = 64;
	}
	
	public void SetPosition(int i, int j){
		xPos = i;
		yPos = j;
	}
}
