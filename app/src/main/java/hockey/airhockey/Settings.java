/*
 * Created by Alexey Kiselev
 * Copyright (c) 2018 . All rights reserved.
 * Last modified 30.06.18 14:00
 */

package hockey.airhockey;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.Display;

final class Settings {

    final double frictionValue;
    final int width, height, playerScale, puckScale, gateHeight, goalStopTime, startAnimStopTime;
    final int numberOfPucks, numberOfPlayers, UPS;
    final float baseVolume, baseBackgroundVolume;
    final boolean friction;

    Settings(Context context) {
        Display display = ((AppCompatActivity) context).getWindowManager().getDefaultDisplay();
        DisplayMetrics metrics = new DisplayMetrics();
        display.getRealMetrics(metrics);
        width = metrics.widthPixels;
        height = metrics.heightPixels;
        playerScale = width / context.getResources().getInteger(R.integer.player_scale);
        puckScale = width / context.getResources().getInteger(R.integer.puck_scale);
        gateHeight = height / context.getResources().getInteger(R.integer.gate_height);
        friction = context.getResources().getBoolean(R.bool.friction);
        baseVolume = context.getResources().getInteger(R.integer.base_volume) / 100f;
        baseBackgroundVolume = context.getResources().getInteger(R.integer.base_background_volume) / 100f;
        goalStopTime = context.getResources().getInteger(R.integer.goal_stop);
        startAnimStopTime = context.getResources().getInteger(R.integer.start_anim_stop);
        frictionValue = context.getResources().getInteger(R.integer.friction_value) / 1000d;
        numberOfPucks = context.getResources().getInteger(R.integer.number_of_pucks) - 1;
        numberOfPlayers = context.getResources().getInteger(R.integer.number_of_players) - 1;
        UPS = context.getResources().getInteger(R.integer.max_ups);
    }
}
