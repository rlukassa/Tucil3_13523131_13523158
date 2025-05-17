package src;
import java.util.ArrayList;

public class GameBoard {
    private int rows, cols;
    private char[][] grid;
    private ArrayList<Piece> pieces;
    private int exitX, exitY; // Koordinat pintu keluar
    private Piece primaryPiece;

    public GameBoard(int rows, int cols, ArrayList<Piece> pieces, int exitX, int exitY) {
        this.rows = rows;
        this.cols = cols;
        this.pieces = pieces;
        this.exitX = exitX;
        this.exitY = exitY;
        for (Piece p : pieces) {
            if (p.isPrimary()) {
                primaryPiece = p;
                break;
            }
        }
        updateGrid();
        // printGameBoard();  debug
    }

    // Membuat grid berdasarkan posisi piece
    private void updateGrid() {
        grid = new char[rows][cols];
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                grid[i][j] = '.';
            }
        }
        for (Piece p : pieces) {
            if (p.isHorizontal()) {
                for (int j = 0; j < p.getSize(); j++) {
                    grid[p.getY()][p.getX() + j] = p.getId();
                }
            } else {
                for (int i = 0; i < p.getSize(); i++) {
                    grid[p.getY() + i][p.getX()] = p.getId();
                }
            }
        }
        if (exitY >= 0 && exitY < rows && exitX >= 0 && exitX < cols) {
            grid[exitY][exitX] = 'K'; // Pintu keluar
        }
    }

    // Mengecek apakah primary piece sudah di pintu keluar
    public boolean isGoal() {
        if (primaryPiece.isHorizontal()) {
            // Consider goal reached if primary piece's right end is at or beyond exitX
            return primaryPiece.getX() + primaryPiece.getSize() - 1 >= exitX;
        } else {
            // Similarly for vertical
            return primaryPiece.getY() + primaryPiece.getSize() - 1 >= exitY;
        }
    }

    // Mendapatkan daftar gerakan yang mungkin
    public ArrayList<GameBoard> getNeighbors() {
        ArrayList<GameBoard> neighbors = new ArrayList<>();
        for (Piece p : pieces) {
            if (p.isHorizontal()) {
                // Coba gerak kiri
                if (p.getX() > 0 && grid[p.getY()][p.getX() - 1] == '.') {
                    ArrayList<Piece> newPieces = clonePieces();
                    for (Piece np : newPieces) {
                        if (np.getId() == p.getId()) {
                            np.setX(np.getX() - 1);
                        }
                    }
                    neighbors.add(new GameBoard(rows, cols, newPieces, exitX, exitY));
                }
                // Coba gerak kanan
                if (p.getX() + p.getSize() < cols && grid[p.getY()][p.getX() + p.getSize()] == '.') {
                    ArrayList<Piece> newPieces = clonePieces();
                    for (Piece np : newPieces) {
                        if (np.getId() == p.getId()) {
                            np.setX(np.getX() + 1);
                        }
                    }
                    neighbors.add(new GameBoard(rows, cols, newPieces, exitX, exitY));
                }
            } else {
                // Coba gerak atas
                if (p.getY() > 0 && grid[p.getY() - 1][p.getX()] == '.') {
                    ArrayList<Piece> newPieces = clonePieces();
                    for (Piece np : newPieces) {
                        if (np.getId() == p.getId()) {
                            np.setY(np.getY() - 1);
                        }
                    }
                    neighbors.add(new GameBoard(rows, cols, newPieces, exitX, exitY));
                }
                // Coba gerak bawah
                if (p.getY() + p.getSize() < rows && grid[p.getY() + p.getSize()][p.getX()] == '.') {
                    ArrayList<Piece> newPieces = clonePieces();
                    for (Piece np : newPieces) {
                        if (np.getId() == p.getId()) {
                            np.setY(np.getY() + 1);
                        }
                    }
                    neighbors.add(new GameBoard(rows, cols, newPieces, exitX, exitY));
                }
            }
        }
        return neighbors;
    }

    // Mendapatkan heuristic (jarak Manhattan ke pintu keluar untuk primary piece)
    public int getHeuristic() {
        if (primaryPiece.isHorizontal()) {
            return Math.abs(primaryPiece.getX() + primaryPiece.getSize() - 1 - exitX);
        } else {
            return Math.abs(primaryPiece.getY() + primaryPiece.getSize() - 1 - exitY);
        }
    }

    // Kloning daftar piece untuk state baru
    private ArrayList<Piece> clonePieces() {
        ArrayList<Piece> newPieces = new ArrayList<>();
        for (Piece p : pieces) {
            newPieces.add(new Piece(p.getId(), p.getX(), p.getY(), p.getSize(), p.isHorizontal(), p.isPrimary()));
        }
        return newPieces;
    }

    // Getter
    public char[][] getGrid() { return grid; }
    public ArrayList<Piece> getPieces() { return pieces; }
    public Piece getPrimaryPiece() { return primaryPiece; }
}
