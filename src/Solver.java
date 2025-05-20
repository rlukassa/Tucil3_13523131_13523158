package src;

import java.util.*;

public class Solver {
    private String algorithm;
    private String heuristic;
    private int nodesVisited;
    private Random random = new Random();
    
    // Statistik tambahan untuk Simulated Annealing
    private int acceptedMoves = 0;
    private int rejectedMoves = 0;
    private double finalTemperature = 0.0;
    private double temperature = 0.0; // Untuk Simulated Annealing
    
    public Solver(String algorithm, String heuristic) {
        this.algorithm = algorithm;
        this.heuristic = heuristic;
        this.nodesVisited = 0;
    }

    public Solver(String algorithm) {
        this.algorithm = algorithm;
        this.nodesVisited = 0;
    }

    public List<GameBoard> solve(GameBoard initialBoard) {
        if (algorithm.equals("UCS")) {
            return ucs(initialBoard);
        } else if (algorithm.equals("Greedy")) {
            return greedyBestFirstSearch(initialBoard);
        } else if (algorithm.equals("AStar")) {
            return aStar(initialBoard);
        } else if (algorithm.equals("SimulatedAnnealing")) {
            return simulatedAnnealing(initialBoard);
        } else if (algorithm.equals("IDS")) {
            return iterativeDeepeningSearch(initialBoard);
        }
        return null;
    }

    private List<GameBoard> ucs(GameBoard initialBoard) {
        PriorityQueue<Node> queue = new PriorityQueue<>(Comparator.comparingInt(Node::getCost));
        Set<String> visited = new HashSet<>();
        queue.add(new Node(initialBoard, null, 0, 0));

        while (!queue.isEmpty()) {
            Node current = queue.poll();
            nodesVisited++;
            GameBoard currentBoard = current.getBoard();

            if (currentBoard.isGoal()) {
                return reconstructPath(current);
            }

            String boardState = boardToString(currentBoard);
            if (!visited.contains(boardState)) {
                visited.add(boardState);
                for (GameBoard neighbor : currentBoard.getNeighbors()) {
                    String neighborState = boardToString(neighbor);
                    if (!visited.contains(neighborState)) {
                        Node newNode = new Node(neighbor, current, current.getCost() + 1, 0);
                        queue.add(newNode);
                    }
                }
            }
        }
        // Simpan final temperature untuk statistik
        finalTemperature = temperature;
        return null;
    }

    private List<GameBoard> greedyBestFirstSearch(GameBoard initialBoard) {
        PriorityQueue<Node> queue = new PriorityQueue<>(Comparator.comparingInt(Node::getHeuristic));
        Set<String> visited = new HashSet<>();
        queue.add(new Node(initialBoard, null, 0, initialBoard.getHeuristic(heuristic)));

        while (!queue.isEmpty()) {
            Node current = queue.poll();
            nodesVisited++;
            GameBoard currentBoard = current.getBoard();

            if (currentBoard.isGoal()) {
                return reconstructPath(current);
            }

            String boardState = boardToString(currentBoard);
            if (!visited.contains(boardState)) {
                visited.add(boardState);
                for (GameBoard neighbor : currentBoard.getNeighbors()) {
                    String neighborState = boardToString(neighbor);
                    if (!visited.contains(neighborState)) {
                        Node newNode = new Node(neighbor, current, 0, neighbor.getHeuristic(heuristic));
                        queue.add(newNode);
                    }
                }
            }
        }
        return null;
    }

    private List<GameBoard> aStar(GameBoard initialBoard) {
        PriorityQueue<Node> queue = new PriorityQueue<>(Comparator.comparingInt(Node::getTotalCost));
        Set<String> visited = new HashSet<>();
        queue.add(new Node(initialBoard, null, 0, initialBoard.getHeuristic(heuristic)));

        while (!queue.isEmpty()) {
            Node current = queue.poll();
            nodesVisited++;
            GameBoard currentBoard = current.getBoard();

            if (currentBoard.isGoal()) {
                return reconstructPath(current);
            }

            String boardState = boardToString(currentBoard);
            if (!visited.contains(boardState)) {
                visited.add(boardState);
                for (GameBoard neighbor : currentBoard.getNeighbors()) {
                    String neighborState = boardToString(neighbor);
                    if (!visited.contains(neighborState)) {
                        Node newNode = new Node(neighbor, current, current.getCost() + 1, neighbor.getHeuristic(heuristic));
                        queue.add(newNode);
                    }
                }
            }
        }
        return null;
    }
  
