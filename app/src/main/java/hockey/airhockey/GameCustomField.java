/*
 * Created by Alexey Kiselev
 * Copyright (c) 2018 . All rights reserved.
 * Last modified 01.07.18 15:28
 */

package hockey.airhockey;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import static hockey.airhockey.MainActivity.settings;
import static hockey.airhockey.Utils.dpToPx;

public class GameCustomField extends SurfaceView implements Runnable {

    static int player1Chosen, puckChosen, player2Chosen;
    static int[] puckArray, playerArray;
    private final Context context;
    private final SurfaceHolder holder;
    private Thread thread;
    private boolean isDrawing, isSpeedSet, animStop;
    private Bitmap background;
    private Player player1, player2;
    private Gate lowerGate, upperGate;
    private Puck puck;
    private long psec;
    private Button start, puckLeft, puckRight, player1Left, player1Right, player2Left, player2Right, mode, back;

    public GameCustomField(Context context) {
        super(context);
        this.context = context;
        player1Chosen = 0;
        player2Chosen = 0;
        puckChosen = 0;
        puckArray = new int[settings.numberOfPucks + 1];
        puckArray[0] = R.drawable.puck_default;
        puckArray[1] = R.drawable.puck_green;
        puckArray[2] = R.drawable.puck_blue;
        playerArray = new int[settings.numberOfPlayers + 1];
        playerArray[0] = R.drawable.player_default;
        playerArray[1] = R.drawable.player_black;
        thread = new Thread();
        holder = getHolder();
        loadGraphics();
        isSpeedSet = false;
        animStop = false;
        psec = System.currentTimeMillis();
        thread.start();
    }

    // обновление игры
    private void update() {
        long sec = System.currentTimeMillis();
        long delta = sec - psec;
        psec = sec;
        if (!isSpeedSet & delta < 1000) {
            isSpeedSet = true;
            puck.v.setVector((settings.width / 2d - puck.x) / settings.startAnimStopTime, 0);
            player1.v.setVector((settings.width / 2d - player1.x) / settings.startAnimStopTime, 0);
            player2.v.setVector((settings.width / 2d - player2.x) / settings.startAnimStopTime, 0);
        }
        player1.update(delta, true);
        player2.update(delta, true);
        puck.update(delta, true);
        if (puck.x <= settings.width / 2 + settings.puckScale / 2 & !animStop) {
            stopAnimation();
        }
    }

    void stopAnimation() {
        player1 = new Player(playerArray[player1Chosen], context, 1);
        player2 = new Player(playerArray[player2Chosen], context, 2);
        puck = new Puck(puckArray[puckChosen], context);
        animStop = true;
    }

    // загрузка графики
    private void loadGraphics() {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.RGB_565;
        background = BitmapFactory.decodeResource(context.getResources(), R.drawable.background, options);
        background = Bitmap.createScaledBitmap(background, settings.width, settings.height, true);
        back = new Button(R.drawable.ic_arrow_back, context, dpToPx(8), dpToPx(32), dpToPx(8), dpToPx(32), dpToPx(12));
        start = new Button(R.drawable.start_button, R.drawable.start_button_pressed, context, (int) (settings.width - 0.0419921875 * settings.height), settings.width, (int) (0.4375 * settings.height), (int) (0.5625 * settings.height), 0);
        mode = new Button(R.drawable.mode_button, R.drawable.mode_button_pressed, context, 0, (int) (0.0419921875 * settings.height), (int) (0.4375 * settings.height), (int) (0.5625 * settings.height), 0);
        puckLeft = new Button(R.drawable.arrow_left, context, (int) (settings.width / 2 - 1.7 * settings.playerScale - 20), settings.width / 2 - settings.playerScale - 20, (int) (settings.height / 2 - 0.7 * settings.playerScale), (int) (settings.height / 2 + 0.7 * settings.playerScale), 0);
        puckRight = new Button(R.drawable.arrow_right, context, settings.width / 2 + settings.playerScale + 20, (int) (settings.width / 2 + 1.7 * settings.playerScale + 20), (int) (settings.height / 2 - 0.7 * settings.playerScale), (int) (settings.height / 2 + 0.7 * settings.playerScale), 0);
        player1Left = new Button(R.drawable.arrow_left, context, (int) (settings.width / 2 - 1.7 * settings.playerScale - 20), settings.width / 2 - settings.playerScale - 20, (int) (0.7 * settings.playerScale), (int) (2.1 * settings.playerScale), 0);
        player1Right = new Button(R.drawable.arrow_right, context, settings.width / 2 + settings.playerScale + 20, (int) (settings.width / 2 + 1.7 * settings.playerScale + 20), (int) (0.7 * settings.playerScale), (int) (2.1 * settings.playerScale), 0);
        player2Left = new Button(R.drawable.arrow_left, context, (int) (settings.width / 2 - 1.7 * settings.playerScale - 20), settings.width / 2 - settings.playerScale - 20, (int) (settings.height - 2.1 * settings.playerScale), (int) (settings.height - 0.7 * settings.playerScale), 0);
        player2Right = new Button(R.drawable.arrow_right, context, settings.width / 2 + settings.playerScale + 20, (int) (settings.width / 2 + 1.7 * settings.playerScale + 20), (int) (settings.height - 2.1 * settings.playerScale), (int) (settings.height - 0.7 * settings.playerScale), 0);
        player1 = new Player(playerArray[player1Chosen], context, 1);
        player1.x = settings.width + settings.playerScale * 2;
        player2 = new Player(playerArray[player2Chosen], context, 2);
        player2.x = settings.width + settings.playerScale * 2;
        puck = new Puck(puckArray[puckChosen], context);
        puck.x = settings.width + settings.puckScale * 2;
        lowerGate = new Gate(R.drawable.lower_gate, context, 1);
        upperGate = new Gate(R.drawable.upper_gate, context, 2);
    }

