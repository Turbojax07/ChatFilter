package org.turbojax;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.jetbrains.annotations.NotNull;
import org.turbojax.config.MainConfig;

public class WordlistManager {
    private static File wordlist = new File(MainConfig.wordlistFile());
    private static final List<String> blockedWords = new ArrayList<>();

    public static void addWord(String word) {
        blockedWords.add(word);
        // WORDLIST_ADDED_WORD("wordlist-added-word", "%prefix%Added \"%word%\" to the blocked word list.")
    }

    public static void removeWord(String word) {
        blockedWords.remove(word);
        // WORDLIST_REMOVED_WORD("wordlist-removed-word", "%prefix%Removed \"%word%\" from the blocked word list.")
    }

    @NotNull
    public static HashMap<Integer,String> getBannedWords(String message) {
        HashMap<Integer,String> foundWords = new HashMap<>();
        StringBuilder builder = new StringBuilder();

        blockedWords.forEach(w -> { builder.append("|"); builder.append(w); });
        Pattern pattern = Pattern.compile("\\b(" + builder.substring(1) + ")\\b", Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(message);
        
        while (matcher.find()) {
            foundWords.put(matcher.start(), matcher.group());
        }

        return foundWords;
    }

    public static List<String> getBlockedWords() {
        return blockedWords;
    }

    public static void reload() {
        // Downloading the wordlist if it does not exist and the wordlist is remote.
        if (!wordlist.exists() && MainConfig.useRemoteWordlist()) {
            redownload();
        }

        // Removing the existing words
        blockedWords.clear();

        // Loading the contents of the wordlist file into the blockedWords array.
        try(Scanner scanner = new Scanner(wordlist)) {
            while (scanner.hasNextLine()) blockedWords.add(scanner.nextLine());
        } catch (FileNotFoundException e) {
            // CANNOT_READ_WORDLIST("cannot-read-wordlist", "%prefix%<red>Could not read the wordlist from %file%.")
            return;
        }

        // WORDLIST_RELOADED("wordlist-reloaded", "%prefix%The wordlist has been reloaded.")
    }

    public static void redownload() {
        try {
            // Downloading the wordlist
            if (MainConfig.overrideWordlistFile()) {
                // TODO: WORDLIST_OVERRIDDEN("wordlist-overridden", "%prefix%The wordlist will be overridden if it exists.")
                Files.copy(new URI(MainConfig.wordlistUrl()).toURL().openStream(), wordlist.toPath(), StandardCopyOption.REPLACE_EXISTING);
            } else {
                Files.copy(new URI(MainConfig.wordlistUrl()).toURL().openStream(), wordlist.toPath());
            }
            // TODO: WORDLIST_SAVED("wordlist-saved", "%prefix%The wordlist has been saved.")
        } catch(FileAlreadyExistsException e) {
            // TODO: WORDLIST_OVERRIDE_STOPPED("wordlist-override-stopped", "%prefix%Cannot override the wordlist with the remote copy.")
        } catch (URISyntaxException | MalformedURLException e) {
            // TODO: WORDLIST_MALFORMED_URL("wordlist-malformed-url", "The provided URL \"%url%\" is not a valid link.")
        } catch (IOException e) {
            // TODO: WORDLIST_DOWNLOAD_ERROR("wordlist-download-error", "Could not download the wordlist from \"%url%\"")
        }
    }
}