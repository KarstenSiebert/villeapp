package com.siehog.ville.ui.scanner;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.NonNull;

public class ScanOverlay extends View {

    private final Paint backgroundPaint;
    private final Paint clearPaint;

    public ScanOverlay(Context context) {
        this(context, null);
    }

    public ScanOverlay(Context context, AttributeSet attrs) {
        super(context, attrs);

        backgroundPaint = new Paint();
        backgroundPaint.setColor(Color.parseColor("#80000000"));

        clearPaint = new Paint();
        clearPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));

        setLayerType(LAYER_TYPE_HARDWARE, null);
    }

    @Override
    protected void onDraw(@NonNull Canvas canvas) {
        super.onDraw(canvas);

        canvas.drawRect(0, 0, getWidth(), getHeight(), backgroundPaint);

        float left = (getWidth() - 512f) / 2f;
        float top = (getHeight() - 512f) / 2f;
        float right = left + 512f;
        float bottom = top + 512f;

        canvas.drawRect(left, top, right, bottom, clearPaint);
    }
}
