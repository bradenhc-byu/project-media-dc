/*
 * ---------------------------------------------------------------------------------------------------------------------
 *                            Brigham Young University - Project MEDIA StillFace DataCenter
 * ---------------------------------------------------------------------------------------------------------------------
 * The contents of this file contribute to the ProjectMEDIA DataCenter for managing and analyzing data obtained from the
 * results of StillFace observational experiments.
 *
 * This code is free, open-source software. You may distribute or modify the code, but Brigham Young University or any
 * parties involved in the development and production of this code as downloaded from the remote repository are not
 * responsible for any repercussions that come as a result of the modifications.
 */
package com.byu.pmedia.log;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Formatter;
import java.util.logging.*;

/**
 * PMLogHTMLFormatter
 * Formats the logs in HTML so that it can be more easily displayed to the user on the log tab of the settings GUI.
 *
 * @author Lars Vogel (c) 2007 from Vogella, modified by Braden Hitchcock
 */
public class PMLogHTMLFormatter extends Formatter {

    /**
     * Called when ever log message is recorded. This will format the log as HTML, making it easier to read from
     * a file rendered in the GUI as opposed to text.
     *
     * @param rec The log record to format
     * @return A string representing the formatted log record
     */
    public String format(LogRecord rec) {
        StringBuffer buf = new StringBuffer(1000);
        buf.append("<tr>\n");

        // colorize any levels >= WARNING in red
        if (rec.getLevel().intValue() >= Level.WARNING.intValue()) {
            buf.append("\t<td style=\"color:red\">");
            buf.append("<b>");
            buf.append(rec.getLevel());
            buf.append("</b>");
        } else {
            buf.append("\t<td>");
            buf.append(rec.getLevel());
        }

        buf.append("</td>\n");
        buf.append("\t<td>");
        buf.append(calcDate(rec.getMillis()));
        buf.append("</td>\n");
        buf.append("\t<td>");
        buf.append(formatMessage(rec));
        buf.append("</td>\n");
        buf.append("</tr>\n");

        return buf.toString();
    }

    /**
     * Calculates the data given the current time in milliseconds
     *
     * @param millisecs Current time in milliseconds
     * @return A string representation of the date
     */
    private String calcDate(long millisecs) {
        SimpleDateFormat date_format = new SimpleDateFormat("MMM dd,yyyy HH:mm");
        Date resultdate = new Date(millisecs);
        return date_format.format(resultdate);
    }

    /**
     * Called just after the handler that utilizes this formated is created. This allows us to create the HTML header
     * and write it to the current file so that the file will actually rendor HTML.
     *
     * @param h The handler using this formater
     * @return A string representing the header of the file to write to
     */
    public String getHead(Handler h) {
        return "<!DOCTYPE html>\n<head>\n<style>\n"
                + "table { width: 100% }\n"
                + "th { font:bold 10pt Tahoma; }\n"
                + "td { font:normal 10pt Tahoma; }\n"
                + "h1 {font:normal 11pt Tahoma;}\n"
                + "</style>\n"
                + "</head>\n"
                + "<body>\n"
                + "<h1>" + (new Date()) + "</h1>\n"
                + "<table border=\"0\" cellpadding=\"5\" cellspacing=\"3\">\n"
                + "<tr align=\"left\">\n"
                + "\t<th style=\"width:10%\">Loglevel</th>\n"
                + "\t<th style=\"width:15%\">Time</th>\n"
                + "\t<th style=\"width:75%\">Log Message</th>\n"
                + "</tr>\n";
    }

    /**
     * Method called just after the handler that is utilizing this formatter is closed. Adds the closing HTML
     * tags to the file
     *
     * @param h The handler using this formatter
     * @return The string representation of the HTML tail (closing tags)
     */
    public String getTail(Handler h) {
        return "</table>\n</body>\n</html>";
    }
}
