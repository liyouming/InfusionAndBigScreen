package com.fugao.newbigscreen.constant;

/** 公共类
 * Created by li on 2016/6/22.
 */
public class BigscreenConstant {
    public static String getHideName(String name){
        String hidename="";
        if(name.length()>=2){
            hidename=name.replace(name.charAt(1), '*');
        }else if(name.length()>=4){
            hidename=name.substring(0,4);
        }
        return hidename;
    }
}
