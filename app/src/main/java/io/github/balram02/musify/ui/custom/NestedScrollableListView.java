package io.github.balram02.musify.ui.custom;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ListView;

public class NestedScrollableListView extends ListView {

    private int height;

    public NestedScrollableListView(Context context) {
        super(context);
    }

    public NestedScrollableListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public NestedScrollableListView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        height = View.MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2, View.MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }
}
