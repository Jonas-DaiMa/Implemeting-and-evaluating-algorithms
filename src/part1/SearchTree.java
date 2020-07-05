package part1;

import java.util.Arrays;
/**
 * Implemetation of Skewed Binary Search Tree that uses nodes
 * to store the values.
 */
public class SearchTree implements SkewedBST{

    public Node root;
    int max;
    int min;

    /**
     * Initializes an SearchTree, all building occurs in the constructor.
     * @param elems the elements to be added.
     * @param alpha decides how skewed the binary search tree is going to be.
     */
    public SearchTree(int[] elems, double alpha){
        int[] S = setS(elems);
        root = buildTree(alpha, 0, S.length - 1, S);
    }

    /**
     * Sorts the set so it is easier to work with and assigns max
     * and min values.
     * @param elems the set to be sorted
     * @return the sorted set
     */
    private int[] setS(int[] elems){
        Arrays.sort(elems);
        min = elems[0];
        max = elems[elems.length-1];
        return elems;
    }
      
    /**
     * Recursively builds the Skewed Binary Search Tree (sbst)
     * @param alpha defines the 'skewness' of the sbst - note alpha: 0.5 -> balanced Binary Search Tree
     * @param lo lowest index 
     * @param hi highest index
     * @return root node of the sbst 
     */
    private Node buildTree(double alpha, int lo, int hi, int[] S){

        if(lo > hi) return null;

        int mid = (int)(lo + (hi - lo) * alpha);        
        
        Node node = new Node(S[mid]);

        node.left = buildTree(alpha, lo, mid - 1, S);
        node.right = buildTree(alpha, mid + 1, hi, S);

        node.weight = size(node.left) + size(node.right) + 1;

        return node;
    }   

    /**
     * Finds and returns the value y in the tree such that y <= x.
     * Worst case is O(log(n)), best case is O(1) if x is outside 
     * the range of the set. (not considering memory)
     * @param x the value to be queried.
     * @return the value if it is in the set, if not "None".
     */
    public StringBuilder Pred(int x){
        if(!(x < min) && !(x > max)){
            return new StringBuilder(inOrderTraverse(root, x).value + " ");
        } else if (x < min){
            return new StringBuilder("None ");
        } else {
            return new StringBuilder(max + " ");
        }
    }

    /**
     * Recursive in-order trasversal of the given sbst
     * @param tree tree to traverse
     * @param x integer value
     * @return integer value y such that y <= x 
     */
    private Node inOrderTraverse(Node node, int x){
        if(node == null){
            return null;
        }
        if (x < node.value){
            return inOrderTraverse(node.left, x);
        } else if (x > node.value){
            Node t = inOrderTraverse(node.right, x);
            if(t == null){
                return node;
            }
            return t;

        } else{
            return node;
        }
    }
    

    /**
     * Finds the weight of a given node, if it is a
     * leaf returns 0.
     * @param n the node to be checked
     * @return the weight
     */
    private int size(Node n){
        if(n == null) return 0;
        else return n.weight; 
    }
    
    public static void main(String[] args) {
            double alpha = 0.4;
            InputReader.readInt();
            SearchTree st = new SearchTree(InputReader.readElems(),alpha);
            InputReader.runOp(st);
    }
}
