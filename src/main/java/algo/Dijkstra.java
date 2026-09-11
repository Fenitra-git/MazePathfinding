package algo;

import java.awt.Point;
import java.util.*;
import modele.Labyrinthe;
import modele.ResultatRecherche;

public class Dijkstra {

    /**
     * Calcule le chemin le plus court entre deux cases d'une grille.
     *
     * @param obstacles grille booléenne : true = case infranchissable 
     * @param depart    position de départ
     * @param arrivee   position d'arrivée
     * @return liste ordonnée des cases à parcourir, du départ à l'arrivée.
     *         Liste vide si aucun chemin n'existe.
     */
    public static ResultatRecherche calculerChemin(boolean[][] obstacles, Point depart, Point arrivee, Labyrinthe labyrinthe, boolean sensUnique) {
        long debut = System.nanoTime();

        int lignes = obstacles.length;
        int colonnes = obstacles[0].length;

        int[][] distance = new int[lignes][colonnes];
        for (int[] ligne : distance) {
            Arrays.fill(ligne, Integer.MAX_VALUE);
        }
        Point[][] precedent = new Point[lignes][colonnes];
        boolean[][] visite = new boolean[lignes][colonnes];

        distance[depart.y][depart.x] = 0;

        PriorityQueue<Point> file = new PriorityQueue<>( 
            Comparator.comparingInt(p -> distance[p.y][p.x])
        );
        file.add(depart);

        // 4 directions : haut, bas, gauche, droite
        int[] dx = {0, 0, -1, 1};
        int[] dy = {-1, 1, 0, 0};

        List<Point> ordreExploration = new ArrayList<>();

        while (!file.isEmpty()) {
            Point actuel = file.poll();

            if (visite[actuel.y][actuel.x]) {
                continue; 
            }
            visite[actuel.y][actuel.x] = true;
            ordreExploration.add(actuel); // on enregistre l'ordre exact de visite

            if (actuel.equals(arrivee)) {
                break; // arrivée atteinte, plus la peine de continuer
            }

            for (int i = 0; i < 4; i++) {
                int nx = actuel.x + dx[i];
                int ny = actuel.y + dy[i];

                if (nx < 0 || nx >= colonnes || ny < 0 || ny >= lignes) {
                    continue; // hors grille
                }
                if (obstacles[ny][nx]) {
                    continue; // case mur
                }
                if (visite[ny][nx]) {
                    continue;
                }
                
                //respect du sens unique
                if (sensUnique && !labyrinthe.estAccessibleSensUnique(actuel, new Point(nx, ny))) {
                    continue;
                }
                
                int nouveauCout = distance[actuel.y][actuel.x] + 1; // coût uniforme = 1

                if (nouveauCout < distance[ny][nx]) {
                    distance[ny][nx] = nouveauCout;
                    precedent[ny][nx] = actuel;
                    file.add(new Point(nx, ny));
                }
            }
        }
        List<Point> chemin = reconstruireChemin(precedent, depart, arrivee);
        int cout = chemin.isEmpty() ? -1 : distance[arrivee.y][arrivee.x];
 
        long fin = System.nanoTime();
 
        return new ResultatRecherche(chemin, ordreExploration, fin - debut, cout); // Il recommence depuis le départ
    }

    /**
     * Reconstruit le chemin en remontant les "precedent" depuis l'arrivée.
     */
    private static List<Point> reconstruireChemin(Point[][] precedent, Point depart, Point arrivee) {
        List<Point> chemin = new ArrayList<>();

        if (depart.equals(arrivee)) {
            chemin.add(depart);
            return chemin;
        }

        if (precedent[arrivee.y][arrivee.x] == null) {
            return chemin; // aucun chemin trouvé (liste vide)
        }

        Point actuel = arrivee;
        while (actuel != null && !actuel.equals(depart)) {
            chemin.add(0, actuel);
            actuel = precedent[actuel.y][actuel.x];
        }
        chemin.add(0, depart);

        return chemin;

        // petit bout de code à décommenter et executer pour tester
        // public static void main(String[] args) {
        //     boolean[][] obstacles = {
        //             {false, true,  false, false, false},
        //             {false, true,  false, true,  false},
        //             {false, false, false, true,  false},
        //             {true,  true,  false, true,  false},
        //             {false, false, false, false, false}
        //     };
    
        //     Point depart = new Point(0, 0);
        //     Point arrivee = new Point(4, 4);
    
        //     List<Point> chemin = calculerChemin(obstacles, depart, arrivee);
    
        //     if (chemin.isEmpty()) {
        //         System.out.println("Aucun chemin trouvé.");
        //     } else {
        //         System.out.println("Chemin trouvé (" + (chemin.size() - 1) + " pas) :");
        //         for (Point p : chemin) {
        //             System.out.println("(" + p.x + ", " + p.y + ")");
        //         }
        //     }
        // }
    }
}