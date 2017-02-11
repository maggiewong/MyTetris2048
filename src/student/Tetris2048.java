/* 
 * Project: Tetris 2048
 * Date: 15/04/2016
 * Student Name: Wong Man Chi (SID: 54026819)
 * 
 * 
 */

/*
 * Game skeleton of Tetris 2048
 *
 * @author Van
 */
package student;

import game.v2.Console;
import game.v2.Game;
import java.awt.Button;
import java.awt.Color;
import java.awt.Font;
import java.awt.Image;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;
import javazoom.jl.player.Player;



public class Tetris2048 extends Game {


    //Declare data fields
    public static final int TOP = 150;
    public static final int LEFT = 10;
    public static final int ROW = 5;
    public static final int COL = 4;
    public static final int SIZE = 110;
    public static final int FPS = 100;
    public static int count;
    public static int activeTileY;
    public static int activeTileX;
    public static int nextTile;
    public static int score;
    public static int scoreOfThisRound;
    public static int bestScore;
    public static int saveCount;
    public static int secondDigit;
    public static int minuteDigit;
    public static int countTime;
    public static boolean isHardMode;
    public static boolean hasActiveTile;
    public static boolean pause;
    public static boolean toLose;
    public static boolean toWin;
    public static boolean useAIPlayer = false;
    public static boolean hasSameValue = false;
    public static BufferedReader bestScoreFileIN;
    public static BufferedWriter bestScoreFileOUT;
   
    private static final Random randomer = new Random();
    
    //Animation Related
    public static final int ANIMATION_LENGTH = 10;
    private static int tileYPixel[][] = new int[4][5];
    private static int tileXPixel[][] = new int[4][5];
    private static int tileYStep[][][] = new int[4][5][ANIMATION_LENGTH];
    private static int tileXStep[][][] = new int[4][5][ANIMATION_LENGTH];
    private static int tileAnimationCycle[][] = new int[4][5];
   
    //Declare all images be global variables
    private static Image tile[] = new Image[11];    //Put all tiles' images in array tile[]
    private static Image easyLevel;
    private static Image play;
    private static Image black;
    private static Image loseDisplay;
    private static Image winDisplay;
    private static Image pauseDisplay;
    private static Image usingAIPlayer;
    private static Music backgroundMusic;
    private static Music soundEffect;
       
    static int block[][] = new int[4][5];  //block[][] = n, n = 0 = empty, else 2^n
    
    //Main method
    public static void main(String[] args) throws IOException{
        
        /*
         Customize the console window per your need but do not show it yet.
         */
        Console.getInstance()
                .setTitle("Tetris 2048")
                .setWidth(450)
                .setHeight(700)
                .setTheme(Console.Theme.LIGHT);
       
        /*
         Similar to the Console class, use the chaining setters to configure the game. Call start() at the end of
         the chain to start the game loop.
         */
        
        //Put all tiles' images in array tile[]
        for (int i = 1; i <= 11; i++)
            tile[i-1] = Console.loadImage("/assets/tiles/" + (1 << i) + ".png");
        
        //Load all images and files
        easyLevel = Console.loadImage("/assets/EasyLevel3.png");
        play = Console.loadImage("/assets/Play.png");
        black = Console.loadImage("/assets/Black.png");
        loseDisplay = Console.loadImage("/assets/Lose3.png");
        winDisplay = Console.loadImage("/assets/Win4.png");
        pauseDisplay = Console.loadImage("/assets/Pause2.png");
        usingAIPlayer = Console.loadImage("/assets/AIPlayer.png");
        bestScoreFileIN = new BufferedReader(new FileReader("src/assets/BestScore.dat"));
        backgroundMusic = new Music("/assets/CheerfulUpbeatPack.wav");
        soundEffect = new Music("/assets/SoundEffect.wav");
        
        //Read the best score in the file
        String line = null;
        String scoreStr = null;
        boolean readScore = true;
        while((line = bestScoreFileIN.readLine()) != null){
            if (readScore){
                scoreStr = line;
                readScore = false;
            }
        }        
        bestScoreFileIN.close();        
        if(readScore == false)
            bestScore = Integer.valueOf(scoreStr);
        else
            bestScore = 0;        
                
        initializeAllValue();
        
        new Tetris2048()
                .setFps(FPS) // Set frame rate
                .setShowFps(true) // Set to display fps on screen
                .setBackground(Console.loadImage("/assets/MyBoard3.png")) // set background image
                .start();   // start game loop
        
        backgroundMusic.playSound();
               
            try{
            
                FileInputStream backgroundMusic = new FileInputStream("src/assets/CheerfulUpbeatPack.mp3");
                Player playMP3 = new Player(backgroundMusic);   
                playMP3.play();

            }catch(Exception e){System.out.println(e);}       
            
        randomGenerateTile();
        
    }
    
