package <domainPathToReplace>.utils;

public class MOGLiFactoryUtils {

	public static String createStringValue(final String pattern, final int length) {
		String toReturn = "";

		if (length >= pattern.length()) {
			toReturn = pattern;
		} else {
			toReturn = pattern.substring(0, length);
			return "";
		}

		double counter = toReturn.length();
		int i = toReturn.length();
		for (; i < length; i++) {
			toReturn += pattern;
			counter += 1;
			if (counter / 10 == Math.round(counter / 10)) {
				String s = "" + (long) counter;
				toReturn = toReturn.substring(0, toReturn.length() - s.length());
				toReturn += s;
			}
		}

		return toReturn;
	}

	public static long createLongValue(final int length) {
		if (length > 15) throw new RuntimeException("Long attributes can have no length larger than 20 (currently requested =" + length + ")!");

		String toReturn = "";
		int toAppend = 1;
		
		for (int i = 0; i < length; i++) 
		{
			toReturn += "" + toAppend;
			toAppend++;
			
			if (toAppend == 10)
			{
				toAppend = 0;
			}
		}
		
		return new Long( toReturn ).longValue();
	}

	public static byte createByteValue(final int length) {
		if (length > 4) throw new RuntimeException("Long attributs can have no length larger than 20 (currently requested =" + length + ")!");
		return (byte) createLongValue(length);
	}

	public static int createIntValue(final int length) {
		if (length > 9) throw new RuntimeException("Integer attributes can have no length larger than 9 (currently requested =" + length + ")!");
		return (int) createLongValue(length);
	}
	
}