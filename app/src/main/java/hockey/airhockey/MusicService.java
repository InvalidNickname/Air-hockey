package hockey.airhockey;

import android.app.ActivityManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.media.MediaPlayer;
import android.os.IBinder;

import java.io.IOException;
import java.util.List;

import static hockey.airhockey.MainActivity.runningActivitiesCounter;
import static hockey.airhockey.MainActivity.settings;
import static hockey.airhockey.MainActivity.volume;

public class MusicService extends Service {

    private MediaPlayer mediaPlayer;
    private Runnable checkerRunnable;
    private Thread checkerThread;
    private boolean run;
    private float mpVolume;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mediaPlayer = new MediaPlayer();
        try {
            AssetFileDescriptor afd = getAssets().openFd("music/big_car_theft.mp3");
            mediaPlayer.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());
            mediaPlayer.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }
        mpVolume = settings.baseBackgroundVolume * volume;
        mediaPlayer.setVolume(mpVolume, mpVolume);
        mediaPlayer.setLooping(true);
        run = true;
        checkerRunnable = new Runnable() {
            @Override
            public void run() {
                while (run) {
                    ActivityManager activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
                    List<ActivityManager.RunningTaskInfo> list;
                    if (activityManager != null) {
                        list = activityManager.getRunningTasks(10);
                        for (ActivityManager.RunningTaskInfo task : list) {
                            String s = task.topActivity.flattenToShortString();
                            if (runningActivitiesCounter == 0) {
                                if (mediaPlayer.isPlaying()) {
                                    mediaPlayer.pause();
                                }
                            } else if ("hockey.airhockey/.MainActivity".equals(s) || "hockey.airhockey/.CreditsActivity".equals(s)) {
                                stopSelf();
                            } else if ("hockey.airhockey/.GameActivity".equals(s) || "hockey.airhockey/.GameCustomActivity".equals(s) || "hockey.airhockey/.WinActivity".equals(s)) {
                                mediaPlayer.start();
                            }
                        }
                    }
                }
            }
        };
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        run = false;
        try {
            checkerThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if (mediaPlayer != null) {
            try {
                mediaPlayer.release();
                mediaPlayer = null;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        checkerThread = new Thread(checkerRunnable);
        checkerThread.start();
        return super.onStartCommand(intent, flags, startId);
    }
}
