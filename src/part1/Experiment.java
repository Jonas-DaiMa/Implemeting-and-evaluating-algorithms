package part1;

import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedList;
import java.util.Scanner;

/**
 * For running experiments on the algorithms in part 1.
 * SortedArray, SearcTree & OtherArray.
 */
public class Experiment {

    private static Generator gen;
    private static FileWriter writer;
    private static boolean warmUpOcccured = false;
    private static int repetition;
    private static int optimalP; 
    private static int constantQ;
    private static double optimalA;
    private static long[] seeds;

    public static void main(String[] args) {
        optimalP = 1; 
        constantQ = 1_000_000; 
        repetition = 200;
        optimalA = 0.75;
        seeds = new long[]{7, 943, 57438};
        startExp();
    }

    /**
     * Starts up the experiment and ask the user for prompts, depending on which experiment is
     * chosen
     */
    public static void startExp() {
        Scanner input = new Scanner(System.in);
        System.err.println();
        System.err.printf("What experiment would you like to perform?%n");
        System.err.printf("\tOptions: findOptimalP (fop), onlyAlpha (oa), compareAllAlgo (caa)%n%n");
        System.err.printf("Please only specify one of these options: ");
        String cmd = input.nextLine();
        System.err.println();
        gen = new Generator(10);

        if (cmd.contains("findOptimalP") || cmd.equals("fop")) {
            int[] tuple = optimalPrompt(input);
            int[] ps = buildP(tuple[0], tuple[1], tuple[2]);
            double[] alphas = new double[]{0.1, 0.35, 0.65, 0.9};
            int constantN = 25_000;
            runExp("findOptimalP", "oa", alphas, constantN, constantN, ps, constantQ);
        }
        else if (cmd.contains("onlyAlpha") || cmd.equals("oa")) {
            int constantN = onlyAlphaPrompt(input);
            double[] alphas = buildAlpha(0.05, 0.95, 0.05);
            runExp("onlyAlpha", "sa st oa", alphas, constantN, constantN, new int[]{optimalP}, constantQ);
        }
        else if (cmd.contains("compareAllAlgo") || cmd.equals("caa")) {
            int[] tuple = compareAllAlgoPrompt(input);
            runExp("compareAllAlgo", "sa st oa", new double[]{optimalA}, tuple[0], tuple[1], new int[]{optimalP},-1);
        } else {
            System.err.printf("%s is not recognised as an Option, please try again%n", cmd);
        }
    }

    /**
     * Builds the values of p given the minimum, maximum and the step to be 
     * performed between them.
     * @param min_p the smallest p value
     * @param max_p the largest p value
     * @param step the number to be added in each iteration
     * @return the p values in an array
     */
    private static int[] buildP(int min_p, int max_p, int step) {
        int[] ps = new int[(max_p - min_p)/step + 1];
        int idx = 0;
        for (int p = min_p; p <= max_p; p += step) {
            ps[idx] = p;
            idx++;
        }
        return ps;
    }

    /**
     * Builds the values of alphas given the minimum, maximum and the step to be 
     * performed between them.
     * @param min_a the smallest alpha value
     * @param max_a the largest alpha value
     * @param step the number to be added in each iteration
     * @return the alphas in an array
     */
    private static double[] buildAlpha(double min_a, double max_a, double step) {
        double[] alphas = new double[(int) ((max_a - min_a)/step) + 1];
        int idx = 0;
        for (double alpha = min_a; alpha <= max_a; alpha += step) {
            alphas[idx] = alpha;
            idx++;
        }
        return alphas;
    }

