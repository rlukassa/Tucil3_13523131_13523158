package src;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class GUI {
    // Buat warna dan font untuk tema terang dan gelap
    private final Color LIGHT_BG = new Color(245, 245, 250);
    private final Color LIGHT_PANEL = new Color(230, 240, 255);
    private final Color LIGHT_ACCENT = new Color(60, 120, 200); 
    
    private final Color DARK_BG = new Color(40, 44, 52);
    private final Color DARK_PANEL = new Color(50, 55, 65);
    private final Color DARK_ACCENT = new Color(97, 175, 239);
    
    private final Color PRIMARY_PIECE_LIGHT = new Color(255, 215, 0);
    private final Color PRIMARY_PIECE_DARK = new Color(255, 200, 0);
    
    private final Font TITLE_FONT = new Font("Segoe UI", Font.BOLD, 16);
    private final Font REGULAR_FONT = new Font("Segoe UI", Font.PLAIN, 13);
    private final Font BOLD_FONT = new Font("Segoe UI", Font.BOLD, 13);
    
    private JFrame frame; // untuk frame utama
    private JPanel mainPanel; // untuk panel 
    private JPanel boardContainerPanel; // papan permainan
    private JLayeredPane boardPanel; // panel berlapis untuk papan
    private JPanel controlPanel; // panel kontrol
    private JPanel statsPanel; // panel statistik
    private JPanel navigationPanel; // panel navigasi
    private JPanel movesPanel; // panel histori gerakan

    private JTextField fileField; // untuk menampilkan nama fielny
    private JButton browseButton; // buat search fileny
    private JComboBox<String> algoBox; // pilih algoritma dalam combo box
    private JButton solveButton; // untuk tombol cari solusi
    private JButton prevButton; // untuk tombol sebelumnya
    private JButton nextButton; // untuk tombol selanjutnya
    private JToggleButton playToggle; // untuk autoplay 
    private JSlider speedSlider; // untuk mengatur kecepatan animasi
    private JLabel statusLabel; // untuk menampilkan status dari aplikasi, keluarannya yang hassil ny
    private JTextArea statsTextArea; // untuk menampilkan statistik (Yaang Algoritma Apa, node yang dikunjungi, waktu eksekusi, jumlah langkah)
    private JLabel moveInfoLabel; // untuk menampilkan informasi gerakan
    private JScrollPane movesScrollPane; 
    private JList<String> movesList; // untuk menampilkan histori gerakan
    private DefaultListModel<String> movesListModel; 
    private JToggleButton themeToggle; // toggle tema gelap/terang
    private JProgressBar progressBar; // progress bar untuk menampilkan status pencarian solusi
    
    private boolean isDarkTheme = false; // awalnya bakal terang
    private Timer animationTimer; // timer untuk animasi
    private Map<Character, JLabel> pieceLabels = new HashMap<>(); // untuk menyimpan label dari setiap piece
    private GameBoard currentBoard; // papan permainan saat ini
    private GameBoard nextBoard; // papan permainan selanjutnya
    private char movingPieceId; // id dari piece yang bergerak
    private String moveDirection; // arah gerakan piece
    private int animationStep; // langkah animasi
    private final int ANIMATION_STEPS = 5; 
    
    private List<GameBoard> solutionSteps; // langkah-langkah solusi
    private int currentStep; // langkah saat ini
    private Solver solver; // solver untuk mencari solusi
    private long execTime; // waktu eksekusi solver

    public GUI() {
        initializeUI();
        setupListeners();
        applyTheme();
    }
    
    private void initializeUI() {
        frame = new JFrame("Rush Hour Solver"); // judul gui
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); 
        frame.setSize(950, 650); 
        frame.setLocationRelativeTo(null); // set posisi di tengah layar
        
        mainPanel = new JPanel(new BorderLayout(10, 10)); // ukuran panel 
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15)); // cuman ukuran border
        
        controlPanel = createControlPanel(); // buat panel kontrol, construct 
        mainPanel.add(controlPanel, BorderLayout.NORTH); // tambahkan panel kontrol ke bagian atas
        
        JPanel centerPanel = new JPanel(new BorderLayout(10, 0)); 
        
        boardContainerPanel = new JPanel(new BorderLayout());
        boardContainerPanel.setBorder(createRoundedTitledBorder("Papan Permainan")); // construct papan permainan (bukan board asli)
        
        boardPanel = new JLayeredPane();
        boardContainerPanel.add(boardPanel, BorderLayout.CENTER);
        centerPanel.add(boardContainerPanel, BorderLayout.CENTER); // papan asli
        
        movesPanel = new JPanel(new BorderLayout(5, 5)); // hgap artinya jarak horizontal, vgap artinya jarak vertikal
        movesPanel.setBorder(createRoundedTitledBorder("Histori Gerakan")); // construct histori gerakan
        
        moveInfoLabel = new JLabel("Belum ada gerakan"); // awalanya bakal defafult constrct bahwa belum ada gerakan
        moveInfoLabel.setFont(BOLD_FONT);  
        moveInfoLabel.setBorder(BorderFactory.createEmptyBorder(0, 5, 5, 0)); //
        movesPanel.add(moveInfoLabel, BorderLayout.NORTH); 
        
        movesListModel = new DefaultListModel<>(); // langkah geraknya
        movesList = new JList<>(movesListModel);
        movesList.setFont(REGULAR_FONT);
        movesList.setCellRenderer(new MoveCellRenderer());
        
        movesScrollPane = new JScrollPane(movesList); // agar bisa discroll
        movesScrollPane.setPreferredSize(new Dimension(220, 300));
        movesPanel.add(movesScrollPane, BorderLayout.CENTER);
        
        centerPanel.add(movesPanel, BorderLayout.EAST); 
        mainPanel.add(centerPanel, BorderLayout.CENTER);
        
        JPanel bottomPanel = new JPanel(new BorderLayout(10, 10));
        
        // Navigation panel
        navigationPanel = createNavigationPanel(); // navigasi panel
        bottomPanel.add(navigationPanel, BorderLayout.NORTH);
        
        // Stats panel
        statsPanel = new JPanel(new BorderLayout());  // stats panel nya, yang algoritma apa, node yang dikunjungi, waktu eksekusi, jumlah langkah
        statsPanel.setBorder(createRoundedTitledBorder("Statistik"));
        
        statsTextArea = new JTextArea(3, 20);
        statsTextArea.setEditable(false);
        statsTextArea.setFont(REGULAR_FONT);
        statsTextArea.setLineWrap(true);
        statsTextArea.setWrapStyleWord(true);
        statsTextArea.setMargin(new Insets(5, 5, 25, 5)); // atur atur lah ini
        statsPanel.add(new JScrollPane(statsTextArea), BorderLayout.CENTER);
        
        bottomPanel.add(statsPanel, BorderLayout.CENTER);
        
        // Status bar
        statusLabel = new JLabel("Silakan pilih file input dan algoritma");
        statusLabel.setFont(REGULAR_FONT);
        statusLabel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        bottomPanel.add(statusLabel, BorderLayout.SOUTH);  
        
        // Progress bar 
        progressBar = new JProgressBar(0, 100);
        progressBar.setStringPainted(true);
        progressBar.setString("0%"); // awalnya disset ke 0%
        progressBar.setValue(0); 
        progressBar.setVisible(false);
        bottomPanel.add(progressBar, BorderLayout.PAGE_END);
        
        mainPanel.add(bottomPanel, BorderLayout.SOUTH);
        
        frame.add(mainPanel);
        frame.setVisible(true);
    }
    
    private JPanel createControlPanel() { // buat panel kontrol
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        
        // Title and theme toggle
        JPanel titlePanel = new JPanel(new BorderLayout());
        JLabel titleLabel = new JLabel("Rush Hour Solver");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        titlePanel.add(titleLabel, BorderLayout.WEST);
        
        themeToggle = new JToggleButton("X");  // toggle buat temany
        themeToggle.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        themeToggle.setToolTipText("Toggle Dark/Light Theme");
        themeToggle.setBorderPainted(false);
        themeToggle.setContentAreaFilled(false);
        themeToggle.setFocusPainted(false);
        titlePanel.add(themeToggle, BorderLayout.EAST);
        
        panel.add(titlePanel);
        panel.add(Box.createVerticalStrut(10));
        
        JPanel inputPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        
        fileField = new JTextField(15);
        fileField.setEditable(false);
        
        browseButton = new JButton("Pilih File");
        browseButton.setFont(REGULAR_FONT); 
        browseButton.setFocusPainted(false);
        
        algoBox = new JComboBox<>(new String[]{"UCS", "Greedy", "AStar"}); // bakal ditambah algoritma lain 
        algoBox.setFont(REGULAR_FONT);

        // Masukkan logika heurisstik disini... (milih heurisstiknya maksudn ya)
        
        solveButton = new JButton("Cari Solusi");
        solveButton.setFont(BOLD_FONT);
        solveButton.setFocusPainted(false);
        
        inputPanel.add(new JLabel("File Input:"));
        inputPanel.add(fileField); // input file 
        inputPanel.add(browseButton);
        inputPanel.add(new JLabel("Algoritma:"));
        inputPanel.add(algoBox);
        inputPanel.add(solveButton);
        
        panel.add(inputPanel);
        return panel; // return panel kontrolnya 
    }
    
    private JPanel createNavigationPanel() { // buat panel navigasi dari proses 
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        
        prevButton = new JButton("◀ Sebelumnya");
        prevButton.setFont(REGULAR_FONT);
        prevButton.setEnabled(false);
        prevButton.setFocusPainted(false);
        
        playToggle = new JToggleButton("▶ Auto Play");
        playToggle.setFont(REGULAR_FONT);
        playToggle.setEnabled(false);
        playToggle.setFocusPainted(false);
        
        nextButton = new JButton("Selanjutnya ▶");
        nextButton.setFont(REGULAR_FONT);
        nextButton.setEnabled(false);
        nextButton.setFocusPainted(false);
        
        speedSlider = new JSlider(JSlider.HORIZONTAL, 1, 10, 5);
        speedSlider.setPreferredSize(new Dimension(100, 30));
        speedSlider.setToolTipText("Animation Speed");
        speedSlider.setMajorTickSpacing(9);
        speedSlider.setPaintTicks(true);
        
        panel.add(prevButton);
        panel.add(playToggle);
        panel.add(nextButton);
        panel.add(new JLabel("Speed:"));
        panel.add(speedSlider);
        
        return panel;
    }
    
    private Border createRoundedTitledBorder(String title) { // buat border dan judul 
        TitledBorder titledBorder = BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(isDarkTheme ? DARK_ACCENT : LIGHT_ACCENT, 1, true), 
            title,
            TitledBorder.DEFAULT_JUSTIFICATION,
            TitledBorder.DEFAULT_POSITION,
            TITLE_FONT,
            isDarkTheme ? DARK_ACCENT : LIGHT_ACCENT
        );
        titledBorder.setTitleFont(TITLE_FONT);
        return new CompoundBorder(
            new EmptyBorder(5, 5, 5, 5),
            titledBorder
        );
    }
    
    private void setupListeners() { // buat listener, ibarat penghubung antara GUI sama logika
        browseButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) { chooseFile(); }
        });
        
        solveButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) { solveRushHour(); }
        });
        
        prevButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) { 
                animateToStep(currentStep - 1); 
            }
        });
        
        nextButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) { 
                animateToStep(currentStep + 1); 
            }
        });
        
        playToggle.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (playToggle.isSelected()) {
                    startAutoPlay();
                } else {
                    stopAutoPlay();
                }
            }
        });
          speedSlider.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent e) {
                if (!speedSlider.getValueIsAdjusting()) {
                    updateAnimationSpeed();
                }
            }
        });
        
        themeToggle.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                isDarkTheme = themeToggle.isSelected();
                applyTheme();
            }
        });
        
        // Keyboard navigation
        KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher(new KeyEventDispatcher() {
            public boolean dispatchKeyEvent(KeyEvent e) {
                if (e.getID() == KeyEvent.KEY_PRESSED) {
                    if (e.getKeyCode() == KeyEvent.VK_LEFT && prevButton.isEnabled()) {
                        animateToStep(currentStep - 1);
                        return true;
                    } else if (e.getKeyCode() == KeyEvent.VK_RIGHT && nextButton.isEnabled()) {
                        animateToStep(currentStep + 1);
                        return true;
                    } else if (e.getKeyCode() == KeyEvent.VK_SPACE) {
                        playToggle.doClick();
                        return true;
                    }
                }
                return false;
            }
        });
        
        movesList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && movesList.getSelectedIndex() != -1) {
                stopAutoPlay();
                int selectedIndex = movesList.getSelectedIndex();
                if (selectedIndex != currentStep) {
                    animateToStep(selectedIndex);
                }
            }
        });
    }
    
    private void chooseFile() { // maukkan file input 
        JFileChooser chooser = new JFileChooser("test");
        chooser.setDialogTitle("Pilih File Konfigurasi Rush Hour");
        chooser.setFileFilter(new javax.swing.filechooser.FileFilter() {
            public boolean accept(java.io.File f) {
                return f.isDirectory() || f.getName().toLowerCase().endsWith(".txt"); // kalo file text adalah .txt maka di accept
            }
            public String getDescription() { 
                return "Text Files (*.txt)";
            }
        });
        
        int res = chooser.showOpenDialog(frame);
        if (res == JFileChooser.APPROVE_OPTION) {
            fileField.setText(chooser.getSelectedFile().getName());
            resetUI();
        }
    }
    
    private void resetUI() { // reset UI ke awal
        movesListModel.clear();
        statsTextArea.setText("");
        moveInfoLabel.setText("Belum ada gerakan");
        prevButton.setEnabled(false);
        nextButton.setEnabled(false);
        playToggle.setEnabled(false);
        playToggle.setSelected(false);
        progressBar.setVisible(false);
        boardPanel.removeAll();
        currentBoard = null;
        solutionSteps = null;
        if (animationTimer != null && animationTimer.isRunning()) {
            animationTimer.stop();
        }
        boardPanel.repaint();
    }
    
    private void solveRushHour() { // cari solusi 
        String filename = fileField.getText();
        if (filename.isEmpty()) {
            showMessage("Pilih file input terlebih dahulu!");
            return;
        }
        
        resetUI();
        statusLabel.setText("Membaca file dan mencari solusi...");
        progressBar.setVisible(true);
        progressBar.setIndeterminate(true);
        
        SwingWorker<List<GameBoard>, Void> worker = new SwingWorker<List<GameBoard>, Void>() {
            @Override
            protected List<GameBoard> doInBackground() throws Exception {
                GameBoard initialBoard = Main.readInput(filename);
                if (initialBoard == null) {
                    return null;
                }
                
                String algo = (String) algoBox.getSelectedItem();
                solver = new Solver(algo);
                long start = System.currentTimeMillis();
                List<GameBoard> solution = solver.solve(initialBoard);
                execTime = System.currentTimeMillis() - start;
                return solution;
            }
            
            @Override
            protected void done() { // kalo udah sselessai 
                try {
                    solutionSteps = get();
                    progressBar.setIndeterminate(false);
                    if (solutionSteps == null) { // kalo ga ada solusinya
                        statusLabel.setText("Tidak ditemukan solusi atau file tidak valid.");
                        progressBar.setVisible(false);
                        return;
                    }
                    
                    updateStatistics(); // bakal update statistik
                    populateMovesList(); // bakal update histori gerakan
                    
                    statusLabel.setText("Solusi ditemukan! " + (solutionSteps.size() - 1) + " langkah.");
                    progressBar.setMaximum(solutionSteps.size() - 1);
                    progressBar.setValue(0);
                    progressBar.setString("0/" + (solutionSteps.size() - 1));
                    
                    currentStep = 0;
                    showStep(0, false);
                    
                    prevButton.setEnabled(false);
                    nextButton.setEnabled(solutionSteps.size() > 1);
                    playToggle.setEnabled(solutionSteps.size() > 1);
                    
                } catch (Exception ex) {
                    statusLabel.setText("Error: " + ex.getMessage());
                    progressBar.setVisible(false);
                }
            }
        };
        
        worker.execute();
    }
    
    private void updateStatistics() { // info tentang statistik
        StringBuilder sb = new StringBuilder();
        sb.append("Algoritma: ").append(algoBox.getSelectedItem()).append("\n");
        sb.append("Node dikunjungi: ").append(solver.getNodesVisited()).append("\n");
        sb.append("Waktu eksekusi: ").append(execTime).append(" ms\n");
        sb.append("Jumlah langkah: ").append(solutionSteps.size() - 1);
        
        statsTextArea.setText(sb.toString());
    }
    
    private void populateMovesList() { // isi histori gerakan
        movesListModel.clear();
        movesListModel.addElement("0. Posisi Awal");
        
        for (int i = 1; i < solutionSteps.size(); i++) {
            String moveInfo = findMoveInfo(solutionSteps.get(i-1), solutionSteps.get(i));
            movesListModel.addElement(i + ". " + moveInfo);
        }
    }
    
    private String findMoveInfo(GameBoard prev, GameBoard current) { // mencetak inffo gereakan 
        ArrayList<Piece> prevPieces = prev.getPieces();
        ArrayList<Piece> currentPieces = current.getPieces();
        
        for (int i = 0; i < prevPieces.size(); i++) {
            Piece prevPiece = prevPieces.get(i);
            Piece currPiece = currentPieces.get(i);
            
            if (prevPiece.getX() != currPiece.getX() || prevPiece.getY() != currPiece.getY()) {
                String direction = "";
                if (prevPiece.getX() < currPiece.getX()) direction = "kanan";
                else if (prevPiece.getX() > currPiece.getX()) direction = "kiri";
                else if (prevPiece.getY() < currPiece.getY()) direction = "bawah";
                else if (prevPiece.getY() > currPiece.getY()) direction = "atas";
                
                return "Piece " + prevPiece.getId() + " ke " + direction;
            }
        }
        return "Tidak ada gerakan";
    }
    
    private void showStep(int step, boolean updateList) {  // 
        if (solutionSteps == null || step < 0 || step >= solutionSteps.size()) return;
        
        currentStep = step;
        drawBoard(solutionSteps.get(step));
        
        prevButton.setEnabled(step > 0);
        nextButton.setEnabled(step < solutionSteps.size() - 1);
        
        progressBar.setValue(step);
        progressBar.setString(step + "/" + (solutionSteps.size() - 1));
        
        if (step > 0) {
            String moveInfo = findMoveInfo(solutionSteps.get(step-1), solutionSteps.get(step));
            moveInfoLabel.setText("Langkah " + step + ": " + moveInfo);
        } else {
            moveInfoLabel.setText("Posisi Awal");
        }
        
        if (updateList) {
            movesList.setSelectedIndex(step);
            movesList.ensureIndexIsVisible(step);
        }
    }
    
    private void animateToStep(int targetStep) { // animasi ke langkah tertentu
        if (solutionSteps == null || targetStep < 0 || targetStep >= solutionSteps.size() 
            || targetStep == currentStep) return;
        
        if (animationTimer != null && animationTimer.isRunning()) {
            animationTimer.stop();
        }
        
        currentBoard = solutionSteps.get(currentStep);
        nextBoard = solutionSteps.get(targetStep);
        
        ArrayList<Piece> currentPieces = currentBoard.getPieces();
        ArrayList<Piece> nextPieces = nextBoard.getPieces();
        
        for (int i = 0; i < currentPieces.size(); i++) {
            Piece currPiece = currentPieces.get(i);
            Piece nextPiece = nextPieces.get(i);
            
            if (currPiece.getX() != nextPiece.getX() || currPiece.getY() != nextPiece.getY()) {
                movingPieceId = currPiece.getId();
                
                if (currPiece.getX() < nextPiece.getX()) moveDirection = "right";
                else if (currPiece.getX() > nextPiece.getX()) moveDirection = "left";
                else if (currPiece.getY() < nextPiece.getY()) moveDirection = "down";
                else moveDirection = "up";
                
                break;
            }
        }
        
        // inisialisai animation
        animationStep = 0;
        drawBoard(currentBoard);
        
        // Update UI selama animation
        moveInfoLabel.setText("Animating: " + findMoveInfo(currentBoard, nextBoard));
        prevButton.setEnabled(false);
        nextButton.setEnabled(false);
        playToggle.setEnabled(false);
        
        int delay = 1000 / (speedSlider.getValue() * 2); // delay nya agar sesuai dengan speed slider
        animationTimer = new Timer(delay, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                animationStep++;
                
                if (animationStep < ANIMATION_STEPS) {
                    updatePiecePosition(movingPieceId, moveDirection, animationStep);
                } else {
                    // Animation complete
                    ((Timer)e.getSource()).stop();
                    currentStep = targetStep;
                    showStep(targetStep, true);
                    
                    prevButton.setEnabled(targetStep > 0);
                    nextButton.setEnabled(targetStep < solutionSteps.size() - 1);
                    playToggle.setEnabled(true);
                    
                    if (playToggle.isSelected() && targetStep < solutionSteps.size() - 1) {
                        Timer delayTimer = new Timer(delay * 3, new ActionListener() {
                            @Override
                            public void actionPerformed(ActionEvent e) {
                                ((Timer)e.getSource()).stop();
                                if (playToggle.isSelected()) {
                                    animateToStep(targetStep + 1);
                                }
                            }
                        });
                        delayTimer.setRepeats(false);
                        delayTimer.start();
                    } else if (targetStep >= solutionSteps.size() - 1 && playToggle.isSelected()) {
                        playToggle.setSelected(false);
                    }
                }
            }
        });
        
        animationTimer.setRepeats(true);
        animationTimer.start();
    }
    
    private void updatePiecePosition(char pieceId, String direction, int step) {
        JLabel pieceLabel = pieceLabels.get(pieceId); // update piece label
        if (pieceLabel == null) return;
        
        Rectangle bounds = pieceLabel.getBounds();
        int stepSize = 0;
        
        if (pieceId == 'P') {
            pieceLabel.setBackground(new Color(255, 220, 0));
        }
        
        switch (direction) {
            case "right":
                stepSize = bounds.width / ANIMATION_STEPS;
                bounds.x += stepSize;
                break;
            case "left":
                stepSize = bounds.width / ANIMATION_STEPS;
                bounds.x -= stepSize;
                break;
            case "down":
                stepSize = bounds.height / ANIMATION_STEPS;
                bounds.y += stepSize;
                break;
            case "up":
                stepSize = bounds.height / ANIMATION_STEPS;
                bounds.y -= stepSize;
                break;
        }
        
        pieceLabel.setBounds(bounds);
    }
    
    private void drawBoard(GameBoard board) { // menggambar papan permainan
        boardPanel.removeAll();
        pieceLabels.clear();
        
        char[][] grid = board.getGrid();
        int rows = grid.length;
        int cols = grid[0].length;
        
        int panelWidth = boardContainerPanel.getWidth() - 40;
        int panelHeight = boardContainerPanel.getHeight() - 40;
        
        int cellSize = Math.min(panelWidth / cols, panelHeight / rows);
        if (cellSize < 10) cellSize = 40; 
        
        int boardWidth = cellSize * cols;
        int boardHeight = cellSize * rows;
        
        JPanel gridPanel = new JPanel(new GridLayout(rows, cols));
        gridPanel.setBounds(20, 20, boardWidth, boardHeight);
        gridPanel.setBackground(isDarkTheme ? DARK_PANEL : LIGHT_PANEL);
        
        for (int i = 0; i < rows * cols; i++) {
            JPanel cell = new JPanel();
            cell.setBorder(BorderFactory.createLineBorder(isDarkTheme ? DARK_BG : Color.LIGHT_GRAY, 1));
            cell.setBackground(isDarkTheme ? new Color(60, 63, 73) : new Color(235, 235, 235));
            gridPanel.add(cell);
        }
        
        boardPanel.add(gridPanel, Integer.valueOf(0));
        
        ArrayList<Piece> pieces = board.getPieces();
        for (Piece piece : pieces) {
            int x = piece.getX();
            int y = piece.getY();
            int size = piece.getSize();
            boolean isHorizontal = piece.isHorizontal();
            boolean isPrimary = piece.isPrimary();
            
            JLabel pieceLabel = new JLabel(String.valueOf(piece.getId()), SwingConstants.CENTER);
            pieceLabel.setOpaque(true);
            pieceLabel.setBorder(BorderFactory.createRaisedBevelBorder());
            pieceLabel.setFont(new Font("Segoe UI", Font.BOLD, cellSize / 2));
            
            if (isPrimary) {
                pieceLabel.setBackground(isDarkTheme ? PRIMARY_PIECE_DARK : PRIMARY_PIECE_LIGHT);
                pieceLabel.setForeground(Color.BLACK);
            } else {
                int hue = (piece.getId() * 40) % 360; // warna piece 
                Color pieceColor = Color.getHSBColor(hue / 360f, 0.7f, isDarkTheme ? 0.7f : 0.85f);
                pieceLabel.setBackground(pieceColor);
                pieceLabel.setForeground(isDarkTheme ? Color.WHITE : Color.BLACK);
            }
            
            int width = isHorizontal ? cellSize * size : cellSize;
            int height = isHorizontal ? cellSize : cellSize * size;
            pieceLabel.setBounds(20 + x * cellSize, 20 + y * cellSize, width, height);
            
            pieceLabels.put(piece.getId(), pieceLabel);
            
            boardPanel.add(pieceLabel, Integer.valueOf(1));
        }
        
        // Mark exit location
        int exitX = board.getExitX();
        int exitY = board.getExitY();
        
        if (exitX >= 0 && exitX < cols && exitY >= 0 && exitY < rows) {
            JLabel exitLabel = new JLabel("EXIT", SwingConstants.CENTER);
            exitLabel.setOpaque(true);
            exitLabel.setBorder(BorderFactory.createLineBorder(Color.GREEN, 2));
            exitLabel.setBackground(new Color(60, 179, 113, 120));
            exitLabel.setForeground(isDarkTheme ? Color.WHITE : Color.BLACK);
            exitLabel.setFont(new Font("Arial", Font.BOLD, cellSize / 3));
            exitLabel.setBounds(20 + exitX * cellSize, 20 + exitY * cellSize, cellSize, cellSize);
            boardPanel.add(exitLabel, Integer.valueOf(2));
        }
        
        boardPanel.revalidate();
        boardPanel.repaint();
    }
    
    private void startAutoPlay() { // autoplay
        if (solutionSteps == null || currentStep >= solutionSteps.size() - 1) {
            playToggle.setSelected(false);
            return;
        }
        
        playToggle.setText("■ Stop");
        animateToStep(currentStep + 1);
    }
    
    private void stopAutoPlay() {
        playToggle.setText("▶ Auto Play");
        if (animationTimer != null && animationTimer.isRunning()) {
            animationTimer.stop();
        }
    }
    
    private void updateAnimationSpeed() {
        // Update animation speed if timer is running
        if (animationTimer != null && animationTimer.isRunning()) {
            animationTimer.setDelay(1000 / (speedSlider.getValue() * 2));
        }
    }
    
    private void applyTheme() { // 
        themeToggle.setText(isDarkTheme ? "X" : "O");
        themeToggle.setToolTipText(isDarkTheme ? "Switch to Light Theme" : "Switch to Dark Theme");
        
        mainPanel.setBackground(isDarkTheme ? DARK_BG : LIGHT_BG);
        controlPanel.setBackground(isDarkTheme ? DARK_PANEL : LIGHT_PANEL);
        statsPanel.setBackground(isDarkTheme ? DARK_PANEL : LIGHT_PANEL);
        boardContainerPanel.setBackground(isDarkTheme ? DARK_PANEL : LIGHT_PANEL);
        movesPanel.setBackground(isDarkTheme ? DARK_PANEL : LIGHT_PANEL);
        navigationPanel.setBackground(isDarkTheme ? DARK_BG : LIGHT_BG);
        
        Color textColor = isDarkTheme ? Color.WHITE : Color.BLACK;
        Color mutedTextColor = isDarkTheme ? new Color(180, 180, 180) : new Color(70, 70, 70);
        
        statusLabel.setForeground(mutedTextColor);
        moveInfoLabel.setForeground(textColor);
        statsTextArea.setForeground(textColor);
        statsTextArea.setBackground(isDarkTheme ? DARK_PANEL : LIGHT_PANEL);
        movesList.setForeground(textColor);
        movesList.setBackground(isDarkTheme ? DARK_PANEL : LIGHT_PANEL);
        
        boardContainerPanel.setBorder(createRoundedTitledBorder("Papan Permainan"));
        statsPanel.setBorder(createRoundedTitledBorder("Statistik"));
        movesPanel.setBorder(createRoundedTitledBorder("Histori Gerakan"));
        
        if (currentBoard != null) {
            drawBoard(currentBoard);
        }
        
        frame.repaint();
    }
    
    private void showMessage(String message) { // pesan 
        statusLabel.setText(message);
    }
    
    private class MoveCellRenderer extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value, 
                int index, boolean isSelected, boolean cellHasFocus) {
            
            JLabel label = (JLabel) super.getListCellRendererComponent(
                    list, value, index, isSelected, cellHasFocus);
            
            if (!isSelected) {
                label.setBackground(isDarkTheme ? DARK_PANEL : LIGHT_PANEL);
                label.setForeground(isDarkTheme ? Color.WHITE : Color.BLACK);
            }
            
            label.setBorder(BorderFactory.createEmptyBorder(3, 5, 3, 5));
            
            if (index == currentStep) {
                label.setText("➤ " + label.getText());
                if (!isSelected) {
                    label.setBackground(isDarkTheme ? new Color(70, 80, 90) : new Color(220, 230, 245));
                }
            }
            
            return label;
        }
    }    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new GUI();
            }
        });
    }
}