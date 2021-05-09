package com.danielpclin.tetromino;

import com.danielpclin.helpers.*;

import java.util.Arrays;

public class Tetromino {

    private Point point = new Point(5, 20);
    private Rotation rotation = Rotation.ZERO;
    private Block block = Block.NONE;

    private static final Vector[][][] TETROMINO_OFFSET = { // SRS Rotation System
            { // I Tetromino Offset Data
                    { new Vector( 0, 0), new Vector(-1, 0), new Vector( 2, 0), new Vector(-1, 0), new Vector( 2, 0) },
                    { new Vector(-1, 0), new Vector( 0, 0), new Vector( 0, 0), new Vector( 0, 1), new Vector( 0,-2) },
                    { new Vector(-1, 1), new Vector( 1, 1), new Vector(-2, 1), new Vector( 1, 0), new Vector(-2, 0) },
                    { new Vector( 0, 1), new Vector( 0, 1), new Vector( 0, 1), new Vector( 0,-1), new Vector( 0, 2) }
            },
            { // O Tetromino Offset Data
                    { new Vector( 0, 0) },
                    { new Vector( 0,-1) },
                    { new Vector(-1,-1) },
                    { new Vector(-1, 0) }
            },
            { // J, L, S, T, Z Tetromino Offset Data
                    { new Vector( 0, 0), new Vector( 0, 0), new Vector( 0, 0), new Vector( 0, 0), new Vector( 0, 0) },
                    { new Vector( 0, 0), new Vector( 1, 0), new Vector( 1,-1), new Vector( 0, 2), new Vector( 1, 2) },
                    { new Vector( 0, 0), new Vector( 0, 0), new Vector( 0, 0), new Vector( 0, 0), new Vector( 0, 0) },
                    { new Vector( 0, 0), new Vector(-1, 0), new Vector(-1,-1), new Vector( 0, 2), new Vector(-1, 2) }
            }
    };

    public static final Vector[][][] TETROMINO_SHAPE_VECTOR = { // SRS Rotation System
            { // Block.NONE
                    { new Vector( 0, 0), new Vector( 0, 0), new Vector( 0, 0), new Vector( 0, 0) }, // Rotation.ZERO
                    { new Vector( 0, 0), new Vector( 0, 0), new Vector( 0, 0), new Vector( 0, 0) }, // Rotation.RIGHT
                    { new Vector( 0, 0), new Vector( 0, 0), new Vector( 0, 0), new Vector( 0, 0) }, // Rotation.TWO
                    { new Vector( 0, 0), new Vector( 0, 0), new Vector( 0, 0), new Vector( 0, 0) }  // Rotation.LEFT
            },
            { // Block.I
                    { new Vector( 0, 0), new Vector(-1, 0), new Vector( 1, 0), new Vector( 2, 0) }, // Rotation.ZERO
                    { new Vector( 0, 0), new Vector( 0, 1), new Vector( 0,-1), new Vector( 0,-2) }, // Rotation.RIGHT
                    { new Vector( 0, 0), new Vector(-2, 0), new Vector(-1, 0), new Vector( 1, 0) }, // Rotation.TWO
                    { new Vector( 0, 0), new Vector( 0, 2), new Vector( 0, 1), new Vector( 0,-1) }  // Rotation.LEFT
            },
            { // Block.O
                    { new Vector( 0, 0), new Vector( 1, 0), new Vector( 0, 1), new Vector( 1, 1) }, // Rotation.ZERO
                    { new Vector( 0, 0), new Vector( 1, 0), new Vector( 0,-1), new Vector( 1,-1) }, // Rotation.RIGHT
                    { new Vector( 0, 0), new Vector(-1, 0), new Vector( 0,-1), new Vector(-1,-1) }, // Rotation.TWO
                    { new Vector( 0, 0), new Vector(-1, 0), new Vector( 0, 1), new Vector(-1, 1) }  // Rotation.LEFT
            },
            { // Block.J
                    { new Vector( 0, 0), new Vector(-1, 0), new Vector( 1, 0), new Vector(-1, 1) }, // Rotation.ZERO
                    { new Vector( 0, 0), new Vector( 0, 1), new Vector( 0,-1), new Vector( 1, 1) }, // Rotation.RIGHT
                    { new Vector( 0, 0), new Vector(-1, 0), new Vector( 1, 0), new Vector( 1,-1) }, // Rotation.TWO
                    { new Vector( 0, 0), new Vector( 0, 1), new Vector( 0,-1), new Vector(-1,-1) }  // Rotation.LEFT
            },
            { // Block.L
                    { new Vector( 0, 0), new Vector(-1, 0), new Vector( 1, 0), new Vector( 1, 1) }, // Rotation.ZERO
                    { new Vector( 0, 0), new Vector( 0, 1), new Vector( 0,-1), new Vector( 1,-1) }, // Rotation.RIGHT
                    { new Vector( 0, 0), new Vector(-1, 0), new Vector( 1, 0), new Vector(-1,-1) }, // Rotation.TWO
                    { new Vector( 0, 0), new Vector( 0, 1), new Vector( 0,-1), new Vector(-1, 1) }  // Rotation.LEFT
            },
            { // Block.S
                    { new Vector( 0, 0), new Vector( 0, 1), new Vector( 1, 1), new Vector(-1, 0) }, // Rotation.ZERO
                    { new Vector( 0, 0), new Vector( 1, 0), new Vector( 1,-1), new Vector( 0, 1) }, // Rotation.RIGHT
                    { new Vector( 0, 0), new Vector( 0,-1), new Vector(-1,-1), new Vector( 1, 0) }, // Rotation.TWO
                    { new Vector( 0, 0), new Vector(-1, 0), new Vector(-1, 1), new Vector( 0,-1) }  // Rotation.LEFT
            },
            { // Block.T
                    { new Vector( 0, 0), new Vector(-1, 0), new Vector( 1, 0), new Vector( 0, 1) }, // Rotation.ZERO
                    { new Vector( 0, 0), new Vector( 0, 1), new Vector( 0,-1), new Vector( 1, 0) }, // Rotation.RIGHT
                    { new Vector( 0, 0), new Vector(-1, 0), new Vector( 1, 0), new Vector( 0,-1) }, // Rotation.TWO
                    { new Vector( 0, 0), new Vector( 0, 1), new Vector( 0,-1), new Vector(-1, 0) }  // Rotation.LEFT
            },

            { // Block.Z
                    { new Vector( 0, 0), new Vector( 0, 1), new Vector(-1, 1), new Vector( 1, 0) }, // Rotation.ZERO
                    { new Vector( 0, 0), new Vector( 1, 0), new Vector( 1, 1), new Vector( 0,-1) }, // Rotation.RIGHT
                    { new Vector( 0, 0), new Vector( 0,-1), new Vector( 1,-1), new Vector(-1, 0) }, // Rotation.TWO
                    { new Vector( 0, 0), new Vector(-1, 0), new Vector(-1,-1), new Vector( 0, 1) }  // Rotation.LEFT
            }
    };

