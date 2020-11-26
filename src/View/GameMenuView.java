package View;

import Model.MenuBackground;
import Model.Player;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Created by Kata on 15.04.2018.
 */
public class GameMenuView
{
    private final MenuBackground menuBackground = new MenuBackground();
    private final int windowSizeX = 800;
    private final int halfWindowSizeX = windowSizeX/2;
    private final int windowSizeY = 600;

    public Parent createStartMenu(GameWindow view) {
        Pane root = new Pane();
        root.setPrefSize(windowSizeX, windowSizeY);
        Rectangle background = new Rectangle(windowSizeX, windowSizeY, Color.DARKGRAY);
        menuBackground.attachDefaultImages();
        root.getChildren().addAll(background, menuBackground.menuBackgroundImageView);

        Title title = new Title("M O R R O W P O R O");
        title.setTranslateX(halfWindowSizeX - 265);
        title.setTranslateY(75);

        MenuItem play = new MenuItem("PLAY");
        play.setOnMouseClicked(event -> view.createPlayerView()); //view.startGame()

        MenuItem highscore = new MenuItem("HIGHSCORES");
        highscore.setOnMouseClicked(event -> view.createHighscoreView());

        MenuItem controls = new MenuItem("CONTROLS");
        controls.setOnMouseClicked(event -> view.createControlsView());

        MenuItem exit = new MenuItem("QUIT");
        exit.setOnMouseClicked(event -> Platform.exit());

        MenuBox menuBox = new MenuBox(
                play,
                highscore,
                controls,
                exit
        );
        menuBox.setTranslateX(halfWindowSizeX - 125);
        menuBox.setTranslateY(275);

        root.getChildren().addAll(title, menuBox);
        return root;
    }

    public Parent createPlayerMenu(GameWindow view) {
        Pane root = new Pane();
        root.setPrefSize(windowSizeX, windowSizeY);
        Rectangle background = new Rectangle(windowSizeX, windowSizeY, Color.DARKGRAY);
        menuBackground.attachDefaultImages();
        root.getChildren().addAll(background, menuBackground.menuBackgroundImageView);

        Title title = new Title("M O R R O W P O R O");
        title.setTranslateX(halfWindowSizeX - 265);
        title.setTranslateY(75);

        InputItem input = new InputItem("<Insert Name>");
        TextField inputText = (TextField) input.getChildren().get(1);

        MenuItem play = new MenuItem("PLAY");
        play.setOnMouseClicked(event -> {
            String inputNickname = inputText.getText();
            if (Pattern.matches("[a-zA-Z0-9]+ ?[a-zA-Z0-9]*", inputNickname) && inputNickname.length() > 3 && inputNickname.length() < 12) {
                System.out.println("Valid Name: " + inputNickname);
                boolean playerExists = view.checkIfPlayerExists(inputNickname);
                view.startGame(inputNickname, playerExists);
            }else {
                System.out.println("Invalid Name: " + inputNickname);
                inputText.setText("<Invalid Name>");
            }
        });

        MenuItem delete = new MenuItem("DELETE");
        delete.setOnMouseClicked(event -> {
            String inputNickname = inputText.getText();
            view.deletePlayer(inputNickname);
        });

        MenuItem back = new MenuItem("BACK");
        back.setOnMouseClicked(event -> view.showStartMenu());

        MenuBox menuBox = new MenuBox(input, play, delete, back);
        menuBox.setTranslateX(halfWindowSizeX - 125);
        menuBox.setTranslateY(275);
        root.getChildren().addAll(title, menuBox);
        return root;
    }

