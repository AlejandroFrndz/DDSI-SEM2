import java.sql.*;
import java.util.ArrayList;

public class MiBaseDatos{
    private Connection conexion = null;
    private Statement sentencia;
    private ResultSet resultado_sentencia;
    private String usuario;
    private String passwd;
    private String nombre_bd;

    public MiBaseDatos(String nombre_bd, String usuario, String passwd) {
        this.nombre_bd = nombre_bd;
        this.usuario = usuario;
        this.passwd = passwd;
    }

    public Boolean conectar(){
        try 
        {
            conexion = DriverManager.getConnection(nombre_bd, usuario, passwd);
            conexion.setAutoCommit(false);
            if (conexion != null) {
                System.out.println("\nTe has conectado a la BD " + nombre_bd + " con el usuario " + usuario + "\n");
                //Para poder ejecutar sentencias de SQL
                sentencia = conexion.createStatement();
            }
            return true;
        } catch (SQLException e) {
            System.err.format("No se ha podido conectar a la base de datos\n");
            return false;
        }
    }

    public String mostrarNombreTablas() throws SQLException
    {
        //ExecuteQuery se usa para sentencias SQL que devuelven un resultado
        resultado_sentencia = sentencia.executeQuery("SELECT table_name FROM user_tables");
        String nombre_tablas = "";
        
        while(resultado_sentencia.next()) //Recorre cada fila de la salida
        {
            //Como la salida tiene dos columnas, ej: DETALLE_PEDIDO TABLE_NAME
            //Nos quedamos con la primera columna, que es el nombre de la tabla.
            String nombre = resultado_sentencia.getString(1);
            if(nombre_tablas.isEmpty())
                nombre_tablas += nombre;
            else
                nombre_tablas += ", " + nombre;
        }
        
        return nombre_tablas;
    }
 
    public void insertarTuplasPredefinidas() throws SQLException
    {
        //Insertar 10 tuplas predefinidas en la tabla Stock
        sentencia.execute("INSERT INTO Stock (Cproducto, Cantidad) VALUES ('1', '5')");
        sentencia.execute("INSERT INTO Stock (Cproducto, Cantidad) VALUES ('2', '15')");
        sentencia.execute("INSERT INTO Stock (Cproducto, Cantidad) VALUES ('3', '3')");
        sentencia.execute("INSERT INTO Stock (Cproducto, Cantidad) VALUES ('4', '5')");
        sentencia.execute("INSERT INTO Stock (Cproducto, Cantidad) VALUES ('5', '8')");
        sentencia.execute("INSERT INTO Stock (Cproducto, Cantidad) VALUES ('6', '10')");
        sentencia.execute("INSERT INTO Stock (Cproducto, Cantidad) VALUES ('7', '26')");
        sentencia.execute("INSERT INTO Stock (Cproducto, Cantidad) VALUES ('8', '1')");
        sentencia.execute("INSERT INTO Stock (Cproducto, Cantidad) VALUES ('9', '9')");
        sentencia.execute("INSERT INTO Stock (Cproducto, Cantidad) VALUES ('10', '30')");
        sentencia.execute("COMMIT");

    }

    public void crearTablas() throws SQLException
    {
        // Crear las tablas de nuestro SI
        // Tabla Stock
        sentencia.execute("CREATE TABLE Stock(Cproducto NUMBER CONSTRAINT CP_Stock PRIMARY KEY, Cantidad INT CONSTRAINT cantidad_positiva_STOCK CHECK( Cantidad >= 0) )");
        // Tabla Pedido
        sentencia.execute("CREATE TABLE Pedido(Cpedido NUMBER CONSTRAINT CP_Pedido PRIMARY KEY, Ccliente NUMBER, Fecha_pedido DATE)");
        // Tabla Detalle_pedido
        sentencia.execute("CREATE TABLE Detalle_pedido(Cpedido CONSTRAINT CE_Detalle_Cpedido REFERENCES Pedido(Cpedido) , Cproducto CONSTRAINT CE_Detalle_Cproducto REFERENCES Stock(Cproducto), Cantidad INT CONSTRAINT cantidad_positiva_DETALLE CHECK (Cantidad > 0), CONSTRAINT CP_Detalle PRIMARY KEY(Cpedido, Cproducto) )");
    }

