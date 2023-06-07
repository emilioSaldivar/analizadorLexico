package py.edu.fpuna.compiladores.traductor;



import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Stack;

public class JSONTranslator {

    private static BufferedReader input;
    private static PrintWriter output;
    private static Stack<String> stack;

    public static void translateJSON(String inputFileName, String outputFileName) {
        try {
            input = new BufferedReader(new FileReader(inputFileName));
            output = new PrintWriter(outputFileName);
            stack = new Stack<>();

            output.println("<root>");

            String line;
            while ((line = input.readLine()) != null) {
                translateLine(line);
            }

            output.println("</root>");

            input.close();
            output.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void translateLine(String line) {
        int i = 0;
        int lineLength = line.length();
        while (i < lineLength) {
            char c = line.charAt(i);
            switch (c) {
                case '{':
                    output.println("<object>");
                    stack.push("}");
                    break;
                case '}':
                    output.println("</object>");
                    stack.pop();
                    break;
                case '[':
                    output.println("<array>");
                    stack.push("]");
                    break;
                case ']':
                    output.println("</array>");
                    stack.pop();
                    break;
                case ',':
                    output.println();
                    if (!stack.isEmpty() && stack.peek().equals("]")) {
                        output.print("\t");
                    }
                    break;
                case ':':
                    output.print("><");
                    break;
                case '"':
                    i++;
                    StringBuilder sb = new StringBuilder();
                    while (i < lineLength && line.charAt(i) != '"') {
                        sb.append(line.charAt(i));
                        i++;
                    }
                    output.print(sb.toString());
                    output.print("</");
                    break;
                case 't':
                case 'f':
                case 'n':
                    i += processKeyword(line, i);
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
                        output.print(sb.toString());
                        output.print("</");
                        i--;
                    } else {
                        reportError(c, i);
                    }
                    break;
            }
            i++;
        }
    }

    private static int processKeyword(String line, int index) {
        char c = line.charAt(index);
        switch (c) {
            case 't':
                if (line.substring(index, index + 4).equals("true")) {
                    output.print("true");
                    return 3;
                } else {
                    reportError(c, index);
                    return 0;
                }
            case 'f':
                if (line.substring(index, index + 5).equals("false")) {
                    output.print("false");
                    return 4;
                } else {
                    reportError(c, index);
                    return 0;
                }
            case 'n':
                if (line.substring(index, index + 4).equals("null")) {
                    output.print("null");
                    return 3;
                } else {
                    reportError(c, index);
                    return 0;
                }
            default:
                reportError(c, index);
                return 0;
        }
    }

    private static void reportError(char c, int index) {
        System.err.println("Error: Invalid character '" + c + "' at position " + index);
    }
}