    public Parent createEscapeMenu(GameWindow view) {
        Pane root = new Pane();
        root.setPrefSize(windowSizeX, windowSizeY);
        root.setTranslateX(560);
        root.setTranslateY(240);

        //Background
        Rectangle background = new Rectangle(1920, 1080, Color.BLACK);
        background.setTranslateX(-560);
        background.setTranslateY(-240);
        background.setOpacity(0.8);
        menuBackground.attachDefaultImages();

        //Title
        Title title = new Title("M O R R O W P O R O");
        title.setTranslateX(halfWindowSizeX - 265);
        title.setTranslateY(75);

        //MenuBorder
        Rectangle menuBorder = new Rectangle(800, 600);
        menuBorder.setTranslateX(menuBorder.getTranslateX() - 1);
        menuBorder.setTranslateY(menuBorder.getTranslateY() + 1);
        menuBorder.setStroke(Color.BLACK);
        menuBorder.setStrokeWidth(2);
        menuBorder.setFill(Color.DARKVIOLET);

        //MenuItems
        MenuItem play = new MenuItem("BACK TO GAME");
        play.setOnMouseClicked(event -> view.quitPauseMenu());

        MenuItem tryAgain = new MenuItem("TRY AGAIN!");
        tryAgain.setOnMouseClicked(event -> view.createNewGame());

        MenuItem nextLevel = new MenuItem("NEXT LEVEL");
        nextLevel.setOnMouseClicked(event -> view.createNextLevel());

        MenuItem exit = new MenuItem("QUIT");
        exit.setOnMouseClicked(event -> view.switchToHomeMenu());

        MenuBox menuBox = new MenuBox(
                play,
                tryAgain,
                //new MenuItem("HIGHSCORES"),
                nextLevel,
                exit
        );
        menuBox.setTranslateX(halfWindowSizeX - 125);
        menuBox.setTranslateY(275);

        //add everything to root(Pane)
        root.getChildren().addAll(background, menuBorder, menuBackground.menuBackgroundImageView, title, menuBox);
        return root;
    }

    public Parent createLevelCompletedMenu(GameWindow view) {
        Pane root = new Pane();
        root.setPrefSize(windowSizeX, windowSizeY);
        root.setTranslateX(560);
        root.setTranslateY(240);

        //Background
        Rectangle background = new Rectangle(1920, 1080, Color.BLACK);
        background.setTranslateX(-560);
        background.setTranslateY(-240);
        background.setOpacity(0.8);
        menuBackground.attachDefaultImages();

        //Title
        Title title = new Title("!!!Level Completed!!!", Color.DARKOLIVEGREEN);
        title.setTranslateX(halfWindowSizeX - 265);
        title.setTranslateY(75);

        //MenuBorder
        Rectangle menuBorder = new Rectangle(800, 600);
        menuBorder.setTranslateX(menuBorder.getTranslateX() - 1);
        menuBorder.setTranslateY(menuBorder.getTranslateY() + 1);
        menuBorder.setStroke(Color.BLACK);
        menuBorder.setStrokeWidth(2);
        menuBorder.setFill(Color.GREENYELLOW);

        //MenuItems
        MenuItem nextLevel = new MenuItem("NEXT LEVEL");
        nextLevel.setOnMouseClicked(event -> view.createNextLevel());

        MenuItem exit = new MenuItem("QUIT");
        exit.setOnMouseClicked(event -> view.switchToHomeMenu());

        DecimalFormat df = new DecimalFormat("#.##");
        MenuItem yourHighscore = new MenuItem("POINTS: " + df.format(view.getHighscore()));

        MenuBox menuBox = new MenuBox(
                yourHighscore,
                nextLevel,
                exit
        );
        menuBox.setTranslateX(halfWindowSizeX - 125);
        menuBox.setTranslateY(275);

        //add everything to root(Pane)
        root.getChildren().addAll(background, menuBorder, menuBackground.menuBackgroundImageView, title, menuBox);
        return root;
    }

