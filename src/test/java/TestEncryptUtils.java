import org.junit.Test;
import utils.EncryptionUtils;
import static org.junit.Assert.*;
import java.math.BigInteger;
import java.util.Base64;

public class TestEncryptUtils {
    private String test1 = "23333";
    @Test
    public void TestGenerate(){
        EncryptionUtils.privateKey = 23333;
        String p1 = EncryptionUtils.generatePublicKey();
        EncryptionUtils.privateKey = 23334;
        String p2 = EncryptionUtils.generatePublicKey();
        EncryptionUtils.privateKey =23333;
        String k1 = EncryptionUtils.generateSecreteKey(p2);
        EncryptionUtils.privateKey = 23334;
        String k2 = EncryptionUtils.generateSecreteKey(p1);
        System.out.println(k1);
        System.out.println(k2);
        assertEquals(k1,k2);
    }

    @Test
    public void TestDES(){

        //生成公钥
        String publicKey = EncryptionUtils.generatePublicKey();
        //生成加密用key
        String commonKey = EncryptionUtils.generateSecreteKey(publicKey);
        //加密文本
        byte[] encryptedText = EncryptionUtils.DES_CBC_Encrypt(test1.getBytes(), commonKey.getBytes());
        //将加密文本base64化方便传输
        String a = Base64.getEncoder().encodeToString(encryptedText);
        //输出加密后的东西
        System.out.println(a);
        //解密
        byte[] decryptedText = EncryptionUtils.DES_CBC_Decrypt(Base64.getDecoder().decode(a), commonKey.getBytes());
        //输出解密后的文本
        System.out.println(test1);
        System.out.println(new String(decryptedText));
        assertEquals(test1, new String(decryptedText));
    }

}
