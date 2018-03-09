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

    Vector addVector(Vector v) {
        return new Vector(this.x + v.x, this.y + v.y);
    }

    Vector getProjection(Vector v, double yAxisAngle) {
        double x, y;
        y = v.x * Math.cos(yAxisAngle) + v.y * Math.sin(yAxisAngle);
        x = v.x * Math.sin(yAxisAngle) + v.y * Math.cos(yAxisAngle);
        return new Vector(x, y);
    }

    Vector multiplyVector(double multiplier) {
        double x, y;
        x = multiplier * this.x / 1000;
        y = multiplier * this.y / 1000;
        return new Vector(x, y);
    }
}