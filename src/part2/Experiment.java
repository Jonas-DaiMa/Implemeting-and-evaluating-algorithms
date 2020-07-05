package part2;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;
import java.util.function.IntToDoubleFunction;

/**
 * For running experiments on the algorithms in part 2.
 * Rank-selectNaive, Rank-selectLookUp & Rank-selectSpaceEfficient.
 */
public class Experiment {

    private static Generator gen;
    private static FileWriter writer;
    private static boolean warmUpOcccured = false;
    private static int repetition;
    private static int[] ks;
    private static long seed;
    private static long[] seeds;
    private static Scanner input;
    private static int min_n;
    private static int max_n;
    private static int q;

    public static void main(String[] args) {
        repetition = 200;
        ks = new int[]{3, 10, 64};
        seeds = new long[]{3945, 34, 586478};
        min_n = 96_000;
        max_n = 768_000;
        q = 1500; 
        Testing.runTests("Na LU SE", 10, -1);
        startExp();
    }

    /**
     * Starts the experiment with prompts.
     */
    static void startExp() {
        input = new Scanner(System.in);
        System.err.println();
        System.err.printf("What experiment would you like to perform?%n");
        System.err.printf("\tOptions: breakIT (b), Queries (simply press enter)%n%n");
        System.err.printf("Please only specify one of these options: ");
        String exp = input.nextLine();
        System.err.println();
        
        gen = new Generator(seed);
        
        runExp(exp);
    }

    /**
     * Runs the experiment. Goes over the number of seeds specified in the main method. Then
     * it iterates over n using doubling experiment, then for the SpaceEfficient it iterates
     * over the values of k specified in main method.
     * @param exp the given experiment to be run. BreakIT or Queries.
     */
    private static void runExp(String exp) {
        System.err.printf("%n*** Running %s experiment ***%n", exp);
        byte cycle = 1;
        if (exp.contains("breakIT") || exp.contains("b")) {
            System.err.printf("Which RankSelect would you like to break?%n");
            System.err.printf("\tOptions: Naive (Na), LookUp (LU), SpaceEfficent (SE) %n%n");
            System.err.printf("Please only specify one of these options: ");
            String algo = input.nextLine();
            prepareWriter(String.format("data/Experiments/Rank-select/breaking_%s.csv", algo));
            setHeaders("Algo,size,seed");
            System.err.println("Algo,size,seed");
            breakIt(algo, 3);
            writeToFile();
        } else {
            prepareWriter(String.format("data/Experiments/Rank-select/Queries.csv", exp));
            setHeaders("Algo,method,size,q,seed,k,Mean,Sdev");
            for (long seed : seeds) {
                gen.setSeed(seed);
                System.err.printf("\tStarting %d cycle%n", cycle);
                Timer t = new Timer();
                for (int n = min_n; n <= max_n; n *= 2) {
                    long[] vector = gen.makeBitVector(n);
                    int qSize = q != -1 ? q : n/64;
                    int[] queries = gen.makeQueries(qSize);
                    String info = String.format("%d,%d,%d", n, qSize, seed);
                    RankSelect[] naive_LU = new RankSelect[] {new RankSelectNaive(vector), new RankSelectLookUp(vector)};
                    runRankSelect(naive_LU, info + ",0", queries);
                    for (int k : ks)
                        runRankSelect(new RankSelect[] {new RankSelectSpaceEfficient(vector, k)}, info + "," + k, queries);
                }
                System.err.printf("\t%d cycle finished. It took %f seconds%n", cycle, t.check());
                cycle++;
            }
            System.err.println("*** Experiments completed ***");
            writeToFile();
        }
    }

    /**
     * Experiment to test how large n can get before the given algorithm breaks.
     * @param algo the algorithm in questions, specified in a String
     * @param k should be optimal k, needed for RankSelectSpaceEfficient
     */
    static void breakIt(String algo, int k) {
        int n = 1_000_000;
        while (true) {
            long[] in = gen.makeBitVector(n);
            RankSelect rs = buildRankSelects(algo, k, in);
            String info = String.format("%s,%d,%d", rs.getClass().getName(), n, seed);
            write(info);
            try {
                writer.flush();
            } catch(IOException e) {System.err.println("problem when flushing");}
            n *= 2;
        }
    }

    /**
     * Initiliazes the given algorithm according to the String given. 
     * "SE" = RankSelectSpaceEfficient
     * "LU" = RankSelectLookUp
     * "Na" = RankSelectNaive
     * @param algo the desired algorithm to initialize
     * @param k parameter for SE
     * @param in the vector to build in the given algorithm
     * @return the RankSelect specified
     */
    private static RankSelect buildRankSelects(String algo, int k, long[] in) {
        if (algo.contains("Na")) return new RankSelectNaive(in);
        else if (algo.contains("LU") || algo.contains("Lo")) return new RankSelectLookUp(in);
        else return new RankSelectSpaceEfficient(in, k);
    }

    /**
     * Runs over all the Skewed Binary Trees (sbst) it is given and performs a warmup for the JVM. 
     * @param algoList all the sbsts to be run over
     * @param info used to pass addtional information to the Mark8Setup
     * @param queries the queries to be run over used for warmup and in Mark8Setup
     * @return dummy value to avoid deadcode
     */
    private static double runRankSelect(RankSelect[] algoList, String info, int[] queries) {
        double dummy = 0;
        for (RankSelect rs : algoList) {
            dummy += runRank(rs, info, queries);
            dummy += runSelect(rs, info, queries);
        }
        return dummy;
    }

