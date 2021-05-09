package com.danielpclin;

import com.danielpclin.tetromino.Block;
import com.danielpclin.tetromino.Tetromino;

import java.util.ArrayList;
import java.util.Random;

public class Tetris {

    private Board gameBoard = new Board();
    private Tetromino tetromino = new Tetromino();
    private Block holdTetromino = Block.NONE;
    private boolean canHold = true;
    private Block nextTetromino = Block.NONE;
    private ArrayList<Block> tetrominoPickQueue = new ArrayList<>();
    private Random random = new Random();
    private boolean paused = false;
    private boolean gameover = false;
    private int clearedLines;

    public void togglePause(){
        paused = !paused;
    }

    public boolean isPaused(){
        return paused;
    }

    public void initializeGame(){
        if (tetromino.getBlock().equals(Block.NONE)){
            pickTetromino();
        }
    }

    public void updateGame(){
        if(paused){
            return;
        }
        tetrominoTryMoveDown();
    }

    public boolean tetrominoLock(){
        if (!gameBoard.testValidMove(tetromino.getDownPoints())){

            gameBoard.placeTetromino(tetromino);
            clearFullLines();
            pickTetromino();
            return true;
        }
        return false;
    }

    private boolean tetrominoCanPlace(){
        return gameBoard.testValidMove(tetromino.getPoints());
    }

    public void tetrominoTryMoveRight(){
        if (gameBoard.testValidMove(tetromino.getRightPoints())){
            tetromino.moveRight();
        }
    }

    public void tetrominoTryMoveLeft(){
        if (gameBoard.testValidMove(tetromino.getLeftPoints())){
            tetromino.moveLeft();
        }
    }

    public void tetrominoTryClockwise(){
        for (int i = 0; i < 5; i++){
            if (gameBoard.testValidMove(tetromino.getClockwisePoints(i))){
                tetromino.rotateClockwise(i);
                break;
            }
        }
    }

    public void tetrominoTryCounterClockwise(){
        for (int i = 0; i < 5; i++){
            if (gameBoard.testValidMove(tetromino.getCounterClockwisePoints(i))){
                tetromino.rotateCounterClockwise(i);
                break;
            }
        }
    }

    public void clearFullLines(){
        clearedLines += gameBoard.clearFullLines();
    }

    private void pickTetromino(){
        if (tetrominoPickQueue.size() == 0){
            tetrominoPickQueue.addAll(Block.PLACEABLE_BLOCKS);
        }
        if (nextTetromino.equals(Block.NONE)){
            nextTetromino = tetrominoPickQueue.remove(random.nextInt(tetrominoPickQueue.size()));
        }
        tetromino.setBlock(nextTetromino);
        if (tetrominoCanPlace()){
            nextTetromino = tetrominoPickQueue.remove(random.nextInt(tetrominoPickQueue.size()));
            canHold = true;
        } else {
            gameover = true;
            tetromino.setBlock(Block.NONE);
        }
    }

    public void tetrominoHardDrop(){
        while (gameBoard.testValidMove(tetromino.getDownPoints())){
            tetromino.moveDown();
        }
        tetrominoLock();
    }

    public boolean tetrominoTryHold(){
        if (canHold) {
            if (holdTetromino.equals(Block.NONE)){
                holdTetromino = tetromino.getBlock();
                pickTetromino();
            } else {
                Block block = holdTetromino;
                holdTetromino = tetromino.getBlock();
                tetromino.setBlock(block);
            }
            canHold = false;
            return true;
        }
        return false;
    }

    public boolean tetrominoTryMoveDown(){
        if (tetrominoCanMoveDown()){
            tetromino.moveDown();
            return true;
        }
        return false;
    }

    public boolean tetrominoCanMoveDown(){
        if (gameBoard.testValidMove(tetromino.getDownPoints())){
            return true;
        }
        return false;
    }

    public Block getHoldTetromino() {
        return holdTetromino;
    }

    public Block getNextTetromino() {
        return nextTetromino;
    }

    public Board getGameBoard() {
        return gameBoard;
    }

    public Tetromino getTetromino() {
        return tetromino;
    }

    public boolean isGameOver() {
        return gameover;
    }

    public int getClearedLines() {
        return clearedLines;
    }
}