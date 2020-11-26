package Model;

import java.util.Random;

public class DamageCalculation {

    public int getDamage(double attackerPhysicalDamage, double attackerMagicalDamage, int attackerGES, int defenderAUS, int defenderKON, int defenderHealth) {
        Random random = new Random();
        if (random.nextInt(99) < attackerGES){
            attackerPhysicalDamage *= 2;
            attackerMagicalDamage *= 2;
            System.out.println("CRITICAL HIT!");
        }
        System.out.println("Incoming DMG: " + attackerPhysicalDamage);
        if (attackerPhysicalDamage > 0){
            defenderHealth = calcDamageMitigation(attackerPhysicalDamage, defenderAUS, defenderHealth);
        }else {
            defenderHealth = calcDamageMitigation(attackerMagicalDamage, defenderKON, defenderHealth);
        }
        if (defenderHealth < 0){ defenderHealth = 0; }
        System.out.println("HEALTH: " + defenderHealth);
        return defenderHealth;
    }

    private int calcDamageMitigation(double damage, int DEF, int health) {
        if (DEF < 100) {
            double damageMitigation = DEF/200.0;
            System.out.println("Damage Mitigation: " + damageMitigation*100 + "%");
            return (int) (health - (damage * (1.0 - damageMitigation)));
        }else {
            System.out.println("Damage Mitigation: 50.0%");
            return (int) (health - (damage * 0.5));
        }
    }

}
