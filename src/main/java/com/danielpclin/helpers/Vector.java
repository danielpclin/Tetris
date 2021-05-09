package com.danielpclin.helpers;

public class Vector {

    int x, y;

    public Vector() {
        this.x = 0;
        this.y = 0;
    }

    public Vector(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public Point subtract(Point point){
        return new Point(this.x - point.x, this.y - point.y);
    }

    public Point add(Point point){
        return new Point(this.x + point.x, this.y + point.y);
    }

    public Point asPoint(){
        return new Point(this.x, this.y);
    }

    public Vector subtract(Vector vector){
        return new Vector(this.x - vector.x, this.y - vector.y);
    }

    public Vector add(Vector vector){
        return new Vector(this.x + vector.x, this.y + vector.y);
    }

    public Boolean equals(Vector vector){
        return this.x == vector.x && this.y == vector.y;
    }

    public static Boolean equals(Vector vector1, Vector vector2){
        return vector1.x == vector2.x && vector1.y == vector2.y;
    }

    @Override
    public String toString() {
        return "Vector{" +
                "x=" + x +
                ", y=" + y +
                '}';
    }
}