    public Node createGoalMenu(GameWindow view) {
        Pane root = new Pane();
        root.setPrefSize(windowSizeX, windowSizeY);
        root.setTranslateX(560);
        root.setTranslateY(240);

        //Background
        Rectangle background = new Rectangle(1920, 1080, Color.BLACK);
        background.setTranslateX(-560);
        background.setTranslateY(-240);
        background.setOpacity(0.8);
        menuBackground.attachDefaultImages();

        //Title
        Title title = new Title("!!!YOU WIN!!!", Color.DARKOLIVEGREEN);
        title.setTranslateX(halfWindowSizeX - 265);
        title.setTranslateY(75);

        //MenuBorder
        Rectangle menuBorder = new Rectangle(800, 600);
        menuBorder.setTranslateX(menuBorder.getTranslateX() - 1);
        menuBorder.setTranslateY(menuBorder.getTranslateY() + 1);
        menuBorder.setStroke(Color.BLACK);
        menuBorder.setStrokeWidth(2);
        menuBorder.setFill(Color.YELLOW);

        //MenuItems
        DecimalFormat df = new DecimalFormat("#.##");
        MenuItem yourHighscore = new MenuItem("HIGHSCORE: " + df.format(view.getTotalHighscore()));

        MenuItem exit = new MenuItem("QUIT");
        exit.setOnMouseClicked(event -> view.switchToHomeMenu());

        MenuBox menuBox = new MenuBox(
                yourHighscore,
                exit
        );
        menuBox.setTranslateX(halfWindowSizeX - 125);
        menuBox.setTranslateY(275);

        root.getChildren().addAll(background, menuBorder, menuBackground.menuBackgroundImageView, title, menuBox);
        return root;
    }

    public Parent createGameOverMenu(GameWindow view) {
        Pane root = new Pane();
        root.setPrefSize(windowSizeX, windowSizeY);
        root.setTranslateX(560);
        root.setTranslateY(240);

        //Background
        Rectangle background = new Rectangle(1920, 1080, Color.BLACK);
        background.setTranslateX(-560);
        background.setTranslateY(-240);
        background.setOpacity(0.8);
        menuBackground.attachDefaultImages();

        //Title
        Title title = new Title("!!!YOU LOOSE!!!", Color.YELLOWGREEN);
        title.setTranslateX(halfWindowSizeX - 265);
        title.setTranslateY(75);

        //MenuBorder + MenuBackground
        Rectangle menuBorder = new Rectangle(800, 600);
        menuBorder.setTranslateX(menuBorder.getTranslateX() - 1);
        menuBorder.setTranslateY(menuBorder.getTranslateY() + 1);
        menuBorder.setStroke(Color.BLACK);
        menuBorder.setStrokeWidth(2);
        menuBorder.setFill(Color.DARKRED);

        //MenuItems
        MenuItem play = new MenuItem("TRY AGAIN!");
        play.setOnMouseClicked(event -> view.createNewGame());

        MenuItem exit = new MenuItem("QUIT");
        exit.setOnMouseClicked(event -> view.switchToHomeMenu());

        DecimalFormat df = new DecimalFormat("#.##");
        MenuItem yourHighscore = new MenuItem("POINTS: " + df.format(view.getHighscore()));

        MenuBox menuBox = new MenuBox(
                yourHighscore,
                play,
                //new MenuItem("HIGHSCORES"),
                exit
        );
        menuBox.setTranslateX(halfWindowSizeX - 125);
        menuBox.setTranslateY(275);

        //add everything to root(Pane)
        root.getChildren().addAll(background, menuBorder, menuBackground.menuBackgroundImageView, title, menuBox);
        return root;
    }

