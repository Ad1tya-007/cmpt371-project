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
     * creates the sound object and set volume
     */
    public Sounds() {
        volume = -20.0f;
    }
    
    /**
     * fuction plays sound one time
     */
    public void play(String musicLocation) {
        try {
            File musicPath = new File(musicLocation);
            /* check if path exists, if true play sound, else send error message*/
            if (musicPath.exists()) {
                /*open audio file and starts clip, audio will stop once file is done playing*/
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

    /* function to play background soundtrack in loop*/
    public void loop(String musicLocation) {
        try {
            File musicPath = new File(musicLocation);
            /* check if path exists, if true play sound, else send error message*/
            if (musicPath.exists()) {
                /*open audio file and start clip, clip will play in a continous loop*/
                AudioInputStream audioInput = AudioSystem.getAudioInputStream(musicPath);
                clip = AudioSystem.getClip();
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
    /* call this function to stop background soundtrack once gameover */
    public void stop(){
        clip.stop();
    }
}
