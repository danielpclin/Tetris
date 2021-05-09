package com.danielpclin.tetromino;

public enum Rotation {
    ZERO, RIGHT, TWO, LEFT;

    private static Rotation[] rotations = values();

    public Rotation clockwiseRotation(){
        return rotations[(this.ordinal() + 1) % rotations.length];
    }

    public Rotation counterClockwiseRotation(){
        return rotations[(this.ordinal() + rotations.length - 1) % rotations.length];
    }
}
