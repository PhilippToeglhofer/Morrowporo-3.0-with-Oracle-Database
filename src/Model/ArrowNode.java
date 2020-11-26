package Model;

import javafx.geometry.Point2D;
import javafx.scene.Node;

public class ArrowNode {

    private final Node node;
    private Point2D arrowVelocity = new Point2D(0, 0.1);
    private boolean isArrow = true;

    public ArrowNode(Node node) {
        this.node = node;
    }

    public Node getNode() {
        return node;
    }

    public Point2D getArrowVelocity() {
        return arrowVelocity;
    }

    public void setArrowVelocity(int x, int y) {
        this.arrowVelocity = arrowVelocity.add(x, y);
    }

    public int getDamage() {
        return 6;
    }

    public boolean isArrow() {
        return isArrow;
    }

    public void setArrow(boolean arrow) {
        isArrow = arrow;
    }
}
