package part1;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;

/**
 * Reads and formats the input needed for part1.
 */
public class InputReader{

    static private BufferedReader br;

    /**
     * Reads in an int from stdin. Normally the size of the set.
     * @return the int
     */
    public static int readInt(){
        try{
            br = new BufferedReader(new InputStreamReader(System.in));
            int n = Integer.parseInt(br.readLine());
            return n;
        } catch (IOException e){
            System.err.println("Something went wrong when reading in n.");
            return -1;
        }
    }
    
    /**
     * Reads in all the elements contained in the set from stdin.
     * @return the elements as an array
     */
    public static int[] readElems(){
        try{
            if(br==null){
                br = new BufferedReader(new InputStreamReader(System.in));
            }
            int[] elems = Arrays.stream(br.readLine().split(" "))
                            .mapToInt(Integer::parseInt)
                            .toArray();
            return elems;
        } catch (IOException e){
            System.err.println("Something went wrong when reading in the elements of the Skewed Binary Tree.");
            return new int[]{-1};
        }
    }

    /**
     * Reads from std all the operations to be performed on the Skewed Binary Tree (sbst).
     * @param st the sbst to be checked.
     */
    public static void runOp(SkewedBST st){
        try{
            if(br==null){
                br = new BufferedReader(new InputStreamReader(System.in));
            }
            String[] queries = br.readLine().split(" ");
            StringBuilder sb = new StringBuilder();
            for(int i = 0 ; i < queries.length;i++){
                sb.append(st.Pred(Integer.parseInt(queries[i])));
            }
            System.out.println(sb);
        } catch (IOException e){
            System.err.println("Something went wrong when reading the queries.");
        }
    }
}