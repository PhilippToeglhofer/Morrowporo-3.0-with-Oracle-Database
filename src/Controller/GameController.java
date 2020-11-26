package Controller;

import Model.*;
import View.GameMenuView;
import View.GameWindow;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.input.KeyCode;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.*;
import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;


/**
 * Created by Kata on 03.04.2018.
 */
public class GameController {

    private final GameWindow view;
    private final ArrayList<Node> platforms = new ArrayList<>();
    private final ArrayList<Node> lavaBlocks = new ArrayList<>();
    private final ArrayList<Node> waterBlocks = new ArrayList<>();
    private final ArrayList<CombatText> combatList = new ArrayList<>();
    private final ArrayList<EnemyNode> enemyList = new ArrayList<>();
    private final ArrayList<ArrowNode> arrowList = new ArrayList<>();
    private List<String> musicList = new ArrayList<>();
    private Node goalBlock;
    private Text text = new Text("");
    private Text hearthText = new Text("");
    private Text healthBarText = new Text("");
    private Text manaBarText = new Text("");
    private Text staminaBarText = new Text("");
    private Text levelText = new Text("");
    private CombatText combatText;
    private MediaPlayer musicPlayer;

    private final GameMenuView gameMenuView;
    private final AttachImages attachImages = new AttachImages();
    private Player player;
    private final Hearth hearth = new Hearth();

    private Node playerNode;
    private Node lifeNode;
    private final DecimalFormat df = new DecimalFormat("#"); //"#.##"

    private Point2D playerVelocity = new Point2D(0, 0);
    private boolean isJumping = true;
    private boolean playerCanGetDamage = true;
    private boolean canRunTimer = true;
    private boolean loadGoalMenu = true;
    private boolean isGoal = false;
    private boolean isGameOver = false;
    private boolean isShootRight = true;
    private boolean isShot = false;

    private double totalHighscore = 0;
    private double highscore = 0;
    private int levelWidth;
    private int levelHeight;
    private int currentLevel;
    private final int windowSizeX = 1920;
    private final int windowSizeY = 1080;
    private final int halfWindowSizeX = 1920/2;
    private int movableViewPointX = windowSizeX/2;
    private final int viewPointY = 40;
    private int playerSpawnX = 0;
    private int playerSpawnY = 0;
    private final int blocksize = 96;
    private final int playersize = blocksize/3*2;
    private Rectangle timerBorder;
    private Rectangle hearthBorder;
    private Rectangle experienceBorder;
    private Rectangle experienceBar;
    private Rectangle levelBorder;
    private Rectangle healthBorder;
    private Rectangle healthBar;
    private Rectangle manaBorder;
    private Rectangle manaBar;
    private Rectangle staminaBorder;
    private Rectangle staminaBar;
    private double timepassed = 300;
    private double movableXtimebased = 0;

    public GameController(GameWindow view) {
        this.view = view;
        gameMenuView = new GameMenuView();
    }

    //All Methods will be updated in every frame (fps)
    public void update() {
        checkPlayerVelocity();
        checkArrowVelocity();
        checkEnemyVelocity();
        ifPlayerIsInLava();
        ifPlayerIsInWater();
        ifPlayerReachesGoal();
        ifPlayerFeltOutOfWorld();
        ifPlayerGetALife();
        updateTimepassedText();
        updatePlayerProfile();
        updateCloudEntity();
        updateCombatList();
        if (hearth.getHearths() <= 0 || isGameOver){ gameOver(); }
    }

    public void createNewPlayer(String inputName) {
        player = new Player(inputName);
        currentLevel = 1;
    }

