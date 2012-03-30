package in.net.pragya.infovault.library;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

public final class CryptoEngine
{
//    CryptoParams parameters;

    private CryptoEngine()
    {}

    /**
    * Encrypt the specified cleartext using the given password.
    * With the correct salt, number of iterations, and password, the decrypt() method reverses
    * the effect of this method.
    * This method generates and uses a random salt, and the user-specified number of iterations
    * and password to create a 16-byte secret key and 16-byte initialization vector.
    * The secret key and initialization vector are then used in the AES-128 cipher to encrypt
    * the given cleartext.
    *
    * @param salt
    *      salt that was used in the encryption (to be populated)
    * @param iterations
    *      number of iterations to use in salting
    * @param password
    *      password to be used for encryption
    * @param cleartext
    *      cleartext to be encrypted
    * @return
    *      ciphertext
     * @throws NoSuchAlgorithmException 
     * @throws UnsupportedEncodingException 
    * @throws Exception
    *      on any error encountered in encryption
    */
    public static byte[] encrypt(
            final String password,
            CryptoParams cryptParams,
            final byte[] cleartext)
            throws Exception
    {
       	SecureRandom.getInstance("SHA1PRNG").nextBytes(cryptParams.salt);
        PBEKeySpec pbeKeySpec = new PBEKeySpec(password.toCharArray(), cryptParams.salt, cryptParams.iterations, cryptParams.keySize);
        SecretKeyFactory keyFac = SecretKeyFactory.getInstance(CryptoParams.KEY_ALGORITHM);
        SecretKey pbeKey = keyFac.generateSecret(pbeKeySpec);

        final Cipher cipher = Cipher.getInstance(CryptoParams.CIPHER_ALGORITHM);

        cipher.init(
                Cipher.ENCRYPT_MODE,
                pbeKey,
                (IvParameterSpec)null);

        cryptParams.iv = cipher.getIV();

        return cipher.doFinal(cleartext);
    }


    /**
    * Decrypt the specified ciphertext using the given password.
    * With the correct salt, number of iterations, and password, this method reverses the effect
    * of the encrypt() method.
    * This method uses the user-specified salt, number of iterations, and password
    * to recreate the 16-byte secret key and 16-byte initialization vector.
    * The secret key and initialization vector are then used in the AES-128 cipher to decrypt
    * the given ciphertext.
    *
    * @param salt
    *      salt to be used in decryption
    * @param iterations
    *      number of iterations to use in salting
    * @param password
    *      password to be used for decryption
    * @param ciphertext
    *      ciphertext to be decrypted
    * @return
    *      cleartext
    * @throws Exception
    *      on any error encountered in decryption
    */
    public static byte[] decrypt(
            final String password,
            CryptoParams cryptParams,
            final byte[] ciphertext)
            throws Exception
    {
        PBEKeySpec pbeKeySpec = new PBEKeySpec(password.toCharArray(), cryptParams.salt, cryptParams.iterations, cryptParams.keySize);
        SecretKeyFactory sKeyFac = SecretKeyFactory.getInstance(CryptoParams.KEY_ALGORITHM);
        SecretKey pbeKey = sKeyFac.generateSecret(pbeKeySpec);

        final Cipher cipher = Cipher.getInstance(CryptoParams.CIPHER_ALGORITHM);
        
        cipher.init(Cipher.DECRYPT_MODE,
        		new SecretKeySpec(pbeKey.getEncoded(), CryptoParams.KEY_ALGORITHM),
        		new IvParameterSpec(cryptParams.iv));

        return cipher.doFinal(ciphertext);
    }
}