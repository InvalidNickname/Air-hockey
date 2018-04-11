package hockey.airhockey;

class Vector {

    double x, y;

    Vector(double x, double y) {
        setVector(x, y);
    }

    void setVector(double x, double y) {
        this.x = x;
        this.y = y;
    }

    Vector deductVector(Vector v) {
        return new Vector(this.x - v.x, this.y - v.y);
    }

    Vector multiplyVector(double multiplier) {
        double x, y;
        x = multiplier * this.x / 1000;
        y = multiplier * this.y / 1000;
        return new Vector(x, y);
    }
}