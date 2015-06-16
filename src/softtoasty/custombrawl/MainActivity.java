package softtoasty.custombrawl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.util.Random;

import softtoasty.custombrawl.R;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.graphics.BitmapFactory.Options;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.view.Menu;
import android.view.Window;
import android.view.WindowManager;

public class MainActivity extends Activity {

	//for sfx and music
	MediaPlayer mp;
	MediaPlayer mpNormalAttack;
	
	SurfacePanel mSurfacePanel;
	
	Bitmap [][] mBitmapArray = new Bitmap[7][6];
	Bitmap [][] mBitmapArrayR = new Bitmap[7][6];
	Bitmap backgroundBit;
	Bitmap modSheetBM;
	
	Rectangle playerPos;
	Rectangle challengerPos;
	
	int frameNo = 0;
	int frameType = 0;
	int cFrameNo = 0;
	int cFrameType = 0;
	int buttonType = 0;
	int health = 100;
	int cHealth = 100;
	
	int knockBack = 0;
	int cKnockBack = 0;
	
	int hitCounter = 40;
	int dodgeCounter = 0;
	
	int chargeAttack = 0;
	int chargeLevel = 0;
	
	float attackLevel = 1;
	float eAttackLevel = 1;
	
	float speedLevel = 1;
	float eSpeedLevel = 1;
	
	boolean attack1 = false;
	boolean attack2 = false;
	boolean collision = false;
	boolean cAttack1 = false;
	boolean blocking = false;
	
	int whoWon = -1;
	
	int chosenCharacter = 0;
	int player2Character = 1;
	
	int gameLevel = 0;
	
	String gameState = "start";
	String AIState = "attacking";
	
	String modCharName = "";
	String modSheetLocation = "";
	String modIconLocation = "";
		
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		//remove title bar
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		
		//remove notification bar
		this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, 
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
				
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		
		//set up media player for sounds
		setVolumeControlStream(AudioManager.STREAM_MUSIC);
		//mp = MediaPlayer.create(getApplicationContext(), R.raw.button_sound);
		
		
		//Initialise surfacePanel
		mSurfacePanel = (SurfacePanel) findViewById(R.id.surfaceView1);
		//mSurfacePanel.setImages();
		
		//delay runnable		
		Handler delayHandler = new Handler();
		delayHandler.postDelayed(mUpdateTimeTask, 100);
		
		setClips();
		
		//initialise objects
		playerPos = new Rectangle(100, 0);
		challengerPos = new Rectangle(200, 0);
		
		////load mods
		//read from charlist    
	    String stringStuff = "";
	    char c;
	    int i;
	    	
	    String filename = "charlist.txt";
	    FileInputStream inputStream;
	        
	    try {
	      	inputStream = openFileInput(filename); 
	        	
	      	//reads till the end of the stream
	       	while((i=inputStream.read()) != -1){
	       		c=(char)i;
	       		stringStuff+=c;        		
	       	}
		        
		    //split string & load the char details
		    String[] splittedString = stringStuff.split(":");
		    
		    modCharName = splittedString[0];
		    modSheetLocation = splittedString[1];
			modIconLocation = splittedString[2];
		           		        
	      	inputStream.close();
	    } catch (Exception e){
	      	e.printStackTrace();
	    } 
	    
		
		//read a file from external storage
		File sdCard = Environment.getExternalStorageDirectory();
		
		try{
			File directory = new File(sdCard.getAbsolutePath()+ "/CustomBrawl");
			File file = new File(directory, "charlist.txt");
			FileInputStream streamIn = new FileInputStream(file);
			
			//reads till the end of the stream
	       	while((i=streamIn.read()) != -1){
	       		c=(char)i;
	       		stringStuff+=c;        		
	       	}
		        
		    //split string & reload the pet details
		    String[] splittedString = stringStuff.split(":");
		    
		    modCharName = splittedString[0];
		    modSheetLocation = splittedString[1];
			modIconLocation = splittedString[2];
			
			streamIn.close();		
		} catch (Exception e){
        	e.printStackTrace();
        }
		
		if(modCharName != ""){
			//read a file from external storage
			modSheetBM = null;		
			sdCard = Environment.getExternalStorageDirectory();
			
			try{
				File directory = new File(sdCard.getAbsolutePath()+ "/CustomBrawl");
				File file = new File(directory, modSheetLocation);
				FileInputStream streamIn = new FileInputStream(file);				
				modSheetBM = BitmapFactory.decodeStream(streamIn);
				
				file = new File(directory, modIconLocation);
				streamIn = new FileInputStream(file);
				mSurfacePanel.SetModPic(BitmapFactory.decodeStream(streamIn));
				
				streamIn.close();		
			} catch (Exception e){
	        	e.printStackTrace();
	        }
			
			
		}
		
