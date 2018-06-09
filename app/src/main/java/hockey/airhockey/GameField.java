package hockey.airhockey;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.util.SparseArray;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.io.IOException;

import static hockey.airhockey.GameCustomField.player1Chosen;
import static hockey.airhockey.GameCustomField.player2Chosen;
import static hockey.airhockey.GameCustomField.playerArray;
import static hockey.airhockey.GameCustomField.puckArray;
import static hockey.airhockey.GameCustomField.puckChosen;
import static hockey.airhockey.MainActivity.settings;
import static hockey.airhockey.MainActivity.volume;

public class GameField extends SurfaceView implements Runnable {

    private final Context context;
    private final SurfaceHolder holder;
    private final SoundPool soundPool;
    private final int[] hitSound = new int[5];
    private final Paint paint, countdownPaint;
    private final Path path;
    private final Rect bounds;
    private int goalSound, countdownSound;
    private boolean pause, multiplayer, draw, isDragging1, isDragging2, isCollision1, isCollision2, isAnimation, startingCountdown, loadingGame;
    private Bitmap background;
    private Player player1, player2;
    private Gate lowerGate, upperGate;
    private Puck puck;
    private long delta;
    private SparseArray<PointF> activePointers;
    private int dragPointer1, dragPointer2, x, y, count1, count2;
    private Button play, back;
    private double capSpeed;
    private Thread thread;
    private long psec, turn, startTime;

