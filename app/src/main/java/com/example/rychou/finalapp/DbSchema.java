package com.example.rychou.finalapp;

public class DbSchema {
    public static final class UserTable {
        public static final String NAME = "users";

        public static final class Cols{
            public static final String USERNAME = "username";
            public static final String PASSWORD = "password";
        }
    }

    public static final class CostTable {
        public static final String NAME = "cost";

        public static final class Cols{
            public static final String TYPE = "Type";//名称（如用餐、服饰）
            public static final String TIME = "Time";
            public static final String FEE = "Fee";//金额
            public static final String WAY = "Way";//支付方式
            public static final String BUDGET = "Budget";//支出还是收入
            public static final String COMMENT = "Comment";//文字备注

        }
    }
}
