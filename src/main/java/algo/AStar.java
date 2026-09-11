/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package algo;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;
import modele.Labyrinthe;
import modele.ResultatRecherche;

/**
 *
 * @author Maroc
 */
public class AStar {
    // comme Dijkstra, mais trié par distance + heuristique pour explorer moins de cases
    public static ResultatRecherche calculerChemin(boolean[][] obstacles, Point depart, Point arrivee,Labyrinthe labyrinthe,boolean sensUnique) {
        long debut = System.nanoTime();
        int lignes = obstacles.length, colonnes = obstacles[0].length;

        int[][] distance = new int[lignes][colonnes];
        for (int[] ligne : distance) Arrays.fill(ligne, Integer.MAX_VALUE);
        Point[][] precedent = new Point[lignes][colonnes];
        boolean[][] visite = new boolean[lignes][colonnes];
        distance[depart.y][depart.x] = 0;

        PriorityQueue<Point> file = new PriorityQueue<>(
            Comparator.comparingInt(p -> distance[p.y][p.x] + heuristique(p, arrivee))
        );
        file.add(depart);

        int[] dx = {0, 0, -1, 1}, dy = {-1, 1, 0, 0};
        List<Point> ordreExploration = new ArrayList<>();

        while (!file.isEmpty()) {
            Point actuel = file.poll();
            if (visite[actuel.y][actuel.x]) continue;
            visite[actuel.y][actuel.x] = true;
            ordreExploration.add(actuel);
            if (actuel.equals(arrivee)) break;

            for (int i = 0; i < 4; i++) {
                int nx = actuel.x + dx[i], ny = actuel.y + dy[i];
                if (nx < 0 || nx >= colonnes || ny < 0 || ny >= lignes) continue;
                if (obstacles[ny][nx] || visite[ny][nx]) continue;
                
                    if (sensUnique && !labyrinthe.estAccessibleSensUnique(actuel,new Point(nx, ny))) {
                        continue;
                    }

                int nouveauCout = distance[actuel.y][actuel.x] + 1;
                if (nouveauCout < distance[ny][nx]) {
                    distance[ny][nx] = nouveauCout;
                    precedent[ny][nx] = actuel;
                    file.add(new Point(nx, ny));
                }
            }
        }

        List<Point> chemin = reconstruireChemin(precedent, depart, arrivee);
        int cout = chemin.isEmpty() ? -1 : distance[arrivee.y][arrivee.x];
        return new ResultatRecherche(chemin, ordreExploration, System.nanoTime() - debut, cout);
    }

    // estimation du coût restant jusqu'à l'arrivée
    private static int heuristique(Point a, Point b) {
        return Math.abs(a.x - b.x) + Math.abs(a.y - b.y);
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
    
