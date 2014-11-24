/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package simulacioncache;

/**
 *
 * @author Juan Carlos
 */
public class SpecialCursorList {

    DLCursor[] array;
    int head;
    int tail;
    int _size;

    class DLCursor {

        int prev;
        int next;
        Object data;

        DLCursor() {
            this.data = null;
            this.prev = -1;
            this.next = -1;
        }

        public int getPrev() {
            return prev;
        }

        public void setPrev(int prev) {
            this.prev = prev;
        }

        public int getNext() {
            return next;
        }

        public void setNext(int next) {
            this.next = next;
        }

        public Object getData() {
            return data;
        }

        public void setData(Object data) {
            this.data = data;
        }
    }

     public int disp() {
        for (int i = 0; i < array.length; ++i) {
            if (array[i].getData() == null) {
                return i;
            }
        }
        return -1;
    }

    private boolean resize(int nSize) {
        DLCursor[] temp = new DLCursor[nSize];
        if (temp != null) {
            for (int i = 0; i < temp.length; i++) {
                temp[i]= new DLCursor();
            }
            for (int i = 0; i < array.length; ++i) {
                temp[i].setPrev(array[i].getPrev());
                temp[i].setNext(array[i].getNext());
                temp[i].setData(array[i].getData());
                array[i].setData(null);
            }
            array = temp;
            return true;
        } else {
            return false;
        }
    }

    public SpecialCursorList() {
        this.array = new DLCursor[10];
        for (int i = 0; i < array.length; i++) {
            array[i]= new DLCursor();
        }
        this.head = 0;
        this.tail = 0;
        this._size = 0;
    }
    
    public SpecialCursorList(int ini) {
        this.array = new DLCursor[ini];
        for (int i = 0; i < array.length; i++) {
            array[i]= new DLCursor();
        }
        this.head = 0;
        this.tail = 0;
        this._size = 0;
    }

    public boolean insert(Object e, int p) {
        if (p < 0 || p > _size) {
            return false;
        }

        if (isFull()) {
            if (!resize((array.length * 3) / 2 + 1)) {
                return false;
            }
        }

        if (isEmpty()) {
            array[head].setData(e);
        } else {
            int neo = disp();
            array[neo].setData(e);
            if (p == 0) {
                array[neo].setNext(head);
                array[head].setPrev(neo);
                head = neo;
            } else if (p > 0 && p < _size) {
                int temp = head;
                for (int i = 0; i < p - 1; ++i) {
                    temp = array[temp].getNext();
                }
                array[neo].setNext(array[temp].getNext());
                array[neo].setPrev(temp);
                array[temp].setNext(neo);
                array[array[neo].getNext()].setPrev(neo);
            } else {
                int temp = head;
                for (int i = 0; i < p - 1; ++i) {
                    temp = array[temp].getNext();
                }
                array[neo].setPrev(temp);
                array[temp].setNext(neo);
                tail = neo;
            }
        }

        _size++;
        return true;
    }

    public Object remove(int p) {
        Object t = null;
        if (p < 0 || p >= _size) {
            return t;
        }
        if (p == 0) {
            int tmp = array[head].getNext();
            array[head].setPrev(-1);
            array[head].setNext(-1);
            t = array[head].getData();
            array[head].setData(null);
            if (tmp != -1) {
                array[tmp].setPrev(-1);
                head = tmp;
            } else {
                head = 0;
            }
        } else if (p == _size - 1) {
            int temp = head;
            for (int i = 0; i < p; ++i) {
                temp = array[temp].getNext();
            }
            array[temp].setNext(-1);
            array[array[temp].getPrev()].setNext(-1);
            tail = array[temp].getPrev();
            array[temp].setPrev(-1);
            t = array[temp].getData();
            array[temp].setData(null);
        } else {
            int temp = head;
            for (int i = 0; i < p; ++i) {
                temp = array[temp].getNext();
            }
            array[array[temp].getPrev()].setNext(array[temp].getNext());
            array[array[temp].getNext()].setPrev(array[temp].getPrev());
            array[temp].setPrev(-1);
            array[temp].setNext(-1);
            t = array[temp].getData();
            array[temp].setData(null);
            
        }
        _size--;
        return t;
    }

    public void clear() {
        if (array != null) {
            for (int i = 0; i < _size; ++i) {
                array[i].setData(null);
                array[i].setPrev(-1);
                array[i].setNext(-1);
            }
            _size = 0;
        }
    }

    public Object first() {
        if (isEmpty()) {
            return null;
        } else {
            return array[head].getData();
        }
    }

//    public Object last() {
//        Object t = null;
//        if (isEmpty()) {
//            return t;
//        }
//        int temp = head;
//        for (int i = 0; i < _size - 1; ++i) {
//            temp = array[temp].getNext();
//        }
//        t = array[temp].getData();
//        return t;
//    }

        public Object last() {
        Object t = null;
        if (isEmpty()) {
            return t;
        }
        t = array[tail].getData();
        return t;
    }
    
    public Object get(int p) {
        if (p < 0 || p >= _size) {
            return null;
        } else {
            int temp = head;
            for (int i = 0; i < p; ++i) {
                temp = array[temp].getNext();
            }
            return array[temp].getData();
        }
    }

    public int indexOf(Object other) {
        int tmp = head;
        for (int i = 0; i < _size; i++) {
            if (array[tmp].getData().equals(other)) {
                return i;
            }
            tmp = array[tmp].getNext();
        }
        return -1;
    }
    
    public void floatToSurface(int toFloat){
        int prev = array[head].getPrev();
        int next = array[head].getNext();
        array[head].setPrev(array[toFloat].getPrev());
        array[head].setNext(array[toFloat].getNext());
        if (array[toFloat].getPrev() != -1) {
          array[array[toFloat].getPrev()].setNext(head);   
        }
        if (array[toFloat].getNext()==-1) {
            tail = head;
        }else{
            array[array[toFloat].getNext()].setPrev(head);
        }
        array[toFloat].setPrev(prev);
        array[toFloat].setNext(next);
        head = toFloat;
    }

    private boolean isFull() {
        return _size == array.length;
    }
    
    public boolean isEmpty(){
        return _size == 0;
    }

    public int capacity() {
        return array.length;
    }
    
    public int size() {
        return _size;
    }

    public void print() {
        int temp = head;
        for (int i = 0; i < _size; ++i) {
            System.out.println(array[temp].getData().toString()+" pos: "+ temp);
            temp = array[temp].getNext();
        }
    }

}
