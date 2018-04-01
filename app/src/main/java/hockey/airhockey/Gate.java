package hockey.airhockey;

import android.content.Context;
import android.graphics.Canvas;
import android.support.graphics.drawable.VectorDrawableCompat;

import static hockey.airhockey.GameActivity.gateHeight;
import static hockey.airhockey.GameActivity.height;
import static hockey.airhockey.GameActivity.width;

class Gate {

    int leftCorner, rightCorner;
    private boolean isUpper;
    private VectorDrawableCompat drawable;

    Gate(int resId, Context context, int num) {
        drawable = VectorDrawableCompat.create(context.getResources(), resId, null);
        leftCorner = width / 4;
        rightCorner = 3 * width / 4;
        if (drawable != null) {
            if (num == 1) {
                isUpper = false;
                drawable.setBounds(leftCorner, height - gateHeight, rightCorner, height + gateHeight);
            } else {
                isUpper = true;
                drawable.setBounds(leftCorner, -gateHeight, rightCorner, gateHeight);
            }
        }
    }

    void update() {
        if (drawable != null) {
            if (!isUpper) {
                drawable.setBounds(leftCorner, height - gateHeight, rightCorner, height + gateHeight);
            } else {
                drawable.setBounds(leftCorner, -gateHeight, rightCorner, gateHeight);
            }
        }
    }

    void drawGate(Canvas canvas) {
        drawable.draw(canvas);
    }

}
