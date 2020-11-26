package View;

import Controller.GameController;
import javafx.animation.AnimationTimer;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

import java.util.HashMap;

/**
 * Created by Kata on 03.04.2018.
 */
public class GameWindow {

    private final HashMap<KeyCode, Boolean> keys = new HashMap<>();

    private final Pane appRoot = new Pane();
    private final Pane statsRoot = new Pane();
    private final Pane recreateRoot = new Pane();
    private final Pane gameRoot = new Pane();
    private final Pane uiRoot = new Pane();
    private final Scene startScene;
    private Scene highscoreScene, controlsScene, createPlayerScene;
    private final Scene gameScene;
    private AnimationTimer animationTimer;
    private boolean isPressedESC = false;
    private boolean canPause = true;
    private final Stage primaryStage;
    private final long[] frameTimes = new long[100];
    private int frameTimeIndex = 0 ;
    private boolean arrayFilled = false ;

    private GameController gameController = new GameController(this);

    public GameWindow(Stage primaryStage){

        this.primaryStage = primaryStage;
        startScene = new Scene(gameController.createStartMenu());
        this.primaryStage.getIcons().add(new Image("img/StartScreenBackground.png"));
        this.primaryStage.setTitle("Morrowporo");
        showStartMenu();
        //primaryStage.initStyle(StageStyle.UNDECORATED); //BORDERLESS
        this.primaryStage.show();
        gameScene = new Scene(appRoot);
    }

