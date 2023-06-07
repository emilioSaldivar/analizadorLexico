
import py.edu.fpuna.compiladores.parser.Parser;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */

/**
 *
 * @author s4aldiv
 */
public class NewMain {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        Parser par = new Parser();
        par.parseFile("/tmp/resultado.txt");
    }
    
}
