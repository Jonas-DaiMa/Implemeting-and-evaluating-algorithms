package part1;

import java.util.Arrays;

/**
 * Implemetation of a Skewed Binary Search Tree that uses an array
 * to store the values.
 */
public class SortedArray implements SkewedBST {

    private static int[] S;
    private double alpha;

    /**
     * Initializes an SortedArray, all building occurs in the constructor.
     * Only sorts the array given, then performs a binary search on that
     * array.
     * @param elems the elements to be added.
     * @param alpha decides how skewed the binary search tree is going to be.
     */
    public SortedArray(int[] elems, double alpha){
        S = elems;
        Arrays.sort(S);
        this.alpha = alpha;
    }

    /**
     * Finds and returns the value y in the tree such that y <= x.
     * Worst case is O(log(n)), best case is O(1) if x is outside 
     * the range of the set (not considering memory).
     * @param x the value to be queried.
     * @return the value if it is in the set, if not "None".
     */
    public StringBuilder Pred(int x){
        return SBS(x);
    }

    /**
     * The Skewed Binary search performed by Pred.
     * @param x the value to be queried.
     * @return the value if it is in the set, if not "None".
     */
    private StringBuilder SBS(int x){
        int lo = 0;
        int hi = S.length - 1;

        if(!(x < S[0]) && !(x> S[S.length -1]) ){
            while (true) {
                int mid = (int)(lo + (hi - lo) * alpha);
                
                if (lo > hi) {
                    return new StringBuilder(S[mid] + " ");
                }

                if      (x < S[mid]) hi = mid - 1;
                else if (x > S[mid]) lo = mid + 1;
                
                else {
                    return new StringBuilder(S[mid] + " ");
                }
            }
        } else if (x > S[S.length - 1]){
            return new StringBuilder(S[S.length - 1] + " ");
        } else {
            return new StringBuilder("None ");
        }
    }

    public static void main(String[] args) {      
        double alpha = 0.4;
        InputReader.readInt();
        SortedArray st = new SortedArray(InputReader.readElems(), alpha);
        InputReader.runOp(st);
    }
}