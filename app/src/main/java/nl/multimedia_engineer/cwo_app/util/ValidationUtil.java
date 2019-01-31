package nl.multimedia_engineer.cwo_app.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ValidationUtil {
    public static boolean isValidEmailAddress(String email) {
        if(email == null || email.isEmpty() )
            return false;
        Pattern pattern = Pattern.compile("^.+@.+\\..+$");
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }
}