		mpNormalAttack = MediaPlayer.create(getApplicationContext(), R.raw.attack1);	
	}
	
	public void setClips(){
		Options options = new BitmapFactory.Options();
    	options.inScaled = false;  
    	
    	//0 = idle
    	//1 = walking
    	//2 = attack 1
    	//3 = hurt, knockout
    	//4 = attack 2    	
    	//5 = victory
    	//6 = dodge, block
    	
    	//bloo spritesheet
		mBitmapArray[0][0] = BitmapFactory.decodeResource(getResources(), R.raw.bloospritesheet, options);
		mBitmapArray[0][0]= Bitmap.createBitmap(mBitmapArray[0][0], 0, 0, 32, 64);
		
		mBitmapArray[0][1] = BitmapFactory.decodeResource(getResources(), R.raw.bloospritesheet, options);
		mBitmapArray[0][1]= Bitmap.createBitmap(mBitmapArray[0][1], 32, 0, 32, 64);
		
		mBitmapArray[0][2] = BitmapFactory.decodeResource(getResources(), R.raw.bloospritesheet, options);
		mBitmapArray[0][2]= Bitmap.createBitmap(mBitmapArray[0][2], 32*2, 0, 32, 64);
		
		mBitmapArray[0][3] = BitmapFactory.decodeResource(getResources(), R.raw.bloospritesheet, options);
		mBitmapArray[0][3]= Bitmap.createBitmap(mBitmapArray[0][3], 32*3, 0, 32, 64);
		
		mBitmapArray[1][0] = BitmapFactory.decodeResource(getResources(), R.raw.bloospritesheet, options);
		mBitmapArray[1][0]= Bitmap.createBitmap(mBitmapArray[1][0], 0, 64, 32, 64);
		
		mBitmapArray[1][1] = BitmapFactory.decodeResource(getResources(), R.raw.bloospritesheet, options);
		mBitmapArray[1][1]= Bitmap.createBitmap(mBitmapArray[1][1], 32, 64, 32, 64);
		
		mBitmapArray[1][2] = BitmapFactory.decodeResource(getResources(), R.raw.bloospritesheet, options);
		mBitmapArray[1][2]= Bitmap.createBitmap(mBitmapArray[1][2], 32*2, 64, 32, 64);
		
		mBitmapArray[1][3] = BitmapFactory.decodeResource(getResources(), R.raw.bloospritesheet, options);
		mBitmapArray[1][3]= Bitmap.createBitmap(mBitmapArray[1][3], 32*3, 64, 32, 64);
		
		mBitmapArray[2][0] = BitmapFactory.decodeResource(getResources(), R.raw.bloospritesheet, options);
		mBitmapArray[2][0]= Bitmap.createBitmap(mBitmapArray[2][0], 0, 64*2, 32, 64);
		
		mBitmapArray[2][1] = BitmapFactory.decodeResource(getResources(), R.raw.bloospritesheet, options);
		mBitmapArray[2][1]= Bitmap.createBitmap(mBitmapArray[2][1], 32, 64*2, 32, 64);
		
		mBitmapArray[2][2] = BitmapFactory.decodeResource(getResources(), R.raw.bloospritesheet, options);
		mBitmapArray[2][2]= Bitmap.createBitmap(mBitmapArray[2][2], 32*2, 64*2, 32, 64);
		
		mBitmapArray[2][3] = BitmapFactory.decodeResource(getResources(), R.raw.bloospritesheet, options);
		mBitmapArray[2][3]= Bitmap.createBitmap(mBitmapArray[2][3], 32*3, 64*2, 32, 64);
				
		mBitmapArray[3][0] = BitmapFactory.decodeResource(getResources(), R.raw.bloospritesheet, options);
		mBitmapArray[3][0]= Bitmap.createBitmap(mBitmapArray[3][0], 0, 64*3, 64, 64);
		
		mBitmapArray[3][1] = BitmapFactory.decodeResource(getResources(), R.raw.bloospritesheet, options);
		mBitmapArray[3][1]= Bitmap.createBitmap(mBitmapArray[3][1], 64, 64*3, 64, 64);
		
		mBitmapArray[3][2] = BitmapFactory.decodeResource(getResources(), R.raw.bloospritesheet, options);
		mBitmapArray[3][2]= Bitmap.createBitmap(mBitmapArray[3][2], 64*2, 64*3, 64, 64);
		
		mBitmapArray[3][3] = BitmapFactory.decodeResource(getResources(), R.raw.bloospritesheet, options);
		mBitmapArray[3][3]= Bitmap.createBitmap(mBitmapArray[3][3], 64*3, 64*3, 64, 64);
		
		mBitmapArray[4][0] = BitmapFactory.decodeResource(getResources(), R.raw.bloospritesheet, options);
		mBitmapArray[4][0]= Bitmap.createBitmap(mBitmapArray[4][0], 0, 64*4, 32, 64);
		
		mBitmapArray[4][1] = BitmapFactory.decodeResource(getResources(), R.raw.bloospritesheet, options);
		mBitmapArray[4][1]= Bitmap.createBitmap(mBitmapArray[4][1], 32, 64*4, 32, 64);
		
		mBitmapArray[4][2] = BitmapFactory.decodeResource(getResources(), R.raw.bloospritesheet, options);
		mBitmapArray[4][2]= Bitmap.createBitmap(mBitmapArray[4][2], 32*2, 64*4, 32, 64);
		
		mBitmapArray[4][3] = BitmapFactory.decodeResource(getResources(), R.raw.bloospritesheet, options);
		mBitmapArray[4][3]= Bitmap.createBitmap(mBitmapArray[4][3], 32*3, 64*4, 32, 64);
			
		mBitmapArray[5][0] = BitmapFactory.decodeResource(getResources(), R.raw.bloospritesheet, options);
		mBitmapArray[5][0]= Bitmap.createBitmap(mBitmapArray[5][0], 0, 64*5, 32, 64);
		
		mBitmapArray[5][1] = BitmapFactory.decodeResource(getResources(), R.raw.bloospritesheet, options);
		mBitmapArray[5][1]= Bitmap.createBitmap(mBitmapArray[5][1], 32, 64*5, 32, 64);
		
		mBitmapArray[5][2] = BitmapFactory.decodeResource(getResources(), R.raw.bloospritesheet, options);
		mBitmapArray[5][2]= Bitmap.createBitmap(mBitmapArray[5][2], 32*2, 64*5, 32, 64);
		
		mBitmapArray[5][3] = BitmapFactory.decodeResource(getResources(), R.raw.bloospritesheet, options);
		mBitmapArray[5][3]= Bitmap.createBitmap(mBitmapArray[5][3], 32*3, 64*5, 32, 64);
		
		mBitmapArray[6][0] = BitmapFactory.decodeResource(getResources(), R.raw.bloospritesheet, options);
		mBitmapArray[6][0]= Bitmap.createBitmap(mBitmapArray[6][0], 0, 64*6, 32, 64);
		
		mBitmapArray[6][1] = BitmapFactory.decodeResource(getResources(), R.raw.bloospritesheet, options);
		mBitmapArray[6][1]= Bitmap.createBitmap(mBitmapArray[6][1], 32, 64*6, 32, 64);
		
		mBitmapArray[6][2] = BitmapFactory.decodeResource(getResources(), R.raw.bloospritesheet, options);
		mBitmapArray[6][2]= Bitmap.createBitmap(mBitmapArray[6][2], 32*2, 64*6, 32, 64);
			
		
		//red spritesheet
		mBitmapArrayR[0][0] = BitmapFactory.decodeResource(getResources(), R.raw.redspritesheet, options);
		mBitmapArrayR[0][0]= Bitmap.createBitmap(mBitmapArrayR[0][0], 0, 0, 32, 64);
		
		mBitmapArrayR[0][1] = BitmapFactory.decodeResource(getResources(), R.raw.redspritesheet, options);
		mBitmapArrayR[0][1]= Bitmap.createBitmap(mBitmapArrayR[0][1], 32, 0, 32, 64);
		
		mBitmapArrayR[0][2] = BitmapFactory.decodeResource(getResources(), R.raw.redspritesheet, options);
		mBitmapArrayR[0][2]= Bitmap.createBitmap(mBitmapArrayR[0][2], 32*2, 0, 32, 64);
		
		mBitmapArrayR[0][3] = BitmapFactory.decodeResource(getResources(), R.raw.redspritesheet, options);
		mBitmapArrayR[0][3]= Bitmap.createBitmap(mBitmapArrayR[0][3], 32*3, 0, 32, 64);
		
		mBitmapArrayR[1][0] = BitmapFactory.decodeResource(getResources(), R.raw.redspritesheet, options);
		mBitmapArrayR[1][0]= Bitmap.createBitmap(mBitmapArrayR[1][0], 0, 64, 32, 64);
		
		mBitmapArrayR[1][1] = BitmapFactory.decodeResource(getResources(), R.raw.redspritesheet, options);
		mBitmapArrayR[1][1]= Bitmap.createBitmap(mBitmapArrayR[1][1], 32, 64, 32, 64);
		
		mBitmapArrayR[1][2] = BitmapFactory.decodeResource(getResources(), R.raw.redspritesheet, options);
		mBitmapArrayR[1][2]= Bitmap.createBitmap(mBitmapArrayR[1][2], 32*2, 64, 32, 64);
		
		mBitmapArrayR[1][3] = BitmapFactory.decodeResource(getResources(), R.raw.redspritesheet, options);
		mBitmapArrayR[1][3]= Bitmap.createBitmap(mBitmapArrayR[1][3], 32*3, 64, 32, 64);
		
		mBitmapArrayR[2][0] = BitmapFactory.decodeResource(getResources(), R.raw.redspritesheet, options);
		mBitmapArrayR[2][0]= Bitmap.createBitmap(mBitmapArrayR[2][0], 0, 64*2, 32, 64);
		
		mBitmapArrayR[2][1] = BitmapFactory.decodeResource(getResources(), R.raw.redspritesheet, options);
		mBitmapArrayR[2][1]= Bitmap.createBitmap(mBitmapArrayR[2][1], 32, 64*2, 32, 64);
		
		mBitmapArrayR[2][2] = BitmapFactory.decodeResource(getResources(), R.raw.redspritesheet, options);
		mBitmapArrayR[2][2]= Bitmap.createBitmap(mBitmapArrayR[2][2], 32*2, 64*2, 32, 64);
		
		mBitmapArrayR[2][3] = BitmapFactory.decodeResource(getResources(), R.raw.redspritesheet, options);
		mBitmapArrayR[2][3]= Bitmap.createBitmap(mBitmapArrayR[2][3], 32*3, 64*2, 32, 64);
		
		mBitmapArrayR[3][0] = BitmapFactory.decodeResource(getResources(), R.raw.redspritesheet, options);
		mBitmapArrayR[3][0]= Bitmap.createBitmap(mBitmapArrayR[3][0], 0, 64*3, 64, 64);
		
		mBitmapArrayR[3][1] = BitmapFactory.decodeResource(getResources(), R.raw.redspritesheet, options);
		mBitmapArrayR[3][1]= Bitmap.createBitmap(mBitmapArrayR[3][1], 64, 64*3, 64, 64);
		
		mBitmapArrayR[3][2] = BitmapFactory.decodeResource(getResources(), R.raw.redspritesheet, options);
		mBitmapArrayR[3][2]= Bitmap.createBitmap(mBitmapArrayR[3][2], 64*2, 64*3, 64, 64);
		
		mBitmapArrayR[3][3] = BitmapFactory.decodeResource(getResources(), R.raw.redspritesheet, options);
		mBitmapArrayR[3][3]= Bitmap.createBitmap(mBitmapArrayR[3][3], 64*3, 64*3, 64, 64);
			
		mBitmapArrayR[4][0] = BitmapFactory.decodeResource(getResources(), R.raw.redspritesheet, options);
		mBitmapArrayR[4][0]= Bitmap.createBitmap(mBitmapArrayR[4][0], 0, 64*4, 32, 64);
		
		mBitmapArrayR[4][1] = BitmapFactory.decodeResource(getResources(), R.raw.redspritesheet, options);
		mBitmapArrayR[4][1]= Bitmap.createBitmap(mBitmapArrayR[4][1], 32, 64*4, 32, 64);
		
		mBitmapArrayR[4][2] = BitmapFactory.decodeResource(getResources(), R.raw.redspritesheet, options);
		mBitmapArrayR[4][2]= Bitmap.createBitmap(mBitmapArrayR[4][2], 32*2, 64*4, 32, 64);
		
		mBitmapArrayR[4][3] = BitmapFactory.decodeResource(getResources(), R.raw.redspritesheet, options);
		mBitmapArrayR[4][3]= Bitmap.createBitmap(mBitmapArrayR[4][3], 32*3, 64*4, 32, 64);
		
		mBitmapArrayR[5][0] = BitmapFactory.decodeResource(getResources(), R.raw.redspritesheet, options);
		mBitmapArrayR[5][0]= Bitmap.createBitmap(mBitmapArrayR[5][0], 0, 64*5, 32, 64);
		
		mBitmapArrayR[5][1] = BitmapFactory.decodeResource(getResources(), R.raw.redspritesheet, options);
		mBitmapArrayR[5][1]= Bitmap.createBitmap(mBitmapArrayR[5][1], 32, 64*5, 32, 64);
		
		mBitmapArrayR[5][2] = BitmapFactory.decodeResource(getResources(), R.raw.redspritesheet, options);
		mBitmapArrayR[5][2]= Bitmap.createBitmap(mBitmapArrayR[5][2], 32*2, 64*5, 32, 64);
		
		mBitmapArrayR[5][3] = BitmapFactory.decodeResource(getResources(), R.raw.redspritesheet, options);
		mBitmapArrayR[5][3]= Bitmap.createBitmap(mBitmapArrayR[5][3], 32*3, 64*5, 32, 64);
		
		mBitmapArrayR[6][0] = BitmapFactory.decodeResource(getResources(), R.raw.redspritesheet, options);
		mBitmapArrayR[6][0]= Bitmap.createBitmap(mBitmapArrayR[6][0], 0, 64*6, 32, 64);
		
		mBitmapArrayR[6][1] = BitmapFactory.decodeResource(getResources(), R.raw.redspritesheet, options);
		mBitmapArrayR[6][1]= Bitmap.createBitmap(mBitmapArrayR[6][1], 32, 64*6, 32, 64);
		
		mBitmapArrayR[6][2] = BitmapFactory.decodeResource(getResources(), R.raw.redspritesheet, options);
		mBitmapArrayR[6][2]= Bitmap.createBitmap(mBitmapArrayR[6][2], 32*2, 64*6, 32, 64);
				
		backgroundBit = BitmapFactory.decodeResource(getResources(), R.raw.backgroundstadium, options);
				
	}	
	
	public void setNewCharacterClips(int nO){	
		Options options = new BitmapFactory.Options();
    	options.inScaled = false;  
    	
    	Bitmap sheet = null;
    	switch(nO){
    	case 1: sheet = BitmapFactory.decodeResource(getResources(), R.raw.bloospritesheet, options); break;
    	case 2: sheet = BitmapFactory.decodeResource(getResources(), R.raw.redspritesheet, options); break;
    	case 3: sheet = modSheetBM; break;
    	default: sheet = BitmapFactory.decodeResource(getResources(), R.raw.bloospritesheet, options); break;
    	}
    	    	
    	//select a random background
    	int randBack = new Random().nextInt(3);
    	
    	switch(randBack){
    	case 0: backgroundBit = BitmapFactory.decodeResource(getResources(), R.raw.backgrounded, options); break;
    	case 1: backgroundBit = BitmapFactory.decodeResource(getResources(), R.raw.darkback, options); break;
    	case 2: backgroundBit = BitmapFactory.decodeResource(getResources(), R.raw.fantasyback, options); break;
    	default: backgroundBit = BitmapFactory.decodeResource(getResources(), R.raw.backgrounded, options); break;
    	}
    	    	
		mBitmapArray[0][0]= Bitmap.createBitmap(sheet, 0, 0, 32, 64);
		
		mBitmapArray[0][1]= Bitmap.createBitmap(sheet, 32, 0, 32, 64);
		
		mBitmapArray[0][2]= Bitmap.createBitmap(sheet, 32*2, 0, 32, 64);
		
		mBitmapArray[0][3]= Bitmap.createBitmap(sheet, 32*3, 0, 32, 64);
		
		mBitmapArray[1][0]= Bitmap.createBitmap(sheet, 0, 64, 32, 64);
		
		mBitmapArray[1][1]= Bitmap.createBitmap(sheet, 32, 64, 32, 64);
		
		mBitmapArray[1][2]= Bitmap.createBitmap(sheet, 32*2, 64, 32, 64);
		
		mBitmapArray[1][3]= Bitmap.createBitmap(sheet, 32*3, 64, 32, 64);
		
		mBitmapArray[2][0]= Bitmap.createBitmap(sheet, 0, 64*2, 32, 64);
		
		mBitmapArray[2][1]= Bitmap.createBitmap(sheet, 32, 64*2, 32, 64);
		
		mBitmapArray[2][2]= Bitmap.createBitmap(sheet, 32*2, 64*2, 32, 64);
		
		mBitmapArray[2][3]= Bitmap.createBitmap(sheet, 32*3, 64*2, 32, 64);
				
		mBitmapArray[3][0]= Bitmap.createBitmap(sheet, 0, 64*3, 64, 64);
		
		mBitmapArray[3][1]= Bitmap.createBitmap(sheet, 64, 64*3, 64, 64);
		
		mBitmapArray[3][2]= Bitmap.createBitmap(sheet, 64*2, 64*3, 64, 64);
		
		mBitmapArray[3][3]= Bitmap.createBitmap(sheet, 64*3, 64*3, 64, 64);
		
		mBitmapArray[4][0]= Bitmap.createBitmap(sheet, 0, 64*4, 32, 64);
		
		mBitmapArray[4][1]= Bitmap.createBitmap(sheet, 32, 64*4, 32, 64);
		
		mBitmapArray[4][2]= Bitmap.createBitmap(sheet, 32*2, 64*4, 32, 64);
		
		mBitmapArray[4][3]= Bitmap.createBitmap(sheet, 32*3, 64*4, 32, 64);
			
		mBitmapArray[5][0]= Bitmap.createBitmap(sheet, 0, 64*5, 32, 64);
		
		mBitmapArray[5][1]= Bitmap.createBitmap(sheet, 32, 64*5, 32, 64);
		
		mBitmapArray[5][2]= Bitmap.createBitmap(sheet, 32*2, 64*5, 32, 64);
		
		mBitmapArray[5][3]= Bitmap.createBitmap(sheet, 32*3, 64*5, 32, 64);
		
		mBitmapArray[6][0]= Bitmap.createBitmap(sheet, 0, 64*6, 32, 64);
		
		mBitmapArray[6][1]= Bitmap.createBitmap(sheet, 32, 64*6, 32, 64);
		
		mBitmapArray[6][2]= Bitmap.createBitmap(sheet, 32*2, 64*6, 32, 64);
		
		sheet = null;
		
		
		//load new opponent sprite
		if(gameLevel == 1){
			for(int i = 0; i < 7; i++){
				for(int j = 0; j < 6; j++){
					mBitmapArrayR[i][j] = null;
				}
			}
			//0 = idle
	    	//1 = walking
	    	//2 = attack 1
	    	//3 = hurt, knockout
	    	//4 = attack 2    	
	    	//5 = victory
	    	//6 = dodge, block
			
			//set player 2 character to peek
			player2Character = 2;
			
			sheet = BitmapFactory.decodeResource(getResources(), R.raw.peekspritesheet, options);	
			
			mBitmapArrayR[0][0]= Bitmap.createBitmap(sheet, 0, 64*3, 32, 64);
			
			mBitmapArrayR[0][1]= Bitmap.createBitmap(sheet, 32, 64*3, 32, 64);
			
			mBitmapArrayR[0][2]= Bitmap.createBitmap(sheet, 32*2, 64*3, 32, 64);
			
			mBitmapArrayR[0][3]= Bitmap.createBitmap(sheet, 32*3, 64*3, 32, 64);
			
			mBitmapArrayR[1][0]= Bitmap.createBitmap(sheet, 0, 64*2, 32, 64);
			
			mBitmapArrayR[1][1]= Bitmap.createBitmap(sheet, 32, 64*2, 32, 64);
			
			mBitmapArrayR[1][2]= Bitmap.createBitmap(sheet, 32*2, 64*2, 32, 64);
			
			mBitmapArrayR[1][3]= Bitmap.createBitmap(sheet, 32*3, 64*2, 32, 64);
					
			
			mBitmapArrayR[2][0]= Bitmap.createBitmap(sheet, 0, 0, 64, 64);
					
			mBitmapArrayR[2][1]= Bitmap.createBitmap(sheet, 64, 0, 64, 64);
			
			mBitmapArrayR[2][2]= Bitmap.createBitmap(sheet, 64*2, 0, 64, 64);
			
			mBitmapArrayR[2][3]= Bitmap.createBitmap(sheet, 64*3, 0, 64, 64);
			
					
			mBitmapArrayR[3][0]= Bitmap.createBitmap(sheet, 0, 64*4, 64, 64);
			
			mBitmapArrayR[3][1]= Bitmap.createBitmap(sheet, 64, 64*4, 64, 64);
			
			mBitmapArrayR[3][2]= Bitmap.createBitmap(sheet, 64*2, 64*4, 64, 64);
			
			mBitmapArrayR[3][3]= Bitmap.createBitmap(sheet, 64*3, 64*4, 64, 64);
			
			
			
			mBitmapArrayR[4][0]= Bitmap.createBitmap(sheet, 64, 0, 64, 64);
			
			mBitmapArrayR[4][1]= Bitmap.createBitmap(sheet, 64*2, 0, 64, 64);
			
			mBitmapArrayR[4][2]= Bitmap.createBitmap(sheet, 64*3, 0, 64, 64);
			
			mBitmapArrayR[4][3]= Bitmap.createBitmap(sheet, 0, 64, 64, 64);
				
			mBitmapArrayR[4][4]= Bitmap.createBitmap(sheet, 64, 64, 64, 64);
			
			
			mBitmapArrayR[5][0]= Bitmap.createBitmap(sheet, 0, 64*5, 64, 64);
			
			mBitmapArrayR[5][1]= Bitmap.createBitmap(sheet, 64, 64*5, 64, 64);
			
			mBitmapArrayR[5][2]= Bitmap.createBitmap(sheet, 64*2, 64*5, 64, 64);
			
			mBitmapArrayR[5][3]= Bitmap.createBitmap(sheet, 64*3, 64*5, 64, 64);
			
			
			mBitmapArrayR[6][0]= Bitmap.createBitmap(sheet, 0, 64*6, 64, 64);
			
			mBitmapArrayR[6][1]= Bitmap.createBitmap(sheet, 64, 64*6, 64, 64);
			
			mBitmapArrayR[6][2]= Bitmap.createBitmap(sheet, 64*2, 64*6, 64, 64);	
			
			eSpeedLevel = 1.5f;
			eAttackLevel = 2;
		}
		else if(gameLevel >= 2){
			for(int i = 0; i < 7; i++){
				for(int j = 0; j < 6; j++){
					mBitmapArrayR[i][j] = null;
				}
			}
			//0 = idle
	    	//1 = walking
	    	//2 = attack 1
	    	//3 = hurt, knockout
	    	//4 = attack 2    	
	    	//5 = victory
	    	//6 = dodge, block
			
			//set player 2 character to swift
			player2Character = 3;
			
			sheet = BitmapFactory.decodeResource(getResources(), R.raw.swiftspritesheet, options);	
			
			mBitmapArrayR[0][0]= Bitmap.createBitmap(sheet, 0, 96*2+64, 64, 64);
			
			mBitmapArrayR[0][1]= Bitmap.createBitmap(sheet, 64, 96*2+64, 64, 64);
			
			mBitmapArrayR[0][2]= Bitmap.createBitmap(sheet, 64*2, 96*2+64, 64, 64);
			
			mBitmapArrayR[0][3]= Bitmap.createBitmap(sheet, 64*3, 96*2+64, 64, 64);
			
			
			mBitmapArrayR[1][0]= Bitmap.createBitmap(sheet, 0, 96*2, 64, 64);
			
			mBitmapArrayR[1][1]= Bitmap.createBitmap(sheet, 64, 96*2, 64, 64);
			
			mBitmapArrayR[1][2]= Bitmap.createBitmap(sheet, 64*2, 96*2, 64, 64);
			
			mBitmapArrayR[1][3]= Bitmap.createBitmap(sheet, 64*3, 96*2, 64, 64);
			
			mBitmapArrayR[1][4]= Bitmap.createBitmap(sheet, 64*4, 96*2, 64, 64);
					
			
			mBitmapArrayR[2][0]= Bitmap.createBitmap(sheet, 0, 0, 64, 96);
					
			mBitmapArrayR[2][1]= Bitmap.createBitmap(sheet, 64, 0, 64, 96);
			
			mBitmapArrayR[2][2]= Bitmap.createBitmap(sheet, 64*2, 0, 64, 96);
			
			mBitmapArrayR[2][3]= Bitmap.createBitmap(sheet, 64*3, 0, 64, 96);
			
					
			mBitmapArrayR[3][0]= Bitmap.createBitmap(sheet, 0, 96*2+64*2, 64, 64);
			
			mBitmapArrayR[3][1]= Bitmap.createBitmap(sheet, 64, 96*2+64*2, 64, 64);
			
			mBitmapArrayR[3][2]= Bitmap.createBitmap(sheet, 64*2, 96*2+64*2, 64, 64);
			
			mBitmapArrayR[3][3]= Bitmap.createBitmap(sheet, 64*3, 96*2+64*2, 64, 64);
			
			mBitmapArrayR[3][4]= Bitmap.createBitmap(sheet, 64*4, 96*2+64*2, 64, 64);
						
			
			mBitmapArrayR[4][0]= Bitmap.createBitmap(sheet, 64*4, 0, 64, 96);
			
			mBitmapArrayR[4][1]= Bitmap.createBitmap(sheet, 64*5, 0, 64, 96);
			
			mBitmapArrayR[4][2]= Bitmap.createBitmap(sheet, 64*6, 0, 64, 96);
			
			mBitmapArrayR[4][3]= Bitmap.createBitmap(sheet, 64*7, 0, 64, 96);
			
			
			mBitmapArrayR[5][0]= Bitmap.createBitmap(sheet, 0, 96*2+64*3, 64, 64);
			
			mBitmapArrayR[5][1]= Bitmap.createBitmap(sheet, 64, 96*2+64*3, 64, 64);
			
			mBitmapArrayR[5][2]= Bitmap.createBitmap(sheet, 64*2, 96*2+64*3, 64, 64);
			
			mBitmapArrayR[5][3]= Bitmap.createBitmap(sheet, 64*3, 96*2+64*3, 64, 64);
						
			
			mBitmapArrayR[6][0]= Bitmap.createBitmap(sheet, 0, 96*2+64*4, 64, 64);
			
			mBitmapArrayR[6][1]= Bitmap.createBitmap(sheet, 64, 96*2+64*4, 64, 64);
			
			mBitmapArrayR[6][2]= Bitmap.createBitmap(sheet, 64*2, 96*2+64*4, 64, 64);
			
			eSpeedLevel = 0.8f;
			eAttackLevel = 3;
		}
		
	}
		
	//handle delayed timers
	@SuppressLint("HandlerLeak")
	private final Handler mHandler = new Handler() {
		@Override public void handleMessage(Message msg){
			//TextView textView = (TextView) findViewById(R.id.textView_name);
	       	//textView.setText("TimerStuff"); 
		}
	};
	
	private Runnable mUpdateTimeTask = new Runnable() {
		public void run() {
			
			GetInput();
			
			mHandler.postDelayed(this, 100);
			
			Update();
			Render();
		}
	};
	
	public void GetInput(){
		buttonType = mSurfacePanel.GetTouch();
	}
	
	public void Update(){
		mSurfacePanel.UpdateState(gameState);
		
		int xVel = 0;
		int yVel = 0;
		
		int cXVel = 0;
		int cYVel = 0;
		
		if(attack1 == true){
			frameNo++;
			xVel = 2;
		}
		
		if(attack2 == true){
			frameNo++;
			xVel = 2*chargeLevel;			
		}	
		
		if(buttonType == 100){
			ResetFight();
			chosenCharacter = mSurfacePanel.GetChosenChar();
			setNewCharacterClips(chosenCharacter);
		}
		
		//handle gamestates
		if(gameState == "title"){
			if(buttonType == 10){
				frameNo = 4;
				frameType = 3;
				gameState = "choose";
			}
		}
		
		blocking = false;
		
		//handle controls
		if(whoWon == 1){
			frameType = 5;
			frameNo = 3;
		}
		else if(health < 1 && gameState != "title" && gameState != "choose"){
			frameType = 3;
			frameNo = 3;
			whoWon = 0;
			gameState = "title";
		}
		else if(dodgeCounter > 0){
			frameType = 6;
			frameNo = 2;
			dodgeCounter--;
		}
		else if(knockBack > 0){
			frameType = 3;
			frameNo = 1;
			knockBack--;
			xVel = -3;
		}
		else if(attack1 == false && attack2 == false){
			if(buttonType == 1){
				xVel = (int) (5*speedLevel);
				frameType = 1;
				frameNo++;
			}else if(buttonType == 2){
				xVel = (int) (-5*speedLevel);
				frameType = 1;
				frameNo++;
			}else if(buttonType == 3){
				attack1 = true;
				frameNo = 0;
				frameType = 2;
				playerPos.width = 40;
			}
			else if(buttonType == 4){
				chargeAttack++;
			}
			else if(buttonType == 5){
				frameType = 6;
				frameNo = 0;
				blocking = true;
			}
			else if(buttonType == 6){
				dodgeCounter = 10;
				frameType = 6;
				frameNo = 2;
			}
			else{
				if(chargeAttack > 0){
					attack2 = true;
					frameNo = 0;
					frameType = 4;
					playerPos.width = 40;					
					chargeLevel = chargeAttack;
					if(chargeLevel > 5){
						chargeLevel = 5;
					}
					chargeAttack = 0;
				}
				if(attack2 == false){
					frameType = 0;
					frameNo++;
				}
			}
		}
		
		//move player with velocity values
		playerPos.xPos+= xVel;
				
		if(frameNo > 3){
			frameNo = 0;
			attack1 = false;
			attack2 = false;
			playerPos.width = 32;
		}else if(frameNo < 0){
			frameNo = 3;
		}
		
		//check if collision
		if(CollidingRects(playerPos, challengerPos) || playerPos.xPos < 50){
			playerPos.xPos-=xVel;
		}
		
		//check if attack hits the challenger
		if((attack1 == true || attack2 == true) && CollidingRects(playerPos, challengerPos) && frameNo == 3 && (frameType == 2 || frameType == 4)){
			if(attack1 == true){
				cHealth-= 10*attackLevel;
				mpNormalAttack.start();
			}
			else{
				cHealth-= 10*chargeLevel*attackLevel;
				mpNormalAttack.start();
			}
			
			attack1 = false;
			attack2 = false;
			playerPos.width = 32;
			cFrameType = 3;
			cFrameNo = 1;
			cKnockBack = 3;
			chargeLevel = 0;
		}
		
		Rectangle checkClose = new Rectangle(challengerPos.xPos, challengerPos.yPos);
		checkClose.xPos -= 10;
		checkClose.width += 10;
		//manage ai's logic
		
		if(whoWon == 0){
			cFrameType = 5;
			cFrameNo = 3;
		}
		else if(cKnockBack > 0){
			cFrameType = 3;
			cFrameNo = 1;
			cKnockBack--;
			cXVel = 3;
		}
		else if(cHealth < 1 && gameState != "title" && gameState != "choose"){
			cFrameType = 3;
			cFrameNo = 3;
			whoWon = 1;
			gameState = "title";
			gameLevel++;
		}
		else if(CollidingRects(playerPos, checkClose) && hitCounter < 0 && cFrameNo == 0){//player is close
			cFrameType = 2;
			cFrameNo++;
			cXVel = -2;
			cAttack1 = true;
			hitCounter = 5;	//amount of time between npc attacks
		}
		else if(cAttack1 == true){
			cFrameNo++;
		}
		//else if(cHealth > 50 || AIState == "beserk"){
		else if(cHealth < 101 || AIState == "beserk"){
			cXVel = (int) (-5*eSpeedLevel);
			cFrameType = 1;
			cFrameNo++;
		}
		else if(AIState == "running" && cHealth > health){
			AIState = "beserk";
		}
		/*else if(cHealth <= 50 && AIState != "berserk"){
			cXVel = (int) (5*eSpeedLevel);
			cFrameType = 1;
			cFrameNo--;
			AIState = "running";
		}*/
		else{//stand still
			cFrameType = 0;
			cFrameNo++;
		}
		
		if(cFrameNo > 3){
			cFrameNo = 0;
			cAttack1 = false;
		}else if(cFrameNo < 0){
			cFrameNo = 3;
		}		
		
		hitCounter --;		
		challengerPos.xPos+= cXVel;
		
		//check if challenger hits the player
		if(cAttack1 == true && CollidingRects(playerPos, checkClose) && cFrameNo == 3 && dodgeCounter <= 0){
			if(blocking == true){
				health-= 3*eAttackLevel;
				mpNormalAttack.start();
			}
			else{
				health-= 5*eAttackLevel;
				mpNormalAttack.start();
				knockBack = 3;
			}
			cAttack1 = false;			
		}
		
		//check if collision
		if(CollidingRects(playerPos, challengerPos) || challengerPos.xPos > 330){
			challengerPos.xPos-=cXVel;
		}
		
		if(whoWon == 0){
			frameType = 3;
			frameNo = 3;
		}
		else if(whoWon == 1){
			cFrameType = 3;
			cFrameNo = 3;
		}
		
		
	}
	
	public void Render(){
		
		int charHeight = mBitmapArrayR[cFrameType][cFrameNo].getHeight();
		
		Rectangle tmpCPos = new Rectangle(challengerPos.xPos, challengerPos.yPos);
		
		if(cFrameType == 3){
			tmpCPos.xPos -= 30;			
		}
		if(charHeight > 64){
			tmpCPos.yPos -= 32;
		}
		
		mSurfacePanel.setDraw(mBitmapArray[frameType][frameNo], playerPos, 
			mBitmapArrayR[cFrameType][cFrameNo], tmpCPos, backgroundBit, 
			health, cHealth, gameState == "title");
	}
	
	public boolean CollidingRects(Rectangle r1, Rectangle r2){
		//check if p1 is outside p2
	    int left1, left2;
	    int right1, right2;
	    int top1, top2;
	    int bottom1, bottom2;

	    left1 = r1.xPos+10;
	    left2 = r2.xPos+10;
	    right1 = r1.xPos + r1.width-10;
	    right2 = r2.xPos + r2.width-10;
	    top1 = r1.yPos;
	    top2 = r2.yPos;
	    bottom1 = r1.yPos + r1.height;
	    bottom2 = r2.yPos + r2.height;

	    if(right1 < left2) return false;
	    if(left1 > right2) return false;
	    if(bottom1 < top2) return false;
	    if(top1 > bottom2) return false;	    

	    return true;  //if they have collided
	}
	
	public void ResetFight(){
		health = 100;
		cHealth = 100;
		frameType = 0;
		frameNo = 0;
		cFrameType = 0;
		cFrameNo = 0;
		playerPos.xPos = 100;
		challengerPos.xPos = 200;
		gameState = "start";
		AIState = "attacking";
		whoWon = -1;
		
		buttonType = 0;
		
		knockBack = 0;
		cKnockBack = 0;
		
		hitCounter = 40;
		
		chargeAttack = 0;
		chargeLevel = 0;
		
		attack1 = false;
		attack2 = false;
		collision = false;
		cAttack1 = false;
	}

}
