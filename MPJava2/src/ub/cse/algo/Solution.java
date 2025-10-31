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
        sorted.sort(Comparator.comparingInt(c -> (int)c.alpha));
        while (!copyClient.isEmpty()) {
            double maxVal = -1;
            Client maxClient = null;
            for (Client client : copyClient) {
                double currentVal = (client.payment * 1000) + ((100 - (int)client.alpha) * 1000000) + (optimalPaths.get(client.id));
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
        HashMap<Integer, ArrayList<Integer>> shortestPaths = new HashMap<>();
        HashMap<Integer, ArrayList<Integer>> usages = new HashMap<>();

        for (Client client : sortedClients) {
            ArrayList<Integer> path = onePath(client, usages);
            shortestPaths.put(client.id, path);
            usages.put(client.id, path);
            //System.out.println(sortedClients.size() - shortestPaths.size());
        }
        return shortestPaths;
    }

    public class PriorityItem implements Comparable<PriorityItem> {
        int priority;
        int id;
        public PriorityItem(int id, int priority) {
            this.id = id;
            this.priority = priority;
        }

        @Override
        public int compareTo(PriorityItem o) {
            if (this.priority > o.priority) {
                return 1;
            }
            return 0;
        }

    }

    //GIVEN: A CLIENT & PRIOR CLIENTS' USED NODES
    //RETURN: THE BFS FOR THIS CLIENT
    public ArrayList<Integer> onePath(Client client, HashMap<Integer, ArrayList<Integer>> usages){
        Queue<PriorityItem> searchQueue = new PriorityQueue<>();
        HashMap<Integer, Integer> priors = new HashMap<>();
        searchQueue.add(new PriorityItem(graph.contentProvider, 0));
        priors.put(graph.contentProvider, -1);
        boolean found = false;
        while (searchQueue.size() > 0 && !found) {
            PriorityItem item = searchQueue.poll();
            int node = item.id;
            for (int neighbor : this.graph.get(node)){
                int nCount = 0;
                for (ArrayList<Integer> list : usages.values()) {
                    if (list.size() > item.priority && list.get(item.priority) == neighbor){
                        nCount++;
                    }
                }
                if (nCount < this.bandwidths.get(neighbor) && !priors.containsKey(neighbor)) {
                    priors.put(neighbor, node);
                    searchQueue.add(new PriorityItem(neighbor, item.priority+1));
                }
                if (neighbor == client.id){
                    found = true;
                    break;
                }
            }
        }


        //BACKPATH GENERATOR
        ArrayList<Integer> clientPath = new ArrayList<>();
        if (found) {
            int current = client.id;
            while (current != -1) {
                clientPath.add(0, current);
                current = priors.get(current);
            }
        }
        return clientPath;
    }
}
