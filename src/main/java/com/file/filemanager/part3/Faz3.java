package com.file.filemanager.part3;

import javax.swing.text.Position;
import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;

interface Tree<E> extends Iterable<E> {
    Position root();

    Position parent(Position p) throws IllegalArgumentException;

    Iterable<Position> children(Position p);

    int numChildren(Position p) throws IllegalArgumentException;

    boolean isInternal(Position p) throws IllegalArgumentException;

    boolean isExternal(Position p) throws IllegalArgumentException;

    boolean isRoot(Position p) throws IllegalArgumentException;

    int size();

    boolean isEmpty();

    Iterator<E> iterator();

    Iterable<Position> positions();
}

abstract class AbstractTree implements Tree {
    public boolean isInternal(Position p) {
        return numChildren(p) > 0;
    }

    public boolean isExternal(Position p) {
        return numChildren(p) == 0;
    }

    public boolean isRoot(Position p) {
        return p == root();
    }

    public boolean isEmpty() {
        return size() == 0;
    }
}

class generaltree<E> extends AbstractTree {
    static class Node<E> implements Position {
        private E element;
        private Node<E> parent;
        private String addres;
        ArrayList<Node<E>> child = new ArrayList<>();

        public Node(E eleman, Node<E> parentt, ArrayList<E> childd, String addres) {
            this.element = eleman;
            this.parent = parentt;
            this.addres = addres;
            if (childd != null) {
                for (int a = 0; a < childd.size(); a++) {
                    child.add((Node<E>) childd.get(a));
                }
            }


        }

        public E getElement() {
            return element;
        }

        public void setElement(E e) {
            e = element;
        }

        public Node<E> getParent() {
            return parent;
        }

        public void setParent(Node<E> parentNode) {
            parent = parentNode;
        }

        public Node<E> getchild() {

            return parent;
        }

        public String getAddres() {
            return addres;
        }

        @Override
        public int getOffset() {
            return 0;
        }
    }

    public Node<E> createNode(E e, Node<E> parent, ArrayList<E> childd, String addres) {
        return new Node<E>(e, parent, childd, addres);
    }

    protected Node<E> root = null;
    // اصلی
    static Queue<Node> q = new LinkedList<>();
    // برای خوندن از این صف و ساخت نودهای جدید فرزند
    static Queue<Node> q2 = new LinkedList<>();
    //برای اضاقه کردن بچه ها در اری لیست
    static Queue<Node> q3 = new LinkedList<>();

    StringBuilder preOrderString = new StringBuilder(), inOrderString = new StringBuilder(), postOrderString = new StringBuilder();

    private int size = 0;

    public Position addRoot(E e, String addres) throws IllegalArgumentException {
        if (!isEmpty()) throw new IllegalArgumentException("Tree is not empty");
        root = createNode(e, null, null, addres);
        size = 1;
        return (Position) root;
    }

    @Override
    public Position root() {
        return root;
    }

    @Override
    public Position parent(Position p) throws IllegalArgumentException {
        return null;
    }

    @Override
    public Iterable<Position> children(Position p) {
        return null;
    }

    @Override
    public int numChildren(Position p) throws IllegalArgumentException {
        return 0;
    }

    @Override
    public int size() {
        return 0;
    }

    @Override
    public Iterator iterator() {
        return null;
    }

    @Override
    public Iterable<Position> positions() {
        return null;
    }

    public void insertValue(ArrayList array, generaltree test, String addres) {

        for (int i = 0; i < array.size(); i++) {
            String value = (String) array.get(i);
            Node node;
            if (test.root() == null) {
                node = new Node(value, null, null, addres);
                test.addRoot(value, addres);
            } else {
                //Node nodep = q3.peek();
                node = new Node(value, q2.peek(), null, addres + "/" + value);
                Node<E> p = q2.peek();
                p.child.add(node);
            }
            q.add(node);
            q2.add(node);
            q3.add(node);
        }
    }

    public void sakht_derakht(generaltree test) {
        while (q2.size() != 0) {
            Node w = q2.peek();
            File file = new File(w.getAddres());
            if (file.isDirectory()) {
                ArrayList child = listoffolder(w.getAddres());
                if (child.size() != 0) {
                    insertValue(child, test, w.getAddres());
                }
            }
            q2.poll();
        }
    }

    /*public void printqueue(){
        while(q.size()!=0){
            if(q.peek().parent == null){
                System.out.print(q.peek().element+" ** "+"null ");
                printchild(q.peek());
            }
            else{
                System.out.print(q.peek().element+" ** "+q.peek().parent.element +" ** ");
                printchild(q.peek());
            }
            q.poll();
            System.out.println();
        }
    }*/
    public static ArrayList<String> listoffolder(String addres) {
        File file = new File(addres);
        ArrayList<String> list = new ArrayList<>();
        if (file.isDirectory()) {
            File[] files = file.listFiles();
            for (File f : files) {
                list.add(f.getName());
            }
        }
        return list;
    }

//    public void printchild(Node node) {
//        for (int t = 0; t < node.child.size(); t++) {
//            Node<E> n = (Node<E>) node.child.get(t);
//            System.out.print(n.element + " ");
//        }
//    }

    public void serch_chap(String name) {
        int p = 0;
        Node<E> n = q.peek();
        while (q.size() != 0 && p == 0) {
            n = (Node<E>) q.poll();

            String elemnt = (String) n.element;
            if (elemnt.compareTo(name) == 0) {
                p++;
            }
        }
        if (p == 0) {
            System.out.println("this file is not exist !!");
        } else {
            preOrder(n);
            postorder(n);
            inorder(n);
        }

    }

    public void preOrder(Node n) {
        if (n == null) {
            return;
        }
        preOrderString.append(n.element).append(" ");
        for (int t = 0; t < n.child.size(); t++) {
            preOrder((Node) n.child.get(t));
        }
    }

    public void postorder(Node n) {
        if (n == null)
            return;
        if (n.child.size() == 2) {
            postorder((Node) n.child.get(0));
            postorder((Node) n.child.get(1));
        } else {
            for (int t = 0; t < n.child.size(); t++) {
                postorder((Node) n.child.get(t));
            }
        }
        postOrderString.append(n.element).append(" ");
    }

    public void inorder(Node n) {
        if (n == null)
            return;
        if (n.child.size() == 2) {
            inorder((Node) n.child.get(0));
            inOrderString.append(n.element).append(" ");
            inorder((Node) n.child.get(1));
        } else {
            for (int t = 0; t < n.child.size(); t++) {
                inorder((Node) n.child.get(t));
            }
            inOrderString.append(n.element).append(" ");
        }
    }
}

public class Faz3 {
    generaltree<String> z = new generaltree();
    ArrayList<String> x = new ArrayList<>();

    public Faz3(String adresasli, String name) {
        x.add("rootUnChanged");
        z.insertValue(x, z, adresasli);
        z.sakht_derakht(z);
        z.serch_chap(name);
    }

    public String getPreOrder() {
        return z.preOrderString.toString();
    }

    public String getPostOrder() {
        return z.postOrderString.toString();
    }

    public String getInOrder() {
        return z.inOrderString.toString();
    }
}