    public Parent createPlayerStatusMenu(GameWindow view, Player player) {
        Pane root = new Pane();
        root.setPrefSize(windowSizeX, windowSizeY);
        root.setTranslateX(560);
        root.setTranslateY(240);

        //Background
        Rectangle background = new Rectangle(1920, 1080, Color.BLACK);
        background.setTranslateX(-560);
        background.setTranslateY(-240);
        background.setOpacity(0.8);
        menuBackground.attachDefaultImages();

        //Title
        Title title = new Title("M O R R O W P O R O");
        title.setTranslateX(halfWindowSizeX - 265);
        title.setTranslateY(50);

        //MenuBorder
        Rectangle menuBorder = new Rectangle(800, 600);
        menuBorder.setTranslateX(menuBorder.getTranslateX() - 1);
        menuBorder.setTranslateY(menuBorder.getTranslateY() + 1);
        menuBorder.setStroke(Color.BLACK);
        menuBorder.setStrokeWidth(2);
        menuBorder.setFill(Color.GRAY);

        //StringItems
        StringItem titel = new StringItem("Title: Warrior of Morrowporo");
        StringItem name = new StringItem("Name: MorrowporoSpm");
        StringItem nothing = new StringItem("");
        StringItem stats = new StringItem("Status:");
        StringItem STR = new StringItem("STR: "+player.getSTR());
        StringItem GES = new StringItem("GES: "+player.getGES());
        StringItem INT = new StringItem("INT: "+player.getINT());
        StringItem AUS = new StringItem("AUS: "+player.getAUS());
        StringItem KON = new StringItem("KON: "+player.getKON());


        MenuBox StatsBox = new MenuBox(
                titel,
                name,
                nothing,
                stats,
                STR,
                GES,
                INT,
                AUS,
                KON
        );
        StatsBox.setTranslateY(150);


        StringItem healthpoints = new StringItem("HP: "+player.getMaxHP());
        StringItem manapoints = new StringItem("MP: "+player.getMaxMP());
        StringItem staminapoints = new StringItem("SP: "+player.getMaxSP());
        StringItem resourcepoints = new StringItem("Resourcepoints: " +player.getResourcepoints());
        StringItem skillpoints = new StringItem("Skillpoints: "+player.getSkillpoints());

        MenuBox ResourceBox = new MenuBox(
                healthpoints,
                manapoints,
                staminapoints,
                resourcepoints,
                skillpoints
        );
        ResourceBox.setTranslateY(274);
        ResourceBox.setTranslateX(310);

        root.getChildren().addAll(background, menuBorder, title, StatsBox, ResourceBox);

        if (player.getSkillpoints() > 0){
            SkillpointItem STRitem = new SkillpointItem();
            STRitem.setOnMouseClicked(event -> {player.incSTR(); view.createPlayerStatusMenu();});
            SkillpointItem GESitem = new SkillpointItem();
            GESitem.setOnMouseClicked(event -> {player.incGES(); view.createPlayerStatusMenu();});
            SkillpointItem INTitem = new SkillpointItem();
            INTitem.setOnMouseClicked(event -> {player.incINT(); view.createPlayerStatusMenu();});
            SkillpointItem AUSitem = new SkillpointItem();
            AUSitem.setOnMouseClicked(event -> {player.incAUS(); view.createPlayerStatusMenu();});
            SkillpointItem KONitem = new SkillpointItem();
            KONitem.setOnMouseClicked(event -> {player.incKON(); view.createPlayerStatusMenu();});

            MenuBox incStatsBox = new MenuBox(
                    STRitem,
                    GESitem,
                    INTitem,
                    AUSitem,
                    KONitem
            );
            incStatsBox.setTranslateY(274);
            incStatsBox.setTranslateX(120);

            //add everything to root(Pane)
            root.getChildren().addAll(incStatsBox);
        }

        if (player.getResourcepoints() > 0){
            SkillpointItem HPitem = new SkillpointItem();
            HPitem.setOnMouseClicked(event -> {player.incHP(); view.setGameControllerHealthBarWidth(); view.createPlayerStatusMenu();});
            SkillpointItem MPitem = new SkillpointItem();
            MPitem.setOnMouseClicked(event -> {player.incMP(); view.setGameControllerManaBarWidth(); view.createPlayerStatusMenu();});
            SkillpointItem SPitem = new SkillpointItem();
            SPitem.setOnMouseClicked(event -> {player.incSP(); view.setGameControllerStaminaBarWidth(); view.createPlayerStatusMenu();});


            MenuBox incResourcesBox = new MenuBox(
                    HPitem,
                    MPitem,
                    SPitem
            );
            incResourcesBox.setTranslateY(274);
            incResourcesBox.setTranslateX(430);

            //add everything to root(Pane)
            root.getChildren().addAll(incResourcesBox);
        }

        return root;
    }

