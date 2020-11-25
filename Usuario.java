import java.sql.SQLException;

public class Usuario{
    // victor x0227576
    // paco x2233897
    // alex.fdz x6591260
    private static String usuario = "x0227576";
    private static MiBaseDatos base_datos = new MiBaseDatos("jdbc:oracle:thin:@//oracle0.ugr.es:1521/practbd.oracle0.ugr.es", usuario, usuario);
    private static Color colores = new Color();

    //Función para ejecutar el borrado, creación de las tablas y la inserción de las tuplas predeterminadas
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
    
    //Función para eliminar un pedido con sus detalles en cascada
    private static void borrarPedido(){
        // Borrar un pedido
        System.out.print(colores.cyan + "Introduzca la clave del pedido que desea borrar: " + colores.reset);
        String cpedido = System.console().readLine();

        try{
            if(!base_datos.existePedido(cpedido)){
                System.out.println("El pedido no existe");
                return;
            }
        }      
        catch (SQLException e)
        {
            e.printStackTrace();
            System.out.println(colores.red + "Error comprobando si existe el pedido identificado por " + cpedido + colores.reset);
        }

        try{base_datos.borrarPedido(cpedido);}      // Borrar el pedido identificado por "cpedido"
        catch (SQLException e)
        {
            e.printStackTrace();
            System.out.println(colores.red + "Error borrando el pedido identificado por " + cpedido + colores.reset);
        }

    }

    //Función que ejecuta el cierre de sesión en la BD
    private static void cerrarSesion(){
        try{base_datos.cerrarConexion();}      // Cerrar la conexión con la BD
        catch (SQLException e)
        {
            e.printStackTrace();
            System.out.println(colores.red + "Error cerrando la conexión con la BD" + colores.reset);
        } 
    }

