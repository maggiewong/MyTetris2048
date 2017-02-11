package student;

import game.v2.Console;
import java.awt.Color;
import java.awt.Font;
import java.awt.Image;
import java.awt.font.FontRenderContext;
import java.awt.geom.AffineTransform;

public class Message {
    
    private static int gridCenterMessageRemainingTime = -1;
    private static String message = "";
    private static Font font = new Font("Arial", Font.BOLD, 30);
    private static AffineTransform affinetransform = new AffineTransform();     
    private static FontRenderContext frc = new FontRenderContext(affinetransform,true,true);    
   // private static final Image gridCenterMsgBg = Console.loadImage("/assets/gridCenterMsgBg.png");
    private static final Color THEME_DARK_BROWN = Color.decode("#776E65");
    
    public static void setGridCenterMessage(String msg, int time){  //time in 0.1 sec
        gridCenterMessageRemainingTime = time*Tetris2048.FPS/10;
        message = msg;
    }
    /*
    public static void draw(){
        if (gridCenterMessageRemainingTime-- > 0){
            //Console.getInstance().drawText(17, 158, message, font, THEME_DARK_BROWN);
            
            Console.getInstance().drawImage(0, 380, gridCenterMsgBg);
            drawCenterText(20,380,460,420,message,font,THEME_DARK_BROWN);
        }
    }
    */
    public static void stopAllMessage(){
        gridCenterMessageRemainingTime = 0;
    }
    
    public static void drawCenterText(int x1, int y1, int x2, int y2, String text, Font font, Color color){
        while (textWidth(text,font) > x2-x1)
            font = new Font(font.getFontName(),font.getStyle(),font.getSize()-1);
        Console.getInstance().drawText((x2+x1-textWidth(text,font)) / 2, (y1+y2+textHeight(text,font)*5/7)/2, text, font, color);
        //font = new Font(font.getFontName(),font.getStyle(),140);
        //Console.getInstance().drawText(x1-3,y2+3, text, font, color);
    }
    
    public static int textHeight(String text, Font font){
        return (int)(font.getStringBounds(text, frc).getHeight());
    }
    
    public static int textWidth(String text, Font font){
        return (int)(font.getStringBounds(text, frc).getWidth());
    }
}
