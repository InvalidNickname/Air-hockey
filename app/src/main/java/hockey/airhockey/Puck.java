package hockey.airhockey;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.graphics.drawable.VectorDrawableCompat;

import static hockey.airhockey.MainActivity.PAINT_FLAGS;
import static hockey.airhockey.MainActivity.settings;

class Puck {

    private final VectorDrawableCompat drawable;
    private final Bitmap shadow;
    private final double shadowSize;
    private final Paint bitmapPaint;
    double x, y;
    Vector v;

    Puck(int resId, Context context, long num) {
        drawable = VectorDrawableCompat.create(context.getResources(), resId, null);
        shadowSize = settings.width / 130;
        bitmapPaint = new Paint(PAINT_FLAGS);
        shadow = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(context.getResources(), R.drawable.shadow), //
                (int) (2 * settings.puckScale + 2 * shadowSize), (int) (2 * settings.puckScale + 2 * shadowSize), true);
        v = new Vector(0, 0);
        if (num == 1) {
            y = settings.height / 3;
        } else if (num == 2) {
            y = settings.height * (2 / 3d);
        }
        x = settings.width / 2;
        if (drawable != null) {
            drawable.setBounds((int) x - settings.puckScale, (int) y - settings.puckScale, (int) x + settings.puckScale, (int) y + settings.puckScale);
        }
    }

    Puck(int resId, Context context) {
        drawable = VectorDrawableCompat.create(context.getResources(), resId, null);
        shadowSize = settings.width / 130;
        bitmapPaint = new Paint(PAINT_FLAGS);
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.RGB_565;
        shadow = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(context.getResources(), R.drawable.shadow, options), //
                (int) (2 * settings.puckScale + 2 * shadowSize), (int) (2 * settings.puckScale + 2 * shadowSize), true);
        v = new Vector(0, 0);
        y = settings.height / 2;
        x = settings.width / 2;
        if (drawable != null) {
            drawable.setBounds((int) x - settings.puckScale, (int) y - settings.puckScale, (int) x + settings.puckScale, (int) y + settings.puckScale);
        }
    }

    void update(long delta, boolean isAnimation) {
        if (!isAnimation) {
            v.setVector(v.x, v.y);
            if (v.v * delta > settings.playerScale * 2) {
                v.setVector(settings.playerScale * 2 * v.cos / delta, settings.playerScale * 2 * v.sin / delta);
            }
        }
        x += v.x * delta;
        y += v.y * delta;
        if (settings.friction & !isAnimation) {
            v = v.multiplyVector(settings.frictionValue);
        }
        drawable.setBounds((int) x - settings.puckScale, (int) y - settings.puckScale, (int) x + settings.puckScale, (int) y + settings.puckScale);
    }

    void draw(Canvas canvas) {
        canvas.drawBitmap(shadow, (int) (x - settings.puckScale - shadowSize), (int) (y - settings.puckScale - shadowSize), bitmapPaint);
        drawable.draw(canvas);
    }
}
