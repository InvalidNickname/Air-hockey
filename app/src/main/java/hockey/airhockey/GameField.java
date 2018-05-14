package hockey.airhockey;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.media.AudioManager;
import android.media.SoundPool;
import android.support.graphics.drawable.VectorDrawableCompat;
import android.util.SparseArray;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.io.IOException;

import static hockey.airhockey.GameCustomField.player1Chosen;
import static hockey.airhockey.GameCustomField.player2Chosen;
import static hockey.airhockey.GameCustomField.playerArray;
import static hockey.airhockey.GameCustomField.puckArray;
import static hockey.airhockey.GameCustomField.puckChosen;
import static hockey.airhockey.MainActivity.goalStopTime;
import static hockey.airhockey.MainActivity.goalThreshold;
import static hockey.airhockey.MainActivity.height;
import static hockey.airhockey.MainActivity.playerScale;
import static hockey.airhockey.MainActivity.puckScale;
import static hockey.airhockey.MainActivity.volume;
import static hockey.airhockey.MainActivity.width;

public class GameField extends SurfaceView implements Runnable {

    private long delta;
    private Thread thread;
    private final Context context;
    private final SurfaceHolder holder;
    private final SoundPool soundPool;
    private final int[] hitSound = new int[5];
    private int goalSound, countdownSound;
    private boolean pause, draw, isDragging1, isDragging2, isCollision1, isCollision2, isAnimation, startingCountdown, loadingGame;
    private VectorDrawableCompat background;
    private Player player1, player2;
    private Gate lowerGate, upperGate;
    private Puck puck;
    private long psec, turn, startTime;
    private SparseArray<PointF> activePointers;
    private int dragPointer1, dragPointer2, x, y, count1, count2;
    private final Paint paint, countdownPaint;
    private Button play;

    public GameField(Context context) {
        super(context);
        this.context = context;
        thread = new Thread();
        soundPool = new SoundPool(5, AudioManager.STREAM_MUSIC, 0);
        try {
            loadMusic();
        } catch (IOException e) {
            e.printStackTrace();
        }
        holder = getHolder();
        count1 = 0;
        count2 = 0;
        turn = Math.round(Math.random()) + 1;
        paint = new Paint();
        paint.setAntiAlias(true);
        paint.setColor(Color.BLUE);
        paint.setTextSize(height / 3.5f);
        paint.setAlpha(50);
        paint.setTypeface(Typeface.createFromAsset(context.getAssets(), "fonts/aldrich.ttf"));
        countdownPaint = new Paint();
        countdownPaint.setAntiAlias(true);
        countdownPaint.setColor(context.getResources().getColor(R.color.countdownText));
        countdownPaint.setTextSize(height / 3f);
        countdownPaint.setTypeface(Typeface.createFromAsset(context.getAssets(), "fonts/aldrich.ttf"));
        startingCountdown = true;
        loadingGame = true;
        startGame();
        thread.start();
    }

    void setPause() {
        pause = true;
    }

    boolean isAbleToPause() {
        return startingCountdown;
    }

    // обновление игры
    private void update() {
        long sec = System.currentTimeMillis();
        delta = sec - psec;
        psec = sec;
        if (!isAnimation & !startingCountdown & !pause) {
            checkCollision();
            player1.setV(delta);
            player2.setV(delta);
            checkWinner();
            checkGoal();
        }
        if (!startingCountdown & !pause) {
            player1.update(delta, isAnimation);
            player2.update(delta, isAnimation);
            puck.update(delta, isAnimation);
        }
        if (isAnimation & length(puck.x, puck.y, width / 2, height / 3) <= puckScale / 2 & turn == 1) {
            startGame();
        } else if (isAnimation & length(puck.x, puck.y, width / 2, height * (2 / 3d)) <= puckScale / 2 & turn == 2) {
            startGame();
        }
        if (!loadingGame) {
            if (sec - startTime > 3000 & startingCountdown) {
                startingCountdown = false;
            }
        }
    }

    // проверка гола
    private void checkGoal() {
        if (puck.y < 0) {
            count2++;
            playGoal();
        } else if (puck.y > height) {
            count1++;
            playGoal();
        }
    }

    // проверка победителя
    private void checkWinner() {
        if (count1 >= goalThreshold) {
            Intent intent = new Intent(context, WinActivity.class);
            intent.putExtra("winner", 1);
            context.startActivity(intent);
        } else if (count2 >= goalThreshold) {
            Intent intent = new Intent(context, WinActivity.class);
            intent.putExtra("winner", 2);
            context.startActivity(intent);
        }
    }

    // анимация перемещения шайбы и бит на изначальные позиции после гола
    private void playGoal() {
        isAnimation = true;
        soundPool.play(goalSound, volume, volume, 0, 0, 1);
        if (turn == 1) {
            puck.v.setVector((width / 2d - puck.x) / goalStopTime, (height / 3d - puck.y) / goalStopTime);
        } else {
            puck.v.setVector((width / 2d - puck.x) / goalStopTime, (height * (2 / 3d) - puck.y) / goalStopTime);
        }
        player1.v.setVector((width / 2d - player1.x) / goalStopTime, (1.4 * playerScale - player1.y) / goalStopTime);
        player2.v.setVector((width / 2d - player2.x) / goalStopTime, (height - 1.4 * playerScale - player2.y) / goalStopTime);
    }

