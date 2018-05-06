package hockey.airhockey;

import android.content.Context;
import android.graphics.Canvas;
import android.support.graphics.drawable.VectorDrawableCompat;

import static hockey.airhockey.MainActivity.gateHeight;
import static hockey.airhockey.MainActivity.height;
import static hockey.airhockey.MainActivity.width;

class Gate {

    final int leftCorner, rightCorner;
    private final VectorDrawableCompat drawable;

    Gate(int resId, Context context, int num) {
        drawable = VectorDrawableCompat.create(context.getResources(), resId, null);
        leftCorner = (int) (width * 0.26);
        rightCorner = (int) (width * 0.74);
        if (drawable != null) {
            if (num == 1) {
                drawable.setBounds(leftCorner, height - gateHeight, rightCorner, height);
            } else {
                drawable.setBounds(leftCorner, 0, rightCorner, gateHeight);
            }
        }
    }

    void draw(Canvas canvas) {
        drawable.draw(canvas);
    }

}
