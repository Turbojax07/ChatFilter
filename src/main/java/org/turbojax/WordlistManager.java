package org.turbojax;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import org.jetbrains.annotations.NotNull;
import org.turbojax.config.MainConfig;

public class WordlistManager {
    private static File wordlist;
    private static final List<String> blockedWords = new ArrayList<>();

    public WordlistManager() {
        wordlist = new File(MainConfig.wordlistFile());
    }

    public static void addWord(String word) {
        blockedWords.add(word);
        // WORDLIST_ADDED_WORD("wordlist-added-word", "%prefix%Added \"%word%\" to the blocked word list.")
    }

    public static void removeWord(String word) {
        blockedWords.remove(word);
        // WORDLIST_REMOVED_WORD("wordlist-removed-word", "%prefix%Removed \"%word%\" from the blocked word list.")
    }

    @NotNull
    public static List<String> getBannedWords(String message) {
        List<String> foundWords = new ArrayList<>();
        for (String blockedWord : blockedWords) {
            if (message.contains(" " + blockedWord + " ")) {
                foundWords.add(blockedWord);
            }
        }

        return foundWords;
    }

    public static List<String> getBlockedwords() {
        return blockedWords;
    }

    public static void reloadBlockedWords() {
        List<String> blocked = new ArrayList<>();
        try(Scanner scanner = new Scanner(wordlist)) {
            while (scanner.hasNextLine()) blocked.add(scanner.nextLine());
        } catch (FileNotFoundException e) {
            // CANNOT_READ_WORDLIST("cannot-read-wordlist", "%prefix%<red>Could not read the wordlist from %file%.")
            return;
        }

        blockedWords.clear();
        blockedWords.addAll(blocked);
        // WORDLIST_RELOADED("wordlist-reloaded", "%prefix%The wordlist has been reloaded.")
    }
}
