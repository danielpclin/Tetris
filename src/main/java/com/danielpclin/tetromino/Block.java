package com.danielpclin.tetromino;

import javafx.scene.image.Image;

import java.util.List;
import java.util.Objects;

import static java.util.Arrays.asList;
import static java.util.Collections.unmodifiableList;

public enum Block {
    NONE, I, O, J, L, S, T, Z;

    private Image image;
    private char aChar;

    static {
        NONE.image = null;
        I.image = new Image(Objects.requireNonNull(Block.class.getResource("/img/blue.png")).toExternalForm());
        O.image = new Image(Objects.requireNonNull(Block.class.getResource("/img/yellow.png")).toExternalForm());
        J.image = new Image(Objects.requireNonNull(Block.class.getResource("/img/deepblue.png")).toExternalForm());
        L.image = new Image(Objects.requireNonNull(Block.class.getResource("/img/orange.png")).toExternalForm());
        S.image = new Image(Objects.requireNonNull(Block.class.getResource("/img/green.png")).toExternalForm());
        T.image = new Image(Objects.requireNonNull(Block.class.getResource("/img/pink.png")).toExternalForm());
        Z.image = new Image(Objects.requireNonNull(Block.class.getResource("/img/red.png")).toExternalForm());
    }

    static {
        NONE.aChar = 'N';
        I.aChar = 'I';
        O.aChar = 'O';
        J.aChar = 'J';
        L.aChar = 'L';
        S.aChar = 'S';
        T.aChar = 'T';
        Z.aChar = 'Z';
    }

    public Image getImage() {
        return image;
    }

    public static final List<Block> PLACEABLE_BLOCKS = unmodifiableList(asList(I, O, J, L, S, T, Z));

    public char toChar() {
        return aChar;
    }

    public static Block valueOf(char c){
        for (Block block: values()){
            if( block.aChar == c ){
                return block;
            }
        }
        return Block.NONE;
    }
}
