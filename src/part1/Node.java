package part1;

/**
 * Node that can compared to other nodes.
 */
class Node implements Comparable<Node>{

    int value, weight;
    Node left, right;

    /**
     * Creates the node with a weight of 0 and the specified value.
     * @param value the value contained in the node.
     */
    Node(int value){
        this.value = value;
        weight = 0;
    }

    /**
     * Overloaded compareTo function. Enables comparison of nodes based on
     * their weights.
     * @param n the Node to be compared to
     * @return -1 if this Node is less than n. 1 if this Node is larger than n. 0 if they are equal.
     */
     public int compareTo(Node n){
         if(this.weight < n.weight){
            return - 1;
        }
        else if(this.weight > n.weight){
            return 1;
        }
        else{
            return 0;
        }
    }
}