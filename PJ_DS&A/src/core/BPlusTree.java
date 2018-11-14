package core;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

public class BPlusTree {
    private BPlusNode head;
    private BPlusNode root;//根节点

    private  int order = 10;//B+树的阶数,也就是每个内部节点最多的子节点个数

    /**插入或更新数据 */
    public void insert(String key, String value){
        insertOrUpdate(key,value,root);
    }
    public boolean delete(String key){
        return remove(key,root);
    }

    /**沿着now节点向下遍历插入或更新数据*/
    private void insertOrUpdate(String key, String value, BPlusNode now){//递归查找到需要插入的叶子节点
        
        if(root == null){ //根节点为空，直接插入
            root = new BPlusNode(true);
            root.entries.add(new AbstractMap.SimpleEntry<String, String>(key,value));
            return;
        }
            
        if (now.isLeaf){        //如果是叶子节点
            //不需要分裂，直接插入或更新
            if (now.contains(key) != -1 || now.entries.size() < order - 1){//每个节点最多order - 1个关键字
                now.insertOrUpdate(key, value);
                return ;
            }

            //需要分裂
            //分裂成左右两个节点
            BPlusNode left = new BPlusNode(true);
            BPlusNode right = new BPlusNode(true);//创建两个叶节点
            
            //设置链接
            if (now.previous != null){
                now.previous.next = left;
                left.previous = now.previous ;
            }
            if (now.next != null) {
                now.next.previous = right;
                right.next = now.next;
            }
            if (now.previous == null){
                head = left;
            }

            left.next = right;
            right.previous = left;

            //复制原节点关键字到分裂出来的新节点
            copy2Nodes(key, value, now, left, right);
                //父节点存在
                if (now.parent != null) {
                    //调整父子节点关系
                    int index = now.parent.children.indexOf(now);
                    now.parent.children.remove(now);
                    left.parent = now.parent;
                    right.parent = now.parent;
                    now.parent.children.add(index,left);
                    now.parent.children.add(index + 1, right);
                    now.parent.entries.add(index,right.entries.get(0));//添加索引
                }else {  //没有父节点
                    BPlusNode parent = new BPlusNode (false); //创建内部节点
                    root = parent;
                    root.isRoot = true;
                    left.parent = parent;
                    right.parent = parent;
                    parent.children.add(left);
                    parent.children.add(right);
                    parent.entries.add(right.entries.get(0));  //添加索引到内部节点
                }
            return ;

        }

//        如果不是叶子节点
//        如果key小于等于节点最左边的key，沿第一个子节点继续搜索
        if (key.compareToIgnoreCase(now.entries.get(0).getKey()) < 0) {
            preSplit(now);//判断是否需要预分裂
            insertOrUpdate(key, value, now.children.get(0));
            //如果key大于节点最右边的key，沿最后一个子节点继续搜索
        }else if (key.compareToIgnoreCase(now.entries.get(now.entries.size()-1).getKey()) >= 0) {
            preSplit(now);//判断是否需要预分裂
            insertOrUpdate(key, value, now.children.get(now.children.size() - 1));
            //否则沿比key大的前一个子节点继续搜索
        }else {
            int low = 0, high = now.entries.size() - 1, mid = 0;
            int comp ;
            while (low <= high) {
                mid = (low + high) / 2;
                comp = now.entries.get(mid).getKey().compareToIgnoreCase(key);
                if (comp == 0) {
                    preSplit(now);
                    insertOrUpdate(key, value, now.children.get(mid+1));
                    break;
                } else if (comp < 0) {
                    low = mid + 1;
                } else {
                    high = mid - 1;
                }
            }
            if(low>high){
                preSplit(now);//判断是否需要预分裂
                insertOrUpdate(key, value, now.children.get(low));
            }
        }
    }

