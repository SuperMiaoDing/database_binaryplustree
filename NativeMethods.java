/**
 * Xiaowen Ding
 * This file is implementation of operation within a node
 */

import java.util.ArrayList;
import java.util.List;

public class NativeMethods<E extends Comparable<E>> {

    private Helper h = null;

    public NativeMethods(String path) {
        this.h = new Helper(path);
    }

    public void sortedInsert(E key, Object record, Node<E> N) {
        int r = 0;
        if (!N.isLeaf()) {
            r = ((Node) record).getId();
        } else {
            r = (Integer) record;
        }
        ArrayList<E> data = N.getKeys();
        List<Integer> pointers = N.getPointers();
        if (data.isEmpty()) {
            data.add(key);
            pointers.add(r);
            return;
        } else {
            if (key.compareTo(data.get(0)) < 0) {
                data.add(0, key);
                pointers.add(0, r);
            } else {
                boolean flag = true;
                for (int i = 0; i < data.size(); i++) {
                    if (key.compareTo(data.get(i)) < 0) {
                        flag = false;
                        data.add(i, key);
                        pointers.add(i, r);
                        break;
                    }
                }
                if (flag) {
                    data.add(key);
                    pointers.add(r);
                }
            }
        }
    }

    public void sortedInsertInternal(E key, Object record, Node<E> N) {
        int r = 0;
        if (!N.isLeaf()) {
            r = ((Node) record).getId();
        } else {
            r = (Integer) record;
        }
        ArrayList<E> data = N.getKeys();
        List<Integer> pointers = N.getPointers();
        if (key.compareTo(data.get(0)) < 0) {
            data.add(0, key);
            pointers.add(1, r);
        } else {
            boolean flag = true;
            for (int i = 0; i < data.size(); i++) {
                if (key.compareTo(data.get(i)) < 0) {
                    data.add(i, key);
                    pointers.add(i + 1, r);
                    flag = false;
                    break;
                }
            }
            if (flag) {
                data.add(key);
                pointers.add(r);
            }
        }
    }

    public void deleteNode(Node<E> n, E k) {
        for (int i = 0; i < n.getKeys().size(); i++) {
            if (k.compareTo(n.getKeys().get(i)) == 0) {
                n.getKeys().remove(i);
                n.getPointers().remove(i);
            }
        }
    }

    public void internalDelete(E key, Node<E> n, Node<E> temp) {
        for (int i = 0; i < n.getKeys().size(); i++) {
            if (n.getKeys().get(i).compareTo(key) == 0) {
                n.getKeys().remove(i);
                n.getPointers().remove(i + 1);
            }
        }
    }

    public int sameParent(Node<E> n, Node<E> parent, int size) {
        ArrayList<E> keys = parent.getKeys();
        boolean _next = false;
        boolean _prev = false;
        Node<E> next = h.readNode(n.getNext());
        Node<E> prev = h.readNode(n.getPrev());
        if (sameParent2(parent, n)) {
            for (int i = 0; i < parent.getPointers().size(); i++) {
                if (next.getId() == h.readNode((Integer) parent.getPointers().get(i)).getId()) {
                    _next = true;
                    break;
                }
            }
        }
        if (!sameParent2(parent, n)) {
            for (int i = 0; i < parent.getPointers().size(); i++) {
                if (prev.getId() == h.readNode((Integer) parent.getPointers().get(i)).getId()) {
                    _prev = true;
                    break;
                }
            }
        }
        if (_next && next.getKeys().size() - 1 >= Math.ceil(size / 2.0)) {
            return 1;
        } else if (_prev && prev.getKeys().size() - 1 >= Math.ceil(size / 2.0)) {
            return 2;
        } else {
            return 0;
        }
    }

    public int nexOrprev(Node<E> n, Node<E> parent, int size) {
        boolean _next = false;
        boolean _prev = false;
        Node<E> next = h.readNode(n.getNext());
        Node<E> prev = h.readNode(n.getPrev());
        if (next != null) {
            for (int i = 0; i < parent.getPointers().size(); i++) {
                if (next.getId() == h.readNode((Integer) parent.getPointers().get(i)).getId()) {
                    _next = true;
                    break;
                }
            }
        }
        if (prev != null) {
            for (int i = 0; i < parent.getPointers().size(); i++) {
                if (prev.getId() == h.readNode((Integer) parent.getPointers().get(i)).getId()) {
                    _prev = true;
                    break;
                }
            }
        }
        if (next != null && _next && next.getKeys().size() - 1 >= 1) {
            return 1;
        } else if (prev != null && _prev && prev.getKeys().size() - 1 >= 1) {
            return 2;
        } else {
            return 0;
        }
    }

    public boolean sameParent2(Node<E> parent, Node<E> n) {
        boolean _next = false;
        boolean _prev = false;
        Node<E> next = h.readNode(n.getNext());
        Node<E> prev = h.readNode(n.getPrev());
        if (next != null) {
            for (int i = 0; i < parent.getPointers().size(); i++) {
                if (next.getId() == h.readNode((Integer) parent.getPointers().get(i)).getId()) {
                    _next = true;
                    break;
                }
            }
        }
        if (prev != null) {
            for (int i = 0; i < parent.getPointers().size(); i++) {
                if (prev.getId() == h.readNode((Integer) parent.getPointers().get(i)).getId()) {
                    _prev = true;
                    break;
                }
            }
        }
        if (_next) {
            return true;
        } else {
            return false;
        }


    }
}
