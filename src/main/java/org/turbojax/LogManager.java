package org.turbojax;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.stream.Stream;
import java.util.zip.GZIPOutputStream;
import javax.annotation.Nullable;
import org.turbojax.config.Message;

public class LogManager {
    public static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

    private static File logDir = new File("plugins/ChatFilter/logs");
    private static File logFile = null;

    public static void initialize() {
        String date = dateFormat.format(new Date());
        int nextId = 1;

        // Creating the log directory if needed
        logDir.mkdirs();
        
        try {
            Stream<Path> files = Files.list(logDir.toPath()) // Collecting log files
                    .filter(p -> p.toFile().getName().startsWith(date)); // Filtering out files not from today
            
            // Getting the id based on how many logs there are
            nextId += files.count();
        } catch (IOException e) {
            Message.sendToConsole("Could not open the log directory");
        }

        logFile = new File(logDir, date + "-" + nextId + ".log");
    }

    public static void log(String message) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("[yyyy-MM-dd: hh:mm:ss] ");

        if (logFile == null) {
            Message.sendToConsole("%prefix%<red>Cannot write to log file.  It hasn't been initialized yet!"); // LOG_FILE_NOT_FOUND
        }

        try(FileWriter writer = new FileWriter(logFile, true)) {
            writer.append(dateFormat.format(new Date()));
            writer.append(message);
            writer.append("\n");
            writer.flush();
        } catch (IOException e) {
            Message.sendToConsole("%prefix%<red>Failed to write to the log file."); // LOG_WRITE_FAILED
        }
    }

    /**
     * Compresses a file with GZ compression.
     * @param in The file to read from.
     * @param out The destination file to write the compressed data to.
     * @return Whether the compression was successful or not.
     */
    public static boolean gzipCompressFile(File in, File out) {
        try(FileInputStream inStream = new FileInputStream(in);
            GZIPOutputStream gzStream = new GZIPOutputStream(new FileOutputStream(out))) {
            gzStream.write(inStream.readAllBytes());
            gzStream.flush();

            Files.delete(in.toPath());

            return true;
        } catch (FileNotFoundException e) {
            Message.sendToConsole("%prefix%<red>Could not find the input file."); // LOG_INPUT_NOT_FOUND
        } catch (IOException e) {
            Message.sendToConsole("%prefix%<red>Could not write to the output file."); // LOG_COMPRESS_FAILED
        }

        return false;
    }

    @Nullable
    public static File getLogFile() {
        return logFile;
    }
}