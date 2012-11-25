package mint;

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/**
 *
 * @author Oliver Chu
 */
public class SmartList<E> extends AbstractList<E> {
    private ArrayList<E> list;
    private Subprogram sub;
    
    public SmartList() {
        list = new ArrayList<E>();
        sub = null;
    }
    
    public SmartList(Subprogram s) {
        list = new ArrayList<E>();
        sub = s;
    }
    
    private void checkBounds(int i) {
        // Note: do not use i >= list.size() or else you will get random
        // error messages that do not apply.
        if (i < -list.size() || i > list.size()) {
            System.err.println("List index out of bounds: Index " + i +
                               ", List: " + list);
        }
    }
    
    public SmartList(E[] array) {
        list = new ArrayList<E>();
        list.addAll(Arrays.asList(array));
    }
    
    public SmartList(List<E> otherList) {
        list = new ArrayList<E>();
        list.addAll(otherList);
    }
    
    public SmartList(Collection<E> collection) {
        list = new ArrayList<E>();
        list.addAll(collection);
    }
    
    @Override
    public E get(int i) {
        if (sub != null) {
            SmartList<Pointer> args = new SmartList<Pointer>();
            args.add(Heap.allocateInt(i));
            try {
                return (E)sub.execute(new Environment(),
                              new SmartList<String>(), args, new Interpreter());
            } catch (MintException ex) {
                System.err.println(ex.getMessage());
            }
        }
        checkBounds(i);
        if (i < 0)
            i += list.size();
        return list.get(i);
    }
    
    @Override
    public E set(int i, E value) {
        checkBounds(i);
        if (i < 0)
            i += list.size();
        return list.set(i, value);
    }
    
    public void prependAll(SmartList<E> lst) {
        ArrayList<E> oldList = list;
        list = new ArrayList<E>();
        list.addAll(lst);
        list.addAll(oldList);
    }
    
    @Override
    public boolean add(E value) {
        return list.add(value);
    }
    
    @Override
    public void add(int i, E value) {
        list.add(i, value);
    }
    
    @Override
    public boolean isEmpty() {
        return list.isEmpty();
    }
    
    @Override
    public int size() {
        if (sub != null) {
            return Integer.MAX_VALUE;
        }
        return list.size();
    }
    
    @Override
    public boolean contains(Object value) {
        return list.contains(value);
    }
    
    @Override
    public E remove(int i) {
        return list.remove(i);
    }
    
    public SmartList<E> reverse() {
        ArrayList<E> oldList = list;
        list = new ArrayList<E>();
        for (int i = oldList.size() - 1; i >= 0; i--) {
            list.add(oldList.get(i));
        }
        return this;
    }
    
    public int find(E e) {
        int i = 0;
        for (E item : list) {
            if (item.equals(e))
                return i;
            i++;
        }
        return -1;
    }
    
    /** Removes the section of this list from index start to index end - 1,
     * and replaces it with a new section 'sublist'.
     */
    public void assignSublist(int start, int end, SmartList<E> sublist) {
        ArrayList<E> oldList = list;
        list = new ArrayList<E>();
        for (int i = 0; i < start; i++) {
            list.add(oldList.get(i));
        }
        for (E element : sublist) {
            list.add(element);
        }
        for (int i = end; i < oldList.size(); i++) {
            list.add(oldList.get(i));
        }
    }
    
    public void assignSublist(int start, int end, E value) {
        SmartList<E> sublist = new SmartList<E>();
        sublist.add(value);
        assignSublist(start, end, sublist);
    }
    
    public E pop() {
        return list.remove(list.size() - 1);
    }
    
    public SmartList<E> subList(int start) {
        return new SmartList<E>(subList(start, size()));
    }
    
    @Override
    public List<E> subList(int fromIndex, int toIndex) {
        if (fromIndex < 0)
            fromIndex += list.size();
        if (toIndex < 0)
            toIndex += list.size();
        return list.subList(fromIndex, toIndex);
    }
    
    @Override
    public String toString() {
        if (sub != null) {
            ArrayList<E> lst = new ArrayList<E>();
            for (int i = 0; i < 10; i++) {
                lst.add(get(i));
            }
            String x = lst.toString();
            x = StrTools2.slice(x, 0, -1) + ", ...]";
            return x;
        }
        return list.toString();
    }
}
