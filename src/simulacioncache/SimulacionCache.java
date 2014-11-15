package simulacioncache;

/**
 *
 * @author Guillermo E. Mazzoni Juan C. Flores
 */
import java.util.*;
import java.io.*;
import java.util.logging.*;

public class SimulacionCache {

    static int[] RAM = new int[4096];
    static int[] Cache = new int[512];
    static int[] Bloque = new int[8];
    static cacheLine[] CacheMemoryD = new cacheLine[64];
    static int[] CacheAsociativa = new int[64];
    //static int[] Conjunto = new int[4];
    static BufferedReader reader;

    public static void main(String[] args) {

        try {

            reader = new BufferedReader(new FileReader("./src/datos.txt"));
            String data = null;
            int c = 0;

            while ((data = reader.readLine()) != null) {
                RAM[c] = Integer.parseInt(data);
                c++;
            }

            reader.close();

        } catch (Exception ex) {
            Logger.getLogger(SimulacionCache.class.getName()).log(Level.SEVERE, null, ex);
        }

        System.out.println("IGNORE" + CacheDirecta(4095));

    }

    //Metodos de Lectura & Escritura
    int Leer(int i, int tipo) {
        return i;
    }

    int NoCache(int i) {
        return i;
    }

    static int CacheDirecta(int i) {

        int Etiqueta = Integer.parseInt(Integer.toBinaryString(0x1000 | i).substring(1).substring(0, 3));
        int Palabra = Integer.parseInt(Integer.toBinaryString(0x1000 | i).substring(1).substring(9, 12), 2);
        int Linea = Integer.parseInt(Integer.toBinaryString(0x1000 | i).substring(1).substring(3, 9), 2);

        if (CacheMemoryD[Linea].isValid()) {
            if (Etiqueta == CacheMemoryD[Linea].getEtiqueta()) {
                return CacheMemoryD[Linea].recoverWord(Palabra);
            } else {
                if (CacheMemoryD[Linea].isModify()) {

                    int Bloque = i / 8;
                    int firstLine = (i / 8) * 8;
                    int count = 0;

                    for (int j = firstLine; j < firstLine + 8; j++) {
                        EscribirCacheDirecta(j, CacheMemoryD[Linea].getPalabra()[count]);
                        count++;
                    }
                    
                    CacheMemoryD[Linea].setValid(true);
                    CacheMemoryD[Linea].setModify(false);

                    return CacheMemoryD[Linea].recoverWord(Palabra);

                } else {

                    int Bloque = i / 8;
                    int firstLine = (i / 8) * 8;
                    int count = 0;

                    CacheMemoryD[Linea].setEtiqueta(Etiqueta);
                    CacheMemoryD[Linea].setValid(true);
                    CacheMemoryD[Linea].setModify(false);

                    for (int j = firstLine; j < firstLine + 8; j++) {
                        CacheMemoryD[Linea].getPalabra()[count] = RAM[j];
                        count++;
                    }

                    return CacheMemoryD[Linea].recoverWord(Palabra);
                }
            }

        } else {

            int Bloque = i / 8;
            int firstLine = (i / 8) * 8;
            int count = 0;

            CacheMemoryD[Linea].setEtiqueta(Etiqueta);
            CacheMemoryD[Linea].setValid(true);
            CacheMemoryD[Linea].setModify(false);

            for (int j = firstLine; j < firstLine + 8; j++) {
                CacheMemoryD[Linea].getPalabra()[count] = RAM[j];
                count++;
            }

            return CacheMemoryD[Linea].recoverWord(Palabra);
        }
    }

    int CacheAsociativo(int i) {
        return i;
    }

    int CacheAsociativoPorConjunto(int i) {
        return i;
    }

    void Escribir(int i, int tipo) {

    }

    static void EscribirCacheDirecta(int i, int v) {

        int Etiqueta = Integer.parseInt(Integer.toBinaryString(0x1000 | i).substring(1).substring(0, 3));
        int Palabra = Integer.parseInt(Integer.toBinaryString(0x1000 | i).substring(1).substring(9, 12), 2);
        int Linea = Integer.parseInt(Integer.toBinaryString(0x1000 | i).substring(1).substring(3, 9), 2);

        int Bloque = i / 8;
        int firstLine = (i / 8) * 8;

        if (CacheMemoryD[Linea].isValid()) {
            if (Etiqueta == CacheMemoryD[Linea].getEtiqueta()) {
                CacheMemoryD[Linea].setModify(true);
                CacheMemoryD[Linea].getPalabra()[Palabra] = v;
            } else {
                if (CacheMemoryD[Linea].isModify()) {
                    //Modifique aquí porque nos daría recursión infinita.
                    int count = 0;
                    for (int j = firstLine; j < firstLine + 8; j++) {
                        RAM[j]= CacheMemoryD[Linea].getPalabra()[count];
                        count++;
                    }
                    CacheDirecta(i);
                    CacheMemoryD[Linea].setValid(true);
                    CacheMemoryD[Linea].setModify(true);
                    CacheMemoryD[Linea].getPalabra()[Palabra] = v;
                } else {
                    CacheMemoryD[Linea].setModify(true);
                    CacheMemoryD[Linea].getPalabra()[Palabra] = v;
                }
            }

        } else {
            CacheDirecta(i);
            CacheMemoryD[Linea].setValid(true);
            CacheMemoryD[Linea].setModify(true);
            CacheMemoryD[Linea].getPalabra()[Palabra] = v;
        }

    }
}