    public static void initializeAllValue() throws IOException{
        
        isHardMode = true; //Preset Hard mode
        count = 0;
        activeTileY = 0;
        activeTileX = 0;
        nextTile = -1;
        score = 0;
        saveCount = -1;
        hasActiveTile = false;
        pause = false;
        toLose = false;   
        toWin = false;
        secondDigit = 0;
        minuteDigit = 0;
        countTime = 0;
        
        recordBestScore();
        
        for (int m=0; m<4; m++){
            for (int n=0; n<5; n++){
                block[m][n] = 0;
                tileAnimationCycle[m][n] = ANIMATION_LENGTH;
            }
        }
    }
    /**
     * **********************************************************************************************
     * There are three abstract methods must be overriden: protected abstract
     * void cycle(); protected abstract void keyPressed(KeyEvent e); protected
     * abstract void mouseClicked(MouseEvent e);    /**
     */
    
    @Override
    protected void cycle() {        //Every 1/FPS second trigger once

       // int e = randomer.nextInt(11) + 1;
        for (int m=0; m<4; m++)
            for (int n=0; n<5; n++)
                if (block[m][n]!=0)
                    drawTile(block[m][n],m,n);
        //drawCenterText(int x1, int y1, int x2, int y2, String text, Font font, Color color)
        Message.drawCenterText(147, 42, 252, 60, String.valueOf(score), new Font("Arial", Font.BOLD, 30), new Color(250,248,239));
        
        //Display Best score
        if (bestScore > score)
            Message.drawCenterText(268, 42, 370, 60, String.valueOf(bestScore), new Font("Arial", Font.BOLD, 30), new Color(250,248,239));
        else
            Message.drawCenterText(268, 42, 370, 60, String.valueOf(score), new Font("Arial", Font.BOLD, 30), new Color(250,248,239));
       
        
        //Display easy level button
        if (!isHardMode)            
            Console.getInstance().drawImage(140, 75, easyLevel); //easy-level button
        
        //Display AIPlayer button
        if (useAIPlayer)
            Console.getInstance().drawImage(11, 91, usingAIPlayer);
        
        //Game Duration        
        countTime++;
        if (countTime%FPS==0)
            if (!toLose && !toWin && !pause) {
            secondDigit++;        
        }
        if (secondDigit > 59){
            secondDigit = 0;
            minuteDigit++;
        }
        String secondStr = String.format("%02d", secondDigit);
        String minuteStr = String.format("%02d", minuteDigit);
        Message.drawCenterText(265, 92, 373, 131, minuteStr + ":" + secondStr, new Font("Arial Black", Font.PLAIN, 20), new Color(119, 110, 101));
        
        //Hard mode or easy mode
        if ((count%(FPS/2)==0 && isHardMode)||((count%FPS==0)&& !isHardMode)){
            boolean setFalse = false;
            
            //If a block with value 2048 is found, the player wins
            for (int m=0; m<4; m++)
                for (int n=1; n<5; n++)
                    if(block[m][n] == 11) 
                    toWin = true;
                            
            //Drop all blocks
            for (int n=3; n>=0; n--)
                for (int m=0; m<4; m++)                    
                    if (block[m][n]!=0){

                        boolean dropped = false;
                        if (block[m][n+1]==0){
                            moveTile(m,n,m,n+1);
                            dropped = true;
                        }
                        else if (block[m][n+1]==block[m][n]){
                            moveTile(m,n,m,n+1);
                            block[m][n+1]++;
                            dropped = true;
                            soundEffect.playSound();
                            score = score + (int)Math.pow(2,block[m][n+1]);
                        }
                        if (dropped && m == activeTileX && n == activeTileY){
                            activeTileY++;
                            hasActiveTile = true;
                        }    
                    }    
            
            myAIPlayer();
            //If a tile left on the first row over 0.5s, the player loses
            for (int m=0; m<4; m++)
                if (block[m][0]!=0)
                    toLose = true;                                      
                           
            if (hasActiveTile && (activeTileY >= 4 ||
                    (block[activeTileX][activeTileY+1] != block[activeTileX][activeTileY] && block[activeTileX][activeTileY+1] != 0)))
                setFalse = true;            
            if (!hasActiveTile)
                randomGenerateTile();
            if (setFalse)
                hasActiveTile = false;         
        }

        
        //X,Y,text content, Font(font type,font style, font size), Color
        console.drawText(403, 61, String.valueOf((int)Math.pow(2, nextTile)), new Font("Arial", Font.BOLD, 30), new Color(250,248,239));

        if (toLose || toWin) { //If lose, the cycle stops
            count = -1;
            if (toLose)
                loseCondition();
            if (toWin)
                winCondition();
        }
        else if (pause) { //If pause, the cycle stops
            saveCount = count;
            count = -1;
            Console.getInstance().drawImage(381, 73, play);
            Console.getInstance().drawImage(0, 140, black);
            Console.getInstance().drawImage(119, 320, pauseDisplay);
        }
        else if (!pause && saveCount!=-1) { //If resume, the cycle continues
            count = saveCount;
            count++;  
        }       
        else //If not pause and not win and not lose, keeps continue
            count++;     
    }
    
