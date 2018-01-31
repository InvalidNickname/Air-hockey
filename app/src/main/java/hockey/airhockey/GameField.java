package hockey.airhockey;

import android.annotation.SuppressLint;
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

    private Thread thread;
    private SurfaceHolder holder;
    private boolean isDrawing, isDragging1, isDragging2;
    private VectorDrawableCompat background;
    private int x, y;
    private Player player1, player2;
    private Puck puck;
    private long psec;
    private SparseArray<PointF> activePointers;
    private int dragPointer1, dragPointer2;

    public GameField(Context context) {
        super(context);
        thread = new Thread();
        holder = getHolder();
        activePointers = new SparseArray<>();
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        background = VectorDrawableCompat.create(context.getResources(), R.drawable.background, null);
        if (background != null) {
            background.setBounds(0, 0, width, MainActivity.height);
        }
        player1 = new Player(R.drawable.player, context, 1);
        player2 = new Player(R.drawable.player, context, 2);
        puck = new Puck(R.drawable.puck, context);
        dragPointer1 = -1;
        dragPointer2 = -1;
        psec = System.currentTimeMillis();
        thread.start();
    }

    private void update() {
        long sec = System.currentTimeMillis();
        player1.setV(sec, psec);
        player2.setV(sec, psec);
        checkCollision();
        player1.update();
        player2.update();
        puck.update(sec, psec);
        psec = sec;
    }

    private void checkCollision() {
        if (puck.y < puckScale) {
            puck.y = puckScale;
            puck.vY = -puck.vY;
        } else if (puck.y > height - puckScale) {
            puck.y = height - puckScale;
            puck.vY = -puck.vY;
        }
        if (puck.x < puckScale) {
            puck.x = puckScale;
            puck.vX = -puck.vX;
        } else if (puck.x > width - puckScale) {
            puck.x = width - puckScale;
            puck.vX = -puck.vX;
        }
        if (Math.sqrt(Math.pow(puck.x - player1.x, 2) + Math.pow(puck.y - player1.y, 2)) < playerScale + puckScale) {
            collisionWithPuck(player1);
        }
        if (Math.sqrt(Math.pow(puck.x - player2.x, 2) + Math.pow(puck.y - player2.y, 2)) < playerScale + puckScale) {
            collisionWithPuck(player2);
        }
    }

    private void collisionWithPuck(Player player) {
        //puck.vX=((puck.mass-player.mass)*puck.vX+2*player.mass*player.vX)/(player.mass+puck.mass);
        //puck.vY=((puck.mass-player.mass)*puck.vY+2*player.mass*player.vY)/(player.mass+puck.mass);
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

    @SuppressLint("ClickableViewAccessibility")
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
                if (isInside(player1.x, player1.y)) {
                    isDragging1 = true;
                    dragPointer1 = pointerId;
                }
                if (isInside(player2.x, player2.y)) {
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
                            player1.x = (int) point.x;
                            player1.y = (int) point.y;
                        }
                        if (isDragging2 & (pointerId == dragPointer2)) {
                            player2.x = (int) point.x;
                            player2.y = (int) point.y;
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
        player1.x = checkX(player1.x);
        if (player1.y < playerScale) {
            player1.y = playerScale;
        } else if (player1.y > height / 2 - playerScale) {
            player1.y = height / 2 - playerScale;
        }
        player2.x = checkX(player2.x);
        if (player2.y > height - playerScale) {
            player2.y = height - playerScale;
        } else if (player2.y < height / 2 + playerScale) {
            player2.y = height / 2 + playerScale;
        }
        return true;
    }

    private void drawOnCanvas(Canvas canvas) {
        canvas.drawColor(Color.WHITE);
        background.draw(canvas);
        player1.drawPlayer(canvas);
        player2.drawPlayer(canvas);
        puck.drawPuck(canvas);
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
