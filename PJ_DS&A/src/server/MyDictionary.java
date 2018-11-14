package server;
import core.*;

import java.io.*;
import java.util.Scanner;

public class MyDictionary {
    private BPlusTree bPlusTree = new BPlusTree();
    private RBTree rbTree = new RBTree();
    private Scanner scanner;

    public void init() throws IOException {   //初始化字典,包括插入删除
        File file = new File("src/server/sample files/1_initial.txt");
        scanner = new Scanner(file,"GB18030");
        String key;String value;
        scanner.nextLine();
        while (scanner.hasNext()){
            key = scanner.nextLine();
            value = scanner.nextLine();
            rbTree.insert(key,value);
            bPlusTree.insert(key,value);
        }

        file = new File("src/server/sample files/2_delete.txt");
        scanner = new Scanner(file,"GB18030");
        scanner.nextLine();
        while (scanner.hasNext()){
            key = scanner.nextLine();
            rbTree.delete(key);
            bPlusTree.delete(key);
        }
        file = new File("src/server/sample files/3_insert.txt");
        scanner = new Scanner(file,"GB18030");
        scanner.nextLine();
        while (scanner.hasNext()){
            key = scanner.nextLine();
            value = scanner.nextLine();
            rbTree.insert(key,value);
            bPlusTree.insert(key,value);
        }
//        bPlusTree.printpreorder_tree_walk(); //打印的方式遍历
//        rbTree.printpreorder_tree_walk();
    }

    public void insert(String key,String value,int type){
        if(type == 0){
            rbTree.insert(key,value);
        }else {
            bPlusTree.insert(key,value);
        }
    }

    public void delete(String key, int type){
        if(type == 0){
            rbTree.delete(key);
        }else {
            bPlusTree.delete(key);
        }
    }

    public String get(String key,int type){
        if(type == 0){
            return rbTree.get(key);
        }else {
            return bPlusTree.get(key);
        }
    }

    public boolean handle(File file,int type) throws FileNotFoundException {
        scanner = new Scanner(file,"GB18030");
        try{
            String order = scanner.nextLine();
            if(order.trim().equalsIgnoreCase("insert")){
                insertFile(type);
                return true;
            }else if(order.trim().equalsIgnoreCase("delete")){
                deleteFile(type);
                return true;
            }
        }catch (Exception e){
            return false;
        }
        return false;
    }

    private void insertFile(int type){
        String key;String value;
        while (scanner.hasNext()){
            key = scanner.nextLine();
            if(scanner.hasNext()){
                value = scanner.nextLine();
            }
            else {
                break;
            }
            if(type == 0){
                rbTree.insert(key,value);
            }else if(type == 1){
                bPlusTree.insert(key,value);
            }else {
                rbTree.insert(key,value);
                rbTree.insert(key,value);
            }
        }
    }
    private void deleteFile(int type){
        String key;
        while (scanner.hasNext()){
            key = scanner.nextLine();
            if(type == 0){
                rbTree.delete(key);
            }else if(type == 1){
                bPlusTree.delete(key);
            }else {
                rbTree.delete(key);
                bPlusTree.delete(key);
            }
        }
    }

    public String getF2(String from,String to,int type){
        StringBuilder result = new StringBuilder();
        if(type == 0){
            return rbTree.getF2(from,to);
        }else{
            BPlusNode bPlusNode = bPlusTree.getHead();
            while (bPlusNode.next != null){
                for(int i = 0; i < bPlusNode.entries.size(); i++){
                    String key = bPlusNode.entries.get(i).getKey();
                    if(from.compareToIgnoreCase(key) <= 0 && to.compareToIgnoreCase(key) >= 0){
                        result.append(key + "/" + bPlusNode.entries.get(i).getValue() + "  ");
                    }
                }
                bPlusNode = bPlusNode.next;
            }
        }
        return result.toString();
    }

    public String preorderWalk(int type){
        if(type == 0){
            return rbTree.preorder_tree_walk();
        }else{
            return bPlusTree.preorder_tree_walk();
        }
    }

}
