package kh.ad.quakereport.tools;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class HelperMethods {
    public static String placeFormat(String place){
        String place1 = "", place2;
        if (place.contains("of")) {
            place1 = place.substring(0, place.indexOf("of") + 2);
            place2 = place.substring(place.indexOf("of") + 3);
        }else {
            place2 = place;
        }
        return place1+"/"+place2;
    }

    public static String dateFormat(long time){
        Date dateObject = new Date(time);
        SimpleDateFormat dateFormatter1 = new SimpleDateFormat("MMM dd, yyyy", Locale.ENGLISH);
        String dateToDisplay1 = dateFormatter1.format(dateObject);
        SimpleDateFormat dateFormatter2 = new SimpleDateFormat("HH:mm a", Locale.ENGLISH);
        String dateToDisplay2 = dateFormatter2.format(dateObject);
        return dateToDisplay1+"/"+dateToDisplay2;
    }
}
