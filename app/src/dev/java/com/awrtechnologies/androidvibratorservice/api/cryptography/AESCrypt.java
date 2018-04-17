package com.awrtechnologies.androidvibratorservice.api.cryptography;

public class AESCrypt {
    private static final String SALT = "E0A353A4647B99F22482736C523927F571DA67E47DA5E2298B65432654E2AD7C1A0D4222A5DA0A456E382240976E01F5";
    private static final int KEY_SIZE = 128;
    private static final int ITERATION_COUNT = 200;

    public static AESCryptResult encrypt(String message, String key,String IV){
        try{
            AesUtil util = new AesUtil(KEY_SIZE, ITERATION_COUNT);
            String result = util.encrypt(SALT, IV, key, message);
            AESCryptResult aesCryptResult = new AESCryptResult();
            aesCryptResult.success = true;
            aesCryptResult.output = result;
            return aesCryptResult;
        } catch (Exception e) {
            AESCryptResult aesCryptResult = new AESCryptResult();
            aesCryptResult.success = false;
            aesCryptResult.output = "Error";
            return aesCryptResult;
        }
    }
    public static AESCryptResult decrypt(String CIPHER_TEXT, String key, String IV){

        try {
            AesUtil util = new AesUtil(KEY_SIZE, ITERATION_COUNT);
            String decrypt = util.decrypt(SALT, IV, key, CIPHER_TEXT);
            AESCryptResult aesCryptResult = new AESCryptResult();
            aesCryptResult.success = true;
            aesCryptResult.output = decrypt;
            return aesCryptResult;
        } catch (Exception e) {
            AESCryptResult aesCryptResult = new AESCryptResult();
            aesCryptResult.success = false;
            aesCryptResult.output = "Error";
            return aesCryptResult;
        }
    }
}