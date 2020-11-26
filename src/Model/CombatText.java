package Model;

import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;

public class CombatText {

    private int hover;
    private final Node node;
    private Text combatText;

    public CombatText(Node node, String combatMessage, Color color) {
        this.hover = 0;
        this.node = node;
        combatText = new Text(node.getTranslateX() + node.getBoundsInLocal().getWidth()/2, node.getTranslateY() - 60, combatMessage);
        combatText.setFill(color);
        combatText.setStroke(Color.BLACK);
        combatText.setStrokeWidth(0.2);
        combatText.setScaleX(3);
        combatText.setScaleY(3);
    }

    public int getHover(){
        return hover+=1;
    }

    public Node getNode() {
        return node;
    }

    public Text getCombatText(){
        return combatText;
    }
}