    public Tetromino() {

    }

    public Tetromino(Block block, Point point) {
        this.block = block;
        this.point = point;
    }

    private Tetromino(Block block, Point point, Rotation rotation) {
        this.block = block;
        this.point = point;
        this.rotation = rotation;
    }

    // Vectors representing tetromino
    private Vector[] getVectors(){
        return getVectors(rotation);
    }

    private Vector[] getVectors(Rotation rotation){
        return TETROMINO_SHAPE_VECTOR[block.ordinal()][rotation.ordinal()];
    }

    // Points representing tetromino
    public Point[] getPoints(){
        return Arrays.stream(getVectors())
                .map(vector -> point.add(vector))
                .toArray(Point[]::new);
    }

    public Point[] getPoints(Rotation rotation){
        return getPoints(rotation, point);
    }

    public Point[] getPoints(Rotation rotation, Point point){
        return Arrays.stream(getVectors(rotation))
                .map(point::add)
                .toArray(Point[]::new);
    }

    private Vector getRotationPointOffset(Rotation newRotation, int offset){
        Vector pointOffset;
        switch (this.block){
            case NONE:
                return null;
            case J:
            case L:
            case S:
            case T:
            case Z:
                pointOffset = TETROMINO_OFFSET[2][rotation.ordinal()][offset].subtract(TETROMINO_OFFSET[2][newRotation.ordinal()][offset]);
                break;
            case I:
                pointOffset = TETROMINO_OFFSET[0][rotation.ordinal()][offset].subtract(TETROMINO_OFFSET[0][newRotation.ordinal()][offset]);
                break;
            case O:
                if(offset != 0) {
                    throw new IllegalArgumentException("Invalid offset number");
                }
                pointOffset = TETROMINO_OFFSET[1][rotation.ordinal()][offset].subtract(TETROMINO_OFFSET[1][newRotation.ordinal()][offset]);
                break;
            default:
                return null;
        }
        return pointOffset;
    }

    public Point[] getClockwisePoints(int offset){
        return getPoints(rotation.clockwiseRotation(), point.add(getRotationPointOffset(rotation.clockwiseRotation(), offset)));
    }

    public Point[] getClockwisePoints(){
        return getClockwisePoints(0);
    }

    public Point[] getCounterClockwisePoints(int offset){
        return getPoints(rotation.counterClockwiseRotation(), point.add(getRotationPointOffset(rotation.counterClockwiseRotation(), offset)));
    }

    public Point[] getCounterClockwisePoints(){
        return getCounterClockwisePoints(0);
    }

    public void rotateClockwise(int offset){
        point = point.add(getRotationPointOffset(rotation.clockwiseRotation(), offset));
        rotation = rotation.clockwiseRotation();
    }

    public void rotateCounterClockwise(int offset){
        point = point.add(getRotationPointOffset(rotation.counterClockwiseRotation(), offset));
        rotation = rotation.counterClockwiseRotation();
    }

    public Point[] getDownPoints(){
        return Arrays.stream(getVectors())
                .map(vector -> point.add(vector).add(new Vector(0, -1)))
                .toArray(Point[]::new);
    }

    public Point[] getRightPoints(){
        return Arrays.stream(getVectors())
                .map(vector -> point.add(vector).add(new Vector(1, 0)))
                .toArray(Point[]::new);
    }

    public Point[] getLeftPoints(){
        return Arrays.stream(getVectors())
                .map(vector -> point.add(vector).add(new Vector(-1, 0)))
                .toArray(Point[]::new);
    }

    public void moveDown(){
        point = point.add(new Vector(0, -1));
    }

    public void moveRight(){
        point = point.add(new Vector(1, 0));
    }

    public void moveLeft(){
        point = point.add(new Vector(-1, 0));
    }

    public Block getBlock() {
        return block;
    }

    public void setBlock(Block block) {
        point = new Point(5, 20);
        rotation = Rotation.ZERO;
        this.block = block;
    }

    @Override
    public String toString() {
        return "Tetromino{" +
                "point.x=" + point.getX() + ", point.y=" + point.getY() +
                ", rotation=" + rotation + " (" + rotation.ordinal() + ")" +
                ", block=" + block + " (" + block.ordinal() + ")" +
                '}';
    }
}
