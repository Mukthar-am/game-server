package com.adof.gameserver.utils.generic;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

/**
 * Created by mukthar on 28/11/16.
 */
public class DecryptUtils {

//    private static byte[] encrypt(byte[] plain) throws Exception {
//        byte[] raw = secretKey.getBytes();
//        SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES/ECB/PKCS7Padding");
//        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS7Padding");
//        cipher.init(Cipher.ENCRYPT_MODE, skeySpec);
//        return cipher.doFinal(plain);
//    }
//
//    public static String AESDecrypt(String encryptMsg) throws Exception {
//        byte[] rawKey = secretKey.getBytes();
//        byte[] enc = Base64.decode(encryptMsg, 0);
//        byte[] result = decrypt(rawKey, enc);
//        return new String(result);
//    }

    private static byte[] decrypt(byte[] raw, byte[] encrypted) throws Exception {
        SecretKeySpec keySpec = new SecretKeySpec(raw, "AES/ECB/PKCS7Padding");
        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS7Padding");
        cipher.init(Cipher.DECRYPT_MODE, keySpec);
        return cipher.doFinal(encrypted);
    }



//
//
//
//    public static String getAPIKey(Context context) {
//        PrefHelper prefHelper = new PrefHelper(context);
//        String user_id = prefHelper.getString(PrefKeys.PREF_USER_ID_KEY, "");
//        long time = System.currentTimeMillis() / 1000;
//        Random random = new Random();
//        int n = 100000 + random.nextInt(900000);
//        String apiKey = time + "," + user_id + "," + n + "," + 1;
//        try {
//            byte[] encryptMsg = encrypt(apiKey.getBytes());
//            byte[] enc = Base64.encode(encryptMsg, Base64.NO_WRAP);
//            return new String(enc);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return null;
//    }




}
