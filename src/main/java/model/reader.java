package model;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;
import javax.swing.JOptionPane;

/* @author Ailer Alvarado - Armando Arce - Daniel Rojas*/
public class reader {
    private static List<String>[] memoria = new ArrayList[25];
    enum Operation {
        LOAD,
        STORE,
        MOV,
        SUB,
        ADD
    }

    enum Memory {
        AX,
        BX,
        CX,
        DX
    }
    /**
     * Busca el índice de la primera ocurrencia de una lista vacía en un arreglo 
     * de listas de cadenas, a partir del índice 'first_i'.
     * 
     * @param check_memory el arreglo de listas de cadenas en el que se va a buscar
     * @param first_i el índice a partir del cual se debe empezar a buscar
     * @return el índice de la primera ocurrencia de la lista vacía, 
     * o -1 si no se encuentra
     */
    public static int buscarIndice(List<String>[] check_memory, int first_i) {
        for (int i = first_i; i < check_memory.length; i++) {
            if (check_memory[i].isEmpty()){
                return i;
            }
        }
        return -1;
    }
    
    /**
     * Busca el índice de un segmento vacío de tamaño 'n' en un arreglo de listas de cadenas.
     * El método comienza la búsqueda a partir del índice 10 y si encuentra un segmento vacío 
     * de tamaño 'n' lo retorna. Si no encuentra un segmento vacío de tamaño 'n', retorna -1.
     * 
     * @param check_memory el arreglo de listas de cadenas en el que se realiza la búsqueda
     * @param n el tamaño del segmento vacío que se busca
     * @return el índice del inicio del segmento vacío, o -1 si no se encuentra
     */
    public static int buscarSegmentoVacio(List<String>[] check_memory, int n) {
        int first_i = 10;
        while (first_i < check_memory.length) {
            int indice = buscarIndice(check_memory, first_i);
            if (indice == -1 || indice + n > check_memory.length) {
                break;
            }
            boolean segmentoVacio = true;
            for (int i = indice + 1; i < indice + n; i++) {
                if (!check_memory[i].isEmpty()) {
                    segmentoVacio = false;
                    break;
                }
            }
            if (segmentoVacio) {
                return indice;
            }
            first_i = indice + 1;
        }
        return -1;
    }

    /**
     * Busca el índice de un segmento vacío de tamaño 'n' en un arreglo de cadenas,
     * de forma aleatoria. El método comienza la búsqueda a partir del índice 10,
     * y si encuentra un segmento vacío lo retorna. Si no encuentra un segmento 
     * vacío de tamaño 'n', retorna -1.
     * 
     * @param check_memory el arreglo de cadenas en el que se realiza la búsqueda
     * @param n el tamaño del segmento vacío que se busca
     * @return el índice del inicio del segmento vacío, o -1 si no se encuentra
     */
    public static int buscarSegmentoVacioRandom(List<String>[] check_memory, int n) {
        List<Integer> indicesSeleccionados = new ArrayList<>();
        int first_i = 10;
        Random rand = new Random();
        while (first_i < check_memory.length) {
            int indice = rand.nextInt(check_memory.length - n - 10) + 10;
            while (indicesSeleccionados.contains(indice)) {
                indice = rand.nextInt(check_memory.length - n);
            }
            indicesSeleccionados.add(indice);

            boolean segmentoVacio = true;
            for (int i = indice; i < indice + n; i++) {
                if (!check_memory[i].isEmpty()) {
                    segmentoVacio = false;
                    break;
                }
            }
            if (segmentoVacio) {
                return indice;
            }
            first_i = indice + 1;
            
            if (check_memory.length - n - n == indicesSeleccionados.size()) { // verifica si se ya no hay memoria disponible o un segmento posible
                return -1;
            }
        }
        return -1;
    }


