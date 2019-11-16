import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
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

    private byte xorChar(char c1, char c2) {
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
                encrypted_line[i] = xorChar(this.key[i], line[i]);
            }
        } else System.out.print("Length of the key isn't equal to line length. Cannot encrypt.");

        return encrypted_line;
    }

    void encryptFile(final String path) {
        try {
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
        } catch (Exception e) {
            System.out.print("Error: plain file not found, can't do any action\n");
        }
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
        for (int pos = 0; pos < this.key_length; pos++) {
            for (int r1 = 0, r2 = 1, r3 = 2; r3 < lines.size(); r1++, r2++, r3++) {
                byte m1 = returnByteArr(lines.get(r1))[pos];
                byte m2 = returnByteArr(lines.get(r2))[pos];
                byte m3 = returnByteArr(lines.get(r3))[pos];

                // if xor c1 and c2 has first 3 letters '010' that I know it's space, I don't know which one
                // if xor c1 and c2 is space and c2 xor c3 isn't space, then the space is c1 and do xor for c2 and c3
                // if xor c1 and c2 is space and c2 xor c3 is space, then c2 is space and do xor for c1 and c3
                // if c1 xor c3 = 0, then c1 and c3 is a space, c2 is a letter
                String m1m2_bin = byteToBin(xorByte(m1, m2));
                String m2m3_bin = byteToBin(xorByte(m2, m3));

                if (m1m2_bin.substring(0, 3).equals("010") || m2m3_bin.substring(0, 3).equals("010")) {  // space
                    byte m2m3 = xorByte(m2, m3);

                    // if m1m2 and m2m3 has space then m2 is space
                    if (m1m2_bin.substring(0, 3).equals("010") && m2m3_bin.substring(0, 3).equals("010")) {

                        for (int c = 97; c <= 122; c++) {
                            byte m2space = xorByte(m2, (byte) c);

                            if (m2space == 32) {
                                this.key[pos] = (char) c;
                            }
                        }

                    } // if m1m2 has space and m2m3 hasn't space then m1 is space
                    else if (m1m2_bin.substring(0, 3).equals("010") && !m2m3_bin.substring(0, 3).equals("010")
                            && m2m3 != 0) {
                        // check first for space
                        byte m1space = xorByte(m1, (byte) 32);

                        if (m1space == 32) {
                            this.key[pos] = (char) 32;
                        } else {
                            for (int c = 97; c <= 122; c++) {
                                m1space = xorByte(m1, (byte) c);

                                if (m1space == 32) {
                                    this.key[pos] = (char) c;
                                }
                            }
                        }
                    } // if m1 equals m3 then m1 and m3 is space
                    else if (m1 == m3) {
                        for (int c = 97; c <= 122; c++) {
                            byte m1space = xorByte(m1, (byte) c);
                            byte m3space = xorByte(m3, (byte) c);

                            if (m1space == 32 && m3space == 32) {
                                this.key[pos] = (char) c;
                            }
                        }
                    } // if m1m2 hasn't space and m2m3 has space then m3 is space
                    else if (!m1m2_bin.substring(0, 3).equals("010") && m2m3_bin.substring(0, 3).equals("010")) {
                        for (int c = 97; c <= 122; c++) {
                            byte m3space = xorByte(m3, (byte) c);

                            if (m3space == 32) {
                                this.key[pos] = (char) c;
                            }
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

    void breakCipher(final String path) {
        try {
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

                System.out.printf("The key is: %s\n", String.valueOf(this.key));
            } else {
                System.out.println("Error: the key is incomplete");
            }
        } catch (Exception e) {
            System.out.print("Error: crypto file not found, can't do any action\n");
        }
    }

}
