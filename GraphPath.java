import java.util.List;

public interface GraphPath<V> {
  public List<V> getPath();

  public double getCost();
}

