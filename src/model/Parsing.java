import java.util.*;
import java.io.File;
import java.io.FileNotFoundException;

public class Parsing {
    public static Map<String, Object> parseTestFile(String fileName) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            File file = new File("test/" + fileName);
            Scanner scanner = new Scanner(file);
            
            // Read board dimensions
            String[] dimensions = scanner.nextLine().split(" ");
            // maksud dari nextLine() adalah untuk membaca baris pertama dari file yang berisi dimensi papan , contoh "3 4"
            // split(" ") digunakan untuk memisahkan string berdasarkan spasi
            // sehingga kita mendapatkan array string yang berisi dua elemen, yaitu jumlah baris dan kolom
            // misalnya, jika baris pertama adalah "3 4", maka dimensions[0] = "3" dan dimensions[1] = "4"
            int rows = Integer.parseInt(dimensions[0]); // ambil jumlah baris
            int cols = Integer.parseInt(dimensions[1]); // ambil jumlah kolom

            // apabila rows dan cols 
            
            // Baca jumlah piece nya 
            int numPieces = Integer.parseInt(scanner.nextLine().trim());
            // nextLine() digunakan untuk membaca baris kedua dari file yang berisi jumlah piece
            // trim() digunakan untuk menghapus spasi di awal dan akhir string

            char[][] boardConfig = new char[rows][cols];
            for (int i = 0; i < rows; i++) {
                String line = scanner.nextLine(); 
                for (int j = 0; j < cols; j++) {
                    if (j < line.length()) {
                        boardConfig[i][j] = line.charAt(j);
                    } else {
                        boardConfig[i][j] = '.';
                    }
                }
            }
            
            scanner.close();
              // Store parsed data in the result map
            result.put("rows", rows);
            result.put("cols", cols);
            result.put("numPieces", numPieces);
            result.put("boardConfig", boardConfig);
            
            return result;
            
        } catch (FileNotFoundException e) {
            System.out.println("File tidak ditemukan : " + fileName);
            e.printStackTrace();
            return null;
        }
    }
}
