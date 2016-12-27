/**
 * Xiaowen Ding
 * This file is main function
 */

import java.io.IOException;

public class Main_App {

    /**
     * @param args
     * @throws IOException
     */
    public static void main(String[] args) throws IOException {
        if (args.length < 3) {
            System.out.println("please input argus!");
        }
//        for (int i = 0; i < args.length; i++) {
//            System.out.println(i + " " + args[i]);
//        }
//        return;
        Index d = new Index();
        switch (args[0]) {
            case "find" :
//                example index -find cs6360.idx 45526813100142A
//                12222222222222C
//                222222332222222C
                d.retrieve(args[1]);
                System.out.println(d.search(args[2]));
                break;
            case "create" :
//                INDEX -create CS6360Asg5TestData.txt cs6360.idx 15
                d.create(args[2]);
                d.loadFile(args[1], Integer.valueOf(args[3]));
                d.updateHead();
                System.out.println("create index successfully");
                break;
            case "list" :
//                index -list cs6360.idx 38417813544394A 12
                d.retrieve(args[1]);
                System.out.println(d.listRange(args[2], Integer.valueOf(args[3])));
                break;
            case "insert" :
//                index -insert cs6360.idx "12222222222222C test data I added"
                d.retrieve(args[1]);
                d.insertRecord(args[2]);
//                System.out.println("insert successfully!");
                break;
            default:
                break;
        }

//         case 1: create new index
//        d.create("indextest");
//        d.loadFile("CS6360Asg5TestData.txt");
//        System.out.println(d.getTree().print());
//        System.out.println(d.search("11627047200100A"));
//        System.out.println(d.search("88888888888888B"));
//        System.out.println(d.search("22222"));
//        d.updateHead();
//        // case 2 : use an exist index
//        Index d2 = new Index();
//        d2.retrieve("indextest");
//        System.out.println(d2.getTree().print());
//        System.out.println(d2.search("11627047200100A"));
//        System.out.println(d2.search("88888888888888B"));
//        System.out.println(d2.search("22222"));
//        d2.updateHead();
    }
}
