package nettion.utils;


import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;

import javax.sound.sampled.*;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.util.Objects;

public class SoundPlayer {
    public void playSound(SoundType st, float volume) {
        new Thread(() -> {
            AudioInputStream as;
            try {
                as = AudioSystem.getAudioInputStream(new BufferedInputStream(Objects.requireNonNull(Minecraft.getMinecraft().getResourceManager().getResource(new ResourceLocation("nettion/sound/" + st.getName()))
                .getInputStream())));
                Clip clip = AudioSystem.getClip();
                clip.open(as);
                clip.start();
                FloatControl gainControl =
                        (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
                gainControl.setValue(volume);
                clip.start();
            } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
                e.printStackTrace();
            }
        }).start();
    }

    public enum SoundType {
        Enable("enable.wav"),
        Disable("disable.wav");

        final String name;

        SoundType(String fileName) {
            this.name = fileName;
        }

        String getName() {
            return name;
        }
    }
}