    void startGame(String inputName, boolean playerExists){
        primaryStage.setMaximized(true);
        if (playerExists){
            System.out.println("Load Player Status from Database...");
            gameController.loadPlayerFromDatabase(inputName);
        }else {
            System.out.println("Create new Player...");
            gameController.insertNewPlayerIntoDatabase(inputName);
            gameController.createNewPlayer(inputName);
        }
        gameController.initContent();
        createPlayerStatusMenu();

        gameScene.setOnKeyPressed(event -> keys.put(event.getCode(), true));
        gameScene.setOnKeyReleased(event -> keys.put(event.getCode(), false));
        gameScene.setOnKeyTyped(event -> checkPauseMenu());

        primaryStage.setScene(gameScene);

        animationTimer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                gameController.update();
                primaryStage.setTitle(String.format("Morrowporo %.0f fps", getFPS(now)));
            }
        };
        animationTimer.start();
    }

    public void animationTimerStop(){
        animationTimer.stop();
    }

    private void animationTimerStart(){
        try {
            animationTimer.start();
        }catch (Exception e){
            System.out.println("AnimationTimer not set or already running");
        }
    }

    private double getFPS (long now) {
        double frameRate = 60;
        long oldFrameTime = frameTimes[frameTimeIndex] ;
        frameTimes[frameTimeIndex] = now ;
        frameTimeIndex = (frameTimeIndex + 1) % frameTimes.length ;
        if (frameTimeIndex == 0) {
            arrayFilled = true ;
        }
        if (arrayFilled) {
            long elapsedNanos = now - oldFrameTime ;
            long elapsedNanosPerFrame = elapsedNanos / frameTimes.length ;
            frameRate = 1_000_000_000.0 / elapsedNanosPerFrame ;
        }
        return frameRate;
    }

    public void setGameRoot(int x){
        gameRoot.setLayoutX(x);
    }

    public void setAppRoot(Rectangle background){

        appRoot.getChildren().clear();
        addRecreateRootTOGameRoot();

        try {
            appRoot.getChildren().addAll(background, gameRoot, statsRoot, uiRoot);
        }catch (Exception e){
            System.out.println("setAppRoot Exception: " + e);
        }
    }

    void switchToHomeMenu(){
        showStartMenu();
        gameController.stopMusic();
        gameController.setCurrentLevelZero();
        gameController.clearLevel();

        appRoot.getChildren().clear();
        statsRoot.getChildren().clear();
        recreateRoot.getChildren().clear();
        gameRoot.getChildren().clear();
        uiRoot.getChildren().clear();
        gameController = new GameController(this);
        quitPauseMenu2();
        setGameRoot(0);
    }

    public void addElementsToGameRoot(Node... nodes){
        gameRoot.getChildren().addAll(nodes);
    }

    public void deleteAllElementsInGameRoot(){ gameRoot.getChildren().clear(); }

    public void deleteNodeInGameRoot(Node node){
        gameRoot.getChildren().remove(node);
    }

    public void addNodeToRecreateRoot(Node node){
        recreateRoot.getChildren().add(node);
    }

    public void deleteNodeInRecreateRoot(Node node){ recreateRoot.getChildren().remove(node); }

    public void deleteAllElementsInRecreateRoot(){ recreateRoot.getChildren().clear(); }

    public void addRecreateRootTOGameRoot(){
        try {
            gameRoot.getChildren().add(recreateRoot);
        }catch (Exception e){
            System.out.println("gameRoot Exception: " + e);
        }
    }

    public HashMap<KeyCode, Boolean> getKeys(){
        return keys;
    }

    public void addElementToUiRoot(Node node){
        try {
            uiRoot.getChildren().clear();
        }catch (Exception e){
            System.out.println("Could not clear UiRoot");
        }

        try {
            uiRoot.getChildren().add(node);
        }catch (Exception e){
            System.out.println("Could not add node to UiRoot");
        }

    }

    public void addElementToStatsRoot(Node node){
        try {
            statsRoot.getChildren().clear();
        }catch (Exception e){
            System.out.println("Could not clear StatsRoot");
        }

        try {
            statsRoot.getChildren().add(node);
        }catch (Exception e){
            System.out.println("Could not add Element to StatsRoot");
        }

    }

    public void hideUiRoot(){
        uiRoot.setVisible(false);
    }

    public void showUiRoot(){
        uiRoot.setVisible(true);
    }

    public void hideStatsRoot(){
        statsRoot.setVisible(false);
    }

    private void showStatsRoot(){
        statsRoot.setVisible(true);
    }

    private boolean isPressed(KeyCode key) {
        return this.getKeys().getOrDefault(key, false);
    }

    public void setCantPause(){
        canPause = false;
    }

    private void checkPauseMenu() {

        if (canPause) {
            if (isPressed(KeyCode.ESCAPE) && !isPressedESC) { //enhance
                this.showUiRoot();
                showPauseMenu();

            } else if (isPressed(KeyCode.ESCAPE) && isPressedESC) {
                quitPauseMenu();
            }

            if (isPressed(KeyCode.C) && !isPressedESC) {
                this.showStatsRoot();
                showPauseMenu();

            } else if (isPressed(KeyCode.C) && isPressedESC) {
                quitPauseMenu();
            }
        }
    }

    public void createPlayerStatusMenu(){
        gameController.createPlayerStatusMenu();
    }

    public void showPauseMenu(){
        isPressedESC = true;
        System.out.println("Show Menu UI");
        gameController.setCanRunTimerFalse();
        this.animationTimerStop();
    }

    void createNewGame(){
        gameController.restartGame();
        quitPauseMenu();
        canPause = true;
    }

    void createNextLevel(){
        gameController.clearLevel();
        gameController.setStartElements();
        gameController.restartGame();
        quitPauseMenu();
        canPause = true;
    }

    void quitPauseMenu(){
        this.animationTimerStart();
        gameController.setCanRunTimerTrue();
        hideUiRoot();
        hideStatsRoot();
        isPressedESC = false;
        System.out.println("Hide Menu UI");
    }

    private void quitPauseMenu2(){
        gameController.setCanRunTimerTrue();
        hideUiRoot();
        hideStatsRoot();
        isPressedESC = false;
        System.out.println("Hide Menu UI");
    }

    double getHighscore(){ return gameController.getHighscore(); }

    double getTotalHighscore(){ return gameController.getTotalHighscore(); }

    void setGameControllerHealthBarWidth(){ gameController.setHealthBarWidth(); }

    void setGameControllerManaBarWidth(){ gameController.setManaBarWidth(); }

    void setGameControllerStaminaBarWidth(){ gameController.setStaminaBarWidth(); }

    void createHighscoreView() {
        highscoreScene = new Scene(gameController.createHighscoreMenu());
        primaryStage.setScene(highscoreScene);
    }

    void createPlayerView() {
        createPlayerScene = new Scene(gameController.createPlayerMenu());
        primaryStage.setScene(createPlayerScene);
    }

    void showStartMenu() {
        primaryStage.setScene(startScene);
    }

    void createControlsView() {
        controlsScene = new Scene(gameController.createControlsMenu());
        primaryStage.setScene(controlsScene);
    }

    public boolean checkIfPlayerExists(String inputNickname) {
        return gameController.checkIfPlayerExists(inputNickname);
    }

    public void deletePlayer(String inputNickname) {
        gameController.deletePlayer(inputNickname);
    }
}
