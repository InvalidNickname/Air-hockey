/*
 * Created by Alexey Kiselev
 * Copyright (c) 2018 . All rights reserved.
 * Last modified 30.06.18 19:57
 */

package hockey.airhockey;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.io.IOException;

import static hockey.airhockey.GameCustomField.player1Chosen;
import static hockey.airhockey.GameCustomField.player2Chosen;
import static hockey.airhockey.GameCustomField.playerArray;
import static hockey.airhockey.GameCustomField.puckArray;
import static hockey.airhockey.GameCustomField.puckChosen;
import static hockey.airhockey.MainActivity.APP_PREFERENCES;
import static hockey.airhockey.MainActivity.PAINT_FLAGS;
import static hockey.airhockey.MainActivity.settings;
import static hockey.airhockey.MainActivity.volume;
import static hockey.airhockey.SettingsActivity.APP_PREFERENCES_GAME_LENGTH_TIME;
import static hockey.airhockey.SettingsActivity.APP_PREFERENCES_GAME_MODE;
import static hockey.airhockey.SettingsActivity.APP_PREFERENCES_GOAL_THRESHOLD;
import static hockey.airhockey.SettingsActivity.APP_PREFERENCES_MULTIPLAYER;
import static hockey.airhockey.SettingsActivity.GAME_MODE_POINTS;
import static hockey.airhockey.SettingsActivity.GAME_MODE_TIME;
import static hockey.airhockey.Utils.calculateLength;
import static hockey.airhockey.Utils.dpToPx;

public class GameField extends SurfaceView implements Runnable {

    private final Context context;
    private final SurfaceHolder holder;
    private final SoundPool soundPool;
    private final int[] hitSound = new int[5];
    private final Paint paint, countdownPaint, bitmapPaint, smallCountdownPaint, timerPaint;
    private final Path path, timerPath;
    private final Rect bounds;
    private final Runnable graphicalRunnable;
    private final BitmapFactory.Options options;
    private final double capSpeed;
    private final int gameMode, goalThreshold;
    private final boolean multiplayer;
    private int goalSound, countdownSound;
    private String time;
    private boolean pause, draw, isDragging1, isDragging2, isCollision1, isCollision2, isAnimation, startingCountdown, loadingGame, firstWin;
    private Bitmap background;
    private Player player1, player2;
    private Gate lowerGate, upperGate;
    private Puck puck;
    private SparseArray<PointF> activePointers;
    private int dragPointer1, dragPointer2, count1, count2, gameLengthTime;
    private Button play, back;
    private double x, y;
    private Thread thread, graphicalThread;
    private long psec, turn, startTime, delta, sec, timeRemaining, pauseStart;
    private boolean dragChanged1, dragChanged2;

