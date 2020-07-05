package part2;

import java.util.LinkedList;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Random;

/**
 * Holds everything related to testing of the rank-select
 * data structures.
 */
public class Testing{

    public static void main(String[] args) {
        Testing.runTests("Na LU SE", 10, 256L);
    }

    private static long seed;

    /**
     * Runs both scenario and automatic tests on all
     * rank-select data structures.
     * @param algos the rank-select data structures to be tested.
     * @param k value needed for SpaceEfficient to determine superblocks.
     * @param newSeed the seed that the bitvectors should be generated at.
     */
    static void runTests(String algos, int k, long nSeed) {
        Random rand = new Random();
        seed = nSeed != -1 ? nSeed : rand.nextLong();
        System.err.printf("%nTest are being run at seed: %d %n%n", seed);        
        System.err.printf("*** Running tests ***%n");
        manualTests(algos, k);
        automaticTests();
        System.err.printf("*** All Tests passed ***%n");
    }

    /**
     * Tests if the method select works as intended.
     * @param i th 1 in the vector
     * @return true if it succeeds or false if it fails 
     */
    public static boolean testRankSelect(int i, RankSelect rs, int n){
        int select = rs.select(i);

        if (select == -1 && rs.rank(n) < i) return true;
        int s1 = rs.rank(select);
        int s2 = i;

        if(s1 == s2){
            return true;
        } else{
            return false;
        }
    }

    /**
     * Tests if the method rank works as intended.
     * @param i amount of 1s until and including this number
     * @return true if it succeeds or false if it fails 
     */
    public static boolean testRank(int i, RankSelect rs){
        int s1 = rs.rank(i);
        int s2 = rs.rank(i-1);

        if(s1 >= s2){
            return true;
        } else{
            return false;
        }
    }

    /**
     * Goes over the files in data/Rank-selectTests/ and checks whether
     * the results from the rank-selects is the same as the expected
     * answer.
     * @param algos the rank-select data structures to be tested.
     * @param k value needed for SpaceEfficient to determine superblocks.
     */
    private static void manualTests(String algos, int k) {
        String input = "nothing";
        try {
            System.err.printf("\tManual tests started ...");
            for (int i = 0; i <= 4; i++) {
                input = "Test0" + i;
                File in = new File(String.format("data/Rank-selectTests/%s.in",input));
                File ans = new File(String.format("data/Rank-selectTests/%s.ans",input));
                LinkedList<Integer> answers = InputReader.readAnswers(new FileInputStream(ans));
                long[] set = InputReader.readLongs(new FileInputStream(in));
                for (RankSelect rs : buildRankSelect(algos, k, set)) {
                    LinkedList<Integer> rsAnswers = InputReader.readOp(rs, new FileInputStream(in));
                    for (int idx = 0; idx < answers.size(); idx++)
                        if (!answers.get(idx).equals(rsAnswers.get(idx))) {
                            System.err.printf("%n%s fails on %s.in on line %d%n", rs.getClass().getName(), input, idx+2);
                            System.err.printf("\tExpected: %d  Got: %d%n", answers.get(idx), rsAnswers.get(idx));
                            throw new AssertionError();
                        }
                }
            }
            System.err.printf(" passed%n");
        } catch (IOException e) {
            System.err.printf("%nError when trying to test, cannot locate %s%n",input);
        }
    }