    /**
    * Valida una línea de entrada, convierte los tokens a mayúsculas y los divide por espacio.
    *
    * Luego, verifica si la primera palabra (operación) es una Operación válida del Enum Operation.
    * A continuación, verifica si el segundo token (memoria) es una memoria válida del Enum Memory.
    * Si el segundo token termina con "," lo elimina.
    * Si la operación es MOV, verifica si hay un tercer token (valor) y que sea un entero.
    * Si la línea es válida, devuelve una lista de tokens, en orden de operación, memoria y valor (si es que hay uno).
    * Si la línea no es válida, devuelve una lista vacía.
    *
    * @param line la línea de entrada a validar y analizar
    * @return una lista de tokens válidos si la línea es válida, de lo contrario una lista vacía
    */
    public static List validarLinea(String line) {
        
        line = line.toUpperCase();
        String[] lista = line.split(" ");

        try {
            List<String> tokenList = new ArrayList<>();
            Operation op = Operation.valueOf(lista[0]);

            String tokenMemory = lista[1];

            if (tokenMemory.endsWith(",")) {
                tokenMemory = tokenMemory.substring(0, tokenMemory.length() - 1);
            }

            Memory mem = Memory.valueOf(tokenMemory);

            tokenList.add(0, op.toString());
            tokenList.add(1, mem.toString());
            if (op.equals(Operation.MOV)) {
                String value = lista[2];

                Integer.parseInt(lista[2]);
                tokenList.add(2, value);
            } 
            return tokenList;
        } catch (Exception ex) {
            return new ArrayList<>();
        }
    }

    /**
     * Asigna un bloque de memoria consecutiva a partir de un índice aleatorio a un
     * conjunto de líneas. El número de líneas en el bloque está determinado por el
     * tamaño de la lista de líneas de entrada. Si no se puede encontrar un bloque 
     * de memoria lo suficientemente grande, se imprime un mensaje de error.
     * 
     * @param lineas la lista de líneas a asignar a la memoria
     */
    public static void asignarMemoria(List<List<String>> lineas){
        int n = lineas.size(); // buscar segmentos de n lineas vacías consecutivas
        int indice = buscarSegmentoVacioRandom(memoria, n); // busca un indice posible de forma random a partir del indice 10
        if (indice == -1) {
            System.out.println("Memoria insuficiente.");
        } else {
            System.out.println("Memoria asginada.");
            int counter = 0;
            for (List<String> temp_linea : lineas) {
                memoria[indice + counter] = temp_linea;
                counter++;
            }
        }
    }
    
    /**
     * Lee un archivo de texto en la ruta especificada y 
     * almacena los tokens de las líneas válidas en la memoria.
     * 
     * @param rutaArchivo la ruta del archivo a leer
     * @param memory_ la memoria previamente cargada
     * @param limpear define si la memoria debe ser limpeada
     * @return la memoria con el nuevo estado
     */
    public static List<String>[] leerArchivo(String rutaArchivo, List<String>[] memory_, boolean limpear){
        boolean flag_error = false;
        String error_message = "";
        Integer numero_linea = 0;
        File archivo = new File(rutaArchivo); // Crea una instancia de File con la ruta del archivo
        List<List<String>> tokenList = new ArrayList<>(); // lista donde se almacenan temporalmente las lineas del *.asm
        if (limpear){
            for (int i = 0; i < memoria.length; i++) { memoria[i] = new ArrayList<>(); } // Limpeamos la memoria
        } else{
            memoria = memory_;
        }
        
        try {
            
            Scanner scanner = new Scanner(archivo); // Crea una instancia de Scanner para leer el archivo
            while (scanner.hasNextLine()) { // Recorre el archivo línea por línea e imprime cada línea
                numero_linea += 1;
                String linea = scanner.nextLine();
                List<String> temp_tokenList = validarLinea(linea);
                if (!temp_tokenList.isEmpty()){
                    tokenList.add(temp_tokenList);
                } else{
                    flag_error = true;
                    error_message = String.format("(%d: %s) no es una instruccion valida", numero_linea, linea);
                    break;
                }
            }
            scanner.close(); // Cierra el scanner para liberar los recursos
        } catch (FileNotFoundException e) {
            System.out.println("El archivo no existe o no se puede leer.");
        }
        if (!flag_error){
            asignarMemoria(tokenList);
        } else{
            System.out.println("La memoria no ha sido asignada.");
            JOptionPane.showMessageDialog(null, error_message,"Error al leer el archivo", JOptionPane.ERROR_MESSAGE);
        }  
        return memoria;
    }
    
    public static void main(String[] args) {}
}
