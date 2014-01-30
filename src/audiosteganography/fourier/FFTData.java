package audiosteganography.fourier;

public class FFTData {
	private double frequency;
	private double magnitude;

	public FFTData(double frequency, double magnitude) {
		this.frequency = frequency;
		this.magnitude = magnitude;
	}

	public double getFrequency() {
		return frequency;
	}

	public double getMagnitude() {
		return magnitude;
	}
}