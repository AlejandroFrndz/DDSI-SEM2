import java.sql.*;

public class MiBaseDatos{
    private Connection conn = null;
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
        try (Connection conn = DriverManager.getConnection(nombre_bd, usuario, contraseña)) 
        {
            if (conn != null) {
                System.out.println("Te has conectado a la BD " + nombre_bd + ", con el usuario " + usuario);
                //Para poder ejecutar sentencias de SQL
                sentencia = conn.createStatement();
            } else {
                System.out.println("Failed to make connection!");
            }
        } catch (SQLException e) {
            System.err.format("SQL State: %s\n%s", e.getSQLState(), e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void mostrarTablas() throws SQLException 
    {
        //ExecuteQuery se usa para sentencias SQL que devuelven un resultado
        resultado_sentencia = sentencia.executeQuery("SELECT table_name FROM user_tables");
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