    //<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<
    public boolean checkIfPlayerExists(String inputNickname) {
        try {
            Connection con = DriverManager.getConnection("jdbc:oracle:thin:@localhost:1521:orcl", "SYSTEM", "root"); // Oracle
            String sqlSelectPlayerNicknames = "SELECT player_data.Nickname FROM player_data";
            try (PreparedStatement ps = con.prepareStatement(sqlSelectPlayerNicknames)) {
                ResultSet rs = ps.executeQuery();
                while (rs.next()) {
                    if (inputNickname.matches(rs.getString(1))) {
                        System.out.println("Player already exists: " + inputNickname);
                        return true;
                    }
                }
                con.close();
                System.out.println("Player doesn´t exist: " + inputNickname);
                return false;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return true;
    }

    public void insertNewPlayerIntoDatabase(String inputNickname) {
        java.awt.Label dateTime = new java.awt.Label();
        dateTime.setText(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
        Connection con;
        int nextPlayerID = -1;
        int row;

        try {
            System.out.println("start database connection");
            con = DriverManager.getConnection("jdbc:oracle:thin:@localhost:1521:orcl", "SYSTEM", "root"); // Oracle

            String sqlSelectNextPlayerID = "SELECT ISEQ$$_76036.nextval FROM DUAL";
            String sqlInsertPlayerData = "INSERT INTO player_data (Player_id, Nickname, First_Name, Last_Name, Place) VALUES(?,?,?,?,?)";
            String sqlInsertPlayerStatus = "INSERT INTO player_status (Player_id, Level_id, Experience, HP, MP, SP, STR, GES, INT, AUS, KON, Gamelevel, isJumping, Skillpoints, Resourcepoints, Highscore, Lastlogin) VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
            //String sqlUpdate = "UPDATE (SELECT player_status.Highscore as OLD FROM player_status INNER JOIN player_data ON player_data.Player_id = player_status.Player_id WHERE player_data.Nickname LIKE 'Morrow%') t SET t.OLD = 33333";
            //String sqlSelect = "SELECT player_data.*, player_status.* FROM player_data INNER JOIN player_status ON player_data.Player_id = player_status.Player_id WHERE player_data.Nickname LIKE 'Morrow%'";
            //String sqlDelete = "DELETE FROM player_data WHERE Nickname LIKE 'Morrow%'";

            try (PreparedStatement ps = con.prepareStatement(sqlSelectNextPlayerID)) {
                ResultSet rs = ps.executeQuery();
                if(rs.next()) {
                    nextPlayerID = rs.getInt(1);
                    System.out.println("Next PlayerID: " + nextPlayerID);
                }
                rs.close();
            }

            try (PreparedStatement ps = con.prepareStatement(sqlInsertPlayerData)) {
                ps.setInt(1, nextPlayerID);
                ps.setString(2, inputNickname);
                ps.setString(3, "Morrow");
                ps.setString(4, "Poro");
                ps.setString(5, "Morrowlad");

                row = ps.executeUpdate();
                System.out.println("INSERT Statement Player Data " + inputNickname);
                System.out.println("Inserted rows: " + row);
            }

            try (PreparedStatement ps = con.prepareStatement(sqlInsertPlayerStatus)) {
                ps.setInt(1, nextPlayerID);
                ps.setInt(2, 1);
                ps.setInt(3, 0);
                ps.setInt(4, 24);
                ps.setInt(5, 24);
                ps.setInt(6, 24);
                ps.setInt(7, 1);
                ps.setInt(8, 1);
                ps.setInt(9, 1);
                ps.setInt(10, 1);
                ps.setInt(11, 1);
                ps.setInt(12, 1);
                ps.setInt(13, 0);
                ps.setInt(14, 0);
                ps.setInt(15, 0);
                ps.setInt(16, 0);
                ps.setDate(17, java.sql.Date.valueOf(dateTime.getText()));

                row = ps.executeUpdate();
                System.out.println("INSERT Statement Player Status " + inputNickname);
                System.out.println("Inserted rows: " + row);
            }
            /*Statement stmt = con.createStatement();
            row = stmt.executeUpdate(sqlUpdate);
            System.out.println("Updated rows: " + row);
            System.out.println("UPDATE Statement Player Status " + (1));

            ResultSet rs = stmt.executeQuery(sqlSelect);
            while (rs.next()) {
                for (int x = 1; x < rs.getMetaData().getColumnCount() + 1; x++){
                    rs.getString(x);
                    System.out.print(rs.getString(x) + " ");
                }
                System.out.println();
            }

            //row = stmt.executeUpdate(sqlDelete);
            //System.out.println("Deleted rows: " + row);
            //System.out.println("DELETE Statement Player Status " + (1));

            rs.close();
            stmt.close();*/
            con.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        System.out.println("closed database connection");
    }

    public void loadPlayerFromDatabase(String inputName) {
        String selectPlayer = "SELECT b.level_id, b.Experience, b.HP, b.MP, b.SP, b.STR, b.GES, b.INT, b.AUS, b.KON," +
                " b.Skillpoints, b.Resourcepoints, b.Highscore, b.Gamelevel, b.isJumping " +
                "FROM player_status b INNER JOIN player_data a ON a.Player_id = b.Player_id WHERE a.Nickname = ?";

        try {
            Connection con = DriverManager.getConnection("jdbc:oracle:thin:@localhost:1521:orcl", "SYSTEM", "root"); // Oracle
            try (PreparedStatement ps = con.prepareStatement(selectPlayer)) {
                ps.setString(1, inputName);
                ResultSet rs = ps.executeQuery();
                if(rs.next()) {
                    int level = rs.getInt(1);
                    int experience = rs.getInt(2);
                    int HP = rs.getInt(3);
                    int MP = rs.getInt(4);
                    int SP = rs.getInt(5);
                    int STR = rs.getInt(6);
                    int GES = rs.getInt(7);
                    int INT = rs.getInt(8);
                    int AUS = rs.getInt(9);
                    int KON = rs.getInt(10);
                    int skillpoints = rs.getInt(11);
                    int resourcepoints = rs.getInt(12);
                    totalHighscore = rs.getInt(13);
                    currentLevel = rs.getInt(14);
                    isJumping = rs.getBoolean(15);
                    player = new Player(inputName, level, experience, STR, GES, INT, AUS, KON, HP, MP, SP, skillpoints, resourcepoints);
                    System.out.println("Player loaded: " + inputName + " " +level + " " + experience +
                            " " + STR + " " + GES + " " + INT + " " + AUS + " " + KON +
                            " " + HP + " " + MP + " " + SP + " " + skillpoints + " " + resourcepoints);
                }else {
                    System.out.println("Player " + inputName + " not found in Database");
                }
            }
            con.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void updatePlayerInDatabase() {
        java.awt.Label dateTime = new java.awt.Label();
        dateTime.setText(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));

        try {
            Connection con = DriverManager.getConnection("jdbc:oracle:thin:@localhost:1521:orcl", "SYSTEM", "root"); // Oracle
            String sqlUpdate = "UPDATE (SELECT  b.level_id as b_level_id, b.Experience as b_Experience, b.HP as b_HP, b.MP as b_MP, b.SP as b_SP, b.STR as b_STR, b.GES as b_GES, b.INT as b_INT, b.AUS as b_AUS, b.KON as b_KON, " +
                    "b.Gamelevel as b_Gamelevel, b.isJumping as b_isJumping, b.Skillpoints as b_Skillpoints, b.Resourcepoints as b_Resourcepoints, b.Highscore as b_Highscore, b.Lastlogin as b_Lastlogin " +
                    "FROM player_status b INNER JOIN player_data a ON a.Player_id = b.Player_id WHERE a.Nickname LIKE ?) " +
                    "SET b_level_id = ?, b_Experience = ?, b_HP = ?, b_MP = ?, b_SP = ?, b_STR = ?, b_GES = ?, b_INT = ?, b_AUS = ?, b_KON = ?, " +
                    "b_Gamelevel = ?, b_isJumping = ?, b_Skillpoints = ?, b_Resourcepoints = ?, b_Highscore = ?, b_Lastlogin = ?";

            try (PreparedStatement ps = con.prepareStatement(sqlUpdate)) {
                ps.setString(1, player.getNickname());
                ps.setInt(2, player.getLevel());
                ps.setInt(3, player.getCurrentExperience());
                ps.setInt(4, player.getMaxHP());
                ps.setInt(5, player.getMaxMP());
                ps.setInt(6, player.getMaxSP());
                ps.setInt(7, player.getSTR());
                ps.setInt(8, player.getGES());
                ps.setInt(9, player.getINT());
                ps.setInt(10, player.getAUS());
                ps.setInt(11, player.getKON());
                ps.setInt(12, currentLevel + 1);
                ps.setInt(13, isJumping ? 1 : 0); //Boolean to int
                ps.setInt(14, player.getSkillpoints());
                ps.setInt(15, player.getResourcepoints());
                ps.setInt(16, (int) getTotalHighscore() + 1);
                ps.setDate(17, java.sql.Date.valueOf(dateTime.getText()));

                int row = ps.executeUpdate();
                System.out.println("Updated rows: " + row);
                System.out.println("UPDATE Statement Player Status " + player.getNickname());
            }
            con.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void deletePlayer(String inputNickname) {
        try {
            Connection con = DriverManager.getConnection("jdbc:oracle:thin:@localhost:1521:orcl", "SYSTEM", "root"); // Oracle
            String sqlDeletePlayer = "DELETE FROM player_data WHERE Nickname = ?";
            try (PreparedStatement ps = con.prepareStatement(sqlDeletePlayer)) {
                ps.setString(1, inputNickname);
                int row = ps.executeUpdate();
                System.out.println("Deleted rows: " + row);
                if (row == 0) {
                    System.out.println("Player doesn´t exist: " + inputNickname);
                }else {
                    System.out.println("Deleted Player " + inputNickname);
                }
                con.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    //<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<

    private boolean moveNodeX(Node currentNode, int x) {
        boolean moveRightElseLeft = x > 0;

        for (int i = 0; i < Math.abs(x); i++){
            for (Node currentPlatform : platforms){
                if (currentNode.getBoundsInParent().intersects(currentPlatform.getBoundsInParent().getMinX(),
                        currentPlatform.getBoundsInParent().getMinY() +1, blocksize, blocksize -2))
                {
                    if (moveRightElseLeft){ //Collision with left side of grassblock
                        if ((currentNode.getTranslateX() + playersize) == currentPlatform.getTranslateX())
                        {
                            return true;
                        }
                    }
                    else { //Collision with right side of grassblock
                        if ((currentNode.getTranslateX()) == currentPlatform.getTranslateX() + blocksize){
                            return true;
                        }
                    }
                }
            }
            currentNode.setTranslateX(currentNode.getTranslateX() + (moveRightElseLeft ? 1 : -1));

            if (moveRightElseLeft){
                currentNode.setScaleX(1);
            }else {
                currentNode.setScaleX(-1);
            }
        }
        return false;
    }

    private JumpVelocityResults moveNodeY(Node currentNode, Point2D currentNodeVelocity) {
        int y = (int) currentNodeVelocity.getY();
        boolean moveDownElseUp = y > 0;

        for (int i = 0; i < Math.abs(y); i++){
            for (Node currentPlatform : platforms){
                if (currentNode.getBoundsInParent().intersects(currentPlatform.getBoundsInParent().getMinX() +1,
                        currentPlatform.getBoundsInParent().getMinY(), blocksize -2, blocksize))
                {
                    if (moveDownElseUp){ //Collision with top side of grassblock
                        if (currentNode.getTranslateY() + playersize -1 == currentPlatform.getTranslateY())
                        {
                            currentNode.setTranslateY(currentNode.getTranslateY() -1);
                            return new JumpVelocityResults(true, currentNodeVelocity);
                        }
                    }
                    else { //Collision with bottom side of grassblock
                        if (currentNode.getTranslateY() == currentPlatform.getTranslateY() + blocksize){
                            while (currentNodeVelocity.getY() < 0){
                                currentNodeVelocity = currentNodeVelocity.add(0, 1);
                            }
                            return new JumpVelocityResults(false, currentNodeVelocity);
                        }
                    }
                }
            }
            currentNode.setTranslateY(currentNode.getTranslateY() + (moveDownElseUp ? 1 : -1));
        }
        return new JumpVelocityResults(false, currentNodeVelocity);
    }

    private void ifPlayerIsInLava() {

        for (Node currentLavaBlock : lavaBlocks){
            if (playerNode.getBoundsInParent().intersects(currentLavaBlock.getBoundsInParent())){
                if (playerNode.getTranslateY() + playersize -32 >= currentLavaBlock.getTranslateY()){

                    if (playerCanGetDamage) {
                        looseAHearth();
                        playerCanGetDamage = false;
                    }

                    lavaVelocity();
                    System.out.println("LAVAAA!");
                    isJumping = true;
                }
            }
        }
    }

    private void updateHearthText(){ hearthText.setText(""+hearth.getHearths()); }

    private void ifPlayerIsInWater() {

        for (Node currentWaterBlock : waterBlocks){
            if (playerNode.getBoundsInParent().intersects(currentWaterBlock.getBoundsInParent())){
                if (playerNode.getTranslateY() + playersize -32 >= currentWaterBlock.getTranslateY()){

                    waterVelocity();
                    isJumping = true;
                    return;
                }
            }
        }
    }

    private void updateCombatList(){
        try {
            for (CombatText currentCombatText: combatList){
                currentCombatText.getCombatText().setX(currentCombatText.getNode().getTranslateX() + currentCombatText.getNode().getBoundsInLocal().getWidth()/2);
                currentCombatText.getCombatText().setY(currentCombatText.getNode().getTranslateY() - 60 - currentCombatText.getHover());
                currentCombatText.getCombatText().setOpacity(currentCombatText.getCombatText().getOpacity() - 0.01);

                if (currentCombatText.getCombatText().getOpacity() <= 0){
                    view.deleteNodeInGameRoot(combatText.getCombatText());
                    combatList.remove(currentCombatText);
                }
            }
        }catch (Exception e){
            System.out.println("Removed CombatText from combatList");
        }
    }

    private void createCombatText(Node onThisNode,String combatString, Color color){
        combatText = new CombatText(onThisNode, combatString, color);
        combatList.add(combatText);
        view.addElementsToGameRoot(combatText.getCombatText());
    }

    private void checkPlayerVelocity(){

        if (isPressed(KeyCode.W) && playerNode.getTranslateY() >= 5){
            jumpPlayer();
        }
        if (isPressed(KeyCode.A) && playerNode.getTranslateX() >= 5){
            moveNodeX(playerNode, -5);
            isShootRight = false;
        }
        if (isPressed(KeyCode.D) && playerNode.getTranslateX() + playersize <= levelWidth -5 ){
            moveNodeX(playerNode, 5);
            isShootRight = true;
        }

        if (isPressed(KeyCode.SPACE)){
            if (!isShot) {
                attachImages.getArrow();
                addImageViewToToRecreateRoot(playerNode.getTranslateX() + playersize/2, playerNode.getTranslateY());
                Node arrow = attachImages.imageView;
                ArrowNode arrowNode = new ArrowNode(arrow);
                if (isShootRight) {
                    arrowNode.setArrowVelocity(10, 0);
                } else {
                    arrowNode.setArrowVelocity(-10, 0);
                }
                arrowList.add(arrowNode);
                isShot = true;
                player.looseStamina(3);
                setStaminaBarWidth();
            }
        }

        if (isPressed(KeyCode.DIGIT1)){
            System.out.println("MagicalBolt");

            if (!isShot && player.getCurrentMP() > 10) {
                attachImages.getMagicalBolt();
                addImageViewToToRecreateRoot(playerNode.getTranslateX() + playersize/2, playerNode.getTranslateY());
                Node arrow = attachImages.imageView;
                ArrowNode arrowNode = new ArrowNode(arrow);
                if (isShootRight) {
                    arrowNode.setArrowVelocity(10, 0);
                } else {
                    arrowNode.setArrowVelocity(-10, 0);
                }
                arrowNode.setArrow(false);
                arrowList.add(arrowNode);
                player.looseMana(11);
                setManaBarWidth();
                isShot = true;
            }
        }

        while (playerVelocity.getY() > 31){
            playerVelocity = playerVelocity.add(0, -1);
        }
        while (playerVelocity.getY() < -20){
            playerVelocity = playerVelocity.add(0, 1);
        }
        if (playerVelocity.getY() < 10){
            playerVelocity = playerVelocity.add(0, 1);
        }

        JumpVelocityResults jumpVelocityResults = moveNodeY(playerNode, playerVelocity);
        playerVelocity = jumpVelocityResults.getVelocity();
        if (jumpVelocityResults.isJump()){ isJumping = true; }
    }

    private void checkEnemyVelocity() {
        for (EnemyNode currentEnemy: enemyList) {

            if (currentEnemy.getNode().getBoundsInParent().intersects(playerNode.getBoundsInParent())){
                if (playerCanGetDamage) {
                    player.setCurrentHP(player.getDamage(currentEnemy.getPhysicalDamage(), currentEnemy.getMagicalDamage(), 0, player.getAUS(), player.getKON(), player.getCurrentHP()));
                    createCombatText(playerNode,"" + (currentEnemy.getPhysicalDamage() + currentEnemy.getMagicalDamage()), Color.ORANGERED);
                    if (player.getCurrentHP() <= 0){
                        looseAHearth();
                    }
                    setHealthBarWidth();
                    playerCanGetDamage = false;
                }
            }else {

                while (currentEnemy.getEnemyVelocity().getY() > 31) {
                    currentEnemy.addEnemyVelocity(0, -1);
                }
                while (currentEnemy.getEnemyVelocity().getY() < -20) {
                    currentEnemy.addEnemyVelocity(0, 1);
                }
                if (currentEnemy.getEnemyVelocity().getY() < 10) {
                    currentEnemy.addEnemyVelocity(0, 1);
                }

                moveNodeY(currentEnemy.getNode(), currentEnemy.getEnemyVelocity());

                if (playerNode.getTranslateX() < currentEnemy.getNode().getTranslateX()) { //Enemy is to the right of the player
                    if (currentEnemy.getNode().getTranslateX() - playerNode.getTranslateX() < blocksize * 9) {
                        moveNodeX(currentEnemy.getNode(), -1);
                    }
                }
                else { //Enemey is to the left of the player
                    if (playerNode.getTranslateX() - currentEnemy.getNode().getTranslateX() < blocksize * 9) {
                        moveNodeX(currentEnemy.getNode(), 1);
                    }
                }

                if (currentEnemy.isShowHealthBar()) {
                    currentEnemy.setHealthBar(currentEnemy.getNode().getTranslateX(), currentEnemy.getNode().getTranslateY());
                }
            }
        }
    }

    private void checkArrowVelocity() {
        try {
            for (ArrowNode currentArrow: arrowList) {
                if (moveNodeX(currentArrow.getNode(), (int) currentArrow.getArrowVelocity().getX())){
                    arrowList.remove(currentArrow);
                }
                if (currentArrow.getNode().getTranslateX() < -100 || currentArrow.getNode().getTranslateX() > levelWidth){
                    view.deleteNodeInRecreateRoot(currentArrow.getNode());
                    arrowList.remove(currentArrow);
                }

                currentArrow.getNode().setTranslateY(currentArrow.getNode().getTranslateY() + currentArrow.getArrowVelocity().getY());

                for (EnemyNode currentEnemy: enemyList) {
                    if (currentArrow.getNode().getBoundsInParent().intersects(currentEnemy.getNode().getBoundsInParent())){
                        System.out.println("ouch");

                        if (!currentEnemy.isShowHealthBar()) {
                            currentEnemy.createHealthBar(currentEnemy.getNode().getTranslateX(), currentEnemy.getNode().getTranslateY());
                            view.addNodeToRecreateRoot(currentEnemy.getHealthBar());
                            currentEnemy.setShowHealthBar(true);
                        }

                        if (currentArrow.isArrow()){
                            currentEnemy.setHealth(currentEnemy.getDamage(currentArrow.getDamage() + player.getSTR(), 0, player.getGES(), currentEnemy.getAUS(), currentEnemy.getKON(), currentEnemy.getHealth()));
                            createCombatText(currentEnemy.getNode(),"" + (currentArrow.getDamage() + player.getSTR()), Color.GOLD);
                        }else {
                            currentEnemy.setHealth(currentEnemy.getDamage(currentArrow.getDamage() + player.getINT(), 0, player.getGES(), currentEnemy.getAUS(), currentEnemy.getKON(), currentEnemy.getHealth()));
                            createCombatText(currentEnemy.getNode(),"" + (currentArrow.getDamage() + player.getINT()), Color.GOLD);
                        }

                        if (currentEnemy.getHealth() <= 0) {
                            if(player.getExperience(currentEnemy.getExperience())){
                                setHealthBarWidth();
                                setManaBarWidth();
                                setStaminaBarWidth();
                                createCombatText(playerNode, "<!LEVEL UP!>", Color.BLUEVIOLET);
                                view.createPlayerStatusMenu();
                            }
                            experienceBar.setWidth(96*player.getPercentageLevelUp());
                            view.deleteNodeInRecreateRoot(currentEnemy.getHealthBar());
                            view.deleteNodeInRecreateRoot(currentEnemy.getNode());
                            enemyList.remove(currentEnemy);
                        }

                        view.deleteNodeInRecreateRoot(currentArrow.getNode());
                        arrowList.remove(currentArrow);
                    }
                }
            }
        } catch (Exception e){
            System.out.println("Removed Node from ArrayList");
        }
    }

    private void lavaVelocity(){
        while (playerVelocity.getY() > 0.1 )
            playerVelocity = playerVelocity.add(0, -0.1);
    }

    private void waterVelocity(){
        playerVelocity = playerVelocity.add(0, -2);
    }

    private void ifPlayerReachesGoal(){
        if (loadGoalMenu){
            if (playerNode.getBoundsInParent().intersects(goalBlock.getBoundsInParent())){
                if (playerNode.getTranslateY() + playersize -288 >= goalBlock.getTranslateY() &&
                        playerNode.getTranslateX() + playersize -120 >= goalBlock.getTranslateX() &&
                        playerNode.getTranslateX() - 150 <= goalBlock.getTranslateX()) {
                    System.out.println("GOAAAL!!!");

                    player.savePlayerStatus();
                    calcHighscore();
                    totalHighscore += highscore;
                    updatePlayerInDatabase();

                    if (currentLevel < 4) {
                        view.showPauseMenu();
                        view.addElementToUiRoot(gameMenuView.createLevelCompletedMenu(view));
                    }else {
                        view.addElementToUiRoot(gameMenuView.createGoalMenu(view));

                        try {
                            //Get File Path
                            Path file = Paths.get("Highscores.txt");

                            //If File doesn´t exist => create it
                            if (!Files.exists(file)) {
                                Files.createFile(file);
                            }

                            //If File exists => save the Highscore
                            if (Files.exists(file)) {

                                //Read the File
                                List<String> lines = Files.readAllLines(file);

                                //Add the Highscore
                                lines.add(df.format(totalHighscore));

                                //Sort the Highscores
                                if (lines.size() > 1) {
                                    lines.sort(Comparator.comparing(Integer::valueOf));
                                }
                                Collections.reverse(lines);

                                //Highscore-List with maximum 10 Highscores
                                while (lines.size() > 10) {
                                    lines.remove(lines.size() - 1);
                                }

                                //Rewrite the File
                                Files.write(file, lines);
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    denyLoadingMenu();
                }
            }
        }
    }

    private void ifPlayerGetALife() {
        if (attachImages.imageViewLife.isVisible() && playerNode.getBoundsInParent().intersects(lifeNode.getBoundsInParent())){
            hearth.get_aHearth();
            hearthText.setText(""+hearth.getHearths());
            attachImages.imageViewLife.setVisible(false);
            createCombatText(playerNode,"♥", Color.RED);
        }
    }

    private void looseAHearth(){
        hearth.loose_aHearth();
        updateHearthText();
        createCombatText(playerNode, "♡", Color.BLACK);
        player.recoverResources();
        setHealthBarWidth();
        setManaBarWidth();
        setStaminaBarWidth();
        if (hearth.getHearths() <= 0 ){ isGameOver = true; }
    }

    private void ifPlayerFeltOutOfWorld(){
        if (playerNode.getTranslateY() > levelHeight + blocksize){
            setPlayerToSpawn();
            looseAHearth();
            view.setGameRoot(0);
            movableViewPointX = windowSizeX/2;
        }
    }

    private void jumpPlayer() {
        if (isJumping && player.getCurrentSP()>=6){
            playerVelocity = playerVelocity.add(0, -30);
            player.looseStamina(6);
            setStaminaBarWidth();
            isJumping = false;
        }
    }

    private boolean isPressed(KeyCode key) {
        return view.getKeys().getOrDefault(key, false);
    }

    public void initContent() {
        Rectangle background = new Rectangle(windowSizeX, windowSizeY, Color.AQUAMARINE);
        generateLevel(selectLevel(currentLevel));
        attachImages.getPlayer();
        view.addElementsToGameRoot(attachImages.imageView);
        setPlayerToSpawn();
        playerNode = attachImages.imageView;

        playerNode.translateXProperty().addListener((obs, old, newValue) -> {
            int offset = newValue.intValue();

            if (offset > windowSizeX/2 && offset < levelWidth - windowSizeX/2) {
                view.setGameRoot(-(offset - windowSizeX/2));
                movableViewPointX = offset;
            }
        });

        setMusic();
        //playMusic();

        view.addElementToUiRoot(gameMenuView.createEscapeMenu(view));
        view.hideUiRoot();
        view.hideStatsRoot();
        view.setAppRoot(background);
        startTimepassedCounter();
    }



    private void generateLevel(String[] currentLevelArray){
        levelWidth = currentLevelArray[0].length()* blocksize;
        levelHeight = currentLevelArray.length* blocksize;

        for (int y = 0; y < currentLevelArray.length; y++){
            String currentTileMapRow = currentLevelArray[y];
            for (int x = 0; x < currentTileMapRow.length(); x++){
                switch (currentTileMapRow.charAt(x)){
                    case '0':
                        break;
                    case '1':
                        attachImages.getGrass();
                        addImageViewToGameRoot(x*blocksize, y*blocksize);
                        platforms.add(attachImages.imageView);
                        break;
                    case '2':
                        attachImages.getLava();
                        addImageViewToGameRoot(x*blocksize, y*blocksize);
                        lavaBlocks.add(attachImages.imageView);
                        break;
                    case '3':
                        attachImages.getWater();
                        addImageViewToGameRoot(x*blocksize, y*blocksize);
                        waterBlocks.add(attachImages.imageView);
                        break;
                    case '4':
                        attachImages.getGoal();
                        addImageViewToGameRoot(x*blocksize, y*blocksize);
                        goalBlock = attachImages.imageView;
                        break;
                    case '5':
                        playerSpawnX = x*blocksize;
                        playerSpawnY = y*blocksize;
                        break;
                    case '6':
                        attachImages.getLife();
                        addImageViewToGameRoot(x*blocksize + blocksize/4, y*blocksize + blocksize/2);
                        lifeNode = attachImages.imageView;
                        break;
                    case '7':
                        attachImages.getDirt();
                        addImageViewToGameRoot(x*blocksize, y*blocksize);
                        platforms.add(attachImages.imageView);
                        break;
                    case '8':
                        attachImages.getEnemy();
                        addImageViewToToRecreateRoot(x*blocksize, y*blocksize);
                        Node enemy = attachImages.imageView;
                        EnemyNode enemyNode = new EnemyNode(enemy, 10*currentLevel, 0*currentLevel, 0*currentLevel, 10*currentLevel, 8*currentLevel);
                        enemyList.add(enemyNode);
                        break;
                    case '9':
                        attachImages.getEnemy();
                        addImageViewToToRecreateRoot(x*blocksize, y*blocksize);
                        Node rangedEnemy = attachImages.imageView;
                        EnemyNode rangedEnemyNode = new EnemyNode(rangedEnemy, 10*currentLevel, 0*currentLevel, 0*currentLevel, 10*currentLevel, 8*currentLevel);
                        enemyList.add(rangedEnemyNode);
                }
            }
        }

        createCloudEntity();
        createPlayerProfile();
        createTimepassedText();
    }

    private void reloadEnemies(){
        enemyList.clear();
        String[] currentLevelArray = selectLevel(currentLevel);
        for (int y = 0; y < currentLevelArray.length; y++){
            String currentTileMapRow = currentLevelArray[y];
            for (int x = 0; x < currentTileMapRow.length(); x++){
                switch (currentTileMapRow.charAt(x)){
                    case '8':
                        attachImages.getEnemy();
                        addImageViewToToRecreateRoot(x*blocksize, y*blocksize);
                        Node enemy = attachImages.imageView;
                        EnemyNode enemyNode = new EnemyNode(enemy, 10*currentLevel, 0*currentLevel, 0*currentLevel, 10*currentLevel, 8*currentLevel);
                        enemyList.add(enemyNode);
                }
            }
        }
    }

    private String[] selectLevel(int selectlevel){
        currentLevel = selectlevel;

        switch (selectlevel){
            case 1: return Level_01.level_01;
            case 2: return Level_01.level_02;
            case 3: return Level_01.level_03;
        }
        return Level_01.level_04;
    }

    public void clearLevel(){
        view.deleteAllElementsInGameRoot();
        platforms.clear();
        lavaBlocks.clear();
        waterBlocks.clear();
    }

    public void setStartElements(){
        generateLevel(selectLevel(currentLevel + 1));
        view.addElementsToGameRoot(attachImages.imageViewPlayer);
        view.addRecreateRootTOGameRoot();
    }

    public void setCurrentLevelZero(){ currentLevel = 0; }

    public void restartGame(){
        //playMusic();
        view.deleteAllElementsInRecreateRoot();
        arrowList.clear();
        reloadEnemies();
        view.addElementToUiRoot(gameMenuView.createEscapeMenu(view));
        player.loadPlayerStatus();
        setStaminaBarWidth();
        setManaBarWidth();
        setHealthBarWidth();
        experienceBar.setWidth(96*player.getPercentageLevelUp());
        setPlayerToSpawn();
        createPlayerStatusMenu();
        view.setGameRoot(0);
        movableViewPointX = windowSizeX/2;
        hearth.set3Hearths();
        updateHearthText();
        attachImages.imageViewLife.setVisible(true);
        loadGoalMenu = true;
        isGoal = false;
        isGameOver = false;
        timepassed = 300;
    }

    public Parent createStartMenu(){
        return gameMenuView.createStartMenu(view);
    }

    public Parent createHighscoreMenu(){
        Path file = Paths.get("Highscores.txt");
        List<String> lines = null;

        if (Files.exists(file)) {
            try {
                lines = Files.readAllLines(file);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return gameMenuView.createHighscoreMenu(view, lines);
    }

    public Parent createPlayerMenu() {
        return gameMenuView.createPlayerMenu(view);
    }

    public Parent createControlsMenu() {
        return gameMenuView.createControlsMenu(view);
    }

    public void createPlayerStatusMenu(){
        view.addElementToStatsRoot(gameMenuView.createPlayerStatusMenu(view, player));
    }

    private Node createEntity(int x, int y, int blockWidth, int blockHeigth, Color color) {
        Rectangle entity = new Rectangle(blockWidth, blockHeigth);
        entity.setTranslateX(x);
        entity.setTranslateY(y);
        entity.setFill(color);
        view.addElementToUiRoot(entity);
        return entity;
    }

    private void addImageViewToGameRoot(int x, int y){
        view.addElementsToGameRoot(attachImages.imageView);
        attachImages.imageView.setTranslateX(x);
        attachImages.imageView.setTranslateY(y);
    }

    private void addImageViewToToRecreateRoot(double x, double y){
        view.addNodeToRecreateRoot(attachImages.imageView);
        attachImages.imageView.setTranslateX(x);
        attachImages.imageView.setTranslateY(y);
    }

    private void setPlayerToSpawn(){
        attachImages.imageViewPlayer.setTranslateX(playerSpawnX);
        attachImages.imageViewPlayer.setTranslateY(playerSpawnY);
    }

    private void createPlayerProfile() {
        hearthBorder = new Rectangle(movableViewPointX - halfWindowSizeX + 5, 5, 130, 75);
        hearthBorder.setFill(Color.ROSYBROWN);

        levelBorder = new Rectangle(movableViewPointX - halfWindowSizeX + 5, 81, 30, 20);
        levelBorder.setStrokeWidth(0.5);
        levelBorder.setStroke(Color.BLACK);
        levelBorder.setFill(Color.OLIVE);

        experienceBorder = new Rectangle(movableViewPointX - halfWindowSizeX + 36, 81, 98, 20);
        experienceBorder.setStrokeWidth(0.5);
        experienceBorder.setStroke(Color.BLACK);
        experienceBorder.setFill(Color.DARKSLATEGRAY);

        experienceBar = new Rectangle(movableViewPointX - halfWindowSizeX + 37, 82, 96*player.getPercentageLevelUp(), 18);
        experienceBar.setFill(Color.CORNFLOWERBLUE);

        healthBorder = new Rectangle(movableViewPointX - halfWindowSizeX + 5, 102, 129, 20);
        healthBorder.setStrokeWidth(0.5);
        healthBorder.setStroke(Color.BLACK);
        healthBorder.setFill(Color.DARKSLATEGRAY);

        healthBar = new Rectangle(movableViewPointX - halfWindowSizeX + 6, 103, 127*player.getPercentageHP(), 18);
        healthBar.setFill(Color.DARKRED);

        healthBarText = new Text(movableViewPointX - halfWindowSizeX + 50, 116, "" + player.getCurrentHP() + " / " + player.getMaxHP());
        healthBarText.setFill(Color.GOLD);
        healthBarText.setTextAlignment(TextAlignment.CENTER);

        manaBorder = new Rectangle(movableViewPointX - halfWindowSizeX + 5, 123, 129, 20);
        manaBorder.setStrokeWidth(0.5);
        manaBorder.setStroke(Color.BLACK);
        manaBorder.setFill(Color.DARKSLATEGRAY);

        manaBar = new Rectangle(movableViewPointX - halfWindowSizeX + 6, 124, 127*player.getPercentageMP(), 18);
        manaBar.setFill(Color.DARKBLUE);

        manaBarText = new Text(movableViewPointX - halfWindowSizeX + 50, 137, "" + player.getCurrentMP() + " / " + player.getMaxMP());
        manaBarText.setFill(Color.GOLD);
        manaBarText.setTextAlignment(TextAlignment.CENTER);

        staminaBorder = new Rectangle(movableViewPointX - halfWindowSizeX + 5, 144, 129, 20);
        staminaBorder.setStrokeWidth(0.5);
        staminaBorder.setStroke(Color.BLACK);
        staminaBorder.setFill(Color.DARKSLATEGRAY);

        staminaBar = new Rectangle(movableViewPointX - halfWindowSizeX + 6, 145, 127*player.getPercentageSP(), 18);
        staminaBar.setFill(Color.DARKGREEN);

        staminaBarText = new Text(movableViewPointX - halfWindowSizeX + 50, 158, "" + player.getCurrentSP() + " / " + player.getMaxSP());
        staminaBarText.setFill(Color.GOLD);
        staminaBarText.setTextAlignment(TextAlignment.CENTER);

        hearthText = new Text(movableViewPointX - halfWindowSizeX + 20, viewPointY + 5, "" + hearth.getHearths());
        hearthText.setFill(Color.DARKRED);
        hearthText.setScaleX(3);
        hearthText.setScaleY(3);

        levelText = new Text(movableViewPointX - halfWindowSizeX + 15, viewPointY + 56, "" + player.getLevel());

        attachImages.getHearth();
        attachImages.imageViewHearth.setTranslateX(movableViewPointX - halfWindowSizeX + 50);
        attachImages.imageViewHearth.setTranslateY(12);

        view.addElementsToGameRoot(hearthBorder, hearthText, attachImages.imageViewHearth, levelBorder, levelText, experienceBorder, experienceBar,
                healthBorder, healthBar, healthBarText, manaBorder, manaBar, manaBarText, staminaBorder, staminaBar, staminaBarText);
    }

    private void updatePlayerProfile() {
        hearthBorder.setX(movableViewPointX - halfWindowSizeX + 5);
        hearthText.setX(movableViewPointX - halfWindowSizeX + 20);
        levelBorder.setX(movableViewPointX - halfWindowSizeX + 5);
        experienceBorder.setX(movableViewPointX - halfWindowSizeX + 36);
        experienceBar.setX(movableViewPointX - halfWindowSizeX + 37);
        healthBorder.setX(movableViewPointX - halfWindowSizeX + 5);
        healthBar.setX(movableViewPointX - halfWindowSizeX + 6);
        healthBarText.setX(movableViewPointX - halfWindowSizeX + 50);
        manaBorder.setX(movableViewPointX - halfWindowSizeX + 5);
        manaBar.setX(movableViewPointX - halfWindowSizeX + 6);
        manaBarText.setX(movableViewPointX - halfWindowSizeX + 50);
        staminaBorder.setX(movableViewPointX - halfWindowSizeX + 5);
        staminaBar.setX(movableViewPointX - halfWindowSizeX + 6);
        staminaBarText.setX(movableViewPointX - halfWindowSizeX + 50);
        levelText.setX(movableViewPointX - halfWindowSizeX + 15);
        levelText.setText(""+player.getLevel());
        attachImages.imageViewHearth.setTranslateX(movableViewPointX - halfWindowSizeX + 50);
    }

    public void setHealthBarWidth(){
        healthBar.setWidth(127*player.getPercentageHP());
        healthBarText.setText("" + player.getCurrentHP() + " / " + player.getMaxHP());
    }

    public void setManaBarWidth(){
        manaBar.setWidth(127*player.getPercentageMP());
        manaBarText.setText("" + player.getCurrentMP() + " / " + player.getMaxMP());
    }

    public void setStaminaBarWidth(){
        staminaBar.setWidth(127*player.getPercentageSP());
        staminaBarText.setText("" + player.getCurrentSP() + " / " + player.getMaxSP());
    }

    private void createCloudEntity(){
        double movableCloudX = movableXtimebased;

        attachImages.getClouds();
        attachImages.imageViewCloud1.setTranslateX(movableCloudX);
        attachImages.imageViewCloud1.setTranslateY(-50);

        attachImages.imageViewCloud2.setTranslateX(movableCloudX + halfWindowSizeX - 110);
        attachImages.imageViewCloud2.setTranslateY(-50);

        attachImages.imageViewCloud3.setTranslateX(movableCloudX + levelWidth/2);
        attachImages.imageViewCloud3.setTranslateY(-50);

        attachImages.imageViewCloud4.setTranslateX(movableCloudX + levelWidth -1000);
        attachImages.imageViewCloud4.setTranslateY(-50);

        view.addElementsToGameRoot(attachImages.imageViewCloud1, attachImages.imageViewCloud2, attachImages.imageViewCloud3, attachImages.imageViewCloud4);
    }

    private void updateCloudEntity(){
        double movableCloudX = movableXtimebased + playerNode.getTranslateX()/10;
        attachImages.imageViewCloud1.setTranslateX(movableCloudX);
        attachImages.imageViewCloud2.setTranslateX(movableCloudX + halfWindowSizeX - 110);
        attachImages.imageViewCloud3.setTranslateX(movableCloudX + levelWidth/2);
        attachImages.imageViewCloud4.setTranslateX(movableCloudX + levelWidth -1000);
    }

    public void setCanRunTimerFalse(){
        canRunTimer = false;
    }

    public void setCanRunTimerTrue(){
        canRunTimer = true;
    }

    private void calcHighscore(){
        highscore = timepassed * 10 + hearth.getHearths() * 1000;
    }

    public double getHighscore(){
        return highscore;
    }

    public double getTotalHighscore(){
        return totalHighscore;
    }

    private void setMusic(){
        musicList.add("/Music/ChilloutPMLP.WAV");
        musicList.add("/Music/MovieScore ~Spm~.WAV");
        musicList.add("/Music/Rock Pop Vol. 9.WAV");
        musicList.add("/Music/SoftMetalPMLP.WAV");
    }

    private void playMusic(){

        try {
            musicPlayer.stop();
        }catch (Exception e){
            System.out.println("No music to stop");
        }

        int trackNumber;
        if (currentLevel<4) { trackNumber = currentLevel; } else { trackNumber = 4; }

        System.out.println("Play Music " + trackNumber);
        String path = musicList.get(trackNumber-1);
        System.out.println("Path: " + path);

        URL url = getClass().getResource(path);
        System.out.println("URL: " + url);
        Media track1 = new Media(url.toString());
        musicPlayer = new MediaPlayer(track1);
        musicPlayer.setVolume(0.4);
        musicPlayer.play();
        //musicPlayer.setAutoPlay(true);
        musicPlayer.setOnEndOfMedia(() -> {
            musicPlayer.stop();
            System.out.println("END OF MUSIC");
            musicPlayer.play();
            System.out.println("REPEAT MUSIC");
        });

    }

    public void stopMusic(){
        //musicPlayer.stop();
    }

    private void denyLoadingMenu(){
        view.setCantPause();
        setCanRunTimerFalse();
        loadGoalMenu = false;
    }

    private void startTimepassedCounter(){

        new Timer().schedule(
                new TimerTask() {

                    private int i = 0;
                    private int k = 0;
                    private int arrowShootingSpeed = 0;
                    private boolean isGamerOverTimer = false;


                    @Override
                    public void run() {
                        if (canRunTimer) {
                            timepassed -= 0.1;
                            movableXtimebased++;

                            if (timepassed <= 0 && !isGamerOverTimer) {
                                isGameOver = true;
                                isGamerOverTimer = true;
                            }

                            if (!playerCanGetDamage) {
                                i++;
                                if (i >= 40) {
                                    playerCanGetDamage = true;
                                    i = 0;
                                }
                            }

                            if (isShot){
                                arrowShootingSpeed++;
                                if (arrowShootingSpeed == 10){
                                    isShot = false;
                                    arrowShootingSpeed = 0;
                                }
                            }

                            if (player.getCurrentSP() < player.getMaxSP()){
                                if (k >= 10){
                                    player.generateStamina(3);
                                    setStaminaBarWidth();
                                    k=0;
                                }
                                k++;
                            }

                        }else if (!loadGoalMenu && !isGoal){
                            view.showUiRoot();
                            view.animationTimerStop();
                            isGoal = true;
                        }
                    }
                }, 0, 100); //Will be sceduled every 100 milliseconds
    }

    private void createTimepassedText() {
        timerBorder = new Rectangle(movableViewPointX - 45, 10, 113, 53);
        timerBorder.setFill(Color.BLACK);
        timerBorder.setOpacity(0.5);

        text = new Text(movableViewPointX, viewPointY, "" + df.format(timepassed));
        text.setFill(Color.BLUEVIOLET);
        text.setScaleX(3);
        text.setScaleY(3);
        view.addElementsToGameRoot(timerBorder, text);
    }

    private void updateTimepassedText() {
        timerBorder.setX(movableViewPointX - 45);
        text.setX(movableViewPointX);
        text.setText(df.format(timepassed));
    }

    private void gameOver(){
        if (loadGoalMenu) {
            System.out.println("Game Over");
            calcHighscore();
            view.addElementToUiRoot(gameMenuView.createGameOverMenu(view));
            denyLoadingMenu();
        }
    }
}
