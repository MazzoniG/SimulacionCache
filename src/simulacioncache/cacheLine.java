
package simulacioncache;

/**
 *
 * @author Guillermo E. Mazzoni & Juan C. Martinez
 */

import java.util.*;

public class cacheLine {
    
    int Etiqueta;
    boolean Value;
    boolean Modify;
    int[] Palabra = new int[8];
   
    
    int recoverWord(int i){
        return Palabra[i];
    }
}
