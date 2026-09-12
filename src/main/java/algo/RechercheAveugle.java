/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package algo;

import java.awt.Point;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import modele.Labyrinthe;
import modele.ResultatRecherche;

/**
 *
 * @author Maroc
 */
public class RechercheAveugle {
    // parcours en largeur : chaque pas coûte 1, donc le premier chemin trouvé est le plus court
    public static ResultatRecherche calculerChemin(boolean[][] obstacles, Point depart, Point arrivee, Labyrinthe labyrinthe, boolean sensUnique) {
        long debut = System.nanoTime();
        int lignes = obstacles.length, colonnes = obstacles[0].length;

        boolean[][] visite = new boolean[lignes][colonnes];
        Point[][] precedent = new Point[lignes][colonnes];

        Queue<Point> file = new LinkedList<>();
        file.add(depart);
        visite[depart.y][depart.x] = true;

        int[] dx = {0, 0, -1, 1}, dy = {-1, 1, 0, 0};
        List<Point> ordreExploration = new ArrayList<>();

        while (!file.isEmpty()) {
            Point actuel = file.poll();
            ordreExploration.add(actuel);
            if (actuel.equals(arrivee)) break;

            for (int i = 0; i < 4; i++) {
                int nx = actuel.x + dx[i], ny = actuel.y + dy[i];
                if (nx < 0 || nx >= colonnes || ny < 0 || ny >= lignes) continue;
                if (obstacles[ny][nx] || visite[ny][nx]) continue;
                
                //respect de sens unique
                if (sensUnique && !labyrinthe.estAccessibleSensUnique(actuel, new Point(nx, ny))) {
                    continue;
                }
                
                visite[ny][nx] = true;
                precedent[ny][nx] = actuel;
                file.add(new Point(nx, ny));
            }
        }

        List<Point> chemin = reconstruireChemin(precedent, depart, arrivee);
        int cout = chemin.isEmpty() ? -1 : chemin.size() - 1;
        return new ResultatRecherche(chemin, ordreExploration, System.nanoTime() - debut, cout);
    }

    private static List<Point> reconstruireChemin(Point[][] precedent, Point depart, Point arrivee) {
        List<Point> chemin = new ArrayList<>();
        if (depart.equals(arrivee)) { chemin.add(depart); return chemin; }
        if (precedent[arrivee.y][arrivee.x] == null) return chemin;

        Point actuel = arrivee;
        while (actuel != null && !actuel.equals(depart)) {
            chemin.add(0, actuel);
            actuel = precedent[actuel.y][actuel.x];
        }
        chemin.add(0, depart);
        return chemin;
    }
    
}
