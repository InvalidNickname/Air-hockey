package hockey.airhockey;

import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.support.graphics.drawable.VectorDrawableCompat;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import static hockey.airhockey.MainActivity.height;
import static hockey.airhockey.MainActivity.numberOfPlayers;
import static hockey.airhockey.MainActivity.numberOfPucks;
import static hockey.airhockey.MainActivity.playerScale;
import static hockey.airhockey.MainActivity.puckScale;
import static hockey.airhockey.MainActivity.startAnimStopTime;
import static hockey.airhockey.MainActivity.width;

public class GameCustomField extends SurfaceView implements Runnable {

    private Thread thread;
    static int player1Chosen, puckChosen, player2Chosen;
    private Context context;
    private SurfaceHolder holder;
    private boolean isDrawing, isSpeedSet;
    private VectorDrawableCompat background;
    private Player player1, player2;
    private Gate lowerGate, upperGate;
    private Puck puck;
    private long psec;
    static int[] puckArray, playerArray;
    private Button start, puckLeft, puckRight, player1Left, player1Right, player2Left, player2Right;
    long delta;

    public GameCustomField(Context context) {
        super(context);
        this.context = context;
        player1Chosen = 0;
        player2Chosen = 0;
        puckChosen = 0;
        puckArray = new int[numberOfPucks + 1];
        puckArray[0] = R.drawable.puck_default;
        puckArray[1] = R.drawable.puck_green;
        playerArray = new int[numberOfPlayers + 1];
        playerArray[0] = R.drawable.player_default;
        playerArray[1] = R.drawable.player_black;
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
        if (puck.x <= width / 2 + puckScale / 2) {
            player1 = new Player(playerArray[player1Chosen], context, 1);
            player2 = new Player(playerArray[player2Chosen], context, 2);
            puck = new Puck(puckArray[puckChosen], context);
        }
    }

    // загрузка графики
    private void loadGraphics() {
        background = VectorDrawableCompat.create(context.getResources(), R.drawable.background, null);
        if (background != null) {
            background.setBounds(0, 0, width, height);
        }
        start = new Button(R.drawable.start_button, context, (int) (0.9074 * width), width, (int) (0.4375 * height), (int) (0.5625 * height));
        puckLeft = new Button(R.drawable.arrow_left, context, (int) (width / 2 - 1.5 * playerScale - 20), width / 2 - playerScale - 20, height / 2 - playerScale / 2, height / 2 + playerScale / 2);
        puckRight = new Button(R.drawable.arrow_right, context, width / 2 + playerScale + 20, (int) (width / 2 + 1.5 * playerScale + 20), height / 2 - playerScale / 2, height / 2 + playerScale / 2);
        player1Left = new Button(R.drawable.arrow_left, context, (int) (width / 2 - 1.5 * playerScale - 20), width / 2 - playerScale - 20, (int) (0.9 * playerScale), (int) (1.9 * playerScale));
        player1Right = new Button(R.drawable.arrow_right, context, width / 2 + playerScale + 20, (int) (width / 2 + 1.5 * playerScale + 20), (int) (0.9 * playerScale), (int) (1.9 * playerScale));
        player2Left = new Button(R.drawable.arrow_left, context, (int) (width / 2 - 1.5 * playerScale - 20), width / 2 - playerScale - 20, (int) (height - 1.9 * playerScale), (int) (height - 0.9 * playerScale));
        player2Right = new Button(R.drawable.arrow_right, context, width / 2 + playerScale + 20, (int) (width / 2 + 1.5 * playerScale + 20), (int) (height - 1.9 * playerScale), (int) (height - 0.9 * playerScale));
        player1 = new Player(playerArray[player1Chosen], context, 1);
        player1.x = width + playerScale * 2;
        player2 = new Player(playerArray[player2Chosen], context, 2);
        player2.x = width + playerScale * 2;
        puck = new Puck(puckArray[puckChosen], context);
        puck.x = width + puckScale * 2;
        lowerGate = new Gate(R.drawable.lower_gate, context, 1);
        upperGate = new Gate(R.drawable.upper_gate, context, 2);
    }

    // рисование
    private void drawOnCanvas(Canvas canvas) {
        background.draw(canvas);
        start.draw(canvas);
        player1Left.draw(canvas);
        player1Right.draw(canvas);
        player2Left.draw(canvas);
        player2Right.draw(canvas);
        puckRight.draw(canvas);
        puckLeft.draw(canvas);
        lowerGate.draw(canvas);
        upperGate.draw(canvas);
        player1.draw(canvas);
        player2.draw(canvas);
        puck.draw(canvas);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            int x = (int) event.getX();
            int y = (int) event.getY();
            if (start.isClicked(x, y)) {
                Intent intent = new Intent(context, GameActivity.class);
                context.startActivity(intent);
            }
            if (puckLeft.isClicked(x, y)) {
                puckChosen -= 1;
                if (puckChosen < 0) {
                    puckChosen = numberOfPucks;
                }
                puck = new Puck(puckArray[puckChosen], context);
                System.out.println(puckChosen);
            }
            if (puckRight.isClicked(x, y)) {
                puckChosen++;
                if (puckChosen > numberOfPucks) {
                    puckChosen = 0;
                }
                puck = new Puck(puckArray[puckChosen], context);
            }
            if (player1Left.isClicked(x, y)) {
                player1Chosen -= 1;
                if (player1Chosen < 0) {
                    player1Chosen = numberOfPlayers;
                }
                player1 = new Player(playerArray[player1Chosen], context, 1);
                System.out.println(puckChosen);
            }
            if (player1Right.isClicked(x, y)) {
                player1Chosen++;
                if (player1Chosen > numberOfPlayers) {
                    player1Chosen = 0;
                }
                player1 = new Player(playerArray[player1Chosen], context, 2);
            }
            if (player2Left.isClicked(x, y)) {
                player2Chosen -= 1;
                if (player2Chosen < 0) {
                    player2Chosen = numberOfPlayers;
                }
                player2 = new Player(playerArray[player2Chosen], context, 1);
                System.out.println(puckChosen);
            }
            if (player2Right.isClicked(x, y)) {
                player2Chosen++;
                if (player2Chosen > numberOfPlayers) {
                    player2Chosen = 0;
                }
                player2 = new Player(playerArray[player2Chosen], context, 2);
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
