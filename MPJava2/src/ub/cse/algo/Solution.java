package ub.cse.algo;

import java.sql.SQLOutput;
import java.util.ArrayList;
import java.util.Comparator;
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
        for (int i = 0; i < this.clients.size(); i++) {
            sol.priorities.put(i, this.clients.get(i).priority);
        }
        HashMap<Integer, ArrayList<Integer>> shortestPath = Traversals.bfsPaths(this.graph, this.clients);

        //System.out.println(this.clients);
        sol.paths = optimalPaths(this.clients);




        //System.out.println(sol.priorities);
        //System.out.println(sol.bandwidths);
        //System.out.println(sol.paths);
        return sol;
    }

    public HashMap<Integer, ArrayList<Integer>> optimalPaths(ArrayList<Client> clients) {
        HashMap<Integer, ArrayList<Integer>> pathList = new HashMap<>();


        int maxAlpha = 0;
        for (Client client : clients) {
            if (!Float.isInfinite(client.alpha)) {
                maxAlpha = Math.max(maxAlpha, (int) client.alpha);
            }
        }
        ArrayList<ArrayList<Client>> alphaSort = new ArrayList<>();
        for (int i = 0; i <= maxAlpha; i++) {
            alphaSort.add(new ArrayList<>());
        }
        for (Client client : clients) {
            int i = 0;
            if (Float.isInfinite(client.alpha)) {
                i = maxAlpha;
            }
            else{
                i = (int)  client.alpha;
            }
            alphaSort.get(i).add(client);
        }

        for (int i = 0; i < alphaSort.size(); i++) {
            alphaSort.set(i, sortClientsByAlpha(alphaSort.get(i)));
        }


        //PERFORM A BFS FOR PATH TO EACH NODE, KEEP TRACK OF BANDWIDTH OF EACH NODE, CANNOT USE IF MAX B > USED B

        return pathList;
    }

    public ArrayList<Client> sortClientsByAlpha(ArrayList<Client> clients) {
        ArrayList<Client> sortedClients = new ArrayList<>(clients);
        while (clients.size() > 1) {
            Client max = clients.getFirst();
            for (int i = 1; i < clients.size() - 1; i++) {
                if (max.payment < clients.get(i).payment) {
                    max = clients.get(i);
                }
            }
            sortedClients.add(max);
            clients.remove(max);
        }
        return sortedClients;
    }
}