    public Parent createHighscoreMenu(GameWindow view, List<String> lines) {
        Pane root = new Pane();
        root.setPrefSize(windowSizeX, windowSizeY);
        Rectangle background = new Rectangle(windowSizeX, windowSizeY, Color.DARKGRAY);
        menuBackground.attachDefaultImages();
        root.getChildren().addAll(background, menuBackground.menuBackgroundImageView);

        Title title = new Title("M O R R O W P O R O");
        title.setTranslateX(halfWindowSizeX - 265);
        title.setTranslateY(75);

        MenuItem back = new MenuItem("BACK");
        back.setOnMouseClicked(event -> view.showStartMenu());

        MenuBox menuBox = new MenuBox(back);
        menuBox.setTranslateX(halfWindowSizeX - 125);
        menuBox.setTranslateY(550);

        List<StringItem> stringItemList = new ArrayList<>();

        //StringItems
        if(lines == null || lines.size() < 1){ stringItemList.add(new StringItem("HIGHSCORE IS HUNGRY!"));
        }else {
            int count = 1;
            for (String currentString : lines) {
                if (count < 10) {
                    currentString = "  " + count++ + ". <  " + currentString + "  >";
                } else {
                    currentString = count++ + ". <  " + currentString + "  >";
                }
                stringItemList.add(new StringItem(currentString));
            }
        }

        MenuBox highscoreBox = new MenuBox(stringItemList);
        highscoreBox.setTranslateY(200);
        highscoreBox.setTranslateX(230);

        root.getChildren().addAll(title, menuBox, highscoreBox);
        return root;
    }

    public Parent createControlsMenu(GameWindow view) {
        Pane root = new Pane();

        root.setPrefSize(windowSizeX, windowSizeY);

        Rectangle background = new Rectangle(windowSizeX, windowSizeY, Color.DARKGRAY);

        menuBackground.attachDefaultImages();
        root.getChildren().addAll(background, menuBackground.menuBackgroundImageView);

        Title title = new Title("M O R R O W P O R O");
        title.setTranslateX(halfWindowSizeX - 265);
        title.setTranslateY(75);

        MenuItem back = new MenuItem("BACK");
        back.setOnMouseClicked(event -> view.showStartMenu());

        MenuBox menuBox = new MenuBox(back);
        menuBox.setTranslateX(halfWindowSizeX - 125);
        menuBox.setTranslateY(550);

        StringItem keyW = new StringItem("W => Jump");
        StringItem left = new StringItem("A => Left");
        StringItem right = new StringItem("D => Right");
        StringItem space = new StringItem("SPACE => Shoot Arrow");
        StringItem keyOne = new StringItem("KEY_1 => Shoot MagicalBolt");
        StringItem keyC = new StringItem("C => Status Menu");
        StringItem escape = new StringItem("ESC => Pause Menu");

        MenuBox controlsBox = new MenuBox(
                keyW,
                left,
                right,
                space,
                keyOne,
                keyC,
                escape
        );
        controlsBox.setTranslateY(200);
        controlsBox.setTranslateX(230);

        root.getChildren().addAll(title, menuBox, controlsBox);
        return root;
    }

    private static class MenuItem extends StackPane{
        MenuItem(String name){
            LinearGradient lienearGradient = new LinearGradient(0, 0, 1, 0, true, CycleMethod.NO_CYCLE, new Stop(0, Color.DARKVIOLET),
                    new Stop(0.1, Color.BLACK),
                    new Stop(0.9, Color.BLACK),
                    new Stop(1, Color.DARKVIOLET));

            Rectangle background = new Rectangle(200, 30);
            background.setOpacity(0.5);

            Text text = new Text(name);
            text.setFill(Color.DARKGREY);
            text.setFont(Font.font("Arial", FontWeight.SEMI_BOLD, 22));

            setAlignment(Pos.CENTER);
            getChildren().addAll(background, text);

            setOnMouseEntered(event -> {
                background.setFill(lienearGradient);
                text.setFill(Color.WHITE);
            });

            setOnMouseExited(event -> {
                background.setFill(Color.BLACK);
                text.setFill(Color.DARKGREY);
            });

            setOnMousePressed(event -> background.setFill(Color.DARKVIOLET));

            setOnMouseReleased(event -> background.setFill(lienearGradient));
        }
    }

    private static class StringItem extends StackPane{
        StringItem(String name){
            Rectangle background = new Rectangle(300, 30);
            background.setOpacity(0.5);

            Text text = new Text(name);
            text.setFill(Color.DARKGREY);
            text.setFont(Font.font("Arial", FontWeight.SEMI_BOLD, 22));

            setAlignment(Pos.CENTER_LEFT);
            getChildren().addAll(background, text);
        }
    }

