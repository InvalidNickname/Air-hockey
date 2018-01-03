package hockey.airhockey;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.support.graphics.drawable.VectorDrawableCompat;
import android.util.Log;
import android.util.SparseArray;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import static hockey.airhockey.MainActivity.height;
import static hockey.airhockey.MainActivity.playerScale;
import static hockey.airhockey.MainActivity.puckScale;
import static hockey.airhockey.MainActivity.width;

public class GameField extends SurfaceView implements Runnable {

    Thread thread;
    SurfaceHolder holder;
    boolean isDrawing, isDragging1, isDragging2;
    VectorDrawableCompat background, player1, player2,puck;
    Paint paint = new Paint();
    int player1X, player2X, player1Y, player2Y, x, y,puckX,puckY;
    float xp1, xp2, yp1, yp2, v1, v2;
    long sec, psec;
    private SparseArray<PointF> activePointers;
    int dragPointer1, dragPointer2;

    public GameField(Context context) {
        super(context);
        thread = new Thread();
        holder = getHolder();
        activePointers = new SparseArray<>();
        paint.setAntiAlias(true);
        background = VectorDrawableCompat.create(context.getResources(), R.drawable.background, null);
        if (background != null) {
            background.setBounds(0, 0, width, MainActivity.height);
        }
        player1 = VectorDrawableCompat.create(context.getResources(), R.drawable.player, null);
        player1X = width / 2;
        player1Y = 2 * playerScale;
        if (player1 != null) {
            player1.setBounds(player1X - playerScale, player1Y - playerScale, player1X + playerScale, player1Y + playerScale);
        }
        player2 = VectorDrawableCompat.create(context.getResources(), R.drawable.player, null);
        player2X = width / 2;
        player2Y = height - 2 * playerScale;
        if (player2 != null) {
            player2.setBounds(player2X - playerScale, player2Y - playerScale, player2X + playerScale, player2Y + playerScale);
        }
        puck = VectorDrawableCompat.create(context.getResources(), R.drawable.puck, null);
        puckX = width / 2;
        puckY = height/2;
        if (puck != null) {
            puck.setBounds(puckX - puckScale, puckY - puckScale, puckX + puckScale, puckY + puckScale);
        }
        dragPointer1 = -1;
        dragPointer2 = -1;
        xp1 = player1X;
        yp1 = player1Y;
        xp2 = player2X;
        yp2 = player2Y;
        psec = System.currentTimeMillis();
        thread.start();
    }

    private void update() {
        player1.setBounds(player1X - playerScale, player1Y - playerScale, player1X + playerScale, player1Y + playerScale);
        player2.setBounds(player2X - playerScale, player2Y - playerScale, player2X + playerScale, player2Y + playerScale);
        puck.setBounds(puckX - puckScale, puckY - puckScale, puckX + puckScale, puckY + puckScale);
        sec = System.currentTimeMillis();
        v1 = (float) Math.sqrt(Math.pow((player1X - xp1) / (sec - psec), 2) + Math.pow((player1Y - yp1) / (sec - psec), 2));
        v2 = (float) Math.sqrt(Math.pow((player2X - xp2) / (sec - psec), 2) + Math.pow((player2Y - yp2) / (sec - psec), 2));
        xp1 = player1X;
        yp1 = player1Y;
        xp2 = player2X;
        yp2 = player2Y;
        psec = sec;
    }

    @Override
    public void run() {
        while (isDrawing) {
            if (holder.getSurface().isValid()) {
                Canvas canvas = holder.lockCanvas();
                update();
                drawOnCanvas(canvas);
                holder.unlockCanvasAndPost(canvas);
            }
        }
    }

    private boolean isInside(int playerX, int playerY) {
        return Math.sqrt(Math.pow(playerX - x, 2) + Math.pow(playerY - y, 2)) < playerScale;
    }

    private int checkX(int playerX) {
        if (playerX < playerScale) {
            playerX = playerScale;
        } else if (playerX > width - playerScale) {
            playerX = width - playerScale;
        }
        return playerX;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int pointerIndex = event.getActionIndex();
        int pointerId = event.getPointerId(pointerIndex);
        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_POINTER_DOWN:
                PointF pointF = new PointF();
                pointF.x = event.getX(pointerIndex);
                pointF.y = event.getY(pointerIndex);
                activePointers.put(pointerId, pointF);
                x = (int) pointF.x;
                y = (int) pointF.y;
                if (isInside(player1X, player1Y)) {
                    isDragging1 = true;
                    dragPointer1 = pointerId;
                }
                if (isInside(player2X, player2Y)) {
                    isDragging2 = true;
                    dragPointer2 = pointerId;
                }
                break;
            case MotionEvent.ACTION_MOVE:
                int pointerCount = event.getPointerCount();
                for (int i = 0; i < pointerCount; i++) {
                    pointerIndex = i;
                    pointerId = event.getPointerId(pointerIndex);
                    PointF point = activePointers.get(event.getPointerId(i));
                    if (point != null) {
                        point.x = event.getX(i);
                        point.y = event.getY(i);
                        if (isDragging1 & (pointerId == dragPointer1)) {
                            player1X = (int) point.x;
                            player1Y = (int) point.y;
                        }
                        if (isDragging2 & (pointerId == dragPointer2)) {
                            player2X = (int) point.x;
                            player2Y = (int) point.y;
                        }
                    }
                }
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_POINTER_UP:
            case MotionEvent.ACTION_CANCEL:
                if (pointerId == dragPointer1) {
                    isDragging1 = false;
                    activePointers.remove(dragPointer1);
                    dragPointer1 = -1;
                }
                if (pointerId == dragPointer2) {
                    isDragging2 = false;
                    activePointers.remove(dragPointer2);
                    dragPointer2 = -1;
                }
                break;
        }
        player1X = checkX(player1X);
        if (player1Y < playerScale) {
            player1Y = playerScale;
        } else if (player1Y > height / 2 - playerScale) {
            player1Y = height / 2 - playerScale;
        }
        player2X = checkX(player2X);
        if (player2Y > height - playerScale) {
            player2Y = height - playerScale;
        } else if (player2Y < height / 2 + playerScale) {
            player2Y = height / 2 + playerScale;
        }
        return true;
    }

    private void drawOnCanvas(Canvas canvas) {
        canvas.drawColor(Color.WHITE);
        background.draw(canvas);
        player1.draw(canvas);
        player2.draw(canvas);
        puck.draw(canvas);
    }

    public void pauseDrawing() {
        isDrawing = false;
        try {
            thread.join();
        } catch (InterruptedException e) {
            Log.e(MainActivity.TAG, e.getMessage());
        }
    }

    public void resumeDrawing() {
        isDrawing = true;
        thread = new Thread(this);
        thread.start();
    }
}
