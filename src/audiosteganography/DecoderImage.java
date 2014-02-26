package audiosteganography;

import audiosteganography.audio.AudioSampleReader;
import audiosteganography.fourier.FFT;
import audiosteganography.fourier.FFTData;
import audiosteganography.fourier.FFTDataAnalyzer;
import audiosteganography.binary.Binary;
import audiosteganography.binary.BinaryTool;
import java.io.*;
import javax.sound.sampled.*;

public class DecoderImage {
	File audioFile;
	
	public DecoderImage(File audioFile) {
		this.audioFile = audioFile;
	}
        	
	public String decodeMessage(String outPath) {
		String hiddenMessage="";
		try {
        		AudioSampleReader sampleReader = new AudioSampleReader(audioFile);
			int bytesRead = 0;
	    		int nbChannels = sampleReader.getFormat().getChannels();
			int totalBytes = (int) sampleReader.getSampleCount()*nbChannels;
			int bytesToRead=2048*2; //some aribituary number thats 2^n
			StringBuilder messageAsBytes = new StringBuilder(totalBytes/bytesToRead);

	   		double[] audioData = new double[totalBytes];
	    		sampleReader.getInterleavedSamples(0, totalBytes, audioData);
			while (bytesRead<totalBytes) {
				if (totalBytes-bytesRead<bytesToRead) {
					bytesToRead = totalBytes-bytesRead;
				}

				//read in the data
				double[] samples = new double[bytesToRead];
				for (int i = 0 ; i<samples.length ; i++) {
					samples[i] = audioData[bytesRead+i];
				}
				bytesRead+=bytesToRead;
				double[] channelOne = new double[samples.length/2];
	    			sampleReader.getChannelSamples(0, samples, channelOne); 

				//take the FFT
				channelOne = FFT.correctDataLength(channelOne);
				// double[][] freqMag = FFT.getMag(channelOne, (int) sampleReader.getFormat().getFrameRate());
				FFTData[] fftData = FFT.getMag(channelOne, (int) sampleReader.getFormat().getFrameRate());

				FFTDataAnalyzer analyzer = new FFTDataAnalyzer(fftData);
				boolean isRest = analyzer.isRest();

				double ampToTest = 0; // TODO: rename
				if (!isRest) {
					ampToTest = analyzer.getMagnitudeOfFrequency(20000); // TODO: don't hardcode frequency
				}
				
				if (!isRest) {
					//compare the overtones to see if there should be a 1 or 0
					//int overtoneToTest = overtones.length;
					//if (Math.abs(overtones[overtoneToTest-1][1]-expectedOvertones[overtoneToTest-1])>.0049) {
					if (ampToTest>.0009) { //just test a certain freq
						//checking if something is null..
						messageAsBytes.append("1");
					} else {
						messageAsBytes.append("0");
					}
					int bitsSaved = messageAsBytes.length();
					if (bitsSaved % 8 == 0 && bitsSaved > 64) {
						if (messageAsBytes.toString().substring(bitsSaved-64, bitsSaved).equals("0000000000000000000000000000000000000000000000000000000000000000")) { //if null
							System.out.println("The message is over.");
							messageAsBytes.delete(bitsSaved-64, bitsSaved); // delete the null terminator
							break; //the message is done
						}
					}
 				}
			}

			// hiddenMessage = constructMessage(messageAsBytes.toString());
				Binary binary = new Binary(messageAsBytes.toString());
				System.out.println("Decoded " + binary.length() + " bits.");
				BinaryTool.writeToFile(binary, outPath);
        	} catch (UnsupportedAudioFileException e) {
        	    e.printStackTrace();
        	} catch (IOException e) {
        	    e.printStackTrace();
        	}
		return hiddenMessage;
	}

	private static byte[] toByteArray(int[] toConvert) {
		byte[] converted = new byte[toConvert.length];
		for (int i = 0 ; i<converted.length ; i++) {
			converted[i] = (byte) toConvert[i];
		}
		return converted;
	}

	private static String constructMessage(String messageInBinary) {
		return BinaryTool.binaryToASCII(new Binary(messageInBinary));
	}

	public static void main(String args[]) {
		String filePath = args[0];
		String outPath = args[1];
		DecoderImage decoder = new DecoderImage(new File(filePath));
		System.out.println("The hidden message was: " + decoder.decodeMessage(outPath));
	}
}
