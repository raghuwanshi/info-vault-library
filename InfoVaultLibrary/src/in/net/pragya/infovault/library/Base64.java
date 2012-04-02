package in.net.pragya.infovault.library;

public class Base64
{
	final static String base64Charset =
			"ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/";
	final static byte[] base64CharArray = base64Charset.getBytes();
	
	public static byte[] decode(final String input)
	{
		byte[] inBytes = input.getBytes();
		
		int outSize = (inBytes.length / 4 * 3) + ((inBytes.length % 4 == 0) ? 0 : 3);
		if(inBytes[inBytes.length-2] == '=')
			outSize--;
		if(inBytes[inBytes.length-1] == '=')
			outSize--;
		byte[] outBytes = new byte[outSize];
		
		int padding = 0;
		for(int i = 0; i < inBytes.length; i += 4)
		{
			int temp = (base64Charset.indexOf(inBytes[i], 0) & 0x0000003F);
			for(int j = 1; j < 4; j++)
			{
				temp <<= 6;
				if((i + j) < inBytes.length && inBytes[i + j] != '=')
					temp |= (base64Charset.indexOf(inBytes[i + j], 0) & 0x0000003F);
				else
					padding++;
			}
				
			for(int k = i / 4 * 3, count = 0; count < 3 - padding; count++)
				outBytes[k + count] = (byte)((temp >>> ((2 - count) * 8)) & 0x000000FF);
		}
		return outBytes;
	}
	
	public static String encode(final byte[] inBytes)
	{
		int padding = (inBytes.length % 3 == 0) ? 0 : (3 - (inBytes.length % 3));
		byte[] outBytes = new byte[(inBytes.length + padding) / 3 * 4];
		for(int i = 0; i < inBytes.length; i += 3)
		{
			int temp = (inBytes[i] << 16) & 0x00FFFFFF;
			for(int j = 1; j < 3; j++)
			{
				if(i + j < inBytes.length)
					temp |= ((inBytes[i + j] << ((2 - j) * 8)) & (0xFFFFFFFF >>> ((j + 1) * 8)));
			}

			for(int k = i / 3 * 4, count = 0; count < 4; count++)
			{
				if((i + 3) >= inBytes.length && count >= 2 && padding >= (4 - count))
					outBytes[k + count] = '=';
				else
					outBytes[k + count] = base64CharArray[(temp >>> ((3 - count) * 6)) & 0x0000003F];
			}
		}
		return new String(outBytes);
	}
	
}
