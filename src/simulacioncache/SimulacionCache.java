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

        //System.out.println("IGNORE" + CacheAsociativa(4095));
        //System.out.println(Integer.toBinaryString(0x1000 | 6).substring(1).substring(9, 12).concat(Integer.toBinaryString(0x1000 | 63).substring(1).substring(6,12)));
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
                    int firstLine = Bloque * 8;
                    int count = 0;

                    int BloqueC = Integer.parseInt(Integer.toBinaryString(0x1000 | CacheMemoryD[Linea].getEtiqueta()).substring(1).substring(9, 12).concat(Integer.toBinaryString(0x1000 | Linea).substring(1).substring(6, 12)));

                    for (int j = firstLine; j < firstLine + 8; j++) {
                        EscribirCacheDirecta(j, CacheMemoryD[Linea].getPalabra()[count]);
                        count++;
                    }

                    CacheMemoryD[Linea].setValid(true);
                    CacheMemoryD[Linea].setModify(false);

                    return CacheMemoryD[Linea].recoverWord(Palabra);

                } else {

                    int Bloque = i / 8;
                    int firstLine = Bloque * 8;
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
            int firstLine = Bloque * 8;
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

    static int CacheAsociativa(int i) {

        int Etiqueta = Integer.parseInt(Integer.toBinaryString(0x1000 | i).substring(1).substring(0, 9));
        int Palabra = Integer.parseInt(Integer.toBinaryString(0x1000 | i).substring(1).substring(9, 12), 2);
        int Linea = -1;

        for (int j = 0; j < CacheMemoryD.length; j++) {
            if (CacheMemoryD[j].getEtiqueta() == Etiqueta) {
                Linea = j;
                break;
            }
        }
        if (Linea == -1) {
            Random r = new Random();
            Linea = r.nextInt(64);
        }

        if (CacheMemoryD[Linea].isValid()) {
            if (CacheMemoryD[Linea].getEtiqueta() == Etiqueta) {
                return CacheMemoryD[Linea].recoverWord(Palabra);
            } else {
                if (CacheMemoryD[Linea].isModify()) {
                    int firstLine = Etiqueta * 8;
                    int firstLineM = CacheMemoryD[Linea].getEtiqueta() * 8;
                    int count = 0;

                    for (int j = firstLineM; j < firstLineM + 8; j++) {
                        RAM[j] = CacheMemoryD[Linea].getPalabra()[count];
                        count++;
                    }

                    count = 0;
                    for (int j = firstLine; j < firstLine + 8; j++) {
                        CacheMemoryD[Linea].getPalabra()[count] = RAM[j];
                        count++;
                    }

                    CacheMemoryD[Linea].setEtiqueta(Etiqueta);
                    CacheMemoryD[Linea].setValid(true);
                    CacheMemoryD[Linea].setModify(false);

                    return CacheMemoryD[Linea].recoverWord(Palabra);
                } else {
                    int firstLine = Etiqueta * 8;
                    int count = 0;

                    count = 0;
                    for (int j = firstLine; j < firstLine + 8; j++) {
                        CacheMemoryD[Linea].getPalabra()[count] = RAM[j];
                        count++;
                    }

                    CacheMemoryD[Linea].setEtiqueta(Etiqueta);
                    CacheMemoryD[Linea].setValid(true);
                    CacheMemoryD[Linea].setModify(false);
                }

                return CacheMemoryD[Linea].recoverWord(Palabra);
            }
        } else {
            int firstLine = Etiqueta * 8;
            int count = 0;

            for (int j = firstLine; j < firstLine + 8; j++) {
                CacheMemoryD[Linea].getPalabra()[count] = RAM[j];
                count++;
            }

            CacheMemoryD[Linea].setEtiqueta(Etiqueta);
            CacheMemoryD[Linea].setValid(true);
            CacheMemoryD[Linea].setModify(false);

            return CacheMemoryD[Linea].recoverWord(Palabra);
        }
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
        int firstLine = Bloque * 8;

        if (CacheMemoryD[Linea].isValid()) {
            if (Etiqueta == CacheMemoryD[Linea].getEtiqueta()) {
                CacheMemoryD[Linea].setModify(true);
                CacheMemoryD[Linea].getPalabra()[Palabra] = v;
            } else {
                if (CacheMemoryD[Linea].isModify()) {
                    //Modifique aquí porque nos daría recursión infinita.
                    int count = 0;
                    for (int j = firstLine; j < firstLine + 8; j++) {
                        RAM[j] = CacheMemoryD[Linea].getPalabra()[count];
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
