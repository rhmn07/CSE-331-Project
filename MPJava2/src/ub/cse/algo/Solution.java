package ub.cse.algo;

import java.sql.SQLOutput;
import java.util.*;

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

        sol.paths = optimalPaths(this.clients);
        System.out.println(sol.paths);
        return sol;
    }

    public HashMap<Integer, ArrayList<Integer>> optimalPaths(ArrayList<Client> clients) {
        //-------------------------------
        ArrayList<Client> copyClient = new ArrayList<>(clients);
        HashMap<Integer, Integer> optimalPaths = Traversals.bfs(this.graph, this.clients);
        ArrayList<Client> sorted = new ArrayList<>();
        while (!copyClient.isEmpty()) {
            double maxVal = -1;
            Client maxClient = null;
            for (Client client : copyClient) {
                double currentVal = (client.payment*1000) + (client.alpha * 1000000) + optimalPaths.get(client.id);
                if (maxVal == -1){
                    maxClient = client;
                    maxVal = currentVal;
                }
                else{
                    if (maxVal < currentVal){
                        maxClient = client;
                        maxVal = currentVal;
                    }
                }
            }
            sorted.add(maxClient);
            copyClient.remove(maxClient);
        }
        //----------------------------------------------
        //PERFORM A BFS FOR PATH TO EACH NODE, KEEP TRACK OF BANDWIDTH OF EACH NODE, CANNOT USE IF MAX B > USED B
        return makePaths(sorted);
    }

    public HashMap<Integer, ArrayList<Integer>> makePaths(ArrayList<Client> sortedClients) {
        HashMap<Integer, ArrayList<Integer>> pathList = new HashMap<>();
        HashMap<Integer, Integer> currBans = new HashMap<>();
        //IDK another way to get all the keys, I don't know Java very well - Kyler
        Set<Integer> keys = this.graph.keySet();
        for (Integer key : keys) {
            currBans.put(key, 0);
        }
        //REWORKED TRAVERSALS BFS FOR ONLY ONE CLIENT & ACCOUNTING FOR BANDWIDTHS
        for (int i = 0; i < sortedClients.size(); i++) { //FOR EACH ALPHA VALUE//FOR EACH CLIENT, STARTING WITH HIGHEST PAYING
                Client client = sortedClients.get(i);

                Queue<Integer> searchQueue = new LinkedList<>();
                HashMap<Integer, Integer> priors = new HashMap<>();
                searchQueue.add(graph.contentProvider);
                priors.put(graph.contentProvider, -1);
                boolean found = false;

                //SINGLE NODE BFS (HEAVILY BASED OFF OF Traversals.bfsPaths())
                while (!searchQueue.isEmpty() && !found) {
                    int node = searchQueue.poll();
                    for (int neighbor : graph.get(node)) {
                        if (!priors.containsKey(neighbor) && currBans.get(neighbor) < this.bandwidths.get(neighbor)) {
                            priors.put(neighbor, node);
                            searchQueue.add(neighbor);
                            if (neighbor == client.id) {
                                found = true;
                                break;
                            }
                        }
                    }
                }
                //BACKPATH GENERATOR
                ArrayList<Integer> clientPath = new ArrayList<>();
                if (found) {
                    Integer current = client.id;
                    while (current != -1) {
                        clientPath.add(0, current);
                        current = priors.get(current);
                    }
                    for (int c : clientPath) {
                        currBans.replace(c, currBans.get(c) + 1);
                    }
                }
                pathList.put(client.id, clientPath);
            }
        return pathList;
    }
}
