import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

class XorCipher {
    private char[] key;
    private String key_str;
    private int key_length;

    XorCipher() {
    }

    XorCipher(String key) {
        this.key = key.toCharArray();
        this.key_length = key.length();
    }

    private byte xor(char c1, char c2) {
        return (byte) (c1 ^ c2);
    }

    private byte xorByte(byte b1, byte b2) {
        return (byte) (b1 ^ b2);
    }

    private String byteToBin(byte b) {
        return String.format("%8s", Integer.toBinaryString(b & 0xFF)).replace(' ', '0');
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

    private char[] decrypt(String line) {
        char[] decrypted_line = new char[this.key_length];
        for (int i = 0; i < this.key_length; i++) {
            byte[] line_arr = returnByteArr(line);
            decrypted_line[i] = (char) ((byte) this.key[i] ^ line_arr[i]);
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
        this.key = new char[returnByteArr(lines.get(1)).length];
        this.key_length = this.key.length;

        // first looking for all possible characters
        for (int i = 0; i < lines.size(); i++) {
            byte[] line1 = returnByteArr(lines.get(i));

            for (int j = 0; j < lines.size(); j++) {
                byte[] line2 = returnByteArr(lines.get(j));

                for (int k = 0; k < lines.size(); k++) {
                    byte[] line3 = returnByteArr(lines.get(k));

                    if (i != j && i != k && j != k) {
                        for (int n = 0; n < this.key_length; n++) {
                            // if xor c1 and c2 has first 3 letters '010' that I know it's space, I don't know which one
                            // if xor c1 and c2 is space and c2 xor c3 isn't space, then the space is c1 and do xor for c2 and c3
                            // if xor c1 and c2 is space and c2 xor c3 is space, then c2 is space and do xor for c1 and c3
                            // if c1 xor c3 = 0, then c1 and c3 is a space, c2 is a letter
                            String xor_c1c2_bin = byteToBin(xorByte(line1[n], line2[n]));

                            if (xor_c1c2_bin.substring(0, 3).equals("010")) {  // space
                                byte xor_c1_space = xorByte(line1[n], (byte) 32);
                                byte xor_c2_space = xorByte(line2[n], (byte) 32);
                                byte xor_c3_space = xorByte(line3[n], (byte) 32);

                                if (xor_c2_space == xor_c3_space) {
                                    if (xor_c2_space >= 97 && xor_c2_space <= 122) this.key[n] = (char) xor_c3_space;
                                } else if (xor_c1_space == xor_c3_space) {
                                    if (xor_c1_space >= 97 && xor_c1_space <= 122) this.key[n] = (char) xor_c1_space;
                                }
                            }
                        }
                    }
                }
            }
        }

        // next looking for spaces
        for (int pos = 0; pos < this.key_length; pos++) {  // let's go by key
            for (int i = 0, j = 1, k = 2; i < lines.size(); i++, j++, k++) {  // let's go to down as column directions
                if (k == lines.size()) break;
                byte[] line = returnByteArr(lines.get(i));
                byte[] line2 = returnByteArr(lines.get(j));
                byte[] line3 = returnByteArr(lines.get(k));
                byte xor_space_b = xorByte(line[pos], line2[pos]);

                if (xor_space_b == 0) {
                    byte xor_test_b1 = xorByte(line[pos], (byte) 32);
                    byte xor_test_b2 = xorByte(line2[pos], (byte) 32);
                    byte xor_test_b3 = xorByte(line3[pos], (byte) 32);

                    if (((xor_test_b1 >= 97 && xor_test_b1 <= 122) || xor_test_b1 == 32) &&
                            ((xor_test_b2 >= 97 && xor_test_b2 <= 122) || xor_test_b2 == 32) &&
                            ((xor_test_b3 >= 97 && xor_test_b3 <= 122) || xor_test_b3 == 32)) {

                        // if xor 1, xor 2 and xor 3 is between 97 and 122 and
                        // xor 1 == xor 2 == xor 3 than it isn't space
                        if ((xor_test_b1 >= 97 && xor_test_b2 >= 97 && xor_test_b3 >= 97) &&
                                !(xor_test_b1 == xor_test_b2 && xor_test_b2 == xor_test_b3)) {
                            this.key[pos] = (char) 32;
                        }
                    }
                }
            }
        }

        // build the string from every character from key array
        boolean found_key = true;
        StringBuilder stringBuilder = new StringBuilder();
        for (char c : this.key) {
            stringBuilder.append(c);

            if (c == '\u0000') found_key = false;
        }

        this.key_str = stringBuilder.toString();
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
            BufferedWriter writerKey = new BufferedWriter(new FileWriter(path + "/key-crypto.txt"));
            writerKey.write(this.key_str);
            writerKey.close();

            BufferedWriter writer = new BufferedWriter(new FileWriter(path + "/decrypt.txt"));
            for (String encrypted : lines) {
                char[] decrypted = this.decrypt(encrypted);
                System.out.printf("encrypted line: %s\n", encrypted);
                System.out.printf("decrypted line: %s\n\n", String.valueOf(decrypted));

                writer.write(decrypted);
                writer.write('\n');
            }

            writer.close();

            System.out.printf("the key is: %s\n", String.valueOf(this.key));
        } else {
            System.out.println("Error: the key is incomplete");
        }
    }

}