    /**沿着now节点向下遍历删除数据*/
    private boolean remove(String key, BPlusNode now){

        if(root == null){//根节点为空则直接返回null
            return false;
        }
        //如果是叶子节点
        if (now.isLeaf){

            //如果不包含该关键字，则直接返回
            if (now.contains(key) == -1){
                return false;
            }

            //如果既是叶子节点又是根节点，直接删除
            if (now == root) {
                return now.remove(key);
            }

            //如果关键字数大于等于M / 2，直接删除
            if (now.entries.size() >= order / 2 && now.entries.size() > 2) {
                return now.remove(key);
            }

            //如果自身关键字数小于M / 2，并且前节点关键字数大于等于M / 2，则从其处借补
            if (now.previous != null && now.previous.parent == now.parent &&
                    now.previous.entries.size() >= order / 2
                    && now.previous.entries.size() > 2 ) {
                //添加到首位
                int size = now.previous.entries.size();
                now.entries.add(0, now.previous.entries.remove(size - 1));
                int index = now.parent.children.indexOf(now.previous);
                now.parent.entries.set(index, now.entries.get(0));
                return now.remove(key);
            }

            //如果自身关键字数小于M / 2，并且后节点关键字数大于M / 2，则从其处借补
            if (now.next != null && now.next.parent == now.parent
                    && now.next.entries.size() >= order / 2
                    && now.next.entries.size() > 2) {
                now.entries.add(now.next.entries.remove(0));
                int index = now.parent.children.indexOf(now);
                now.parent.entries.set(index, now.next.entries.get(0));
                return now.remove(key);
            }

            //同前面节点合并
            if (now.previous != null && now.previous.parent == now.parent) {
                boolean returnValue =  now.remove(key);
                //将当前节点的关键字添加到前节点的末尾
                now.previous.entries.addAll(now.entries);
                now.entries = now.previous.entries;
                now.parent.children.remove(now.previous);

                //更新链表
                if (now.previous.previous != null) {
                    now.previous.previous.next = now;
                    now.previous = now.previous.previous;
                }else{
                    now.previous = null;
                    head = now;
                }

                now.parent.entries.remove(now.parent.children.indexOf(now));
                return returnValue;
            }

            //同后面节点合并
            if(now.next != null && now.next.parent == now.parent) {
                boolean returnValue = now.remove(key);
                //从首位开始添加到末尾
                now.entries.addAll(now.next.entries);
                now.parent.children.remove(now.next);
                //更新链表
                if (now.next.next != null) {
                    now.next.next.previous = now;
                    now.next = now.next.next;
                }else {
                    now.next = null;
                }
                //更新父节点的关键字列表
                now.parent.entries.remove(now.parent.children.indexOf(now));
                return returnValue;
            }
        }
        /*如果不是叶子节点*/

        //如果key小于等于节点最左边的key，沿第一个子节点继续搜索
        if (key.compareToIgnoreCase(now.entries.get(0).getKey()) < 0) {
            preMerge(now);//预合并
            return remove(key, now.children.get(0));
            //如果key大于节点最右边的key，沿最后一个子节点继续搜索
        }else if (key.compareToIgnoreCase(now.entries.get(now.entries.size()-1).getKey()) >= 0) {
            preMerge(now);//预合并
            return remove(key, now.children.get(now.children.size() - 1));
            //否则沿比key大的前一个子节点继续搜索
        }else {
            preMerge(now);//预合并
            int comp ;
            for(int i = 0; i < now.entries.size(); i++){
                comp = now.entries.get(i).getKey().compareToIgnoreCase(key);
                if(comp == 0){
                    return remove(key,now.children.get(i + 1));
                }else if(comp > 0){
                    return remove(key,now.children.get(i));
                }
            }
            return false;//cannot reach
        }
    }

    public BPlusNode getHead() {
        return head;
    }

    public BPlusNode getNext(BPlusNode node){
        return node.next;
    }

    public AbstractMap.SimpleEntry<String,String> getEntry(BPlusNode node,int i){
        return (AbstractMap.SimpleEntry<String, String>) node.entries.get(i);
    }

