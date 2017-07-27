package com.fugao.bigscreen;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.GridView;

public class StandardGridView extends GridView {

    public StandardGridView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public StandardGridView(Context context) {
        super(context);
    }

    public StandardGridView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int expandSpec = MeasureSpec.makeMeasureSpec
                (Integer.MAX_VALUE >> 2, MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, expandSpec);
    }

}
