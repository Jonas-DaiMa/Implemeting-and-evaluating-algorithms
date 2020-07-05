package part2;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.LinkedList;
import java.io.InputStream;

/**
 * Reads and formats the input needed for part2.
 */
public class InputReader {

    static private BufferedReader br;

    /**
     * Formats the a long array into a bit vector contained in an int array.
     * @param input the bitvector stored in an array of longs
     * @return a bit vector
     */
    static int[] formatVector(long[] input) {
        String[] s = new String[input.length];
        int[] a = new int[s.length * 64];
        
        for(int i = 0; i < s.length; i++){
            s[i] = Long.toBinaryString(input[i]);
            
            int leading =  64 - s[i].length();

            if(leading > 0){
                String pattern = "%0" + leading + "d";
                s[i] = String.format(pattern, 0) + s[i];
            }
            for(int j = 0; j < 64; j++){
                a[64 * i + j] = s[i].charAt(j) -48;
            }
        }
        return a;
    }

    /**
     * Reads in the vector from stdin, given that it is represented as a long
     * and that the size of the vector is atleast 64 and a power of two.
     * @param input determines whether should be read from file or stdin, or another InputStream
     * @return the bitvector stored in an array of longs
     */
    static long[] readLongs(InputStream input) {
        long[] a;
        try {
            br = new BufferedReader(new InputStreamReader(input));
            String[] s = br.readLine().split(" ");
            a = new long[s.length];

            for (int i = 0; i < s.length; i++)
                a[i] = Long.parseLong(s[i],10);

        } catch (IOException e) {
            System.err.println("Something went wrong when reading it in as longs");
            a = new long[]{-1};
        }
        return a;
    }

    /**
     * Runs the Rank-select on the given operations specified in the input.
     * @param rs the Rank-select data structure that should read the input 
     */
    static void runOp(RankSelect rs) {
        try {
            String line = "";
            while( (line = br.readLine()) != null){
                String[] input = line.split(" ");
                String op = input[0];
                int num = Integer.parseInt(input[1]);
                
                if(op.startsWith("R")){
                    System.out.println(rs.rank(num));
                }
                else{
                    System.out.println(rs.select(num));
                }
            }
        } catch (IOException e) {
            System.err.println("Something went wrong when reading the operations");
        }
    }

    /**
     * Runs the Rank-select on the given operations specified in the input.
     * @param rs the Rank-select data structure that should read the input
     * @param input determines whether the input should be read from file, or stdin, or another InputStream
     * @return the results of all operations as a list
     */
    static LinkedList<Integer> readOp(RankSelect rs, InputStream input) {
        LinkedList<Integer> answers = new LinkedList<>();
        try {
            br = new BufferedReader(new InputStreamReader(input));
            br.readLine();
            String line = "";
            while( (line = br.readLine()) != null){
                String[] in = line.split(" ");
                String op = in[0];
                int num = Integer.parseInt(in[1]);
                if(op.startsWith("R")){
                    answers.add(rs.rank(num));
                }
                else{
                    answers.add(rs.select(num));
                }
            }
        } catch (IOException e) {
            System.err.println("Something went wrong when reading the operations");
        }
        return answers;
    }

    /**
     * Reads in all the expected answers and returns them in a List.
     * @param input determines whether it should be read from file, or stdin, or another InputStream
     * @return the correct answers to all operations specified in a .ans file
     */
    static LinkedList<Integer> readAnswers(InputStream input) {
        LinkedList<Integer> answers = new LinkedList<>();
        try {
            br = new BufferedReader(new InputStreamReader(input));
            String line = "";
            while( (line = br.readLine()) != null){
                String[] in = line.split(" ");
                int num = Integer.parseInt(in[0]);
                answers.add(num);
            }
        } catch (IOException e) {
            System.err.println("Something went wrong when reading the operations");
        }
        return answers;
    }
}