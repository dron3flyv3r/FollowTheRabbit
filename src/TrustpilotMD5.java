import java.io.*;
import java.util.ArrayList;

public class TrustpilotMD5 extends Thread {
    static String anagram = "poultry outwits ants";
    static byte[] anagramChars = new byte[26];
    static int anagramLength = anagram.length();
    static ArrayList<String> wordList = new ArrayList<String>();
    static long startTime;
    static int threadCount;
    //hashes
    private static final String hash = "e4820b45d2277f3844eac66c903e84be";
    //private static final String hash = "23170acc097c24edb98fc5488ab033fe";
    //private static final String hash = "665e5bcb0c20062fe8abaaf4628bb154";

    TrustpilotMD5(int numberOfThreads, boolean full) {
        if (full) {
            threadCount = numberOfThreads;
            anagramToArray();
            readWordList();
        }
        startTime = System.nanoTime();
        generateAnagrams();
    }

    public void checkAgain() {
        generateAnagrams();
    }

    public static void anagramToArray() {
        //DONE - convert anagram to anagramChars array
        // 1 - create a loop for reading each consecutive char in the anagram
        for (int i=0 ; i<anagramLength ; i++) {
            // 2 - check if the read char is a letter from a-z (or we can simply assume it is)
            char tempChar = anagram.charAt(i);
            if (tempChar >= 'a' && tempChar<='z') {
                // 3 - add +1 to the spot in the anagramChars array, which corresponds to the given char
                anagramChars[tempChar -'a']++;
            }
        }
    }

    //specific function for checking if a string s is an anagram of the global string 'anagram'
    public static boolean isAnagram(String s) {
        //throw away obvious failed anagrams (assuming 3 word anagrams)
        if (s.length() != anagramLength) return false;
        // 1 - convert s to a char array like the one used for the anagram
        byte[] tempChars = new byte[26];
        for (int i=0 ; i<anagramLength ; i++) {
            char tempChar = s.charAt(i);
            if (tempChar != ' ') {
                tempChars[tempChar -'a']++;
            }
        }
        // 2 - compare the number of elements in both arrays to see if they match
        for (int i=0 ; i<26 ; i++) {
            if (tempChars[i] != anagramChars[i]) return false;
        }
        //if we made it here, everything matched
        return true;
    }

    //general function for checking if two strings are anagrams of each other
    public static boolean isAnagram(String s1, String s2) {
        byte[] tempChars1 = new byte[26];
        byte[] tempChars2 = new byte[26];
        int length;
        // 1 - convert s1 and s2 to a char arrays
        length = s1.length();
        for (int i=0 ; i<length ; i++) {
            char tempChar1 = s1.charAt(i);
            if (tempChar1 != ' ') {
                tempChars1[tempChar1 -'a']++;
            }
        }
        length = s2.length();
        for (int i=0 ; i<length ; i++) {
            char tempChar2 = s2.charAt(i);
            if (tempChar2 != ' ') {
                tempChars2[tempChar2 -'a']++;
            }
        }
        // 2 - compare the number of elements in both arrays to see if they match
        for (int i=0 ; i<26 ; i++) {
            if (tempChars1[i] != tempChars2[i]) return false;
        }
        //if we made it here, everything matched
        return true;

    }

    //check if a given string can be written using the letters available from the anagram
    public static boolean lettersAvailable(String s) {
        byte[] tempChars = new byte[26];
        // convert s to tempChars array
        int length = s.length();
        for (int i=0 ; i<length ; i++) {
            char tempChar = s.charAt(i);
            if (tempChar >= 'a' && tempChar<='z') {
                tempChars[tempChar -'a']++;
            } else {
                //if we make it here, the char wasn't a letter from a-z
                return false;
            }
        }
        // compare the number of elements in both arrays to see if we can write the string
        for (int i=0 ; i<26 ; i++) {
            if (tempChars[i] > anagramChars[i]) return false;
        }
        return true;
    }

    //function for reading the wordlist into memory, while only adding words we can actually write
    public static void readWordList() {
        try {
            File myFile = new File("src/wordlist.txt");
            FileReader myFileReader = new FileReader(myFile);
            BufferedReader myReader = new BufferedReader(myFileReader);

            //time to read the entire user file
            String word;
            String lastWord = "";

            while((word = myReader.readLine()) != null) {
                if (lettersAvailable(word)) {
                    if (!word.equals(lastWord)) {
                        if ( word.length()>1) {
                            wordList.add(word);
                            lastWord = word;
                        }
                    }
                }
            }
            //add single characters
            wordList.add("a");
            wordList.add("i");
            wordList.add("o");

            System.out.println("Words addded = "+wordList.size());
            myReader.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //function for generating and checking anagrams
    public void generateAnagrams() {
        //1 - generate 3 word sentences
        //2 - check if the sentences are anagrams
        //3 - check if any anagram has the right MD5
        int length = wordList.size();
        String testPhrase;

        double startTime = System.nanoTime();

        for (int t=0; t < threadCount; t++){
            int startThread = t * length / threadCount;
            new Thread(new Runnable() {
                @Override
                public void run() {
                    for (int i = startThread; i < startThread + length / threadCount; i++) {
                        for (int j = 0; j < length; j++) {
                            for (int k = 0; k < length; k++) {

                                    String testPhrase = wordList.get(i) + " " + wordList.get(j) + " " + wordList.get(k);
                                if (isAnagram(testPhrase)) {
                                    if (Hashing.MD5Hash(testPhrase).equals(hash)) {
                                        //System.out.println("Time: " + ((System.nanoTime() - startTime) / 1000000) + "ms");
                                        //System.out.println("Found anagram: " + testPhrase);
                                        //System.exit(0);
                                        long endTime = System.nanoTime();
                                        // save the time to a txt file
                                        Main.AddTime((float) (endTime - startTime) / 1000000);
                                    }
                                }
                            }
                        }
                    }
                }
            }).start();
        }
    }
}