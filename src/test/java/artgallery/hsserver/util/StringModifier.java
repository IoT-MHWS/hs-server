package artgallery.hsserver.util;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * StringModifier
 */
public class StringModifier {

    public static String getUniqueString(String str){
        return str + new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
    }
}
