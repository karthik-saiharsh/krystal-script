import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.BufferedReader;

public class KrystalScript {
    static boolean hasError = false;    
    public static void main(String args[]) throws IOException {
        if(args.length > 1) {
            // Exit with code 64 if incorrect arguments are passed in
            System.out.println("Usage is as follows: krystalscript [source.ks]");
            System.exit(64);
        } else if(args.length == 1) {
            String file_extension = args[0].substring(args[0].length()-3);
            
            // Check to ensure that the extension of code file is .ks only
            if(file_extension.toLowerCase().equals(".ks")) {
                runFile(args[0]); // Run the file provided                
            } else {
                System.out.println("Please Pass in a KrystalScript code file. (file must have a .ks extension)");
            }

        } else {
            // Start a REPL if no arguments are passed in
            runREPL();
        }
    }

    // Function to handle reading a file passed in as argument
    public static void runFile(String path) throws IOException {
        byte[] bytes = Files.readAllBytes(Paths.get(path));
        // Convert bytes into String
        String code = new String(bytes, Charset.defaultCharset());
        run(code);
    }

    // Function to handle REPL mode
    public static void runREPL() throws IOException {
        InputStreamReader input = new InputStreamReader(System.in);
        BufferedReader reader = new BufferedReader(input);

        // Run the REPL
        while(true) {
            System.out.print("KS> ");
            String line = reader.readLine();

            if(line == null || line.equals("exit")) break;
            else run(line);
            hasError = false;
        }
    }

    // Handle Running the code file line by line
    public static void run(String source) {
        if(hasError) System.exit(65);
    }

    /////// Error Handling ///////
    static void error(int line, String message) {
        report(line, "", message);
    }

    private static void report(int line, String where, String message) {
        String error = "[line: " + line + "]" + "Error: " + where + "; " + message;
        System.err.println(error);
        hasError = true;
    }
}