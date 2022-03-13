package com.danielpclin;

import com.danielpclin.helpers.Point;
import com.danielpclin.helpers.Vector;
import com.danielpclin.tetromino.Block;
import com.danielpclin.tetromino.Tetromino;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

public class GameController {

    @FXML private Label clearedLines;
    @FXML private Label messageLabel;
    @FXML private HBox hbox;
    @FXML private Canvas holdTetrominoCanvas, gameBoardCanvas, gameBoardGridCanvas, nextTetrominoCanvas;
    @FXML private Label gameoverLabel;
    @FXML private Button startBtn;

    private static final int BLOCK_PIXEL_LENGTH = 30;
    private static final int LINES_TO_WIN = 40;
    private GraphicsContext gameGraphicsContent, gameGridGraphicsContent,
            nextGraphicsContent, holdGraphicsContent;
    private Timer gameTimer;
    private Tetris tetris;

    private final EventHandler<KeyEvent> gameEventHandler = e-> {
        e.consume();
        if (!tetris.isGameOver()){
            switch (e.getCode()) {
                case DOWN:
                case S:
                    if (tetris.tetrominoTryMoveDown()){
                        tetrominoDelayLock();
                    }
                    break;
                case LEFT:
                case A:
                    tetris.tetrominoTryMoveLeft();
                    break;
                case RIGHT:
                case D:
                    tetris.tetrominoTryMoveRight();
                    break;
                case SPACE:
                    tetris.tetrominoHardDrop();
                    drawNext();
                    break;
                case SHIFT:
                case C:
                    if (tetris.tetrominoTryHold()) {
                        drawHold();
                        drawNext();
                    }
                    break;
                case Z:
                    tetris.tetrominoTryCounterClockwise();
                    break;
                case X:
                case UP:
                    tetris.tetrominoTryClockwise();
                    break;
                case P:
                    tetris.togglePause();
                    break;
                default:
            }
        }
        renderGame();
    };

    @FXML
    private void initialize() {
        try {
            start();
        } catch (IOException e) {
            e.printStackTrace();
        }
        gameGraphicsContent = gameBoardCanvas.getGraphicsContext2D();
        gameGridGraphicsContent = gameBoardGridCanvas.getGraphicsContext2D();
        holdGraphicsContent = holdTetrominoCanvas.getGraphicsContext2D();
        nextGraphicsContent = nextTetrominoCanvas.getGraphicsContext2D();
        drawGrid(gameGridGraphicsContent, BLOCK_PIXEL_LENGTH);
    }

    @FXML
    private void startGameBtnHandler(){
        startGame();
    }

    private void initializeSceneEventListener(){
        hbox.getScene().addEventFilter(KeyEvent.KEY_PRESSED, gameEventHandler);
    }

    private void startGame(){
        if (!(gameTimer == null)){
            gameOver();
        }
        initializeSceneEventListener();
        clearCanvas(holdGraphicsContent);
        clearCanvas(nextGraphicsContent);
        gameTimer = new Timer(true);
        tetris = new Tetris();
        Platform.runLater(()->{
            startBtn.setVisible(false);
            gameoverLabel.setVisible(false);
            messageLabel.setVisible(false);
        });
        tetris.initializeGame();
        drawNext();
        gameTimer.scheduleAtFixedRate(new TimerTask(){
            @Override
            public void run() {
                if (!tetris.isGameOver()){
                    doGameCycle();
                } else {
                    gameOver();
                }
            }
        }, 0, 1000);
    }

    private void broadcastGameOver(){
        startBtn.setVisible(true);
    }

    private void gameOver(){
        hbox.getScene().removeEventFilter(KeyEvent.KEY_PRESSED, gameEventHandler);
        gameTimer.cancel();
        Platform.runLater(()->{
            startBtn.setVisible(true);
            gameoverLabel.setVisible(true);
        });
    }

    private void doGameCycle(){
        if (!tetris.isPaused()){
            tetris.updateGame();
            tetrominoDelayLock();
            renderGame();
        }
    }

    private void clearCanvas(GraphicsContext gc){
        Platform.runLater(() -> {
            gc.clearRect(0, 0, gc.getCanvas().getWidth(), gc.getCanvas().getHeight());
        });
    }

    private void drawGrid(GraphicsContext graphicsContext, int pixelWidth){
        Image background1 = new Image(getClass().getResource("/img/background1.png").toExternalForm());
        Image background2 = new Image(getClass().getResource("/img/background2.png").toExternalForm());
        for (int x = 0; x < pixelWidth * Board.BOARD_WIDTH; x += pixelWidth){
            for (int y = 0; y < pixelWidth * Board.BOARD_HEIGHT; y += pixelWidth){
                if ((x+y) % (2 * pixelWidth) == 0) {
                    graphicsContext.drawImage(background1, x, y, pixelWidth, pixelWidth);
                } else {
                    graphicsContext.drawImage(background2, x, y, pixelWidth, pixelWidth);
                }
            }
        }
    }

