package Model;

/**
 * Created by Kata on 03.04.2018.
 */
public class Player extends DamageCalculation {
    private final String nickname;
    private int level;
    private int experience;
    private int skillpoints; //Skillpunkte
    private int resourcepoints; //Ressourcenpunkte
    private int STR; //STRENGTH (Stärke => Waffenschdaden)
    private int GES; //AGILITY (Geschicklichkeit => Waffen-/MagieKritWahrscheinlichkeit)
    private int INT; //INTELLIGENCE (Intelligenz => Magieschaden)
    private int AUS; //DEFENSIV (Ausdauer => Rüstung)
    private int KON; //CONCENTRAION(Konzentration => Magieresistenz)
    private int HP; //Healthpoints (Lebenspunkte)
    private int MP; //Manapoints (Manapunkte)
    private int SP; //Staminapoints (Ausdauerpunkte)
    private int currentHP;
    private int currentMP;
    private int currentSP;
    //MAX DEF = 100 => 50% Damage mitigation //2 DEF = 1%
    //MAX CRITICAL CHANCE => 100 GES => CRITICAL HIT = damage*2

    private int savelevel;
    private int saveexperience;
    private int saveskillpoints;
    private int saveresourcepoints;
    private int saveSTR;
    private int saveGES;
    private int saveINT;
    private int saveAUS;
    private int saveKON;
    private int saveHP;
    private int saveMP;
    private int saveSP;

    public Player(String nickname) {
        this.nickname = nickname;
        level = 1;
        experience = 0;
        skillpoints = 0;
        resourcepoints = 0;
        STR = 1;
        GES = 1;
        INT = 1;
        AUS = 1;
        KON = 1;
        HP = 24;
        MP = 24;
        SP = 24;
        currentHP = HP;
        currentMP = MP;
        currentSP = SP;
        savelevel = level;
        saveexperience = experience;
        saveskillpoints = skillpoints;
        saveresourcepoints = resourcepoints;
        saveSTR = STR;
        saveGES = GES;
        saveINT = INT;
        saveAUS = AUS;
        saveKON = KON;
        saveHP = HP;
        saveMP = MP;
        saveSP = SP;
        System.out.println("New Player Initialised");
    }

    public Player(String nickname, int level, int experience, int STR, int GES, int INT, int AUS, int KON, int HP, int MP, int SP, int skillpoints, int resourcepoints) {
        this.nickname = nickname;
        this.level = level;
        this.experience = experience;
        this.STR = STR;
        this.GES = GES;
        this.INT = INT;
        this.AUS = AUS;
        this.KON = KON;
        this.HP = HP;
        this.MP = MP;
        this.SP = SP;
        this.skillpoints = skillpoints;
        this.resourcepoints = resourcepoints;
        currentHP = HP;
        currentMP = MP;
        currentSP = SP;
        savelevel = level;
        saveexperience = experience;
        saveskillpoints = skillpoints;
        saveresourcepoints = resourcepoints;
        saveSTR = STR;
        saveGES = GES;
        saveINT = INT;
        saveAUS = AUS;
        saveKON = KON;
        saveHP = HP;
        saveMP = MP;
        saveSP = SP;
        System.out.println("Player from Database Initialised");

    }

    public boolean getExperience(int experience) {
        this.experience += experience;
        System.out.println("EXP: " + this.experience);
        return checkIfLevelUp();
    }

    private boolean checkIfLevelUp(){
        if (experience >= 10*level){
            level++;
            experience = 0;
            System.out.println("Level UP! You reached Level " + level);
            increaseStats();
            recoverResources();
            return true;
        }
        return false;
    }

    public int getLevel() {
        return level;
    }

    public double getPercentageLevelUp() {
        return (double) experience/(10*level);
    }

    private void increaseStats(){
        STR += 2;
        GES += 2;
        INT += 2;
        AUS += 2;
        KON += 2;
        HP += 10;
        MP += 10;
        SP += 10;
        skillpoints++;
        resourcepoints++;
    }

    public void savePlayerStatus(){
        savelevel = level;
        saveexperience = experience;
        saveskillpoints = skillpoints;
        saveresourcepoints = resourcepoints;
        saveSTR = STR;
        saveGES = GES;
        saveINT = INT;
        saveAUS = AUS;
        saveKON = KON;
        saveHP = HP;
        saveMP = MP;
        saveSP = SP;
    }

    public void loadPlayerStatus(){
        level = savelevel;
        experience = saveexperience;
        skillpoints = saveskillpoints;
        resourcepoints = saveresourcepoints;
        STR = saveSTR;
        GES = saveGES;
        INT = saveINT;
        AUS = saveAUS;
        KON = saveKON;
        HP = saveHP;
        MP = saveMP;
        SP = saveSP;
        currentHP = HP;
        currentMP = MP;
        currentSP = SP;
    }

    public String getNickname() {
        return nickname;
    }

    public int getSTR() {
        return STR;
    }

    public int getGES() {
        return GES;
    }

    public int getINT() {
        return INT;
    }

    public int getAUS() {
        return AUS;
    }

    public int getKON() {
        return KON;
    }

    public int getMaxHP() {
        return HP;
    }

    public int getMaxMP() {
        return MP;
    }

    public int getMaxSP() {
        return SP;
    }

    public int getCurrentHP() {
        return currentHP;
    }

    public int getCurrentMP() {
        return currentMP;
    }

    public int getCurrentSP() {
        return currentSP;
    }

    public int getCurrentExperience() {
        return experience;
    }

    public double getPercentageHP() {
        return (double) currentHP/HP;
    }

    public double getPercentageMP() {
        return (double) currentMP/MP;
    }

    public double getPercentageSP() {
        return (double) currentSP/SP;
    }

    public int getSkillpoints() {
        return skillpoints;
    }

    public int getResourcepoints() {
        return resourcepoints;
    }

    public void incSTR() {
        if (skillpoints > 0) {
            STR++;
            skillpoints--;
        }
    }

    public void incGES() {
        if (skillpoints > 0) {
            GES++;
            skillpoints--;
        }
    }

    public void incINT() {
        if (skillpoints > 0) {
            INT++;
            skillpoints--;
        }
    }

    public void incAUS() {
        if (skillpoints > 0) {
            AUS++;
            skillpoints--;
        }
    }

    public void incKON() {
        if (skillpoints > 0) {
            KON++;
            skillpoints--;
        }
    }

    public void incHP() {
        if (resourcepoints > 0) {
            HP += 25;
            resourcepoints--;
        }
    }

    public void incMP() {
        if (resourcepoints > 0) {
            MP += 25;
            resourcepoints--;
        }
    }

    public void incSP() {
        if (resourcepoints > 0) {
            SP += 25;
            resourcepoints--;
        }
    }

    public void setCurrentHP(int currentHP) {
        this.currentHP = currentHP;
    }

    public void looseMana(int MP){
        this.currentMP -= MP;
    }

    public void looseStamina(int SP){
        this.currentSP -= SP;
    }

    public void generateStamina(int SP){
        this.currentSP += SP;
    }

    public void recoverResources(){
        currentHP = HP;
        currentMP = MP;
        currentSP = SP;
    }

}