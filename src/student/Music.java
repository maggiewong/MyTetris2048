/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package student;

import java.io.*;
import sun.audio.*;

public class Music {

    private String soundPath;

    Music(String soundPath) {
        this.soundPath = soundPath;
    }

    public void playSound() {
        try {
            InputStream inputStream = getClass().getResourceAsStream(soundPath);
            AudioStream audioStream = new AudioStream(inputStream);
            AudioPlayer.player.start(audioStream);
            } catch (Exception e) {
        }
    }
 
}
