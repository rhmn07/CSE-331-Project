package ub.cse.algo;

import java.util.ArrayList;
import java.util.HashMap;

public class Solution {

    private Info info;
    private Graph graph;
    private ArrayList<Client> clients;
    private ArrayList<Integer> bandwidths;

    /**
     * Basic Constructor
     *
     * @param info: data parsed from input file
     */
    public Solution(Info info) {
        this.info = info;
        this.graph = info.graph;
        this.clients = info.clients;
        this.bandwidths = info.bandwidths;
    }

    /**
     * Method that returns the calculated 
     * SolutionObject as found by your algorithm
     *
     * @return SolutionObject containing the paths, priorities and bandwidths
     */
    public SolutionObject outputPaths() {
        SolutionObject sol = new SolutionObject();
        /* TODO: Your solution goes here */
        sol.bandwidths = this.bandwidths;
        sol.priorities = new HashMap<Integer, Integer>(this.graph.size());
        for (int i = 0; i < this.graph.size(); i++) {
            sol.priorities.put(i, 0);
        }

        HashMap<Integer, ArrayList<Integer>> shortestPath = Traversals.bfsPaths(this.graph, this.clients);


        //System.out.println(sol.priorities);
        //System.out.println(sol.bandwidths);
        //System.out.println(sol.paths);
        return sol;
    }
}
