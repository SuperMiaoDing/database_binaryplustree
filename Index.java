/**
 * Xiaowen Ding
 * This file is implementation of index
 */

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Index {
// testing purpose
    private BPTree<String> index;
    private String dataFile = null;
    private Helper helper = null;

    public void create(String path) {
        this.helper = new Helper(path);
        index = new BPTree<String>(20, 20, 3, path, dataFile);
//        dataFile = path;
    }

    public void retrieve(String path) {
        this.helper = new Helper(path);
        String[] head = helper.getHeadInfo();
        int leafSize = Integer.valueOf(head[0]);
        int internalSize = Integer.valueOf(head[1]);
        int rootId = Integer.valueOf(head[2]);
        int bufferSize = Integer.valueOf(head[3]);
        String indexPath = head[5];
        int totalNodes = Integer.valueOf(head[4]);
        dataFile = head[6];
        index = new BPTree<String>(leafSize, internalSize, bufferSize, rootId, indexPath, totalNodes, dataFile);
    }


    public void loadFile(String path, Integer keyLength) throws IOException {
        dataFile = path;
        index.setDataFile(dataFile);
        FileReader fr = new FileReader(dataFile);
        BufferedReader r = new BufferedReader(fr);
        String s = r.readLine();
        ArrayList<String> dic = new ArrayList<String>();
        ArrayList<Object> record = new ArrayList<Object>();
        int count=1;
        while (s != null) {
//            String[] row = s.split(" ");
            // use keylength to determin which part is key
            String key = null;
            if (s.length() < keyLength) {
                key = s;
            } else {
                key = s.substring(0, keyLength);
            }
//            dic.add((String) row[0].toLowerCase());
//            record.add(count);
            index.insertNode(key, count);
            s = r.readLine();
            count++;
        }
        r.close();
        fr.close();
        System.out.println(count);
//        dictionay.insertBulk(dic, record);
    }

    public String search(String s) throws IOException {
        Object n = index.search(s);
        if (n == null) {
            return "not found";
        } else {
            int row = (Integer) n;
            return readOneLine(row);
        }
    }

    private String readOneLine(int line)  throws IOException {
        FileReader fr = new FileReader(dataFile);
        BufferedReader r = new BufferedReader(fr);
        String s = r.readLine();
        int count=1;
        while (s != null) {
            if (count == line) {
                return s;
            }
            s = r.readLine();
            count++;
        }
        r.close();
        fr.close();
        return "wrong row number. cant find row " + line;
    }

    public String listRange(String s, int count) throws IOException {
        List<Integer> result = index.searchRange(s, count);
        StringBuilder sb = new StringBuilder();
        if (result.get(0) == 0) {
            sb.append("cant find key " + s + "\n");
            if (result.get(1) == null) {
                sb.append("next larger one is : null \n");
            } else {
                sb.append("next larger one is in line " + result.get(1) + "\n");
                sb.append(readOneLine(result.get(1)));
            }
        } else {
            for (int i = 1; i < result.size(); i++) {
                sb.append(readOneLine(result.get(i)) + "\n");
            }
        }

        return sb.toString();
    }

    public BPTree<String> getTree() {
        return index;
    }

    public void updateHead() {
        helper.insertHeadInfo(index);
    }

    public void insertRecord(String content) {
        if (content == null || content.length() == 0) {
            System.out.println("insert record is empty!");
            return;
        }
        String[] row = content.split(" ");

        String result = null;
        try {
            result = search(row[0]);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (result != null && !result.equals("not found")) {
            System.out.println("already has a record with a same key : " + result);
            return;
        }
        try {
            FileWriter writer = new FileWriter(dataFile, true);
            writer.write("\n" + content);
            writer.close();

            FileReader fr = new FileReader(dataFile);
            BufferedReader r = new BufferedReader(fr);
            String s = r.readLine();
            int count=0;
            while (s != null) {
                count++;
                s = r.readLine();
            }
            r.close();
            fr.close();
            index.insertNode(row[0], count);
            System.out.println("insert new node successfully");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
