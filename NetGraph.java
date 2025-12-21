import java.util.*;

public class NetGraph {

    // Represents a cell in the board
    public static class Node {
        public int r, c;
        public Node(int r, int c) { this.r = r; this.c = c; }

        public boolean equals(Object o) {
            if (!(o instanceof Node)) return false;
            Node n = (Node)o;
            return r == n.r && c == n.c; // same row & col
        }

        public int hashCode() { return Objects.hash(r, c); }

        public String toString() { return "(" + r + "," + c + ")"; }
    }

    // Represents a connection from one node to another
    public static class Edge {
        public Node from, to;
        public Edge(Node f, Node t) { from = f; to = t; }
        public String toString() { return from + " -> " + to; }
    }

    private Map<Node, List<Edge>> adjList = new HashMap<>(); // adjacency list
    private final int[] dr = {-1,0,1,0}; // row direction: up,right,down,left
    private final int[] dc = {0,1,0,-1}; // col direction

    public NetGraph(Board board) { buildGraph(board); } // build graph on creation

    private void buildGraph(Board board) {
        // Initialize all nodes in adjacency list
        for (int r = 0; r < board.rows; r++)
            for (int c = 0; c < board.cols; c++)
                adjList.put(new Node(r,c), new ArrayList<>());

        // Add edges based on tile connections
        for (int r = 0; r < board.rows; r++)
            for (int c = 0; c < board.cols; c++) {
                Node from = new Node(r,c);
                Tile t = board.grid[r][c];
                for (int d = 0; d < 4; d++) {           // check 4 directions
                    if (!t.hasConn(d)) continue;       // skip if no connection
                    int nr = r + dr[d], nc = c + dc[d];
                    if (nr<0||nc<0||nr>=board.rows||nc>=board.cols) continue; // skip out-of-bounds
                    if (board.grid[nr][nc].hasConn((d+2)%4)) { // check opposite connection
                        adjList.get(from).add(new Edge(from, new Node(nr,nc))); // add edge
                    }
                }
            }
    }

    // BFS to find all reachable nodes from start
    public Set<Node> bfs(Node start) {
        Set<Node> vis = new HashSet<>();        // visited nodes
        Queue<Node> q = new ArrayDeque<>();
        q.add(start); vis.add(start);

        while (!q.isEmpty()) {
            Node u = q.poll();
            for (Edge e : adjList.get(u))
                if (vis.add(e.to)) q.add(e.to);  // visit unvisited neighbors
        }
        return vis; // return reachable nodes
    }
}
