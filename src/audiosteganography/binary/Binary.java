package audiosteganography.binary;

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

	@Override
	public String toString() {
		String stringRepr = "";
		for (int i = 0 ; i<contents.length ; i++) {
			stringRepr += contents[i];
		}
		return stringRepr;
	}
}