    /**预合并非叶节点*/
    private void preMerge(BPlusNode now) {

        // 如果子节点数小于等于M / 2或者小于2，则需要合并节点
        if (now.children.size() <= order / 2 || now.children.size() < 2) {
            if (now == root) {
                // 如果是根节点并且子节点数大于等于2
                if (now.children.size() >= 2) return;
                 //否则与子节点合并
                root = now.children.get(0);
                root.parent = null;
                return ;
            }

            //计算前后节点索引
            int currIdx = now.parent.children.indexOf(now);
            int prevIdx = currIdx - 1;
            int nextIdx = currIdx + 1;
            BPlusNode previous = null, next = null;
            if (prevIdx >= 0) {
                previous = now.parent.children.get(prevIdx);
            }
            if (nextIdx < now.parent.children.size()) {
                next = now.parent.children.get(nextIdx);
            }

            // 如果前节点子节点数大于M / 2并且大于2，则从其处借补
            if (previous != null
                    && previous.children.size() > order / 2
                    && previous.children.size() > 2) {

                //前叶子节点末尾节点添加到首位
                int idx = previous.children.size() - 1;
                BPlusNode borrow = previous.children.get(idx);
                previous.children.remove(idx);
                borrow.parent = now;
                now.children.add(0, borrow);
                int preIndex = now.parent.children.indexOf(previous);
                now.entries.add(0,now.parent.entries.get(preIndex));
                now.parent.entries.set(preIndex, previous.entries.remove(idx - 1));
                return ;
            }

            // 如果后节点子节点数大于M / 2并且大于2，则从其处借补
            if (next != null
                    && next.children.size() > order / 2
                    && next.children.size() > 2) {
                //后叶子节点首位添加到末尾
                BPlusNode borrow = next.children.get(0);
                next.children.remove(0);
                borrow.parent = now;
                now.children.add(borrow);
                int preIndex = now.parent.children.indexOf(now);
                now.entries.add(now.parent.entries.get(preIndex));
                now.parent.entries.set(preIndex, next.entries.remove(0));
                return ;
            }

             //同前面节点合并
            if (previous != null) {
                previous.children.addAll(now.children);
                for(int i = 0; i < previous.children.size();i++){
                    previous.children.get(i).parent = now;
                }
                int indexPre = now.parent.children.indexOf(previous);
                previous.entries.add(now.parent.entries.get(indexPre));
                previous.entries.addAll(now.entries);
                now.children = previous.children;
                now.entries = previous.entries;

                //更新父节点的关键字列表
                now.parent.children.remove(previous);
                previous.parent = null;
                previous.children = null;
                previous.entries = null;
                now.parent.entries.remove(now.parent.children.indexOf(now));
                return ;
            }

            // 同后面节点合并
            if (next != null) {
                BPlusNode child;
                for (int i = 0; i < next.children.size(); i++) {
                    child = next.children.get(i);
                    now.children.add(child);
                    child.parent = now;
                }
                int index = now.parent.children.indexOf(now);
                now.entries.add(now.parent.entries.get(index));
                now.entries.addAll(next.entries);
                now.parent.children.remove(next);
                next.parent = null;
                next.children = null;
                next.entries = null;
                now.parent.entries.remove(now.parent.children.indexOf(now));
            }

        }
    }

    /**预分裂非叶节点*/
    private void preSplit(BPlusNode z){//预分裂

        if(z.children.size() == order){//当前节点的子节点已经满了，则先分裂防止后续再分裂
            //分裂成左右两个节点
            BPlusNode left = new BPlusNode(false);
            BPlusNode right = new BPlusNode(false);

            //左右两个内部子节点的长度
            int leftSize = (order / 2) + (order % 2);
            int rightSize = order  / 2;

            //复制子节点到分裂出来的新节点，并更新关键字
            for (int i = 0; i < leftSize; i++){
                left.children.add(z.children.get(i));
                z.children.get(i).parent = left;
            }
            for (int i = 0; i < rightSize; i++){
                right.children.add(z.children.get(leftSize + i));
                z.children.get(leftSize + i).parent = right;
            }

            for (int i = 0; i < leftSize - 1; i++) {
                left.entries.add(z.entries.get(i));
            }
            for (int i = 0; i < rightSize - 1; i++) {
                right.entries.add(z.entries.get(leftSize + i));  //余下一个索引插入到父节点
            }

            //如果不是根节点
            if (z.parent != null) {
                //调整父子节点关系
                int index = z.parent.children.indexOf(z);
                z.parent.children.remove(z);
                left.parent = z.parent;
                right.parent = z.parent;
                z.parent.children.add(index,left);
                z.parent.children.add(index + 1, right);
                z.parent.entries.add(index,z.entries.get(leftSize - 1));
            }else {                //如果是根节点
                BPlusNode parent = new BPlusNode (false); //创建内部节点
                root = parent;
                left.parent = parent;
                right.parent = parent;
                parent.children.add(left);
                parent.children.add(right);
                parent.entries.add(z.entries.get(leftSize - 1));  //添加索引到根节点
            }
        }
    }