    /**
     * Runs the experiment. Goes over the number of seeds specified in the main method. Then
     * it iterates over the given alpha values, then size of the set. And lastly the given
     * number of ps wanted tested.
     * @param exp Contains the name of the experiment being run
     * @param algos Contains the shorthand notation for the algos wanted to run over
     * @param alphas the values of alpha wanting to be running
     * @param min_n the initial value that the set should be initial at
     * @param max_n the last value that the set should be initial at
     * @param ps the ps that should be run over
     * @param q The constant query, if one wants queries to be twice the size of set, put in -1.
     */
    private static void runExp(String exp, String algos, double[] alphas, 
                                int min_n, int max_n, int[] ps, int q) {
        prepareWriter(String.format("data/Experiments/SkewedBST/%s.csv", exp));
        setHeaders("p,n,alpha,seed");
        System.err.printf("%n*** Running %s experiment ***%n", exp);
        byte cycle = 1;
        
        for (long seed : seeds) {
            gen.setSeed(seed);
            System.err.printf("\tStarting %d cycle%n", cycle);
            Timer t = new Timer();
            for (double alpha : alphas) {
                for (int n = min_n; n <= max_n; n *= 2) {
                    int[] set = gen.generateSet(n);
                    int qSize = q != -1 ? q : n*2; 
                    int[] queries = gen.generateQueries(qSize);
                    for (int p : ps) {
                        LinkedList<SkewedBST> algoList = makeSBST(set, alpha, p, algos);
                        String info = String.format("%d,%d,%f,%d", p, n, alpha, seed);
                        runSBST(algoList, info, queries);
                    }
                }
            }
            System.err.printf("\t%d cycle finished. It took %f seconds%n", cycle, t.check());
            cycle++;
        }
        System.err.println("*** Experiments completed ***");
        writeToFile();
    }

    /**
     * Creates the Skewed Binary Trees (sbst) specified in the String and adds them to a List.
     * @param set the set of values used when initializing an sbst
     * @param alpha the value that decides the skewness of sbst.
     * @param p the value that decides the size of heavy nodes in OtherArray.
     * @param algos indicates which sbsts that should be made.
     * @return all the sbsts specified in the String.
     */
    private static LinkedList<SkewedBST> makeSBST(int[] set, double alpha, int p, String algos) {
        LinkedList<SkewedBST> algoList = new LinkedList<>();
        if (algos.contains("sa")) algoList.add(new SortedArray(set,alpha));
        if (algos.contains("st")) algoList.add(new SearchTree(set,alpha));
        if (algos.contains("oa")) algoList.add(new OtherArray(set,alpha,p));
        return algoList;
    }

    /**
     * Runs over all the Skewed Binary Trees (sbst) it is given and performs a warmup for the JVM. 
     * @param algoList all the sbsts to be run over
     * @param info used to pass addtional information to the Mark8Setup.
     * @param queries the queries to be run over used for warmup and in Mark8Setup.
     * @return dummy value to avoid deadcode.
     */
    private static int runSBST(LinkedList<SkewedBST> algoList, String info, int[] queries) {
        int dummy = 0;
        for (SkewedBST sbst : algoList) {
            if (!warmUpOcccured) {
                dummy += warmUp(sbst, queries);
                warmUpOcccured = true;
            }
            dummy += Mark8Setup(sbst.getClass().getName(), info, sbst, queries);
        }
        return dummy;
    }

    /**
     * The benchmarking of the Pred for each Skewed Binary Tree (sbst) happens in this function. It goes
     * over each query rep times. Calculates the mean and standard deviation and then writes it
     * to a file.
     * Mark8Setup is taken from the below cited paper, with modifications to fit this specific case. 
	 * Peter Sestoft (2015, September). Microbenchmarks in Java and C#. (0.8.0). IT University of Copenhagen, Copenhagen.
     * @param className the name of the given algorithm that is being checked.
     * @param info contains additional information, speficially the value of p, n, alpha and the seed.
     * @param sbst the given sbst that one wants to use the Pred function from.
     * @param rep the number of times one would like each query to executed
     * @param queries all the queries that will be benchmarked.
     * @return a dummy that ensures Java will not optimize away the results.
     */
	private static int Mark8Setup(String className, String info, SkewedBST sbst, int rep, int[] queries) {
        double runningTime = 0.0, st = 0.0, sst = 0.0;
        int dummy = 0;

        for (int j=0; j<rep; j++) {
            Timer t = new Timer();
            for (int query : queries) {
                dummy += sbst.Pred(query).capacity();
            }
            runningTime = t.check();
            double time = runningTime * 1e9 / queries.length;
            st += time;
            sst += time * time;
        }
        double mean = st/rep, sdev = Math.sqrt((sst - mean*mean*rep)/(rep-1));
        write(String.format("%s,%s,%f,%f", className, info, mean, sdev));
		return dummy;
	}