    public GameField(Context context) {
        super(context);
        this.context = context;
        thread = new Thread();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            soundPool = new SoundPool.Builder().setMaxStreams(5).build();
        } else {
            soundPool = new SoundPool(5, AudioManager.STREAM_MUSIC, 0);
        }
        try {
            loadMusic();
        } catch (IOException e) {
            e.printStackTrace();
        }
        holder = getHolder();
        path = new Path();
        bounds = new Rect();
        count1 = 0;
        count2 = 0;
        turn = Math.round(Math.random()) + 1;
        paint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG);
        paint.setColor(Color.BLUE);
        paint.setTextSize(settings.height / 3.5f);
        paint.setAlpha(50);
        paint.setTypeface(Typeface.createFromAsset(context.getAssets(), "fonts/aldrich.ttf"));
        countdownPaint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG);
        countdownPaint.setColor(ContextCompat.getColor(context, R.color.countdownText));
        countdownPaint.setTextSize(settings.height / 3f);
        countdownPaint.setTypeface(Typeface.createFromAsset(context.getAssets(), "fonts/aldrich.ttf"));
        startingCountdown = true;
        loadingGame = true;
        startGame();
        thread.start();
    }

    void setMultiplayer(boolean multiplayer) {
        this.multiplayer = multiplayer;
    }

    void setPause() {
        pause = true;
    }

    boolean isAbleToPause() {
        return startingCountdown;
    }

    void setTimeAfterPause() {
        psec = System.currentTimeMillis();
    }

    // обновление игры
    private void update() {
        long sec = System.currentTimeMillis();
        delta = sec - psec;
        psec = sec;
        capSpeed = settings.height / 1560d;
        if (!isAnimation & !startingCountdown & !pause) {
            checkCollision();
            if (multiplayer) {
                player1.setV(delta);
            } else {
                moveBot();
            }
            player2.setV(delta);
            checkWinner();
            checkGoal();
        }
        if (!startingCountdown & !pause) {
            player1.update(delta, isAnimation || !multiplayer);
            player2.update(delta, isAnimation);
            puck.update(delta, isAnimation);
        }
        if (isAnimation & puck.v.y >= 0 & turn == 1 & puck.y >= settings.height / 3) {
            startGame();
        } else if (isAnimation & puck.v.y <= 0 & turn == 1 & puck.y <= settings.height / 3) {
            startGame();
        }
        if (isAnimation & puck.v.y >= 0 & turn == 2 & puck.y >= settings.height * (2 / 3d)) {
            startGame();
        } else if (isAnimation & puck.v.y <= 0 & turn == 2 & puck.y <= settings.height * (2 / 3d)) {
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
        } else if (puck.y > settings.height) {
            count1++;
            playGoal();
        }
    }

    // проверка победителя
    private void checkWinner() {
        if (count1 >= settings.goalThreshold) {
            Intent intent = new Intent(context, WinActivity.class);
            intent.putExtra("winner", 1);
            intent.putExtra("multiplayer", multiplayer);
            context.startActivity(intent);
        } else if (count2 >= settings.goalThreshold) {
            Intent intent = new Intent(context, WinActivity.class);
            intent.putExtra("winner", 2);
            intent.putExtra("multiplayer", multiplayer);
            context.startActivity(intent);
        }
    }

    // анимация перемещения шайбы и бит на изначальные позиции после гола
    private void playGoal() {
        isAnimation = true;
        soundPool.play(goalSound, volume, volume, 0, 0, 1);
        if (turn == 1) {
            puck.v.setVector((settings.width / 2d - puck.x) / settings.goalStopTime, (settings.height / 3d - puck.y) / settings.goalStopTime);
        } else {
            puck.v.setVector((settings.width / 2d - puck.x) / settings.goalStopTime, (settings.height * (2 / 3d) - puck.y) / settings.goalStopTime);
        }
        player1.v.setVector((settings.width / 2d - player1.x) / settings.goalStopTime, (1.4 * settings.playerScale - player1.y) / settings.goalStopTime);
        player2.v.setVector((settings.width / 2d - player2.x) / settings.goalStopTime, (settings.height - 1.4 * settings.playerScale - player2.y) / settings.goalStopTime);
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

    private int dpToPx(float dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, context.getResources().getDisplayMetrics());
    }

    // загрузка графики
    private void loadGraphics() {
        play = new Button(R.drawable.play_circle_orange, context, (int) (0.4 * settings.width), (int) (0.6 * settings.width), (int) (settings.height / 2 - 0.1 * settings.width), (int) (settings.height / 2 + 0.1 * settings.width));
        back = new Button(R.drawable.arrow_back_orange, context, dpToPx(8), dpToPx(32), dpToPx(8), dpToPx(32));
        background = BitmapFactory.decodeResource(context.getResources(), R.drawable.background);
        background = Bitmap.createScaledBitmap(background, settings.width, settings.height, true);
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
        canvas.drawBitmap(background, 0, 0, new Paint(Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG));
        lowerGate.draw(canvas);
        upperGate.draw(canvas);
        paint.getTextBounds(String.valueOf(count1), 0, 1, bounds);
        path.reset();
        path.moveTo((settings.width + paint.measureText(String.valueOf(count1))) / 2f, (settings.height / 1.9f - bounds.height()) / 2f);
        path.lineTo((settings.width - paint.measureText(String.valueOf(count1))) / 2f, (settings.height / 1.9f - bounds.height()) / 2f);
        canvas.drawTextOnPath(String.valueOf(count1), path, 0, 0, paint);
        paint.getTextBounds(String.valueOf(count2), 0, 1, bounds);
        canvas.drawText(String.valueOf(count2), (settings.width - paint.measureText(String.valueOf(count2))) / 2f, (settings.height * 1.475f + bounds.height()) / 2f, paint);
        player1.draw(canvas);
        player2.draw(canvas);
        puck.draw(canvas);
        if (startingCountdown) {
            canvas.drawColor(ContextCompat.getColor(context, R.color.transparentGrey));
            if (!loadingGame) {
                String countdown = String.valueOf((int) Math.ceil((3000 - System.currentTimeMillis() + startTime) / 1000d));
                countdownPaint.getTextBounds(countdown, 0, 1, bounds);
                canvas.drawText(countdown, (settings.width - countdownPaint.measureText(countdown)) / 2f, (settings.height + bounds.height()) / 2f, countdownPaint);
            }
        }
        if (pause) {
            canvas.drawColor(ContextCompat.getColor(context, R.color.transparentGrey));
            play.draw(canvas);
            back.draw(canvas);
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
            if (puck.y < settings.puckScale) {
                if (length(puck.x, puck.y, lowerGate.leftCorner, 0) < settings.playerScale) {
                    collision(new Vector(0, 0), Math.acos((puck.x - lowerGate.leftCorner) / length(puck.x, puck.y, lowerGate.leftCorner, 0)), false);
                } else if (length(puck.x, puck.y, lowerGate.rightCorner, 0) < settings.playerScale) {
                    collision(new Vector(0, 0), Math.acos((puck.x - lowerGate.rightCorner) / length(puck.x, puck.y, lowerGate.rightCorner, 0)), false);
                }
            } else if (puck.y > settings.height - settings.puckScale) {
                if (length(puck.x, puck.y, upperGate.leftCorner, settings.height) < settings.playerScale) {
                    collision(new Vector(0, 0), Math.acos((puck.x - upperGate.leftCorner) / length(puck.x, puck.y, upperGate.leftCorner, settings.height)), false);
                } else if (length(puck.x, puck.y, upperGate.rightCorner, settings.height) < settings.playerScale) {
                    collision(new Vector(0, 0), Math.acos((puck.x - upperGate.rightCorner) / length(puck.x, puck.y, upperGate.rightCorner, settings.height)), false);
                }
            }
        } else {
            if (puck.y < settings.puckScale) {
                puck.y = settings.puckScale;
                puck.v.y = -puck.v.y;
            } else if (puck.y > settings.height - settings.puckScale) {
                puck.y = settings.height - settings.puckScale;
                puck.v.y = -puck.v.y;
            }
        }
        // проверка столкновения с левой/правой стенкой
        if (puck.x < settings.puckScale) {
            puck.x = settings.puckScale;
            puck.v.x = -puck.v.x;
        } else if (puck.x > settings.width - settings.puckScale) {
            puck.x = settings.width - settings.puckScale;
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
            if ((length(px, py, plx1, ply1) < settings.playerScale + settings.puckScale - 5) & !isCollision1) {
                if (ply1 <= py) {
                    collision(player1.v, Math.acos((px - plx1) / length(px, py, plx1, ply1)), true);
                } else {
                    collision(player1.v, Math.acos((plx1 - px) / length(px, py, plx1, ply1)), true);
                }
                isCollision1 = true;
            }
            if (!(length(px, py, plx1, ply1) < settings.playerScale + settings.puckScale - 5)) {
                isCollision1 = false;
            }
            if ((length(px, py, plx2, ply2) < settings.playerScale + settings.puckScale - 5) & !isCollision2) {
                if (ply2 <= py) {
                    collision(player2.v, Math.acos((px - plx2) / length(px, py, plx2, ply2)), true);
                } else {
                    collision(player2.v, Math.acos((plx2 - px) / length(px, py, plx2, ply2)), true);
                }
                isCollision2 = true;
            }
            if (!(length(px, py, plx2, ply2) < settings.playerScale + settings.puckScale - 5)) {
                isCollision2 = false;
            }
        }
    }

    // движение верхней биты, если игра против ИИ
    private void moveBot() {
        if (puck.y - settings.puckScale < settings.height / 2 & length(puck.x, puck.y, player1.x, player1.y) >= settings.playerScale + settings.puckScale - 5) {
            double y = capSpeed / Math.sqrt(Math.pow((puck.x - player1.x) / (puck.y - player1.y), 2) + 1);
            if (puck.y < player1.y) {
                y = -Math.abs(y);
            }
            double x = Math.sqrt(Math.pow(capSpeed, 2) - Math.pow(y, 2));
            if (puck.x < player1.x) {
                x = -Math.abs(x);
            }
            player1.v.setVector(x, y);
        } else if (length(player1.x, player1.y, settings.width / 2, (int) (1.4 * settings.playerScale)) > player1.v.v * delta) {
            double y = capSpeed / Math.sqrt(Math.pow((settings.width / 2 - player1.x) / ((int) (1.4 * settings.playerScale) - player1.y), 2) + 1);
            if (1.4 * settings.playerScale < player1.y) {
                y = -Math.abs(y);
            }
            double x = Math.sqrt(Math.pow(capSpeed, 2) - Math.pow(y, 2));
            if (settings.width / 2 < player1.x) {
                x = -Math.abs(x);
            }
            player1.v.setVector(x, y);
        } else {
            player1.v.setVector(0, 0);
            player1.x = settings.width / 2;
            player1.y = (int) (1.4 * settings.playerScale);
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
        return Math.sqrt(Math.pow(playerX - x, 2) + Math.pow(playerY - y, 2)) < settings.playerScale;
    }

    // не дает сдвинуть биту за вертикальные стенки
    private double checkX(double playerX) {
        if (playerX < settings.playerScale) {
            playerX = settings.playerScale;
        } else if (playerX > settings.width - settings.playerScale) {
            playerX = settings.width - settings.playerScale;
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
                if (pause) {
                    if (play.isClicked(x, y)) {
                        pause = false;
                    }
                    if (back.isClicked(x, y)) {
                        Intent intent = new Intent(context, GameCustomActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        context.startActivity(intent);
                    }
                }
                if (isInside(player1.x, player1.y) & multiplayer) {
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
                        if (isDragging1 & (pointerId == dragPointer1) & multiplayer) {
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
            if (player1.y < settings.playerScale) {
                player1.y = settings.playerScale;
            } else if (player1.y > settings.height / 2 - settings.playerScale) {
                player1.y = settings.height / 2 - settings.playerScale;
            }
            player2.x = checkX(player2.x);
            if (player2.y > settings.height - settings.playerScale) {
                player2.y = settings.height - settings.playerScale;
            } else if (player2.y < settings.height / 2 + settings.playerScale) {
                player2.y = settings.height / 2 + settings.playerScale;
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
