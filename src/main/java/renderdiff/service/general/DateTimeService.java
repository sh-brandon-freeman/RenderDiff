package renderdiff.service.general;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 *
 */
public class DateTimeService {
    /**
     * Get a timestamp
     *
     * @param format Timestamp format
     * @return Formatted timestamp
     */
    public static String getTimestamp(String format) {
        if (format == null) {
            format = "yyyy-MM-dd HH:mm:ss";
        }
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);
        Date now = new Date();
        return simpleDateFormat.format(now);
    }
}
