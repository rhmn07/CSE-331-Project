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

        //System.out.println(this.clients);
        sol.paths = optimalPaths(this.clients);
        //System.out.println(sol.paths);
        return sol;
    }

    public HashMap<Integer, ArrayList<Integer>> optimalPaths(ArrayList<Client> clients) {
        //START OF MAHIR'S SECTION
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
            } else {
                i = (int) client.alpha;
            }
            alphaSort.get(i).add(client);
        }
        // END OF SECTION
        for (int i = 0; i < alphaSort.size(); i++) {
            if (alphaSort.get(i).size() > 0) {
                alphaSort.set(i, sortClientsByAlpha(alphaSort.get(i)));
            }
        }
        //PERFORM A BFS FOR PATH TO EACH NODE, KEEP TRACK OF BANDWIDTH OF EACH NODE, CANNOT USE IF MAX B > USED B

        return makePaths(alphaSort);
    }

    public ArrayList<Client> sortClientsByAlpha(ArrayList<Client> clients) {
        ArrayList<Client> sortedClients = new ArrayList<>();
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

    public HashMap<Integer, ArrayList<Integer>> makePaths(ArrayList<ArrayList<Client>> sortedClients) {
        HashMap<Integer, ArrayList<Integer>> pathList = new HashMap<>();
        HashMap<Integer, Integer> currBans = new HashMap<>();
        Set<Integer> keys = this.graph.keySet();
        for (Integer key : keys) {
            currBans.put(key, 0);
        }

        //System.out.println(currBans);


        //REWORKED TRAVERSALS BFS FOR ONLY ONE CLIENT & ACCOUNTING FOR BANDWIDTHS
        for (int i = 0; i < sortedClients.size(); i++) { //FOR EACH ALPHA VALUE
            for (int j = 0; j < sortedClients.get(i).size(); j++) { //FOR EACH CLIENT, STARTING WITH HIGHEST PAYING
                Client client = sortedClients.get(i).get(j);

                Queue<Integer> searchQueue = new LinkedList<>();
                HashMap<Integer, Integer> priors = new HashMap<>();
                searchQueue.add(graph.contentProvider);
                priors.put(graph.contentProvider, -1);
                boolean found = false;

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

                if (found) {
                    ArrayList<Integer> clientPath = new ArrayList<>();
                    Integer current = client.id;
                    while (!current.equals(null) && current != -1) {
                        clientPath.add(0, current);
                        current = priors.get(current);
                    }
                    for (int c : clientPath) {
                        currBans.replace(c, currBans.get(c) + 1);
                    }
                    pathList.put(client.id, clientPath);
                    //OTHERWISE, WE DITCH THIS CLIENT
                }
            }
        }
        return pathList;
    }
}
