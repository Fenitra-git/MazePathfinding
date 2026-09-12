package vue;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.swing.JPanel;
import javax.swing.Timer;
import modele.Direction;
import modele.Labyrinthe;
import modele.MoteurJeu;
import modele.ResultatRecherche;

/**
 *
 * @author fenit
 */
public class PanneauJeu extends JPanel {

    // Taille d'une case
    private final int CELL_SIZE = 35;
    // Couleurs principales du labyrinthe
    private final Color COULEUR_FOND = new Color(8, 13, 22);
    private final Color COULEUR_MUR = new Color(9, 15, 25);
    private final Color COULEUR_PASSAGE = new Color(20, 30, 46);
    private final Color COULEUR_BORDURE = new Color(36, 52, 74);
    private final Color COULEUR_CHEMIN = new Color(91, 70, 22);

    // Couleur utilisée pour les noeuds explorés
    private static final Map<MoteurJeu.Algorithme, Color> COULEURS_EXPLORATION = new EnumMap<>(MoteurJeu.Algorithme.class);
    static {
        COULEURS_EXPLORATION.put(MoteurJeu.Algorithme.DIJKSTRA, new Color(18, 105, 120));
        COULEURS_EXPLORATION.put(MoteurJeu.Algorithme.A_STAR, new Color(85, 60, 145));
        COULEURS_EXPLORATION.put(MoteurJeu.Algorithme.RECHERCHE_AVEUGLE, new Color(145, 80, 30));
    }

    private MoteurJeu moteurJeu;
    private Labyrinthe labyrinthe;
    // Dimensions du labyrinthe
    private int ROWS;
    private int COL;
    // true = mur, false = passage
    private boolean[][] maze;
    private Point start;
    // Point d'arrivée
    private Point goal;

    // Cases explorées et chemin final, séparés par algorithme
    private Map<MoteurJeu.Algorithme, List<Point>> exploredNodesParAlgo = new EnumMap<>(MoteurJeu.Algorithme.class);
    private Map<MoteurJeu.Algorithme, List<Point>> cheminParAlgo = new EnumMap<>(MoteurJeu.Algorithme.class);
    
    // Timer utilisé pour l'animation des noeuds explorés
    private Timer timerRecherche;
    // Timer utilisé pour le déplacement du joueur sur le chemin
    private Timer timerChemin;
    private java.awt.Point caseSelectionnee = null;

