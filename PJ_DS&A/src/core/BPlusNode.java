package core;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class BPlusNode{
    boolean isLeaf;//是叶节点
    boolean isRoot;
    BPlusNode parent;//父节点
    public BPlusNode previous;//叶子节点可能会含有前节点
    public BPlusNode next;//叶子节点可能会含有后节点
    List<BPlusNode> children = new ArrayList<>();//内部节点含有子节点
    public List<Map.Entry<String,String>> entries = new ArrayList<>();//关键字
    BPlusNode(boolean isLeaf){
        this.isLeaf = isLeaf;
    }
    /** 删除节点*/
    boolean remove(String key){
        int comp ;
        for (Map.Entry<String, String> entry : entries) {
            comp = entry.getKey().compareToIgnoreCase(key);
            if (comp == 0) {
                return entries.remove(entry);
            }
        }
        return false;
    }

    int contains(String key){//判断该节点是否含有指定关键字
        int low = 0, high = entries.size() - 1, mid;
        int comp ;
        while (low <= high) {
            mid = (low + high) / 2;//总是从中间向两边
            comp = entries.get(mid).getKey().compareToIgnoreCase(key);
            if (comp == 0) {
                return mid;
            } else if (comp < 0) {
                low = mid + 1;
            } else {
                high = mid - 1;
            }
        }
        return -1;
    }

    void insertOrUpdate(String key, String value){//插入到当前节点，或更新
        int comp ;
        boolean isInsert = false;
        for(int i = 0; i < entries.size(); i++){
            comp = entries.get(i).getKey().compareToIgnoreCase(key);
            if(comp == 0){
                entries.get(i).setValue(value);//更新
                isInsert = true;
                break;
            }else if(comp > 0){
                entries.add(i,new AbstractMap.SimpleEntry<String, String>(key, value));
                isInsert = true;
                break;
            }
        }
        if(!isInsert){
            entries.add(new AbstractMap.SimpleEntry<String, String>(key, value));
        }
    }


} 