    private void drawBlock(Point point, Image image, GraphicsContext gc, int block_length){
        if (image!=null) {
            Platform.runLater(() -> {
                Point canvasPoint = convertPointToDraw(point, block_length);
                gc.drawImage(image, canvasPoint.getX(), canvasPoint.getY(), block_length, block_length);
            });
        }
    }

    private void drawBlock(Point point, Image image, GraphicsContext gc){
        drawBlock(point, image, gc, BLOCK_PIXEL_LENGTH);
    }

    private void drawBlock(Point point, Image image){
        drawBlock(point, image, gameGraphicsContent);
    }

    private Point convertPointToDraw(Point point, int block_length){
        return new Point((point.getX() - 1) * block_length, (Board.BOARD_HEIGHT - point.getY()) * block_length);
    }

    private Point convertPointToDraw(Point point){
        return convertPointToDraw(point, BLOCK_PIXEL_LENGTH);
    }

    private void drawTetromino(Tetromino tetromino){
        for (Point point : tetromino.getPoints()){
            drawBlock(point, tetromino.getBlock().getImage());
        }
    }

    private void drawBoard(Board gameBoard){
        Block[][] boardMap = gameBoard.getBoardMap();
        for (int width = 0; width < boardMap.length; width++){
            for (int height = 0; height < boardMap[width].length; height++){
                drawBlock(new Point(width + 1, height + 1), boardMap[width][height].getImage());
            }
        }
    }

    private void drawBoard(Board gameBoard, GraphicsContext graphicsContext, int blockLength){
        Block[][] boardMap = gameBoard.getBoardMap();
        for (int width = 0; width < boardMap.length; width++){
            for (int height = 0; height < boardMap[width].length; height++){
                drawBlock(new Point(width + 1, height + 1), boardMap[width][height].getImage(), graphicsContext, blockLength);
            }
        }
    }

    private void renderGame(){
        clearCanvas(gameGraphicsContent);
        drawBoard(tetris.getGameBoard());
        drawTetromino(tetris.getTetromino());
        System.out.println("Cleared Lines - " + tetris.getClearedLines());
        clearedLines.setText("CL: " + tetris.getClearedLines());
        if (tetris.getClearedLines() >= LINES_TO_WIN) {
            gameOver();
            broadcastGameOver();
        }
    }

    private void drawNext() {
        drawSideCanvas(tetris.getNextTetromino(), nextGraphicsContent, nextTetrominoCanvas);
    }

    private void drawSideCanvas(Block nextTetromino, GraphicsContext nextGraphicsContent, Canvas nextTetrominoCanvas) {
        final Vector drawOffset;
        switch (nextTetromino){
            case I:
                drawOffset = new Vector(15 + BLOCK_PIXEL_LENGTH, 20 - BLOCK_PIXEL_LENGTH/2);
                break;
            case O:
                drawOffset = new Vector(15 + BLOCK_PIXEL_LENGTH, 20);
                break;
            case J:
            case L:
            case S:
            case T:
            case Z:
                drawOffset = new Vector(30 + BLOCK_PIXEL_LENGTH, 20);
                break;
            default:
                drawOffset = null;
        }
        if (drawOffset != null) {
            Platform.runLater(() -> {
                nextGraphicsContent.clearRect(0, 0, nextTetrominoCanvas.getWidth(), nextTetrominoCanvas.getHeight());
                for (Vector vector : Tetromino.TETROMINO_SHAPE_VECTOR[nextTetromino.ordinal()][0]) {
                    Point point = vector.asPoint();
                    nextGraphicsContent.drawImage(nextTetromino.getImage(),
                            point.getX() * BLOCK_PIXEL_LENGTH + drawOffset.getX(),
                            (1 - point.getY()) * BLOCK_PIXEL_LENGTH + drawOffset.getY());
                }
            });
        }
    }

    private void drawHold() {
        if (!tetris.getHoldTetromino().equals(Block.NONE)) {
            drawSideCanvas(tetris.getHoldTetromino(), holdGraphicsContent, holdTetrominoCanvas);
        }
    }

    private void tetrominoDelayLock(){
        if (!tetris.tetrominoCanMoveDown()) {
            Timer timer = new Timer(true);
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    if (tetris.tetrominoLock()){
                        renderGame();
                        drawNext();
                    }
                }
            }, 750);
        }
    }

    public void start() throws IOException{
        startBtn.setVisible(true);
    }
}
