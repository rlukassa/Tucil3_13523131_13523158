package components;
public class Piece {
    private char id;
    private int x, y; // Koordinat kiri atas atau atas (tergantung orientasi)
    private int size;
    private boolean isHorizontal;
    private boolean isPrimary;

    public Piece(char id, int x, int y, int size, boolean isHorizontal, boolean isPrimary) {
        this.id = id;
        this.x = x;
        this.y = y;
        this.size = size;
        this.isHorizontal = isHorizontal;
        this.isPrimary = isPrimary;
    }

    // Getter
    public char getId() { return id; }
    public int getX() { return x; }
    public int getY() { return y; }
    public int getSize() { return size; }
    public boolean isHorizontal() { return isHorizontal; }
    public boolean isPrimary() { return isPrimary; }

    // Setter untuk pergerakan
    public void setX(int x) { this.x = x; }
    public void setY(int y) { this.y = y; }
}
