package in.net.pragya.infovault.library;

import java.io.IOException;

public class CryptoParams implements java.io.Serializable
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public static String KEY_ALGORITHM = "PBEWITHSHA256AND192BITAES";//PBKDF2WithHmacSHA1";
	public static String CIPHER_ALGORITHM = "AES/CBC/PKCS5Padding";
	
	public int keySize = 256;//Cipher.getMaxAllowedKeyLength(CIPHER_ALGORITHM);
	public int iterations = 20;

	public byte[] salt = new byte[keySize];    
	public byte[] iv = null;
	
	public CryptoParams()
	{
	}

	private void writeObject(java.io.ObjectOutputStream out) throws IOException
	{
		out.writeUTF(KEY_ALGORITHM);
		out.writeUTF(CIPHER_ALGORITHM);
		out.write(keySize);
		out.write(iterations);
		out.write(salt);
		out.write(iv);
	}

	private void readObject(java.io.ObjectInputStream in) throws IOException, ClassNotFoundException
	{
		KEY_ALGORITHM = in.readUTF();
		CIPHER_ALGORITHM = in.readUTF();
		keySize = in.read();
		iterations = in.read(); 
		in.read(salt);
		in.read(iv);
	}

}
