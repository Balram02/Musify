package io.github.balram02.musify.ui;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;

import androidx.appcompat.widget.AppCompatImageView;

public class ResponsiveImageView extends AppCompatImageView {

    private float radius = 18.0f;
    private RectF rect;
    private Paint paint;

    public ResponsiveImageView(Context context) {
        super(context);
        init();
    }

    public ResponsiveImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ResponsiveImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public void init() {
        rect = new RectF(0, 0, this.getWidth(), this.getHeight());
        paint = new Paint();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width = getMeasuredWidth();
        width *= 0.9;
        setMeasuredDimension(width, width);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawRoundRect(rect, radius, radius, paint);
        super.onDraw(canvas);
    }
}