    protected void drawTile(int e, int x, int y){   //e = value (2^e), col = column (0-3), row = row(0-4)
        int c = tileAnimationCycle[x][y];
        if (c < ANIMATION_LENGTH){
            tileXPixel[x][y] = tileXStep[x][y][c];
            tileYPixel[x][y] = tileYStep[x][y][c];
            tileAnimationCycle[x][y]++;
        }
        console.drawImage(tileXPixel[x][y], tileYPixel[x][y], tile[e-1]);
    }    
    
    @Override
    protected void keyPressed(KeyEvent e) { //Trigger when a key is pressed
       // System.out.println("Key Pressed: " + e.getKeyCode());
        if (!toLose && !toWin){
            switch(e.getKeyCode()){
                case 37: //left
                    if (activeTileX-1<0 || block[activeTileX-1][activeTileY]!=block[activeTileX][activeTileY] && block[activeTileX-1][activeTileY] != 0)
                        break;
                    if (block[activeTileX-1][activeTileY]==block[activeTileX][activeTileY]){
                        moveTile(activeTileX,activeTileY,activeTileX-1,activeTileY);
                        block[activeTileX-1][activeTileY]++;
                        score = score + (int)Math.pow(2,block[activeTileX-1][activeTileY]);
                        soundEffect.playSound();
                        activeTileX--;
                        hasActiveTile = true;
                        break;
                    }  
                    moveTile(activeTileX,activeTileY,activeTileX-1,activeTileY);
                    activeTileX--;
                    break;
                    
                case 40: //down
                    for (int n=4; n>activeTileY; n--){
                        if (block[activeTileX][n] == 0){
                            if (n != 4){
                                if (block[activeTileX][activeTileY] == block[activeTileX][n+1]){
                                    moveTile(activeTileX,activeTileY,activeTileX,n+1);
                                    block[activeTileX][n+1]++;
                                    soundEffect.playSound();
                                    score = score + (int)Math.pow(2,block[activeTileX][n+1]);
                                    activeTileY = n+1;                                            
                                    break;
                                }
                                else {
                                    moveTile(activeTileX,activeTileY,activeTileX,n);                                    
                                    activeTileY = n;
                                    break;
                                }
                            }
                            else {
                                moveTile(activeTileX,activeTileY,activeTileX,n);                             
                                activeTileY = n;
                                break;
                            }
                        }
                    }
                    break;
                   
                case 39: //right
                    if (activeTileX+1>3 || block[activeTileX+1][activeTileY]!=block[activeTileX][activeTileY] && block[activeTileX+1][activeTileY] != 0)
                        break;
                    if (block[activeTileX+1][activeTileY]==block[activeTileX][activeTileY]){
                        moveTile(activeTileX,activeTileY,activeTileX+1,activeTileY);
                        block[++activeTileX][activeTileY]++;
                        score = score + (int)Math.pow(2,block[activeTileX][activeTileY]);
                        soundEffect.playSound();
                        hasActiveTile = true;
                        break;
                    }    
                    moveTile(activeTileX,activeTileY,activeTileX+1,activeTileY);
                    activeTileX++;
                    break;

                case 32: //space bar
                    if (!pause)
                        pause = true;                   
                    else                     
                        pause = false;                   
            }           
        }
        else if (toLose || toWin){
            if (e.getKeyCode() == 82 || e.getKeyCode() == 10)  //Retry button, R=82, Enter=10
                try{
                    initializeAllValue();     
                }catch(Exception e2){System.out.println(e);}
        }   
    }

