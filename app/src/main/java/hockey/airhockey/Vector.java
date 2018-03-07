package hockey.airhockey;

class Vector {

    double x, y;
    private double angle, v;

    Vector(double x, double y) {
        setVector(x, y);
    }

    void setVector(double x, double y) {
        this.x = x;
        this.y = y;
        this.v = Math.sqrt(Math.pow(x, 2) + Math.pow(y, 2));
        this.angle = Math.acos(x / v);
    }

    Vector deductVector(Vector v) {
        return new Vector(this.x - v.x, this.y - v.y);
    }
}