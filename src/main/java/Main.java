import java.sql.*;
import java.util.Random;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;


public class Main {

    // @TODO Sistituye xxx por los parámetros de tu conexión

    private static final String DB_SERVER = "localhost";
    private static final int DB_PORT = 3306;
    private static final String DB_NAME = "bd2324";
    private static final String DB_USER = "root";
    private static final String DB_PASS = "root";
    private static Connection conn;

    public static void main(String[] args) throws Exception {
        try {
            // carga del driver
            Class.forName("com.mysql.cj.jdbc.Driver").newInstance();

            // conexion con database
            String url = "jdbc:mysql://" + DB_SERVER + ":" + DB_PORT + "/" + DB_NAME;
            // try & catch

            conn = DriverManager.getConnection(url, DB_USER, DB_PASS);
            System.out.println("Conectado a " + url + " como " + DB_USER);

        } catch (SQLException MainError) {
            System.out.println("Conexion fallida");
        }

        // @TODO Prueba sus funciones

        // 1. Añadete como autor a la base de datos

        try{

        }catch(Exception NuevoAutorError){
            System.out.println("Error integrando nuevos autores");
        }

        nuevoAutor("Jorge Escudero");
        nuevoAutor("Laura Motoa");
        nuevoAutor("Manuel Moreno");
        nuevoAutor("Pablo Foronda");
        nuevoAutor("Javier Lopez");

        // 2. Muestra por pantalla la lista de artículos del autor “Ortega F.” en 2021
        listaArticulosPorAutor("Ortega F.", 2021);

        // 3. Muestra por pantalla una lista de las afiliaciones y el número de autores
         listaAfiliaciones();

         conn.close();
    }

    private static void nuevoAutor(String authorName) throws SQLException {
        // @TODO Crea un metodo que añada un nuevo autor a la base de datos con importancia 0.
        // Genera el id de forma aleatoria

        Random random = new Random();
        PreparedStatement stmt = null;
        try {
            //usar callable statement
            int id = random.nextInt(60000);
            float importance = 0;

            stmt = conn.prepareCall("INSERT INTO author VALUES (? ,? , ?) ");
            stmt.setInt(1, id);
            stmt.setString(2, authorName);
            stmt.setFloat(3, importance);
            stmt.execute();

        } catch (SQLException NuevoAutorError) {
            System.out.println("Error integrando nuevo autor: " + authorName);
        }
        stmt.close();//liberamos recursos

    }

    private static void listaArticulosPorAutor(String authorName, int year) throws SQLException {
        // @TODO Muestra por pantalla una lista de articulos (título y fecha de publicación) para un autor y año
        ResultSet rs = null;

        try {
           PreparedStatement stmt = conn.prepareCall("SELECT title, publication_date FROM article JOIN author_article ON article.DOI = author_article.DOI JOIN author ON author_article.author_id = author.author_id WHERE author_name = ? AND year(publication_date) = ?");
            stmt.setString(1, authorName);
            stmt.setInt(2, year);

            rs = stmt.executeQuery();
            while (rs.next()) {
                String titleArt = rs.getString("title");
                String yearArt = rs.getDate("publication_date").toString();
                System.out.println("Titulo: " + titleArt + "|| Año: " + yearArt);
            }
            stmt.close();
            rs.close();
        } catch (SQLException ListaArticulosError) {
            System.out.println("Error listando articulos de: " + authorName + " en el año: " + year);
        }
    }

    private static void listaAfiliaciones() throws SQLException {
        // @TODO Muestra por pantalla una lista de las instituciones y el numero de autores que tienen
        // @TODO ordenados de más a menos autores
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {

            stmt = conn.prepareCall("SELECT affiliation_name, COUNT(author_id) AS numAutores " +
                    "FROM author_affiliation" +
                    " join affiliation on author_affiliation.affiliation_id = affiliation.affiliation_id" +
                    " GROUP BY affiliation.affiliation_name " +
                    "ORDER BY numAutores DESC");

            rs = stmt.executeQuery();

            System.out.println("Lista de afiliaciones || numero de autores:");
            while (rs.next()) {
                String nameAff = rs.getString("affiliation_name");
                int numAutores = rs.getInt("numAutores");
                System.out.println("Nombre: " + nameAff + " N. autores: " + numAutores);
            }
            stmt.close();
        } catch (SQLException ListaAfiliacionesError) {
            System.out.println("Error listando afiliaciones");
        }
    }
}

