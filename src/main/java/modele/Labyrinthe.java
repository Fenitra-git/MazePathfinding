package modele;

import java.awt.Point;
import java.util.ArrayList;
import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Collections;
import java.util.Deque;
import java.util.List;
import java.util.Random;

/**
 *
 * @author fenit
 */
public class Labyrinthe {

    private static final int LARGEUR = 23;
    private static final int HAUTEUR = 15;

    //Densité de murs à l'intérieur (0.0 = vide, 1.0 = plein)
    private static final double DENSITE_MURS = 0.28;
    
    // true = mur, false = passage
    private final boolean[][] grille;

    // Point de départ
    private final Point entree;

    // Point d'arrivée
    private final Point sortie;
    
    private final Random random = new Random();

    // Constructeur
    public Labyrinthe() {

        // Grille
        this.grille = genererLabyrinthe();

        // Position du départ
        this.entree = new Point(1, 1);
        // Position de l'arrivée
        this.sortie = new Point(LARGEUR - 2, HAUTEUR - 2);
        
        grille[entree.y][entree.x] = false;
        grille[sortie.y][sortie.x] = false;
        
        garantirChemin();
    }
        
    // Vérifie si une case est un mur ou hors de la grille
    public boolean estMur(int x, int y) {
        if (x < 0 || x >= getLargeur() || y < 0 || y >= getHauteur()) {
            return true;
        }

        return grille[y][x];
    }

    private boolean[][] genererLabyrinthe() {
        boolean[][] g = new boolean[HAUTEUR][LARGEUR];
        for (int y = 0; y < HAUTEUR; y++) {
            for (int x = 0; x < LARGEUR; x++) {
                if (x == 0 || y == 0 || x == LARGEUR - 1 || y == HAUTEUR - 1) {
                    g[y][x] = true;                       // murs extérieurs
                } else {
                    g[y][x] = random.nextDouble() < DENSITE_MURS; // intérieur aléatoire
                }
            }
        }
        return g;
    }

    private void garantirChemin() {
        boolean[][] accessible = new boolean[HAUTEUR][LARGEUR];
        Deque<Point> file = new ArrayDeque<>();
        int[] dx = {1, -1, 0, 0};
        int[] dy = {0, 0, 1, -1};

        accessible[entree.y][entree.x] = true;
        file.add(entree);

        while (!file.isEmpty()) {
            Point p = file.poll();
            for (int i = 0; i < 4; i++) {
                int nx = p.x + dx[i];
                int ny = p.y + dy[i];
                if (nx >= 0 && nx < LARGEUR && ny >= 0 && ny < HAUTEUR
                        && !accessible[ny][nx] && !grille[ny][nx]) {
                    accessible[ny][nx] = true;
                    file.add(new Point(nx, ny));
                }
            }
        }

        if (accessible[sortie.y][sortie.x]) return; // OK

        // Creuse depuis la sortie vers l'entrée jusqu'à rejoindre une case accessible
        int x = sortie.x, y = sortie.y;
        while (!accessible[y][x]) {
            grille[y][x] = false;
            if (x == entree.x) {
                y += (entree.y > y) ? 1 : -1;
            } else if (y == entree.y) {
                x += (entree.x > x) ? 1 : -1;
            } else if (random.nextBoolean()) {
                x += (entree.x > x) ? 1 : -1;
            } else {
                y += (entree.y > y) ? 1 : -1;
            }
        }
        grille[y][x] = false;
    }
    
    // Retourne la grille
    public boolean[][] getObstacles() {
        return grille;
    }

    // Retourne le point de départ
    public Point getEntree() {
        return entree;
    }

    // Retourne le point d'arrivée
    public Point getSortie() {
        return sortie;
    }

    // Retourne la largeur du labyrinthe
    public int getLargeur() {
        return grille[0].length;
    }

    // Retourne la hauteur du labyrinthe
    public int getHauteur() {
        return grille.length;
    }
}
