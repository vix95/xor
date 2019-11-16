import java.io.*;
import java.util.Scanner;

public class Xor {
    private static String path = System.getProperty("user.dir") + "/test_files";

    public static void main(String[] args) {
        try {
            String cmd;
            String key;

            if (args == null) printWrongArg();
            else if (args.length > 1)
                System.out.print("Too many arguments, script need only one, please type one: -p, -e, -k\n");
            else if (args.length == 1) {
                cmd = args[0];
                switch (cmd) {
                    case "-p":  // prepare a file
                        key = readKey();
                        if (key != null) preparePlainFile(key);
                        break;

                    case "-e":  // encrypt
                        key = readKey();
                        if (key != null) {
                            XorCipher xorCipher = new XorCipher(key);
                            xorCipher.encryptFile(path);
                        }

                        break;

                    case "-k":  // crypto analysis break key
                        XorCipher xorCipher = new XorCipher();
                        xorCipher.breakCipher(path);
                        break;

                    default:
                        printWrongArg();
                        break;
                }
            } else printWrongArg();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void preparePlainFile(String key) throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter(path + "/plain.txt"));
        Scanner scanner = new Scanner(new File(path + "/orig.txt"));

        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            String prepared_line = prepareLine(String.valueOf(line), key);

            System.out.println("orig line: " + line);
            System.out.println("\nprepared lines:\n" + prepared_line + "\n");

            writer.write(prepared_line);
            writer.write('\n');
        }

        writer.close();
        scanner.close();
    }

    private static String prepareLine(String line, String key) {
        String formattedLine = line.replaceAll("[^a-zA-Z ]+", "").toLowerCase().replace("  ", " ");
        char[] line_arr = formattedLine.toCharArray();
        StringBuilder preparedLine = new StringBuilder();
        int key_length = key.length();
        int fake_chars = key_length - (line_arr.length % key_length);

        // first prepare real data
        for (int i = 0; i < line_arr.length; i++) {
            if (i % key_length == 0 && i > 0) preparedLine.append('\n');
            preparedLine.append(line_arr[i]);
        }

        // fill the gap to key_length
        if (fake_chars != key_length) for (int i = 0; i < fake_chars; i++) preparedLine.append(' ');

        return preparedLine.toString();
    }

    private static String readKey() {
        Scanner scanner;
        try {
            scanner = new Scanner(new File(path + "/key.txt"));
            try {
                String key = scanner.nextLine().toLowerCase();
                if (!key.equals(key.replaceAll("[^a-zA-Z ]+ ", ""))) {
                    System.out.print("Error: unrecognized key, the key must be a positive number and meet the requirements\n");
                    return null;
                }

                System.out.printf("The key has been loaded: %s\n\n", key);
                return key;
            } catch (Exception e) {
                System.out.print("Error: unrecognized key, the key must be a positive number and meet the requirements\n");
                return null;
            }
        } catch (Exception e) {
            System.out.print("Error: key file not found, can't do any action\n");
        }

        return null;
    }

    private static void printWrongArg() {
        System.out.print("Unrecognized argument, please type one: -p, -e, -k\n");
    }
}