    /**
     * Runs the rank queries.
     * @param rs the RankSelect that the rank queries should be ran upon
     * @param info a String containing info to be carried to the Mark8
     * @param queries the queries to be run over
     * @return dummy value to avoid deadcode.
     */
    private static double runRank(RankSelect rs, String info, int[] queries) {
        IntToDoubleFunction rank = i -> rs.rank(i);
        double dummy = warmUp(rank, queries);
        String class_method = String.format("%s,rank",rs.getClass().getName());
        return dummy + Mark8Setup(class_method, info, rank, queries);
    }

    /**
     * Runs the select queries.
     * @param rs the Rank-select that the rank select should be ran upon
     * @param info a String containing info to be carried to the Mark8
     * @param queries the queries to be run over
     * @return dummy value to avoid deadcode.
     */
    private static double runSelect(RankSelect rs, String info, int[] queries) {
        IntToDoubleFunction rank = i -> rs.select(i);
        double dummy = warmUp(rank, queries);
        String class_method = String.format("%s,select",rs.getClass().getName());
        return Mark8Setup(class_method, info, i -> rs.select(i), queries);
    }

      /**
     * The benchmarking of the given query for each Rank-select (rs) happens in here. It goes
     * over each query rep times. Calculates the mean and standard deviation and then writes it
     * to a file.
     * Mark8Setup is taken from the below cited paper, with modifications to fit this specific case. 
	 * Peter Sestoft (2015, September). Microbenchmarks in Java and C#. (0.8.0). IT University of Copenhagen, Copenhagen.
     * @param className the name of the given algorithm that is being checked
     * @param info contains additional information, speficially the size of the bicvector, query and the seed
     * @param f the function that one would like to query upon
     * @param rep the number of times one would like each query to executed
     * @param queries all the queries that will be benchmarked
     * @return a dummy that ensures Java will not optimize away the results
     */
      private static double Mark8Setup(String className, String info, IntToDoubleFunction f, int rep, int[] queries) {
        double dummy = 0.0, runningTime = 0.0, st = 0.0, sst = 0.0;
        for (int j=0; j<rep; j++) {
            Timer t = new Timer();
            for (int query : queries) {
                dummy += f.applyAsDouble(query);
            }
            runningTime = t.check();
            double time = runningTime * 1e9;
            st += time;
            sst += time * time;
        }
        double mean = st/rep, sdev = Math.sqrt((sst - mean*mean*rep)/(rep-1));
        write(String.format("%s,%s,%f,%f", className, info, mean, sdev));
		return dummy;
	}

	/**
	 * Overloaded method to make it easier to change the number of iterations. Also inspired from:
	 * Peter Sestoft (2015, September). Microbenchmarks in Java and C#. (0.8.0). IT University of Copenhagen, Copenhagen.
     * @param className the name of the given algorithm that is being checked
     * @param info contains additional information, speficially the size of the bicvector, query and the seed
     * @param f the function that one would like to query upon
     * @param queries all the queries that will be benchmarked
	 * @return Same value as in Mark8Setup, to avoid deadcode
	 */
	private static double Mark8Setup(String className, String info, IntToDoubleFunction f, int[] queries) {
		return Mark8Setup(className, info, f, repetition, queries);
    }

    /**
     * Ensures that the JVM is warmed up, to reduce oddities in the intial tests.
     * @param f the function to be tested upon
     * @param queries the queries that the function should take
     * @return dummy value to avoid deadcode
     */
    private static double warmUp(IntToDoubleFunction f, int[] queries) {
        double dummy = 0;
        if (!warmUpOcccured) {
            System.err.print("\tWarmup Started ...");
            for (int i = 0; i < 100; i++)
                for (int query : queries)
                    dummy += f.applyAsDouble(query);
            System.err.println(" Warmup Completed");
            warmUpOcccured = true;
        }
        return dummy;
    }
    
    /**
     * Creates a writer with a given file name.
     * @param file the file name
     */
    private static void prepareWriter(String file) {
        try {
            writer = new FileWriter(file);
            System.err.printf("*** Writer is ready ***%n\tFiles will be written to: %s%n", file);
        } catch (IOException e) {
            System.err.println("Something went wrong when creating the file");
        }
    }

    /**
     * Sets the headers for the table (csv file)
     * @param info Misc. header can be set to anything, the others are constant
     */
    private static void setHeaders(String info) {
        try {
            writer.append(info);
            writer.append("\n");
            System.err.printf("\tHeaders are set to:   %s%n", info);
        } catch (IOException e) {
            System.err.println("Error occured when setting headers");
        }
    }

    /**
     * Appends the given String to the writer
     * @param content the String to be added
     */
    private static void write(String content) {
        try {
            writer.append(content);
            writer.append("\n");
        } catch (IOException e) {
            System.err.printf("Error occured when trying to append: %n %s", content);
        }
    }

    /**
     * Flushes the writer and closes it.
     */
    private static void writeToFile() {
        System.err.printf("*** Now writing to file ...");
        try {
            writer.flush();
            writer.close();
        } catch (IOException e) {
            System.err.println("Error occured when trying to flush and close FileWriter");
        }
        System.err.printf(" Success***%n");
    }

    /**
     * Crude wall clock timing utility, measuring time in seconds.
     * Taken from the below paper. 
     * Peter Sestoft (2015, September). Microbenchmarks in Java and C#. (0.8.0). IT University of Copenhagen, Copenhagen.
     */   
    public static class Timer {
        private long start, spent = 0;
        public Timer() { play(); }
        public double check() { return (System.nanoTime()-start+spent)/1e9; }
        public void pause() { spent += System.nanoTime()-start; }
        public void play() { start = System.nanoTime(); }
    }
}
