package uk.gov.hmcts.ccd.automated.email.reader;

import java.time.LocalDate;
import java.util.Date;

public class formatDate {


    private static String convertMonth(String month){
        String convertedMonth;
        switch(month){
            case "Jan":
                convertedMonth = "01";
                break;
            case "Feb":
                convertedMonth = "02";
                break;
            case "Mar":
                convertedMonth = "03";
                break;
            case "Apr":
                convertedMonth = "04";
                break;
            case "May":
                convertedMonth = "05";
                break;
            case "Jun":
                convertedMonth = "06";
                break;
            case "Jul":
                convertedMonth = "07";
                break;
            case "Aug":
                convertedMonth = "08";
                break;
            case "Sep":
                convertedMonth = "09";
                break;
            case "Nov":
                convertedMonth = "10";
                break;
            case "Oct":
                convertedMonth = "11";
                break;
            case "Dec":
                convertedMonth = "12";
                break;
            default:
                convertedMonth = "ERROR";
        }
        return convertedMonth;
    }

    public static LocalDate formattedDate(Date date){
        String sentDate = date.toString();

        String month = convertMonth(sentDate.substring(4, 7));
        String day = sentDate.substring(8,10);
        String year = sentDate.substring(24, 28);

        sentDate = year + "-" + month + "-" + day;

        return LocalDate.parse(sentDate);
    }
}
