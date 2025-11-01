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
        //System.out.println(sol.paths);
        return sol;
    }

    public HashMap<Integer, ArrayList<Integer>> optimalPaths(ArrayList<Client> clients) {
        //-------------------------------
        ArrayList<Client> copyClient = new ArrayList<>(clients);
        HashMap<Integer, Integer> optimalPaths = Traversals.bfs(this.graph, this.clients);
        ArrayList<Client> sorted = new ArrayList<>();
        copyClient.sort(Comparator.comparingInt(client -> 100 - (int)client.alpha));
//        while (!copyClient.isEmpty()) {
//            double maxVal = -1;
//            Client maxClient = null;
//            for (Client client : copyClient) {
//                double currentVal = (client.payment * 1000) + ((100 - (int)client.alpha) * 1000000) + (optimalPaths.get(client.id));
//                if (maxVal == -1){
//                    maxClient = client;
//                    maxVal = currentVal;
//                }
//                else{
//                    if (maxVal < currentVal){
//                        maxClient = client;
//                        maxVal = currentVal;
//                    }
//                }
//            }
//            sorted.add(maxClient);
//            copyClient.remove(maxClient);
//        }
        //----------------------------------------------
        //PERFORM A BFS FOR PATH TO EACH NODE, KEEP TRACK OF BANDWIDTH OF EACH NODE, CANNOT USE IF MAX B > USED B
        return makePaths(copyClient);
    }

    public HashMap<Integer, ArrayList<Integer>> makePaths(ArrayList<Client> sortedClients) {
        HashMap<Integer, ArrayList<Integer>> shortestPaths = new HashMap<>();
        HashMap<Integer, HashMap<Integer, Integer>> usages = new HashMap<>(); //FIRST INDEX = TIME STEP, SECOND = INDEX TO USAGE
        int index = 0;
        for (Client client : sortedClients) {
            ArrayList<Integer> path = onePath(client, usages); //PATH IS IDS IN ORDER OF TIME STEP

            for (int i = 0; i < path.size(); i++) {
                int j = index +i;
                int node = path.get(i);
                //getOrDefault Learned from JAVA
                // https://docs.oracle.com/en/java/javase/25/docs/api/java.base/java/util/HashMap.html
                HashMap<Integer, Integer> temp = usages.getOrDefault(j, new HashMap<>());
                temp.put(node,temp.getOrDefault(node,0)+1);
                usages.put(j, temp);
            }
            index+= path.size();

            shortestPaths.put(client.id, path);
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
            return Integer.compare(this.priority, o.priority);
        }

    }

    //GIVEN: A CLIENT & PRIOR CLIENTS' USED NODES
    //RETURN: THE BFS FOR THIS CLIENT
    public ArrayList<Integer> onePath(Client client, HashMap<Integer, HashMap<Integer, Integer>> usages){
        Queue<PriorityItem> searchQueue = new LinkedList<>();
        HashMap<Integer, Integer> priors = new HashMap<>();
        searchQueue.add(new PriorityItem(graph.contentProvider, 0));
        priors.put(graph.contentProvider, -1);
        boolean found = false;
        while (!searchQueue.isEmpty() && !found) {
            PriorityItem item = searchQueue.remove();
            int node = item.id;
            for (int neighbor : this.graph.get(node)){
                HashMap<Integer, Integer> interm = usages.getOrDefault(item.priority+1, new HashMap<>());
                int nCount = interm.getOrDefault(neighbor, 0);
                if (nCount < this.bandwidths.get(neighbor) && !priors.containsKey(neighbor)) {
                    priors.put(neighbor, node);
                    searchQueue.add(new PriorityItem(neighbor, item.priority+1));
                    if (neighbor == client.id){
                        found = true;
                        break;
                    }
                }
            }
        }


        //BACKPATH GENERATOR
        ArrayList<Integer> clientPath = new ArrayList<>();
        System.out.println(usages);
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
