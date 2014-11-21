/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package simulacioncache;

/**
 *
 * @author Guillermo E. Mazzoni Juan C. Flores
 */
public class CacheLineSet {
    int nextLine;
    cacheLine[] Lines;
    
    public CacheLineSet() {
        Lines = new cacheLine[4];
        for (int i = 0; i < Lines.length; i++) {
            Lines[i]= new cacheLine();
        }
        nextLine =0;
    }
    
    public int getNextLine() {
        if (nextLine >3) {
            nextLine = 0;
        }
        nextLine++;
        return nextLine-1;
    }

    public void setLines(cacheLine[] Lines) {
        this.Lines = Lines;
    }

    public cacheLine[] getLines() {
        return Lines;
    }

    
}