    public void borrarTablas() throws SQLException 
    {
        // Eliminar las tablas del SI
        // Tabla Detalle_pedido
        sentencia.execute("DROP TABLE Detalle_pedido");
        
        // Tabla Stock
        sentencia.execute("DROP TABLE Stock");
        
        // Tabla Pedido
        sentencia.execute("DROP TABLE Pedido");
    }
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // FUNCIONES NECESARIAS PARA LA TRANSACCIÓN DAR DE ALTA NUEVO PEDIDO
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    public void insertarPedido(String cpedido, String ccliente ) throws SQLException
    {
        // Añadir los datos del pedido capturados en la interfaz 
        sentencia.execute("INSERT INTO PEDIDO VALUES(" + cpedido + "," + ccliente + ", SYSDATE)");      // Como fecha_pedido es la fecha en la que se realiza el pedido le damos el  valor de la fecha del sistema SYSDATE 
        sentencia.execute("SAVEPOINT Pedido");
    }                                                                                                   

    public void insertarDetalle_pedido(String cpedido, String cproducto, String cantidad) throws SQLException
    {
        sentencia.execute("UPDATE Stock SET Cantidad = Cantidad-" + cantidad + " WHERE Cproducto=" + cproducto);                                                                                                                       

        // Añadir los datos del pedido capturados en la interfaz 
        sentencia.execute("INSERT INTO Detalle_pedido VALUES(" + cpedido + "," + cproducto + ", " + cantidad + ")");
    }

    public void elminarDetallesPedido(String Cpedido, String Cproducto) throws SQLException
    {
        sentencia.execute("DELETE FROM Detalle_pedido WHERE Cpedido=" + Cpedido + " AND Cproducto=" + Cproducto);
    }

    public void commit() throws SQLException{
        sentencia.execute("COMMIT");
    }

    public void rollback() throws SQLException{
        sentencia.execute("ROLLBACK");
    }

    public void rollbackTo(String savepoint) throws SQLException{
        sentencia.execute("ROLLBACK TO " + savepoint);
    }


    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Este método se utilizará para la opción dar alta nuevo pedido y borrar un pedido 
    public void borrarPedido(String Cpedido) throws SQLException
    {
        //Borrar en cascada

            DevolverStock(Cpedido);

            //Primero borrar en Detalle_pedido     
            sentencia.execute("DELETE FROM Detalle_pedido WHERE Cpedido=" + Cpedido);
           
            //Borrar en Pedido
            sentencia.execute("DELETE FROM Pedido WHERE Cpedido=" + Cpedido);

            sentencia.execute("COMMIT");
    }

    public void cerrarConexion() throws SQLException
    {
        // Cerramos conexión con la BD
        sentencia.close();
        conexion.close();
    }


    // Función que comprueba si hay Stock de un producto
    public boolean hayStock(String cproducto) throws SQLException
    {
        // Sacamos la cantidad de Stock que hay para ese producto
        resultado_sentencia = sentencia.executeQuery("SELECT Cantidad FROM Stock WHERE Cproducto=" + cproducto);
        resultado_sentencia.next();     // resultado que buscamos

        // Comprobar que la cantidad introducida sea mayor que 0
        if(Integer.parseInt(resultado_sentencia.getString(1)) == 0)
            return false;
        
        return true;
    }



    // Función que comprueba si hay Stock suficiente de un producto
    public boolean stockSuficiente(String cproducto, String cantidad) throws SQLException
    {
        boolean correcto = true;

        // Sacamos la cantidad de Stock que hay para ese producto
        resultado_sentencia = sentencia.executeQuery("SELECT Cantidad FROM Stock WHERE Cproducto=" + cproducto);
        resultado_sentencia.next();     // resultado que buscamos

        // Comprobar que la cantidad introducida sea mayor que 0
        if(Integer.parseInt(cantidad) > 0){
            if(Integer.parseInt(resultado_sentencia.getString(1)) < Integer.parseInt(cantidad)) {       // Pasar de String a int
                System.out.println("Lo sentimos, el stock máximo de este producto es " + resultado_sentencia.getString(1));
                correcto = false;
            }
        }
        else{
            System.out.println("Debes introducir una cantidad mayor que 0");
            correcto = false;
        }
        
        return correcto;
    }


