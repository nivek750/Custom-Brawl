package com.softtoast.custombrawl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.BitmapFactory.Options;
import android.os.Environment;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class SurfacePanel extends SurfaceView implements SurfaceHolder.Callback{
	Bitmap mbitmap;
	Bitmap mbitmap2;
	Bitmap healthBar;
	Bitmap arcadeControlBM;
	Bitmap blooPicBM;
	Bitmap redPicBM;
	Bitmap modPicBM;
	Bitmap readyBM;
	Bitmap [][] fighter1Bitmaps = new Bitmap[6][2];
	Bitmap [][] fighter2Bitmaps = new Bitmap[6][2];	
	Bitmap [] healthBitmaps = new Bitmap[6];
	
	Bitmap leftB;
	Bitmap rightB;
	Bitmap attack1B;
	Bitmap attack2B;
	Bitmap blockB;
	Bitmap dodgeB;
	
	Bitmap modBM;
	
	Context context2;
	MyThread mythread;
	SurfaceHolder thisHolder;
	//save the bitmapPositions in an array [x][y]
	int[][] bitmapPositions = new int[2][2];
	
	int clipSize = 0;
	int effectSize = 0;
	
	int bgColour;
	
	int chosenChar = -1;
	
	float scaleMultiplier = 2;
	
	Canvas mcanvas;
	
	boolean isTouch = false;
	boolean isTouchNPC = false;
	boolean modAvailable = false;
	int touchType = 0;
	
	String gamestate = "start";
	
	public SurfacePanel(Context context, AttributeSet attrSet){
		super(context, attrSet);
		
		//hdpi * 2
		//xhdpi * 4
		//set clip by density
	    float checkDensity = getResources().getDisplayMetrics().density;
	      
	    if(checkDensity == 0.75){//ldpi
	    	scaleMultiplier = 1;
	    }
	    else if(checkDensity == 1.0){//mdpi
	    	scaleMultiplier = 1.5f;
	    }
	    else if(checkDensity == 1.5){//hdpi
	    	scaleMultiplier = 2;
	    }
	    else if(checkDensity == 2.0){//xhdpi
	    	scaleMultiplier = 3;
	    }
	    else if(checkDensity == 3.0){//xxhdpi
	    	scaleMultiplier = 5;
	    }
		
		thisHolder = getHolder();
		thisHolder.addCallback(this);
		
		context2 = context;		
		
		bgColour = Color.rgb(100,0,10);
		
		try {
			setImages();
		} catch (IOException e) {
			e.printStackTrace();
		}		
	}	
	
	@Override
	public boolean onTouchEvent(MotionEvent event){
		int X = (int) event.getX();
		int Y = (int) event.getY();
		int eventaction = event.getAction();
		
		
		switch(eventaction){
		case MotionEvent.ACTION_DOWN:
			//Toast.makeText(context2, "ACTION_DOWN AT COORDS "+"X: "+X+"Y: "+Y,
				//	Toast.LENGTH_SHORT).show();
			
			if(chosenChar > -1 && X > getWidth() - 60*scaleMultiplier && X < getWidth() && Y > getHeight() - 30*scaleMultiplier && 
					Y < getHeight()){
				touchType = 100;
			}
			else if(gamestate == "choose" && X > 20*scaleMultiplier && X < (20+23)*scaleMultiplier && Y > 20*scaleMultiplier && Y < (20+25)*scaleMultiplier){
				chosenChar = 1;
			}
			else if(gamestate == "choose" && X > 20*scaleMultiplier && X < (20+23)*scaleMultiplier && Y > 50*scaleMultiplier && Y < (50+25)*scaleMultiplier){
				chosenChar = 2;
			}
			else if(gamestate == "choose" && X > 20*scaleMultiplier && X < (20+23)*scaleMultiplier && Y > 80*scaleMultiplier && Y < (80+25)*scaleMultiplier &&
			modAvailable == true){
				chosenChar = 3;
			}			
			else if(gamestate == "title" && X > 20*scaleMultiplier && X < (20+28)*scaleMultiplier && Y > 60*scaleMultiplier && Y < (60+22)*scaleMultiplier){
				touchType = 10;
			}			
			
			else if(scaleMultiplier == 2.0f){//i
				if(X < 64*(scaleMultiplier+0.5f) && Y > ((getHeight()-32)/3+10)*(scaleMultiplier+0.5f)){
					touchType = 2;//if touched the left button
				}
				else if(X < 132*(scaleMultiplier+0.5f) && X > 54*(scaleMultiplier+0.5f) && Y > ((getHeight()-32)/3+10)*(scaleMultiplier+0.5f)){
					touchType = 1;//if touched the right button
				}				
				else if(X < (getWidth()/3+12)*(scaleMultiplier+0.5f) && X > (getWidth()/3-20)*(scaleMultiplier+0.5f) && Y > ((getHeight()-32)/3-32)*(scaleMultiplier+0.5f) &&
						Y < ((getHeight()-32)/3)*(scaleMultiplier+0.5f)){
					touchType = 3;//if touched the attack1 button
				}
				else if(X < (getWidth()/3+12)*(scaleMultiplier+0.5f) && X > (getWidth()/3-20)*(scaleMultiplier+0.5f) && Y > ((getHeight()-32)/3)*(scaleMultiplier+0.5f) &&
					Y < ((getHeight()-32)/3+32)*(scaleMultiplier+0.5f)){
					touchType = 4;//if touched the attack2 button
				}
				else if(X < (getWidth()/3+44)*(scaleMultiplier+0.5f) && X > (getWidth()/3+12)*(scaleMultiplier+0.5f) && Y > ((getHeight()-32)/3)*(scaleMultiplier+0.5f) &&
					Y < ((getHeight()-32)/3+32)*(scaleMultiplier+0.5f)){
					touchType = 5;//if touched the block button
				}
				else if(X < (getWidth()/3+44)*(scaleMultiplier+0.5f) && X > (getWidth()/3+12)*(scaleMultiplier+0.5f) && Y > ((getHeight()-32)/3-32)*(scaleMultiplier+0.5f) &&
						Y < ((getHeight()-32)/3)*(scaleMultiplier+0.5f)){
					touchType = 6;//if touched the dodge button
				}
			}
			else if(scaleMultiplier == 3.0f){//i
				if(X < 64*(scaleMultiplier+0.5f) && Y > ((getHeight()-32)/4)*(scaleMultiplier+0.5f)){
					touchType = 2;//if touched the left button
				}
				else if(X < 132*(scaleMultiplier+0.5f) && X > 54*(scaleMultiplier+0.5f) && Y > ((getHeight()-32)/4)*(scaleMultiplier+0.5f)){
					touchType = 1;//if touched the right button
				}				
				else if(X < (getWidth()/4+12)*(scaleMultiplier+0.5f) && X > (getWidth()/4-20)*(scaleMultiplier+0.5f) && Y > ((getHeight()-32)/4-32)*(scaleMultiplier+0.5f) &&
						Y < ((getHeight()-32)/4)*(scaleMultiplier+0.5f)){
					touchType = 3;//if touched the attack1 button
				}
				else if(X < (getWidth()/4+12)*(scaleMultiplier+0.5f) && X > (getWidth()/4-20)*(scaleMultiplier+0.5f) && Y > ((getHeight()-32)/4)*(scaleMultiplier+0.5f) &&
					Y < ((getHeight()-32)/4+32)*(scaleMultiplier+0.5f)){
					touchType = 4;//if touched the attack2 button
				}
				else if(X < (getWidth()/4+44)*(scaleMultiplier+0.5f) && X > (getWidth()/4+12)*(scaleMultiplier+0.5f) && Y > ((getHeight()-32)/4)*(scaleMultiplier+0.5f) &&
					Y < ((getHeight()-32)/4+32)*(scaleMultiplier+0.5f)){
					touchType = 5;//if touched the block button
				}
				else if(X < (getWidth()/4+44)*(scaleMultiplier+0.5f) && X > (getWidth()/4+12)*(scaleMultiplier+0.5f) && Y > ((getHeight()-32)/4-32)*(scaleMultiplier+0.5f) &&
						Y < ((getHeight()-32)/4)*(scaleMultiplier+0.5f)){
					touchType = 6;//if touched the dodge button
				}
			}
			else if(scaleMultiplier == 5.0f){
				if(X < 64*(scaleMultiplier+0.5f)+10 && Y > ((getHeight()-32)/6-20)*(scaleMultiplier+0.5f)){
					touchType = 2;//if touched the left button
				}
				else if(X < 142*(scaleMultiplier+0.5f) && X > 54*(scaleMultiplier+0.5f) && Y > ((getHeight()-32)/6-20)*(scaleMultiplier+0.5f)){
					touchType = 1;//if touched the right button
				}				
				else if(X < (getWidth()/8+32)*(scaleMultiplier+0.5f) && X > (getWidth()/8-0)*(scaleMultiplier+0.5f) && Y > ((getHeight()-32)/6-52)*(scaleMultiplier+0.5f) &&
						Y < ((getHeight()-32)/6-20)*(scaleMultiplier+0.5f)){
					touchType = 3;//if touched the attack1 button
				}
				else if(X < (getWidth()/8+32)*(scaleMultiplier+0.5f) && X > (getWidth()/8-0)*(scaleMultiplier+0.5f) && Y > ((getHeight()-32)/6-20)*(scaleMultiplier+0.5f) &&
					Y < ((getHeight()-32)/6+12)*(scaleMultiplier+0.5f)){
					touchType = 4;//if touched the attack2 button
				}
				else if(X < (getWidth()/8+64)*(scaleMultiplier+0.5f) && X > (getWidth()/8+32)*(scaleMultiplier+0.5f) && Y > ((getHeight()-32)/6-20)*(scaleMultiplier+0.5f) &&
					Y < ((getHeight()-32)/6+12)*(scaleMultiplier+0.5f)){
					touchType = 5;//if touched the block button
				}
				else if(X < (getWidth()/8+64)*(scaleMultiplier+0.5f) && X > (getWidth()/8+32)*(scaleMultiplier+0.5f) && Y > ((getHeight()-32)/6-52)*(scaleMultiplier+0.5f) &&
						Y < ((getHeight()-32)/6-20)*(scaleMultiplier+0.5f)){
					touchType = 6;//if touched the dodge button
				}
				
				/*else if(scaleMultiplier == 5.0f){ // my nexus 5
					doDraw(mcanvas, leftB, 10, (getHeight()-32)/6-20);
					doDraw(mcanvas, rightB, 80, (getHeight()-32)/6-20);
					
					doDraw(mcanvas, attack1B, getWidth()/8-0, (getHeight()-32)/6-52);
					doDraw(mcanvas, attack2B, getWidth()/8-0, (getHeight()-32)/6-20);
					doDraw(mcanvas, dodgeB, getWidth()/8+32, (getHeight()-32)/6-52);
					doDraw(mcanvas, blockB, getWidth()/8+32, (getHeight()-32)/6-20);
				}*/
			}
			
			break;
			
		case MotionEvent.ACTION_MOVE:
			if(scaleMultiplier == 2.0f){//i
				if(X < 64*(scaleMultiplier+0.5f) && Y > ((getHeight()-32)/3+10)*(scaleMultiplier+0.5f)){
					touchType = 2;//if touched the left button
				}
				else if(X < 132*(scaleMultiplier+0.5f) && X > 54*(scaleMultiplier+0.5f) && Y > ((getHeight()-32)/3+10)*(scaleMultiplier+0.5f)){
					touchType = 1;//if touched the right button
				}
			}
			
			//Toast.makeText(context2, "move "+"X: "+X+"Y: "+Y,
				//	Toast.LENGTH_SHORT).show();
			/*if(X < getWidth()/4){
				touchType = 2;
			}
			else if(X < getWidth()/2 && X > getWidth()/4){
				touchType = 1;
			}*/
			
			break;
			
		case MotionEvent.ACTION_UP:
			//Toast.makeText(context2, "ACTION_UP "+"X: "+X+"Y: "+Y,
				//	Toast.LENGTH_SHORT).show();
			touchType = 0;
			break;			
		}
		
		return true;
	}
	
	public void setImages() throws IOException{
		Options options = new BitmapFactory.Options();
    	options.inScaled = false; 
    	
		healthBar = BitmapFactory.decodeResource(getResources(), R.raw.healthbar, options);
				
		healthBitmaps[0] = BitmapFactory.decodeResource(getResources(), R.raw.healthbar, options);
		healthBitmaps[0] = Bitmap.createBitmap(healthBitmaps[0], 0, 0, 8, 16);
		
		healthBitmaps[1] = BitmapFactory.decodeResource(getResources(), R.raw.healthbar, options);
		healthBitmaps[1] = Bitmap.createBitmap(healthBitmaps[1], 8, 0, 8, 16);

		healthBitmaps[2] = BitmapFactory.decodeResource(getResources(), R.raw.healthbar, options);
		healthBitmaps[2] = Bitmap.createBitmap(healthBitmaps[2], 16, 0, 8, 16);
		
		healthBitmaps[3] = BitmapFactory.decodeResource(getResources(), R.raw.healthbar, options);
		healthBitmaps[3] = Bitmap.createBitmap(healthBitmaps[3], 24, 0, 8, 16);
		
		healthBitmaps[4] = BitmapFactory.decodeResource(getResources(), R.raw.healthbar, options);
		healthBitmaps[4] = Bitmap.createBitmap(healthBitmaps[4], 32, 0, 8, 16);
		
		healthBitmaps[5] = BitmapFactory.decodeResource(getResources(), R.raw.healthbar, options);
		healthBitmaps[5] = Bitmap.createBitmap(healthBitmaps[5], 40, 0, 8, 16);
		
		arcadeControlBM = BitmapFactory.decodeResource(getResources(), R.raw.arcadecontroller, options);
		blooPicBM = BitmapFactory.decodeResource(getResources(), R.raw.bloopic, options);
		redPicBM = BitmapFactory.decodeResource(getResources(), R.raw.redpic, options);
		readyBM = BitmapFactory.decodeResource(getResources(), R.raw.ready, options);
				
		leftB = BitmapFactory.decodeResource(getResources(), R.raw.leftarrowbutton, options);
		rightB = BitmapFactory.decodeResource(getResources(), R.raw.rightarrowbutton, options);
		attack1B = BitmapFactory.decodeResource(getResources(), R.raw.abutton, options);
		attack2B = BitmapFactory.decodeResource(getResources(), R.raw.a2button, options);
		blockB = BitmapFactory.decodeResource(getResources(), R.raw.bbutton, options);
		dodgeB = BitmapFactory.decodeResource(getResources(), R.raw.dbutton, options);
		
		//write a file to external storage          
        File sdCard = Environment.getExternalStorageDirectory();
		
		File directory = new File(sdCard.getAbsolutePath()+ "/CustomBrawl");
		File file = new File(directory, "readme.txt");
                
        try {

            FileOutputStream outputStream = new FileOutputStream(file);
            PrintWriter pw = new PrintWriter(outputStream);
            pw.println("This file belongs to the Custom Brawl game app");
            pw.println("Steps on how to add your own fighter:");
            pw.println("1)Create a file called charlist.txt");
            pw.println("2)add to the file, your character's name, spritesheet name and icon name, in the format #name:spritesheet:icon"); 
            pw.println("3)copy your spritesheet and icon to the folder");                       
            pw.flush();
            pw.close();
        	outputStream.close();
        } catch (Exception e){
        	e.printStackTrace();
        }
		
		//read a file from external storage
		modBM = null;		
		sdCard = Environment.getExternalStorageDirectory();
		
		try{
			directory = new File(sdCard.getAbsolutePath()+ "/CustomBrawl");
			file = new File(directory, "pop.png");
			FileInputStream streamIn = new FileInputStream(file);
			
			modBM = BitmapFactory.decodeStream(streamIn);
			streamIn.close();		
		} catch (Exception e){
        	e.printStackTrace();
        }
		
	}
	
	@Override
	public void surfaceDestroyed(SurfaceHolder holder)
	{
		/*mythread.setRunning(false);
		boolean retry = true;
		while(retry)
		{
			try{
				mythread.join();
				retry = false;
			}
			catch(Exception e){
				Log.v("Exception Occured", e.getMessage());
			}
		}*/
	}
	
	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width, int height)
	{
		
	}
	
	@Override
	public void surfaceCreated(SurfaceHolder holder)
	{
		//mythread = new MyThread(holder, context2, this);
		//thisHolder = holder;
		//mythread.setRunning(true);
		//mythread.start();
	}
	
	public Rectangle ReturnSurfaceDimensions(){
		Rectangle surfaceRectangle = new Rectangle(getWidth(), getHeight());
		return surfaceRectangle;
	}
	
	void doDraw(Canvas canvas, Bitmap b, int xOffset, int yOffset){
		canvas.drawBitmap(b, xOffset, yOffset, null);
	}
	
	void doDrawFlipped(Canvas canvas, Bitmap b, int xOffset, int yOffset){
		Paint mPaint = new Paint();
		mPaint.setFilterBitmap(false);
		
		Matrix flipHorizontalMatrix;
		flipHorizontalMatrix = new Matrix();
		flipHorizontalMatrix.preScale(-1.0f, 1.0f);
		//flipHorizontalMatrix.postTranslate(enemyBitmap.getWidth(),0);
		
		Bitmap flippedBitmap = Bitmap.createBitmap(b, 0, 0, b.getWidth(), 
				b.getHeight(), flipHorizontalMatrix, true);
		
		flipHorizontalMatrix.setTranslate(xOffset, yOffset);
		canvas.drawBitmap(flippedBitmap, flipHorizontalMatrix, mPaint);
	}
	
	void setDraw(Bitmap player, Rectangle pPos, Bitmap challenger, Rectangle cPos, 
			Bitmap bg, int h1, int h2, boolean fightFin){
		mcanvas = getHolder().lockCanvas();
		if(mcanvas != null){
			//draw background
			mcanvas.drawColor(Color.WHITE);		
			if(gamestate == "choose"){
				mcanvas.drawColor(Color.rgb(50,50,50));
				
				float scaleW = 2.0f;
				float scaleH = 2.0f;
				
				scaleW = scaleMultiplier;
				scaleH = scaleMultiplier;
				
				Matrix scaleMatrix;
				scaleMatrix = new Matrix();
				scaleMatrix.preScale(scaleW, scaleH);			
				mcanvas.save();
				mcanvas.setMatrix(scaleMatrix);
				doDraw(mcanvas, blooPicBM, 20, 20);	
				doDraw(mcanvas, redPicBM, 20, 50);
				if(modAvailable == true){
					doDraw(mcanvas, modPicBM, 20, 80);				
				}
				
				String charDescription = "";
				if(chosenChar == 1){
					charDescription = "The Bloo Fello is a normal fighter";
				}
				else if(chosenChar == 2){
					charDescription = "The Red Hao is a normal fighter";
				}
				else if(chosenChar == 3){
					charDescription = "This is a modded fighter";
				}
				
				//draw text				
				Paint paint = new Paint();
				paint.setColor(Color.GREEN);
				
				mcanvas.drawText(charDescription, 60, 30, paint);
				
				if(chosenChar > -1){
					//doDraw(mcanvas, readyBM, getWidth()/2 - 60, getHeight()/2 - 30);
				}
				
				if(scaleMultiplier == 1.5f){				
					doDraw(mcanvas, readyBM, getWidth()/2 - 60, getHeight()/2 - 30);
				}			
				else if(scaleMultiplier == 2.0f){//i
					doDraw(mcanvas, readyBM, getWidth()/2 - 60, getHeight()/2 - 30);
				}
				else if(scaleMultiplier == 3.0f){//i
					doDraw(mcanvas, readyBM, getWidth()/3 - 60, getHeight()/3 - 30);
				}
				else if(scaleMultiplier == 5.0f){ // my nexus 5
					doDraw(mcanvas, readyBM, getWidth()/5 - 60, getHeight()/5 - 30);
				}
				
				mcanvas.restore();
				
			}
			else{
				float scaleW = (float)getWidth()/(float)bg.getWidth();
				float scaleH = (float)getHeight()/(float)bg.getHeight();
				
				//draw background on whole screen
				Matrix scaleMatrix;
				scaleMatrix = new Matrix();
				scaleMatrix.preScale(scaleW, scaleH);			
				mcanvas.save();
				mcanvas.setMatrix(scaleMatrix);
				doDraw(mcanvas, bg, 0, 0);			
				mcanvas.restore();
				
				scaleW = scaleMultiplier;
				scaleH = scaleMultiplier;
				
				scaleMatrix = new Matrix();
				scaleMatrix.preScale(scaleW, scaleH);			
				mcanvas.save();
				mcanvas.setMatrix(scaleMatrix);
				
				//doDraw(mcanvas, modBM, 0, 0);
				if(scaleMultiplier == 5){
					doDraw(mcanvas, player, pPos.xPos, pPos.yPos+getHeight()/6-80);
					doDrawFlipped(mcanvas, challenger, cPos.xPos, cPos.yPos+getHeight()/6-80);
				}
				if(scaleMultiplier == 3){
					doDraw(mcanvas, player, pPos.xPos, pPos.yPos+getHeight()/4-60);
					doDrawFlipped(mcanvas, challenger, cPos.xPos, cPos.yPos+getHeight()/4-60);
				}
				else if(scaleMultiplier == 1.5f){
					int x = (int) (pPos.xPos*0.75);
					int x2 = (int) (cPos.xPos*0.75);
					
					doDraw(mcanvas, player, x, pPos.yPos+getHeight()/4+30);
					doDrawFlipped(mcanvas, challenger, x2, cPos.yPos+getHeight()/4+30);
				}
				else{
					doDraw(mcanvas, player, pPos.xPos, pPos.yPos+getHeight()/4+10);
					doDrawFlipped(mcanvas, challenger, cPos.xPos, cPos.yPos+getHeight()/4+10);
				}
				
				//draw healthbars				
				for(int i = 0; i < h1/10; i++){
					if(i == 0){
						doDraw(mcanvas, healthBitmaps[0], 50, 20);
					}
					else if(i%2 == 0){
						doDraw(mcanvas, healthBitmaps[1], 50+(8*i), 20);
					}
					else{
						doDraw(mcanvas, healthBitmaps[2], 50+(8*i), 20);
					}
				}			
				if(h1/10 == 10){
					doDraw(mcanvas, healthBitmaps[5], 50+80, 20);
				}
				
				int hbOffset = 350;
				if(scaleMultiplier == 1.5f){
					hbOffset = 270;
				}				
				else if(scaleMultiplier == 3.0f){
					hbOffset = 360;
				}
				else if(scaleMultiplier == 5.0f){
					hbOffset = 300;
				}
				
				for(int i = 0; i < h2/10; i++){
					if(i == 0){
						doDraw(mcanvas, healthBitmaps[5], hbOffset, 20);
					}
					else if(i%2 == 0){
						doDraw(mcanvas, healthBitmaps[1], hbOffset+(-8*i), 20);
					}
					else{
						doDraw(mcanvas, healthBitmaps[2], hbOffset+(-8*i), 20);
					}
				}		
				if(h2/10 == 10){
					doDraw(mcanvas, healthBitmaps[0], hbOffset-80, 20);
				}
				
				//draw arcade button
				if(fightFin == true){
					doDraw(mcanvas, arcadeControlBM, 20, 60);
				}
				
				mcanvas.restore();			
				
				//draw fight buttons
				scaleW = scaleMultiplier;
				scaleH = scaleMultiplier;
				scaleMatrix = new Matrix();
				scaleMatrix.preScale(scaleW+0.5f, scaleH+0.5f);			
				mcanvas.save();
				mcanvas.setMatrix(scaleMatrix);
				
				if(scaleMultiplier == 1.5f){
					doDraw(mcanvas, leftB, 0, (getHeight()-32)/2+10);
					doDraw(mcanvas, rightB, 70, (getHeight()-32)/2+10);
					
					doDraw(mcanvas, attack1B, getWidth()/2-20, (getHeight()-32)/2-32);
					doDraw(mcanvas, attack2B, getWidth()/2-20, (getHeight()-32)/2);
					doDraw(mcanvas, dodgeB, getWidth()/2+12, (getHeight()-32)/2-32);
					doDraw(mcanvas, blockB, getWidth()/2+12, (getHeight()-32)/2);					
				}			
				else if(scaleMultiplier == 2.0f){//i
					doDraw(mcanvas, leftB, 0, (getHeight()-32)/3+10);
					doDraw(mcanvas, rightB, 70, (getHeight()-32)/3+10);
					
					doDraw(mcanvas, attack1B, getWidth()/3-20, (getHeight()-32)/3-32);
					doDraw(mcanvas, attack2B, getWidth()/3-20, (getHeight()-32)/3);
					doDraw(mcanvas, dodgeB, getWidth()/3+12, (getHeight()-32)/3-32);
					doDraw(mcanvas, blockB, getWidth()/3+12, (getHeight()-32)/3);
				}
				else if(scaleMultiplier == 3.0f){//i
					doDraw(mcanvas, leftB, 0, (getHeight()-32)/4);
					doDraw(mcanvas, rightB, 70, (getHeight()-32)/4);
					
					doDraw(mcanvas, attack1B, getWidth()/4-20, (getHeight()-32)/4-32);
					doDraw(mcanvas, attack2B, getWidth()/4-20, (getHeight()-32)/4);
					doDraw(mcanvas, dodgeB, getWidth()/4+12, (getHeight()-32)/4-32);
					doDraw(mcanvas, blockB, getWidth()/4+12, (getHeight()-32)/4);
				}
				else if(scaleMultiplier == 5.0f){ // my nexus 5
					doDraw(mcanvas, leftB, 10, (getHeight()-32)/6-20);
					doDraw(mcanvas, rightB, 80, (getHeight()-32)/6-20);
					
					doDraw(mcanvas, attack1B, getWidth()/8-0, (getHeight()-32)/6-52);
					doDraw(mcanvas, attack2B, getWidth()/8-0, (getHeight()-32)/6-20);
					doDraw(mcanvas, dodgeB, getWidth()/8+32, (getHeight()-32)/6-52);
					doDraw(mcanvas, blockB, getWidth()/8+32, (getHeight()-32)/6-20);
				}
				
				mcanvas.restore();
				
				if(scaleMultiplier != 2.0f && scaleMultiplier != 3.0f && scaleMultiplier != 5.0f){
					//draw input box
					Paint paint = new Paint();
					paint.setColor(Color.rgb(50,200,50));
					
					if(touchType == 2){
						mcanvas.drawLine(getWidth()/4, 0, getWidth()/4, getHeight(), paint);
					}			
					else if(touchType == 1){
						mcanvas.drawLine(getWidth()/4, 0, getWidth()/4, getHeight(), paint);
						mcanvas.drawLine(getWidth()/2, 0, getWidth()/2, getHeight(), paint);
					}				
					else if(touchType == 3){
						mcanvas.drawLine(getWidth()*0.5f, 0, getWidth()*0.5f, getHeight()/2, paint);
						mcanvas.drawLine(getWidth()*0.5f, getHeight()/2, getWidth() *0.75f, getHeight()/2, paint);
						mcanvas.drawLine(getWidth()*0.75f, 0, getWidth() *0.75f, getHeight()/2, paint);
					}
					else if(touchType == 4){
						mcanvas.drawLine(getWidth()*0.5f, getHeight()/2, getWidth()*0.5f, getHeight(), paint);
						mcanvas.drawLine(getWidth()*0.5f, getHeight()/2, getWidth()*0.75f, getHeight()/2, paint);
						mcanvas.drawLine(getWidth()*0.75f, getHeight()/2, getWidth()*0.75f, getHeight(), paint);
					}
					else if(touchType == 5){
						mcanvas.drawLine(getWidth()*0.75f, getHeight()/2, getWidth()*0.75f, getHeight(), paint);
						mcanvas.drawLine(getWidth()*0.75f, getHeight()/2, getWidth(), getHeight()/2, paint);
					}
					else if(touchType == 6){
						mcanvas.drawLine(getWidth()*0.75f, 0, getWidth()*0.75f, getHeight()/2, paint);
						mcanvas.drawLine(getWidth()*0.75f, getHeight()/2, getWidth(), getHeight()/2, paint);
					}
				}
				
			}			
			
			getHolder().unlockCanvasAndPost(mcanvas);
		}
	}
	
	String getOutofBounds(Point checkPos){
		if(checkPos.x+32 > getWidth())
			return "Right";
		if(checkPos.x < 0)
			return "Left";
		if(checkPos.y+96 > getHeight())
			return "Bottom";
		if(checkPos.y < 100)
			return "Top";
		
		return "Not";		
	}
	
	Rectangle GetBounds(){
		Rectangle b = new Rectangle(0,0);
		b.width = getWidth();
		b.height = getHeight();
		return b;
	}
	
	void setMovingPosition(int x, int y){
		bitmapPositions[1][0] = x;
		bitmapPositions[1][1] = y;
	}
	
	void setRandomEnemy(int type){  	
		
	}
	
	int GetTouch(){
		return touchType;
	}
	
	void UpdateState(String s){
		gamestate = s;
	}
	
	int GetChosenChar(){
		int tmp = chosenChar;
		chosenChar = -1;
		return tmp;
		
	}
	
	void SetModPic(Bitmap i){
		modPicBM = null;
		modPicBM = i;
		
		if(modPicBM != null){
			modAvailable = true;
		}
	}
	
}