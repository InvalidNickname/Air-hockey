/*
 * Created by Alexey Kiselev
 * Copyright (c) 2018 . All rights reserved.
 * Last modified 30.06.18 14:00
 */

package hockey.airhockey;

import android.content.Context;
import android.graphics.Canvas;
import android.support.graphics.drawable.VectorDrawableCompat;

import static hockey.airhockey.MainActivity.settings;

class Gate {

    final int leftCorner, rightCorner;
    private final VectorDrawableCompat drawable;

    Gate(int resId, Context context, int num) {
        drawable = VectorDrawableCompat.create(context.getResources(), resId, null);
        leftCorner = (int) (settings.width * 0.26);
        rightCorner = (int) (settings.width * 0.74);
        if (drawable != null) {
            if (num == 1) {
                drawable.setBounds(leftCorner, settings.height - settings.gateHeight, rightCorner, settings.height);
            } else {
                drawable.setBounds(leftCorner, 0, rightCorner, settings.gateHeight);
            }
        }
    }

    void draw(Canvas canvas) {
        drawable.draw(canvas);
    }

}
