package com.danielpclin;

import com.danielpclin.helpers.Broadcastable;
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
import javafx.scene.layout.BorderPane;

import java.io.IOException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GameController {

    @FXML private Label clearedLines;
    @FXML private Label messageLabel;
    @FXML private BorderPane boarderPane;
    @FXML private Canvas holdTetrominoCanvas, gameBoardCanvas, gameBoardGridCanvas, nextTetrominoCanvas;
    @FXML private ArrayList<Canvas> sideGameCanvas, sideGridCanvas;
    @FXML private Label gameoverLabel;
    @FXML private Button startBtn;

    private static final int BLOCK_PIXEL_LENGTH = 30;
    private static final int MAX_PLAYERS = 4;
    private static final int LINES_TO_WIN = 40;
    private GraphicsContext gameGraphicsContent, gameGridGraphicsContent,
            nextGraphicsContent, holdGraphicsContent;
    private ArrayList<GraphicsContext> sideGameGraphicsContext = new ArrayList<>(0),
            sideGridGraphicsContext = new ArrayList<>(0);
    private Timer gameTimer;
    private Tetris tetris;
    private Broadcastable broadcastable;
    private ArrayList<String> sideClients = new ArrayList<>(0);
    private ArrayList<String> sideClientsInPlay = new ArrayList<>(0);
    private Pattern socketAddressPattern = Pattern.compile("^(\\[(?:(?:\\w+|(?:[0-9]{1,3}\\.){3}[0-9]{1,3}):\\d*)]): \\((\\d+)\\)([NIOTLJSZ]{200})");
    private boolean isServer = false;

    private EventHandler<KeyEvent> gameEventHandler = e-> {
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
            startServer();
        } catch (IOException e) {
            System.out.println("Info: Can't bind to server port. Server already exists!");
            startClient();
        }
        gameGraphicsContent = gameBoardCanvas.getGraphicsContext2D();
        gameGridGraphicsContent = gameBoardGridCanvas.getGraphicsContext2D();
        holdGraphicsContent = holdTetrominoCanvas.getGraphicsContext2D();
        nextGraphicsContent = nextTetrominoCanvas.getGraphicsContext2D();
        sideGameCanvas.forEach(canvas -> sideGameGraphicsContext.add(canvas.getGraphicsContext2D()));
        sideGridCanvas.forEach(canvas -> sideGridGraphicsContext.add(canvas.getGraphicsContext2D()));
        drawGrid(gameGridGraphicsContent, BLOCK_PIXEL_LENGTH);
        sideGridGraphicsContext.forEach(graphicsContext -> drawGrid(graphicsContext, BLOCK_PIXEL_LENGTH / 2));
    }

    @FXML
    private void startGameBtnHandler(){
        StringBuilder msg = new StringBuilder("STG- [Server:" + ((Server)broadcastable).getPort() + "]");
        sideClients.forEach(str -> msg.append(" ").append(str));
        broadcastMessage(msg.toString());
        for (int i = 0; i < MAX_PLAYERS && i < sideClients.size(); i++) {
            sideClientsInPlay.add(sideClients.get(i));
        }
        startGame();
    }

    private void initializeSceneEventListener(){
        boarderPane.getScene().addEventFilter(KeyEvent.KEY_PRESSED, gameEventHandler);
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
                    Platform.runLater(()->{
                        if (isServer && !sideClientsInPlay.isEmpty()) {
                            messageLabel.setVisible(true);
                            startBtn.setVisible(false);
                        }
                    });
                }
            }
        }, 0, 1000);
    }

    private void broadcastGameOver(){
        if (isServer) {
            broadcastMessage("GAMEOVER");
            startBtn.setVisible(true);
        }
    }

    private void gameOver(){
        boarderPane.getScene().removeEventFilter(KeyEvent.KEY_PRESSED, gameEventHandler);
        gameTimer.cancel();
        Platform.runLater(()->{
            if (isServer){
                startBtn.setVisible(true);
            } else {
                broadcastMessage("GVR- [" + ((Client)broadcastable).getClientAddress() + "]");
                messageLabel.setVisible(true);
            }
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
        Image background1 = new Image(Objects.requireNonNull(getClass().getResource("/img/background1.png")).toExternalForm());
        Image background2 = new Image(Objects.requireNonNull(getClass().getResource("/img/background2.png")).toExternalForm());
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
        broadcastMessage(prepareBroadcast(tetris.getGameBoard(), tetris.getTetromino()));
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

    private Matcher parseReceivedMessage(String message){
        return socketAddressPattern.matcher(message);
    }

    private void drawBoardToCorrespondSideCanvas(String message) {
        Matcher matcher = parseReceivedMessage(message);
        if (matcher.find()) {
            int gcIndex =  sideClients.indexOf(matcher.group(1));
            System.out.println(gcIndex);
            if (gcIndex >= MAX_PLAYERS || gcIndex < 0){
                return;
            }
            if (isServer && Integer.parseInt(matcher.group(2)) >= LINES_TO_WIN) {
                broadcastGameOver();
                gameOver();
            }
            Board board = Board.valueOf(matcher.group(3));
            clearCanvas(sideGameGraphicsContext.get(gcIndex));
            drawBoard(board, sideGameGraphicsContext.get(gcIndex), BLOCK_PIXEL_LENGTH/2);
        }
    }

    private void serverReceiveMessage(String message){
        System.out.println(message);
        if (message.startsWith("EST- ")){
            sideClients.add(message.substring(5));
        } else if (message.startsWith("DSC- ") || message.startsWith("GVR- ")) {
            sideClientsInPlay.remove(message.substring(5));
            if (sideClientsInPlay.size() == 0 && tetris.isGameOver()){
                broadcastGameOver();
            }
        } else {
            System.out.println(sideClients);
            drawBoardToCorrespondSideCanvas(message);
        }
    }

    private void clientReceiveMessage(String message){
        System.out.println(message);
        if (message.startsWith("STG- ")){
            sideClients.clear();
            Arrays.asList(message.substring(5).split(" ")).forEach(clientString->{
                if ( !("[" + ((Client)broadcastable).getClientAddress() + "]").equals(clientString) ){
                    sideClients.add(clientString);
                }
            });
            startGame();
        } else if (message.equals("GAMEOVER")) {
            if (!tetris.isGameOver()){
                gameOver();
            }
        } else {
            System.out.println(sideClients);
            drawBoardToCorrespondSideCanvas(message);
        }
    }

    private String prepareBroadcast(Board board, Tetromino tetromino){
        StringBuilder stringBuilder = board.toStringBuilder();
        for ( Point point: tetromino.getPoints()){
            if (point.getX() > Board.BOARD_WIDTH || point.getY() > Board.BOARD_HEIGHT){
                continue;
            }
            stringBuilder.replace((point.getX()-1)*Board.BOARD_HEIGHT+point.getY()-1,
                    (point.getX()-1)*Board.BOARD_HEIGHT+point.getY(),
                    String.valueOf(tetromino.getBlock().toChar()));
        }
        stringBuilder.insert(0, "(" + tetris.getClearedLines() + ")");
        if (isServer){
            return "[Server:" + ((Server)broadcastable).getPort() + "]: " + stringBuilder.toString();
        } else {
            return "[" + ((Client)broadcastable).getClientAddress() + "]: " + stringBuilder.toString();
        }
    }

    private void broadcastMessage(String msg) {
        try {
            broadcastable.broadcast(msg);
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    public void startServer() throws IOException{
        Server server = new Server((message)->{
            serverReceiveMessage(message);
            return message;
        });
        isServer = true;
        messageLabel.setText("Waiting for other clients!");
        startBtn.setVisible(true);
        broadcastable = server;
        Thread thread = new Thread(server);
        thread.setDaemon(true);
        thread.start();
    }

    public void startClient() {
        Client client = new Client(this::clientReceiveMessage);
        isServer = false;
        messageLabel.setText("Waiting for server!");
        messageLabel.setVisible(true);
        broadcastable = client;
        Thread thread = new Thread(client);
        thread.setDaemon(true);
        thread.start();
    }
}
