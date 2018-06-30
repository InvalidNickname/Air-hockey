/*
 * Created by Alexey Kiselev
 * Copyright (c) 2018 . All rights reserved.
 * Last modified 30.06.18 14:00
 */

package hockey.airhockey;

class Vector {

    double x, y, v, cos, sin;

    Vector(double x, double y) {
        setVector(x, y);
    }

    void setVector(double x, double y) {
        this.x = x;
        this.y = y;
        v = Math.sqrt(Math.pow(x, 2) + Math.pow(y, 2));
        cos = x / v;
        sin = y / v;
    }

    Vector deductVector(Vector v) {
        return new Vector(this.x - v.x, this.y - v.y);
    }

    Vector multiplyVector(double multiplier) {
        return new Vector(multiplier * this.x, multiplier * this.y);
    }
}