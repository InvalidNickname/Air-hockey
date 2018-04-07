package hockey.airhockey;

import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.support.graphics.drawable.VectorDrawableCompat;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import static hockey.airhockey.MainActivity.height;
import static hockey.airhockey.MainActivity.playerScale;
import static hockey.airhockey.MainActivity.puckScale;
import static hockey.airhockey.MainActivity.startAnimStopTime;
import static hockey.airhockey.MainActivity.width;

public class GameCustomField extends SurfaceView implements Runnable {

    private Thread thread;
    private Context context;
    private SurfaceHolder holder;
    private boolean isDrawing, isSpeedSet;
    private VectorDrawableCompat background, startButton;
    private Player player1, player2;
    private Gate lowerGate, upperGate;
    private Puck puck;
    private long psec;
    long delta;

    public GameCustomField(Context context) {
        super(context);
        this.context = context;
        thread = new Thread();
        holder = getHolder();
        loadGraphics();
        isSpeedSet = false;
        thread.start();
    }

    // обновление игры
    private void update() {
        long sec = System.currentTimeMillis();
        delta = sec - psec;
        psec = sec;
        if (!isSpeedSet & delta < 1000) {
            isSpeedSet = true;
            puck.v.setVector((width / 2d - puck.x) / startAnimStopTime, 0);
            player1.v.setVector((width / 2d - player1.x) / startAnimStopTime, 0);
            player2.v.setVector((width / 2d - player2.x) / startAnimStopTime, 0);
        }
        player1.update(delta, true);
        player2.update(delta, true);
        puck.update(delta, true);
        if (Math.abs(puck.x - width / 2) <= puckScale / 2) {
            player1 = new Player(R.drawable.player, context, 1);
            player2 = new Player(R.drawable.player, context, 2);
            puck = new Puck(R.drawable.puck, context);
        }
    }

    // загрузка графики
    private void loadGraphics() {
        background = VectorDrawableCompat.create(context.getResources(), R.drawable.background, null);
        if (background != null) {
            background.setBounds(0, 0, width, height);
        }
        startButton = VectorDrawableCompat.create(context.getResources(), R.drawable.start_button, null);
        if (startButton != null) {
            startButton.setBounds(width - 100, height / 2 - 120, width, height / 2 + 120);
        }
        player1 = new Player(R.drawable.player, context, 1);
        player1.x = width + playerScale * 2;
        player2 = new Player(R.drawable.player, context, 2);
        player2.x = width + playerScale * 2;
        puck = new Puck(R.drawable.puck, context);
        puck.x = width + puckScale * 2;
        lowerGate = new Gate(R.drawable.lower_gate, context, 1);
        upperGate = new Gate(R.drawable.upper_gate, context, 2);
    }

    // рисование
    private void drawOnCanvas(Canvas canvas) {
        background.draw(canvas);
        startButton.draw(canvas);
        lowerGate.drawGate(canvas);
        upperGate.drawGate(canvas);
        player1.drawPlayer(canvas);
        player2.drawPlayer(canvas);
        puck.drawPuck(canvas);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            int x = (int) event.getX();
            int y = (int) event.getY();
            if (y <= height / 2 + 120 & y >= height / 2 - 120 & x <= width & x >= width - 100) {
                Intent intent = new Intent(context, GameActivity.class);
                context.startActivity(intent);
            }
        }
        performClick();
        return true;
    }

    @Override
    public boolean performClick() {
        return super.performClick();
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

    public void pauseDrawing() {
        isDrawing = false;
        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void resumeDrawing() {
        isDrawing = true;
        thread = new Thread(this);
        thread.start();
    }
}
