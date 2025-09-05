import java.util.List;
import java.util.Map;
import java.util.Set;

public interface DirectedWeightedGraph<V> {
  public void addVertex(V v) throws IllegalArgumentException;

  public void removeVertex(V v);

  public DirectedWeightedEdge<V> addEdge(V from, V to, double weight);

  public List<V> getAdjacents(V v);

  public Set<V> getVertices();

  public Set<DirectedWeightedEdge<V>> getEdges();

  public int order();

  public int size();

  public Set<V> getReachable(V start, double distance);

  public GraphPath<V> getShortestPath(V start, V finish);

  public Map<V, GraphPath<V>> getShortestPaths(V start);

  public List<V> byDegree();

  public Map<V, Integer> degrees();
}
