package com.file.filemanager.part3;
import javax.swing.text.Position;
import java.util.*;
import java.io.File;

interface Tree<E> extends Iterable<E>{
    Position root();
    Position parent(Position p)throws IllegalArgumentException;
    Iterable<Position> children (Position p);
    int numChildren(Position p)throws  IllegalArgumentException;
    boolean isInternal(Position p)throws IllegalArgumentException;
    boolean isExternal(Position p) throws IllegalArgumentException;
    boolean isRoot(Position p) throws IllegalArgumentException;
    int size();
    boolean isEmpty();
    Iterator<E> iterator();
    Iterable<Position> positions();
}

abstract class AbstractTree implements Tree{
    public boolean isInternal(Position p){return numChildren(p)>0;}
    public boolean isExternal(Position p){return numChildren(p)==0;}
    public boolean isRoot(Position p){return  p==root();}
    public boolean isEmpty(){return size()==0;}
}

class generaltree<E> extends AbstractTree{
    static class Node<E> implements Position{
        private E element;
        private Node<E> parent;
        private String addres ;
        ArrayList<Node<E>> child = new ArrayList<>();

        public Node(E eleman, Node<E> parentt , ArrayList<E> childd, String addres){
            this.element = eleman;
            this.parent = parentt;
            this.addres = addres;
            if(childd != null){
                for(int a = 0 ; a< childd.size() ; a++){
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
        public Node<E> getParent() {return parent;}
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
    public Node<E> createNode(E e, Node<E> parent , ArrayList<E> childd,String addres){
        return new Node<E>(e, parent,childd, addres);
    }
    protected  Node<E> root = null;
    // اصلی
    static Queue<Node> q = new LinkedList<>();
    // برای خوندن از این صف و ساخت نودهای جدید فرزند
    static Queue<Node> q2 = new LinkedList<>();
    //برای اضاقه کردن بچه ها در اری لیست
    static Queue<Node> q3 = new LinkedList<>();

    private int size = 0;

    public Position addRoot(E e , String addres) throws IllegalArgumentException{
        if (!isEmpty()) throw new IllegalArgumentException("Tree is not empty");
        root = createNode(e,null,null, addres);
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

    public void insertValue(ArrayList array, generaltree test , String addres){

        for (int i=0 ; i<array.size(); i++){
            String value = (String) array.get(i);
            Node node;
            if (test.root()==null){
                node = new Node(value,null,null, addres);
                test.addRoot(value ,addres);
            }else {
                //Node nodep = q3.peek();
                node = new Node(value,q2.peek(),null, addres+"/"+value);
                Node<E> p = q2.peek();
                p.child.add(node);
            }
            q.add(node);
            q2.add(node);
            q3.add(node);
        }
    }
    public void sakht_derakht( generaltree test){
        while(q2.size() != 0){
            Node w = q2.peek();
            File file = new File(w.getAddres());
            if(file.isDirectory()) {
                ArrayList child = listoffolder(w.getAddres());
                if (child.size() != 0) {
                    insertValue(child, test, w.getAddres());
                }
            }
            q2.poll();
        }
    }
    public void printqueue(){
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
    }
    public static ArrayList <String> listoffolder (String addres){
        File file = new File(addres);
        ArrayList <String> list = new ArrayList<>();
        if(file.isDirectory())
        {
            File[] files = file.listFiles();
            for(File f : files)
            {
                list.add(f.getName());
            }
        }
        return list;
    }
    public void printchild(Node node){
        for(int t = 0; t<node.child.size() ; t++){
            Node<E> n = (Node<E>) node.child.get(t);
            System.out.print(n.element+" ");
        }
    }
}
    public class faz3 {
        public static void main(String[] args) {
            //daryaft addres folder asli
            System.out.println("in the name of god");
            generaltree z = new generaltree();
            ArrayList x = new ArrayList<>();
            x.add("Folder");
            String adresasli = "C:/Folder";
            z.insertValue(x,z,adresasli);
            z.sakht_derakht(z);
            z.printqueue();
        }
    }
