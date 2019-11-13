import java.io.*;
import java.util.Scanner;

public class Xor {
    private static String path = System.getProperty("user.dir") + "/test_files";

    public static void main(String[] args) {
        try {
            String cmd;
            String key;

            if (args != null) {
                cmd = args[0];
                switch (cmd) {
                    case "-p":  // prepare a file
                        preparePlainFile();
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

    private static void preparePlainFile() throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter(path + "/plain.txt"));
        Scanner scanner = new Scanner(new File(path + "/orig.txt"));

        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            String prepared_line = prepareLine(String.valueOf(line));

            System.out.println("orig line: " + line);
            System.out.println("prepared line: " + prepared_line + "\n");

            writer.write(prepared_line);
            writer.write('\n');
        }

        writer.close();
        scanner.close();
    }

    private static String prepareLine(String line) {
        return line.replaceAll("[^a-zA-Z] +", "").toLowerCase();
    }

    private static String readKey() throws FileNotFoundException {
        Scanner scanner = new Scanner(new File(path + "/key.txt"));

        try {
            String key = scanner.nextLine().toLowerCase();
            if (!key.equals(key.replaceAll("[^a-zA-Z ]+ ", ""))) {
                System.out.print("Error: unrecognized key, the key must be a positive number and meet the requirements\n");
                return null;
            }

            return key;
        } catch (Exception e) {
            System.out.print("Error: unrecognized key, the key must be a positive number and meet the requirements\n");
            return null;
        }
    }

    private static void printWrongArg() {
        System.out.print("Unrecognized argument, please type one: -p, -e, -k\n");
    }
}