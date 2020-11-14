

public class Usuario{

    public static void main(String[] args) {
        String nombre_bd = "jdbc:oracle:thin:@//oracle0.ugr.es:1521/practbd.oracle0.ugr.es";
        String usuario = "x2233897";
        String contraseña = "x2233897";
        
        MiBaseDatos base_datos = new MiBaseDatos(nombre_bd,usuario,contraseña);

        //Me conecto a la BD
        base_datos.conectar();
        base_datos.mostrarTablas();
    }
}