    /**
     * Generates random sets at a given seed and of different sizes and runs
     * over different queries a 1000 times. RankSelectNaive is tested more in
     * depth than the others, since the others will be tested up agaisnt it.
     * The method reports if an error occurs and stops the run via an
     * AssertionError().
     */
    private static void automaticTests() {
        Generator gen = new Generator(seed);
        
        RankSelectLookUp rsLU = new RankSelectLookUp(gen.makeBitVector(2*64));
        RankSelectNaive rs = new RankSelectNaive(gen.makeBitVector(2*64));
        RankSelectSpaceEfficient rsSE = new RankSelectSpaceEfficient(gen.makeBitVector(2*64), 3);

        System.err.printf("\tRunning automatic tests ...");
        for (int in = 64; in < 1025; in ++) {
            long[] input = gen.makeBitVector(in*64);
            rs.reBuild(input);
            for (int op = 1; op < 51; op++) {
                int i = gen.randomI();
                boolean rank = testRank(i,rs); 
                boolean sel = testRankSelect(i,rs,in); 
                if (rank && sel){
                    int ins = input.length*64;
                    rsSE.reBuild(input, 3);
                    rsLU.reBuild(input);
                    naiveTestSL(rs, rsLU, i, ins, input);
                    naiveTestSL(rs, rsSE, i, ins, input);
                } else if(!rank) {
                    System.err.printf("%s fails testRank() on input %d%n", rs.getClass().getName(),i);
                    System.err.printf("\twith size %d and seed %d %n", in,seed);
                    throw new AssertionError();
                } else if (!sel) {
                    System.err.printf("%s fails testRankSelect() on input %d%n", rs.getClass().getName(),i);
                    System.err.printf("\twith size %d and seed %d %n", in,seed);
                    throw new AssertionError();
                }
                
            }
        }
        System.err.printf(" passed%n");
    }

    /**
     * Tests the other rank-select data structures up agaisnt the naive implementation.
     * @param rs the rank-select RankSelectLookUp and RankSelectSpaceEfficient will be tested agaisnt.
     * @param i the query to be given to rank() or select()
     * @param input the bitvector needed to build the rank-select
     */
    private static void naiveTestSL(RankSelect rs, RankSelect rsLU, int i, int ins, long[] input) {
        if (rsLU.rank(i) != rs.rank(i)) {
            errorMsg(rs, rsLU, ins, i, "rank", -1);
        } else if (rsLU.select(i) != rs.select(i)) {
            errorMsg(rs, rsLU, ins, i, "select", -1);
        }
    }

    /**
     * Prints an error message informing the user of what the given problem is.
     * along with throwing an AssertionError() to stop the program.
     * @param rs1 the rank-select being tested agaisnt
     * @param rs2 the rank-select being tested
     * @param in the size of the input
     * @param i the query given to rank() or select()
     * @param method to indicate whether it failed on rank() or select()
     * @param k to indicate at which value of k RankSelectSpaceEfficient failed
     */
    private static void errorMsg(RankSelect rs1, RankSelect rs2, int in, int i, String method, int k) {
        String kString = k != -1 ? "and k" + k : "";
        int rs1Val; int rs2Val;
        if (method.equals("rank")) {
            rs1Val = rs1.rank(i);
            rs2Val = rs2.rank(i);
        } else {
            rs1Val = rs1.select(i);
            rs2Val = rs2.select(i);
        }
        System.err.printf("%nOn %s query.%n", method);
        System.err.printf("\t%s != %s%n", rs1.getClass().getName(), rs2.getClass().getName());
        System.err.printf("\t%d and %d, respectively%n", rs1Val,rs2Val);
        System.err.printf("\twith size %d, input %d and seed %d" + kString + "%n", in,i,seed);
        throw new AssertionError();
    }

    /**
     * Based on a String creates the rank-select algorithms and adds them to a list.
     * @param algos the rank-select data structures to be tested.
     * @param k value needed for SpaceEfficient to determine superblocks.
     * @param in the bitvector to be read in
     * @return a list containing the specified rank-select data structures.
     */
    private static LinkedList<RankSelect> buildRankSelect(String algos, int k, long[] in) {
        LinkedList<RankSelect> algoList = new LinkedList<>();
        if (algos.contains("Na")) algoList.add(new RankSelectNaive(in));
        if (algos.contains("LU")) algoList.add(new RankSelectLookUp(in));
        if (algos.contains("SE")) algoList.add(new RankSelectSpaceEfficient(in, k));
        return algoList;
    }
}