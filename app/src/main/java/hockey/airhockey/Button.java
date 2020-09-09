/*
 * Created by Alexey Kiselev
 * Copyright (c) 2018 . All rights reserved.
 * Last modified 30.06.18 14:00
 */

package hockey.airhockey;

import android.content.Context;
import android.graphics.Canvas;
import androidx.vectordrawable.graphics.drawable.VectorDrawableCompat;

class Button {

    private final int left, right, top, bottom, padding;
    private final VectorDrawableCompat normal, pressed;
    private boolean isPressed;

    Button(int resId, int pressedResId, Context context, int left, int right, int top, int bottom, int padding) {
        this.left = left;
        this.right = right;
        this.top = top;
        this.bottom = bottom;
        this.padding = padding;
        normal = VectorDrawableCompat.create(context.getResources(), resId, null);
        pressed = VectorDrawableCompat.create(context.getResources(), pressedResId, null);
        if (normal != null & pressed != null) {
            normal.setBounds(left, top, right, bottom);
            pressed.setBounds(left, top, right, bottom);
        }
    }

    Button(int resId, Context context, int left, int right, int top, int bottom, int padding) {
        this.left = left;
        this.right = right;
        this.top = top;
        this.bottom = bottom;
        this.padding = padding;
        normal = VectorDrawableCompat.create(context.getResources(), resId, null);
        pressed = VectorDrawableCompat.create(context.getResources(), resId, null);
        if (normal != null & pressed != null) {
            normal.setBounds(left, top, right, bottom);
            pressed.setBounds(left, top, right, bottom);
        }
    }

    void draw(Canvas canvas) {
        if (isPressed) {
            pressed.draw(canvas);
        } else {
            normal.draw(canvas);
        }
    }

    void setPressed(boolean bool) {
        isPressed = bool;
    }

    boolean isClicked(double x, double y) {
        return (y >= top - padding & y <= bottom + padding & x >= left - padding & x <= right + padding);
    }
}
