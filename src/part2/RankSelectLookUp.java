package part2;

/**
 * Implements a RankSelect Datastructure that stores
 * all the precomputed ranks of a given bitvector.
 */
public class RankSelectLookUp implements RankSelect {

    private int[] ranks; 
    private RankSelectSpaceEfficient rs;

    /**
     * Creates the data structure Rank-select with precomputed ranks.
     * @param vector takes an n-bit vector
     */
    public RankSelectLookUp(long[] input) {
        ranks = build(input);
    }

    /**
     * Precomputes all the rankings use the RankSelectNaive.
     * @param vector the bit vector that has be read in.
     * @return takes an n-bit vector
     */
    private int[] build(long[] input){
        if(rs!=null) rs.reBuild(input, 3);
        else rs = new RankSelectSpaceEfficient(input, 3);
        int[] rank = new int[input.length*64];

        for(int i = 0; i < rank.length; i++)
            rank[i] = rs.rank(i + 1);
        
        return rank;
    }

    /**
     * Method updates the field variables instead of initializing a new object. Used for testing.
     * @param vector the given vectors
     */
    public void reBuild(long[] input){
        ranks = build(input);
    }

    /**
     * Finds the rank of the given position in the vector. The position
     * is between 1 to the size of the vector. Works in Constant time.
     * @param i the positon one wants to be ranked 
     * @return the rank at the given position
     */
    public int rank(int i) {
        if (i == 0) return 0; 
        if (i < 1 || i > ranks.length) return -1;
        return ranks[i - 1];
    }

    /**
     * Finds the position of the rth 1 in the vector. Works in O(logn) time.
     * @param r which 1 should be found
     * @return the position of the rth 1
     */
    public int select(int r) {
        if (r < 1 || r > ranks.length ||ranks[ranks.length-1] < r) return -1;
        int result = binarySearch(ranks, r);
        return result == -1 ? -1 : result + 1;
    }

    /**
     * A modified binary search implementation that returns the index of element x, 
     * if there is more than one x, it finds the one in the smallest index, if it is
     * present in array arr. If it isn't returns -1.
     * Inspiration taken from:
     * https://stackoverflow.com/questions/13197552/using-binary-search-with-sorted-array-with-duplicates. 
     * @param ranks the array that should be searched through
     * @param x the target element
     * @return the index of the element or -1
     */
    private int binarySearch(int[] ranks, int x){
        if(ranks[0]==x) return 0;
        
        int lo = 0;
        int hi = ranks.length -1;

        int si = -1;

        while(lo <= hi){
            int mid = (hi - lo) / 2 + lo;
            if(ranks[mid] > x){
                hi = mid - 1;
            } else if(ranks[mid] == x){
                si = mid;
                hi = mid -1;
            } else{
                lo = mid + 1;
            }
        }
        return si;
    }

    public static void main(String[] args) {
        RankSelectLookUp rs = new RankSelectLookUp(InputReader.readLongs(System.in));
        InputReader.runOp(rs);
    }
}