package simulacioncache;

/**
 *
 * @author Guillermo E. Mazzoni Juan C. Flores
 */
import java.io.*;
import java.util.logging.*;
import javax.swing.JOptionPane;

public class SimulacionCache {

    static int[] RAM = new int[4096];
    static int[] Cache = new int[512];
    static int[] Bloque = new int[8];
    static cacheLine[] CacheMemoryD = new cacheLine[64];
    static CacheLineSet[] Conjuntos = new CacheLineSet[16];
    static BufferedReader reader;
    static LRU leastRecentlyUsed = new LRU();
    static double Time = 0;

    public static void main(String[] args) {
        for (int i = 0; i < CacheMemoryD.length; i++) {
            CacheMemoryD[i] = new cacheLine();
        }
        for (int i = 0; i < Conjuntos.length; i++) {
            Conjuntos[i] = new CacheLineSet();
        }

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
        

        int Tipo = Integer.parseInt(JOptionPane.showInputDialog(null, "Seleccione su opcion: \n"
                + "    1. Cache Directa \n"
                + "    2. Cache Asociativa \n"
                + "    3. Cache Asociativa Por Conjunto \n"
                + "    4. Sin Cache \n"));

        for (int i = 0; i < 4094; i++) {
            for (int j = i + 1; j < 4095; j++) {
                int Temp = 0;
                if (Leer(i, Tipo) > Leer(j, Tipo)) {

                    Temp = Leer(i, Tipo);
                    Escribir(i, Tipo, Leer(j, Tipo));
                    Escribir(j, Tipo, Temp);

                }
            }
        }

        /*
         //Prueba Escritorio
         int TIPO = 1;
         Escribir(TIPO, 100, 10);
         Escribir(TIPO, 101, 13);
         Escribir(TIPO, 102, 21);
         Escribir(TIPO, 103, 11);
         Escribir(TIPO, 104, 67);
         Escribir(TIPO, 105, 43);
         Escribir(TIPO, 106, 9);
         Escribir(TIPO, 107, 11);
         Escribir(TIPO, 108, 19);
         Escribir(TIPO, 109, 23);
         Escribir(TIPO, 110, 32);
         Escribir(TIPO, 111, 54);
         Escribir(TIPO, 112, 98);
         Escribir(TIPO, 113, 7);
         Escribir(TIPO, 114, 13);
         Escribir(TIPO, 115, 1);
         int Menor = Leer(TIPO, 100);
         int Mayor = Menor;

         int K = 0;

         for (int i = 101; i <= 115; i++) {
         K++;
         Escribir(TIPO, 615, K);
         if (Leer(TIPO, i) < Menor) {
         Menor = Leer(TIPO, i);
         if (Leer(TIPO, i) > Mayor) {
         Mayor = Leer(TIPO, i);
         }

         }
         } */
        
        System.out.println(Time);
        JOptionPane.showMessageDialog(null, "El tiempo es:  " + Time, "Resultado", JOptionPane.INFORMATION_MESSAGE);
    }

    //Metodos de Lectura & Escritura
    static int Leer(int i, int tipo) {
        switch (tipo) {
            case 1:
                return CacheCorrespondenciaDirecta(i);
            case 2:
                return CacheAsociativa(i);
            case 3:
                return CacheAsociativoPorConjunto(i);
            default:
                return NoCache(i);
        }
    }

    static void Escribir(int i, int v, int tipo) {
        switch (tipo) {
            case 1:
                EscribirCacheCorrespondenciaDirecta(i, v);
                break;
            case 2:
                EscribirCacheAsociativa(i, v);
                break;
            case 3:
                EscribirCacheAsociativoPorConjunto(i, v);
                break;
            default:
                EscribirNoCache(i, v);
                break;
        }
    }

    static int NoCache(int i) {
        Time += 0.1;
        return RAM[i];
    }

    static void EscribirNoCache(int i, int v) {
        Time += 0.1;
        RAM[i] = v;
    }

