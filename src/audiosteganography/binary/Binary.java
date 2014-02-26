package audiosteganography.binary;

import java.math.BigInteger;

public class Binary {
	private int[] contents;

	public Binary(int[] contents) {
		this.contents = contents;
	}

	public Binary(String stringRepr) {
		contents = new int[stringRepr.length()];
		for (int i = 0 ; i < stringRepr.length() ; i++) {
			contents[i] = Integer.parseInt(stringRepr.substring(i, i+1));
		}
	}

	public void append(Binary toAppend) {
		int[] appended = new int[contents.length + toAppend.contents.length];
		System.arraycopy(contents, 0, appended, 0, contents.length);
		System.arraycopy(toAppend.contents, 0, appended, contents.length, toAppend.contents.length);
		contents = appended;
	}

	public int[] getIntArray() {
		return contents;
	}

	public byte[] getByteArray() {
		String stringRepr = getStringRepr();
		byte[] bytes = new byte[stringRepr.length()/8];
		for (int i = 0 ; i < bytes.length ; i++) {
			bytes[i] = (byte) Integer.parseInt(stringRepr.substring(i*8, (i+1)*8), 2);
		}
		return bytes;
	}

	public String getStringRepr() {
		String stringRepr = "";
		for (int i = 0 ; i<contents.length ; i++) {
			stringRepr += contents[i];
		}
		return stringRepr;
	}

	@Override
	public String toString() {
		return getStringRepr();
	}

	// returns the total number of bits represented by this object
	public int length() {
		return getStringRepr().length();
	}
}