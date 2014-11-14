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
    static int[] CacheDirecta = new int[64];
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

        System.out.println(CacheDirecta(100));

    }

    //Metodos de Lectura & Escritura
    int Leer(int i, int tipo) {
        return i;
    }

    int NoCache(int i) {
        return i;
    }

    static String CacheDirecta(int i) {

        return Integer.toBinaryString(0x1000 | i).substring(1).substring(9, 12);
    }

    int CacheAsociativo(int i) {
        return i;
    }

    int CacheAsociativoPorConjunto(int i) {
        return i;
    }

    void Escribir(int i, int tipo) {

    }

}
