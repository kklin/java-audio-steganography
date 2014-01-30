package audiosteganography.fourier;

public class FFTDataAnalyzer {
	public static final double REST_THRESHOLD = .01;

	FFTData[] fftData;

	public FFTDataAnalyzer(FFTData[] fftData) {
		this.fftData = fftData;
	}

	public boolean isRest() {
		double maxMagnitude = 0;
		for (int i = 0 ; i<fftData.length ; i++) {
			if (fftData[i].getMagnitude() > maxMagnitude) { // Math.abs(data.getMagnitude())
				maxMagnitude = fftData[i].getMagnitude();
			}
		}

		return (maxMagnitude < REST_THRESHOLD);
	}

	public double getMagnitudeOfFrequency(double frequency) {
		double magnitude = 0;
		for (int i = 0 ; i<fftData.length ; i++) { //you don't have to start from 0..
			//if (Math.abs(Math.abs(freqMag[i][0])-20000)<5) {
			if (Math.abs(fftData[i].getFrequency() - frequency) < 5) { // remove "magic number" 5
				return fftData[i].getMagnitude();
			}
		}
		return -1;
	}
}