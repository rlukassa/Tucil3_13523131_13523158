
import java.util.*;

import components.GameBoard;
import utils.*;

public class Main {
    public static final String CYAN_BOLD = "\u001B[1;96m";
    public static final String GREEN_BOLD = "\u001B[1;92m";
    public static final String YELLOW_BOLD = "\u001B[1;93m";
    public static final String RED_BOLD = "\u001B[1;91m";
    public static final String BLUE_BG = "\u001B[44m";
    public static final String RESET = "\u001B[0m";
    
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
        do {
            System.out.println(YELLOW_BOLD + "\n► Masukkan nama file input " + RESET + "(contoh: test.txt): ");
            System.out.print(BLUE_BG + " ➤ " + RESET + " ");
            filename = scanner.nextLine();

            // Baca input dari file
            System.out.println(YELLOW_BOLD + "\n⌛ Membaca file input..." + RESET);
            initialBoard = FileUtils.readFile(filename);
            if (initialBoard == null) {
            System.out.println(RED_BOLD + "✗ Gagal membaca file input. Silakan coba lagi." + RESET);
            }
        } while (initialBoard == null);
        System.out.println(GREEN_BOLD + "✓ File berhasil dibaca!" + RESET);

        String algo;
        String heuristic = "Manhattan Distance"; // heuristik default
        
        do {
            System.out.println(YELLOW_BOLD + "\n► Pilih algoritma " + RESET + "(UCS/Greedy/AStar/SimulatedAnnealing/IDS): ");
            System.out.print(BLUE_BG + " ➤ " + RESET + " ");
            algo = scanner.nextLine().trim();
            if (!(algo.equalsIgnoreCase("UCS") || algo.equalsIgnoreCase("Greedy") || 
                  algo.equalsIgnoreCase("AStar") || algo.equalsIgnoreCase("SimulatedAnnealing") || algo.equalsIgnoreCase("IDS"))) {
                System.out.println(RED_BOLD + "✗ Algoritma tidak valid. Pilih UCS, Greedy, AStar, SimulatedAnnealing, atau IDS." + RESET);
                algo = null;
            }
        } while (algo == null);
        
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
                        heuristic = "Manhattan Distance"; 
                    }
                    break;
                default:
                    heuristic = "Manhattan Distance"; 
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

            System.out.println(YELLOW_BOLD + "\nApakah Anda ingin menyimpan solusi ke file? (y/n): " + RESET);
            String saveChoice = scanner.nextLine().trim().toLowerCase();
            if (saveChoice.equals("y") || saveChoice.equals("yes")) {
                System.out.println(YELLOW_BOLD + "Masukkan nama file output (contoh: hasil.txt): " + RESET);
                String outputFilename = scanner.nextLine().trim();
                FileUtils.printSolutionToFile(solution, outputFilename);
                System.out.println(GREEN_BOLD + "✓ Solusi berhasil disimpan ke " + outputFilename + RESET);
            }
            
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
            
            if (algo.equalsIgnoreCase("SimulatedAnnealing")) {
                System.out.printf("│  + Gerakan diterima: %-16d │\n", solver.getAcceptedMoves());
                System.out.printf("│  + Gerakan ditolak : %-16d │\n", solver.getRejectedMoves());
                System.out.printf("│  + Temp akhir      : %-16.3f │\n", solver.getFinalTemperature());
            }
            System.out.println("└───────────────────────────────────────┘" + RESET);

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

    private static final String BOLD = "\u001B[1m";
    private static final String UNDERLINE = "\u001B[4m";
    
    private static void printSolution(List<GameBoard> solution) {
        
        System.out.println(BOLD + UNDERLINE + "Papan Awal:" + RESET);
        printBoard(solution.get(0));
        
        int totalSteps = solution.size() - 1;
        
        for (int i = 1; i < solution.size(); i++) {
            String moveInfo = FileUtils.findMoveInfo(solution.get(i-1), solution.get(i));
            String[] parts = moveInfo.split(" ke ");
            String piece = parts[0];
            String direction = parts[1];

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

        System.out.print("┌");
        for (int j = 0; j < cols; j++) {
            System.out.print("───");
        }
        System.out.println("┐");

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
                    String color = colorMap.computeIfAbsent(c, key -> 
                        COLORS[Math.abs(key.hashCode()) % COLORS.length]);
                    
                    System.out.print(color + " " + c + " " + RESET);
                }
            }
            System.out.println("│");
        }

        System.out.print("└");
        for (int j = 0; j < cols; j++) {
            System.out.print("───");
        }
        System.out.println("┘");
        System.out.println();
    }
}