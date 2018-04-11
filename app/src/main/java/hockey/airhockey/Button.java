package hockey.airhockey;

import android.content.Context;
import android.graphics.Canvas;
import android.support.graphics.drawable.VectorDrawableCompat;

class Button {

    int left, right, top, bottom;
    private VectorDrawableCompat drawable;

    Button(int resId, Context context, int left, int right, int top, int bottom) {
        this.left = left;
        this.right = right;
        this.top = top;
        this.bottom = bottom;
        drawable = VectorDrawableCompat.create(context.getResources(), resId, null);
        if (drawable != null) {
            drawable.setBounds(left, top, right, bottom);
        }
    }

    void draw(Canvas canvas) {
        drawable.draw(canvas);
    }

    boolean isClicked(int x, int y) {
        return (y >= top & y <= bottom & x >= left & x <= right);
    }
}
