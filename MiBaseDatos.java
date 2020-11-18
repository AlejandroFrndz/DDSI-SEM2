import java.sql.*;

public class MiBaseDatos{
    private Connection conexion = null;
    private Statement sentencia;
    private ResultSet resultado_sentencia;
    private String usuario;
    private String contraseña;
    private String nombre_bd;

    public MiBaseDatos(String nombre_bd, String usuario, String contraseña) {
        this.nombre_bd = nombre_bd;
        this.usuario = usuario;
        this.contraseña = contraseña;
    }

    public void conectar(){
        try 
        {
            conexion = DriverManager.getConnection(nombre_bd, usuario, contraseña);
            if (conexion != null) {
                System.out.println("\nTe has conectado a la BD " + nombre_bd + " con el usuario " + usuario + "\n");
                //Para poder ejecutar sentencias de SQL
                sentencia = conexion.createStatement();
            } 
        } catch (SQLException e) {
            System.err.format("No se ha podido conectar a la base de datos");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void mostrarNombreTablas() throws SQLException         // FUNCIONA
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
        System.out.println("Las tablas de la base de datos son las siguientes: " + nombre_tablas);
    }
 
    public void insertarTuplasPredefinidas() throws SQLException            // FUNCIONA
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

    }

    public void crearTablas() throws SQLException               // FUNCIONA
    {
        // Crear las tablas de nuestro SI
        // Tabla Stock
        sentencia.execute("CREATE TABLE Stock(Cproducto NUMBER PRIMARY KEY, Cantidad NUMBER)");
        // Tabla Pedido
        sentencia.execute("CREATE TABLE Pedido(Cpedido NUMBER PRIMARY KEY, Ccliente NUMBER, Fecha_pedido DATE)");
        // Tabla Detalle_pedido
        sentencia.execute("CREATE TABLE Detalle_pedido(Cpedido REFERENCES Pedido(Cpedido), Cproducto REFERENCES Stock(Cproducto), Cantidad NUMBER, PRIMARY KEY(Cpedido, Cproducto) )");
    }

    public void borrarTablas() throws SQLException              // FUNCIONA
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
        sentencia.execute("INSERT INTO PEDIDO VALUES(" + cpedido + "," + ccliente + ", SYSDATE)");      // Como fecha_pedido es la fecha en la que se realiza el pedido
    }                                                                                                   // le damos el  valor de la fecha del sistema SYSDATE 

    public void insertarDetalle_pedido(String cpedido, String cproducto, String cantidad) throws SQLException
    {
        // Añadir los datos del pedido capturados en la interfaz 
        sentencia.execute("INSERT INTO Detalle_pedido VALUES(" + cpedido + "," + cproducto + ", " + cantidad + ")");                                                                                                                        
    }

    public void elminarDetallesPedido(String Cpedido, String Cproducto) throws SQLException
    {
        sentencia.execute("DELETE FROM Detalle_pedido WHERE Cpedido=" + Cpedido + " AND Cproducto=" + Cproducto);
    }



    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Este método se utilizará para la opción dar alta nuevo pedido y borrar un pedido 
    public void borrarPedido(String Cpedido) throws SQLException
    {
        //Borrar en cascada
            //Primero borrar en Detalle_pedido     
            sentencia.execute("DELETE FROM Detalle_pedido WHERE Cpedido=" + Cpedido);
           
            //Borrar en Pedido
            sentencia.execute("DELETE FROM Pedido WHERE Cpedido=" + Cpedido);
    }

    public void cerrarConexion() throws SQLException
    {
        // Cerramos conexión con la BD
        sentencia.close();
        conexion.close();
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
