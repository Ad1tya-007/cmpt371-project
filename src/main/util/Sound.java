package util;

import java.io.File;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;

public class Sound {

    float volume;
    FloatControl fc;

    /**
     * creates the sound object
     */
    public Sound() {
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
                Clip clip = AudioSystem.getClip();
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

}
