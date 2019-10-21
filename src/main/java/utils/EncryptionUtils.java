package utils;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.IvParameterSpec;
import java.math.BigInteger;
import java.util.Random;

public class EncryptionUtils {
    private static final BigInteger primeNumberP = BigInteger.valueOf(99991);
    private static final BigInteger primeNumberG = BigInteger.valueOf(11);
    private static Random random = new Random(System.currentTimeMillis());
    public static int privateKey = random.nextInt(10000) + 2;

    public static String generatePublicKey(){
        return primeNumberG.pow(privateKey).mod(primeNumberP).toString();
    }

    public static String generateSecreteKey(String publicKey){
        BigInteger key = BigInteger.valueOf(Long.parseLong(publicKey)).pow(privateKey).mod(primeNumberP);
        while (key.compareTo(BigInteger.valueOf(9999999)) < 0){
            key = key.multiply(BigInteger.valueOf(10L));
        }
        System.out.println(key);
        return key.toString();
    }

    public static byte[] DES_CBC_Encrypt(byte[] content, byte[] keyBytes) {

        try {
            DESKeySpec keySpec = new DESKeySpec(keyBytes);
            String algorithm =  "DES";//指定使什么样的算法
            SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(algorithm);
            SecretKey key = keyFactory.generateSecret(keySpec);

            String transformation = "DES/CBC/PKCS5Padding";
            Cipher cipher = Cipher.getInstance(transformation);
            cipher.init(Cipher.ENCRYPT_MODE, key, new IvParameterSpec(keySpec.getKey()));
            byte[] result = cipher.doFinal(content);
            return result;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static byte[] DES_CBC_Decrypt(byte[] content, byte[] keyBytes) {

        try {
            DESKeySpec keySpec = new DESKeySpec(keyBytes);


            String algorithm = "DES";
            SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(algorithm );
            SecretKey key = keyFactory.generateSecret(keySpec);

            String transformation = "DES/CBC/PKCS5Padding";
            Cipher cipher = Cipher.getInstance(transformation );
            cipher.init(Cipher.DECRYPT_MODE, key, new IvParameterSpec(keyBytes));
            byte[] result = cipher.doFinal(content);

            return result;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
