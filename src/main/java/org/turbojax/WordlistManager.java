package org.turbojax;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.jetbrains.annotations.NotNull;
import org.turbojax.config.MainConfig;
import org.turbojax.config.Message;

public class WordlistManager {
    private static File wordlist;
    private static final List<String> blockedWords = new ArrayList<>();

    /**
     * Adds a word to the blacklist.
     * Words are case-insensitive.
     * 
     * @param word The word to blacklist.
     * 
     * @return Returns true if the word was added.  Returns false if it is already in the list.
     */
    public static boolean addWord(String word) {
        if (blockedWords.contains(word)) return false;
        return blockedWords.add(word.toLowerCase());
    }

    /**
     * Removes a word from the blacklist.
     * Words are case-insensitive.
     * 
     * @param word The word to remove from the blacklist
     * 
     * * @return Returns true if the word was removed.  Returns false if it is not in the list.
     */
    public static boolean removeWord(String word) {
        return blockedWords.remove(word.toLowerCase());
    }

    /**
     * Checks a string for any blocked words.
     * It returns a map of each word and its index in the string.
     * 
     * @param text The string to check for blocked words.
     * 
     * @return A map of every word and its index in the string.
     */
    @NotNull
    public static HashMap<Integer,String> getBannedWords(String text) {
        HashMap<Integer,String> foundWords = new HashMap<>();
        StringBuilder builder = new StringBuilder();

        blockedWords.forEach(w -> { builder.append("|"); builder.append(w); });
        Pattern pattern = Pattern.compile("\\b(" + builder.substring(1) + ")\\b", Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(text);
        
        while (matcher.find()) {
            foundWords.put(matcher.start(), matcher.group());
        }

        return foundWords;
    }

    /**
     * Gets the list of blocked words.
     * 
     * @return The list of blocked words.
     */
    public static List<String> getBlockedWords() {
        return blockedWords;
    }

    /**
     * Reloads the wordlist file.
     * It will also redownload the remote wordlist if necessary.
     */
    public static void reload() {
        // Downloading the wordlist if it does not exist and the wordlist is remote.
        if (!wordlist.exists() && MainConfig.useRemoteWordlist()) {
            redownload();
        }

        // Redefining the wordlist
        wordlist = new File("plugins/ChatFilter/" + MainConfig.wordlistFile());

        // Removing the existing words
        blockedWords.clear();

        Map<String,String> placeholders = Message.getCommonPlaceholders();
        placeholders.put("%file%", wordlist.getName());

        // Loading the contents of the wordlist file into the blockedWords array.
        try(Scanner scanner = new Scanner(wordlist)) {
            while (scanner.hasNextLine()) blockedWords.add(scanner.nextLine());
        } catch (FileNotFoundException e) {
            Message.sendToConsole(Message.CANNOT_READ_WORDLIST, placeholders);
            return;
        }

        Message.sendToConsole(Message.WORDLIST_RELOADED, placeholders);
    }

    /**
     * Saves the active wordlist to the file.
     */
    public static void save() {
        Map<String,String> placeholders = Message.getCommonPlaceholders();
        placeholders.put("%file%", wordlist.getName());

        // Loading the contents of the wordlist file into the blockedWords array.
        try(FileWriter writer = new FileWriter(wordlist)) {
            // Deleting the original file
            Files.delete(wordlist.toPath());

            // Loading the list into the new file.
            for (String word : blockedWords) {
                writer.append(word + "\n");
                writer.flush();
            }
        } catch (IOException e) {
            Message.sendToConsole(Message.WORDLIST_CANNOT_SAVE, placeholders);
            return;
        }

        Message.sendToConsole(Message.WORDLIST_SAVED, placeholders);
    }

    /** 
     * Redownloads the wordlist file.
     * It doesn't overwrite the local wordlist unless specified in the config.
     */
    public static void redownload() {
        Map<String,String> placeholders = Message.getCommonPlaceholders();
        placeholders.put("%file%", wordlist.getName());
        placeholders.put("%url%", MainConfig.wordlistUrl());

        try {
            // Downloading the wordlist
            if (!wordlist.exists() || MainConfig.overrideWordlistFile()) {
                Message.sendToConsole(Message.WORDLIST_OVERRIDDEN, placeholders);
                Files.copy(new URI(MainConfig.wordlistUrl()).toURL().openStream(), wordlist.toPath(), StandardCopyOption.REPLACE_EXISTING);
            }
            Message.sendToConsole(Message.WORDLIST_SAVED, placeholders);
        } catch (URISyntaxException | MalformedURLException e) {
            Message.sendToConsole(Message.WORDLIST_MALFORMED_URL, placeholders);
        } catch (IOException e) {
            Message.sendToConsole(Message.WORDLIST_DOWNLOAD_ERROR, placeholders);
        }
    }
}