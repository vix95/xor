import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

class XorCipher {
    private char[] key;
    private int key_length;

    XorCipher() {
    }

    XorCipher(String key) {
        this.key = key.toCharArray();
        this.key_length = key.length();
    }

    private int[] encrypt(char[] line) {
        int[] encrypted_line = new int[line.length];
        if (this.key_length == line.length) {
            for (int i = 0; i < line.length; i++) {
                encrypted_line[i] = this.key[i] ^ line[i];
            }
        } else System.out.print("Length of the key isn't equal to line length. Cannot encrypt.");

        return encrypted_line;
    }

    void encryptFile(final String path) throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter(path + "/crypto.txt"));
        Scanner scanner = new Scanner(new File(path + "/plain.txt"));

        while (scanner.hasNextLine()) {
            char[] line = scanner.nextLine().toCharArray();
            int[] encrypted_line = this.encrypt(line);

            // prepare string to write in file
            StringBuilder prepared_encyrpted_line = new StringBuilder();;
            for (int i : encrypted_line) prepared_encyrpted_line.append(i).append(' ');
            prepared_encyrpted_line.deleteCharAt(prepared_encyrpted_line.length() - 1);  // remove last space


            System.out.println("plain line: " + String.valueOf(line));
            System.out.println("encrypted line: " + prepared_encyrpted_line + "\n");

            writer.write(prepared_encyrpted_line.toString());
            writer.write('\n');
        }

        writer.close();
        scanner.close();
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
