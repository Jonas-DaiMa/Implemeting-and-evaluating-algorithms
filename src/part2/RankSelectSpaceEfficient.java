package part2;

/**
 * Implements a SpaceEfficient RankSelect Datastructure, following
 * the principles from 
 * González, R., Grabowski, S., Mäkinen, V., & Navarro, G. (2005, May). 
 * Practical implementation of rank and select queries. 
 * In Poster Proc. Volume of 4th Workshop on Efficient and Experimental Algorithms (WEA) (pp. 27-38).
 */
public class RankSelectSpaceEfficient implements RankSelect {

    private int[] bitVector;
    private int[] Rs;
    private int s;
    private int n;
    private int k;
    private byte[] popc;
    /**
     * Creates the data structure Rank-select using layers to minimize space usage.
     * Superblocks determined by k * 32, since the bitvectors are stored as a 32-bit
     * integers. 
     * @param vector the given vectors
     * @param k number that decides how many superblocks there will be
     */
    public RankSelectSpaceEfficient(long[] vector, int k) {
        this.k = k; n = vector.length * 64; s = k * 32;
        popc = buildPopc();
        bitVector = buildBitVector(n, vector);
        Rs = buildRs(n, s);
    }

    /**
     * Method updates the field variables instead of initializing a new object. Used for testing.
     * @param vector the given vectors
     * @param k number that decides how many superblocks there will be
     */
    public void reBuild(long[] vector, int k){
        this.k = k; n = vector.length * 64; s = k * 32;
        bitVector = buildBitVector(n, vector);
        Rs = buildRs(n, s);
    }

    /**
     * Finds the rank of the given position in the vector. The position
     * is between 1 to the size of the vector.
     * @param i the positon one wants to be ranked
     * @return the rank at the given position
     */
    public int rank(int i){
        if (i == 0) return 0;
        if (i < 0 || i >  n) return -1;
        return rankSuperblock(i) + rankBlock(i) + rankInt(i);
    }
    
    /**
     * Finds the position of the rth 1 in the vector using binary search over rank. If the
     * there are not that many set bits in the vector or r < 1, then r will return -1.
     * The binary search part is almost identical to the implementation in RankSelectLookUp.
     * @param r which number of 1s should be found at the given index.
     * @return the position of the rth 1 or -1.
     */
    public int select(int r){
        if (r < 1 || r > n || Rs[Rs.length-1] < r) return -1;
        int l = 1;
        int h = n;

        int idx = -1;

        while(l <= h){
            int m = (h - l) / 2 + l;
            if(rank(m) > r){
                h = m - 1;
            } else if(rank(m) == r){
                idx = m;
                h = m -1;
            } else{
                l = m + 1;
            }
        }
        return idx;
    }

    /**
     * Finds the rank of the superblock that precedes i.
     * @param i the position being ranked
     * @return the rank of the preceding superblock
     */
    private int rankSuperblock(int i) {
        int idx = i / s;
        return idx == 0 ? 0 : Rs[idx - 1];
    }

    /**
     * Finds the rank of 32-bit blocks inbetween the superblock that precedes i and the 
     * block that is in.
     * @param i the position being ranked
     * @return the rank of blocks within the superblock and the block containing i
     */
    private int rankBlock(int i) {
        int start =  (i / s) * k;
        int idx = i / 32;
        int sum = 0;
        if (idx == 0) return 0;
        for (int j = start; j < idx; j++)
            sum += popcount(bitVector[j]);
        return sum;
    }

    /**
     * Finds the rank of the block up to position i.
     * @param i the position being ranked
     * @return the rank in the block up to position i.
     */
    private int rankInt(int i) {
        int idx = i / 32; int x = i - (idx * 32);
        int shift = 32 - x;
        return idx == bitVector.length || x == 0 ? 0 : popcount(bitVector[idx] >>> shift);
    }

    /**
     * Builds the bitvector, representing it in blocks of 32-bits to use less memory.
     * @param n the length of the bitvector
     * @param vector the actual bitvector
     * @return the bitvector in represented in 32-bit blocks.
     */
    private int[] buildBitVector(int n, long[] vector) {
        int[] arr = new int[n/32];
        int previous = -1;
        for (int i = 0; i < arr.length; i++) {
            int j = i >> 1;
            if (j != previous)
                arr[i] = (int) (vector[j] >>> 32);
            else
                arr[i] = (int) vector[j];
            previous = j;
        }
        return arr;
    }

    /**
     * Builds the superblock array containing precomputed value of the ranks up until each 
     * superblock
     * @param n the size of the bitvector
     * @param s the size of each superblock (k*32)
     * @return superblock array with precomputed values
     */
    private int[] buildRs(int n, int s) {
        int[] arr = new int[n/s + 1];
        for (int i = 0; i < bitVector.length; i++) {
            int j = (i * 32) / s;
            arr[j] += popcount(bitVector[i]);
        }
        for (int j = arr.length -1; j >= 0; j--) {
            for (int l = j - 1; l >= 0; l--)
                arr[j] += arr[l];
        }
        return arr;
    }

    /**
     * Builds the popc table used for computing the rank of the integers
     * contained by the bitvector.
     * @return the popc table contaning the computed ranks of 0 to 255
     */
    private byte[] buildPopc() {
        byte[] arr = new byte[256];
        for (int i = 0; i < arr.length; i++) {
            for (byte b = 0; b < 9; b++)
                arr[i] += (byte) ((i >>> b) & 1);
        }
        return arr;
    }

    /**
     * Figures out how many bits are set in the given integer using the popc table.
     * @param x the integer where one wants the bit counted
     * @return the number of bits set in the integer
     */
    private int popcount(int x) {
        return popc[x & 0xFF] + popc[(x >>> 8) & 0xFF] + popc[(x >>> 16) & 0xFF] + popc[(x >>> 24)];
    }

    public static void main(String[] args) {
        int k = 1;
        RankSelectSpaceEfficient rs = new RankSelectSpaceEfficient(InputReader.readLongs(System.in), k);
        InputReader.runOp(rs);
    }
}