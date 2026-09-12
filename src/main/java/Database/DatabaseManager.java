package Database;

import modele.ResultatRecherche;
import io.github.cdimascio.dotenv.Dotenv;
import java.awt.Point;
import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class DatabaseManager {

    private static final String DB_HOST = "localhost";
    private static final String DB_PORT = "5432";
    private static final String DB_NAME = "mazepathfinding";
    private static final Dotenv dotenv = Dotenv.configure()
            .ignoreIfMissing()
            .load();
    private static final String DB_USER = dotenv.get("DB_USER");
    private static final String DB_PASSWORD = dotenv.get("DB_PASSWORD");

    private static final String DB_URL = "jdbc:postgresql://" + DB_HOST + ":" + DB_PORT + "/" + DB_NAME;

    private static Connection connecter() throws SQLException {
        return DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
    }

    public static void initialiser() {

        String sql = "CREATE TABLE IF NOT EXISTS recherche_historique ("
                + " id SERIAL PRIMARY KEY,"
                + " algorithme TEXT NOT NULL,"
                + " chemin TEXT NOT NULL,"
                + " longueur_chemin INTEGER NOT NULL,"
                + " cout INTEGER NOT NULL,"
                + " temps_execution_ms REAL NOT NULL,"
                + " noeuds_explores INTEGER NOT NULL,"
                + " date_recherche TEXT NOT NULL"
                + ");";

        try (Connection conn = connecter(); Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        } catch (SQLException e) {
            System.err.println("Erreur d'initialisation de la base de données : " + e.getMessage());
        }
    }

    // Appelé dans le moteurJeu
    public static void enregistrerResultat(String algorithme, ResultatRecherche resultat) {
        String sql = "INSERT INTO recherche_historique "
                + "(algorithme, chemin, longueur_chemin, cout, temps_execution_ms, noeuds_explores, date_recherche) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = connecter(); PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, algorithme);
            ps.setString(2, serialiserChemin(resultat.getChemin()));
            ps.setInt(3, resultat.getLongueurChemin());
            ps.setInt(4, resultat.getCout());
            ps.setDouble(5, resultat.getTempsExecutionMs());
            ps.setInt(6, resultat.getNoeudsExplores());
            ps.setString(7, LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));

            ps.executeUpdate();

        } catch (SQLException e) {
            System.err.println("Erreur lors de l'enregistrement du résultat : " + e.getMessage());
        }
    }

    public static List<HistoriqueRecherche> recupererHistorique() {
        List<HistoriqueRecherche> historique = new ArrayList<>();
        String sql = "SELECT * FROM recherche_historique ORDER BY id DESC";

        try (Connection conn = connecter(); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                historique.add(mapRow(rs));
            }

        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération de l'historique : " + e.getMessage());
        }
        return historique;
    }

    public static List<HistoriqueRecherche> recupererHistoriqueParAlgorithme(String algorithme) {
        List<HistoriqueRecherche> historique = new ArrayList<>();
        String sql = "SELECT * FROM recherche_historique WHERE algorithme = ? ORDER BY id DESC";

        try (Connection conn = connecter(); PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, algorithme);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    historique.add(mapRow(rs));
                }
            }

        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération par algorithme : " + e.getMessage());
        }
        return historique;
    }

    public static Map<String, HistoriqueRecherche> recupererDernierParAlgorithme() {
        Map<String, HistoriqueRecherche> dernier = new LinkedHashMap<>();
        String sql = "SELECT * FROM recherche_historique rh "
                + "WHERE rh.id = (SELECT MAX(id) FROM recherche_historique WHERE algorithme = rh.algorithme) "
                + "ORDER BY rh.algorithme";

        try (Connection conn = connecter(); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                HistoriqueRecherche h = mapRow(rs);
                dernier.put(h.getAlgorithme(), h);
            }

        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération de la comparaison : " + e.getMessage());
        }
        return dernier;
    }

    public static void viderHistorique() {
        String sql = "DELETE FROM recherche_historique";
        try (Connection conn = connecter(); Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        } catch (SQLException e) {
            System.err.println("Erreur lors du vidage de l'historique : " + e.getMessage());
        }
    }

    private static String serialiserChemin(List<Point> chemin) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < chemin.size(); i++) {
            Point p = chemin.get(i);
            if (i > 0) {
                sb.append(";");
            }
            // "," ne peut pas apparaître dans un entier, contrairement à "-" qui est
            // aussi le signe négatif : "-1-2" serait ambigu à la relecture.
            sb.append(p.x).append(",").append(p.y);
        }
        return sb.toString();
    }

    private static List<Point> deserialiserChemin(String texte) {
        List<Point> chemin = new ArrayList<>();
        if (texte == null || texte.isEmpty()) {
            return chemin;
        }
        for (String morceau : texte.split(";")) {
            String[] xy = morceau.split(",");
            if (xy.length != 2) {
                System.err.println("Case ignorée, format invalide : " + morceau);
                continue;
            }
            try {
                chemin.add(new Point(Integer.parseInt(xy[0]), Integer.parseInt(xy[1])));
            } catch (NumberFormatException e) {
                System.err.println("Case ignorée, coordonnée invalide : " + morceau);
            }
        }
        return chemin;
    }

    private static HistoriqueRecherche mapRow(ResultSet rs) throws SQLException {
        return new HistoriqueRecherche(
                rs.getInt("id"),
                rs.getString("algorithme"),
                deserialiserChemin(rs.getString("chemin")),
                rs.getInt("longueur_chemin"),
                rs.getInt("cout"),
                rs.getDouble("temps_execution_ms"),
                rs.getInt("noeuds_explores"),
                rs.getString("date_recherche")
        );
    }
}
