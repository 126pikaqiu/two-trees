package core;

public class RBTree {
    private final boolean RED = true;
    private final boolean BLACK = false;
    private final RBNode RBNiL = new RBNode(BLACK);//虚拟子结点

    private RBNode root = RBNiL;

    private void left_rotate(RBNode now){   //x节点左旋
        RBNode child = now.right;
        now.right = child.left;
        if(child.left != RBNiL && child.left != null){
            child.left.parent = now;
        }
        if(now.parent == RBNiL){
            root = child;
        }else if(now == now.parent.left){
            now.parent.left = child;
        }else{
            now.parent.right = child;
        }
        child.parent = now.parent;
        child.left = now;
        now.parent = child;
    }

    private void right_rotate(RBNode now){   //x节点右旋
        RBNode child = now.left;
        now.left = child.right;
        if(child.right != RBNiL && child.right != null){
            child.right.parent = now;
        }
        if(now.parent == RBNiL){
            root = child;
        }else if(now == now.parent.left){
            now.parent.left = child;
        }else{
            now.parent.right = child;
        }
        child.parent = now.parent;
        child.right = now;
        now.parent = child;
    }




    public void insert(String key,String value){//根据键和值创建节点插入红黑树
        RBNode now = new RBNode();
        now.key = key;
        now.value = value;
        insert(now);
    }

    private void insert(RBNode now){
        RBNode prev = RBNiL;
        RBNode cur = root;
        while(cur != RBNiL){
            prev = cur;
            if(now.key.compareToIgnoreCase(cur.key) < 0){
                cur = cur.left;
            }else if(now.key.compareToIgnoreCase(cur.key) == 0){
                cur.value = now.value;
                return;
            } else{
                cur = cur.right;
            }
        }
        now.parent = prev;
        if(prev == RBNiL){
            root = now;
            root.color = BLACK;
            return;
        }else if(now.key.compareToIgnoreCase(prev.key) < 0){
            prev.left = now;
        }else{
            prev.right = now;
        }
        insert_fixup(now);
    }

    private void insert_fixup(RBNode now){//插入之后的修复
        while (now.parent != RBNiL && now.parent.color){//为真表示红色
            if(now.parent.parent == RBNiL){
                return;
            }
            if(now.parent == now.parent.parent.left){
                RBNode uncle = now.parent.parent.right;
                if(uncle.color){
                    now.parent.color = BLACK;//黑
                    uncle.color = BLACK;
                    now.parent.parent.color = RED;//红
                    now = now.parent.parent;
                }else{
                    if(now == now.parent.right){
                        now = now.parent;
                        left_rotate(now);//预旋转
                    }
                    now.parent.color = BLACK;
                    now.parent.parent.color = RED;
                    right_rotate(now.parent.parent);
                }
            }else if(now.parent.parent.right == now.parent){
                RBNode uncle = now.parent.parent.left;
                if(uncle != RBNiL && uncle.color){
                    now.parent.color = BLACK;//黑
                    uncle.color = BLACK;
                    now.parent.parent.color = RED;//红
                    now = now.parent.parent;
                }else{
                    if(now == now.parent.left){
                        now = now.parent;
                        right_rotate(now);//预旋转
                    }
                    now.parent.color = BLACK;
                    now.parent.parent.color = RED;
                    left_rotate(now.parent.parent);
                }
            }
        }
        root.color = BLACK;
    }

    private void transplant(RBNode u, RBNode v){//供delete使用,直接删掉元素,并把子节点接上去
        if(u.parent == RBNiL){
            root = v;
        }else if(u == u.parent.left){
            u.parent.left = v;
        }else {
            u.parent.right = v;
        }
        v.parent = u.parent;
    }

    public boolean delete(String key){//通过键删除节点
        RBNode z = new RBNode();
        z.key = key;
        return delete(z);
    }

    public String getF2(String from,String to){
        return getF2(from,to,root);
    }

    public String getF2(String from,String to,RBNode now){
        String result = "";
        if(now.left != RBNiL){
            result += getF2(from,to,now.left);
        }
        if(now != RBNiL && from.compareToIgnoreCase(now.key) <= 0 && to.compareToIgnoreCase(now.key)>= 0){
            result += now.key + "/" + now.value + "  ";
        }
        if(now.right != RBNiL){
            result += getF2(from,to,now.right);
        }
        return result;
    }

    private boolean delete(RBNode now){//删除元素
        now = get(now.key,root);//查询得到目的节点
        if(now == null || now == RBNiL){
            return false;
        }
        RBNode y = now;
        RBNode x;
        if(!(now.left == RBNiL || now.right == RBNiL)){
            y = tree_minimum(now.right);//有两个子节点则取后继
        }
        if(y.left != RBNiL){
            x = y.left;
        }else {
            x = y.right;//获得接上来的节点
        }
        transplant(y,x);
        if(y != now) {//删除后继的话
            now.key = y.key;
            now.value = y.value;
        }
        if(!y.color){
            delete_fixup(x);
        }
        return true;
    }