    /**
     * Implementasi Simulated Annealing untuk Rush Hour
     * SA menggunakan probabilitas untuk menerima state yang lebih buruk,
     * dengan probabilitas yang menurun seiring dengan penurunan temperature
     */
    private List<GameBoard> simulatedAnnealing(GameBoard initialBoard) {
        // Parameter SA
        double initialTemperature = 1000.0;
        double finalTemperature = 0.1;
        double coolingRate = 0.95;
        int maxIterations = 10000;
        int maxStagnation = 1000; // Maksimum iterasi tanpa improvement
        
        GameBoard currentBoard = initialBoard;
        GameBoard bestBoard = initialBoard;
        int currentCost = calculateCostForSA(currentBoard);
        int bestCost = currentCost;
        
        // Untuk menyimpan path terbaik yang ditemukan
        Map<GameBoard, GameBoard> parentMap = new HashMap<>();
        Map<GameBoard, Integer> costMap = new HashMap<>();
        
        parentMap.put(initialBoard, null);
        costMap.put(initialBoard, 0);
        
        double temperature = initialTemperature;
        int stagnationCounter = 0;
        int iterationsWithoutImprovement = 0;
        
        for (int iteration = 0; iteration < maxIterations && temperature > finalTemperature; iteration++) {
            nodesVisited++;
            
            // Cek apakah sudah mencapai goal
            if (currentBoard.isGoal()) {
                return reconstructPathFromMap(currentBoard, parentMap);
            }
            
            // Dapatkan neighbor secara random
            ArrayList<GameBoard> neighbors = currentBoard.getNeighbors();
            if (neighbors.isEmpty()) {
                break; // Tidak ada gerakan yang mungkin
            }
            
            GameBoard nextBoard = neighbors.get(random.nextInt(neighbors.size()));
            int nextCost = calculateCostForSA(nextBoard);
            
            // Hitung delta cost (perbedaan cost)
            int deltaCost = nextCost - currentCost;
            
            // SA decision: terima state baru jika lebih baik, atau dengan probabilitas tertentu jika lebih buruk
            boolean acceptMove = false;
            if (deltaCost < 0) {
                // State lebih baik, pasti diterima
                acceptMove = true;
                iterationsWithoutImprovement = 0;
            } else {
                // State lebih buruk, terima dengan probabilitas exp(-delta/T)
                double probability = Math.exp(-deltaCost / temperature);
                if (random.nextDouble() < probability) {
                    acceptMove = true;
                }
                iterationsWithoutImprovement++;
            }
            
            if (acceptMove) {
                // Update parent map untuk path reconstruction
                parentMap.put(nextBoard, currentBoard);
                costMap.put(nextBoard, costMap.getOrDefault(currentBoard, 0) + 1);
                
                currentBoard = nextBoard;
                currentCost = nextCost;
                stagnationCounter = 0;
                acceptedMoves++;
                
                // Update best solution jika ditemukan yang lebih baik
                if (currentCost < bestCost) {
                    bestBoard = currentBoard;
                    bestCost = currentCost;
                    iterationsWithoutImprovement = 0;
                }
            } else {
                stagnationCounter++;
                rejectedMoves++;
            }
            
            // Cooling schedule: kurangi temperature
            temperature *= coolingRate;
            
            // Early termination jika terlalu lama tidak ada improvement
            if (stagnationCounter > maxStagnation || iterationsWithoutImprovement > maxStagnation) {
                break;
            }
            
            // Restart jika stuck di local optimum untuk waktu yang lama
            if (iterationsWithoutImprovement > maxStagnation / 2) {
                // Random restart dengan temperature yang lebih tinggi
                temperature = Math.max(temperature, initialTemperature * 0.1);
                currentBoard = getRandomValidState(initialBoard);
                currentCost = calculateCostForSA(currentBoard);
                iterationsWithoutImprovement = 0;
            }
        }
        
        // Jika tidak menemukan solusi optimal, kembalikan path ke best state yang ditemukan
        if (bestBoard.isGoal()) {
            return reconstructPathFromMap(bestBoard, parentMap);
        }
        
        // Jika SA tidak menemukan solusi, coba fallback dengan hill climbing sederhana
        return hillClimbingFallback(bestBoard, parentMap, costMap);
    }
    
