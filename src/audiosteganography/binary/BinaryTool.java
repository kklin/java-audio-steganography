/**
 * Class that can convert various types of data into their binary representations
 */

package audiosteganography.binary;

import java.util.Arrays;
import audiosteganography.binary.Binary;

public class BinaryTool {
	public static final int NUMBER_OF_BITS = 8;

	public static Binary charToBinary(char toConvert) {
		StringBuilder stringRepr = new StringBuilder(Integer.toBinaryString(toConvert));
		while (stringRepr.length() < NUMBER_OF_BITS) { // pad with 0's
			stringRepr.insert(0, "0"); // add a 0 to beginning
		}
		return new Binary(stringRepr.toString());
	}

	public static Binary ASCIIToBinary(String toConvert) {
		if (toConvert.length() == 0) {
			return null;
		}

		char[] composite_chars = toConvert.toCharArray();
		Binary converted = charToBinary(composite_chars[0]);
		for (int i = 1 ; i < composite_chars.length ; i++) {
			converted.append(charToBinary(composite_chars[i]));
		}
		return converted;
	}	

	public static String binaryToASCII(Binary toConvert) {
		int[] binaryData = toConvert.getIntArray();
		StringBuilder stringRepr = new StringBuilder(binaryData.length/NUMBER_OF_BITS);
		for (int i = 0 ; i < binaryData.length ; i+=NUMBER_OF_BITS) {
			Binary charBinary = new Binary(Arrays.copyOfRange(binaryData, i, i+NUMBER_OF_BITS));
			String character = Character.toString(binaryToChar(charBinary));
			stringRepr.append(character);
		}
		return stringRepr.toString();
	}

	public static char binaryToChar(Binary toConvert) {
		String binaryData = toConvert.getStringRepr();
		return (char) Integer.parseInt(binaryData, 2);
	}

	public static void main(String args[]) {
		String toConvert = args[0];
		Binary testBinary = BinaryTool.ASCIIToBinary(toConvert);
		System.out.println(toConvert + " in " + NUMBER_OF_BITS + "-bit binary is: " + testBinary);
		System.out.println(testBinary + " converted back to ASCII is: " + BinaryTool.binaryToASCII(testBinary));
	}
}