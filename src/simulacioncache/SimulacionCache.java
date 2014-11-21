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
    static CacheLineSet[] Conjuntos = new CacheLineSet[16];
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

        int Tipo = 1;

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

        //System.out.println("IGNORE" + CacheAsociativa(4095));
        //System.out.println(Integer.toBinaryString(0x1000 | 6).substring(1).substring(9, 12).concat(Integer.toBinaryString(0x1000 | 63).substring(1).substring(6,12)));
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
        return RAM[i];
    }

    static void EscribirNoCache(int i, int v) {
        RAM[i] = v;
    }

    static int CacheCorrespondenciaDirecta(int i) {

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
                    int firstLine = Integer.parseInt(Integer.toString(Etiqueta), 2) * 8;
                    int firstLineM = Integer.parseInt(Integer.toHexString(CacheMemoryD[Linea].getEtiqueta()), 2) * 8;
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
            } else {
                if (CacheMemoryD[Linea].isModify()) {
                    int count = 0;

                    int BloqueC = Integer.parseInt(Integer.toBinaryString(0x1000 | CacheMemoryD[Linea].getEtiqueta()).substring(1).substring(9, 12).concat(Integer.toBinaryString(0x1000 | Linea).substring(1).substring(6, 12)));
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

                    CacheMemoryD[Linea].setModify(true);
                } else {

                    int count = 0;
                    for (int j = firstLine; j < firstLine + 8; j++) {
                        CacheMemoryD[Linea].getPalabra()[count] = RAM[j];
                        count++;
                    }

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
        }

    }

    static void EscribirCacheAsociativa(int i, int v) {
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
                CacheMemoryD[Linea].setModify(true);
                CacheMemoryD[Linea].getPalabra()[Palabra] = v;
            } else {
                if (CacheMemoryD[Linea].isModify()) {
                    int firstLine = Integer.parseInt(Integer.toString(Etiqueta), 2) * 8;
                    int firstLineM = Integer.parseInt(Integer.toHexString(CacheMemoryD[Linea].getEtiqueta()), 2) * 8;
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
                    CacheMemoryD[Linea].setModify(true);

                } else {
                    int firstLine = Integer.parseInt(Integer.toString(Etiqueta), 2) * 8;
                    int count = 0;
                    for (int j = firstLine; j < firstLine + 8; j++) {
                        CacheMemoryD[Linea].getPalabra()[count] = RAM[j];
                        count++;
                    }

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

            Conjuntos[Conjunto].getLines()[Linea].setEtiqueta(Etiqueta);
            Conjuntos[Conjunto].getLines()[Linea].setValid(true);
            Conjuntos[Conjunto].getLines()[Linea].setModify(true);

            Conjuntos[Conjunto].getLines()[Linea].getPalabra()[Palabra] = v;
        }
    }
}