    static int CacheCorrespondenciaDirecta(int i) {

        int Etiqueta = Integer.parseInt(Integer.toBinaryString(0x1000 | i).substring(1).substring(0, 3));
        int Palabra = Integer.parseInt(Integer.toBinaryString(0x1000 | i).substring(1).substring(9, 12), 2);
        int Linea = Integer.parseInt(Integer.toBinaryString(0x1000 | i).substring(1).substring(3, 9), 2);

        if (CacheMemoryD[Linea].isValid()) {
            if (Etiqueta == CacheMemoryD[Linea].getEtiqueta()) {
                Time += 0.01;
                return CacheMemoryD[Linea].recoverWord(Palabra);
            } else {
                if (CacheMemoryD[Linea].isModify()) {

                    int Bloque = i / 8;
                    int firstLine = Bloque * 8;
                    int count = 0;

                    int BloqueC = Integer.parseInt(Integer.toBinaryString(0x1000 | CacheMemoryD[Linea].getEtiqueta()).substring(1).substring(9, 12).concat(Integer.toBinaryString(0x1000 | Linea).substring(1).substring(6, 12)), 2);
                    int firstLineM = BloqueC * 8;

                    for (int j = firstLineM; j < firstLineM + 8; j++) {
                        RAM[j] = CacheMemoryD[Linea].getPalabra()[count];
                        count++;
                    }

                    count = 0;
                    for (int j = firstLine; j < firstLine + 8; j++) {
                        CacheMemoryD[Linea].getPalabra()[count] = RAM[j];
                        count++;
                    }

                    CacheMemoryD[Linea].setValid(true);
                    CacheMemoryD[Linea].setModify(false);

                    Time += 0.66 + 0.66 + 0.01;
                    return CacheMemoryD[Linea].recoverWord(Palabra);

                } else {

                    int Bloque = i / 8;
                    int firstLine = Bloque * 8;
                    int count = 0;

                    CacheMemoryD[Linea].setValid(true);
                    CacheMemoryD[Linea].setModify(false);

                    for (int j = firstLine; j < firstLine + 8; j++) {
                        CacheMemoryD[Linea].getPalabra()[count] = RAM[j];
                        count++;
                    }
                }
                CacheMemoryD[Linea].setEtiqueta(Etiqueta);
                Time += 0.1 + 0.01;
                return CacheMemoryD[Linea].recoverWord(Palabra);
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

            Time += 0.1 + 0.01;
            return CacheMemoryD[Linea].recoverWord(Palabra);
        }
    }

    static int CacheAsociativa(int i) {

        int Etiqueta = Integer.parseInt(Integer.toBinaryString(0x1000 | i).substring(1).substring(0, 9));
        int Palabra = Integer.parseInt(Integer.toBinaryString(0x1000 | i).substring(1).substring(9, 12), 2);
        int Linea = leastRecentlyUsed.consult(Integer.parseInt(Integer.toBinaryString(Etiqueta),2));
        leastRecentlyUsed.checkMap();

        if (CacheMemoryD[Linea].isValid()) {
            if (CacheMemoryD[Linea].getEtiqueta() == Etiqueta) {
                Time += 0.01;
                return CacheMemoryD[Linea].recoverWord(Palabra);
            } else {
                if (CacheMemoryD[Linea].isModify()) {
                    int firstLine = Integer.parseInt(Integer.toString(Etiqueta), 2) * 8;
                    int firstLineM = Integer.parseInt(Integer.toString(CacheMemoryD[Linea].getEtiqueta()), 2) * 8;
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

                    CacheMemoryD[Linea].setValid(true);
                    CacheMemoryD[Linea].setModify(false);
                    Time += 0.75;

                } else {
                    int firstLine = Integer.parseInt(Integer.toString(Etiqueta), 2) * 8;
                    int count = 0;

                    count = 0;
                    for (int j = firstLine; j < firstLine + 8; j++) {
                        CacheMemoryD[Linea].getPalabra()[count] = RAM[j];
                        count++;
                    }

                    CacheMemoryD[Linea].setValid(true);
                    CacheMemoryD[Linea].setModify(false);
                }

                CacheMemoryD[Linea].setEtiqueta(Etiqueta);
                Time += 0.66;
                return CacheMemoryD[Linea].recoverWord(Palabra);
            }
        } else {
            int firstLine = Integer.parseInt(Integer.toString(Etiqueta), 2) * 8;
            int count = 0;

            for (int j = firstLine; j < firstLine + 8; j++) {
                CacheMemoryD[Linea].getPalabra()[count] = RAM[j];
                count++;
            }

            CacheMemoryD[Linea].setEtiqueta(Etiqueta);
            CacheMemoryD[Linea].setValid(true);
            CacheMemoryD[Linea].setModify(false);
            Time += 0.66;
            return CacheMemoryD[Linea].recoverWord(Palabra);
        }
    }

    static int CacheAsociativoPorConjunto(int i) {
        int Etiqueta = Integer.parseInt(Integer.toBinaryString(0x1000 | i).substring(1).substring(0, 5));
        int Conjunto = Integer.parseInt(Integer.toBinaryString(0x1000 | i).substring(1).substring(5, 9), 2);
        int Palabra = Integer.parseInt(Integer.toBinaryString(0x1000 | i).substring(1).substring(9, 12), 2);

        int Linea = -1;
        for (int j = 0; j < Conjuntos[Conjunto].getLines().length; j++) {
            if (Conjuntos[Conjunto].getLines()[j].getEtiqueta() == Etiqueta) {
                Linea = j;
            }
        }

        if (Linea == -1) {
            Linea = Conjuntos[Conjunto].getNextLine();
        }

        if (Conjuntos[Conjunto].getLines()[Linea].isValid()) {
            if (Conjuntos[Conjunto].getLines()[Linea].getEtiqueta() == Etiqueta) {
                Time += 0.01;
                return Conjuntos[Conjunto].getLines()[Linea].getPalabra()[Palabra];
            } else {
                if (Conjuntos[Conjunto].getLines()[Linea].isModify()) {
                    String SBloqueN = Integer.toString(Conjuntos[Conjunto].getLines()[Linea].getEtiqueta()) + Integer.toBinaryString(Conjunto);
                    int firstLineN = Integer.parseInt(SBloqueN, 2) * 8;
                    int count = 0;

                    for (int j = firstLineN; j < firstLineN + 8; j++) {
                        RAM[j] = Conjuntos[Conjunto].getLines()[Linea].getPalabra()[count];
                        count++;
                    }

                    count = 0;
                    String SBloque = Integer.toString(Etiqueta) + Integer.toBinaryString(Conjunto);
                    int firstLine = Integer.parseInt(SBloque, 2) * 8;

                    for (int j = firstLine; j < firstLine + 8; j++) {
                        Conjuntos[Conjunto].getLines()[Linea].getPalabra()[count] = RAM[j];
                        count++;
                    }

                    Time += 0.75;
                    Conjuntos[Conjunto].getLines()[Linea].setValid(true);
                    Conjuntos[Conjunto].getLines()[Linea].setModify(false);

                } else {
                    String SBloque = Integer.toString(Etiqueta) + Integer.toBinaryString(Conjunto);
                    int firstLine = Integer.parseInt(SBloque, 2) * 8;
                    int count = 0;

                    for (int j = firstLine; j < firstLine + 8; j++) {
                        Conjuntos[Conjunto].getLines()[Linea].getPalabra()[count] = RAM[j];
                        count++;
                    }

                    Time += 0.66;
                    Conjuntos[Conjunto].getLines()[Linea].setValid(true);
                    Conjuntos[Conjunto].getLines()[Linea].setModify(false);

                }
                Conjuntos[Conjunto].getLines()[Linea].setEtiqueta(Etiqueta);
                return Conjuntos[Conjunto].getLines()[Linea].getPalabra()[Palabra];
            }
        } else {
            String SBloque = Integer.toString(Etiqueta) + Integer.toBinaryString(Conjunto);
            int firstLine = Integer.parseInt(SBloque, 2) * 8;
            int count = 0;

            for (int j = firstLine; j < firstLine + 8; j++) {
                Conjuntos[Conjunto].getLines()[Linea].getPalabra()[count] = RAM[j];
                count++;
            }

            Conjuntos[Conjunto].getLines()[Linea].setEtiqueta(Etiqueta);
            Conjuntos[Conjunto].getLines()[Linea].setValid(true);
            Conjuntos[Conjunto].getLines()[Linea].setModify(false);

            Time += 0.66;
            return Conjuntos[Conjunto].getLines()[Linea].getPalabra()[Palabra];
        }
    }

    static void EscribirCacheCorrespondenciaDirecta(int i, int v) {

        int Etiqueta = Integer.parseInt(Integer.toBinaryString(0x1000 | i).substring(1).substring(0, 3));
        int Palabra = Integer.parseInt(Integer.toBinaryString(0x1000 | i).substring(1).substring(9, 12), 2);
        int Linea = Integer.parseInt(Integer.toBinaryString(0x1000 | i).substring(1).substring(3, 9), 2);

        int Bloque = i / 8;
        int firstLine = Bloque * 8;

        if (CacheMemoryD[Linea].isValid()) {
            if (Etiqueta == CacheMemoryD[Linea].getEtiqueta()) {
                CacheMemoryD[Linea].setModify(true);
                CacheMemoryD[Linea].getPalabra()[Palabra] = v;
                Time += 0.01;
            } else {
                if (CacheMemoryD[Linea].isModify()) {
                    int count = 0;

                    //int BloqueC = Integer.parseInt(Integer.toBinaryString(0x1000 | CacheMemoryD[Linea].getEtiqueta()).substring(1).substring(9, 12).concat(Integer.toBinaryString(0x1000 | Linea).substring(1).substring(6, 12)));
                    //int firstLineM = BloqueC * 8;
                    String SBloque = Integer.toString(CacheMemoryD[Linea].getEtiqueta()) + Integer.toBinaryString(Linea);
                    int firstLineM = Integer.parseInt(SBloque, 2) * 8;

                    for (int j = firstLineM; j < firstLineM + 8; j++) {
                        RAM[j] = CacheMemoryD[Linea].getPalabra()[count];
                        count++;
                    }

                    count = 0;
                    for (int j = firstLine; j < firstLine + 8; j++) {
                        CacheMemoryD[Linea].getPalabra()[count] = RAM[j];
                        count++;
                    }

                    Time += 0.66 + 0.66 + 0.01;
                    CacheMemoryD[Linea].setModify(true);
                } else {

                    int count = 0;
                    for (int j = firstLine; j < firstLine + 8; j++) {
                        CacheMemoryD[Linea].getPalabra()[count] = RAM[j];
                        count++;
                    }

                    Time += 0.1 + 0.01;
                    CacheMemoryD[Linea].setModify(true);
                }
                CacheMemoryD[Linea].setEtiqueta(Etiqueta);
                CacheMemoryD[Linea].getPalabra()[Palabra] = v;
            }
        } else {
            int count = 0;

            for (int j = firstLine; j < firstLine + 8; j++) {
                CacheMemoryD[Linea].getPalabra()[count] = RAM[j];
                count++;
            }
            CacheMemoryD[Linea].setEtiqueta(Etiqueta);
            CacheMemoryD[Linea].setValid(true);
            CacheMemoryD[Linea].setModify(true);
            CacheMemoryD[Linea].getPalabra()[Palabra] = v;
            Time += 0.01 + 0.1;
        }

    }

    static void EscribirCacheAsociativa(int i, int v) {
        int Etiqueta = Integer.parseInt(Integer.toBinaryString(0x1000 | i).substring(1).substring(0, 9));
        int Palabra = Integer.parseInt(Integer.toBinaryString(0x1000 | i).substring(1).substring(9, 12), 2);
        int Linea = leastRecentlyUsed.consult(Integer.parseInt(Integer.toBinaryString(Etiqueta),2));
        leastRecentlyUsed.checkMap();

        if (CacheMemoryD[Linea].isValid()) {
            if (CacheMemoryD[Linea].getEtiqueta() == Etiqueta) {
                CacheMemoryD[Linea].setModify(true);
                CacheMemoryD[Linea].getPalabra()[Palabra] = v;
                Time += 0.01;
            } else {
                if (CacheMemoryD[Linea].isModify()) {
                    int firstLine = Integer.parseInt(Integer.toString(Etiqueta), 2) * 8;
                    int firstLineM = Integer.parseInt(Integer.toString(CacheMemoryD[Linea].getEtiqueta()), 2) * 8;
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

                    Time += 0.75;
                    CacheMemoryD[Linea].setValid(true);
                    CacheMemoryD[Linea].setModify(true);

                } else {
                    int firstLine = Integer.parseInt(Integer.toString(Etiqueta), 2) * 8;
                    int count = 0;
                    for (int j = firstLine; j < firstLine + 8; j++) {
                        CacheMemoryD[Linea].getPalabra()[count] = RAM[j];
                        count++;
                    }

                    Time += 0.66;
                    CacheMemoryD[Linea].setModify(true);
                }
                CacheMemoryD[Linea].setEtiqueta(Etiqueta);
                CacheMemoryD[Linea].getPalabra()[Palabra] = v;
            }
        } else {
            int firstLine = Integer.parseInt(Integer.toString(Etiqueta), 2) * 8;
            int count = 0;

            for (int j = firstLine; j < firstLine + 8; j++) {
                CacheMemoryD[Linea].getPalabra()[count] = RAM[j];
                count++;
            }

            CacheMemoryD[Linea].setEtiqueta(Etiqueta);
            CacheMemoryD[Linea].setValid(true);
            CacheMemoryD[Linea].setModify(true);
            CacheMemoryD[Linea].getPalabra()[Palabra] = v;
            Time += 0.66;
        }
    }

    static void EscribirCacheAsociativoPorConjunto(int i, int v) {
        int Etiqueta = Integer.parseInt(Integer.toBinaryString(0x1000 | i).substring(1).substring(0, 5));
        int Conjunto = Integer.parseInt(Integer.toBinaryString(0x1000 | i).substring(1).substring(5, 9), 2);
        int Palabra = Integer.parseInt(Integer.toBinaryString(0x1000 | i).substring(1).substring(9, 12), 2);

        int Linea = -1;
        for (int j = 0; j < Conjuntos[Conjunto].getLines().length; j++) {
            if (Conjuntos[Conjunto].getLines()[j].getEtiqueta() == Etiqueta) {
                Linea = j;
            }
        }

        if (Linea == -1) {
            Linea = Conjuntos[Conjunto].getNextLine();
        }

        if (Conjuntos[Conjunto].getLines()[Linea].isValid()) {
            if (Conjuntos[Conjunto].getLines()[Linea].getEtiqueta() == Etiqueta) {
                Conjuntos[Conjunto].getLines()[Linea].setModify(true);
                Conjuntos[Conjunto].getLines()[Linea].getPalabra()[Palabra] = v;
                Time += 0.01;
            } else {
                if (Conjuntos[Conjunto].getLines()[Linea].isModify()) {
                    String SBloqueN = Integer.toString(Conjuntos[Conjunto].getLines()[Linea].getEtiqueta()) + Integer.toBinaryString(Conjunto);
                    int firstLineN = Integer.parseInt(SBloqueN, 2) * 8;
                    int count = 0;

                    for (int j = firstLineN; j < firstLineN + 8; j++) {
                        RAM[j] = Conjuntos[Conjunto].getLines()[Linea].getPalabra()[count];
                        count++;
                    }

                    String SBloque = Integer.toString(Etiqueta) + Integer.toBinaryString(Conjunto);
                    int firstLine = Integer.parseInt(SBloque, 2) * 8;

                    count = 0;
                    for (int j = firstLine; j < firstLine + 8; j++) {
                        Conjuntos[Conjunto].getLines()[Linea].getPalabra()[count] = RAM[j];
                        count++;
                    }

                    Time += 0.75;
                    Conjuntos[Conjunto].getLines()[Linea].setValid(true);
                    Conjuntos[Conjunto].getLines()[Linea].setModify(true);

                } else {
                    String SBloque = Integer.toString(Etiqueta) + Integer.toBinaryString(Conjunto);
                    int firstLine = Integer.parseInt(SBloque, 2) * 8;
                    int count = 0;

                    for (int j = firstLine; j < firstLine + 8; j++) {
                        Conjuntos[Conjunto].getLines()[Linea].getPalabra()[count] = RAM[j];
                        count++;
                    }

                    Time += 0.66;
                    Conjuntos[Conjunto].getLines()[Linea].setValid(true);
                    Conjuntos[Conjunto].getLines()[Linea].setModify(true);

                }
                Conjuntos[Conjunto].getLines()[Linea].setEtiqueta(Etiqueta);
                Conjuntos[Conjunto].getLines()[Linea].getPalabra()[Palabra] = v;
            }
        } else {
            String SBloque = Integer.toString(Etiqueta) + Integer.toBinaryString(Conjunto);
            int firstLine = Integer.parseInt(SBloque, 2) * 8;
            int count = 0;

            for (int j = firstLine; j < firstLine + 8; j++) {
                Conjuntos[Conjunto].getLines()[Linea].getPalabra()[count] = RAM[j];
                count++;
            }

            Time += 0.66;
            Conjuntos[Conjunto].getLines()[Linea].setEtiqueta(Etiqueta);
            Conjuntos[Conjunto].getLines()[Linea].setValid(true);
            Conjuntos[Conjunto].getLines()[Linea].setModify(true);
            Conjuntos[Conjunto].getLines()[Linea].getPalabra()[Palabra] = v;

        }
    }
}
