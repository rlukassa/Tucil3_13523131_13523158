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
        int primaryLeft = primaryPiece.getX();
        int primaryRight = primaryPiece.getX() + primaryPiece.getSize() - 1;
        int primaryTop = primaryPiece.getY();
        int primaryBottom = primaryPiece.getY() + primaryPiece.getSize() - 1;
        
        // Determine exit position relative to the primary piece
        // Exit is to the right of the board
        if (exitX >= cols) {
            return primaryPiece.isHorizontal() && primaryRight == cols - 1;
        } 
        // Exit is to the left of the board 
        else if (exitX < 0) {
            return primaryPiece.isHorizontal() && primaryLeft == 0;
        } 
        // Exit is above the board
        else if (exitY < 0) {
            return !primaryPiece.isHorizontal() && primaryTop == 0;
        } 
        // Exit is below the board
        else if (exitY >= rows) {
            return !primaryPiece.isHorizontal() && primaryBottom == rows - 1;
        }
        
        // Exit is inside the board - we need to check if primary piece is adjacent to exit
        // For horizontal primary piece
        if (primaryPiece.isHorizontal()) {
            // Exit to the right
            if (exitX > primaryRight && exitY == primaryTop) {
                return primaryRight == exitX - 1;
            }
            // Exit to the left
            else if (exitX < primaryLeft && exitY == primaryTop) {
                return primaryLeft == exitX + 1;
            }
        }
        // For vertical primary piece
        else {
            // Exit below
            if (exitY > primaryBottom && exitX == primaryLeft) {
                return primaryBottom == exitY - 1;
            }
            // Exit above
            else if (exitY < primaryTop && exitX == primaryLeft) {
                return primaryTop == exitY + 1;
            }
        }
        
        return false;
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
        int primaryLeft = primaryPiece.getX();
        int primaryRight = primaryPiece.getX() + primaryPiece.getSize() - 1;
        int primaryTop = primaryPiece.getY();
        int primaryBottom = primaryPiece.getY() + primaryPiece.getSize() - 1;
        
        // Horizontal primary piece (move left/right to exit)
        if (primaryPiece.isHorizontal()) {
            // Exit is to the right of the board
            if (exitX >= cols) {
                return exitX - primaryRight;
            } 
            // Exit is to the left of the board
            else if (exitX < 0) {
                return primaryLeft - exitX;
            }
            // Exit is aligned horizontally, but may not be aligned vertically
            else {
                return Math.abs(primaryTop - exitY);
            }
        } 
        // Vertical primary piece (move up/down to exit)
        else {
            // Exit is below the board
            if (exitY >= rows) {
                return exitY - primaryBottom; // Push to move down
            } 
            // Exit is above the board
            else if (exitY < 0) {
                return primaryTop - exitY; // Push to move up
            }
            // Exit is aligned vertically, but may not be aligned horizontally
            else {
                return Math.abs(primaryLeft - exitX);
            }
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
    public int getExitX() { return exitX; }
    public int getExitY() { return exitY; }
}
