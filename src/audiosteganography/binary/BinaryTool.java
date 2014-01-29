/**
 * Class that can convert various types of data into their binary representations
 */

package audiosteganography.binary;

import audiosteganography.binary.Binary;

public class BinaryTool {
	public static Binary charToBinary(char toConvert) {
		StringBuilder stringRepr = new StringBuilder(Integer.toBinaryString(toConvert));
		while (stringRepr.length() < 8) { // pad with 0's
			stringRepr.insert(0, "0"); // add a 0 to beginning
		}
		stringRepr.reverse(); // WHY?????
		return new Binary(stringRepr.toString());
	}

	public static Binary stringToBinary(String toConvert) {
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
}