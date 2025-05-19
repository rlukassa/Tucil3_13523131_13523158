package src;
import java.util.ArrayList;

public class GameBoard {
    private int rows, cols;
    private char[][] grid;
    private ArrayList<Piece> pieces;
    private int exitX, exitY; // Koordinat pintu keluar
    private Piece primaryPiece;
    
    public GameBoard(int rows, int cols, ArrayList<Piece> pieces, int exitX, int exitY) {
        this.rows = rows; // constructor biasa 
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
            grid[exitY][exitX] = 'K'; // Pintu keluar, asumsi nya ketika K tepat di samping dari kotak dalam baris dan kolom board, maka sudah return keluar 
        }
    }

    // Mengecek apakah primary piece sudah di pintu keluar
    public boolean isGoal() {
        int primaryLeft = primaryPiece.getX(); // posisi paling kiri dari primary piece
        int primaryRight = primaryPiece.getX() + primaryPiece.getSize() - 1; // posisi paling kanan dari primary piece
        int primaryTop = primaryPiece.getY();
        int primaryBottom = primaryPiece.getY() + primaryPiece.getSize() - 1;
        
        if (exitX >= cols) {
            return primaryPiece.isHorizontal() && primaryRight == cols - 1;
        } 

        else if (exitX < 0) {
            return primaryPiece.isHorizontal() && primaryLeft == 0;
        } 

        else if (exitY < 0) {
            return !primaryPiece.isHorizontal() && primaryTop == 0;
        } 
        else if (exitY >= rows) {
            boolean atBottomEdge = !primaryPiece.isHorizontal() && primaryBottom == rows - 1;
            boolean correctColumn = primaryPiece.getX() == exitX;
            return atBottomEdge && correctColumn;
        }
    
        if (primaryPiece.isHorizontal()) {
            if (exitX > primaryRight && exitY == primaryTop) {
                return primaryRight == exitX - 1;
            }
            else if (exitX < primaryLeft && exitY == primaryTop) {
                return primaryLeft == exitX + 1;
            }
        }
        else {
            if (exitY > primaryBottom && exitX == primaryLeft) {
                return primaryBottom == exitY - 1;
            }
            else if (exitY < primaryTop && exitX == primaryLeft) {
                return primaryTop == exitY + 1;
            }
        }
        // kalo ga nemu yang diatas, berarti ga  bisa keluar
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

    // ORIGINAL HEURISTIC SYSTEM - udah dipindah dan disesuaikan, , ini manhattan distance
    /*
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
                // Strongly encourage moving down to the bottom exit
                return (rows - 1 - primaryBottom) * 2 + 1; // Prioritize moving down
            } 
            // Exit is above the board
            else if (exitY < 0) {
                // Encourage moving up to the top exit
                return primaryTop * 2 + 1; // Prioritize moving up
            }
            // Exit is aligned vertically, but may not be aligned horizontally
            else {
                return Math.abs(primaryLeft - exitX);
            }
        }
    }
    */

    // Heuristik terbaru ada tambahan disini 
    
    /**
     * 1. Manhattan Distance - Pure  heuristik admissible, jadi costnya ga lebih dari yang seharusnya
     * 
     */
    public int getManhattanDistance() {
        int primaryLeft = primaryPiece.getX();
        int primaryRight = primaryPiece.getX() + primaryPiece.getSize() - 1;
        int primaryTop = primaryPiece.getY();
        int primaryBottom = primaryPiece.getY() + primaryPiece.getSize() - 1;
        
        if (primaryPiece.isHorizontal()) {
            if (exitX >= cols) {
                return Math.max(0, exitX - primaryRight);
            } 
            else if (exitX < 0) {
                return Math.max(0, primaryLeft - exitX);
            }
            else {
                return Math.abs(primaryTop - exitY);
            }
        } else {
            if (exitY >= rows) {
                return Math.max(0, exitY - primaryBottom);
            } 
            else if (exitY < 0) {
                return Math.max(0, primaryTop - exitY);
            }
            else {
                return Math.abs(primaryLeft - exitX);
            }
        }
    }
    
    /**
     * 2. Manhattan Distance + Blocking Penalty, jadi kalo ada blocking piece di jalur primary piece ke pintu keluar, 
     * akan ada penalty
     */
    public int getManhattanWithBlocking() {
        int manhattanDist = getManhattanDistance();
        int blockingPenalty = calculateBlockingPenalty();
        return manhattanDist + blockingPenalty;
    }
    
    /**
     * 3. Manhattan Distance + Alignment Penalty, kalo primary piece ga sejajar dengan pintu keluar,
     * 
     */
    // public int getManhattanWithAlignment() {
    //     int manhattanDist = getManhattanDistance();
    //     int alignmentPenalty = calculateAlignmentPenalty();
    //     return manhattanDist + alignmentPenalty;
    // }
    
    /**
     * 4. Enhanced Heuristic (Kombinasi dari beberapa heuristik), jadi ada penalty blocking, 
     */ 
    public int getEnhancedHeuristic() {
        int manhattanDist = getManhattanDistance();
        int blockingPenalty = calculateBlockingPenalty();
        // int alignmentPenalty = calculateAlignmentPenalty();
        int mobilityPenalty = calculateMobilityPenalty();
        return manhattanDist + blockingPenalty  + mobilityPenalty; // belum ada alignment penalty
    }
    
    /**
     * 5. Custom SA Cost (Simulated Annealing specific), untuk menghitung cost untuk simulated annealing, 
     */
    public int getSimulatedAnnealingCost() {
        int basicHeuristic = getManhattanDistance();
        int blockingPenalty = calculateBlockingPenalty() * 2; // Double penalti, 2 karena blocking
        // int alignmentPenalty = calculateAlignmentPenalty() * 3; // Triple penalty
        int congestionPenalty = calculateCongestionPenalty();
        return basicHeuristic + blockingPenalty  + congestionPenalty;
    }
    
    // Method pembantu
    
    private int calculateBlockingPenalty() {
        int penalty = 0;
        char[][] grid = getGrid();
        
        if (primaryPiece.isHorizontal()) {
            // Cari jalur horizontal dari primary piece ke pintu keluar
            int y = primaryPiece.getY();
            int startX = primaryPiece.getX() + primaryPiece.getSize();
            int endX = (exitX >= cols) ? cols : exitX;
            
            for (int x = startX; x < endX && x < cols; x++) {
                if (grid[y][x] != '.' && grid[y][x] != 'K') {
                    penalty += 2; // tiap blocking piece menambah penalti
                }
            }
        } else {
            // kalo primary piece vertikal
            int x = primaryPiece.getX();
            int startY = primaryPiece.getY() + primaryPiece.getSize();
            int endY = (exitY >= rows) ? rows : exitY;
            
            for (int y = startY; y < endY && y < rows; y++) {
                if (grid[y][x] != '.' && grid[y][x] != 'K') {
                    penalty += 2; // tiap blocking piece menambah penalti
                }
            }
        }
        
        return penalty;
    }
    
    // private int calculateAlignmentPenalty() {
    //     int penalty = 0;
        
    //     if (primaryPiece.isHorizontal()) {
    //         penalty = Math.abs(primaryPiece.getY() - exitY);
    //     } else {
    //         penalty = Math.abs(primaryPiece.getX() - exitX);
    //     }
        
    //     return penalty;
    // }
    
    private int calculateMobilityPenalty() {
        int penalty = 0;
        
        for (Piece piece : pieces) {
            int possibleMoves = 0;
            
            if (piece.isHorizontal()) {
                if (piece.getX() > 0 && grid[piece.getY()][piece.getX() - 1] == '.') {
                    possibleMoves++;
                }
                if (piece.getX() + piece.getSize() < cols && 
                    grid[piece.getY()][piece.getX() + piece.getSize()] == '.') {
                    possibleMoves++;
                }
            } else {
                if (piece.getY() > 0 && grid[piece.getY() - 1][piece.getX()] == '.') {
                    possibleMoves++;
                }
                if (piece.getY() + piece.getSize() < rows && 
                    grid[piece.getY() + piece.getSize()][piece.getX()] == '.') {
                    possibleMoves++;
                }
            }
            
            if (possibleMoves == 0) penalty += 3;
            else if (possibleMoves == 1) penalty += 1;
        }
        
        return penalty;
    }
    
    private int calculateCongestionPenalty() { // penalti untuk congestion, congestion itu banyak piece yang ada di sekitar primary piece
        int penalty = 0;
        int congestionRadius = 2;
        
        int startX = Math.max(0, Math.min(exitX, primaryPiece.getX()) - congestionRadius);
        int endX = Math.min(cols - 1, Math.max(exitX, primaryPiece.getX() + primaryPiece.getSize()) + congestionRadius);
        int startY = Math.max(0, Math.min(exitY, primaryPiece.getY()) - congestionRadius);
        int endY = Math.min(rows - 1, Math.max(exitY, primaryPiece.getY() + primaryPiece.getSize()) + congestionRadius);
        
        for (int y = startY; y <= endY; y++) {
            for (int x = startX; x <= endX; x++) {
                if (grid[y][x] != '.' && grid[y][x] != 'K' && grid[y][x] != 'P') {
                    penalty += 1;
                }
            }
        }
        
        return penalty;
    }
    
    // Method untuk mendapatkan heuristik berdasarkan tipe heuristik yang diinginkan
    public int getHeuristic(String heuristicType) {
        switch (heuristicType) {
            case "Manhattan Distance":
                return getManhattanDistance();
            case "Manhattan + Blocking":
                return getManhattanWithBlocking();
            // case "Manhattan + Alignment":
            //     return getManhattanWithAlignment(); masih bingung
            case "Enhanced Heuristic":
                return getEnhancedHeuristic();
            case "SA Custom Cost":
                return getSimulatedAnnealingCost();
            default:
                return getManhattanDistance(); // Default fallback
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
    public int getRows() { return rows; }
    public int getCols() { return cols; }
}