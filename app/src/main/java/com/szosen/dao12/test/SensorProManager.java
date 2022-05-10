package com.szosen.dao12.test;

public class SensorProManager {
    private static SensorProManager manager = new SensorProManager();
    public static SensorProManager getInstance() {
        return manager;
    }

    public Test getCustomProvider(){
        return new Test();
    }


}
