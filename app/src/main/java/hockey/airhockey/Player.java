package hockey.airhockey;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.graphics.drawable.VectorDrawableCompat;

import static hockey.airhockey.MainActivity.PAINT_FLAGS;
import static hockey.airhockey.MainActivity.settings;

class Player {

    final Vector v;
    private final VectorDrawableCompat drawable;
    private final Bitmap shadow;
    private final double shadowSize;
    private final Paint bitmapPaint;
    double x, y;
    private double xp, yp;

    Player(int resId, Context context, int num) {
        drawable = VectorDrawableCompat.create(context.getResources(), resId, null);
        shadowSize = settings.width / 130;
        bitmapPaint = new Paint(PAINT_FLAGS);
        shadow = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(context.getResources(), R.drawable.shadow), //
                (int) (2 * settings.playerScale + 2 * shadowSize), (int) (2 * settings.playerScale + 2 * shadowSize), true);
        v = new Vector(0, 0);
        x = settings.width / 2;
        if (num == 1) {
            y = (int) (1.4 * settings.playerScale);
        } else {
            y = settings.height - (int) (1.4 * settings.playerScale);
        }
        if (drawable != null) {
            drawable.setBounds((int) x - settings.playerScale, (int) y - settings.playerScale, (int) x + settings.playerScale, (int) y + settings.playerScale);
        }
        xp = x;
        yp = y;
    }

    void draw(Canvas canvas) {
        canvas.drawBitmap(shadow, (int) (x - settings.playerScale - shadowSize), (int) (y - settings.playerScale - shadowSize), bitmapPaint);
        drawable.draw(canvas);
    }

    void update(long delta, boolean isAnimation) {
        if (isAnimation) {
            x += v.x * delta;
            y += v.y * delta;
        }
        drawable.setBounds((int) x - settings.playerScale, (int) y - settings.playerScale, (int) x + settings.playerScale, (int) y + settings.playerScale);
    }

    void setV(long delta) {
        v.x = (x - xp) / delta;
        v.y = (y - yp) / delta;
        v.setVector(v.x, v.y);
        xp = x;
        yp = y;
    }

}
