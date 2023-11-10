import java.util.*;
import java.io.*;
import edu.duke.*;

public class VigenereBreaker {
    public String sliceString(String message, int whichSlice, int totalSlices) {
        StringBuilder slice = new StringBuilder();
        for (int i = whichSlice; i < message.length(); i+=totalSlices) {
            slice.append(message.charAt(i));
        }
        return slice.toString();
    }

    public int[] tryKeyLength(String encrypted, int klength, char mostCommon) {
        int[] key = new int[klength];
        CaesarCracker caesar = new CaesarCracker(mostCommon);
        for (int i = 0; i < klength; i+=1) {
         String slice = sliceString (encrypted, i, klength);
         key[i] = caesar.getKey(slice);
         //System.out.println(key[i]);
        }
        return key;
    }

    public void breakVigenere () {
        FileResource file = new FileResource();
        String input = file.asString();
        HashMap<String, HashSet<String>> languages = new HashMap<String, HashSet<String>>();
        DirectoryResource dictionaries = new DirectoryResource();
        for (File fr : dictionaries.selectedFiles()) {
            FileResource currDic = new FileResource(fr);
            languages.put(fr.getName(), readDictionary(currDic));
        }
        System.out.println(breakForAllLangs(input, languages).substring(0,100));
    }
    
    public HashSet<String> readDictionary (FileResource fr) {
     HashSet<String> dictionary = new HashSet<String>();
     for (String word : fr.lines()) {
         dictionary.add(word.toLowerCase());
        }
     return dictionary;
    }
    
    public int countWords (String message, HashSet<String> dictionary) {
     String[] words = message.split("\\W+");
     int count = 0;
     for (int i = 0; i<words.length; i+=1) {
         if (dictionary.contains(words[i].toLowerCase())) {
             count +=1;
            }
        }
     return count;
    }
    
    public String breakForLanguage (String encrypted, HashSet<String> dictionary) {
     int count = 0;
     int[] bestKey = tryKeyLength(encrypted, 1, 'e');
     char bestChar = mostCommonCharIn(dictionary);
     for (int key = 1; key<=100; key+=1) {
         int[] currKey = tryKeyLength(encrypted, key, bestChar);
         VigenereCipher message = new VigenereCipher(currKey);
         String decrypted = message.decrypt(encrypted);
         int currCount = countWords(decrypted, dictionary);
         if (currCount > count) {
             count = currCount;
             bestKey = currKey;
            }
         //if (key == 38) {
         //    System.out.println(currCount);
         //   }
        }
     VigenereCipher finalMessage = new VigenereCipher(bestKey);
     //System.out.println(bestKey.length);
     //System.out.println(count);
     return finalMessage.decrypt(encrypted);
    }
    
    public char mostCommonCharIn (HashSet<String> dictionary) {
     HashMap<Character, Integer> chars = new HashMap<Character, Integer>();
     for (char alphabet = 'a'; alphabet<='z'; alphabet++) {
         chars.put(alphabet, 0);
        }
     for (String word : dictionary) {
         for (int i = 0; i<word.length(); i+=1) {
             char currChar = Character.toLowerCase(word.charAt(i));
             if (chars.containsKey(currChar)) {
                 chars.put(currChar, chars.get(currChar) + 1);
                }
            }
        }
     int max = 0;
     char finalChar = 'a';
     for (char maxChar : chars.keySet()) {
         if (chars.get(maxChar) > max) {
             max = chars.get(maxChar);
             finalChar = maxChar;
            }
        }
     return finalChar;
    }
    
    public String breakForAllLangs (String encrypted, HashMap<String, HashSet<String>> languages) {
     String bestDecr = "";
     int bestCount = 0;
     String bestLang = "";
        for (String language : languages.keySet()) {
         String currDecr = breakForLanguage(encrypted, languages.get(language));
         int currCount = countWords(currDecr, languages.get(language));
         if (currCount > bestCount) {
             bestCount = currCount;
             bestDecr = currDecr;
             bestLang = language;
            }
        }
     System.out.println(bestLang);
     return bestDecr;
    }
}
