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

        } catch (Exception ex) {
            Logger.getLogger(SimulacionCache.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    //Metodos de Lectura & Escritura
    int Leer(int i, int tipo) {
        return i;
    }

    void Escribir(int i, int tipo) {

    }

}
