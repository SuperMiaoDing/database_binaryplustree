/**
 * Xiaowen Ding
 * This file is implementation of node
 */

import java.util.ArrayList;
import java.io.Serializable;
import java.util.List;

public class Node<E> implements Serializable{

    private static final long serialVersionUID = -5809782578272943999L;

    private Integer id;



    private ArrayList<E> keys;
    private List<Integer> pointers;
    private Integer next;
    private Integer prev;
    boolean isLeaf;
    int nodeSize;


    public Node(int id, int nodeSize, boolean isLeaf) {
        super();
        this.id = id;
        keys = new ArrayList<E>();
        pointers = new ArrayList<Integer>();
        this.nodeSize = nodeSize;
        this.isLeaf = isLeaf;
    }

    public ArrayList<E> getKeys() {
        return keys;
    }

    public void setKeys(ArrayList<E> keys) {
        this.keys = keys;
    }

    public List<Integer> getPointers() {
        return pointers;
    }

    public void setPointers(List<Integer> pointers) {
        this.pointers = pointers;
    }

    public Integer getNext() {
        return next;
    }

    public void setNext(Integer next) {
        this.next = next;
    }

    public Integer getPrev() {
        return prev;
    }

    public void setPrev(Integer prev) {
        this.prev = prev;
    }

    public boolean isLeaf() {
        return isLeaf;
    }

    public void setLeaf(boolean isLeaf) {
        this.isLeaf = isLeaf;
    }

    public int getNodeSize() {
        return nodeSize;
    }

    public void setNodeSize(int nodeSize) {
        this.nodeSize = nodeSize;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

}
