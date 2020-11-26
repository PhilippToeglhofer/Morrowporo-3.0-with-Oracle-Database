package Model;

import javafx.geometry.Point2D;

public class JumpVelocityResults {

    private boolean jump;
    private Point2D velocity;

    public JumpVelocityResults(boolean jump, Point2D velocity) {
        this.jump = jump;
        this.velocity = velocity;
    }

    public boolean isJump() {
        return jump;
    }

    public void setJump(boolean jump) {
        this.jump = jump;
    }

    public Point2D getVelocity() {
        return velocity;
    }

    public void setVelocity(Point2D velocity) {
        this.velocity = velocity;
    }

}
