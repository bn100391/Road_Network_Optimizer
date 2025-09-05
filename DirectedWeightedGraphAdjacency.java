import java.util.*;

public class DirectedWeightedGraphAdjacency<V>
    implements DirectedWeightedGraph<V> {
    private Map<V, List<DirectedWeightedEdge<V>>> adjacencies;
    private int vertexCount, edgeCount;

    public DirectedWeightedGraphAdjacency() {
        adjacencies = new HashMap<V, List<DirectedWeightedEdge<V>>>();
    }


    class Edge<V> implements DirectedWeightedEdge<V> {
        private V from;
        private V to;
        private double weight;

        public Edge(V from, V to, double weight) {
            this.from = from;
            this.to = to;
            this.weight = weight;
        }

        @Override
        public V getFrom() {
            return from;
        }


        public V getTo() {
            return to;
        }


        public double getWeight() {
            return weight;
        }

    }

    class Path<V> implements GraphPath<V> {
        private List<V> path;
        private double cost;

        public Path(ArrayList<V> path, double cost) {
            this.path = path;
            this.cost = cost;
        }

        @Override
        public List<V> getPath() {
            return path;
        }

        @Override
        public double getCost() {
            return cost;
        }

        public void addToPath(V v) {
            path.add(v);
        }

    }

    public DirectedWeightedEdge<V> getEdge(V from, V to) {
        List<DirectedWeightedEdge<V>> edgeList = adjacencies.get(from);
        for (DirectedWeightedEdge<V> edge : edgeList) {
            if (edge.getTo().equals(to)) {
                return edge;
            }
        }
        return null;
    }


    @Override
    public void addVertex(V v) throws IllegalArgumentException {
        if (v == null) {
            throw new IllegalArgumentException();
        }
        if (adjacencies.containsKey(v)) {
            return;
        } else {
            ArrayList<DirectedWeightedEdge<V>> newList = new ArrayList<DirectedWeightedEdge<V>>();
            adjacencies.put(v, newList);
        }
    }

    @Override
    public void removeVertex(V v) {
        for (V node : adjacencies.keySet()) {
            (adjacencies.get(node)).remove(v);
        }
        adjacencies.remove(v);
        vertexCount--;
    }


    @Override
    public DirectedWeightedEdge<V> addEdge(V from, V to, double weight) {
        if (!adjacencies.containsKey(from) || !adjacencies.containsKey(to)) {
            return null;
        }
        Edge<V> newEdge = new Edge<V>(from, to, weight);
        (adjacencies.get(from)).add(newEdge);
        edgeCount++;

        return newEdge;
    }


    @Override
    public List<V> getAdjacents(V v) {
        ArrayList<V> a = new ArrayList<V>();
        for (DirectedWeightedEdge<V> item : adjacencies.get(v)) {
            a.add(item.getTo());
        }
        return a;
    }


    @Override
    public Set<V> getVertices() {
        return adjacencies.keySet();
    }


    @Override
    public Set<DirectedWeightedEdge<V>> getEdges() {
        Collection<List<DirectedWeightedEdge<V>>> edgesCollection = adjacencies.values();
        HashSet<DirectedWeightedEdge<V>> edgeSet = new HashSet<DirectedWeightedEdge<V>>();
        for (List<DirectedWeightedEdge<V>> list : edgesCollection) {
            edgeSet.addAll(list);
        }
        return edgeSet;
    }


    @Override
    public int order() {
        return vertexCount;
    }


    @Override
    public int size() {
        return edgeCount;
    }


    @Override
    public Set<V> getReachable(V start, double distance) {
        Set<V> validEdges = new HashSet<V>();
        List<DirectedWeightedEdge<V>> list = adjacencies.get(start);
        for (DirectedWeightedEdge<V> edge : list) {
            if (edge.getWeight() <= distance) {
                validEdges.add(edge.getFrom());
            }
        }
        return validEdges;
    }


    @Override
    public GraphPath<V> getShortestPath(V start, V finish) {
        Map<V, GraphPath<V>> shortestPaths = getShortestPaths(start);
        return shortestPaths.get(finish);
    }


    @SuppressWarnings({"rawtypes", "unchecked"})
    @Override
    public Map<V, GraphPath<V>> getShortestPaths(V start) {
        Set<V> known = new HashSet<V>(); 
        Map<V, V> from = new HashMap<V, V>();
        ExplicitHeapPriorityQueue<Double, V> dist = new ExplicitHeapPriorityQueue<Double, V>();
        Map<V, GraphPath<V>> result = new HashMap<V, GraphPath<V>>();
        for (V v : getVertices()) {
            dist.add(Double.POSITIVE_INFINITY, v);
        }

        dist.add(0.0, start);

        while (true) {
            V v = dist.remove();
            if (v == null) {
                break;
            }
            known.add(v);
            dist.remove(); 

            for (V w : getAdjacents(v)) {
                if (!known.contains(w)) {
                    DirectedWeightedEdge<V> e = getEdge(v, w);
                    double cost = e.getWeight();
                    if (dist.getPriority(v) + cost < dist.getPriority(w)) { 
                        dist.add(dist.getPriority(v) + cost, w);
                        from.put(w, v);
                    }
                }
            }
        }

        Path<V> path;
        ArrayList<Edge> edges;
        Set<V> alreadyAdded = new HashSet<V>();
        boolean empty = false;
        while (!empty) {
            Double cost = dist.removeCost();
            V vertice = dist.remove();
            if (alreadyAdded.contains(vertice)) {
                continue;
            }
            edges = new ArrayList<Edge>();
            path = new Path(edges, cost);

            V fromNode = from.get(vertice);
            while (fromNode != start) {
                path.addToPath(fromNode);
                fromNode = from.get(fromNode);
            }

            result.put(vertice, path);
            if (dist.remove() == null) {
                empty = true;
            }
        }

        return result;
    }


    @Override
    public List<V> byDegree() {
        Map<V, Integer> graphMap = degrees();
        List<Map.Entry<V, Integer>> list =
                new ArrayList<Map.Entry<V, Integer>>(graphMap.entrySet());

        for (int i = 0; i < list.size(); i++) {
            for (int j = 0; j < list.size() - 1; j++) {
                if (list.get(i).getValue() < list.get(j).getValue()) {
                    Map.Entry<V, Integer> temp = list.get(i);
                    list.set(i, list.get(j));
                    list.set(j, temp);
                }
            }
        }

        List<V> v_by_degree = new ArrayList<V>();
        for (Map.Entry<V, Integer> entry : list) {
            v_by_degree.add(entry.getKey());
        }

        return v_by_degree;
    }


    @Override
    public Map<V, Integer> degrees() {
        HashMap<V, Integer> verticeDegree = new HashMap<V, Integer>();
        for (V v : adjacencies.keySet()) {
            verticeDegree.put(v, findDegree(v));
        }
        return verticeDegree;
    }


    public int findDegree(V v) {
        int degree = 0;
        for (V keyV : adjacencies.keySet()) {
            if (v.equals(keyV)) {
                degree++;
            }
        }
        for (List<DirectedWeightedEdge<V>> list : adjacencies.values()) {
            for (DirectedWeightedEdge<V> item : list) {
                if (item.equals(v)) {
                    degree++;
                }
            }
        }
        return degree;
    }
}