    @Override
    protected void mouseClicked(MouseEvent e) { //trigger when the console is clicked
        int x = e.getX();
        int y = e.getY();
        System.out.println("Click on (" + x + "," + y + ")");
               
        // Choose the difficulty of the game
        if (checkClicked(e, 143, 78, 109, 53)){
            System.out.println("Clicked");
            if (isHardMode)
                isHardMode = false;
            else
                isHardMode = true;
        }
        
        //Pause and Resume button
        if (checkClicked(e, 384, 78, 55, 52)){
            System.out.println("Clicked2");
                if (!pause)
                    pause = true;
                else
                    pause = false;            
        }
        
        //Retry button
        if (checkClicked(e, 233, 394, 103, 43) && (toLose||toWin)){ //Retry button
            System.out.println("Clicked3");
            try{
                initializeAllValue();
            }catch(Exception e3){System.out.println(e);}    
        }        
        
        if (checkClicked(e, 14, 94, 117, 37)){
            System.out.println("Clicked4");
            if (useAIPlayer)
                useAIPlayer = false;
            else
                useAIPlayer = true;
        }
    }
    
    protected boolean checkClicked(MouseEvent e, int btnX, int btnY, int btnWidth, int btnHeight){
        int x = e.getX();
        int y = e.getY();
        if (x >= btnX && x <= btnX+btnWidth && y >= btnY && y <=btnY+btnHeight)
            return true;
        else
            return false;
    }
    
    public static void randomGenerateTile(){

        int r1 = randomer.nextInt(10);
        int r2 = randomer.nextInt(4);
        int temp;
      
        if (r1<6)
            temp=1;
        else if (r1<9)
            temp=2;
        else
            temp=3;
        
        if (!isHardMode)
            temp = 4 - temp;
        if (nextTile != -1){
            tileAnimationCycle[r2][0] = ANIMATION_LENGTH;
            tileXPixel[r2][0] = calPixel(r2,true);
            tileYPixel[r2][0] = calPixel(0,false);
            block[r2][0] = nextTile;
        }
        activeTileY = 0;
        activeTileX = r2;
        
        if (nextTile != -1)
            hasActiveTile = true;
        
        nextTile = temp;   
    }
    
