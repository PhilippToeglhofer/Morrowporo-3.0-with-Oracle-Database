package Model;

import Controller.ImageLoader;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

/**
 * Created by Kata on 16.04.2018.
 */
public class MenuBackground {
    public ImageView menuBackgroundImageView;
    private final String imageSourceName = "StartScreenBackground.png";
    private final Image menuBackgroundImage = new ImageLoader().loadImage(imageSourceName);

    public void attachDefaultImages() {
        try {
            menuBackgroundImageView = new ImageView(menuBackgroundImage);
            menuBackgroundImageView.setFitWidth(800);
            menuBackgroundImageView.setFitHeight(600);
        } catch (Exception e) {
            System.out.println("Error... " + imageSourceName + " attaching Image");
        }
    }
}
