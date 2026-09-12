package modele;

import java.awt.Point;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Random;

/**
 *
 * @author fenit
 */
public class Labyrinthe {

    private static final int LARGEUR = 23;
    private static final int HAUTEUR = 15;
    
    private static final double DENSITE_MURS = 0.28;

    // true = mur, false = passage
    private final boolean[][] grille;
    
    //direction[x][y][direction]
    private final boolean[][][] direction = new boolean[HAUTEUR][LARGEUR][4];
    
    private final boolean[][][] sensUniquePose = new boolean[HAUTEUR][LARGEUR][4];
    
    private final Point entree;
    private final Point sortie;
    
    private final Random random = new Random();

    // Constructeur
    public Labyrinthe() {
        this.grille = genererLabyrinthe();
            
        // Position du départ
        this.entree = new Point(1, 1);
        // Position de l'arrivée
        this.sortie = new Point(LARGEUR - 2, HAUTEUR - 2);
        
        grille[entree.y][entree.x] = false;
        grille[sortie.y][sortie.x] = false;
        
        initialiserDirections();
        
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
                    g[y][x] = true;                                  // murs extérieurs
                } else {
                    g[y][x] = random.nextDouble() < DENSITE_MURS;    // intérieur aléatoire
                }
            }
        }
        return g;
    }
    
    private void initialiserDirections() {
        for (int y = 0; y < HAUTEUR; y++) {
            for (int x = 0; x < LARGEUR; x++) {
                if (grille[y][x]) continue; // mur => on saute
                if (y > 0 && !grille[y-1][x]) direction[y][x][0] = true; // haut
                if (y < HAUTEUR - 1 && !grille[y+1][x]) direction[y][x][1] = true; // bas
                if (x > 0 && !grille[y][x-1]) direction[y][x][2] = true; // gauche
                if (x < LARGEUR - 1 && !grille[y][x+1]) direction[y][x][3] = true; // droite
            }
        }
    }
    
    public boolean[][][] getSensUniquePose() { 
        return sensUniquePose; 
    }

    public boolean toggleSensUnique(int xA, int yA, int xB, int yB) {
        if (estMur(xA, yA) || estMur(xB, yB)) return false;

        int d = directionDepuis(xA, yA, xB, yB);
        if (d == -1) return false;

        boolean dejaPose = sensUniquePose[yA][xA][d];

        if (dejaPose) {
            // Retire : redevient bidirectionnel
            sensUniquePose[yA][xA][d]         = false;
            sensUniquePose[yB][xB][oppose(d)] = false;
            direction[yA][xA][d]              = true;
            direction[yB][xB][oppose(d)]      = true;
        } else {
            // Pose : sens unique A => B
            sensUniquePose[yA][xA][d]         = true;
            sensUniquePose[yB][xB][oppose(d)] = false;
            direction[yA][xA][d]              = true;
            direction[yB][xB][oppose(d)]      = false;
        }
        return true;
    }
    
    public void effacerSensUniques() {
        for (int y = 0; y < HAUTEUR; y++) {
            for (int x = 0; x < LARGEUR; x++) {
                for (int d = 0; d < 4; d++) {
                    sensUniquePose[y][x][d] = false;
                }
            }
        }
        for (int y = 0; y < HAUTEUR; y++) {
            for (int x = 0; x < LARGEUR; x++) {
                for (int d = 0; d < 4; d++) {
                    direction[y][x][d] = false;
                }
            }
        }
        initialiserDirections();
    }
    
    private void garantirChemin() {
        boolean[][] accessible = new boolean[HAUTEUR][LARGEUR];
        Deque<Point> file = new ArrayDeque<>();

        accessible[entree.y][entree.x] = true;
        file.add(entree);

        while (!file.isEmpty()) {
            Point p = file.poll();
            for (int d = 0; d < 4; d++) {
                if (!direction[p.y][p.x][d]) continue;  
                
                int nx = p.x + dxDe(d);
                int ny = p.y + dyDe(d);

                if (nx < 0 || nx >= LARGEUR || ny < 0 || ny >= HAUTEUR) continue;
                if (accessible[ny][nx] || grille[ny][nx]) continue;

                accessible[ny][nx] = true;
                file.add(new Point(nx, ny));
            }
        }


        if (accessible[sortie.y][sortie.x]) return; // déjà OK

        // Creuse depuis la sortie vers l'entrée jusqu'à rejoindre une case accessible
        int x = sortie.x, y = sortie.y;
        int securite = LARGEUR * HAUTEUR * 4;
        while (!accessible[y][x] && securite-- > 0) {
            int nx = x, ny = y;

            if (x == entree.x) {
                ny += (entree.y > y) ? 1 : -1;
            } else if (y == entree.y) {
                nx += (entree.x > x) ? 1 : -1;
            } else if (random.nextBoolean()) {
                nx += (entree.x > x) ? 1 : -1;
            } else {
                ny += (entree.y > y) ? 1 : -1;
            }

            // Mémorise la direction du passage creusé (dans les deux sens)
            int dir = directionDepuis(x, y, nx, ny);
            if (dir != -1) {
                direction[y][x][dir] = true;
                direction[ny][nx][oppose(dir)] = true;
            }

            grille[y][x] = false;
            x = nx;
            y = ny;
        }
        grille[y][x] = false;
    }
    
    private static int dxDe(int d) {
        switch (d) {
            case 2: return -1;
            case 3: return  1;
            default: return 0;
        }
    }

    private static int dyDe(int d) {
        switch (d) {
            case 0: return -1;
            case 1: return  1;
            default: return 0;
        }
    }
    
    private static int directionDepuis(int x1, int y1, int x2, int y2) {
        if (x2 == x1 && y2 == y1 - 1) return 0; // haut
        if (x2 == x1 && y2 == y1 + 1) return 1; // bas
        if (x2 == x1 - 1 && y2 == y1) return 2; // gauche
        if (x2 == x1 + 1 && y2 == y1) return 3; // droite
        return -1;
    }

    private static int oppose(int dir) {
        switch (dir) {
            case 0: return 1;
            case 1: return 0;
            case 2: return 3;
            case 3: return 2;
            default: return -1;
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
