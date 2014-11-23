package simulacioncache;

/**
 *
 * @author Guillermo E. Mazzoni & Juan C. Martinez
 */

import java.util.*;

public class cacheLine {
    
    int Etiqueta;
    boolean Valid;
    boolean Modify;
    int[] Palabra;

    public cacheLine() {
        this.Etiqueta = 0b10111011100000;
        this.Valid = false;
        this.Modify = false;
        this.Palabra = new int[8];
    }

    public cacheLine(int Etiqueta, boolean Valid, boolean Modify) {
        this.Etiqueta = Etiqueta;
        this.Valid = Valid;
        this.Modify = Modify;
    }

    public void setEtiqueta(int Etiqueta) {
        this.Etiqueta = Etiqueta;
    }

    public void setValid(boolean Valid) {
        this.Valid = Valid;
    }

    public void setModify(boolean Modify) {
        this.Modify = Modify;
    }

    public void setPalabra(int[] Palabra) {
        this.Palabra = Palabra;
    }

    public int getEtiqueta() {
        return Etiqueta;
    }

    public boolean isValid() {
        return Valid;
    }

    public boolean isModify() {
        return Modify;
    }

    public int[] getPalabra() {
        return Palabra;
    }
   
    int recoverWord(int i){
        return Palabra[i];
    }
}
