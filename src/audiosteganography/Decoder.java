package audiosteganography;

import audiosteganography.audio.AudioSampleReader;
import audiosteganography.fourier.FFT;
import java.io.*;
import javax.sound.sampled.*;

public class Decoder {
	File audioFile;
	
	public Decoder(File audioFile) {
		this.audioFile = audioFile;
	}
        	
	public String decodeMessage() {
		String hiddenMessage="";
		try {
        		AudioSampleReader sampleReader = new AudioSampleReader(audioFile);
			int bytesRead = 0;
	    		int nbChannels = sampleReader.getFormat().getChannels();
			int totalBytes = (int) sampleReader.getSampleCount()*nbChannels;
			int bytesToRead=4096*2; //some aribituary number thats 2^n
			String[] messageAsBytes = new String[totalBytes/bytesToRead];
			int currentCharIndex = 0;
			int bitsSaved = 0;

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
				double[][] freqMag = FFT.getMag(channelOne, (int) sampleReader.getFormat().getFrameRate());

				//pick the fundamentalAmp			
				double fundamentalAmp = 0;
				for (int i = 0 ; i<freqMag.length ; i++) {
					if (Math.abs(freqMag[i][1])>fundamentalAmp) {
						fundamentalAmp=freqMag[i][1];
					}
				}
				boolean isRest = false;
				if (fundamentalAmp<.01) { 
					isRest = true;
				}

				//get the amplitude of freq 20000
				double ampToTest = 0;
				if (!isRest) {
					for (int i = 0 ; i<freqMag.length ; i++) { //you don't have to start from 0..
						if (Math.abs(Math.abs(freqMag[i][0])-20000)<5) {
							ampToTest = freqMag[i][1];
						}
					}
				}
				
				if (!isRest) {
					//compare the overtones to see if there should be a 1 or 0
					//int overtoneToTest = overtones.length;
					//if (Math.abs(overtones[overtoneToTest-1][1]-expectedOvertones[overtoneToTest-1])>.0049) {
					if (ampToTest>.009) { //just test a certain freq
						//checking if something is null..
						if (messageAsBytes[currentCharIndex]==null) {
							messageAsBytes[currentCharIndex]="1";
						} else {
							messageAsBytes[currentCharIndex]="1"+messageAsBytes[currentCharIndex]; //adding a 1
						}
					} else {
						if (messageAsBytes[currentCharIndex]==null) {
							messageAsBytes[currentCharIndex]="0";
						} else {
							messageAsBytes[currentCharIndex]="0"+messageAsBytes[currentCharIndex]; //adding a 0
						}
					}
					bitsSaved++;
					if (bitsSaved%8==0) {
						if (messageAsBytes[currentCharIndex].equals("00000000")) { //if null
							System.out.println("The message is over.");
							break; //the message is done
						}
						currentCharIndex++;
					}
 				}
			}

			hiddenMessage=constructMessage(messageAsBytes);
        	} catch (UnsupportedAudioFileException e) {
        	    e.printStackTrace();
        	} catch (IOException e) {
        	    e.printStackTrace();
        	}
		return hiddenMessage;
	}

	private static String constructMessage(String[] messageInBinary) {
		String message = "";
		for (int i = 0 ; i<messageInBinary.length ; i++) {
			int byteAsInt = byteToInt(messageInBinary[i]);
			if (byteAsInt!=-1) {
				message = message + String.valueOf((char)byteAsInt);
			}
		}

		return message;
	}

	private static int byteToInt(String byteAsString) {
		if (byteAsString==null) {
			return -1;
		}
		int byteAsInt = Integer.parseInt(byteAsString);
		int intValue = 0;
		for (int i = 1 ; i<9 ; i++) {
			if ((numberOfPlaces( (int) (byteAsInt%Math.pow(10,i)) )==i) && (byteAsInt%Math.pow(10,i)!=0)) {
				intValue+=Math.pow(2,(i-1));
			}
		}
		return intValue;
	}

	private static int numberOfPlaces(int num) {
		int toReturn;
		if (num>9999999) {
			toReturn =  8;
		} else if (num>999999) {
			toReturn =  7;
		} else if (num>99999) {
			toReturn = 6;
		} else if (num>9999) {
			toReturn = 5;
		} else if (num>999) {
			toReturn = 4;
		} else if (num>99) {
			toReturn = 3;
		} else if (num>9) {
			toReturn = 2;
		} else {
			toReturn = 1;
		}
		return toReturn;
	}

	public static void main(String args[]) {
		String filePath = args[0];
		Decoder decoder = new Decoder(new File(filePath));
		System.out.println("The hidden message was: " + decoder.decodeMessage());
	}
}
