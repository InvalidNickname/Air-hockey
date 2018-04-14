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
        double x, y;
        x = multiplier * this.x;
        y = multiplier * this.y;
        return new Vector(x, y);
    }
}