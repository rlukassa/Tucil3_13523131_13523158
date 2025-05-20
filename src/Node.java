package src;
public class Node implements Comparable<Node> {
    private GameBoard board;
    private Node parent;
    private int cost; // g(n) untuk UCS dan A*
    private int heuristic; // h(n) untuk Greedy dan A*
    private int totalCost; // f(n) = g(n) + h(n) untuk A*

    public Node(GameBoard board, Node parent, int cost, int heuristic) {
        this.board = board;
        this.parent = parent;
        this.cost = cost;
        this.heuristic = heuristic;
        this.totalCost = cost + heuristic;
    }

    // Getter
    public GameBoard getBoard() { return board; }
    public Node getParent() { return parent; }
    public int getCost() { return cost; }
    public int getHeuristic() { return heuristic; }
    public int getTotalCost() { return totalCost; }

    @Override
    public int compareTo(Node other) {
        return Integer.compare(this.totalCost, other.totalCost);
    }
}
