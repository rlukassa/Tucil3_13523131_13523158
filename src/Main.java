package src;
import java.io.*;
import java.util.*;

public class Main {
    // ANSI colors and styles for CLI interface
    private static final String CYAN_BOLD = "\u001B[1;96m";
    private static final String GREEN_BOLD = "\u001B[1;92m";
    private static final String YELLOW_BOLD = "\u001B[1;93m";
    private static final String RED_BOLD = "\u001B[1;91m";
    private static final String BLUE_BG = "\u001B[44m";
    private static final String RESET = "\u001B[0m";
    
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        
        // Print welcome banner
        System.out.print("\033[H\033[2J");
        System.out.flush();
        System.out.println(CYAN_BOLD + 
            "╔═══════════════════════════════════════════════════════╗\n" +
            "║                                                       ║\n" +
            "║                     RUSH HOUR                         ║\n" +
            "║    UCS, Greedy, A*, Simulated Annealing Solver       ║\n" +
            "║                   Implementation                      ║\n" +
            "║                                                       ║\n" +
            "╚═══════════════════════════════════════════════════════╝" + RESET);
        
        String filename;
        GameBoard initialBoard = null;
        // Loop until a valid file is read
        do {
            System.out.println(YELLOW_BOLD + "\n► Masukkan nama file input " + RESET + "(contoh: test.txt): ");
            System.out.print(BLUE_BG + " ➤ " + RESET + " ");
            filename = scanner.nextLine();

            // Baca input dari file
            System.out.println(YELLOW_BOLD + "\n⌛ Membaca file input..." + RESET);
            initialBoard = readInput(filename);
            if (initialBoard == null) {
            System.out.println(RED_BOLD + "✗ Gagal membaca file input. Silakan coba lagi." + RESET);
            }
        } while (initialBoard == null);
        System.out.println(GREEN_BOLD + "✓ File berhasil dibaca!" + RESET);

        String algo;
        String heuristic = "Manhattan Distance"; // Default heuristic
        
        // Loop until a valid algorithm is chosen
        do {
            System.out.println(YELLOW_BOLD + "\n► Pilih algoritma " + RESET + "(UCS/Greedy/AStar/SimulatedAnnealing): ");
            System.out.print(BLUE_BG + " ➤ " + RESET + " ");
            algo = scanner.nextLine().trim();
            if (!(algo.equalsIgnoreCase("UCS") || algo.equalsIgnoreCase("Greedy") || 
                  algo.equalsIgnoreCase("AStar") || algo.equalsIgnoreCase("SimulatedAnnealing"))) {
                System.out.println(RED_BOLD + "✗ Algoritma tidak valid. Pilih UCS, Greedy, AStar, atau SimulatedAnnealing." + RESET);
                algo = null;
            }
        } while (algo == null);
        
        // Choose heuristic if algorithm uses it
        if (!algo.equalsIgnoreCase("UCS")) {
            System.out.println(YELLOW_BOLD + "\n► Pilih heuristik:" + RESET);
            
            if (algo.equalsIgnoreCase("Greedy") || algo.equalsIgnoreCase("AStar")) {
                System.out.println("1. Manhattan Distance (Admissible)");
                System.out.println("2. Manhattan + Blocking");
                System.out.println("3. Manhattan + Alignment");
                System.out.println("4. Enhanced Heuristic");
            } else if (algo.equalsIgnoreCase("SimulatedAnnealing")) {
                System.out.println("1. Manhattan Distance");
                System.out.println("2. Manhattan + Blocking");
                System.out.println("3. Manhattan + Alignment");
                System.out.println("4. Enhanced Heuristic");
                System.out.println("5. SA Custom Cost (Recommended)");
            }
            
            System.out.print(BLUE_BG + " ➤ " + RESET + " ");
            String heuristicChoice = scanner.nextLine().trim();
            
            switch (heuristicChoice) {
                case "1":
                    heuristic = "Manhattan Distance";
                    break;
                case "2":
                    heuristic = "Manhattan + Blocking";
                    break;
                case "3":
                    heuristic = "Manhattan + Alignment";
                    break;
                case "4":
                    heuristic = "Enhanced Heuristic";
                    break;
                case "5":
                    if (algo.equalsIgnoreCase("SimulatedAnnealing")) {
                        heuristic = "SA Custom Cost";
                    } else {
                        heuristic = "Manhattan Distance"; // fallback
                    }
                    break;
                default:
                    heuristic = "Manhattan Distance"; // default
                    break;
            }
            
            System.out.println(GREEN_BOLD + "✓ Heuristik dipilih: " + heuristic + RESET);
        }

        // Jalankan solver
        System.out.println(YELLOW_BOLD + "\n⌛ Mencari solusi menggunakan " + algo + 
                          (algo.equalsIgnoreCase("UCS") ? "" : " dengan " + heuristic) + "..." + RESET);
        
        Solver solver = new Solver(algo, heuristic);
        long startTime = System.currentTimeMillis();
        List<GameBoard> solution = solver.solve(initialBoard);
        long endTime = System.currentTimeMillis();
        
