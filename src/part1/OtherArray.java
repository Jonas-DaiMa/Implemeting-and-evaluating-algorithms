package part1;

import java.util.PriorityQueue;

/**
 * An implementation of a pqDFS Skewed Binary Search Tree. Based on the below paper
 * Brodal, G.S. and Moruz, G., 2006, September. Skewed binary search trees. 
 * In European Symposium on Algorithms (pp. 708-719). Springer, Berlin, Heidelberg. 
 */
public class OtherArray implements SkewedBST{

    int[] set;
    Node[] nodes;
    int idx;
    int min;
    int max;

    /**
     * Initializes an OtherArray, all building occurs in the constructor.
     * This implementation uses the SearchTree to build.
     * @param elems the elements to be added.
     * @param alpha decides how skewed the binary search tree is going to be.
     * @param p the size of each block.
     */
    public OtherArray(int[] elems, double alpha, int p){
        SearchTree tree = new SearchTree(elems, alpha);
        int n = elems.length;
        set = new int[3 * n];
        nodes = new Node[3 * n];
        idx = 1;
        pqDFS(tree.root, p);
        setupOtherArray();
        min = tree.min;
        max = tree.max;
    }

    /**
     * Finds and returns the value y in the tree such that y <= x.
     * Worst case is O(log(n)), best case is O(1) if x is outside 
     * the range of the set. (not considering memory)
     * @param x the value to be queried.
     * @return the value if it is in the set, if not "None".
     */
    public StringBuilder Pred(int x){
        int pos = 1;
        int k = set[pos];
        int y = Integer.MIN_VALUE;

        if (x < min) return new StringBuilder("None ");
        if (x > max) return new StringBuilder(max + " ");

        while (true) {
            if (x < k) pos = set[pos-1];
            if (x > k) pos = set[pos+1];
            
            if (k == x) return new StringBuilder(k + " ");
            if (k < x) y = k;

            if (pos == -1 && y > x) return new StringBuilder("None ");
            if (pos == -1 && y == Integer.MIN_VALUE) return new StringBuilder("None ");
            if (pos == -1 && y < x) return new StringBuilder(y + " ");

            k = set[pos];
        }
    }

    /**
     * Given a binary search tree - adds all the nodes to a priority queue
     * nodes are stored according to their weight (size of their subtrees)
     * @param tree the tree to be added to the priority queue
     * @param pq the priority queue to be added to
     */
    private void addToPQ(Node tree, PriorityQueue<Node> pq){
        if (tree != null) {
            if (tree.right != null) pq.add(tree.right);
            if (tree.left != null) pq.add(tree.left);
        }
    }

    /**
     * Adds the keys of all the 'nodes' to set each node being 3 positions 
     * in the array like so [left, key, right], where both left and right 
     * are the index of the children to the 'node'. It stores the p heaviest
     * nodes and then recursively adds the children of those in p blocks.
     * @param tree the tree to be added be built into the array
     * @param p the size of the block
     */
    private void pqDFS(Node tree, int p){
        // PQ of Nodes
        PriorityQueue<Node> pq = new PriorityQueue<>((e1, e2) -> e2.weight - e1.weight);  
        pq.add(tree);
        for (int i = 0; i < p; i++ ) {
            if (pq.size() > 0) {
                tree = pq.poll();
                set[idx] = tree.value;
                nodes[idx] = tree;
                addToPQ(tree, pq);
                idx += 3;
            } else
                return;
        }
        while (pq.size() > 0)
            pqDFS(pq.poll(), p);
    }

    /**
     * Adds the left and right children indices to each node.
     */
    private void setupOtherArray(){
        for(int i = 1; i < set.length; i += 3){
            Node left = nodes[i].left;
            Node right = nodes[i].right;
            if (left != null) set[i-1] = findIndex(left.value);
            else set[i-1] = -1;
            if (right != null) set[i+1] = findIndex(right.value);
            else set[i+1] = -1;
        }
        nodes = null;
    }

    /**
     * Finds the index of a given value in the set array.
     * @param value the value that one wants to find the index of.
     * @return the index value if it is there and -1 if it isnt.
     */
    private int findIndex(int value) {
        for (int i = 1; i < set.length; i += 3) {
            if (value == set[i]) return i;
        }
        return -1;
    }

    public static void main(String[] args) {
        double alpha = 0.4;
        int p = 21;
        InputReader.readInt();
        OtherArray st = new OtherArray(InputReader.readElems(),alpha, p);
        InputReader.runOp(st);
    }
}
