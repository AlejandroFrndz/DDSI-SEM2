import java.sql.SQLException;
import java.util.Scanner;

public class Usuario{

    private static MiBaseDatos base_datos = new MiBaseDatos("jdbc:oracle:thin:@//oracle0.ugr.es:1521/practbd.oracle0.ugr.es","x2233897","x2233897");
    private static Color colores = new Color();

    public static void borradoInsercion(){
        // Borrar las tablas
        try{base_datos.borrarTablas();}
        catch (SQLException e)
        {
            e.printStackTrace();
            System.out.println(colores.red + "Error borrando las tablas" + colores.reset);
        }

        // Creación de las tablas
        try{base_datos.crearTablas();}
        catch (SQLException e)
        {
            e.printStackTrace();
            System.out.println(colores.red + "Error creando las tablas"+ colores.reset);
        }

        // Inserción de 10 tuplas predefinidas en la tabla Stock
        try{base_datos.insertarTuplasPredefinidas();}
        catch (SQLException e)
        {
            e.printStackTrace();
            System.out.println(colores.red + "Error insertando las tuplas predefinidas en Stock" + colores.reset);
        } 

        // Imprimir contenido de la tabla Stock
        try{base_datos.imprimirContenidoTabla("Stock");}
        catch (SQLException e)
        {
            System.out.println(colores.red + "Error imprimiendo las tablas"+ colores.reset);
        }
    }

    private static void borrarPedido(){
        // Borrar un pedido
        System.out.println(colores.cyan + "Introduzca la clave del pedido que desea borrar" + colores.reset);
        String cpedido = System.console().readLine();

        try{base_datos.borrarPedido(cpedido);}      // Borrar el pedido identificado por "cpedido"
        catch (SQLException e)
        {
            e.printStackTrace();
            System.out.println(colores.red + "Error borrando el pedido identificado por " + cpedido + colores.reset);
        }
    }
    public static void main(String[] args) {
        String opcion;

        //Me conecto a la BD
        base_datos.conectar();
    
        System.out.print(colores.blue); 
        System.out.println("========================================================================================================================");
        System.out.println("Menú principal");
        System.out.println("========================================================================================================================");
        System.out.print(colores.reset + colores.purple); 
        System.out.println("1. Borrado y nueva creación de las tablas e inserción de 10 tuplas predefinidas en el código en la tabla Stock");
        System.out.println("2. Dar de alta nuevo pedido");
        System.out.println("3. Borrar un pedido (borrando sus detalles en cascada)");
        System.out.println("4. Salir del programa y cerrar conexión a BD");
        System.out.print(colores.reset); 

        // Capturamos la opción elegida por nuestro usuario
        opcion = System.console().readLine();

        while(!opcion.equals("4")){
            switch (opcion) {
                case "1":
                    borradoInsercion();
                break;

                case "2":
                    // Por hacer la interfaz de "Dar de alta nuevo pedido"
                    System.out.println("WIP\n");
                break;
                case "3":
                    borrarPedido();
                break;
                case "4":
                    try{base_datos.cerrarConexion();}      // Cerrar la conexión con la BD
                    catch (SQLException e)
                    {
                        e.printStackTrace();
                        System.out.println(colores.red + "Error cerrando la conexión con la BD" + colores.reset);
                    } 
                break;
                default:
                    System.out.println("Te equivocaste de opción, pulsa un número entre 1 y 4");
                break;
            }

            System.out.print(colores.blue); 
            System.out.println("========================================================================================================================");
            System.out.println("Menú principal");
            System.out.println("========================================================================================================================");
            System.out.print(colores.reset + colores.purple); 
            System.out.println("1. Borrado y nueva creación de las tablas e inserción de 10 tuplas predefinidas en el código en la tabla Stock");
            System.out.println("2. Dar de alta nuevo pedido");
            System.out.println("3. Borrar un pedido (borrando sus detalles en cascada)");
            System.out.println("4. Salir del programa y cerrar conexión a BD");
            System.out.print(colores.reset); 

            opcion = System.console().readLine();
        }
        
    }
}
