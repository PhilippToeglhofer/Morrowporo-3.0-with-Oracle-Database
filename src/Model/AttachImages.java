package Model;

import Controller.ImageLoader;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class AttachImages {

    public ImageView imageView;
    public ImageView imageViewLife;
    public ImageView imageViewHearth;
    public ImageView imageViewPlayer;
    public ImageView imageViewCloud1;
    public ImageView imageViewCloud2;
    public ImageView imageViewCloud3;
    public ImageView imageViewCloud4;


    private final String imageSourceNamePlayer = "GolemMorrowporo2.png";
    private final Image imagePlayer = new ImageLoader().loadImage(imageSourceNamePlayer);
    private final String imageSourceNameGrass = "GrassMorrow6.png";
    private final Image imageGrass = new ImageLoader().loadImage(imageSourceNameGrass);
    private final String imageSourceNameDirt = "DirtMorrow2.png";
    private final Image imageDirt = new ImageLoader().loadImage(imageSourceNameDirt);
    private final String imageSourceNameArrow = "arrow2.png";
    private final Image imageArrow = new ImageLoader().loadImage(imageSourceNameArrow);
    private final String imageSourceNameWater = "WaterMorrow.png";
    private final Image imageWater = new ImageLoader().loadImage(imageSourceNameWater);
    private final String imageSourceNameLava = "LavaMorrow3.png";
    private final Image imageLava = new ImageLoader().loadImage(imageSourceNameLava);
    private final String imageSourceNameLife = "HearthMorrow.png";
    private final Image imageLife = new ImageLoader().loadImage(imageSourceNameLife);
    private final String imageSourceNameGoal = "GoalMorrow.png";
    private final Image imageGoal = new ImageLoader().loadImage(imageSourceNameGoal);
    private final String imageSourceNameEnemy = "EnemyGolemMorrowporo.png";
    private final Image imageEnemy = new ImageLoader().loadImage(imageSourceNameEnemy);
    private final String imageSourceNameMagicalBolt = "magicalBolt.png";
    private final Image imageMagicalBolt = new ImageLoader().loadImage(imageSourceNameMagicalBolt);
    private final String imageSourceNameCloud = "Cloud3Morrow.png";
    private final Image imageCloud = new ImageLoader().loadImage(imageSourceNameCloud);

    public void getPlayer(){
        attachDefaultImages(imagePlayer, imageSourceNamePlayer, 64,64);
        imageViewPlayer = imageView;
    }
    public void getGrass(){
        attachDefaultImages(imageGrass, imageSourceNameGrass, 96,96);
    }
    public void getDirt(){
        attachDefaultImages(imageDirt, imageSourceNameDirt, 96,96);
    }
    public void getWater(){
        attachDefaultImages(imageWater, imageSourceNameWater, 96,96);
    }
    public void getLava(){
        attachDefaultImages(imageLava, imageSourceNameLava, 96,96);
    }
    public void getArrow(){
        attachDefaultImages(imageArrow, imageSourceNameArrow, 64,64);
    }
    public void getLife(){
        attachDefaultImages(imageLife, imageSourceNameLife, 50,50);
        imageViewLife = imageView;
    }
    public void getHearth(){
        attachDefaultImages(imageLife, imageSourceNameLife, 72,62);
        imageViewHearth = imageView;
    }
    public void getEnemy(){
        attachDefaultImages(imageEnemy, imageSourceNameEnemy, 64,64);
    }
    public void getMagicalBolt(){
        attachDefaultImages(imageMagicalBolt, imageSourceNameMagicalBolt, 32,32);
    }
    public void getGoal(){
        attachDefaultImages(imageGoal, imageSourceNameGoal, 288,288);
    }
    public void getClouds(){
        attachDefaultImages(imageCloud, imageSourceNameCloud, 222,222);
        imageViewCloud1 = imageView;
        attachDefaultImages(imageCloud, imageSourceNameCloud, 222,222);
        imageViewCloud2 = imageView;
        attachDefaultImages(imageCloud, imageSourceNameCloud, 222,222);
        imageViewCloud3 = imageView;
        attachDefaultImages(imageCloud, imageSourceNameCloud, 222,222);
        imageViewCloud4 = imageView;
    }

    private void attachDefaultImages(Image image, String imageSourceName, int width, int height) {
        try {
            imageView = new ImageView(image);
            imageView.setFitWidth(width);
            imageView.setFitHeight(height);
            imageView.setPreserveRatio(true);
        }catch (Exception e){
            System.out.println("Error... " + imageSourceName + " attaching Image");
        }
    }
}
