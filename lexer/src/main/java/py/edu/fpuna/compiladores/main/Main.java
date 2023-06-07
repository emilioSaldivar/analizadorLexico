package py.edu.fpuna.compiladores.main;

import py.edu.fpuna.compiladores.lexer.Lexer;
import py.edu.fpuna.compiladores.parser.Parser;
import py.edu.fpuna.compiladores.traductor.JSONTranslator;

public class Main {
    public static void main(String[] args) {
        if (args.length >= 2) {
            String inputFileName = args[0];//"/tmp/fuente.txt";
            String outputFileName = args[1];//"/tmp/salida.txt"; 

            // Llamada al lexer para realizar el análisis léxico y generar el fuente para el parser
            Lexer.lexer(inputFileName, outputFileName);

            // Llamada al parser para realizar el análisis sintáctico y generar la estructura de datos AST 
            Parser parser = new Parser();
            parser.parseFile(outputFileName);
            JSONTranslator.translateJSON(inputFileName, outputFileName);
        } else {
            System.err.println("Debe proporcionar los nombres de archivo de entrada y salida.");
        }
    }
}
