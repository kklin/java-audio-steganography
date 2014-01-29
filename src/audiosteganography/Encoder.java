package audiosteganography;

import java.io.*;
import javax.sound.sampled.*;
import audiosteganography.audio.AudioSampleReader;
import audiosteganography.audio.AudioSampleWriter;
import audiosteganography.fourier.Complex;
import audiosteganography.fourier.FFT;
import audiosteganography.binary.BinaryTool;

public class Encoder {
	File audioFile;

	public Encoder(File audioFile) {
		this.audioFile = audioFile;
	}

	public void encodeMessage(String message, String outPath) { //change outPath to File
		int[] messageAsBits = BinaryTool.ASCIIToBinary(message).getIntArray();
		int currentBit = 0;
        	try {
        		AudioSampleReader sampleReader = new AudioSampleReader(audioFile);
			int bytesRead = 0;
	    		int nbChannels = sampleReader.getFormat().getChannels();
			int totalBytes = (int) sampleReader.getSampleCount()*nbChannels;
			double[] out = new double[totalBytes];
			int bytesToRead=4096*2; //some aribituary number thats 2^n
	   		double[] audioData = new double[totalBytes];
	    		sampleReader.getInterleavedSamples(0, totalBytes, audioData);

			if (totalBytes/bytesToRead<messageAsBits.length) {
				throw new RuntimeException("The audio file is too short for the message to fit!");
			}

			while (bytesRead<totalBytes && currentBit<messageAsBits.length) {
				if (totalBytes-bytesRead<bytesToRead) {
					bytesToRead = totalBytes-bytesRead;
				}

				//System.out.println("Reading data.");
				//take a portion of the data
				double[] samples = new double[bytesToRead];
				for (int i = 0 ; i<samples.length ; i++) {
					samples[i] = audioData[bytesRead+i];
				}
				bytesRead+=bytesToRead;
				double[] channelOne = new double[samples.length/2];
	    			sampleReader.getChannelSamples(0, samples, channelOne); 

				//System.out.println("Taking the FFT.");
				//take the FFT
				double[][] freqMag = FFT.getMag(channelOne, (int) sampleReader.getFormat().getFrameRate());

				channelOne = FFT.correctDataLength(channelOne);
				Complex[] complexData = new Complex[channelOne.length];
				for (int i = 0 ; i<channelOne.length ; i++) {
					complexData[i] = new Complex(channelOne[i],0);
				}
				Complex[] complexMags=FFT.fft(complexData);
				double[] freqs = FFT.getFreqs(complexData.length, (int) sampleReader.getFormat().getFrameRate());

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

				//System.out.println("Writing the 1 or 0");
				//decide if the overtone should be changed and if so, change it. don't write if its a rest
				if (messageAsBits[currentBit]==1 && isRest==false) {
					//edit the data thats going to be ifft'd
					for (int i = 0 ; i<freqs.length ; i++) {
						if (Math.abs(Math.abs(freqs[i])-20000)<5) { //lets try changing a set freq
							complexMags[i]=new Complex(.01*channelOne.length,0);
						}
					}

					//take the IFFT
					Complex[] ifft = FFT.ifft(complexMags);

					//change ifft data from complex to real. put in fft class?
					double[] ifftReal = new double[ifft.length]; 
					for (int i = 0 ; i < ifftReal.length ; i++) {
						ifftReal[i]=ifft[i].re();
					}

					appendOutput(interleaveSamples(ifftReal), bytesRead-bytesToRead, out); //add to the array thats going to be written out
					currentBit++; 	
				} else if (messageAsBits[currentBit]==0 && isRest==false) {
					//add a 0 to the message
					appendOutput(samples, bytesRead-bytesToRead, out);
					currentBit++; 
				} else if (isRest==true) {
					appendOutput(samples, bytesRead-bytesToRead, out); //add on the rest so it doesn't sound weird, but don't make it seem like any bits are being written.
				}
			}

			//writing out the leftover part of the audio file (which doesn't have any encoded btis in it)
			if (bytesRead<totalBytes) {
				double[] leftoverData = new double[totalBytes-bytesRead];
				//take a portion of the data
				for (int i = 0 ; i<leftoverData.length ; i++) {
					leftoverData[i] = audioData[bytesRead+i];
				}
				appendOutput(leftoverData, bytesRead, out);
			}

            		File outFile = new File(outPath);
            		AudioSampleWriter audioWriter = new AudioSampleWriter(outFile,
                    		sampleReader.getFormat(), AudioFileFormat.Type.WAVE);
           		audioWriter.write(out);
            		audioWriter.close();
        	} catch (UnsupportedAudioFileException e) {
        	    e.printStackTrace();
        	} catch (IOException e) {
        	    e.printStackTrace();
        	}
	}

	/*//takes in data for one channel and turns it into two channels
	private static double[] interleaveSamples(double[] mono) {
		double[] interleavedSamples = new double[mono.length*2];
		for (int i = 0 ; i < mono.length ; i++) {
			interleavedSamples[i]=mono[i];
			interleavedSamples[mono.length+i]=mono[i];
		}
		return interleavedSamples;
	}*/

	//takes in data for one channel and turns it into two channels
	private static double[] interleaveSamples(double[] mono) {
		double[] interleavedSamples = new double[mono.length*2];
		int interleavedSamplesCounter = 0;
		for (int i = 0 ; i < mono.length ; i++) {
			interleavedSamples[interleavedSamplesCounter] = mono[i];
			interleavedSamplesCounter++;
			interleavedSamples[interleavedSamplesCounter] = mono[i];
			interleavedSamplesCounter++;
		}
		return interleavedSamples;
	}

	//adds the data to the specified part of the array out
	private static void appendOutput(double[] in, int startIndex, double[] out) {
		for (int i = 0 ; i < in.length ; i++) {
			out[startIndex]=in[i];
			startIndex++;
		}
	}

	public static void main(String args[]) {
		String message = args[0];
		String filePath = args[1];
		String outPath = filePath.substring(0,filePath.length()-4)+"-Encoded.wav";
		Encoder encoder = new Encoder(new File(filePath));
		encoder.encodeMessage(message,outPath);
		System.out.println("Successfully encoded the message into " + outPath);
	}
}
