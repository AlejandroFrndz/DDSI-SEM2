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

    public void mostrarTablas() throws SQLException 
    {
        //ExecuteQuery se usa para sentencias SQL que devuelven un resultado
        resultado_sentencia = sentencia.executeQuery("SELECT table_name FROM user_tables");
        System.out.println("Las tablas de la base de datos son las siguientes: ");
        imprimirResultSet(resultado_sentencia);
    }
   
    //Para imprimir resultado de una sentencia: https://coderwall.com/p/609ppa/printing-the-result-of-resultset
    public void imprimirResultSet(ResultSet rs) throws SQLException 
    {
        ResultSetMetaData rsmd = rs.getMetaData();
        int columnsNumber = rsmd.getColumnCount();
        while (rs.next()) {
            for (int i = 1; i <= columnsNumber; i++) {
                if (i > 1) System.out.print(",  ");
                String columnValue = rs.getString(i);
                System.out.print(columnValue + " " + rsmd.getColumnName(i));
            }
            System.out.println("");
        }
    }

}
