package src;
import java.io.*;
import java.util.*;
// import java.io.File ; 


public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Masukkan nama file input (contoh: input.txt): ");
        String filename = scanner.nextLine();
    

        // Baca input dari file
        GameBoard initialBoard = readInput(filename);
        if (initialBoard == null) {
            System.out.println("Gagal membaca file input.");
            scanner.close();
            return;
        }
        System.out.println("Pilih algoritma (UCS/Greedy/AStar): ");
        String algo = scanner.nextLine();

        // Jalankan solver
        Solver solver = new Solver(algo);
        long startTime = System.currentTimeMillis();
        List<GameBoard> solution = solver.solve(initialBoard);
        long endTime = System.currentTimeMillis();

        // Tampilkan hasil
        if (solution != null) {
            System.out.println("Solusi ditemukan!");
            System.out.println("Jumlah node yang dikunjungi: " + solver.getNodesVisited());
            System.out.println("Waktu eksekusi: " + (endTime - startTime) + " ms");
            printSolution(solution);
        } else {
            System.out.println("Tidak ditemukan solusi.");
        }
        scanner.close();
    }    private static GameBoard readInput(String filename) {
        try (BufferedReader br = new BufferedReader(new FileReader("test/" + filename))) {
            String[] dims = br.readLine().split(" ");
            int rows = Integer.parseInt(dims[0].trim()); 
            System.out.println("rows: " + rows); // debug ; 
            int cols = Integer.parseInt(dims[1].trim());
            System.out.println("cols: " + cols); // debug ;
            int n = Integer.parseInt(br.readLine().trim()); // Jumlah piece bukan primary
            System.out.println("n: " + n); // debug ;

            int exitX = -1, exitY = -1;
            char[][] grid = new char[rows][cols];
            for (int i = 0; i < rows; i++) {
                String line = br.readLine();
                // debug ; 
                System.out.println("line: " + line); // debug ;
                line = line.trim();
                for (int j = 0; j < cols; j++) {
                    grid[i][j] = line.charAt(j);
                }
                // Check for characters beyond the declared columns
                for (int j = cols; j < line.length(); j++) {
                    char ch = line.charAt(j);
                    if (ch == 'K') {
                        exitX = j;
                        exitY = i;
                    } else {
                        System.out.println("Input tidak valid: karakter '" + ch + "' di luar batas kolom.");
                        return null;
                    }
                }
            }

            // Proses grid untuk membuat daftar piece
            ArrayList<Piece> pieces = new ArrayList<>();
            Set<Character> processed = new HashSet<>();

            for (int i = 0; i < rows; i++) {
                for (int j = 0; j < cols; j++) {
                    char c = grid[i][j];
                    if (c != '.' && c != 'K' && !processed.contains(c)) {
                        processed.add(c);
                        // Tentukan ukuran dan orientasi
                        int size = 1;
                        boolean isHorizontal = false;
                        if (j + 1 < cols && grid[i][j + 1] == c) {
                            isHorizontal = true;
                            while (j + size < cols && grid[i][j + size] == c) size++;
                        } else if (i + 1 < rows && grid[i + 1][j] == c) {
                            while (i + size < rows && grid[i + size][j] == c) size++;
                        }
                        boolean isPrimary = (c == 'P');
                        pieces.add(new Piece(c, j, i, size, isHorizontal, isPrimary));
                    } else if (c == 'K') {
                        exitX = j;
                        exitY = i;
                    }
                }
            }
            return new GameBoard(rows, cols, pieces, exitX, exitY);

        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }    private static void printSolution(List<GameBoard> solution) {
        System.out.println("Papan Awal");
        printBoard(solution.get(0));
        for (int i = 1; i < solution.size(); i++) {
            String moveInfo = findMoveInfo(solution.get(i-1), solution.get(i));
            System.out.println("Gerakan " + i + ": " + moveInfo);
            printBoard(solution.get(i));
        }
    }
    
    private static String findMoveInfo(GameBoard prev, GameBoard current) {
        ArrayList<Piece> prevPieces = prev.getPieces();
        ArrayList<Piece> currentPieces = current.getPieces();
        
        for (int i = 0; i < prevPieces.size(); i++) {
            Piece prevPiece = prevPieces.get(i);
            Piece currPiece = currentPieces.get(i);
            
            if (prevPiece.getX() != currPiece.getX() || prevPiece.getY() != currPiece.getY()) {
                char pieceId = prevPiece.getId();
                String direction;
                
                if (currPiece.getX() > prevPiece.getX()) {
                    direction = "kanan";
                } else if (currPiece.getX() < prevPiece.getX()) {
                    direction = "kiri";
                } else if (currPiece.getY() > prevPiece.getY()) {
                    direction = "bawah";
                } else {
                    direction = "atas";
                }
                
                return pieceId + " ke " + direction;
            }
        }
        return "Tidak ada gerakan";
    }

    private static void printBoard(GameBoard board) {
        char[][] grid = board.getGrid();
        for (char[] row : grid) {
            for (char c : row) {
                // Tambahkan warna sederhana (hanya teks, bisa diperluas dengan library ANSI)
                if (c == 'P') {
                    System.out.print("[P]");
                } else if (c == 'K') {
                    System.out.print("[K]");
                } else {
                    System.out.print(" " + c + " ");
                }
            }
            System.out.println();
        }
        System.out.println();
    }
}
