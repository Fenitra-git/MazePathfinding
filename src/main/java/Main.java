
import Database.DatabaseManager;
import javax.swing.SwingUtilities;
import modele.Labyrinthe;
import modele.MoteurJeu;
import modele.ResultatRecherche;
import vue.FenetreJeu;
import vue.PanneauJeu;

public class Main {

    public static void main(String[] args) {

        //DatabaseManager.initialiser();
        SwingUtilities.invokeLater(() -> {

            // Création du labyrinthe et du moteur
            Labyrinthe labyrinthe = new Labyrinthe();
            MoteurJeu moteurJeu = new MoteurJeu(labyrinthe);

            new FenetreJeu(moteurJeu);
            DatabaseManager.initialiser();
        });
    }
}
