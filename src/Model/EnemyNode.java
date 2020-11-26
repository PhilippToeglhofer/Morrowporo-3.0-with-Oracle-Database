package Model;

import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class EnemyNode extends DamageCalculation{

    private final Node node;
    private final int maxHealth;
    private int health;
    private final int AUS;
    private final int KON;
    private final int experience;
    private final int damage;
    private final int healthBarY = 17;
    private double healthBarX = -10;
    private double oldWidth = 0;
    private boolean showHealthBar = false;
    private Point2D enemyVelocity = new Point2D(0, 0);
    private Rectangle healthBar;

    public EnemyNode(Node node, int maxHealth, int AUS, int KON, int experience, int damage){
        this.node = node;
        this.maxHealth = maxHealth;
        this.health = maxHealth;
        this.AUS = AUS;
        this.KON = KON;
        this.experience = experience;
        this.damage = damage;
    }

    public Point2D getEnemyVelocity() {
        return enemyVelocity;
    }

    public void addEnemyVelocity(int x, int y) {
        enemyVelocity = enemyVelocity.add(x, y);
    }

    public Node getNode() {
        return node;
    }

    public int getHealth() {
        return health;
    }

    public void setHealth(int health){
        System.out.println("Enemy Health: " + health);
        this.health = health;
        updateHealthBar();
    }

    public void createHealthBar(double x, double y){
        Rectangle healthBar = new Rectangle(x - healthBarX, y - healthBarY, 84 , 10);
        healthBar.setStroke(Color.BLACK);
        healthBar.setFill(Color.DARKRED);
        this.healthBar = healthBar;

        /*Text amountOfHealth = new Text(x + 31, y - 10, "" + health);
        amountOfHealth.setFill(Color.GOLD);
        amountOfHealth.setTextAlignment(TextAlignment.CENTER);*/
    }

    private void updateHealthBar(){

        double percentageOfHealthLost = 84 * ((double) health / (double) maxHealth);
        if (oldWidth == 0) {
            healthBarX += (84 - percentageOfHealthLost) / 2;
        }else {
            healthBarX += (84 - percentageOfHealthLost) / 2 / oldWidth;
        }
        oldWidth += 1.5;
        healthBar.setWidth(percentageOfHealthLost);
    }

    public void setShowHealthBar(boolean healthBarBoolean) {
        this.showHealthBar = healthBarBoolean;
    }

    public boolean isShowHealthBar() {
        return showHealthBar;
    }

    public Rectangle getHealthBar() {
        return healthBar;
    }

    public void setHealthBar(double x, double y) {
        this.healthBar.setX(x + healthBarX);
        this.healthBar.setY(y - healthBarY);
    }

    private void setHealthBarX(double healthBarX) {
        this.healthBarX += healthBarX;
    }

    public int getExperience() {
        return experience;
    }

    public int getPhysicalDamage() {
        return damage;
    }

    public int getMagicalDamage() {
        return 0;
    }

    public int getAUS() {
        return AUS;
    }

    public int getKON() {
        return KON;
    }
}