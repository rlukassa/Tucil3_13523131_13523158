package src;

import java.util.*;

public class Solver {
    private String algorithm;
    private int nodesVisited;

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
        }
        return null;
    }

    private List<GameBoard> ucs(GameBoard initialBoard) {
        PriorityQueue<Node> queue = new PriorityQueue<>(Comparator.comparingInt(Node::getCost));
        Set<String> visited = new HashSet<>();
        queue.add(new Node(initialBoard, null, 0, 0));
        Map<GameBoard, Node> nodeMap = new HashMap<>();
        nodeMap.put(initialBoard, queue.peek());

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
                        nodeMap.put(neighbor, newNode);
                    }
                }
            }
        }
        return null;
    }

    private List<GameBoard> greedyBestFirstSearch(GameBoard initialBoard) {
        PriorityQueue<Node> queue = new PriorityQueue<>(Comparator.comparingInt(Node::getHeuristic));
        Set<String> visited = new HashSet<>();
        queue.add(new Node(initialBoard, null, 0, initialBoard.getHeuristic()));
        Map<GameBoard, Node> nodeMap = new HashMap<>();
        nodeMap.put(initialBoard, queue.peek());

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
                        Node newNode = new Node(neighbor, current, 0, neighbor.getHeuristic());
                        queue.add(newNode);
                        nodeMap.put(neighbor, newNode);
                    }
                }
            }
        }
        return null;
    }

    private List<GameBoard> aStar(GameBoard initialBoard) {
        PriorityQueue<Node> queue = new PriorityQueue<>(Comparator.comparingInt(Node::getTotalCost));
        Set<String> visited = new HashSet<>();
        queue.add(new Node(initialBoard, null, 0, initialBoard.getHeuristic()));
        Map<GameBoard, Node> nodeMap = new HashMap<>();
        nodeMap.put(initialBoard, queue.peek());

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
                        Node newNode = new Node(neighbor, current, current.getCost() + 1, neighbor.getHeuristic());
                        queue.add(newNode);
                        nodeMap.put(neighbor, newNode);
                    }
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
        return sb.toString();
    }

    public int getNodesVisited() {
        return nodesVisited;
    }
}
