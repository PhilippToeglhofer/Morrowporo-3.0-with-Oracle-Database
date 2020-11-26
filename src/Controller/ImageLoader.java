package Controller;

import javafx.scene.image.Image;

/**
 * Created by Kata on 10.04.2018.
 */
public class ImageLoader {
    
    private Image image;

    public Image loadImage(String imageName){
        try {
             image = new Image(ImageLoader.class.getResourceAsStream("/img/" + imageName));

        }catch (Exception e){
            System.out.println("Error... " + imageName + " Image not found");
        }
        return image;
    }
}