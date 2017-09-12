package com.swen;

public class TransientSetting
{
    private static boolean noImage = false;
    private static boolean nightMode = false;

    public static boolean isNoImage() { return noImage; }
    public static void setNoImage(boolean _noImage) { noImage = _noImage; }

    public static boolean isNightMode() { return nightMode; }
    public static void setNightMode(boolean _nightMode) { nightMode = _nightMode; }
}