    public PanneauJeu(MoteurJeu moteurJeu) {
        this.moteurJeu = moteurJeu;
        chargerDepuisLabyrinthe();

        // Calcul de la taille du panneau
        int width = COL * CELL_SIZE;
        int height = ROWS * CELL_SIZE;

        this.setPreferredSize(new java.awt.Dimension(width, height));
        this.setBackground(COULEUR_FOND);
        this.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                gererClicSouris(e);
            }
        });
    }

    // Recopie les données du labyrinthe actuel du moteur de jeu dans le panneau
    private void chargerDepuisLabyrinthe() {
        this.labyrinthe = moteurJeu.getLabyrinthe();
        this.ROWS = labyrinthe.getHauteur();
        this.COL = labyrinthe.getLargeur();
        this.maze = labyrinthe.getObstacles();
        this.start = labyrinthe.getEntree();
        this.goal = labyrinthe.getSortie();
    }

    // Appelée après moteurJeu.nouveauLabyrinthe() pour rafraîchir l'affichage
    public void nouveauLabyrinthe() {
        chargerDepuisLabyrinthe();
        exploredNodesParAlgo.clear();
        cheminParAlgo.clear();
        caseSelectionnee = null;
        repaint();
    }

    // Dessine tous les éléments du jeu
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;

        drawMaze(g2);
        drawExploredNodes(g2);
        drawPath(g2);
        drawStartAndGoal(g2);
        drawPlayer(g2);
        drawGrid(g2);
        drawDirections(g2);
        drawSelection(g2); 
    }

    // Dessine les murs du labyrinthe
    private void drawMaze(Graphics2D g2) {
        for (int row = 0; row < ROWS; row++) {
            for (int col = 0; col < COL; col++) {
                int x = col * CELL_SIZE;
                int y = row * CELL_SIZE;

                if (maze[row][col]) {
                    g2.setColor(COULEUR_MUR);
                } else {
                    g2.setColor(COULEUR_PASSAGE);
                }
                g2.fillRect(x, y, CELL_SIZE, CELL_SIZE);
            }
        }
    }
    
    // Dessine les directions des routes lorsque le mode sens unique est activé
    private void drawDirections(Graphics2D g2) {
        boolean[][][] directions  = labyrinthe.getDirections();
        boolean[][][] sensUniques = labyrinthe.getSensUniquePose();
        
        // Si le sens unique est désactivé, aucune flèche n'est affichée
        if (!moteurJeu.isModeEditionSensUnique() && !moteurJeu.isSensUnique()) {
         return;
        }

        g2.setColor(new Color(255, 210, 80));
        g2.setFont(new java.awt.Font("SansSerif", java.awt.Font.BOLD, 18));

        for (int row = 0; row < ROWS; row++) {
            for (int col = 0; col < COL; col++) {
                if (maze[row][col]) continue;

                int centreX = col * CELL_SIZE + CELL_SIZE / 2;
                int centreY = row * CELL_SIZE + CELL_SIZE / 2;

                // On affiche uniquement les directions marquées comme "sens unique posé"
                if (sensUniques[row][col][0]) g2.drawString("↑", centreX - 7, centreY + 6);
                if (sensUniques[row][col][1]) g2.drawString("↓", centreX - 7, centreY + 6);
                if (sensUniques[row][col][2]) g2.drawString("←", centreX - 8, centreY + 6);
                if (sensUniques[row][col][3]) g2.drawString("→", centreX - 5, centreY + 6);
            }
        }
    }

    private void drawExploredNodes(Graphics2D g2) {
        Map<Point, List<Color>> couleursParCase = new HashMap<>();
 
        // L'ordre d'insertion (EnumMap = ordre naturel de l'enum) garantit un rendu stable, toujours dans le même ordre Dijkstra / A* / BFS
        for (Map.Entry<MoteurJeu.Algorithme, List<Point>> entry : exploredNodesParAlgo.entrySet()) {
            Color couleur = COULEURS_EXPLORATION.get(entry.getKey());
            for (Point p : entry.getValue()) {
                couleursParCase.computeIfAbsent(p, k -> new ArrayList<>()).add(couleur);
            }
        }
 
        for (Map.Entry<Point, List<Color>> entry : couleursParCase.entrySet()) {
            dessinerCaseMulticolore(g2, entry.getKey(), entry.getValue());
        }
    }

    // Dessine une case explorée, avec un motif différent selon le nombre d'algorithmes qui l'ont explorée (1 = uni, 2 = diagonale, 3 = bandes)
    private void dessinerCaseMulticolore(Graphics2D g2, Point p, List<Color> couleurs) {
        int marge = 7;
        int taille = CELL_SIZE - 14;
        int x = p.x * CELL_SIZE + marge;
        int y = p.y * CELL_SIZE + marge;
 
        if (couleurs.size() == 1) {
            g2.setColor(couleurs.get(0));
            g2.fillRect(x, y, taille, taille);
 
        } else if (couleurs.size() == 2) {
            // Diagonale : triangle haut-gauche pour la 1ère couleur, bas-droit pour la 2e
            g2.setColor(couleurs.get(0));
            g2.fillPolygon(
                    new int[]{x, x + taille, x},
                    new int[]{y, y, y + taille},
                    3
            );
            g2.setColor(couleurs.get(1));
            g2.fillPolygon(
                    new int[]{x + taille, x + taille, x},
                    new int[]{y, y + taille, y + taille},
                    3
            );
 
        } else {
            // 3 algorithmes : bandes verticales égales
            int largeurBande = taille / couleurs.size();
            for (int i = 0; i < couleurs.size(); i++) {
                g2.setColor(couleurs.get(i));
                g2.fillRect(x + i * largeurBande, y, largeurBande, taille);
            }
        }
    }

    // Dessine le chemin final
    private void drawPath(Graphics2D g2) {
        g2.setColor(COULEUR_CHEMIN);

        Set<Point> casesChemin = new HashSet<>();
        for (List<Point> chemin : cheminParAlgo.values()) {
            casesChemin.addAll(chemin);
        }

        for (Point p : casesChemin) {
            int x = p.x * CELL_SIZE;
            int y = p.y * CELL_SIZE;
            g2.fillRect(x + 5, y + 5, CELL_SIZE - 10, CELL_SIZE - 10);
        }
    }

    // Dessine les cases de départ et d'arrivée avec leurs lettres
    private void drawStartAndGoal(Graphics2D g2) {
        // Case de départ
        int startX = start.x * CELL_SIZE;
        int startY = start.y * CELL_SIZE;

        g2.setColor(new Color(10, 88, 45));
        g2.fillRect(startX, startY, CELL_SIZE, CELL_SIZE);

        // Lettre S
        g2.setColor(new Color(70, 255, 140));
        g2.setFont(new java.awt.Font("SansSerif", java.awt.Font.BOLD, 14));

        g2.drawString("S", startX + (CELL_SIZE / 2) - 5, startY + (CELL_SIZE / 2) + 5);

        // Case d'arrivée
        int goalX = goal.x * CELL_SIZE;
        int goalY = goal.y * CELL_SIZE;

        g2.setColor(new Color(105, 20, 28));
        g2.fillRect(goalX, goalY, CELL_SIZE, CELL_SIZE);

        // Lettre E
        g2.setColor(new Color(255, 110, 110));

        g2.drawString("E", goalX + (CELL_SIZE / 2) - 5, goalY + (CELL_SIZE / 2) + 5);
    }

    // Dessine le joueur
    private void drawPlayer(Graphics2D g2) {
        // Position actuelle récupérée depuis le backend
        Point player = moteurJeu.getJoueur().getPosition();

        int x = player.x * CELL_SIZE;
        int y = player.y * CELL_SIZE;

        g2.setColor(Color.BLUE);
        g2.fillOval(x + 5, y + 5, CELL_SIZE - 10, CELL_SIZE - 10);
    }

    // Dessine les lignes de la grille
    private void drawGrid(Graphics2D g2) {
        // Couleur des lignes qui séparent les cases
        g2.setColor(COULEUR_BORDURE);
        // Dessine les lignes verticales de la grille
        for (int col = 0; col <= COL; col++) {
            int x = col * CELL_SIZE;
            g2.drawLine(x, 0, x, ROWS * CELL_SIZE);
        }

        // Dessine les lignes horizontales de la grille
        for (int row = 0; row <= ROWS; row++) {
            int y = row * CELL_SIZE;
            g2.drawLine(0, y, COL * CELL_SIZE, y);
        }
    }
    
    private void drawSelection(Graphics2D g2) {
        if (caseSelectionnee == null) return;

        int x = caseSelectionnee.x * CELL_SIZE;
        int y = caseSelectionnee.y * CELL_SIZE;

        g2.setColor(new Color(255, 230, 80, 90));
        g2.fillRect(x, y, CELL_SIZE, CELL_SIZE);
        g2.setColor(new Color(255, 230, 80));
        g2.setStroke(new java.awt.BasicStroke(2f));
        g2.drawRect(x + 1, y + 1, CELL_SIZE - 2, CELL_SIZE - 2);
        g2.setStroke(new java.awt.BasicStroke(1f));
    }

    // Anime le déplacement du joueur sur le chemin final
    public void animerChemin(List<Point> chemin, Runnable finAnimation) {
        // Le premier point correspond déjà à la position de départ
        final int[] index = {1};
        timerChemin = new Timer(200, e -> {
            // Arrête l'animation lorsque le chemin est terminé
            if (index[0] >= chemin.size()) {
                timerChemin.stop();
                if (finAnimation != null) {
                    finAnimation.run();
                }
                return;
            }
            // Position actuelle du joueur
            Point positionActuelle = moteurJeu.getJoueur().getPosition();

            // Prochaine position à atteindre
            Point prochainePosition = chemin.get(index[0]);

            // Calcul du déplacement
            int dx = prochainePosition.x - positionActuelle.x;
            int dy = prochainePosition.y - positionActuelle.y;
            Direction direction = null;

            // Détermine la direction à suivre
            if (dx == 1) {
                direction = Direction.DROITE;

            } else if (dx == -1) {
                direction = Direction.GAUCHE;

            } else if (dy == 1) {
                direction = Direction.BAS;

            } else if (dy == -1) {
                direction = Direction.HAUT;
            }

            // Demande au backend de déplacer le joueur
            if (direction != null) {
                moteurJeu.traiterDeplacement(direction);
            }
            // Redessine le joueur
            repaint();
            index[0]++;
        });
        timerChemin.start();
    }

    // Anime progressivement les nœuds explorés pendant la recherche
    public void animerRecherche(MoteurJeu.Algorithme algorithme, List<Point> noeudsExplores,
            List<Point> chemin, ResultatRecherche resultat, FenetreJeu fenetre, Runnable finAnimation) {
 
        exploredNodesParAlgo.clear();
        cheminParAlgo.clear();
        exploredNodesParAlgo.put(algorithme, new ArrayList<>());
 
        final int[] index = {0};
 
        timerRecherche = new Timer(50, e -> {
            if (index[0] >= noeudsExplores.size()) {
                timerRecherche.stop();
 
                cheminParAlgo.put(algorithme, chemin);
                repaint();
 
                fenetre.setNoeudsExplores(resultat.getNoeudsExplores());
                fenetre.setLongueurChemin(resultat.getLongueurChemin());
                fenetre.setCout(resultat.getCout());
                fenetre.setTempsExecution(resultat.getTempsExecutionMs());
 
                animerChemin(chemin, finAnimation);
                return;
            }
 
            exploredNodesParAlgo.get(algorithme).add(new Point(noeudsExplores.get(index[0])));
            repaint();
            index[0]++;
        });
        timerRecherche.start();
    }

    // Anime les 3 algorithmes simultanément (mode comparaison)
    public void animerComparaison(Map<MoteurJeu.Algorithme, ResultatRecherche> resultats,
            FenetreJeu fenetre, Runnable finAnimation) {
 
        exploredNodesParAlgo.clear();
        cheminParAlgo.clear();
 
        for (MoteurJeu.Algorithme algo : resultats.keySet()) {
            exploredNodesParAlgo.put(algo, new ArrayList<>());
        }
 
        // Chaque algorithme avance à son propre rythme : on continue tant que le plus long des 3 n'a pas fini de révéler ses cases explorées
        int maxExplores = resultats.values().stream()
            .mapToInt(r -> r.getOrdreExploration().size())
            .max().orElse(0);
 
        final int[] index = {0};
 
        timerRecherche = new Timer(50, e -> {
            if (index[0] >= maxExplores) {
                timerRecherche.stop();
 
                // Affiche les chemins finaux de chaque algorithme
                for (Map.Entry<MoteurJeu.Algorithme, ResultatRecherche> entry : resultats.entrySet()) {
                    cheminParAlgo.put(entry.getKey(), entry.getValue().getChemin());
                }
                repaint();
 
                // Remplit les 3 cartes de résultats + le tableau de comparaison
                for (Map.Entry<MoteurJeu.Algorithme, ResultatRecherche> entry : resultats.entrySet()) {
                    ResultatRecherche r = entry.getValue();
                    fenetre.getPanneauResultats().setAlgorithmeActuel(entry.getKey());
                    fenetre.setNoeudsExplores(r.getNoeudsExplores());
                    fenetre.setLongueurChemin(r.getLongueurChemin());
                    fenetre.setCout(r.getCout());
                    fenetre.setTempsExecution(r.getTempsExecutionMs());
                }
 
                if (finAnimation != null) {
                    finAnimation.run();
                }
                return;
            }
 
            for (Map.Entry<MoteurJeu.Algorithme, ResultatRecherche> entry : resultats.entrySet()) {
                List<Point> ordre = entry.getValue().getOrdreExploration();
                if (index[0] < ordre.size()) {
                    exploredNodesParAlgo.get(entry.getKey()).add(new Point(ordre.get(index[0])));
                }
            }
 
            repaint();
            index[0]++;
        });
        timerRecherche.start();
    }
    
    // Une case est une intersection si elle a 3 passages ouverts ou plus autour d'elle
    private boolean estIntersection(int row, int col) {
        int voisinsOuverts = 0;
        int[] dx = {0, 0, -1, 1};
        int[] dy = {-1, 1, 0, 0};

        for (int i = 0; i < 4; i++) {
            if (!labyrinthe.estMur(col + dx[i], row + dy[i])) {
                voisinsOuverts++;
            }
        }
        return voisinsOuverts >= 3;
    }
    
    private void gererClicSouris(java.awt.event.MouseEvent e) {
        if (!moteurJeu.isModeEditionSensUnique()) return;

        int col = e.getX() / CELL_SIZE;
        int row = e.getY() / CELL_SIZE;

        if (row < 0 || row >= ROWS || col < 0 || col >= COL) return;
        if (maze[row][col]) return; // clic sur un mur → ignore

        java.awt.Point nouvelle = new java.awt.Point(col, row);

        if (caseSelectionnee == null) {
            // Aucune sélection → on mémorise
            caseSelectionnee = nouvelle;
        } else if (caseSelectionnee.equals(nouvelle)) {
            // Re-clic sur la même case → on désélectionne
            caseSelectionnee = null;
        } else {
            // Deuxième clic : tester l'adjacence
            int dx = Math.abs(nouvelle.x - caseSelectionnee.x);
            int dy = Math.abs(nouvelle.y - caseSelectionnee.y);

            if ((dx == 1 && dy == 0) || (dx == 0 && dy == 1)) {
                // Adjacent → bascule le sens unique
                moteurJeu.toggleSensUnique(
                    caseSelectionnee.x, caseSelectionnee.y,
                    nouvelle.x, nouvelle.y
                );
                caseSelectionnee = null; // on efface la sélection après l'action
            } else {
                // Non adjacent → on déplace la sélection
                caseSelectionnee = nouvelle;
            }
        }

        repaint();
    }

    // Arrete les animations et reinitialise l'affichage de la recherche 
    public void reinitialiserRecherche() {
        if (timerRecherche != null && timerRecherche.isRunning()) {
            timerRecherche.stop();
        }

        if (timerChemin != null && timerChemin.isRunning()) {
            timerChemin.stop();
        }

        exploredNodesParAlgo.clear();
        cheminParAlgo.clear();
        repaint();
    }

    public MoteurJeu getMoteurJeu() {
        return moteurJeu;
    }

}
