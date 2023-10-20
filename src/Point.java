/**
 * A simple class representing a location in 2D space.
 */
public final class Point {
    private final int x;
    private final int y;

//    private Point prior;
//
//    private int g;
//    private int h;

    public Point(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int x() {
        return x;
    }

    public int y() {
        return y;
    }

    public String toString() {
        return "(" + x + "," + y + ")";
    }

    @Override
    public boolean equals(Object other) {
        return other instanceof Point && ((Point) other).x == this.x && ((Point) other).y == this.y;
    }
    public boolean adjacent(Point p2) {
        return (this.x == p2.x() && Math.abs(this.y - p2.y()) == 1) || (this.y == p2.y() && Math.abs(this.x - p2.x()) == 1);
    }

    public int hashCode() {
        int result = 17;
        result = result * 31 + x;
        result = result * 31 + y;
        return result;
    }
}