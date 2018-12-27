package com.example.rychou.finalapp;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;

public class DbSchema {
    public static final class UserTable {
        public static final String NAME = "users";

        public static final class Cols{
            public static final String USERNAME = "username";
            public static final String PASSWORD = "password";
            public static final String SALT = "salt";
        }
    }

    public static final class CostTable {
        public static final String NAME = "cost";

        public static final class Cols{
            public static final String ID = "_id";
            public static final String TYPE = "Type";//名称（如用餐、服饰）
            public static final String TIME = "Time";
            public static final String FEE = "Fee";//金额
            public static final String WAY = "Way";//支付方式
            public static final String BUDGET = "Budget";//支出还是收入
            public static final String COMMENT = "Comment";//文字备注
            public static final String IMG_DATA = "img";

        }
    }

    public static String getMD5(String content) {
        byte[] hash;
        try {
            hash = MessageDigest.getInstance("MD5").digest(content.getBytes("UTF-8"));
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("NoSuchAlgorithmException",e);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("UnsupportedEncodingException", e);
        }

        StringBuilder hex = new StringBuilder(hash.length * 2);
        for (byte b : hash) {
            if ((b & 0xFF) < 0x10){
                hex.append("0");
            }
            hex.append(Integer.toHexString(b & 0xFF));
        }
        return hex.toString();
    }

    public static String createSalt(){
        return UUID.randomUUID().toString().substring(0,5);
    }
}
