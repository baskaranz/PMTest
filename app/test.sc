import java.util.Date

import org.joda.time.{DateTimeZone, DateTime}

val timestamp = 1469278218

val year = new DateTime(timestamp * 1000L).getYear