    /**
     * Fungsi cost untuk Simulated Annealing
     * Kombinasi dari heuristic dan penalty untuk state yang tidak baik
     */
    private int calculateCostForSA(GameBoard board) {
        int heuristicCost = board.getHeuristic(heuristic);
        
        // Tambahkan penalty untuk state yang jauh dari goal
        int primaryPiece_x = board.getPrimaryPiece().getX();
        int primaryPiece_y = board.getPrimaryPiece().getY();
        
        // Penalty jika primary piece tidak searah dengan exit
        int alignmentPenalty = 0;
        if (board.getPrimaryPiece().isHorizontal()) {
            // Untuk horizontal piece, penalty jika tidak sejajar dengan exit row
            alignmentPenalty = Math.abs(primaryPiece_y - board.getExitY()) * 2;
        } else {
            // Untuk vertical piece, penalty jika tidak sejajar dengan exit column  
            alignmentPenalty = Math.abs(primaryPiece_x - board.getExitX()) * 2;
        }
        
        // Penalty untuk blocking pieces (pieces yang menghalangi primary piece)
        int blockingPenalty = calculateBlockingPenalty(board);
        
        return heuristicCost + alignmentPenalty + blockingPenalty;
    }

    public int calculateBlockingPenalty(GameBoard board) {
        // Hitung penalty untuk blocking pieces
        int penalty = 0;
        
        // Cek apakah ada blocking pieces di jalur primary piece
        if (board.getPrimaryPiece().isPrimary()) {
            penalty += calculateBlockingPenaltyForSA(board);
        }
        
        return penalty;
    }
    
    private int calculateBlockingPenaltyForSA(GameBoard board) {
        Piece primaryPiece = board.getPrimaryPiece();
        char[][] grid = board.getGrid();
        int penalty = 0;
        
        if (primaryPiece.isHorizontal()) {
            // Cek jalur horizontal dari primary piece ke exit
            int y = primaryPiece.getY();
            int startX = primaryPiece.getX() + primaryPiece.getSize();
            int endX = board.getExitX();
            
            if (endX >= board.getCols()) endX = board.getCols();
            
            for (int x = startX; x < endX && x < grid[0].length; x++) {
                if (y < grid.length && grid[y][x] != '.' && grid[y][x] != 'K') {
                    penalty += 5; // Penalty untuk setiap blocking piece
                }
            }
        } else {
            // Cek jalur vertikal dari primary piece ke exit
            int x = primaryPiece.getX();
            int startY = primaryPiece.getY() + primaryPiece.getSize();
            int endY = board.getExitY();
            
            if (endY >= board.getRows()) endY = board.getRows();
            
            for (int y = startY; y < endY && y < grid.length; y++) {
                if (x < grid[0].length && grid[y][x] != '.' && grid[y][x] != 'K') {
                    penalty += 5; // Penalty untuk setiap blocking piece
                }
            }
        }
        
        return penalty;
    }
    
    /**
     * Dapatkan random valid state dari initial state dengan random walk
     */
    private GameBoard getRandomValidState(GameBoard initialBoard) {
        GameBoard current = initialBoard;
        int maxSteps = 20; // Maksimum langkah untuk random walk
        
        for (int i = 0; i < maxSteps; i++) {
            ArrayList<GameBoard> neighbors = current.getNeighbors();
            if (neighbors.isEmpty()) break;
            
            current = neighbors.get(random.nextInt(neighbors.size()));
        }
        
        return current;
    }
    
