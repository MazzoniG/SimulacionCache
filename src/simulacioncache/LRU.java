/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package simulacioncache;
/**
 *
 * @author OWNER
 */

import java.util.HashMap;
import java.util.Map;

public class LRU {
     SpecialCursorList list;
     Map<Integer, Integer> tags; // Clave etiqueta, valor LíneaCache.
     Map<Integer, Integer> arrayPos; // Clave LineaCache, Valor Linea del arreglo

    public LRU() {
        list =new  SpecialCursorList(64);
        tags = new HashMap<>();
        arrayPos = new HashMap<>();
        for (int i = 0; i < 64; i++) {
            list.insert(i, i);
            arrayPos.put(i, i);
        }
    }
    
    public LRU(int ini) {
        list =new  SpecialCursorList(ini);
        tags = new HashMap<>();
        arrayPos = new HashMap<>();
        for (int i = 0; i < ini; i++) {
            list.insert(i, i);
            arrayPos.put(i, i);
        }
    }
     
    int consult(int tag){
        //Map<Integer, Integer> tags; // Clave etiqueta, valor LíneaCache.
        //Map<Integer, Integer> arrayPos; // Clave LineaCache, Valor Linea del arreglo
        int line;
        if (tags.containsKey(tag)) {
            line = tags.get(tag);
            int arrayLine = arrayPos.get(line);
            list.floatToSurface(arrayLine);
        }else{
            int leastRecentlyUsed =(Integer) list.last();
            tags.put(tag,leastRecentlyUsed);
            int arrayLine = arrayPos.get(leastRecentlyUsed);
            line = leastRecentlyUsed;
            list.floatToSurface(arrayLine);
        }
        return line;
    }
}