        // Tampilkan hasil
        if (solution != null) {
            System.out.println(GREEN_BOLD + "✓ Solusi ditemukan!" + RESET);
            
            // Print solution first
            printSolution(solution);
            
            // Then print statistics
            System.out.println(CYAN_BOLD + "┌───────────────────────────────────────┐");
            System.out.println("│  Statistik Pencarian:                 │");
            System.out.println("├───────────────────────────────────────┤");
            System.out.printf("│  + Algoritma       : %-16s │\n", algo);
            if (!algo.equalsIgnoreCase("UCS")) {
                System.out.printf("│  + Heuristik       : %-16s │\n", heuristic.length() > 16 ? 
                    heuristic.substring(0, 13) + "..." : heuristic);
            }
            System.out.printf("│  + Node dikunjungi : %-16d │\n", solver.getNodesVisited());
            System.out.printf("│  + Waktu eksekusi  : %-5d ms         │\n", (endTime - startTime));
            System.out.printf("│  + Jumlah langkah  : %-16d │\n", (solution.size() - 1));
            
            // Print additional statistics for Simulated Annealing
            if (algo.equalsIgnoreCase("SimulatedAnnealing")) {
                System.out.printf("│  + Gerakan diterima: %-16d │\n", solver.getAcceptedMoves());
                System.out.printf("│  + Gerakan ditolak : %-16d │\n", solver.getRejectedMoves());
                System.out.printf("│  + Temp akhir      : %-16.3f │\n", solver.getFinalTemperature());
            }
            System.out.println("└───────────────────────────────────────┘" + RESET);
            
            // Print performance analysis for Simulated Annealing
            if (algo.equalsIgnoreCase("SimulatedAnnealing")) {
                System.out.println(YELLOW_BOLD + "\n📊 Analisis Simulated Annealing:" + RESET);
                System.out.println("• SA menggunakan pendekatan probabilistik untuk eksplorasi state space");
                System.out.println("• Dapat menerima solusi yang lebih buruk dengan probabilitas tertentu");
                System.out.println("• Temperature yang menurun secara bertahap mengurangi eksplorasi random");
                System.out.println("• Efektif untuk menghindari local optimum dalam problem optimasi");
                
                double acceptanceRate = (double)solver.getAcceptedMoves() / (solver.getAcceptedMoves() + solver.getRejectedMoves()) * 100;
                System.out.printf("• Tingkat penerimaan gerakan: %.1f%%\n", acceptanceRate);
            }
        } else {
            System.out.println(RED_BOLD + "✗ Tidak ditemukan solusi." + RESET);
            if (algo.equalsIgnoreCase("SimulatedAnnealing")) {
                System.out.println(YELLOW_BOLD + "💡 Tips untuk Simulated Annealing:" + RESET);
                System.out.println("  • Coba tingkatkan initial temperature");
                System.out.println("  • Kurangi cooling rate untuk eksplorasi yang lebih lama");
                System.out.println("  • Tingkatkan max iterations");
                System.out.println("  • Problem ini mungkin memerlukan multiple restarts");
            }
        }
        scanner.close();
    }

    // Make readInput public static so GUI can access it
    public static GameBoard readInput(String filename) {
        try (BufferedReader br = new BufferedReader(new FileReader("test/" + filename))) {
            String[] dims = br.readLine().split(" ");
            int rows = Integer.parseInt(dims[0].trim()); 
            int cols = Integer.parseInt(dims[1].trim());
            /* Jumlah piece bukan primary - currently unused */
            br.readLine(); // Skip this line as we're not using the value

            int exitX = -1, exitY = -1;
            
            // CHECK FOR EXIT ABOVE THE GRID
            String topLine = br.readLine();
            if (topLine.contains("K")) {
                // Count leading spaces to determine column position
                int kPos = topLine.indexOf('K');
                exitX = kPos; // Column position where K is found
                exitY = -1;   // K is above the grid
                System.out.println(YELLOW_BOLD + "Pintu keluar (K) terdeteksi di atas grid pada kolom " + exitX + RESET);
                // Read a new line to start the grid proper
                topLine = br.readLine();
            }

            char[][] grid = new char[rows][cols];
            for (int i = 0; i < rows; i++) {
                String line;
                if (i == 0 && topLine != null) {
                    // Use topLine for the first row if we already read it
                    line = topLine;
                } else {
                    line = br.readLine();
                }
                line = line.trim();
                  // Check for left edge K first
                if (line.charAt(0) == 'K') {
                    exitX = -1;
                    exitY = i;
                    System.out.println(YELLOW_BOLD + "Pintu keluar (K) terdeteksi di kiri grid pada baris " + exitY + RESET);
                    // Process the rest of the line normally, but skip the K character
                    String restOfLine = line.substring(1); // Skip the K at the beginning
                    for (int j = 0; j < cols; j++) {
                        if (j < restOfLine.length()) {
                            grid[i][j] = restOfLine.charAt(j);
                        } else {
                            grid[i][j] = '.'; // Fill with empty if needed
                        }
                    }
                } else {
                    // Process regular line
                    for (int j = 0; j < cols; j++) {
                        grid[i][j] = line.charAt(j);
                    }
                }
                  // Check for characters beyond the declared columns (right edge exit)
                // Only do this check for regular lines (not those that had K at the beginning)
                if (line.charAt(0) != 'K') {
                    for (int j = cols; j < line.length(); j++) {
                        char ch = line.charAt(j);
                        if (ch == 'K') {
                            exitX = cols;
                            exitY = i;
                            System.out.println(YELLOW_BOLD + "Pintu keluar (K) terdeteksi di kanan grid pada baris " + exitY + RESET);
                        } else if (ch != ' ') {
                            System.out.println(RED_BOLD + "Input tidak valid: karakter '" + ch + "' di luar batas kolom." + RESET);
                            return null;
                        }
                    }
                }
            }
            
            // CHECK FOR EXIT BELOW THE GRID
            String bottomLine = br.readLine();
            if (bottomLine != null && bottomLine.contains("K")) {
                // Count leading spaces to determine column position
                int kPos = bottomLine.indexOf('K');
                exitX = kPos; // Column position where K is found
                exitY = rows; // K is below the grid
                System.out.println(YELLOW_BOLD + "Pintu keluar (K) terdeteksi di bawah grid pada kolom " + exitX + RESET);
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
            return null;
        }
    }

    // ANSI styling codes
    private static final String BOLD = "\u001B[1m";
    private static final String UNDERLINE = "\u001B[4m";
    
    private static void printSolution(List<GameBoard> solution) {
        
        System.out.println(BOLD + UNDERLINE + "Papan Awal:" + RESET);
        printBoard(solution.get(0));
        
        int totalSteps = solution.size() - 1;
        
        for (int i = 1; i < solution.size(); i++) {
            String moveInfo = findMoveInfo(solution.get(i-1), solution.get(i));
            String[] parts = moveInfo.split(" ke ");
            String piece = parts[0];
            String direction = parts[1];
            
            // Get color for this piece
            String pieceColor = (piece.equals("P")) ? PRIMARY_COLOR : 
                colorMap.computeIfAbsent(piece.charAt(0), key -> 
                    COLORS[Math.abs(key.hashCode()) % COLORS.length]);
            
            System.out.println(BOLD + "┌─────────────────────┐");
            System.out.printf("│ Langkah %2d dari %2d  │\n", i, totalSteps);
            System.out.print("└─────────────────────┘" + RESET + "\n");
            
            System.out.print("Gerakan: " + pieceColor + " " + piece + " " + RESET);
            System.out.println("ke " + BOLD + direction + RESET);
            
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
                
                return pieceId + "  ke " + direction;
            }
        }
        return "Tidak ada gerakan";
    }

    // ANSI color codes
    private static final String[] COLORS = {
        "\u001B[31m", // Red
        "\u001B[32m", // Green
        "\u001B[33m", // Yellow
        "\u001B[34m", // Blue
        "\u001B[35m", // Magenta
        "\u001B[36m", // Cyan
        "\u001B[91m", // Bright Red
        "\u001B[92m", // Bright Green
        "\u001B[93m", // Bright Yellow
        "\u001B[94m", // Bright Blue
        "\u001B[95m", // Bright Magenta
        "\u001B[96m"  // Bright Cyan
    };
    
    private static final String PRIMARY_COLOR = "\u001B[97;42m"; // White on green background
    private static final String EXIT_COLOR = "\u001B[97;42m";    // White on green background
    private static final String EMPTY_COLOR = "\u001B[90m";      // Dark gray
    
    private static final Map<Character, String> colorMap = new HashMap<>();
    
    private static void printBoard(GameBoard board) {
        char[][] grid = board.getGrid();
        int rows = grid.length;
        int cols = grid[0].length;
        
        // Print top border
        System.out.print("┌");
        for (int j = 0; j < cols; j++) {
            System.out.print("───");
        }
        System.out.println("┐");
        
        // Print grid with colors
        for (int i = 0; i < rows; i++) {
            System.out.print("│");
            for (int j = 0; j < cols; j++) {
                char c = grid[i][j];
                if (c == '.') {
                    System.out.print(EMPTY_COLOR + " · " + RESET);
                } else if (c == 'P') {
                    System.out.print(PRIMARY_COLOR + " P " + RESET);
                } else if (c == 'K') {
                    System.out.print(EXIT_COLOR + " K " + RESET);
                } else {
                    // Get or assign a color for this character
                    String color = colorMap.computeIfAbsent(c, key -> 
                        COLORS[Math.abs(key.hashCode()) % COLORS.length]);
                    
                    System.out.print(color + " " + c + " " + RESET);
                }
            }
            System.out.println("│");
        }
        
        // Print bottom border
        System.out.print("└");
        for (int j = 0; j < cols; j++) {
            System.out.print("───");
        }
        System.out.println("┘");
        System.out.println();
    }
}