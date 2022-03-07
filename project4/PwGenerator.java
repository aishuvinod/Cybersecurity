import java.util.*;
import java.io.*;
import java.net.*;



// password generator that randomly generators 4 words put together, random number of words with caps (chosen by user), randomly placed symbols, and 
// random numbers
public class PwGenerator {

   
   private static int length;
   // file of words from which the password generator randomly pulls words from
   private final static String fileName = "words.txt";
   private static File file;
  


    public static void main(String[] args) throws IOException, URISyntaxException {

        ArrayList<String> arr = new ArrayList<String> (Arrays.asList(args));

        // default number of words
        int randwords = 4;
        // default number of words that are in caps lock
        int capslock = 0;
        // default number of numbers in the generated password
        int randnumbers = 0;
        // default number of symbols in the generated password 
        int randsymbols = 0;

        
        File base = new File
                (Thread.currentThread().getContextClassLoader().getResource("").toURI());

        // constructor that assigns words.txt to a file
        file = new File(base, fileName);

        // throws an exception when file is not found 
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(file));
        }catch(FileNotFoundException exception) {
            throw exception;
        }
        length = 0;
        while(reader.readLine() !=  null) {
            length++;
        }
        reader.close();


        boolean print = true;


        if(arr.contains("-h") || arr.contains("--help")) {
            System.out.println("usage: xkcdpwgen [-h] [-w WORDS] [-c CAPS] [-n NUMBERS] [-s SYMBOLS]\n"
                + "                \n"
                + "Generate a secure, memorable password using the XKCD method\n"
                + "                \n"
                + "optional arguments:\n"
                + "    -h, --help            show this help message and exit\n"
                + "    -w WORDS, --words WORDS\n"
                + "                          include WORDS words in the password (default=4)\n"
                + "    -c CAPS, --caps CAPS  capitalize the first letter of CAPS random words\n"
                + "                          (default=0)\n"
                + "    -n NUMBERS, --numbers NUMBERS\n"
                + "                          insert NUMBERS random numbers in the password\n"
                + "                          (default=0)\n"
                + "    -s SYMBOLS, --symbols SYMBOLS\n"
                + "                          insert SYMBOLS random symbols in the password\n"
                + "                          (default=0)");

            print = false;
        }


     
        if(arr.contains("-w") || arr.contains("--words")) {
            int index = arr.indexOf("-w");
            if(arr.indexOf("--words") > index) {
                index = arr.indexOf("--words");
            }
            randwords = Integer.parseInt(arr.get(index + 1));
            print = true;
        }

   
        if(arr.contains("-c") || arr.contains("--caps")) {
            int index = arr.indexOf("-c");
            if(arr.indexOf("--caps") > index) {
                index = arr.indexOf("--caps");
            }
            capslock = Integer.parseInt(arr.get(index + 1));
            print = true;
        }

    
        if(arr.contains("-n") || arr.contains("--numbers")) {
            int index = arr.indexOf("-n");
            if(arr.indexOf("--numbers") > index) {
                index = arr.indexOf("--numbers");
            }
            randnumbers = Integer.parseInt(arr.get(index + 1));
            print = true;
        }

      
        if(arr.contains("-s") || arr.contains("--symbols")) {
            int index = arr.indexOf("-s");
            if(arr.indexOf("--symbols") > index) {
                index = arr.indexOf("--words");
            }
            randsymbols = Integer.parseInt(arr.get(index + 1));
            print = true;
        }

        if(print) {
            System.out.println(generatePassword(randwords, capslock, randnumbers, randsymbols));
        }

    }

  
    public static String generatePassword(int words, int capslock, int numbers, int symbols) {

        ArrayList<String>base = new ArrayList<>(words);

        Random random = new Random();


        for(int i = 0; i < words; i++) {
            Scanner scan = null;
            try {
                scan = new Scanner(file);
            } catch (IOException exception) {

            }
            int rand = random.nextInt(length);
            String toAdd = "";
            for(int j = 0; j < rand; j++) {
                toAdd = scan.next();
            }
            base.add(toAdd);
        }
        String toReturn = "";



        while(notAllUppercase(base) && capslock > 0) {

            int rand = random.nextInt(base.size());
            char toCaps = base.get(rand).charAt(0);
            if(Character.isLowerCase(toCaps)) {
                toCaps = Character.toUpperCase(toCaps);
                base.set(rand, toCaps + base.get(rand).substring(1));
                capslock--;
            }
        }

        addNumbers(base, numbers);

        addSymbols(base, symbols);





        //need to add more loops to handle each argument(caps, numbers, symbols)

        for (String str: base) {
            toReturn+=str;
        }
        return toReturn;
    }

    //returns true if there is a word that doesn't have an upper-cased first letter
    public static boolean notAllUppercase(ArrayList<String> base) {
        for(String word: base) {
            if(Character.isLowerCase(word.charAt(0))) {
                return true;
            }
        }
        return false;
    }

    public static void addNumbers(ArrayList<String> base, int num) {

        Random rand = new Random();

        for (int i = 0; i < num; i++) {
            int randIndex = rand.nextInt(base.size());
            base.add(randIndex, Integer.toString(rand.nextInt(10)));
        }
    }

    public static void addSymbols(ArrayList<String> base, int num) {

       
        ArrayList<String> symbols = new ArrayList<>(
                Arrays.asList("~", "!", "@", "#", "$", "%", "^", "&", "*", ".", ":", ";", "<", ">", "?"));

        Random rand = new Random();

        for (int i = 0; i < num; i++) {
            int randIndex = rand.nextInt(base.size());

            String symbol = symbols.get(rand.nextInt(symbols.size()));

            base.add(randIndex, symbol);
        }
    }

}
