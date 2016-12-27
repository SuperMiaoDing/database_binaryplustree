/**
 * Xiaowen Ding
 * This file is implementation of object serializaion
 */

import java.io.*;
import java.util.Arrays;

/**
 * Created by mac on 16/11/6.
 */
public class Helper {

    private String indexPath = null;

    private final Integer BLOCK_SIZE = 1024;

    public Helper(String path) {
        this.indexPath = path;
    }

    private byte[] serializaion(Node node) {
        try {
            ByteArrayOutputStream b = new ByteArrayOutputStream();
            ObjectOutputStream o = new ObjectOutputStream(b);
            o.writeObject(node);
            byte[] bytes = b.toByteArray();
            o.close();
            b.close();
            return bytes;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private Node deserializaion(byte[] bytes) {
        try {
            ByteArrayInputStream b = new ByteArrayInputStream(bytes);
            ObjectInputStream input = new ObjectInputStream(b);
            Node n = (Node) input.readObject();
            input.close();
            b.close();
            return n;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public String getIndexPath() {
        return indexPath;
    }

    public void setIndexPath(String indexPath) {
        this.indexPath = indexPath;
    }

    public Node readNode(int block){
        try{
            int pointer = block * BLOCK_SIZE;
            RandomAccessFile raf=new RandomAccessFile(indexPath, "r");
            raf.seek(pointer);
            byte[]  buff=new byte[1024];
            int readLength=0;
            readLength=raf.read(buff);
            raf.close();
            if (readLength > 0) {
                Node n = deserializaion(Arrays.copyOf(buff, readLength));
                return n;
            } else {
                return null;
            }
        } catch(Exception e){
            e.printStackTrace();
        }
        return null;
    }

    public void insertNode(int block, Node node){
        try {
            int pointer = block * BLOCK_SIZE;
            RandomAccessFile raf=new RandomAccessFile(indexPath, "rw");
            raf.seek(pointer);
            raf.write(serializaion(node));
            raf.close();
        } catch(Exception e){
            e.printStackTrace();
        }
    }

    public void insertHeadInfo(BPTree tree) {
        try {
            int pointer = 0;
            tree.setRootId(tree.getRoot().getId());
            StringBuilder sb = new StringBuilder();
            sb.append(tree.getLeafSize());
            sb.append(" ");
            sb.append(tree.getInternalSize());
            sb.append(" ");
            sb.append(tree.getRootId());
            sb.append(" ");
            sb.append(tree.getBufferboolSize());
            sb.append(" ");
            sb.append(tree.getTotalNodes());
            sb.append(" ");
            sb.append(tree.getPath());
            sb.append(" ");
            sb.append(tree.getDataFile());
            sb.append(" ");
            sb.append("*");
            RandomAccessFile raf=new RandomAccessFile(indexPath, "rw");
            raf.seek(pointer);
            raf.write(sb.toString().getBytes());
            raf.close();
        } catch(Exception e){
            e.printStackTrace();
        }
    }

    public String[] getHeadInfo() {
        try{
            int pointer = 0;
            RandomAccessFile raf=new RandomAccessFile(indexPath, "r");
            raf.seek(pointer);
            byte[]  buff=new byte[1024];
            int readLength=0;
            readLength=raf.read(buff);
            raf.close();
            if (readLength > 0) {
                String s = new String(buff, 0, readLength);
                return s.split(" ");
            } else {
                return null;
            }
        } catch(Exception e){
            e.printStackTrace();
        }
        return null;
    }

//    public static void main(String[] argus) {
//        Helper h = new Helper("dxwtest");
//        Node n1 = new Node(10, true);
//        ArrayList<Object> list = new ArrayList<Object>();
//        list.add(1);
//        list.add(2);
//        list.add(3);
//        n1.setPointers(list);
//        Node n2 = new Node(11, false);
//        h.insertNode(1, n1);
//        h.insertNode(2, n2);
//        Node n3 = new Node(55, true);
//        h.insertNode(1, n3);
//        Node result1 = h.readNode(1);
//        Node result2 = h.readNode(2);
//        System.out.println(result1.nodeSize);
//        System.out.println(result2.nodeSize);
//    }
}
