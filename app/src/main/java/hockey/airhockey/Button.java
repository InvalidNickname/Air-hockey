package hockey.airhockey;

import android.content.Context;
import android.graphics.Canvas;
import android.support.graphics.drawable.VectorDrawableCompat;

class Button {

    private final int left, right, top, bottom;
    private final VectorDrawableCompat normal, pressed;
    private boolean isPressed;

    Button(int resId, int pressedResId, Context context, int left, int right, int top, int bottom) {
        this.left = left;
        this.right = right;
        this.top = top;
        this.bottom = bottom;
        normal = VectorDrawableCompat.create(context.getResources(), resId, null);
        pressed = VectorDrawableCompat.create(context.getResources(), pressedResId, null);
        if (normal != null & pressed!= null) {
            normal.setBounds(left, top, right, bottom);
            pressed.setBounds(left, top, right, bottom);
        }
    }

    Button(int resId, Context context, int left, int right, int top, int bottom) {
        this.left = left;
        this.right = right;
        this.top = top;
        this.bottom = bottom;
        normal = VectorDrawableCompat.create(context.getResources(), resId, null);
        pressed = VectorDrawableCompat.create(context.getResources(), resId, null);
        if (normal != null & pressed!= null) {
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

    boolean isClicked(int x, int y) {
        return (y >= top & y <= bottom & x >= left & x <= right);
    }
}
