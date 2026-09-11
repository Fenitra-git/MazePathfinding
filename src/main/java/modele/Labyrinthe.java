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

    // true = mur, false = passage
    private final boolean[][] grille;
    
    //direction[x][y][direction]
    private final boolean[][][] direction;
    
    // Point de départ
    private final Point entree;

    // Point d'arrivée
    private final Point sortie;

    // Constructeur
    public Labyrinthe() {
        
        this.grille = new boolean[HAUTEUR][LARGEUR];
        this.direction = new boolean[HAUTEUR][LARGEUR][4];

        //4= direction
        //0= haut
        //1=bas
        //2=gauche
        //3=droit
        
        genererLabyrinthe();
    
        // Position du départ
        this.entree = new Point(1, 1);
        // Position de l'arrivée
        this.sortie = new Point(LARGEUR - 2, HAUTEUR - 2);
    }

    // Vérifie si une case est un mur ou hors de la grille
    public boolean estMur(int x, int y) {
        if (x < 0 || x >= getLargeur() || y < 0 || y >= getHauteur()) {
            return true;
        }

        return grille[y][x];
    }
    
    private void genererLabyrinthe() {
         for (boolean[] ligne : grille) {
         Arrays.fill(ligne, true);    
         }
        
        Random random = new Random();
        Deque<Point> pile = new ArrayDeque<>();

        Point depart = new Point(1, 1);
        grille[depart.y][depart.x] = false;
        pile.push(depart);

        // Déplacements de 2 cases (pour garder un mur entre deux passages)
        int[] dx = {0, 0, -2, 2};
        int[] dy = {-2, 2, 0, 0};

        while (!pile.isEmpty()) {
            Point actuel = pile.peek();

            List<Integer> directionsMelanger = new ArrayList<>(List.of(0, 1, 2, 3));
            Collections.shuffle(directionsMelanger, random);

            boolean aAvance = false;

            for (int dir : directionsMelanger) {
                int nx = actuel.x + dx[dir];
                int ny = actuel.y + dy[dir];

                boolean dansLesLimites = nx > 0 && nx < LARGEUR - 1 && ny > 0 && ny < HAUTEUR - 1;

                if (dansLesLimites && grille[ny][nx]) {
                    
                    //case intermediare
                    int mx = actuel.x + dx[dir]/2;
                    int my = actuel.y + dy[dir]/2;
                    
                    // Casse le mur situé entre la case actuelle et la voisine
                    grille[my][mx]= false;
                    grille[ny][nx] = false;
                    
                    /*sens unique
                    actuel - inteediare - nouvelle case
                    on autorise que cette direction
                    */
                    direction[actuel.y][actuel.x][dir] = true;
                    direction[my][mx][dir]= true;
                    
                    pile.push(new Point(nx, ny));
                    aAvance = true;
                    break;
                }
            }
            if (!aAvance) {
                pile.pop(); // aucune voisine disponible, on revient en arrière
            }
        }
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
    
    public boolean estAccessibleSensUnique(Point depart, Point arrivee) {
        
       int dx = arrivee.x - depart.x;
       int dy = arrivee.y - depart.y;
       
       int dir = -1;
        
       if (dx == 0 && dy == -1) {
           dir = 0; // HAUT
       }else if (dx == 0 && dy == 1) {
          dir = 1; // BAS 
       }else if (dx == -1 && dy == 0) {
          dir = 2; // GAUCHE 
       }else if (dx == 1 && dy == 0) {
          dir = 3; // DROITE 
       }
       
       if (dir == -1) {
          return false; 
       }
       
       return direction[depart.y][depart.x][dir];
    }
    
      public boolean[][][] getDirections() {
          return direction;
      }
}
