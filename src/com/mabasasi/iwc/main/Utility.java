package com.mabasasi.iwc.main;


public class Utility {
    
    /**
     * 意味のある文字列かどうかの判定.
     * @param str 対象文字列
     * @return null、空の場合、false
     */
    public static boolean isSignificantString(String str){
        if (str != null){
            str = str.replace(" ", "").replace("　", "");
            if (!"".equals(str)){
                return true;
            }
        }
        return false;
    }
}