    public static void loseCondition(){
        Console.getInstance().drawImage(0, 140, black);
        Console.getInstance().drawImage(90, 270, loseDisplay);
        scoreOfThisRound = score;        
    }
    
    public static void winCondition(){
        Console.getInstance().drawImage(0, 140, black);
        Console.getInstance().drawImage(90, 270, winDisplay);  
        scoreOfThisRound = score; 
    }
    
    private static void recordBestScore() throws IOException{
        if (scoreOfThisRound > bestScore){
            bestScoreFileOUT = new BufferedWriter(new FileWriter("src/assets/BestScore.dat"));
            bestScoreFileOUT.write(Integer.toString(scoreOfThisRound));
            bestScoreFileOUT.newLine();
            bestScoreFileOUT.close();
            bestScore = scoreOfThisRound;
        }
    }    
    
    private void moveTile (int x, int y, int desX, int desY){
        block[desX][desY] = block[x][y];
        int disX = calPixel(desX,true) - tileXPixel[x][y];
        int disY = calPixel(desY,false) - tileYPixel[x][y];
        tileAnimationCycle[desX][desY] = 0;
        for (int i = 0; i < ANIMATION_LENGTH-1; i++){
            tileXStep[desX][desY][i] = (int)(tileXPixel[x][y] + disX*1.0*i/ANIMATION_LENGTH);
            tileYStep[desX][desY][i] = (int)(tileYPixel[x][y] + disY*1.0*i/ANIMATION_LENGTH);
        }
        tileXStep[desX][desY][ANIMATION_LENGTH-1] = calPixel(desX,true);
        tileYStep[desX][desY][ANIMATION_LENGTH-1] = calPixel(desY,false);
        
        block[x][y] = 0;
        tileYPixel[x][y] = calPixel(x,true);
        tileXPixel[x][y] = calPixel(y,true);
        tileAnimationCycle[x][y] = ANIMATION_LENGTH;
        
    }
    
