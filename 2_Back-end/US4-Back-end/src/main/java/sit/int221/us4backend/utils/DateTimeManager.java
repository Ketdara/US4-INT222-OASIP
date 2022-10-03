package sit.int221.us4backend.utils;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class DateTimeManager {
    private static final DateTimeManager dateTimeManager = new DateTimeManager();
    private DateTimeManager() { }
    public static DateTimeManager getInstance() {
        return dateTimeManager;
    }

    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private SimpleDateFormat ISOFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.sss'Z'");

    private SimpleDateFormat emailFormatFirst = new SimpleDateFormat("EEE MMM d, yyyy HH:mm");
    private SimpleDateFormat emailFormatSecond = new SimpleDateFormat(" - HH:mm (z)");

    public String dateStringToISOString(String dateString) {
        if(dateString == null) return null;
        Date date = dateStringToDate(dateString);
        return ISOFormat.format(date);
    }

    public String ISOStringToDateString(String ISOString) {
        if(ISOString == null) return null;
        Date date = ISOStringToDate(ISOString);
        return dateFormat.format(date);
    }

    public Date dateStringToDate(String dateString) {
        if(dateString == null) return null;
        try {
            return dateFormat.parse(dateString);
        }catch(ParseException p) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Date/Time format not supported");
        }
    }

    public Date ISOStringToDate(String ISOString) {
        if(ISOString == null) return null;
        try {
            return ISOFormat.parse(ISOString);
        }catch(ParseException p) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Date/Time format not supported");
        }
    }

    public String dateStringToEndDateISOString(String dateString, Integer duration) {
        Date date = dateStringToDate(dateString);
        Date endDate = dateToEndDate(date, duration);
        return dateToISOString(endDate);
    }

    public Date dateToEndDate(Date date, Integer duration) {
        return new Date(date.getTime() + (60000 * duration));
    }

    public String dateToISOString(Date date) {
        return ISOFormat.format(date);
    }

    public String dateToDateString(Date date) {
        return dateFormat.format(date);
    }

    public String ISOStringToOffsetDateString(String ISOString, Integer offsetHour) {
        if(ISOString == null) return null;
        if(offsetHour == null) return null;

        Long selectedTime = dateTimeManager.ISOStringToDate(ISOString).getTime();
        return dateTimeManager.dateToDateString(new Date(selectedTime + (3600000 * offsetHour)));
    }

    public String getEmailDate(String dateString, Integer duration) {
        if(dateString == null || duration == null) return null;
        Date startDate = dateStringToDate(dateString);
        Date endDate = dateToEndDate(startDate, duration);

        emailFormatFirst.setTimeZone(TimeZone.getTimeZone("Asia/Bangkok"));
        emailFormatSecond.setTimeZone(TimeZone.getTimeZone("Asia/Bangkok"));

        String firstHalf = emailFormatFirst.format(startDate);
        String secondHalf = emailFormatSecond.format(endDate);

        return firstHalf.concat(secondHalf);
    }
}
