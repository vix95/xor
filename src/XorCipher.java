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

    private int getKey_length() {
        return key_length;
    }

    private byte xor(char c1, char c2) {
        return (byte) (c1 ^ c2);
    }

    private byte xorByte(byte b1, byte b2) {
        return (byte) (b1 ^ b2);
    }

    private byte[] encrypt(char[] line) {
        byte[] encrypted_line = new byte[line.length];
        if (this.key_length == line.length) {
            for (int i = 0; i < line.length; i++) {
                encrypted_line[i] = xor(this.key[i], line[i]);
            }
        } else System.out.print("Length of the key isn't equal to line length. Cannot encrypt.");

        return encrypted_line;
    }

    void encryptFile(final String path) throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter(path + "/crypto.txt"));
        Scanner scanner = new Scanner(new File(path + "/plain.txt"));

        while (scanner.hasNextLine()) {
            char[] line = scanner.nextLine().toCharArray();
            byte[] encrypted_line = this.encrypt(line);

            // prepare string to write in file
            StringBuilder prepared_encyrpted_line = new StringBuilder();
            ;
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

    private byte[] returnByteArr(String line) {
        String[] line_arr = line.split(" ");
        byte[] arr = new byte[line_arr.length];

        for (int i = 0; i < line_arr.length; i++) {
            arr[i] = Byte.parseByte(line_arr[i]);
        }

        return arr;
    }

    private boolean cryptanalysis(final ArrayList<String> lines) {
        boolean found_key = false;
        this.key = new char[returnByteArr(lines.get(1)).length];
        this.key_length = this.key.length;

        // how to find the key?
        // 1. take first and second line to analyze
        // 2. do loop for every char from 'a' to 'z' (97 - 122) as a potential key
        // 3. if there isn't matched any char, check for the space (32)
        // 4. compare char from potential key and char from first and second line as XOR calculation
        // 5. if there is the match between XOR result c1 and c2 or c1 and c3 that c1 is a key char
        // 6. go to next char
        // 7. after loop for first and second line check that the key has every chars
        // 7.a if not then compare first line with third line etc...
        // 7.b if yes that the key has been fully collected

        for (int i = 0; i < lines.size(); i++) {
            byte[] line1 = returnByteArr(lines.get(i));

            for (int j = 0; j < lines.size(); j++) {
                byte[] line2 = returnByteArr(lines.get(j++));

                if (i != j) {
                    for (int n = 0; n < this.key_length; n++) {
                        // check for the spaces
                        byte xor_c1c2 = xorByte(line1[n], line2[n]);
                        byte xor_c1k = xorByte(line1[n], (byte) 32);
                        byte xor_c2k = xorByte(line2[n], (byte) 32);

                        byte a = xorByte(line1[n], line2[n]);

                        /*for (int k = 97; k < 122; k++) {
                            char xor_c1c2 = xorByte(line1[n], line2[n]);
                            char xor_c2k = xorByte(line2[n], (byte) k);

                            char a = xorByte(line1[n], line2[n]);
                        }*/
                    }
                }
            }
        }

        return found_key;
    }

    void breakCipher(final String path) throws IOException {
        Scanner scanner = new Scanner(new File(path + "/crypto.txt"));
        ArrayList<String> lines = new ArrayList<>();

        // first read all encrypted lines
        while (scanner.hasNextLine()) lines.add(scanner.nextLine());
        scanner.close();

        if (lines.size() == 0) {
            System.out.print("No text to decrypt\n");
            return;
        }

        if (this.cryptanalysis(lines)) {
            //char[] decrypted = this.decrypt(encrypted);
            //System.out.printf("encrypted line: %s\n", String.valueOf(encrypted));
            //System.out.printf("decrypted line: %s\n", String.valueOf(decrypted));

            // build the string from every character from key array
            StringBuilder builder = new StringBuilder();
            for (char c : key) {
                if (Character.isLetter(c)) {
                    builder.append(c);
                }
            }

            BufferedWriter writer = new BufferedWriter(new FileWriter(path + "/decrypt.txt"));
            BufferedWriter writerKey = new BufferedWriter(new FileWriter(path + "/key-crypto.txt"));

            //writer.write(decrypted);
            writerKey.write(builder.toString());

            writer.close();
            writerKey.close();
        } else {
            System.out.println("Error: finding the key is impossible");
        }
    }

}
