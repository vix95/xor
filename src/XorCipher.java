import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

class XorCipher {
    private int m = 26; // chars qty
    private char[] key;

    XorCipher() {
    }

    XorCipher(String key) {
        this.key = key.toCharArray();
    }

    private char[] encrypt(char[] line) {
        char[] encrypted_line = new char[line.length];
        for (int i = 0, j = 0; i < line.length; i++) {
            int int_line = line[i] - 'a'; // plain
            if (Character.isLetter(line[i])) {
            }
        }

        return encrypted_line;
    }

    void encryptFile(final String path) throws IOException {
        if (this.key != null) {
            BufferedWriter writer = new BufferedWriter(new FileWriter(path + "/crypto.txt"));
            Scanner scanner = new Scanner(new File(path + "/plain.txt"));

            while (scanner.hasNextLine()) {
                char[] line = scanner.nextLine().toCharArray();
                char[] encrypted_line = this.encrypt(line);

                System.out.println("plain line: " + String.valueOf(line));
                System.out.println("encrypted line: " + String.valueOf(encrypted_line) + "\n");

                writer.write(encrypted_line);
                writer.write('\n');
            }

            writer.close();
            scanner.close();
        }
    }

    private char[] decrypt(char[] line) {
        char[] decrypted_line = new char[line.length];
        for (int i = 0, j = 0; i < line.length; i++) {
            if (Character.isLetter(line[i])) {
            }
        }

        return decrypted_line;
    }

    private boolean cryptanalysis(final char[] encrypted) {
        boolean found_key = false;
        return found_key;
    }

    void breakCipher(final String path) throws IOException {
        Scanner scanner = new Scanner(new File(path + "/crypto.txt"));
        ArrayList<String> lines = new ArrayList<>();

        while (scanner.hasNextLine()) lines.add(scanner.nextLine());

        // get the length of array
        int array_length = 0;
        for (String s : lines) array_length += s.length();

        if (array_length == 0) {
            System.out.print("No text to decrypt\n");
            return;
        }

        char[] encrypted = new char[array_length + lines.size() - 1];

        int pos = 0;
        for (String s : lines) {
            for (char c : s.toCharArray()) {
                if (Character.isLetter(c)) encrypted[pos++] = c;
                else encrypted[pos++] = ' ';
            }
            pos++;
        }

        scanner.close();

        if (this.cryptanalysis(encrypted)) {
            char[] decrypted = this.decrypt(encrypted);
            System.out.printf("encrypted line: %s\n", String.valueOf(encrypted));
            System.out.printf("decrypted line: %s\n", String.valueOf(decrypted));

            // build the string from every character from key array
            StringBuilder builder = new StringBuilder();
            for (char c : key) {
                if (Character.isLetter(c)) {
                    builder.append(c);
                }
            }

            BufferedWriter writer = new BufferedWriter(new FileWriter(path + "/decrypt.txt"));
            BufferedWriter writerKey = new BufferedWriter(new FileWriter(path + "/key-crypto.txt"));

            writer.write(decrypted);
            writerKey.write(builder.toString());

            writer.close();
            writerKey.close();
        } else {
            System.out.println("Error: finding the key is impossible");
        }
    }

}