    public static int calPixel(int i, boolean X){
        if (X)
            return LEFT + i * SIZE;
        else
            return TOP + i * SIZE;
    }
    
    
    public void myAIPlayer(){

        Button a = new Button();
        
        if (useAIPlayer){
            if (hasActiveTile){
                
                int getLowestX = 0;
                for (int x=1; x<=3; x++)                    
                    if (getHeight(x)<getHeight(getLowestX))
                        getLowestX = x;    
                
                hasSameValue = false;
                for (int m=0; m<=3; m++){
                    if (getHeight(m) != 0){
                        if (block[activeTileX][activeTileY] == block[m][getTopTileY(m)]){
                            hasSameValue = true;
                            int ax = activeTileX;
                            if (ax == m){
                                keyPressed(new KeyEvent(a, 1, 20, 1, 40, 'a'));
                                break;
                            }
                            else if (ax>m){
                                for (int i=m; ax>i; i++){
                                    keyPressed(new KeyEvent(a, 1, 20, 1, 37, 'a'));
                                }
                                    keyPressed(new KeyEvent(a, 1, 20, 1, 40, 'a'));                                   
                                    break;
                            }                             
                            else if (ax<m){
                                for (int i=m; ax<i; i--){
                                    keyPressed(new KeyEvent(a, 1, 20, 1, 39, 'a'));
                                }
                                    keyPressed(new KeyEvent(a, 1, 20, 1, 40, 'a'));
                                    break;                                
                            }
                        }
                    }
                }      
                
                if (!hasSameValue){
                    int ax = activeTileX;
                    
                    if (ax == getLowestX){
                        keyPressed(new KeyEvent(a, 1, 20, 1, 40, 'a'));
                    }
                        
                    else if (ax>getLowestX){
                        for (int i=getLowestX; ax>i; i++){
                            keyPressed(new KeyEvent(a, 1, 20, 1, 37, 'a'));
                        }
                            keyPressed(new KeyEvent(a, 1, 20, 1, 40, 'a'));
                            keyPressed(new KeyEvent(a, 1, 20, 1, 37, 'a'));
                    }
                    else if (ax<getLowestX){
                        for (int i=getLowestX; ax<i; i--){
                            keyPressed(new KeyEvent(a, 1, 20, 1, 39, 'a'));
                        }
                            keyPressed(new KeyEvent(a, 1, 20, 1, 40, 'a'));
                            keyPressed(new KeyEvent(a, 1, 20, 1, 37, 'a'));
                    }
                }   
                
            for (int n=3; n>=0; n--)
                for (int m=0; m<4; m++)                    
                    if (block[m][n]!=0){

                        boolean dropped = false;
                        if (block[m][n+1]==0){
                            moveTile(m,n,m,n+1);
                            dropped = true;
                        }
                        else if (block[m][n+1]==block[m][n]){
                            moveTile(m,n,m,n+1);
                            block[m][n+1]++;
                            dropped = true;
                            soundEffect.playSound();
                            score = score + (int)Math.pow(2,block[m][n+1]);
                        }
                        if (dropped && m == activeTileX && n == activeTileY){
                            activeTileY++;
                            hasActiveTile = true;
                        }    
                    }  
            
            for (int n=3; n>=0; n--)
                for (int m=0; m<4; m++)                    
                    if (block[m][n]!=0){

                        boolean dropped = false;
                        if (block[m][n+1]==0){
                            moveTile(m,n,m,n+1);
                            dropped = true;
                        }
                        else if (block[m][n+1]==block[m][n]){
                            moveTile(m,n,m,n+1);
                            block[m][n+1]++;
                            dropped = true;
                            soundEffect.playSound();
                            score = score + (int)Math.pow(2,block[m][n+1]);
                        }
                        if (dropped && m == activeTileX && n == activeTileY){
                            activeTileY++;
                            hasActiveTile = true;
                        }    
                    }  
            
            for (int n=3; n>=0; n--)
                for (int m=0; m<4; m++)                    
                    if (block[m][n]!=0){

                        boolean dropped = false;
                        if (block[m][n+1]==0){
                            moveTile(m,n,m,n+1);
                            dropped = true;
                        }
                        else if (block[m][n+1]==block[m][n]){
                            moveTile(m,n,m,n+1);
                            block[m][n+1]++;
                            dropped = true;
                            soundEffect.playSound();
                            score = score + (int)Math.pow(2,block[m][n+1]);
                        }
                        if (dropped && m == activeTileX && n == activeTileY){
                            activeTileY++;
                            hasActiveTile = true;
                        }    
                    }  
            
                if (activeTileX +1 != 4){
                    if (block[activeTileX][activeTileY] == block[activeTileX+1][activeTileY]){
                        keyPressed(new KeyEvent(a, 1, 20, 1, 39, 'a'));
                        keyPressed(new KeyEvent(a, 1, 20, 1, 37, 'a'));
                    }
                }                
                if ((activeTileX-1) != -1){
                    if (block[activeTileX][activeTileY] == block[activeTileX-1][activeTileY]){
                        keyPressed(new KeyEvent(a, 1, 20, 1, 37, 'a'));
                        keyPressed(new KeyEvent(a, 1, 20, 1, 37, 'a'));
                        keyPressed(new KeyEvent(a, 1, 20, 1, 37, 'a'));
                    }
                    if (block[activeTileX][activeTileY] == 0){
                        keyPressed(new KeyEvent(a, 1, 20, 1, 37, 'a'));
                        keyPressed(new KeyEvent(a, 1, 20, 1, 37, 'a'));
                        keyPressed(new KeyEvent(a, 1, 20, 1, 37, 'a'));                       
                    }
                } 
            }
        }    
    }
     
    public static int getHeight(int c1){
        int countHeight = 0;
        for (int n=4; n>0; n--)
            if (block[c1][n] != 0){
                countHeight++;
            }
        return countHeight;
    }
    
    public static int getTopTileY(int c1){
        return 5-getHeight(c1);
    }
    
}

