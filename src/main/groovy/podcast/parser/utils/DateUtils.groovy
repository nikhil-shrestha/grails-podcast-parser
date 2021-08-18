package podcast.parser.utils

import java.text.ParseException
import java.text.SimpleDateFormat

class DateUtils {

  public static Date stringToDate(String dt) {
    SimpleDateFormat[] dateFormats = {
      new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss Z", Locale.US)
      new SimpleDateFormat("dd MMM yyyy HH:mm:ss Z", Locale.US)
      new SimpleDateFormat("EEE, dd MMM yyyy HH:mm Z", Locale.US)
      new SimpleDateFormat("dd MMM yyyy HH:mm Z", Locale.US)
      new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'")
    };

    Date date = null;

    for (SimpleDateFormat dateFormat : dateFormats) {
      try {
        dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
        date = dateFormat.parse(dt);
        break;
      } catch (ParseException e) {
        //This format didn't work, keep going
      }
    }

    return date;
  }

  public static int compareDates(Date prevReleaseDate, Date newReleaseDate) {
    int result = prevReleaseDate.compareTo(newReleaseDate);
    return result;
  }
}