    private void delete_fixup(RBNode node){//删除过后的修复
        while(node != root && !node.color){
            if(node == node.parent.left){
                RBNode sib = node.parent.right;
                if(sib == null){
                    System.out.println("null");
                    return;
                }
                if(sib.color){//红色 则把他的sib变为黑色
                    sib.color = BLACK;
                    node.parent.color = RED;//红色
                    left_rotate(node.parent);
                    sib = node.parent.right;
                }
                if(!sib.left.color && !sib.right.color){//两个侄子都是黑色,换颜色
                    sib.color = RED;
                    node = node.parent;//让其父亲承担两个黑色的角色
                }else{
                    if(!sib.right.color){ //侄子异侧节点颜色为黑色的话,预旋
                        sib.color = RED;
                        sib.left.color = BLACK;
                        right_rotate(sib);
                        sib = node.parent.right;
                    }
                    sib.color = node.parent.color;
                    node.parent.color = BLACK;
                    sib.right.color = BLACK;
                    left_rotate(node.parent);
                    node = root;
                }
            }else{
                RBNode sib = node.parent.left;
                if(sib == null){
                    System.out.println("null");
                    return;
                }
                if(sib.color){//红色 则把他的sib变为黑色
                    sib.color = BLACK;
                    node.parent.color = RED;//红色
                    right_rotate(node.parent);
                    sib = node.parent.left;
                }
                if(!sib.left.color && !sib.right.color){//两个侄子都是黑色,换颜色
                    sib.color = RED;
                    node = node.parent;//让其父亲承担两个黑色的角色
                }else{
                    if(!sib.left.color){ //侄子异侧节点颜色为黑色的话,预旋
                        sib.color = RED;
                        sib.right.color = BLACK;
                        left_rotate(sib);
                        sib = node.parent.left;
                    }
                    sib.color = node.parent.color;
                    node.parent.color = BLACK;
                    sib.left.color = BLACK;
                    right_rotate(node.parent);
                    node = root;
                }
            }
        }
        node.color = BLACK;
    }

    public RBNode get(String k,RBNode rbNode){//通过迭代的方式查询，返回目的节点或空
        RBNode x = root;
        while (x != RBNiL && !k.equalsIgnoreCase(x.key)){
            x = k.compareToIgnoreCase(x.key) < 0?x.left:x.right;
        }
        return x;
    }
    public String get(String key){
        return get(key,root).value;
    }

    private void printpreorder_tree_walk(RBNode now,int nth,int level){  //前序遍历
        if(now != RBNiL){
            System.out.println("level=" + level + "  child=" + nth + "  " + now.key + "/" + now.value + (now.color?"RED":"BLACK"));
            level++;
            printpreorder_tree_walk(now.left,0,level);
            printpreorder_tree_walk(now.right,1,level);
        }else{
            System.out.println("level=" + level + "  child=" + nth + "  NULL ");
        }
    }

    private void inorder_tree_walk(RBNode now,int nth,int level){  //前序遍历
        if(now != RBNiL){
            level++;
            inorder_tree_walk(now.left,0,level);
            System.out.println("level=" + (level - 1) + "  child=" + nth + "  " + now.key + "/" + now.value + (now.color?"RED":"BLACK"));
            inorder_tree_walk(now.right,1,level);
        }else{
            System.out.println("level=" + level + "  child=" + nth + "  NULL ");
        }
    }

    public void inorder_tree_walk(){
        inorder_tree_walk(root,0,0);
    }

    public void printpreorder_tree_walk(){
        printpreorder_tree_walk(root,0,0);
    }

    private String preorder_tree_walk(RBNode now,int nth,int level){  //前序遍历
        if(now != RBNiL){
            level++;
            return "level=" + (level - 1) + "  child=" + nth + "  " + now.key + "/" + now.value + (now.color?"RED":"BLACK") + "\n"
                    + preorder_tree_walk(now.left,0,level) + preorder_tree_walk(now.right,1,level);
        }else{
            return "level=" + level + "  child=" + nth + "  NULL \n";
        }
    }

    public String preorder_tree_walk(){
        return preorder_tree_walk(root,0,0);
    }

    private RBNode tree_minimum(RBNode x){ //获得以x为根节点的数的最小值
        if(x.left == RBNiL){
            return x;
        }
        return tree_minimum(x.left);
    }

    private RBNode tree_maximum(RBNode x){  //获得以x为根节点的数的最大值
        if(x.right == RBNiL){
            return x;
        }

        return tree_maximum(x.right);
    }

    class RBNode{//节点内部类
        RBNode left = RBNiL;
        RBNode right = RBNiL;
        RBNode parent = RBNiL;
        boolean color = RED;//RED表示颜色为红色，BLACK表示颜色为黑色
        String key = "";
        String value = "";
        RBNode() {}
        RBNode(boolean bool){
            this.color = bool;
        }
    }

}