    private void copy2Nodes(String key,String value,BPlusNode now,BPlusNode left,BPlusNode right){//将一个节点的值和一个键值对拷贝到两个节点中
        int leftSize = order  / 2 + order % 2; //leftSize = order / 2 + 1;        //左右两个节点关键字长度
        boolean b = false;   //用于记录新元素是否已经被插入
        for (int i = 0; i < now.entries.size(); i++) {
            if(leftSize !=0){
                leftSize --;
                if(!b && now.entries.get(i).getKey().compareToIgnoreCase(key) > 0){
                    left.entries.add(new AbstractMap.SimpleEntry<String,String>(key, value));
                    b = true;
                    i--;
                }else {
                    left.entries.add(now.entries.get(i)); //小于key的leftSize个元素加入到left节点中
                }
            }else {
                if(!b&&now.entries.get(i).getKey().compareToIgnoreCase(key) > 0){
                    right.entries.add(new AbstractMap.SimpleEntry<String,String>(key, value));
                    b = true;
                    i--;
                }else {
                    right.entries.add(now.entries.get(i));
                }
            }
        }
        if(!b){
            right.entries.add(new AbstractMap.SimpleEntry<String,String>(key, value));//没有插入新节点的话就在末尾插入
        }
    }


    public String get(String key){//通过关键字查询值
        return get(key,root);//返回空或者值
    }

    private String get(String key, BPlusNode now){  //用递归实现
        if(now.isLeaf){    //到达叶节点后开始查找值
            int low = 0, high = now.entries.size() - 1, mid;
            int comp ;
            while (low <= high) {
                mid = (low + high) / 2;//总是从中间向两边
                comp = now.entries.get(mid).getKey().compareToIgnoreCase(key);
                if (comp == 0) {
                    return now.entries.get(mid).getValue();
                } else if (comp < 0) {
                    low = mid + 1;
                } else {
                    high = mid - 1;
                }
            }
            //未找到所要查询的对象
            return null;
        }

        //寻找索引中ing
        if (key.compareToIgnoreCase(now.entries.get(0).getKey()) < 0) {//如果key小于节点最左边的key，沿第一个子节点继续搜索

            return get(key,now.children.get(0));
        } else if (key.compareToIgnoreCase(now.entries.get(now.entries.size() - 1).getKey()) >= 0) {//如果key大于等于节点最右边的key，沿最后一个子节点继续搜索
            return get(key,now.children.get(now.children.size() - 1));
        } else {            //否则沿比key大的前一个子节点继续搜索
            int comp ;
            for(int i = 0; i < now.entries.size(); i++){
                comp = now.entries.get(i).getKey().compareToIgnoreCase(key);
                if(comp == 0){
                    return get(key,now.children.get(i + 1));
                }else if(comp > 0){
                    return get(key,now.children.get(i));
                }
            }
            return null;//cannot reach
        }
    }

    private void printpreorder_tree_walk(BPlusNode now,int nth,int level){  //前序遍历
        if(now != null){
                System.out.print("level=" + level + "  children=" + nth);
                for (int i = 0; i < now.entries.size(); i++){
                    System.out.print("  /" + now.entries.get(i).getKey() + ":  " + now.entries.get(i).getValue());
                }
                System.out.println();
            level++;
            for(int i = 0; i < now.children.size();i++) {
                printpreorder_tree_walk(now.children.get(i),i,level);
            }
        }
    }

    private String preorder_tree_walk(BPlusNode now,int nth,int level){  //前序遍历
        StringBuilder result;
        if(now != null){
            result = new StringBuilder("level=" + level + "  children=" + nth);
            for (int i = 0; i < now.entries.size(); i++){
                result.append("  /").append(now.entries.get(i).getKey()).append(":  ").append(now.entries.get(i).getValue());
            }
            result.append("\n");
            level++;
            for(int i = 0; i < now.children.size();i++) {
                result.append(preorder_tree_walk(now.children.get(i),i,level));
            }
            return result.toString();
        }else{
            return "";
        }
    }

    public String preorder_tree_walk(){
        return preorder_tree_walk(root,0,0);
    }

    public void printpreorder_tree_walk(){
        printpreorder_tree_walk(root,0,0);
    }


}
