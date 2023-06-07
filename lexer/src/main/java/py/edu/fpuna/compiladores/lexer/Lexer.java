/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package py.edu.fpuna.compiladores.lexer;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Stack;
/**
 *
 * @author emiliomatasc@fpuna.edu.py
 */
public class Lexer {
    // Tokens
    private static final int L_LLAVE = 1;
    private static final int R_LLAVE = 2;
    private static final int L_CORCHETE = 3;
    private static final int R_CORCHETE = 4;
    private static final int COMA = 5;
    private static final int DOS_PUNTOS = 6;
    private static final int STRING = 7;
    private static final int NUMBER = 8;
    private static final int PR_TRUE = 9;
    private static final int PR_FALSE = 10;
    private static final int PR_NULL = 11;

    // Palabras reservadas
    private static final String TRUE = "true";
    private static final String FALSE = "false";
    private static final String NULL = "null";

    private static BufferedReader input;
    private static PrintWriter output;
    private static Stack<Integer> stack;
    
    public static void lexer(String inputFileName, String outputFileName) {

        try {
            input = new BufferedReader(new FileReader(inputFileName));
            output = new PrintWriter(outputFileName);
            stack = new Stack<>();

            String line;
            while ((line = input.readLine()) != null) {
                lex(line);
                output.println();
            }

            input.close();
            output.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
        private static void lex(String line) {
        int i = 0;
        int lineLength = line.length();
        while (i < lineLength) {
            char c = line.charAt(i);
            switch (c) {
                case '{':
                    outputToken(L_LLAVE);
                    stack.push(L_LLAVE);
                    break;
                case '}':
                    outputToken(R_LLAVE);
                    if (!stack.isEmpty() && stack.peek() == L_CORCHETE) {
                        output.print("\t");
                    }
                    stack.pop();
                    break;
                case '[':
                    outputToken(L_CORCHETE);
                    stack.push(L_CORCHETE);
                    break;
                case ']':
                    outputToken(R_CORCHETE);
                    stack.pop();
                    break;
                case ',':
                    outputToken(COMA);
                    if (!stack.isEmpty() && stack.peek() == L_CORCHETE) {
                        output.print("\t");
                    }
                    break;
                case ':':
                    outputToken(DOS_PUNTOS);
                    break;
                case '"':
                    i++;
                    StringBuilder sb = new StringBuilder();
                    while (i < lineLength && line.charAt(i) != '"') {
                        sb.append(line.charAt(i));
                        i++;
                    }
                    outputToken(STRING);
                    break;
                case 't':
                    if (line.substring(i, i + 4).equals(TRUE)) {
                        outputToken(PR_TRUE);
                        i += 3;
                    } else {
                        reportError(c, i);
                    }
                    break;
                case 'f':
                    if (line.substring(i, i + 5).equals(FALSE)) {
                        outputToken(PR_FALSE);
                        i += 4;
                    } else {
                        reportError(c, i);
                    }
                    break;
                case 'n':
                    if (line.substring(i, i + 4).equals(NULL)) {
                        outputToken(PR_NULL);
                        i += 3;
                    } else {
                        reportError(c, i);
                    }
                    break;
                case ' ':
                case '\t':
                case '\r':
                case '\n':
                    break;
                default:
                    if (Character.isDigit(c)) {
                        sb = new StringBuilder();
                        sb.append(c);
                        i++;
                        boolean dotFound = false;
                        while (i < lineLength) {
                            c = line.charAt(i);
                            if (Character.isDigit(c)) {
                                sb.append(c);
                                i++;
                            } else if (c == '.') {
                                if (dotFound) {
                                    reportError(c, i);
                                } else {
                                    dotFound = true;
                                    sb.append(c);
                                    i++;
                                }
                            } else {
                                break;
                            }
                        }
                        outputToken(NUMBER);
                        i--;
                    } else {
                        reportError(c, i);
                    }
                    break;
            }
            i++;
        }
    }
     
    private static void outputToken(int token) {
        switch (token) {
            case L_LLAVE:
                output.print("L_LLAVE");
                output.print("\n");
                if (!stack.isEmpty() && stack.peek() == L_CORCHETE) {
                    output.print("\t");
                }
                break;
            case R_LLAVE:
                output.print("R_LLAVE");
                break;
            case L_CORCHETE:
                output.print("L_CORCHETE");
                output.print("\n");
                stack.push(L_CORCHETE);
                output.print("\t");
                break;
            case R_CORCHETE:
                output.print("\n");
                output.print("\t");
                output.print("R_CORCHETE");
                stack.pop();
                break;
            case COMA:
                output.print("COMA");
                output.print("\n");
                if (!stack.isEmpty() && stack.peek() == L_CORCHETE) {
                    output.print("\t");
                }
                break;
            case DOS_PUNTOS:
                output.print("DOS_PUNTOS");
                break;
            case STRING:
                output.print("STRING");
                break;
            case NUMBER:
                output.print("NUMBER");
                break;
            case PR_TRUE:
                output.print("PR_TRUE");
                break;
            case PR_FALSE:
                output.print("PR_FALSE");
                break;
            case PR_NULL:
                output.print("PR_NULL");
                break;
        }
        if (token != COMA && token != L_CORCHETE) {
            output.print(" ");
        }
    }

    private static void reportError(char c, int i) {
        System.err.println("Error lexico (" + c + ") en la posicion " + i);
    }
}
