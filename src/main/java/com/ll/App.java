package com.ll;

public class App {
    public static final String BASE_PACKAGE_PATH = "com.ll";
    public static String DB_HOST = "localhost";
    public static int DB_PORT = 3306;
    public static String DB_ID = "root";
    public static String DB_PASSWORD = "";
    public static String DB_NAME = "mvc__dev";
    public static boolean isProd = false;

    static {
        String profilesActive = System.getenv("SPRING_PROFILES_ACTIVE");

        if (profilesActive != null && profilesActive.equals("production")) {
            isProd = true;
        }

        if (isProd) {
            // 운영DB 정보
            DB_HOST = "172.17.0.1";
            DB_PORT = 3306;
            DB_ID = "lldjlocal";
            DB_PASSWORD = "1234";
            DB_NAME = "mvc__prod";
        }
    }
}
