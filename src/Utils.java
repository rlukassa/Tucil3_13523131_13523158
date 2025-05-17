import view.UI ; 

public  class Utils {
    private int choices ; 
    private static boolean running ; 

    public Utils() {
        this.choices = 0 ; 
    }

    public int getChoices() {
        return choices;
    }

    public void setChoices(int choices) {
        this.choices = choices;
    }

    public void incrementChoices() { 
        this.choices++ ;  
    }

    public boolean isRunning() {
        return running;
    }

    public void setRunning(boolean running) {
        Utils.running = running;
    }

    public void makeUI() { 
        int menuCount = 0;
        System.out.println("Masukkan pilihan menu :");
        menuCount++;
        System.out.println("1. Algoritma Uniform Cost Search (UCS)");
        menuCount++;
        System.out.println("2. Algoritma Greedy Best First Search");
        menuCount++;
        System.out.println("3. Algoritma A* Search");
        menuCount++;
        System.out.println("4. Algoritma Hill Climbing Search");
        menuCount++;
        this.choices = menuCount;
    }

    public int rangeChoices() {
        makeUI();
        return this.choices ; 
    }




}
