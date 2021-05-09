package com.danielpclin.helpers;

public class Point {

    int x, y;

    public Point(int x, int y) {
        this.x = x;
        this.y = y;
    }
    public Point() {
        this(0, 0);
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public Vector subtract(Point point){
        return new Vector(this.x - point.x, this.y - point.y);
    }

    public Vector add(Point point){
        return new Vector(this.x + point.x, this.y + point.y);
    }

    public Point subtract(Vector vector){
        return new Point(this.x - vector.x, this.y - vector.y);
    }

    public Point add(Vector vector){
        return new Point(this.x + vector.x, this.y + vector.y);
    }

    public Boolean equals(Point point){
        return this.x == point.x && this.y == point.y;
    }

    public static Boolean equals(Point point1, Point point2){
        return point1.x == point2.x && point1.y == point2.y;
    }

    @Override
    public String toString() {
        return "Point{" +
                "x=" + x +
                ", y=" + y +
                '}';
    }
}
