package com.dev.chatgptbot.converter;

import org.springframework.stereotype.Component;

import javax.sound.sampled.*;
import java.io.*;
import java.util.ArrayList;

@Component
public class OggToWavConverter {

    public void convertTelegramVoiceToWav() throws IOException, LineUnavailableException, UnsupportedAudioFileException {
        File file = new File("voice.ogg");
        AudioInputStream in = AudioSystem.getAudioInputStream(file);
        AudioInputStream din = null;
        AudioFormat baseFormat = in.getFormat();

        AudioFormat decodedFormat = new AudioFormat(
                AudioFormat.Encoding.PCM_SIGNED, baseFormat.getSampleRate(),
                16, baseFormat.getChannels(), baseFormat.getChannels() * 2,
                baseFormat.getSampleRate(), false);

        din = AudioSystem.getAudioInputStream(decodedFormat, in);
        AudioSystem.write(din, AudioFileFormat.Type.WAVE, new File("voice.wav"));
        System.out.println("Converted to wav");
        in.close();
    }
}
