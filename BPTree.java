/**
 * Xiaowen Ding
 * This file is implementation of B+ tree
 */

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class BPTree<E extends Comparable<E>> {

    private int leafSize;
    private int internalSize;
    private Node<E> root;
    private int rootId;
    private int size;
    private NativeMethods<E> nm;
    private LinkedList<Node<E>> buffer;
    private String dataFile; // original data file
    private int bufferboolSize;
    private String path;
    private int totalNodes = 0;
    private Helper h = null;

    /**
     * The first time we build a index, use this constructor to make a new B+ tree
     * @param leafSize
     * @param internalSize
     * @param bufferbool
     */
    public BPTree(int leafSize, int internalSize, int bufferbool, String path, String dataFile) {
        super();
        this.leafSize = leafSize;
        this.internalSize = internalSize;
        this.root = new Node<E>(++totalNodes, leafSize, true);
        rootId = root.getId();
        nm = new NativeMethods<E>(path);
        buffer = new LinkedList<Node<E>>();
        this.bufferboolSize = bufferbool;
        this.path = path;
        h = new Helper(path);
        h.insertNode(rootId, root);
        this.dataFile = dataFile;
    }

    /**
     * read tree info from index file
     * @param leafSize
     * @param internalSize
     * @param bufferbool
     */
    public BPTree(int leafSize, int internalSize, int bufferbool, int rootId, String path, int totalNodes, String dataFile) {
        super();
        this.leafSize = leafSize;
        this.internalSize = internalSize;
        this.rootId = rootId;
        nm = new NativeMethods<E>(path);
        buffer = new LinkedList<Node<E>>();
        this.bufferboolSize = bufferbool;
        h = new Helper(path);
        this.root = h.readNode(rootId);
        this.totalNodes = totalNodes;
        this.dataFile = dataFile;
    }

    // ======================================================================
    // ===========================INSERTION==================================
    @SuppressWarnings("unchecked")
    public void insertNode(E key, Object data) {
        // stack to hold parent
        LinkedList<Node<E>> stack = new LinkedList<Node<E>>();
        Node<E> n = root;
        //sezrching fo the element
        while (!n.isLeaf) {
            stack.push(n);
            // ===================================================
            if (key.compareTo(n.getKeys().get(0)) < 0) {  // if in first pointer
                n = h.readNode((Integer) n.getPointers().get(0));
            } else if (key.compareTo(n.getKeys().get(n.getKeys().size() - 1)) >= 0) {// if in last pointer
                n = h.readNode((Integer)  n.getPointers().get(n.getPointers().size() - 1));
            } else {
                for (int i = 0; i < n.getKeys().size() - 1; i++) { // general case
                    if (n.getKeys().size() > 1 && key.compareTo(n.getKeys().get(i)) >= 0 && key.compareTo(n.getKeys().get(i + 1)) < 0) {
                        n = h.readNode((Integer)  n.getPointers().get(i + 1));
                        break;
                    }
                }
            }
        }
        // check if the elemnet in the node or not
        for (int i = 0; i < n.getKeys().size(); i++) {
            if (key == n.getKeys().get(i)) {
                return;
            }
        }
        // if node is not full
        if (n.getKeys().size() < leafSize) {
            nm.sortedInsert(key, data, n);
            h.insertNode(n.getId(), n);
        } else {
            ///    spliting two leaf nodes
            // copying all current node contents in temp node then insert the new element on it
            Node<E> temp = new Node(++totalNodes, leafSize, true);
            temp.setKeys(new ArrayList<E>(n.getKeys()));
            temp.setPointers(n.getPointers());
            nm.sortedInsert(key, data, temp);
            Node newNode = new Node(++totalNodes, leafSize, true);
            int j = (int) Math.ceil(n.getPointers().size() / (double) 2);
            //take the first half of the temp nde in current node
            n.setKeys(new ArrayList<E>(temp.getKeys().subList(0, j)));
            n.setPointers(new ArrayList<Integer>(temp.getPointers().subList(0, j)));
            // next and prev
            if (n.getNext() != null) {
                Node nNext = h.readNode(n.getNext());
                nNext.setPrev(newNode.getId());
                h.insertNode(nNext.getId(), nNext);
            }
            newNode.setNext(n.getNext());
            n.setNext(newNode.getId());
            // copying the rest of temp node in new node
            newNode.setPrev(n.getId());
            newNode.setKeys(new ArrayList<E>(temp.getKeys().subList(j, temp.getKeys().size())));
            newNode.setPointers(new ArrayList<Integer>(temp.getPointers().subList(j, temp.getPointers().size())));
            // keeping the key that will be inserting in parent node
            key = temp.getKeys().get(j);
            h.insertNode(n.getId(), n);
            h.insertNode(newNode.getId(), newNode);
            boolean finished = false;
            do {
                // if the parent is null (root case)
                if (stack.isEmpty()) {
                    root = new Node(++totalNodes, internalSize, false);
                    List<Integer> point = new ArrayList<Integer>();
                    point.add(n.getId());
                    point.add(newNode.getId());
                    ArrayList<E> keys_ = new ArrayList<E>();
                    keys_.add(key);
                    root.setKeys(keys_);
                    root.setPointers(point);
                    h.insertNode(root.getId(), root);
                    finished = true;
                } else {
                    // if there's parent
                    n = stack.pop();
                    // if there's no need for splitting internal
                    if (n.getKeys().size() < internalSize) {
                        nm.sortedInsertInternal(key, newNode, n);
                        finished = true;
                    } else {
                        /* splitting two internal nodes by copying them into new node and insert
                        new elemnet in the temp node then divide it betwwen current node and new node
                         */
                        temp.setLeaf(false);
                        temp.setKeys(new ArrayList<E>(n.getKeys()));
                        temp.setPointers(n.getPointers());

                        nm.sortedInsertInternal(key, newNode, temp);
                        newNode = new Node(++totalNodes, internalSize, false);
                        j = (int) Math.ceil(temp.getPointers().size() / (double) 2);

                        n.setKeys(new ArrayList<E>(temp.getKeys().subList(0, j - 1)));
                        n.setPointers(new ArrayList<Integer>(temp.getPointers().subList(0, j)));
                        if (n.getNext() != null) {
                            Node nNext = h.readNode((Integer) n.getNext());
                            nNext.setPrev(newNode.getId());
                            h.insertNode(nNext.getId(), nNext);
                        }
                        newNode.setNext(n.getNext());
                        n.setNext(newNode.getId());
                        newNode.setPrev(n.getId());
                        newNode.setKeys(new ArrayList<E>(temp.getKeys().subList(j, temp.getKeys().size())));
                        newNode.setPointers(new ArrayList<Integer>(temp.getPointers().subList(j, temp.getPointers().size())));

                        key = temp.getKeys().get(j - 1);
                    }
                    h.insertNode(n.getId(), n);
                    h.insertNode(newNode.getId(), newNode);
                }
            } while (!finished);
        }
    }

    // ======================================================================
    // =============================SEARCHING================================
    @SuppressWarnings("unchecked")
    public Object search(E key) {
        // secrhing in buffer array to check if the required
        // element on it or not
        for (int i = 0; i < buffer.size(); i++) {
            ArrayList<E> find = buffer.get(i).getKeys();
            if (find.contains(key)) {
                for (int index = 0; index < find.size(); index++) {
                    if (key.compareTo(find.get(i)) == 0) {
                        return buffer.get(i).getPointers().get(index);
                    }
                }
            }
//            if (find.contains(key)) {
//                return buffer.get(i);
//            }
        }
        // if the elemnet isn't in buffer bool
        Node<E> n = root;
        while (!n.isLeaf) {
            //sezrching fo the element
            if (key.compareTo(n.getKeys().get(0)) < 0) {// if in the first pointer
                n = h.readNode((Integer) n.getPointers().get(0));
            } else if (key.compareTo(n.getKeys().get(n.getKeys().size() - 1)) >= 0) {// if in the last pointer
                n = h.readNode((Integer) n.getPointers().get(n.getPointers().size() - 1));
            } else {
                for (int i = 0; i < n.getKeys().size() - 1; i++) {
                    if (n.getKeys().size() > 1 && key.compareTo(n.getKeys().get(i)) >= 0 && key.compareTo(n.getKeys().get(i + 1)) < 0) {// general case
                        n = h.readNode((Integer) n.getPointers().get(i + 1));
                        break;
                    }
                }
            }
        }
        // adding new node to buffre bool
        for (int i = 0; i < n.getKeys().size(); i++)
            if (key.compareTo(n.getKeys().get(i)) == 0) {
                if (buffer.size() == bufferboolSize) {
                    buffer.removeFirst();
                    buffer.add(n);
                } else {
                    buffer.add(n);
                }
                return n.getPointers().get(i);
            }
        return null;
    }

    public List<Integer> searchRange(E key, int count) {
        List<Integer> range = new ArrayList<Integer>();
        // first position indicate whether we can find this key in tree

        // secrhing in buffer array to check if the required
        // element on it or not
        for (int i = 0; i < buffer.size(); i++) {
            ArrayList<E> find = buffer.get(i).getKeys();
            if (find.contains(key)) {
                for (int index = 0; index < find.size(); index++) {
                    if (key.compareTo(find.get(i)) == 0) {
                        range.add(1);
                        range.addAll(getNNode(key, buffer.get(i), count));
                        return range;
                    }
                }
            }
//            if (find.contains(key)) {
//                return buffer.get(i);
//            }
        }
        // if the elemnet isn't in buffer bool
        Node<E> n = root;
        while (!n.isLeaf) {
            //sezrching fo the element
            if (key.compareTo(n.getKeys().get(0)) < 0) {// if in the first pointer
                n = h.readNode((Integer) n.getPointers().get(0));
            } else if (key.compareTo(n.getKeys().get(n.getKeys().size() - 1)) >= 0) {// if in the last pointer
                n = h.readNode((Integer) n.getPointers().get(n.getPointers().size() - 1));
            } else {
                for (int i = 0; i < n.getKeys().size() - 1; i++) {
                    if (n.getKeys().size() > 1 && key.compareTo(n.getKeys().get(i)) >= 0 && key.compareTo(n.getKeys().get(i + 1)) < 0) {// general case
                        n = h.readNode((Integer) n.getPointers().get(i + 1));
                        break;
                    }
                }
            }
        }
        // adding new node to buffre bool
        for (int i = 0; i < n.getKeys().size(); i++)
            if (key.compareTo(n.getKeys().get(i)) == 0) {
                if (buffer.size() == bufferboolSize) {
                    buffer.removeFirst();
                    buffer.add(n);
                } else {
                    buffer.add(n);
                }
                range.add(1);
                range.addAll(getNNode(key, n, count));
                return range;
            }
//      cant find this key
        range.add(0);
        range.add(getLargerOne(key));
        return range;
    }

    private Integer getLargerOne(E key) {
        LinkedList<Node<E>> stack = new LinkedList<Node<E>>();
        Node<E> n = root;
        //sezrching fo the element
        while (!n.isLeaf) {
            stack.push(n);
            // ===================================================
            if (key.compareTo(n.getKeys().get(0)) < 0) {  // if in first pointer
                n = h.readNode((Integer) n.getPointers().get(0));
            } else if (key.compareTo(n.getKeys().get(n.getKeys().size() - 1)) >= 0) {// if in last pointer
                n = h.readNode((Integer)  n.getPointers().get(n.getPointers().size() - 1));
            } else {
                for (int i = 0; i < n.getKeys().size() - 1; i++) { // general case
                    if (n.getKeys().size() > 1 && key.compareTo(n.getKeys().get(i)) >= 0 && key.compareTo(n.getKeys().get(i + 1)) < 0) {
                        n = h.readNode((Integer)  n.getPointers().get(i + 1));
                        break;
                    }
                }
            }
        }
        for (int i = 0; i < n.getKeys().size(); i++) {
            if (key.compareTo(n.getKeys().get(i)) < 0) {
                return n.getPointers().get(i);
            }
        }
        return null;
    }

    private List<Integer> getNNode(E key, Node<E> goal, int count) {
        List<Integer> list = new ArrayList<Integer>();

        Node<E> n = goal;
        int j = 0;
        boolean flag = false;
        while (n != null) {
            for (int i = 0; i < n.getKeys().size(); i++) {
                if (key.compareTo(n.getKeys().get(i)) == 0) {
                    flag = true;
                    list.add(n.getPointers().get(i));
                    j++;
                } else {
                    if (flag) {
                        if (j == count) {
                            break;
                        } else {
                            list.add(n.getPointers().get(i));
                            j++;
                        }
                    }
                }
            }
            if (j == count) {
                break;
            } else {
                n = h.readNode(n.getNext());
            }
        }
//        list.add(goal);
//        int j = 1;
//        while (j < count) {
//            Integer next = n.getNext();
//            if (next == null) {
//                break;
//            }
//            list.add(next);
//            j++;
//            n = h.readNode(next);
//        }
        return list;
    }


    public int getLeafSize() {
        return leafSize;
    }

    public void setLeafSize(int leafSize) {
        this.leafSize = leafSize;
    }

    public int getInternalSize() {
        return internalSize;
    }

    public void setInternalSize(int internalSize) {
        this.internalSize = internalSize;
    }

    public Node<E> getRoot() {
        return root;
    }

    public void setRoot(Node<E> root) {
        this.root = root;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public String print() {
        String s = "";
        LinkedList<Node<E>> view = new LinkedList<Node<E>>();
        view.add(root);
        while (!view.isEmpty()) {
            Node<E> e = view.pop();
            for (int i = 0; i
                    < e.getKeys().size(); i++) {
                s += (e.getKeys().get(i) + " ");
            }
            for (int i = 0; i < e.getPointers().size(); i++) {
                try {
                    if (!e.isLeaf()) {
                        view.add(h.readNode((Integer) e.getPointers().get(i)));
                    } else {

                    }

                } catch (Exception e1) {
                    e1.printStackTrace();
                }
            }
            s += "\n";
        }
        return s;
    }

    public int getRootId() {
        return rootId;
    }

    public void setRootId(int rootId) {
        this.rootId = rootId;
    }

    public int getBufferboolSize() {
        return bufferboolSize;
    }

    public void setBufferboolSize(int bufferboolSize) {
        this.bufferboolSize = bufferboolSize;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public int getTotalNodes() {
        return totalNodes;
    }

    public void setTotalNodes(int totalNodes) {
        this.totalNodes = totalNodes;
    }

    public String getDataFile() {
        return dataFile;
    }

    public void setDataFile(String dataFile) {
        this.dataFile = dataFile;
    }
}
