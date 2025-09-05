import java.util.*;

@SuppressWarnings("rawtypes")
public class ExplicitHeapPriorityQueue<P extends Comparable<? super P>, V> implements ExplicitPriorityQueue<P, V> {
    List<pEntry> data;
    Map<V, Integer> indicies;

    class pEntry implements Map.Entry<P, V>, Comparable<pEntry> {
        private P priority;
        private V value;

        public pEntry(P p, V v) {
            this.priority = p;
            this.value = v;
        }

        @Override
        public int compareTo(pEntry o) {
            return this.priority.compareTo(o.getKey());
        }

        @Override
        public P getKey() {
            return priority;
        }

        @Override
        public V getValue() {
            return value;
        }

        @Override
        public V setValue(V value) {
            V temp = this.value;
            this.value = value;
            return temp;
        }
    }


    public ExplicitHeapPriorityQueue() {
        data = new ArrayList<pEntry>();
        indicies = new HashMap<V, Integer>();
        pEntry entry = new pEntry(null, null);
        data.add(entry);
    }


    @SuppressWarnings("unchecked")
    public P removeCost() {
        if (this.isEmpty()) {
            return (P) Double.valueOf(0.0);
        }
        pEntry firstEntry = data.get(1);
        return firstEntry.getKey();
    }


    @Override
    public V remove() throws NoSuchElementException {
        if (this.isEmpty()) {
            return null;
        }
        pEntry firstEntry = data.get(1);
        Collections.swap(data, 1, (data.size() - 1));
        data.remove(data.size() - 1);
        bubbleDown(1);
        return firstEntry.getValue();
    }


    public void bubbleDown(int n) {
        int child = 2 * n;
        if (child < data.size() - 1 && (data.get(child + 1).compareTo(data.get(child)) < 0)) {
            ++child;
        }

        if (child < data.size() && (data.get(n).compareTo(data.get(child))) > 0) {
            pEntry currEntry = data.get(n);
            pEntry childEntry = data.get(child);
            indicies.put(currEntry.getValue(), Integer.valueOf(child));
            indicies.put(childEntry.getValue(), Integer.valueOf(n));
            Collections.swap(data, n, child);
            bubbleDown(child);
        }
    }


    @Override
    public P minPriority() throws NoSuchElementException {
        if (data.get(1).getKey() == null) {
            throw new NoSuchElementException();
        } else {
            return data.get(1).getKey();
        }
    }


    @Override
    public V minValue() throws NoSuchElementException {
        if (data.get(1).getValue() == null) {
            throw new NoSuchElementException();
        } else {
            return data.get(1).getValue();
        }
    }


    @Override
    @SuppressWarnings("unchecked")
    public void add(Comparable p, Object v) {
        pEntry entry = new pEntry((P) p, (V) v);
        indicies.put((V) v, Integer.valueOf(data.size() + 1));
        data.add(entry);
        int index = data.size() - 1;
        int indexOfParent = index / 2;
        if (data.size() > 2) {
            P currPriority = entry.getKey();
            pEntry parentEntry = data.get(indexOfParent);
            P parentPriority = parentEntry.getKey();

            while (currPriority.compareTo(parentPriority) < 0) {
                indicies.put(entry.getValue(), indexOfParent);
                indicies.put(parentEntry.getValue(), index);
                Collections.swap(data, index, indexOfParent);
                index = indexOfParent;
                indexOfParent = indexOfParent / 2;
                if (indexOfParent == 0) {
                    break;
                }
            }
        }
    }


    @Override
    public int size() {
        return data.size() - 1;
    }


    @Override
    public boolean isEmpty() {
        if (data.size() <= 1) {
            return true;
        } else {
            return false;
        }
    }


    public Double getPriority(V v) {
        Integer index = indicies.get(v);
        if (index >= data.size()) {
            return 0.0;
        }
        return (Double) data.get(index).getKey();
    }
}
