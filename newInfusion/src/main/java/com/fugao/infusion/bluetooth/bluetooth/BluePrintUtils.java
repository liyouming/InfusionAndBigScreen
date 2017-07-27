package com.fugao.infusion.bluetooth.bluetooth;

/**
 * Created by fgmh on 14/10/10.
 */
public class BluePrintUtils {


    /**
     * 设置文字的大小 16x16为1  24x24为0
     * @param size
     * @return
     */
    public static byte[] getQSOrderSize(int size){
        byte[] bt = new byte[3];
        bt[0] = 27;
        bt[1] = 77;
        bt[2] = (byte)size;
        return bt;
    }
    /**
     * 设置距离左边的边距
     * @param length
     * @return
     */
    public static byte[] getQSOrderPaddingLeft( int length){

        byte[] bt = new byte[3];
        bt[0] = 27;
        bt[1] = 108;
        bt[2] = (byte)length;
        return bt;
    }

    /**
     * 设置距离右边的边距
     * @param length
     * @return
     */
    public static byte[] getQSOrderPaddingRight(int length){
        byte[] bt = new byte[3];
        bt[0] = 27;
        bt[1] = 32;
        bt[2] = (byte)length;
        return bt;
    }
    /**
     * 设置距离行的边距
     * @param length
     * @return
     */
    public static byte[] getQSOrderPaddingRow(int length){
        byte[] bt = new byte[3];
        bt[0] = 27;
        bt[1] = 51;
        bt[2] = (byte)length;
        return bt;
    }

    /**
     * 设置对齐方式 0左对齐 1居中 2右对齐
     * @param length
     * @return
     */
    public static byte[] getQSOrderPositionWay(int length){
        byte[] bt = new byte[3];
        bt[0] = 27;
        bt[1] = 97;
        bt[2] = (byte)length;
        return bt;
    }
    /**
     * 设置打印图片
     * @param length
     * @return
     */
    public static byte[] getQSOrderPicture(int length){
        byte[] bt = new byte[4];
        bt[0] = 31;
        bt[1] = 16;
        bt[2] = (byte)length;
        bt[3] = 0;
        return bt;
    }
    /**
     * 设置字符大小  0为正常字符  17为两倍
     * @param length
     * @return
     */
    public static byte[] getQSOrderCharacterSize(int length){
        byte[] bt = new byte[3];
        bt[0] = 29;
        bt[1] = 33;
        bt[2] = (byte)length;
        return bt;
    }

    /***
     * 设置蓝牙打印机的字体是否为粗体 1为允许粗体打印  0为禁止粗体打印
     * @param length
     * @return
     */
    public static byte[] allowBoldSize(int length){
        byte[] bt = new byte[3];
        bt[0] = 27;
        bt[1] = 69;
        bt[2] = (byte)length;
        return bt;
    }

    /**
     * 设置换行
     * @return
     */
    public static byte[] useNewline(){
        byte[] bt = new byte[1];
        bt[0] = 10;
        return bt;
    }

    /**
     * 间隙打印机设置打印下一张纸
     * @return
     */
    public static byte[] printNextPage(){
        byte[] bt = new byte[1];
        bt[0] = 12;
        return bt;
    }
}
