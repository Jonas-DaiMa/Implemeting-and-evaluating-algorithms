package part2;

/**
 * Implements a RankSelect Datastructure that stores
 * the bitvector.
 */
public class RankSelectNaive implements RankSelect {

    private int[] n;
    private int maxSelect;

    /**
     * Creates the naive data structure Rank-select that holds the vector.
     * @param vector the given vector one would like to read in.
     */
    public RankSelectNaive(long[] input) {
        n = InputReader.formatVector(input);
        maxSelect = rank(n.length);
    }

    /**
     * Method updates the field variables instead of initializing a new object. Used for testing.
     * @param vector the given vectors
     */
    public void reBuild(long[] input){
        n = InputReader.formatVector(input);
        maxSelect = rank(n.length);
    }

    /**
     * Finds the rank of the given position in the vector. The position
     * is between 1 to the size of the vector. Works in Linear time.
     * @param i the positon one wants to be ranked
     * @return the rank at the given position
     */
    public int rank(int i){
        if (i == 0) return 0;
        if (i < 0 || i > n.length) return -1;
        int numberOfIs = 0;

        for(int j = 0; j < i; j++){
            if(n[j] == 1){
                numberOfIs ++;
            }
        }
        return numberOfIs;
    }

    /**
     * Finds the position of the rth 1 in the vector. Works in linear time.
     * @param r which 1 should be found
     * @return the position of the rth 1
     */
    public int select(int r){
        int num_1 = 0;
        int position = 0;

        if (r < 1 || r > n.length || maxSelect < r) return -1;

        for(int i = 0; i < n.length; i++){
            if(n[i] == 1){
                num_1 ++;
            }
            if(num_1 == r){
                position = i + 1;
                break;
            }
        }
        return num_1 != r ? -1 : position;
    }

    /**
     * Provides the size of RankSelectNaive.
     * @return the size of the data structure
     */
    int size() {
        return n.length;
    }

    public static void main(String[] args) {
        RankSelectNaive rs = new RankSelectNaive(InputReader.readLongs(System.in));
        InputReader.runOp(rs);
    }
}