    public GameField(Context context) {
        super(context);
        this.context = context;
        // получение и установка настроек
        SharedPreferences preferences = context.getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);
        multiplayer = preferences.getBoolean(APP_PREFERENCES_MULTIPLAYER, true);
        gameMode = preferences.getInt(APP_PREFERENCES_GAME_MODE, GAME_MODE_POINTS);
        goalThreshold = preferences.getInt(APP_PREFERENCES_GOAL_THRESHOLD, 7);
        gameLengthTime = preferences.getInt(APP_PREFERENCES_GAME_LENGTH_TIME, 2) * 60 * 1000;
        capSpeed = settings.height / 1560d;
        turn = Math.round(Math.random()) + 1;
        startingCountdown = true;
        loadingGame = true;
        firstWin = true;
        // настройка потоков
        thread = new Thread();
        holder = getHolder();
        graphicalRunnable = new Runnable() {
            @Override
            public void run() {
                while (draw) {
                    if (holder.getSurface().isValid()) {
                        Canvas canvas = holder.lockCanvas();
                        drawOnCanvas(canvas);
                        holder.unlockCanvasAndPost(canvas);
                    }
                }
            }
        };
        graphicalThread = new Thread(graphicalRunnable);
        // загрузка музыки и звуков
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
        // загрузка и настройка графики
        path = new Path();
        timerPath = new Path();
        bounds = new Rect();
        paint = new Paint(PAINT_FLAGS);
        countdownPaint = new Paint(PAINT_FLAGS);
        smallCountdownPaint = new Paint(PAINT_FLAGS);
        bitmapPaint = new Paint(PAINT_FLAGS);
        timerPaint = new Paint(PAINT_FLAGS);
        setPaint();
        options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.RGB_565;
        loadGraphicsOnce();
        // начало игры, запуск потоков
        startGame();
        thread.start();
        graphicalThread.start();
    }

    // настройка paint
    private void setPaint() {
        paint.setColor(Color.BLUE);
        paint.setTextSize(settings.height / 3.5f);
        paint.setAlpha(50);
        paint.setTypeface(Typeface.createFromAsset(context.getAssets(), "fonts/aldrich.ttf"));
        countdownPaint.setColor(ContextCompat.getColor(context, R.color.countdownText));
        countdownPaint.setTextSize(settings.height / 3f);
        countdownPaint.setTypeface(Typeface.createFromAsset(context.getAssets(), "fonts/aldrich.ttf"));
        smallCountdownPaint.setColor(ContextCompat.getColor(context, R.color.countdownText));
        timerPaint.setColor(ContextCompat.getColor(context, R.color.colorText));
        findBestTextSize();
        timerPath.reset();
        timerPaint.getTextBounds("0:00", 0, 4, bounds);
        timerPath.moveTo(settings.width - bounds.height() * 1.5f, settings.height / 2 - timerPaint.measureText("0:00") / 2);
        timerPath.lineTo(settings.width - bounds.height() * 1.5f, settings.height / 2 + timerPaint.measureText("0:00") / 2);
    }

    // нахождение лучшего размера текста
    private void findBestTextSize() {
        int i = 0;
        while (true) {
            i++;
            smallCountdownPaint.setTextSize(settings.height / i);
            if (smallCountdownPaint.measureText(context.getResources().getQuantityString(R.plurals.countdown_mode_points, goalThreshold, goalThreshold)) <= settings.width * (1 / 2.5f)) {
                break;
            }
        }
        i = 0;
        while (true) {
            i++;
            timerPaint.setTextSize(settings.height / i);
            if (timerPaint.measureText("0:00") <= settings.width * (1 / 9f)) {
                break;
            }
        }
    }

    // загрузка звуков
    private void loadMusic() throws IOException {
        // после загрузки всех 7 звуков начинается игра
        soundPool.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener() {
            @Override
            public void onLoadComplete(SoundPool soundPool, int i, int i1) {
                if (i == countdownSound & i1 == 0) {
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

    // загрузка графики только в начале игры
    private void loadGraphicsOnce() {
        background = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(context.getResources(), R.drawable.background, options), settings.width, settings.height, true);
        play = new Button(R.drawable.ic_play_circle_orange, context, (int) (0.4 * settings.width), (int) (0.6 * settings.width), (int) (settings.height / 2 - 0.1 * settings.width), (int) (settings.height / 2 + 0.1 * settings.width), 0);
        back = new Button(R.drawable.ic_arrow_back_orange, context, dpToPx(8), dpToPx(32), dpToPx(8), dpToPx(32), dpToPx(12));
        lowerGate = new Gate(R.drawable.lower_gate, context, 1);
        upperGate = new Gate(R.drawable.upper_gate, context, 2);
    }

    // загрузка графики
    private void loadGraphics() {
        player1 = new Player(playerArray[player1Chosen], context, 1);
        player2 = new Player(playerArray[player2Chosen], context, 2);
        if (count2 == 0 & count1 == 0) {
            puck = new Puck(puckArray[puckChosen], context);
        } else {
            puck = new Puck(puckArray[puckChosen], context, turn);
        }
    }

    // начало новой игры
    private void startGame() {
        activePointers = new SparseArray<>();
        isCollision1 = false;
        isCollision2 = false;
        loadGraphics();
        if (turn == 1) {
            turn = 2;
        } else {
            turn = 1;
        }
        isAnimation = false;
        psec = System.currentTimeMillis();
    }

    // пауза игры
    void setPause() {
        if (!startingCountdown) {
            pause = true;
            pauseStart = System.currentTimeMillis();
        }
    }

    // обновление игры
    private void update() {
        if (!isAnimation & !startingCountdown & !pause) {
            checkWinner();
            checkCollision();
            if (multiplayer) {
                if (dragChanged1) {
                    player1.setV(delta);
                    dragChanged1 = false;
                }
            } else {
                moveBot();
            }
            if (dragChanged2) {
                player2.setV(delta);
                dragChanged2 = false;
            }
            checkGoal();
        }
        if (!startingCountdown & !pause) {
            player1.update(delta, isAnimation || !multiplayer);
            player2.update(delta, isAnimation);
            puck.update(delta, isAnimation);
        }
        // проверка завершения анимации после гола
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
        // проверка завершения стартового отсчёта
        if (!loadingGame) {
            if (sec - startTime > 3000 & startingCountdown) {
                startingCountdown = false;
                startTime = sec;
            }
        }
        if (!pause) {
            timeRemaining = gameLengthTime - (sec - startTime);
            if (timeRemaining / 1000 % 60 <= 9) {
                time = timeRemaining / 1000 / 60 + ":0" + timeRemaining / 1000 % 60;
            } else {
                time = timeRemaining / 1000 / 60 + ":" + timeRemaining / 1000 % 60;
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

    // проверка победителя и завершения игры
    private void checkWinner() {
        switch (gameMode) {
            case GAME_MODE_POINTS:
                if (count1 >= goalThreshold) {
                    Intent intent = new Intent(context, WinActivity.class);
                    intent.putExtra("winner", 1);
                    intent.putExtra("multiplayer", multiplayer);
                    intent.putExtra("firstWin", firstWin);
                    firstWin = false;
                    context.startActivity(intent);
                } else if (count2 >= goalThreshold) {
                    Intent intent = new Intent(context, WinActivity.class);
                    intent.putExtra("winner", 2);
                    intent.putExtra("multiplayer", multiplayer);
                    intent.putExtra("firstWin", firstWin);
                    firstWin = false;
                    context.startActivity(intent);
                }
                break;
            case GAME_MODE_TIME:
                if (timeRemaining <= 0) {
                    if (count1 > count2) {
                        Intent intent = new Intent(context, WinActivity.class);
                        intent.putExtra("winner", 1);
                        intent.putExtra("multiplayer", multiplayer);
                        intent.putExtra("firstWin", firstWin);
                        firstWin = false;
                        context.startActivity(intent);
                    } else if (count2 > count1) {
                        Intent intent = new Intent(context, WinActivity.class);
                        intent.putExtra("winner", 2);
                        intent.putExtra("multiplayer", multiplayer);
                        intent.putExtra("firstWin", firstWin);
                        firstWin = false;
                        context.startActivity(intent);
                    } else {
                        Intent intent = new Intent(context, WinActivity.class);
                        intent.putExtra("winner", 0);
                        intent.putExtra("multiplayer", multiplayer);
                        intent.putExtra("firstWin", firstWin);
                        firstWin = false;
                        context.startActivity(intent);
                    }
                }
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

    // рисование
    private void drawOnCanvas(Canvas canvas) {
        canvas.drawBitmap(background, 0, 0, bitmapPaint);
        lowerGate.draw(canvas);
        upperGate.draw(canvas);
        paint.getTextBounds(String.valueOf(count1), 0, String.valueOf(count1).length(), bounds);
        path.reset();
        path.moveTo((settings.width + paint.measureText(String.valueOf(count1))) / 2f, (settings.height / 1.9f - bounds.height()) / 2f);
        path.lineTo((settings.width - paint.measureText(String.valueOf(count1))) / 2f, (settings.height / 1.9f - bounds.height()) / 2f);
        canvas.drawTextOnPath(String.valueOf(count1), path, 0, 0, paint);
        if (!startingCountdown & gameMode == GAME_MODE_TIME) {
            canvas.drawTextOnPath(time, timerPath, 0, 0, timerPaint);
        }
        paint.getTextBounds(String.valueOf(count2), 0, String.valueOf(count2).length(), bounds);
        canvas.drawText(String.valueOf(count2), (settings.width - paint.measureText(String.valueOf(count2))) / 2f, (settings.height * 1.475f + bounds.height()) / 2f, paint);
        player1.drawShadow(canvas);
        player2.drawShadow(canvas);
        puck.drawShadow(canvas);
        player1.draw(canvas);
        player2.draw(canvas);
        puck.draw(canvas);
        if (startingCountdown) {
            canvas.drawColor(ContextCompat.getColor(context, R.color.transparentGrey));
            if (!loadingGame) {
                String countdown = String.valueOf((int) Math.ceil((3000 - System.currentTimeMillis() + startTime) / 1000d));
                countdownPaint.getTextBounds(countdown, 0, 1, bounds);
                canvas.drawText(countdown, (settings.width - countdownPaint.measureText(countdown)) / 2f, (settings.height + bounds.height()) / 2f, countdownPaint);
                switch (gameMode) {
                    case GAME_MODE_POINTS:
                        String tip = context.getResources().getQuantityString(R.plurals.countdown_mode_points, goalThreshold, goalThreshold);
                        smallCountdownPaint.getTextBounds(tip, 0, tip.length(), bounds);
                        canvas.drawText(tip, (settings.width - smallCountdownPaint.measureText(tip)) / 2f, 0.8f * settings.height + bounds.height() / 2f, smallCountdownPaint);
                        break;
                    case GAME_MODE_TIME:
                        String tip2 = context.getResources().getQuantityString(R.plurals.countdown_mode_time, gameLengthTime / 60 / 1000, gameLengthTime / 60 / 1000);
                        smallCountdownPaint.getTextBounds(tip2, 0, tip2.length(), bounds);
                        canvas.drawText(tip2, (settings.width - smallCountdownPaint.measureText(tip2)) / 2f, 0.8f * settings.height + bounds.height() / 2f, smallCountdownPaint);
                        break;
                }
            }
        }
        if (pause) {
            canvas.drawColor(ContextCompat.getColor(context, R.color.transparentGrey));
            play.draw(canvas);
            back.draw(canvas);
        }
    }

    // проверка столковения шайбы
    private void checkCollision() {
        // проверка столкновения с верхней/нижней стенкой, пролетит ли в ворота
        if (puck.x < upperGate.rightCorner & puck.x > upperGate.leftCorner) {
            if (puck.y < settings.puckScale) {
                if (calculateLength(puck.x, puck.y, lowerGate.leftCorner, 0) < settings.playerScale) {
                    collision(new Vector(0, 0), Math.acos((puck.x - lowerGate.leftCorner) / calculateLength(puck.x, puck.y, lowerGate.leftCorner, 0)), false);
                } else if (calculateLength(puck.x, puck.y, lowerGate.rightCorner, 0) < settings.playerScale) {
                    collision(new Vector(0, 0), Math.acos((puck.x - lowerGate.rightCorner) / calculateLength(puck.x, puck.y, lowerGate.rightCorner, 0)), false);
                }
            } else if (puck.y > settings.height - settings.puckScale) {
                if (calculateLength(puck.x, puck.y, upperGate.leftCorner, settings.height) < settings.playerScale) {
                    collision(new Vector(0, 0), Math.acos((puck.x - upperGate.leftCorner) / calculateLength(puck.x, puck.y, upperGate.leftCorner, settings.height)), false);
                } else if (calculateLength(puck.x, puck.y, upperGate.rightCorner, settings.height) < settings.playerScale) {
                    collision(new Vector(0, 0), Math.acos((puck.x - upperGate.rightCorner) / calculateLength(puck.x, puck.y, upperGate.rightCorner, settings.height)), false);
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
        // проверка столкновения с верхней битой
        if ((calculateLength(puck.x, puck.y, player1.x, player1.y) < settings.playerScale + settings.puckScale - 5) & !isCollision1) {
            if (player1.y <= puck.y) {
                collision(player1.v, Math.acos((puck.x - player1.x) / calculateLength(puck.x, puck.y, player1.x, player1.y)), true);
            } else {
                collision(player1.v, Math.acos((player1.x - puck.x) / calculateLength(puck.x, puck.y, player1.x, player1.y)), true);
            }
            isCollision1 = true;
        }
        if (!(calculateLength(puck.x, puck.y, player1.x, player1.y) < settings.playerScale + settings.puckScale - 5)) {
            isCollision1 = false;
        }
        // проверка столкновения с нижней битой
        if ((calculateLength(puck.x, puck.y, player2.x, player2.y) < settings.playerScale + settings.puckScale - 5) & !isCollision2) {
            if (player2.y <= puck.y) {
                collision(player2.v, Math.acos((puck.x - player2.x) / calculateLength(puck.x, puck.y, player2.x, player2.y)), true);
            } else {
                collision(player2.v, Math.acos((player2.x - puck.x) / calculateLength(puck.x, puck.y, player2.x, player2.y)), true);
            }
            isCollision2 = true;
        }
        if (!(calculateLength(puck.x, puck.y, player2.x, player2.y) < settings.playerScale + settings.puckScale - 5)) {
            isCollision2 = false;
        }
    }

    // движение верхней биты, если игра против ИИ
    private void moveBot() {
        if (puck.y - settings.puckScale < settings.height / 2 & calculateLength(puck.x, puck.y, player1.x, player1.y) >= settings.playerScale + settings.puckScale - 5) {
            // движение к шайбе, если она на верхней половине поля
            double y = capSpeed / Math.sqrt(Math.pow((puck.x - player1.x) / (puck.y - player1.y), 2) + 1);
            if (puck.y < player1.y) {
                y = -Math.abs(y);
            }
            double x = Math.sqrt(Math.pow(capSpeed, 2) - Math.pow(y, 2));
            if (puck.x < player1.x) {
                x = -Math.abs(x);
            }
            player1.v.setVector(x, y);
        } else if (calculateLength(player1.x, player1.y, settings.width / 2, (int) (1.4 * settings.playerScale)) > player1.v.v * delta) {
            // движение к стратовой позиции, если шайба не на верхней половине поля
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
            // остановка, если достиг стартовой позиции
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
            // расчёт физики выполняется UPS раз в секунду
            sec = System.currentTimeMillis();
            delta = sec - psec;
            if (delta >= 1000 / settings.UPS) {
                update();
                psec = sec;
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
                x = pointF.x;
                y = pointF.y;
                // нажатие на кнопки "продолжить" и "выйти" возможно только во время паузы
                if (pause) {
                    if (play.isClicked(x, y)) {
                        pause = false;
                        // увеличение времени игры на время при паузе
                        gameLengthTime += sec - pauseStart;
                    }
                    if (back.isClicked(x, y)) {
                        Intent intent = new Intent(context, GameCustomActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        context.startActivity(intent);
                    }
                }
                // двигать верхней битой можно только при мультиплеере
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
                            player1.x = point.x;
                            player1.y = point.y;
                            dragChanged1 = true;
                        }
                        if (isDragging2 & (pointerId == dragPointer2)) {
                            player2.x = point.x;
                            player2.y = point.y;
                            dragChanged2 = true;
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
                    if (!isAnimation & !pause) {
                        player1.resetV();
                    }
                }
                if (pointerId == dragPointer2) {
                    isDragging2 = false;
                    activePointers.remove(dragPointer2);
                    dragPointer2 = -1;
                    if (!isAnimation & !pause) {
                        player2.resetV();
                    }
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
            graphicalThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void releaseMemory() {
        background.recycle();
    }

    public void resumeDrawing() {
        draw = true;
        thread = new Thread(this);
        graphicalThread = new Thread(graphicalRunnable);
        psec = System.currentTimeMillis();
        thread.start();
        graphicalThread.start();
    }
}
