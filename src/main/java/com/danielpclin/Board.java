package com.danielpclin;

import com.danielpclin.helpers.Point;
import com.danielpclin.tetromino.Block;
import com.danielpclin.tetromino.Tetromino;

import java.util.Arrays;

class Board {

    public static final int BOARD_HEIGHT = 20;
    public static final int BOARD_WIDTH = 10;

    private Block[][] boardMap;

    Board(){
        this.boardMap = new Block[BOARD_WIDTH][BOARD_HEIGHT];
        for (Block[] column: this.boardMap) {
            Arrays.fill(column, Block.NONE);
        }
    }

    private Board(Block[][] boardMap){
        if (boardMap.length == BOARD_WIDTH || boardMap[0].length == BOARD_HEIGHT){
            this.boardMap = boardMap;
        } else {
            this.boardMap = new Block[BOARD_WIDTH][BOARD_HEIGHT];
            for (Block[] column: this.boardMap) {
                Arrays.fill(column, Block.NONE);
            }
        }
    }

    public void placeTetromino(Tetromino tetromino){
        for (Point point : tetromino.getPoints()){
            if (point.getX() > BOARD_WIDTH || point.getY() > BOARD_HEIGHT) {
                continue;
            }
            if (boardMap[point.getX()-1][point.getY()-1].equals(Block.NONE)){
                boardMap[point.getX()-1][point.getY()-1] = tetromino.getBlock();
            } else {
                throw new IllegalArgumentException("Tried to place tetromino on existing block");
            }
        }
    }

    public Block[][] getBoardMap() {
        return boardMap;
    }

    public Boolean testValidMove(Point[] points){
        for (Point point : points){
            if (point.getX() > Board.BOARD_WIDTH || point.getX() < 1 || point.getY() < 1){
                return false;
            } else if (point.getY() > Board.BOARD_HEIGHT) {
                // Don't check board if block is above playing field
                continue;
            }
            if(!boardMap[point.getX()-1][point.getY()-1].equals(Block.NONE)){
                return false;
            }
        }
        return true;
    }

    public int clearFullLines(){
        int removedLines = 0;
        int[] shiftLines = new int[BOARD_HEIGHT];
        int linesToShift = 0;
        for (int height = 0; height < BOARD_HEIGHT; height++){
            boolean needRemove = true;
            for (int width = 0; width < BOARD_WIDTH; width++){
                if (boardMap[width][height].equals(Block.NONE)) {
                    needRemove = false;
                    break;
                }
            }
            if (needRemove) {
                linesToShift++;
                removedLines++;
            } else {
                shiftLines[height] = linesToShift;
            }
        }
        for (int height = 0; height < BOARD_HEIGHT; height++){
            if (shiftLines[height]>0) {
                for (int width = 0; width < BOARD_WIDTH; width++) {
                    boardMap[width][height - shiftLines[height]] = boardMap[width][height];
                }
            }
        }
        for (int height = BOARD_HEIGHT - linesToShift; height < BOARD_HEIGHT; height++) {
            for (int width = 0; width < BOARD_WIDTH; width++) {
                boardMap[width][height] = Block.NONE;
            }
        }
        return removedLines;
    }

    public StringBuilder toStringBuilder(){
        StringBuilder stringBuilder = new StringBuilder(0);
        for( Block[] col: this.getBoardMap() ){
            for( Block block : col ){
                stringBuilder.append(block.toChar());
            }
        }
        return stringBuilder;
    }

    public static Board valueOf(String string){
        Block[][] boardMap = new Block[BOARD_WIDTH][BOARD_HEIGHT];
        for (int width = 0; width < boardMap.length; width++){
            for (int height = 0; height < boardMap[width].length; height++){
                boardMap[width][height] = Block.valueOf(string.charAt(width*Board.BOARD_HEIGHT+height));
            }
        }
        return new Board(boardMap);
    }
}
