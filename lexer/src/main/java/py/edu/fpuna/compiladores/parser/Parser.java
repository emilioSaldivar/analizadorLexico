package py.edu.fpuna.compiladores.parser;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class Parser {
    private BufferedReader input;
    private String currentToken;

    public void parseFile(String inputFileName) {
        try {
            input = new BufferedReader(new FileReader(inputFileName));

            // Leer el primer token
            getNextToken();

            // Llamar al símbolo inicial de la gramática
            jsonObject();

            // Comprobar si se alcanzó el final del archivo
            if (currentToken != null) {
                reportError("Se esperaba fin de archivo");
            }

            input.close();
        } catch (IOException e) {
        }
    }

    private void getNextToken() throws IOException {
        String line = input.readLine();
        if (line != null) {
            currentToken = line.trim();
        } else {
            currentToken = null;
        }
    }

    private void match(String expectedToken) throws IOException {
        if (currentToken != null && currentToken.equals(expectedToken)) {
            getNextToken();
        } else {
            reportError("Se esperaba " + expectedToken);
        }
    }

    // Símbolos no terminales

    private void jsonObject() throws IOException {
        if (currentToken != null && currentToken.equals("L_LLAVE")) {
            match("L_LLAVE");
            members();
            match("R_LLAVE");
        } else {
            reportError("Se esperaba L_LLAVE");
        }
    }

    private void members() throws IOException {
        if (currentToken != null && currentToken.equals("STRING")) {
            pair();
            pairList();
        } else if (currentToken != null && currentToken.equals("R_LLAVE")) {
            // No hay miembros, es un objeto vacío
        } else {
            reportError("Se esperaba STRING o R_LLAVE");
        }
    }

    private void pairList() throws IOException {
        if (currentToken != null && currentToken.equals("COMA")) {
            match("COMA");
            pair();
            pairList();
        } else if (currentToken != null && currentToken.equals("R_LLAVE")) {
            // No hay más pares, es el final del objeto
        } else {
            reportError("Se esperaba COMA o R_LLAVE");
        }
    }

    private void pair() throws IOException {
        if (currentToken != null && currentToken.equals("STRING")) {
            match("STRING");
            match("DOS_PUNTOS");
            value();
        } else {
            reportError("Se esperaba STRING");
        }
    }

    private void value() throws IOException {
        if (currentToken != null && currentToken.equals("L_LLAVE")) {
            jsonObject();
        } else if (currentToken != null && currentToken.equals("L_CORCHETE")) {
            jsonArray();
        } else if (currentToken != null && currentToken.equals("STRING")) {
            match("STRING");
        } else if (currentToken != null && (currentToken.equals("NUMBER") ||
                currentToken.equals("PR_TRUE") ||
                currentToken.equals("PR_FALSE") ||
                currentToken.equals("PR_NULL"))) {
            primitive();
        } else {
            reportError("Se esperaba L_LLAVE, L_CORCHETE, STRING, NUMBER, PR_TRUE, PR_FALSE o PR_NULL");
        }
    }

    private void jsonArray() throws IOException {
        if (currentToken != null && currentToken.equals("L_CORCHETE")) {
            match("L_CORCHETE");
            elements();
            match("R_CORCHETE");
        } else {
            reportError("Se esperaba L_CORCHETE");
        }
    }

    private void elements() throws IOException {
        if (currentToken != null && currentToken.equals("R_CORCHETE")) {
            // No hay elementos, es un array vacío
        } else {
            value();
            elementList();
        }
    }

    private void elementList() throws IOException {
        if (currentToken != null && currentToken.equals("COMA")) {
            match("COMA");
            value();
            elementList();
        } else if (currentToken != null && currentToken.equals("R_CORCHETE")) {
            // No hay más elementos, es el final del array
        } else {
            reportError("Se esperaba COMA o R_CORCHETE");
        }
    }

    private void primitive() throws IOException {
        if (currentToken != null && currentToken.equals("NUMBER")) {
            match("NUMBER");
        } else if (currentToken != null && currentToken.equals("PR_TRUE")) {
            match("PR_TRUE");
        } else if (currentToken != null && currentToken.equals("PR_FALSE")) {
            match("PR_FALSE");
        } else if (currentToken != null && currentToken.equals("PR_NULL")) {
            match("PR_NULL");
        } else {
            reportError("Se esperaba NUMBER, PR_TRUE, PR_FALSE o PR_NULL");
        }
    }

    private void reportError(String message) {
        System.err.println("Error sintáctico: " + message);
    }
}
