package main.util;

import java.io.File;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;

public class Sounds {

    float volume;
    FloatControl fc;

    private Clip clip;

    /**
     * creates the sound object
     */
    public Sounds() {
        volume = -20.0f;
    }
    
    /**
     * plays sound
     */
    public void play(String musicLocation) {
        try {
            File musicPath = new File(musicLocation);
            if (musicPath.exists()) {
                AudioInputStream audioInput = AudioSystem.getAudioInputStream(musicPath);
                clip = AudioSystem.getClip();
                clip.open(audioInput);
                fc = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
                fc.setValue(volume);
                clip.start();
            } else {
                System.out.println("ERROR: Cant find file!");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /* play background soundtrack in loop*/
    public void loop(String musicLocation) {
        try {
            File musicPath = new File(musicLocation);
            AudioInputStream audioInput = AudioSystem.getAudioInputStream(musicPath);
            clip = AudioSystem.getClip();
            if (musicPath.exists()) {
                clip.open(audioInput);
                fc = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
                fc.setValue(volume);
                clip.start();
                clip.loop(clip.LOOP_CONTINUOUSLY);
            } else {
                System.out.println("ERROR: Cant find file!");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    /* stop background soundtrack once gameover */
    public void stop(){
        clip.stop();
    }
}