    /**
	 * Overloaded method to make it easier to play around with number of iterations. Also inspired from:
	 * Peter Sestoft (2015, September). Microbenchmarks in Java and C#. (0.8.0). IT University of Copenhagen, Copenhagen.
     * @param className the name of the given algorithm that is being checked.
     * @param info contains additional information, speficially the value of p, n, alpha and the seed.
     * @param sbst the given sbst that one wants to use the Pred function from.
     * @param queries all the queries that will be benchmarked.
     * @return dummy value to avoid deadcode.
     */
	private static int Mark8Setup(String className, String info, SkewedBST sbst, int[] queries) {
		return Mark8Setup(className, info, sbst, repetition, queries);
    }

    /**
     * Prepares the system for the test with a warmup round..
     * @param sbst the Skewed Binary Search Tree to be tested upon
     * @param queries the queries that the Pred should take.
     * @return dummy value to avoid deadcode.
     */
    private static int warmUp(SkewedBST sbst, int[] queries) {
        int dummy = 0;
        System.err.print("\tWarmup Started ...");
        for (int i = 0; i < 100; i++)
            for (int query : queries)
                dummy += sbst.Pred(query).capacity();
        System.err.println(" Warmup Completed");
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
     * @param info Misc. header can be set to anything, the others are constant.
     */
    private static void setHeaders(String info) {
        try {
            String[] headers = new String[]{"Algo", info, "Mean", "Sdev"};
            writer.append(String.join(",", headers));
            writer.append("\n");
            System.err.printf("\tHeaders are set to:   %s%n", String.join(",", headers));
        } catch (IOException e) {
            System.err.println("Error occured when setting headers");
        }
    }

    /**
     * Appends the given String to the writer
     * @param content the String to be added.
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
     * Prompts the user for specific input for the findOptimalP experiment. Also tells the
     * user which values it will be run at.
     * @param input a scanner that reads the input from the terminal
     * @return the mimumum and maximum values of p and the step that should be performed within the range.
     */
    private static int[] optimalPrompt(Scanner input) {
        System.err.printf("*** The findOptimalP experiment is about to be performed ***%n");
        System.err.printf("\tSet size and query size have been set to 25,000 and %n");
        System.err.printf("\t1,000,000, respectively. P will be set below, the range %n");
        System.err.printf("\twill be inclusive. %n");
        System.err.printf("%n\t** Please set the following **%n");
        System.err.printf("\tSmallest P: ");
        int min_p = input.nextInt();
        System.err.printf("\tLargest P: ");
        int max_p = input.nextInt();
        System.err.printf("\tStep value: ");
        int step = input.nextInt();
        System.err.println();
        return new int[]{min_p,max_p,step};
    }

    /**
     * Prompts the user for specific input for the onlyAlpha experiment. Also tells the
     * user which values it will be run at.
     * @param input a scanner that reads the input from the terminal
     * @return the constant set size
     */
    private static int onlyAlphaPrompt(Scanner input) {
        System.err.printf("*** The onlyAlpha experiment is about to be performed ***%n");
        System.err.printf("\tIn this experiment only the values of alpha will change,%n");
        System.err.printf("\tSet size and p will be constant. Alpha goes from 0.05 to %n");
        System.err.printf("\t0.95, inclusive.%n");
        System.err.printf("%n\t** Please set the following **%n");
        System.err.printf("\tConstant Set size: ");
        int n = input.nextInt();
        System.err.println();
        return n;
    }
    
    /**
     * Prompts the user for specific input for the compareAllAlgo experiment. Also tells the
     * user which values it will be run at.
     * @param input a scanner that reads the input from the terminal
     * @return the range of values that the set should be generated inbetween, inclusive.
     */
    private static int[] compareAllAlgoPrompt(Scanner input) {
        System.err.printf("*** The compareAllAlgo experiment is about to be performed ***%n");
        System.err.printf("\tBelow you will be asked to select Set size. Alpha will be%n");
        System.err.printf("\tconstant at %d. The Set will be gone over using the double%n", optimalP);
        System.err.printf("\texperiment. The range is inclusive for both %n");
        System.err.printf("%n\t** Please set the following **%n");
        System.err.printf("\tSmallest Set size: ");
        int minN = input.nextInt();
        System.err.printf("\tLargest Set size: ");
        int maxN = input.nextInt();
        System.err.println();
        return new int[]{minN, maxN};
    }

    /**
     * Crude wall clock timing utility, measuring time in seconds
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