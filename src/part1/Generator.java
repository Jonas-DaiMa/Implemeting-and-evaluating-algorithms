package part1;

import java.util.Random;
import java.util.stream.IntStream;

/**
 * Controls all generation needed for part1.
 */
public class Generator{

    private Random rand;
    public int count = 0;
    private int max;
    private int min;

    /**
     * Instantiates the Generator with a seed
     * @param seed the value seeded
     */
    public Generator(long seed) {
        rand = new Random(seed);
    }

    /**
     * Generates a set of size n.
     * @param n the size of the set
     * @return the set as an array
     */
    int[] generateSet(int n) {
        if (n < 2) throw new IllegalArgumentException("n has to be larger than 1");
        findRange(n);
        IntStream range = IntStream.builder()
                                .add(max)
                                .add(min)
                                .build();
        IntStream set = rand
                            .ints(min + 1, max) // random ints between min and max, exclusive
                            .distinct()
                            .limit(n-2); // because adding both min and max value
        return IntStream.concat(set, range).toArray();
    }

    /**
     * Generates the queries to be performed on the set. The queries will be of size n.
     * The queries will always contain the maximum and minimum values of the set, to
     * ensure worst case queries are included.
     * @param n the number of queries to be made.
     * @return the queries as an array
     */
    int[] generateQueries(int n) {
        if (n < 2) throw new IllegalArgumentException("n has to be larger than 1");
        IntStream worstVal = IntStream.builder()
                                .add(max)
                                .add(min)
                                .build();
        IntStream Q = rand.ints(n-2, min + 1, max); // random ints between min and max exclusive
        return IntStream.concat(Q, worstVal).toArray();
    }

    /**
     * Finds the maximum and minimum values randomly, ensuring that the difference between
     * them are atleast n.
     * @param n the wanted size of the set
     */
    private void findRange(int n) {
        max = rand.nextInt();
        min = rand.nextInt();
        max = Math.max(max, min);
        min = Math.min(max, min);
        if (max - min < n) findRange(n);
    }

    /**
     * Sets the seed of the generator.
     * @param seed the seed to be set.
     */
    void setSeed(long seed) {
        rand.setSeed(seed);
    }
}