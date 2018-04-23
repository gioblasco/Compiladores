
import AST.Program;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {

        File file = null;
        FileReader stream;
        int numChRead;
        int num = 0;
        if (args.length == 0) {

            File dir = new File("./src/tests/");
            File[] filesList = dir.listFiles();
            for (int c = 0; c < filesList.length; c++) {
                if (filesList[c].isFile()) {
                    System.out.println((c + 1) + " - " + filesList[c].getName());
                }
            }
            Scanner in = new Scanner(System.in);
            System.out.print("Select a file to compile: ");
            try {
                num = in.nextInt();
            } catch (Exception e) {
                System.err.println("Number expected!");
                System.exit(-1);
            }

            try {
                file = filesList[num - 1];
            } catch (ArrayIndexOutOfBoundsException e) {
                System.err.println("Choose a valid number on the list!");
                System.exit(-1);
            }

        } else {
            file = new File(args[0]);
            if (!file.exists() || !file.canRead()) {
                System.out.println("Either the file " + args[0] + " does not exist or it cannot be read");
                throw new RuntimeException();
            }
        }
        try {
            stream = new FileReader(file);
        } catch (FileNotFoundException e) {
            System.out.println("Something wrong: file does not exist anymore");
            throw new RuntimeException();
        }
        // one more character for '\0' at the end that will be added by the
        // compiler
        char[] input = new char[(int) file.length() + 1];

        try {
            numChRead = stream.read(input, 0, (int) file.length());
        } catch (IOException e) {
            System.out.println("Error reading file " + args[0]);
            throw new RuntimeException();
        }

        // if ( numChRead != file.length() ) {
        //     System.out.println("Read error");
        //     throw new RuntimeException();
        // }
        try {
            stream.close();
        } catch (IOException e) {
            System.out.println("Error in handling the file " + args[0]);
            throw new RuntimeException();
        }

        Compiler compiler = new Compiler();
        Program p = compiler.compile(input);
        p.genC();

    }
}
