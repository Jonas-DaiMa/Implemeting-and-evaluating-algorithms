package part2;

import java.util.Random;
import java.util.stream.IntStream;
import java.util.stream.LongStream;

/**
 * Controls all generation needed for part2.
 */
public class Generator{

    private Random rand;
    private int bitVecSize;

    /**
     * Instantiates the Generator with a seed.
     * @param seed the value seeded
     */
    public Generator(long seed) {
        rand = new Random(seed);
    }

    /**
     * Generates a bitvector of size n. n should be a multiple of 64.
     * @param n size of the bitvector
     * @return returns the bitvector as a an array of longs.
     */
    long[] makeBitVector(int n) {
        if (n % 64 != 0) throw new IllegalArgumentException("the given number must be divisble by 64");
        bitVecSize = n;
        int size = n/64;
        LongStream ls = LongStream.generate(() -> makeLong());
        return ls.limit(size).toArray();
    }

    /**
     * Sets the seed of the generator.
     * @param seed the seed to be set
     */
    void setSeed(long seed) {
        rand.setSeed(seed);
    }

    /**
     * Creates a random long.
     * Inspired by:
     * https://stackoverflow.com/questions/21591637/what-is-a-the-quickest-way-to-bitwise-convert-two-ints-to-a-long-in-java
     * @return the random long
     */
    private long makeLong() {
        int lo = rand.nextInt(); 
        int hi = rand.nextInt(); 
        return (((long) hi) << 32) | (lo & 0xFFFFFFFFL);
    }

    /**
     * Creates the queries to be used for both rank and select.
     * @param n the number of queries to be made
     * @return the queries in an array
     */
    int[] makeQueries(int n) {
        IntStream range = IntStream.builder()
                            .add(bitVecSize)
                            .add(1)
                            .build();
        IntStream is = rand.ints(n, 2, bitVecSize);
        return IntStream.concat(is,range).toArray();
    }

    /**
     * Generates a random number between 1 and the bitvector's size.
     * The bitvector having been generated in the makeBitVector()
     * method.
     * @return a random number in the bitvector
     */
    int randomI() {
        return rand.nextInt(bitVecSize) + 1;
    }
}