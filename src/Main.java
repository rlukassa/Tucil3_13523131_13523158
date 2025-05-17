import java.util.* ;

class Main { 
    public static void main(String args[]) { 

        // Parsing file
        Utils check = new Utils() ; 
        System.out.print("Masukkan file dengan fomat txt sebaga konfigurasi : "); 
        Scanner txt = new Scanner(System.in);
        String fileName = txt.nextLine();
        Map<String, Object> boardData = Parsing.parseTestFile(fileName);
        if (boardData != null) {
            System.out.println("Parsed Board Data:");
            System.out.println("Rows: " + boardData.get("rows"));
            System.out.println("Cols: " + boardData.get("cols"));
            System.out.println("Number of Pieces: " + boardData.get("numPieces"));

            char[][] boardConfig = (char[][]) boardData.get("boardConfig");
            for (char[] row : boardConfig) {
                System.out.println(row);
            }
            check.setRunning(true); 
            
        } else {
            System.out.println("Gagal ngeparse file.");
            check.setRunning(false);
        }
        txt.close();


        // Menampilkan pilihan menu untuk pemilihan 
        // Algoritma dan Heurissstiknya 
        while(check.isRunning()) { 
            
            System.out.println("======================== Program Berjalan ========================");
            check.rangeChoices() ; 
            Scanner input = new Scanner(System.in);
            if (input.hasNextInt()) {
                check.setChoices(input.nextInt());
                System.out.println("Pilihan menu yang anda pilih adalah : " + check.getChoices());
                System.out.println("Program sedang maintenace, silahkan tunggu sebentar");
            } else {
                System.out.println("Input tidak valid. Harap masukkan angka.");
                check.setChoices(-1); // or handle as needed
            }
            input.close();
            break ; 
        }   
        
        // Transfer ke algoritma yang dipilih dan di proses di sana 
        // Output dari hasil prosess 
    }
}