    // Función que comprueba si un producto introducido existe o no
    public boolean existeProducto(String cproducto) throws SQLException
    {
        resultado_sentencia = sentencia.executeQuery("SELECT * FROM Stock WHERE Cproducto=" + cproducto);  

        if(!resultado_sentencia.next()){     // El método next devolverá un 0 si no hay contenido (no existe el producto)
            return false;
        }     
        
        return true;
    }


    // Función que comprueba si un pedido introducido existe o no
    public boolean existePedido(String cpedido) throws SQLException
    {
        resultado_sentencia = sentencia.executeQuery("SELECT * FROM Pedido WHERE Cpedido=" + cpedido);  

        if(!resultado_sentencia.next()){     // El método next devolverá un 0 si no hay contenido (no existe el pedido)
            return false;
        }     
        
        return true;
    }

    //Función que comprueba si el producto a añadir a un pedido ya está en el carrito
    public boolean productoEnCarrito(String cPedido, String cProducto) throws SQLException{
        
        resultado_sentencia = sentencia.executeQuery("SELECT * FROM Detalle_pedido WHERE Cpedido=" + cPedido + "AND Cproducto=" + cProducto);

        if(!resultado_sentencia.next()){
            return false;
        }

        return true;
    }

    //Función que aumenta la cantidad de un producto en el carrito
    public void aumentarCantidad(String cPedido, String cProducto, String cantidad) throws SQLException{

        //Decrementar el stock del producto añadido
        sentencia.execute("UPDATE Stock SET Cantidad = Cantidad-" + cantidad + " WHERE Cproducto=" + cProducto);
        //Incrementar la cantidad del producto añadido
        sentencia.execute("UPDATE Detalle_pedido SET Cantidad = Cantidad+" + cantidad + " WHERE Cpedido=" + cPedido + " AND Cproducto=" + cProducto);
    }

    public void DevolverStock(String cPedido) throws SQLException{
        resultado_sentencia = sentencia.executeQuery("SELECT Cproducto, Cantidad FROM Detalle_Pedido WHERE Cpedido=" + cPedido);
        ArrayList<String> productos, cantidades;
        productos = new ArrayList<>();
        cantidades = new ArrayList<>();

        while (resultado_sentencia.next()) {
            productos.add(resultado_sentencia.getString(1));
            cantidades.add(resultado_sentencia.getString(2));
        }

        for(int i = 0; i < productos.size(); i++){
            sentencia.execute("UPDATE Stock SET Cantidad = Cantidad+" + cantidades.get(i) + " WHERE Cproducto=" + productos.get(i));
        }
    }

    public void imprimirContenidoTabla(String tabla) throws SQLException 
    {
        resultado_sentencia = sentencia.executeQuery("SELECT * FROM " + tabla);
        ResultSetMetaData rsmd = resultado_sentencia.getMetaData();
        int columnsNumber = rsmd.getColumnCount();
        
        // Formar la cadena que contendrá el nombre de las columnas
        String nombre_columnas="";
        for(int i=1; i<=rsmd.getColumnCount(); i++){
            nombre_columnas = nombre_columnas + "|" + rsmd.getColumnName(i) + "|\t";
        }
        
        System.out.println("\n_____________________________________________");
        System.out.println("               Tabla " + tabla);

        System.out.println(nombre_columnas);     // Imprimir las columnas de la tabla
        
        //Imprimir el contenido
        while (resultado_sentencia.next()) {
            for (int i = 1; i <= columnsNumber; i++) {
                if (i > 1) System.out.print("\t\t");
                String columnValue = resultado_sentencia.getString(i);
                System.out.print("    " + columnValue);
            }
            System.out.println("");
        }
    }

}
