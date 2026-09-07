package modele;

import algo.Dijkstra;
import algo.AStar;
import algo.RechercheAveugle;
import Database.DatabaseManager;
import java.util.HashMap;
import java.util.Map;

public class MoteurJeu { // Reçoit les déplacements du joueur et met à jour l'état du jeu

    public enum Etat {
        EN_COURS,
        GAGNE
    }

    public enum Algorithme {
        DIJKSTRA,
        A_STAR,
        RECHERCHE_AVEUGLE
    }

    private Labyrinthe labyrinthe;
    private Joueur joueur;
    private Etat etat;

    public MoteurJeu(Labyrinthe labyrinthe) {
        this.labyrinthe = labyrinthe;
        this.joueur = new Joueur(labyrinthe);
        this.etat = Etat.EN_COURS;
    }

    public void traiterDeplacement(Direction direction) { // Appelé par le front à chaque touche pressée par le joueur. Va vers la direction
        if (etat != Etat.EN_COURS) {
            return;
        }
        joueur.deplacer(direction);
        if (joueur.estArrive()) {
            etat = Etat.GAGNE;
        }
    }

    public ResultatRecherche obtenirIndice() {
        return obtenirIndice(Algorithme.DIJKSTRA);
    }

    public ResultatRecherche obtenirIndice(Algorithme algo) {
        String nomAlgorithme;
        ResultatRecherche resultat;
        switch (algo) {
            case A_STAR:
                resultat = AStar.calculerChemin(
                    labyrinthe.getObstacles(),
                    joueur.getPosition(),
                    labyrinthe.getSortie()
                );
                nomAlgorithme = "A*";
                break;
            case RECHERCHE_AVEUGLE:
                resultat = RechercheAveugle.calculerChemin(
                    labyrinthe.getObstacles(),
                    joueur.getPosition(),
                    labyrinthe.getSortie()
                );
                nomAlgorithme = "Recherche aveugle";
                break;
            default:
                resultat = Dijkstra.calculerChemin(
                    labyrinthe.getObstacles(),
                    joueur.getPosition(),
                    labyrinthe.getSortie()
                );
                nomAlgorithme = "Dijkstra";
                break;
        }
        DatabaseManager.enregistrerResultat(nomAlgorithme, resultat);
        return resultat;
    }

    public Map<Algorithme, ResultatRecherche> comparerAlgorithmes() {
        Map<Algorithme, ResultatRecherche> resultats = new HashMap<>();
        resultats.put(Algorithme.DIJKSTRA, obtenirIndice(Algorithme.DIJKSTRA));
        resultats.put(Algorithme.A_STAR, obtenirIndice(Algorithme.A_STAR));
        resultats.put(Algorithme.RECHERCHE_AVEUGLE, obtenirIndice(Algorithme.RECHERCHE_AVEUGLE));
        return resultats;
    }

    public void reinitialiser() {
        joueur.reinitialiser();
        etat = Etat.EN_COURS;
    }

    // Génère un TOUT NOUVEAU labyrinthe aléatoire, avec un joueur frais dessus
    public void nouveauLabyrinthe() {
        this.labyrinthe = new Labyrinthe();
        this.joueur = new Joueur(labyrinthe);
        this.etat = Etat.EN_COURS;
    }

    public Etat getEtat() {
        return etat;
    }

    public Joueur getJoueur() {
        return joueur;
    }

    public Labyrinthe getLabyrinthe() {
        return labyrinthe;
    }
}
