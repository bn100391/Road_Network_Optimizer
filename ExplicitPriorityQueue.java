import java.util.*;

public interface ExplicitPriorityQueue<P extends Comparable<? super P>, V> {
  public V remove() throws NoSuchElementException;

  public P minPriority() throws NoSuchElementException;

  public V minValue() throws NoSuchElementException;

  public void add(P p, V v);

  public int size();

  public boolean isEmpty();
  
}