    // рисование
    private void drawOnCanvas(Canvas canvas) {
        canvas.drawBitmap(background, 0, 0, new Paint(Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG));
        start.draw(canvas);
        mode.draw(canvas);
        lowerGate.draw(canvas);
        upperGate.draw(canvas);
        if (animStop) {
            player1Left.draw(canvas);
            player1Right.draw(canvas);
            player2Left.draw(canvas);
            player2Right.draw(canvas);
            puckRight.draw(canvas);
            puckLeft.draw(canvas);
        }
        back.draw(canvas);
        player1.drawShadow(canvas);
        player2.drawShadow(canvas);
        puck.drawShadow(canvas);
        player1.draw(canvas);
        player2.draw(canvas);
        puck.draw(canvas);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN: {
                int x = (int) event.getX();
                int y = (int) event.getY();
                if (start.isClicked(x, y)) {
                    start.setPressed(true);
                }
                if (mode.isClicked(x, y)) {
                    mode.setPressed(true);
                }
                if (back.isClicked(x, y)) {
                    Intent intent = new Intent(context, MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    context.startActivity(intent);
                }
                if (puckLeft.isClicked(x, y)) {
                    puckChosen -= 1;
                    if (puckChosen < 0) {
                        puckChosen = settings.numberOfPucks;
                    }
                    puck = new Puck(puckArray[puckChosen], context);
                    System.out.println(puckChosen);
                }
                if (puckRight.isClicked(x, y)) {
                    puckChosen++;
                    if (puckChosen > settings.numberOfPucks) {
                        puckChosen = 0;
                    }
                    puck = new Puck(puckArray[puckChosen], context);
                }
                if (player1Left.isClicked(x, y)) {
                    player1Chosen -= 1;
                    if (player1Chosen < 0) {
                        player1Chosen = settings.numberOfPlayers;
                    }
                    player1 = new Player(playerArray[player1Chosen], context, 1);
                }
                if (player1Right.isClicked(x, y)) {
                    player1Chosen++;
                    if (player1Chosen > settings.numberOfPlayers) {
                        player1Chosen = 0;
                    }
                    player1 = new Player(playerArray[player1Chosen], context, 1);
                }
                if (player2Left.isClicked(x, y)) {
                    player2Chosen -= 1;
                    if (player2Chosen < 0) {
                        player2Chosen = settings.numberOfPlayers;
                    }
                    player2 = new Player(playerArray[player2Chosen], context, 2);
                }
                if (player2Right.isClicked(x, y)) {
                    player2Chosen++;
                    if (player2Chosen > settings.numberOfPlayers) {
                        player2Chosen = 0;
                    }
                    player2 = new Player(playerArray[player2Chosen], context, 2);
                }
                break;
            }
            case MotionEvent.ACTION_UP: {
                start.setPressed(false);
                mode.setPressed(false);
                int x = (int) event.getX();
                int y = (int) event.getY();
                if (start.isClicked(x, y)) {
                    Intent intent = new Intent(context, GameActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    context.startActivity(intent);
                }
                if (mode.isClicked(x, y)) {
                    Intent intent = new Intent(context, SettingsActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    context.startActivity(intent);
                }
            }
        }
        return true;
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

    public void releaseMemory() {
        background.recycle();
    }

    public void resumeDrawing() {
        isDrawing = true;
        thread = new Thread(this);
        thread.start();
    }
}
