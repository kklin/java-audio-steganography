/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package audiosteganography;

import java.io.File;
import audiosteganography.audio.AudioSampleReader;
import audiosteganography.audio.AudioSampleWriter;
import audiosteganography.audio.AudioTool;
import javax.sound.sampled.*;
import audiosteganography.fourier.Complex;
import audiosteganography.fourier.FFT;

/**
 *
 * @author kklin
 */
public class Test {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        String message = args[0];
        String filePath = args[1];
        String outPath = filePath.substring(0, filePath.length() - 4) + "-Encoded.wav";
        Encoder encoder = new Encoder(new File(filePath));
        encoder.encodeMessage(message, outPath);
        System.out.println("Successfully encoded the message into " + outPath);

        System.out.println("Beginning decode");
        Decoder decoder = new Decoder(new File(outPath));
        System.out.println("The hidden message was: " + decoder.decodeMessage());
    }
}
