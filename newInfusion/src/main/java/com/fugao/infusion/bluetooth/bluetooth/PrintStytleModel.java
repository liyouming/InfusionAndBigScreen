package com.fugao.infusion.bluetooth.bluetooth;

import java.io.Serializable;

/**
 * Created by fgmh on 14/10/10.
 */
public class PrintStytleModel implements Serializable {

    public int type;
    public int size;
    public int padingLeft;
    public int paddingRow;
    public String content;

    public PrintStytleModel(int type, int size, int padingLeft, int paddingRow, String content) {
        this.type = type;
        this.size = size;
        this.padingLeft = padingLeft;
        this.paddingRow = paddingRow;
        this.content = content;
    }
    public PrintStytleModel(String content) {
        this.content=content;
    }

    /**
     *   标题 标题字体大小。
     *
     *   内容
     *     文本：字体大小， 左边句，右边距
     *     非文本：二维码内容， 左边距 ，右边距
     *   底部：
     *
     */
    //    }

}
