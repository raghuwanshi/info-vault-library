package in.net.pragya.infovault.library;

public class Base64
{
	final static String base64Charset =
			"ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/";
	final static byte[] base64CharArray = base64Charset.getBytes();
	
	public static byte[] decode(final String input)
	{
		byte[] inBytes = input.getBytes();
		byte[] outBytes = new byte[(inBytes.length / 4 * 3)
		                           + ((inBytes.length % 4 == 0) ? 0 : 3)];
		for(int i = 0; i < inBytes.length; i += 4)
		{
			int temp = (base64Charset.indexOf(inBytes[i], 0) & 0x0000003F);
			for(int j = 1; j < 4; j++)
			{
				temp <<= 6;
				if((i + j) < inBytes.length && inBytes[i + j] != '=')
					temp |= (base64Charset.indexOf(inBytes[i + j], 0) & 0x0000003F);
			}
				
			for(int k = i / 4 * 3, count = 0; count < 3; count++)
				outBytes[k + count] = (byte)((temp >> ((2 - count) * 8)) & 0x000000FF);
		}
		return outBytes;
	}
	
	public static String encode(final byte[] inBytes)
	{
		int padding = (inBytes.length % 3 == 0) ? 0 : (3 - (inBytes.length % 3));
		byte[] outBytes = new byte[(inBytes.length + padding) / 3 * 4];
		for(int i = 0; i < inBytes.length; i += 3)
		{
			int temp = (inBytes[i] << 16);
			for(int j = 1; j < 3; j++)
				temp |= ((i + j < inBytes.length) ? (inBytes[i + j] << ((2 - j) * 8)) : 0);

			for(int k = i / 3 * 4, count = 0; count < 4; count++)
			{
				if((i + 3) >= inBytes.length && count >= 2 && padding >= (4 - count))
					outBytes[k + count] = '=';
				else
					outBytes[k + count] = base64CharArray[(temp >> ((3 - count) * 6)) & 0x0000003F];
			}
		}
		return new String(outBytes);
	}
	
}
