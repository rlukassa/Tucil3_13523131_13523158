package utils;
import java.util.*;
import java.io.*;

import components.GameBoard;
import components.Piece;

public class FileUtils {
    public static GameBoard readFile(String filename) {
        try (BufferedReader br = new BufferedReader(new FileReader("test/" + filename))) { 
            String[] dims = br.readLine().split(" ");
            int rows = Integer.parseInt(dims[0].trim()); 
            int cols = Integer.parseInt(dims[1].trim());
            /* Jumlah piece bukan primary - currently unused */
            br.readLine(); 

            int exitX = -1, exitY = -1; //inisiasi koordinat K
            
            // Cek Exit
            String topLine = br.readLine();
            if (topLine.contains("K")) {
                int kPos = topLine.indexOf('K');
                exitX = kPos; 
                exitY = -1;   
                
                topLine = br.readLine();
            }

            char[][] grid = new char[rows][cols];
            for (int i = 0; i < rows; i++) {
                String line;
                if (i == 0 && topLine != null) {
                    line = topLine;
                } else {
                    line = br.readLine();
                }
                line = line.trim();
                if (line.charAt(0) == 'K') {
                    exitX = -1;
                    exitY = i;
                    String restOfLine = line.substring(1); 
                    for (int j = 0; j < cols; j++) {
                        if (j < restOfLine.length()) {
                            grid[i][j] = restOfLine.charAt(j);
                        } else {
                            grid[i][j] = '.'; 
                        }
                    }
                } else {
                    for (int j = 0; j < cols; j++) {
                        grid[i][j] = line.charAt(j);
                    }
                }
                if (line.charAt(0) != 'K') {
                    for (int j = cols; j < line.length(); j++) {
                        char ch = line.charAt(j);
                        if (ch == 'K') {
                            exitX = cols;
                            exitY = i;
                        } else if (ch != ' ') {
                            System.out.println("Input tidak valid: karakter '" + ch + "' di luar batas kolom.");
                            return null;
                        }
                    }
                }
            }
            
            String bottomLine = br.readLine();
            if (bottomLine != null && bottomLine.contains("K")) {
                int kPos = bottomLine.indexOf('K');
                exitX = kPos; 
                exitY = rows; 
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

    public static void printSolutionToFile(List<GameBoard> solution, String filename) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(filename))) {
            writer.println("Papan Awal:");
            printBoardToWriter(solution.get(0), writer);

            for (int i = 1; i < solution.size(); i++) {
                String moveInfo = findMoveInfo(solution.get(i-1), solution.get(i));
                String[] parts = moveInfo.split(" ke ");
                String piece = parts[0];
                String direction = parts[1];

                writer.printf("│ Langkah %2d dari %2d %n", i, solution.size() - 1);
                writer.print("│ Gerakan: " + piece + " ");
                writer.println("ke " + direction);

                printBoardToWriter(solution.get(i), writer);
            }
        } catch (IOException e) {
            System.err.println("Error writing solution to file: " + e.getMessage());
        }
    }

    private static void printBoardToWriter(GameBoard board, PrintWriter writer) {
        char[][] grid = board.getGrid();
        int rows = grid.length;
        int cols = grid[0].length;

        for (int i = 0; i < cols; i++){
            writer.print("───");
        }
        writer.println();

        int exitX = board.getExitX();
        int exitY = board.getExitY();

        if (exitY == -1 && exitX >= 0 && exitX < cols) {
            // Atas
            for(int i = 0; i < cols; i++ ){
                if(!(i == exitX)){
                    writer.print(" ");
                }
                else{
                    writer.print("K");
                }
            }

            for (int i = 0; i < rows; i++) {
                for (int j = 0; j < cols; j++) {
                    char c = grid[i][j];
                    if (c == '.') {
                        writer.print(" · ");
                    } else if (c == 'P') {
                        writer.print(" P ");
                    } else {
                        writer.print(" " + c + " ");
                    }
                }
                writer.println();
            }

        } else if (exitY == rows && exitX >= 0 && exitX < cols) {
            // Bawah
            for (int i = 0; i < rows; i++) {
                for (int j = 0; j < cols; j++) {
                    char c = grid[i][j];
                    if (c == '.') {
                        writer.print(" · ");
                    } else if (c == 'P') {
                        writer.print(" P ");
                    } else {
                        writer.print(" " + c + " ");
                    }
                }
                writer.println();
            }
            
            for(int i = 0; i < cols; i++ ){
                if(!(i == exitX)){
                    writer.print(" ");
                }
                else{
                    writer.print("K");
                }
            }

        } else if (exitX == -1 && exitY >= 0 && exitY < rows) {
            // Kiri
            for (int i = 0; i < rows; i++) {

                if(!(i == exitY)){
                    writer.print(" ");
                }
                else{
                    writer.print("K");
                }

                for (int j = 0; j < cols; j++) {
                    char c = grid[i][j];
                    if (c == '.') {
                        writer.print(" · ");
                    } else if (c == 'P') {
                        writer.print(" P ");
                    } else {
                        writer.print(" " + c + " ");
                    }
                }
                writer.println();
            }
        } else if (exitX == cols && exitY >= 0 && exitY < rows) {
            for (int i = 0; i < rows; i++) {
                for (int j = 0; j < cols; j++) {
                    char c = grid[i][j];
                    if (c == '.') {
                        writer.print(" · ");
                    } else if (c == 'P') {
                        writer.print(" P ");
                    } else {
                        writer.print(" " + c + " ");
                    }
                }
                if(i == exitY){
                    writer.print("K");
                }
                writer.println();
            }
        }
        
        for (int i = 0; i < cols; i++){
            writer.print("───");
        }
        writer.println();
    }

    public static String findMoveInfo(GameBoard prev, GameBoard current) {
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
}