    /**
     * Fallback hill climbing jika SA tidak menemukan solusi
     */
    private List<GameBoard> hillClimbingFallback(GameBoard startBoard, 
                                               Map<GameBoard, GameBoard> parentMap,
                                               Map<GameBoard, Integer> costMap) {
        GameBoard current = startBoard;
        Set<String> visited = new HashSet<>();
        int maxSteps = 1000;
        
        for (int step = 0; step < maxSteps; step++) {
            nodesVisited++;
            
            if (current.isGoal()) {
                return reconstructPathFromMap(current, parentMap);
            }
            
            String currentState = boardToString(current);
            if (visited.contains(currentState)) {
                break; // Stuck di local optimum
            }
            visited.add(currentState);
            
            // Cari neighbor terbaik
            GameBoard bestNeighbor = null;
            int bestCost = calculateCostForSA(current);
            
            for (GameBoard neighbor : current.getNeighbors()) {
                int neighborCost = calculateCostForSA(neighbor);
                if (neighborCost < bestCost) {
                    bestCost = neighborCost;
                    bestNeighbor = neighbor;
                }
            }
            
            if (bestNeighbor == null) {
                break; // Tidak ada neighbor yang lebih baik
            }
            
            parentMap.put(bestNeighbor, current);
            costMap.put(bestNeighbor, costMap.getOrDefault(current, 0) + 1);
            current = bestNeighbor;
        }
        
        return null; // Tidak menemukan solusi
    }
    
    /**
     * Reconstruct path dari parent map
     */
    private List<GameBoard> reconstructPathFromMap(GameBoard goal, Map<GameBoard, GameBoard> parentMap) {
        List<GameBoard> path = new ArrayList<>();
        GameBoard current = goal;
        
        while (current != null) {
            path.add(current);
            current = parentMap.get(current);
        }
        
        Collections.reverse(path);
        return path;

    private List<GameBoard> iterativeDeepeningSearch(GameBoard initialBoard) {
        int depth = 0;
        while (true) {
            Set<String> visited = new HashSet<>();
            nodesVisited = 0;
            Node root = new Node(initialBoard, null, 0, 0);
            List<GameBoard> result = depthLimitedSearch(root, depth, visited);
            if (result != null) {
                return result;
            }
            depth++;
            if (depth > 1000) {
                return null;
            }
        }
    }

    private List<GameBoard> depthLimitedSearch(Node node, int limit, Set<String> visited) {
        nodesVisited++;
        GameBoard board = node.getBoard();
        if (board.isGoal()) {
            return reconstructPath(node);
        }
        if (limit == 0) {
            return null;
        }
        String boardState = boardToString(board);
        if (visited.contains(boardState)) {
            return null;
        }
        visited.add(boardState);
        for (GameBoard neighbor : board.getNeighbors()) {
            String neighborState = boardToString(neighbor);
            if (!visited.contains(neighborState)) {
                Node child = new Node(neighbor, node, node.getCost() + 1, 0);
                List<GameBoard> result = depthLimitedSearch(child, limit - 1, visited);
                if (result != null) {
                    return result;
                }
            }
        }
        return null;
    }

    private List<GameBoard> reconstructPath(Node goalNode) {
        List<GameBoard> path = new ArrayList<>();
        Node current = goalNode;
        while (current != null) {
            path.add(current.getBoard());
            current = current.getParent();
        }
        Collections.reverse(path);
        return path;
    }

    private String boardToString(GameBoard board) {
        StringBuilder sb = new StringBuilder();
        char[][] grid = board.getGrid();
        for (char[] row : grid) {
            for (char c : row) {
                sb.append(c);
            }
        }
        sb.append("P").append(board.getPrimaryPiece().getX()).append(",").append(board.getPrimaryPiece().getY());
        sb.append("E").append(board.getExitX()).append(",").append(board.getExitY());
        return sb.toString();
    }

    public int getNodesVisited() {
        return nodesVisited;
    }
    
    public int getAcceptedMoves() {
        return acceptedMoves;
    }
    
    public int getRejectedMoves() {
        return rejectedMoves;
    }
    
    public double getFinalTemperature() {
        return finalTemperature;
    }
}
