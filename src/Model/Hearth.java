package Model;

/**
 * Created by Kata on 09.04.2018.
 */
public class Hearth {
    private int hearths = 3;

    public int getHearths(){
        return hearths;
    }

    public void loose_aHearth(){
        if (hearths > 0) { hearths--; }
    }

    public void get_aHearth(){
        hearths++;
    }

    public void set3Hearths() { hearths = 3; }

}