    //Función para la creación del pedido
    private static void crearPedido(){
        String cPedido = "", cCliente, opcion;
        boolean existe = true;
        
        while(existe){          // Pedir el pedido hasta que se introduzca uno que no existe
            System.out.print(colores.cyan + "Introduzca el código del pedido: " + colores.reset);
            cPedido = System.console().readLine();

            try {
                existe = base_datos.existePedido(cPedido);
            } catch (Exception e) {
                System.out.println(colores.red + "Error comprobando si existe el pedido" + colores.reset);
            }

            if(existe)
                System.out.println("Por favor, introduzca un identificador de pedido no existente");
        }

        System.out.print(colores.cyan + "Introduzca el código del cliente: " + colores.reset);
        cCliente = System.console().readLine();

        // INSERTA PEDIDO EN LA TABLA
        try{base_datos.insertarPedido(cPedido, cCliente);}
        catch (SQLException e)
        {
            e.printStackTrace();
            System.out.println(colores.red + "Error dando de alta pedido " + cPedido + colores.reset);
        } 
        
        opcion = "0";

        while(!opcion.equals("4") && !opcion.equals("3")){
            //Submenu con las opciones disponibles
            System.out.println("1. Añadir detalle de producto");
            System.out.println("2. Eliminar todos los detalles de producto");
            System.out.println("3. Cancelar pedido");
            System.out.println("4. Finalizar pedido");

            opcion = System.console().readLine();
            
            switch (opcion) {
                case "1":
                    // Añadir detalle profucto
                    aniadirDetalleProducto(cPedido);
                    break;
                case "2":
                    try {
                        base_datos.rollbackTo("Pedido");
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                    
                    break;
                case "3":
                    try {
                        base_datos.rollback();
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                
                    break;
                case "4":
                    try {
                        base_datos.commit();
                    } catch (SQLException e) {
                        e.printStackTrace();
                        System.out.println("Error al commitear el pedido");
                    }
                    
                    break;
                default:
                    System.out.println("Te equivocaste de opción, pulsa un número entre 1 y 4");
                break;
            }
        }
    }

    public static void aniadirDetalleProducto(String cPedido){
        String cProducto, cantidad = "";
        boolean existeProducto = false, quedaStock = false;
        boolean stockCorrecto = false;
        boolean enCarrito = false;


        while(!existeProducto){
            System.out.print(colores.cyan + "Introduzca el codigo del producto: " + colores.reset);
            cProducto = System.console().readLine();

            try { existeProducto = base_datos.existeProducto(cProducto);}       // Comprobar si existe el producto
            catch (Exception e) {
                System.out.println(colores.red + "Error comprobando si el producto " + cProducto + " existe" + colores.reset);
            }

            if(existeProducto){
                // Comprobar si quedan existencias del producto
                try { quedaStock = base_datos.hayStock(cProducto);} 
                catch (Exception e) {
                    System.out.println(colores.red + "Error comprobando el Stock del producto" + cProducto + colores.reset);
                }

                if(quedaStock){
                    // Pedir el Stock hasta que se introduzca un stock disponible
                    while(!stockCorrecto){
                        System.out.print(colores.cyan + "Introduzca la cantidad que desea: " + colores.reset);
                        cantidad = System.console().readLine();
                        
                        try{stockCorrecto = base_datos.stockSuficiente(cProducto, cantidad);}
                        catch(SQLException e){
                            System.out.println("Hubo un error al comprobar el stock");
                        }
                    }
                    
                    try {
                        enCarrito = base_datos.productoEnCarrito(cPedido, cProducto);
                    } catch (SQLException e) {
                        System.out.println("Hubo un error comprobando el carrito");
                    }

                    if(enCarrito){
                        try {
                            base_datos.aumentarCantidad(cPedido,cProducto,cantidad);
                        } catch (SQLException e) {
                            System.out.println(colores.red + "Error insertando detalles del pedido " + cPedido + colores.reset);
                        }
                    }
                    else{
                        try{base_datos.insertarDetalle_pedido(cPedido, cProducto, cantidad);}
                        catch (SQLException e)
                        {
                            System.out.println(colores.red + "Error insertando detalles del pedido " + cPedido + colores.reset);
                        }
                    }
                }
                else{
                    System.out.println("Lo sentimos, no queda stock de este producto");
                }
            }
            else{
                System.out.println("El producto " + cProducto + " no existe");
            }
        }
    }

    public static void imprimirTablas(){
        try {
            base_datos.imprimirContenidoTabla("Stock");
            base_datos.imprimirContenidoTabla("Pedido");
            base_datos.imprimirContenidoTabla("Detalle_pedido");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    

    public static void main(String[] args) {
        String opcion;

        //Intentar conexión a la base de datos. Si no se establece la conexión se sale del programa
        if(!base_datos.conectar()){
            return;
        }
    
        System.out.print(colores.blue); 
        System.out.println("========================================================================================================================");
        System.out.println("Menú principal");
        System.out.println("========================================================================================================================");

        opcion = "0";
        while(!opcion.equals("5")){

            System.out.print(colores.purple); 
            System.out.println("\n1. Borrado y nueva creación de las tablas e inserción de 10 tuplas predefinidas en el código en la tabla Stock");
            System.out.println("2. Dar de alta nuevo pedido");
            System.out.println("3. Borrar un pedido (borrando sus detalles en cascada)");
            System.out.println("4. Mostrar el contenido de las tablas");
            System.out.println("5. Salir del programa y cerrar conexión a BD");
            System.out.print(colores.reset); 

            // Capturamos la opción elegida por nuestro usuario
            opcion = System.console().readLine();

            switch (opcion) {
                case "1":
                    borradoInsercion();
                break;

                case "2":
                    crearPedido();
                break;

                case "3":
                    borrarPedido();
                break;

                case "4":
                    imprimirTablas();
                break;
                
                case "5":
                    cerrarSesion();
                break;

                default:
                    System.out.println("Te equivocaste de opción, pulsa un número entre 1 y 4");
                break;
            }
        }
    }
}