    private static class SkillpointItem extends StackPane{
        SkillpointItem(){
            LinearGradient lienearGradient = new LinearGradient(0, 0, 1, 0, true, CycleMethod.NO_CYCLE, new Stop(0, Color.DARKVIOLET),
                    new Stop(0.1, Color.BLACK),
                    new Stop(0.9, Color.BLACK),
                    new Stop(1, Color.DARKVIOLET));

            Rectangle background = new Rectangle(30, 30);
            background.setOpacity(0.5);

            Text text = new Text("[+]");
            text.setFill(Color.DARKGREY);
            text.setFont(Font.font("Arial", FontWeight.SEMI_BOLD, 22));

            setAlignment(Pos.CENTER);
            getChildren().addAll(background, text);

            setOnMouseEntered(event -> {
                background.setFill(lienearGradient);
                text.setFill(Color.WHITE);
            });

            setOnMouseExited(event -> {
                background.setFill(Color.BLACK);
                text.setFill(Color.DARKGREY);
            });

            setOnMousePressed(event -> background.setFill(Color.DARKVIOLET));

            setOnMouseReleased(event -> background.setFill(lienearGradient));
        }
    }

    private static class InputItem extends StackPane{
        InputItem(String s){
            Rectangle background = new Rectangle(200, 30);
            background.setOpacity(0.5);

            TextField textField = new TextField(s);
            textField.setMaxWidth(200);
            textField.setAlignment(Pos.CENTER);

            textField.setStyle("-fx-background-color: BLACK; -fx-text-inner-color: DARKVIOLET;");
            textField.setFont(Font.font("Arial", FontWeight.SEMI_BOLD, 22));

            setAlignment(Pos.CENTER_LEFT);
            getChildren().addAll(background, textField);
        }
    }

    private static class MenuBox extends VBox {
        MenuBox(MenuItem... menuItems){
            getChildren().add(createSeperator(200));
            for (MenuItem currentItem : menuItems){
                getChildren().addAll(currentItem, createSeperator(200));
            }
        }

        MenuBox(StringItem... stringItems){
            getChildren().add(createSeperator(300));
            for (StringItem currentStringItem : stringItems){
                getChildren().addAll(currentStringItem, createSeperator(300));
            }
        }

        MenuBox(SkillpointItem... skillpointItems){
            getChildren().add(createSeperator(30));
            for (SkillpointItem currentSkillpointItem : skillpointItems){
                getChildren().addAll(currentSkillpointItem, createSeperator(30));
            }
        }

        MenuBox(List<StringItem> stringItemList) {
            getChildren().add(createSeperator(300));
            for (StringItem currentItem : stringItemList){
                currentItem.setAlignment(Pos.CENTER);
                getChildren().addAll(currentItem, createSeperator(300));
            }
        }

        MenuBox(InputItem input, MenuItem play, MenuItem delete, MenuItem back) {
            getChildren().add(createSeperator(200));
            getChildren().addAll(input, createSeperator(200));
            getChildren().addAll(play, createSeperator(200));
            getChildren().addAll(delete, createSeperator(200));
            getChildren().addAll(back, createSeperator(200));
        }

        private Line createSeperator(int x) {
            Line line = new Line();
            line.setEndX(x);
            line.setStroke(Color.DARKGREY);
            return line;
        }
    }

    private static class Title extends StackPane {
        Title(String name){
            Rectangle background = new Rectangle(530, 60);
            background.setStroke(Color.WHITE);
            background.setStrokeWidth(2);
            background.setFill(null);

            Text text = new Text(name);
            text.setFill(Color.WHITE);
            text.setFont(Font.font("Arial", FontWeight.SEMI_BOLD, 50));

            setAlignment(Pos.CENTER);
            getChildren().addAll(background, text);
        }

        Title(String name, Color color){
            Rectangle background = new Rectangle(530, 60);
            background.setStroke(color);
            background.setStrokeWidth(2);
            background.setFill(null);

            Text text = new Text(name);
            text.setFill(color);
            text.setFont(Font.font("Arial", FontWeight.SEMI_BOLD, 50));

            setAlignment(Pos.CENTER);
            getChildren().addAll(background, text);
        }
    }
}