    // начало новой игры
    private void startGame() {
        activePointers = new SparseArray<>();
        isCollision1 = false;
        isCollision2 = false;
        psec = System.currentTimeMillis();
        loadGraphics();
        if (turn == 1) {
            turn = 2;
        } else {
            turn = 1;
        }
        isAnimation = false;
    }

    // загрузка звуков
    private void loadMusic() throws IOException {
        soundPool.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener() {
            @Override
            public void onLoadComplete(SoundPool soundPool, int i, int i1) {
                if (i == 7) {
                    soundPool.play(countdownSound, volume, volume, 0, 0, 1);
                    startTime = System.currentTimeMillis();
                    loadingGame = false;
                }
            }
        });
        hitSound[0] = soundPool.load(context.getAssets().openFd("sounds/hitSound/hit1.wav"), 1);
        hitSound[1] = soundPool.load(context.getAssets().openFd("sounds/hitSound/hit2.wav"), 1);
        hitSound[2] = soundPool.load(context.getAssets().openFd("sounds/hitSound/hit3.wav"), 1);
        hitSound[3] = soundPool.load(context.getAssets().openFd("sounds/hitSound/hit4.wav"), 1);
        hitSound[4] = soundPool.load(context.getAssets().openFd("sounds/hitSound/hit5.wav"), 1);
        goalSound = soundPool.load(context.getAssets().openFd("sounds/goal.wav"), 1);
        countdownSound = soundPool.load(context.getAssets().openFd("sounds/countdown.wav"), 1);
    }

    // загрузка графики
    private void loadGraphics() {
        play = new Button(R.drawable.play_circle_orange, context, (int) (0.4 * width), (int) (0.6 * width), (int) (height / 2 - 0.1 * width), (int) (height / 2 + 0.1 * width));
        background = VectorDrawableCompat.create(context.getResources(), R.drawable.background, null);
        if (background != null) {
            background.setBounds(0, 0, width, height);
        }
        player1 = new Player(playerArray[player1Chosen], context, 1);
        player2 = new Player(playerArray[player2Chosen], context, 2);
        if (count2 == 0 & count1 == 0) {
            puck = new Puck(puckArray[puckChosen], context);
        } else {
            puck = new Puck(puckArray[puckChosen], context, turn);
        }
        lowerGate = new Gate(R.drawable.lower_gate, context, 1);
        upperGate = new Gate(R.drawable.upper_gate, context, 2);
    }

    // рисование
    private void drawOnCanvas(Canvas canvas) {
        background.draw(canvas);
        lowerGate.draw(canvas);
        upperGate.draw(canvas);
        Rect bounds = new Rect();
        paint.getTextBounds(String.valueOf(count1), 0, 1, bounds);
        Path path = new Path();
        path.reset();
        path.moveTo((width + paint.measureText(String.valueOf(count1))) / 2f, (height / 1.9f - bounds.height()) / 2f);
        path.lineTo((width - paint.measureText(String.valueOf(count1))) / 2f, (height / 1.9f - bounds.height()) / 2f);
        canvas.drawTextOnPath(String.valueOf(count1), path, 0, 0, paint);
        paint.getTextBounds(String.valueOf(count2), 0, 1, bounds);
        canvas.drawText(String.valueOf(count2), (width - paint.measureText(String.valueOf(count2))) / 2f, (height * 1.475f + bounds.height()) / 2f, paint);
        player1.draw(canvas);
        player2.draw(canvas);
        puck.draw(canvas);
        if (startingCountdown) {
            canvas.drawColor(context.getResources().getColor(R.color.transparentGrey));
            if (!loadingGame) {
                String countdown = String.valueOf((int) Math.ceil((3000 - System.currentTimeMillis() + startTime) / 1000d));
                countdownPaint.getTextBounds(countdown, 0, 1, bounds);
                canvas.drawText(countdown, (width - countdownPaint.measureText(countdown)) / 2f, (height + bounds.height()) / 2f, countdownPaint);
            }
        }
        if (pause) {
            canvas.drawColor(context.getResources().getColor(R.color.transparentGrey));
            play.draw(canvas);
        }
    }

    // нахождение расстояния между двумя точками
    private double length(double x1, double y1, double x2, double y2) {
        return Math.sqrt(Math.pow(x1 - x2, 2) + Math.pow(y1 - y2, 2));
    }

    // проверка столковения шайбы
    private void checkCollision() {
        // проверка столкновения с верхней/нижней стенкой, пролетит ли в ворота
        if (puck.x < upperGate.rightCorner & puck.x > upperGate.leftCorner) {
            if (puck.y < puckScale) {
                if (length(puck.x, puck.y, lowerGate.leftCorner, 0) < playerScale) {
                    collision(new Vector(0, 0), Math.acos((puck.x - lowerGate.leftCorner) / length(puck.x, puck.y, lowerGate.leftCorner, 0)), false);
                } else if (length(puck.x, puck.y, lowerGate.rightCorner, 0) < playerScale) {
                    collision(new Vector(0, 0), Math.acos((puck.x - lowerGate.rightCorner) / length(puck.x, puck.y, lowerGate.rightCorner, 0)), false);
                }
            } else if (puck.y > height - puckScale) {
                if (length(puck.x, puck.y, upperGate.leftCorner, height) < playerScale) {
                    collision(new Vector(0, 0), Math.acos((puck.x - upperGate.leftCorner) / length(puck.x, puck.y, upperGate.leftCorner, height)), false);
                } else if (length(puck.x, puck.y, upperGate.rightCorner, height) < playerScale) {
                    collision(new Vector(0, 0), Math.acos((puck.x - upperGate.rightCorner) / length(puck.x, puck.y, upperGate.rightCorner, height)), false);
                }
            }
        } else {
            if (puck.y < puckScale) {
                puck.y = puckScale;
                puck.v.y = -puck.v.y;
            } else if (puck.y > height - puckScale) {
                puck.y = height - puckScale;
                puck.v.y = -puck.v.y;
            }
        }
        // проверка столкновения с левой/правой стенкой
        if (puck.x < puckScale) {
            puck.x = puckScale;
            puck.v.x = -puck.v.x;
        } else if (puck.x > width - puckScale) {
            puck.x = width - puckScale;
            puck.v.x = -puck.v.x;
        }
        // проверка столкновения с битой
        for (int i = 0; i < 2; i++) {
            double plx1, ply1, plx2, ply2, px, py;
            plx1 = player1.x + player1.v.x * delta / 6 * i;
            ply1 = player1.y + player1.v.y * delta / 6 * i;
            plx2 = player2.x + player2.v.x * delta / 6 * i;
            ply2 = player2.y + player2.v.y * delta / 6 * i;
            px = puck.x + puck.v.x * delta / 6 * i;
            py = puck.y + puck.v.y * delta / 6 * i;
            if ((length(px, py, plx1, ply1) < playerScale + puckScale - 5) & !isCollision1) {
                if (ply1 <= py) {
                    collision(player1.v, Math.acos((px - plx1) / length(px, py, plx1, ply1)), true);
                } else {
                    collision(player1.v, Math.acos((plx1 - px) / length(px, py, plx1, ply1)), true);
                }
                isCollision1 = true;
            }
            if (!(length(px, py, plx1, ply1) < playerScale + puckScale - 5)) {
                isCollision1 = false;
            }
            if ((length(px, py, plx2, ply2) < playerScale + puckScale - 5) & !isCollision2) {
                if (ply2 <= py) {
                    collision(player2.v, Math.acos((px - plx2) / length(px, py, plx2, ply2)), true);
                } else {
                    collision(player2.v, Math.acos((plx2 - px) / length(px, py, plx2, ply2)), true);
                }
                isCollision2 = true;
            }
            if (!(length(px, py, plx2, ply2) < playerScale + puckScale - 5)) {
                isCollision2 = false;
            }
        }
    }

    // столкновение точки с шайбой, alpha - угол наклона прямой, соединяющей центры точки и биты к прямой x
    private void collision(Vector speed, double alpha, boolean needSound) {
        if (needSound) {
            int random = (int) Math.round(Math.random() * 4);
            soundPool.play(hitSound[random], volume, volume, 0, 0, 1);
        }
        Vector relative, collided = new Vector(0, 0);
        // нахождение скорости шайбы относительно точки
        relative = puck.v.deductVector(speed);
        // проецирование скорости шайбы в систему координат с наклоном прямой x на угол alpha, отражение vy
        collided.y = -(relative.x * Math.cos(alpha) + relative.y * Math.sin(alpha));
        collided.x = relative.x * Math.sin(alpha) - relative.y * Math.cos(alpha);
        // проецирование отраженной скорости на нормальную систему координат
        puck.v.x = collided.x * Math.sin(alpha) + collided.y * Math.cos(alpha);
        puck.v.y = -collided.x * Math.cos(alpha) + collided.y * Math.sin(alpha);
    }

    @Override
    public void run() {
        while (draw) {
            if (holder.getSurface().isValid()) {
                Canvas canvas = holder.lockCanvas();
                update();
                drawOnCanvas(canvas);
                holder.unlockCanvasAndPost(canvas);
            }
        }
    }

    // определяет, находится ли точка внутри биты
    private boolean isInside(double playerX, double playerY) {
        return Math.sqrt(Math.pow(playerX - x, 2) + Math.pow(playerY - y, 2)) < playerScale;
    }

    // не дает сдвинуть биту за вертикальные стенки
    private double checkX(double playerX) {
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
                if (play.isClicked(x, y)) {
                    pause = false;
                }
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
                    if (point != null & !isAnimation & !startingCountdown & !pause) {
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
        if (!isAnimation) {
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
        }
        return true;
    }

    public void pauseDrawing() {
        draw = false;
        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void resumeDrawing() {
        draw = true;
        thread = new Thread(this);
        thread.start();
    }
}
