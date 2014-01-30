package audiosteganography.audio;

public class AudioTool {
	//takes in data for one channel and turns it into two channels
	public static double[] interleaveSamples(double[] mono) {
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
}