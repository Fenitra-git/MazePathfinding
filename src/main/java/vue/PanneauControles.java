package vue;

import java.awt.Color;
import java.util.Map;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import modele.MoteurJeu;
import modele.ResultatRecherche;

/**
 *
 * @author fenitra
 */
public class PanneauControles extends JPanel {

    private JButton boutonDijkstra;
    private JButton boutonAStar;
    private JButton boutonBFS;
    private JButton boutonLancer;
    private JButton boutonLancerSimultane;
    private MoteurJeu.Algorithme algorithmeSelectionne = MoteurJeu.Algorithme.DIJKSTRA;

    public PanneauControles(PanneauJeu panneauJeu, FenetreJeu fenetre) {
        // Configuration du panneau gauche
        this.setLayout(new javax.swing.BoxLayout(this, javax.swing.BoxLayout.Y_AXIS));
        this.setBackground(new Color(15, 24, 40));
        this.setPreferredSize(new java.awt.Dimension(250, 0));
        this.setBorder(BorderFactory.createEmptyBorder(30, 20, 20, 20));

        // Titre de la section des algorithmes
        JLabel titreAlgorithmes = new JLabel("ALGORITHMES");
        titreAlgorithmes.setForeground(new Color(150, 170, 200));
        titreAlgorithmes.setFont(new java.awt.Font("SansSerif", java.awt.Font.BOLD, 13));
        titreAlgorithmes.setAlignmentX(java.awt.Component.LEFT_ALIGNMENT);

        Color couleurDijkstra = new Color(25, 200, 230);
        Color couleurAStar = new Color(145, 100, 255);
        Color couleurBFS = new Color(255, 145, 55);
        Color couleurBordure = new Color(45, 60, 80);

        // Bouton Dijkstra
        boutonDijkstra = new javax.swing.JButton("<html><span style='color:#19C8E6;'>●</span>&nbsp;&nbsp;Dijkstra</html>");
        boutonDijkstra.setAlignmentX(java.awt.Component.LEFT_ALIGNMENT);
        boutonDijkstra.setMaximumSize(new java.awt.Dimension(210, 38));
        boutonDijkstra.setBackground(new Color(25, 38, 58));
        boutonDijkstra.setForeground(Color.WHITE);
        boutonDijkstra.setFocusPainted(false);
        boutonDijkstra.setBorder(BorderFactory.createLineBorder(couleurBordure, 1));

        // Bouton A*
        boutonAStar = new javax.swing.JButton("<html><span style='color:#9164FF;'>●</span>&nbsp;&nbsp;A*</html>");
        boutonAStar.setAlignmentX(java.awt.Component.LEFT_ALIGNMENT);
        boutonAStar.setMaximumSize(new java.awt.Dimension(210, 38));
        boutonAStar.setBackground(new Color(25, 38, 58));
        boutonAStar.setForeground(Color.WHITE);
        boutonAStar.setFocusPainted(false);
        boutonAStar.setBorder(BorderFactory.createLineBorder(couleurBordure, 1));

        // Bouton BFS
        boutonBFS = new javax.swing.JButton("<html><span style='color:#FF9137;'>●</span>&nbsp;&nbsp;BFS</html>");
        boutonBFS.setAlignmentX(java.awt.Component.LEFT_ALIGNMENT);
        boutonBFS.setMaximumSize(new java.awt.Dimension(210, 38));
        boutonBFS.setBackground(new Color(25, 38, 58));
        boutonBFS.setForeground(Color.WHITE);
        boutonBFS.setFocusPainted(false);
        boutonBFS.setBorder(BorderFactory.createLineBorder(couleurBordure, 1));

        // Dijkstra est sélectionné par défaut au lancement
        boutonDijkstra.setForeground(couleurDijkstra);
        boutonDijkstra.setBorder(BorderFactory.createLineBorder(couleurDijkstra, 2));

        // Sélection de Dijkstra
        boutonDijkstra.addActionListener(e -> {
            algorithmeSelectionne = MoteurJeu.Algorithme.DIJKSTRA;
            boutonDijkstra.setForeground(couleurDijkstra);
            boutonAStar.setForeground(Color.WHITE);
            boutonBFS.setForeground(Color.WHITE);

            boutonDijkstra.setBorder(BorderFactory.createLineBorder(couleurDijkstra, 2));
            boutonAStar.setBorder(BorderFactory.createLineBorder(couleurBordure, 1));
            boutonBFS.setBorder(BorderFactory.createLineBorder(couleurBordure, 1));
        });

        // Sélection de A*
        boutonAStar.addActionListener(e -> {
            algorithmeSelectionne = MoteurJeu.Algorithme.A_STAR;
            boutonDijkstra.setForeground(Color.WHITE);
            boutonAStar.setForeground(couleurAStar);
            boutonBFS.setForeground(Color.WHITE);

            boutonDijkstra.setBorder(BorderFactory.createLineBorder(couleurBordure, 1));
            boutonAStar.setBorder(BorderFactory.createLineBorder(couleurAStar, 2));
            boutonBFS.setBorder(BorderFactory.createLineBorder(couleurBordure, 1));
        });

        // Sélection de BFS
        boutonBFS.addActionListener(e -> {
            algorithmeSelectionne = MoteurJeu.Algorithme.RECHERCHE_AVEUGLE;
            boutonDijkstra.setForeground(Color.WHITE);
            boutonAStar.setForeground(Color.WHITE);
            boutonBFS.setForeground(couleurBFS);

            boutonDijkstra.setBorder(BorderFactory.createLineBorder(couleurBordure, 1));
            boutonAStar.setBorder(BorderFactory.createLineBorder(couleurBordure, 1));
            boutonBFS.setBorder(BorderFactory.createLineBorder(couleurBFS, 2));
        });

        // Ajout des éléments
        this.add(titreAlgorithmes);
        this.add(javax.swing.Box.createVerticalStrut(15));
        this.add(boutonDijkstra);
        this.add(javax.swing.Box.createVerticalStrut(8));
        this.add(boutonAStar);
        this.add(javax.swing.Box.createVerticalStrut(8));
        this.add(boutonBFS);

        this.add(javax.swing.Box.createVerticalStrut(30));

        // Titre de la section contraintes
        JLabel titreContraintes = new JLabel("CONTRAINTES");
        titreContraintes.setForeground(new Color(150, 170, 200));
        titreContraintes.setFont(new java.awt.Font("SansSerif", java.awt.Font.BOLD, 13));
        titreContraintes.setAlignmentX(java.awt.Component.LEFT_ALIGNMENT);

        this.add(titreContraintes);
        this.add(javax.swing.Box.createVerticalStrut(12));
        
        //Bouton pour sense unique
        JButton boutonSenseUnique = new JButton("➜  Sens unique");
        
        boutonSenseUnique.setForeground(Color.white);
        boutonSenseUnique.setBackground(new Color(25, 38, 58));
        boutonSenseUnique.setFont(new java.awt.Font("SansSerif", java.awt.Font.PLAIN, 13));
        boutonSenseUnique.setFocusPainted(false);
        boutonSenseUnique.setBorder(BorderFactory.createLineBorder(couleurBordure, 1));
        boutonSenseUnique.setAlignmentX(java.awt.Component.LEFT_ALIGNMENT);
        boutonSenseUnique.setMaximumSize(new java.awt.Dimension(220, 38));
        
        // État du bouton
        final boolean[] sensUniqueActive = {false};
        
        // Quand on clique sur le bouton
        boutonSenseUnique.addActionListener(e -> {
            sensUniqueActive[0] = !sensUniqueActive[0];
            
            panneauJeu.getMoteurJeu().setSensUnique(sensUniqueActive[0]);
            panneauJeu.repaint();
            
            if (sensUniqueActive[0]) {
                boutonSenseUnique.setText("➜  Sens unique : ON");
                boutonSenseUnique.setForeground(new Color(60, 190, 100));
                boutonSenseUnique.setBorder(
                        BorderFactory.createLineBorder(new Color(60, 190, 100), 2)
                );
            }else {
               boutonSenseUnique.setText("➜  Sens unique : OFF");
               boutonSenseUnique.setForeground(Color.WHITE);
               boutonSenseUnique.setBorder(
                       BorderFactory.createLineBorder(couleurBordure, 1)
               );
            }
        });
        
        // Informations sur les contraintes utilisées dans le parcours
        JLabel obstacles = new JLabel("■  Obstacles");
        obstacles.setForeground(Color.WHITE);
        obstacles.setFont(new java.awt.Font("SansSerif", java.awt.Font.PLAIN, 13));
        obstacles.setAlignmentX(java.awt.Component.LEFT_ALIGNMENT);
        
        this.add(boutonSenseUnique);
        this.add(javax.swing.Box.createVerticalStrut(8));
        this.add(obstacles);

        // Titre de la section contrôles
        JLabel titreControles = new JLabel("CONTRÔLES");
        titreControles.setForeground(new Color(150, 170, 200));
        titreControles.setFont(new java.awt.Font("SansSerif", java.awt.Font.BOLD, 13));
        titreControles.setAlignmentX(java.awt.Component.LEFT_ALIGNMENT);

        this.add(titreControles);
        this.add(javax.swing.Box.createVerticalStrut(12));

        // Boutons principaux de l'interface
        boutonLancer = new javax.swing.JButton("▶  Lancer");
        boutonLancer.addActionListener(e -> {
            // Empêche de lancer plusieurs recherches en même temps
            boutonLancer.setEnabled(false);
            boutonLancerSimultane.setEnabled(false);
            MoteurJeu moteurJeu = panneauJeu.getMoteurJeu();
            // Chaque nouvelle recherche repart du début
            moteurJeu.reinitialiser();
            // Nettoie l'ancienne recherche affichée
            panneauJeu.reinitialiserRecherche();
            // Nettoie les anciens résultats
            fenetre.getPanneauResultats().reinitialiserResultats();
            fenetre.getPanneauResultats().setAlgorithmeActuel(algorithmeSelectionne);
            ResultatRecherche resultat = moteurJeu.obtenirIndice(algorithmeSelectionne);
            panneauJeu.animerRecherche(algorithmeSelectionne, resultat.getOrdreExploration(),
                resultat.getChemin(), resultat, fenetre, () -> {
                    boutonLancer.setEnabled(true);
                    boutonLancerSimultane.setEnabled(true);
                });
        });

        // Lance les 3 algorithmes en même temps, peu importe la sélection ci-dessus
        boutonLancerSimultane = new javax.swing.JButton("▶▶  Lancer les 3 algorithmes");
            boutonLancerSimultane.addActionListener(e -> {
                boutonLancer.setEnabled(false);
                boutonLancerSimultane.setEnabled(false);
    
                MoteurJeu moteurJeu = panneauJeu.getMoteurJeu();
                moteurJeu.reinitialiser();
                panneauJeu.reinitialiserRecherche();
                fenetre.getPanneauResultats().reinitialiserResultats();
    
                Map<MoteurJeu.Algorithme, ResultatRecherche> resultats = moteurJeu.comparerAlgorithmes();
                panneauJeu.animerComparaison(resultats, fenetre, () -> {
                    boutonLancer.setEnabled(true);
                    boutonLancerSimultane.setEnabled(true);
                });
        });

        javax.swing.JButton boutonReinitialiser = new javax.swing.JButton("↻ Réinitialiser");
        boutonReinitialiser.addActionListener(e -> {
            panneauJeu.getMoteurJeu().reinitialiser();
            panneauJeu.reinitialiserRecherche();
            fenetre.getPanneauResultats().reinitialiserResultats();
            // Réactive Lancer même si l'animation a été interrompue
            boutonLancer.setEnabled(true);
            boutonLancerSimultane.setEnabled(true);
        });

        javax.swing.JButton boutonNouveauLabyrinthe = new javax.swing.JButton("⊞ Nouveau");
        boutonNouveauLabyrinthe.addActionListener(e -> {
            // Arrêter et nettoyer l'ancienne recherche
            panneauJeu.getMoteurJeu().reinitialiser();
            panneauJeu.reinitialiserRecherche();
            fenetre.getPanneauResultats().reinitialiserResultats();
            // Générer le nouveau labyrinthe
            panneauJeu.getMoteurJeu().nouveauLabyrinthe();
            panneauJeu.nouveauLabyrinthe();
            // Réactiver Lancer
            boutonLancer.setEnabled(true);
            boutonLancerSimultane.setEnabled(true);
        });

        boutonLancer.setBackground(new Color(30, 110, 210));
        boutonLancer.setForeground(Color.WHITE);
        
        boutonLancerSimultane.setBackground(new Color(30, 110, 210));
        boutonLancerSimultane.setForeground(Color.WHITE);

        boutonReinitialiser.setBackground(new Color(25, 38, 58));
        boutonReinitialiser.setForeground(Color.WHITE);

        boutonNouveauLabyrinthe.setBackground(new Color(25, 38, 58));
        boutonNouveauLabyrinthe.setForeground(Color.WHITE);

        // Taille identique pour les deux boutons
        boutonLancer.setMaximumSize(new java.awt.Dimension(220, 38));
        boutonLancerSimultane.setMaximumSize(new java.awt.Dimension(220, 38));
        boutonReinitialiser.setMaximumSize(new java.awt.Dimension(220, 38));
        boutonNouveauLabyrinthe.setMaximumSize(new java.awt.Dimension(220, 38));

        boutonLancer.setFocusPainted(false);
        boutonLancerSimultane.setFocusPainted(false);
        boutonReinitialiser.setFocusPainted(false);
        boutonNouveauLabyrinthe.setFocusPainted(false);

        boutonLancer.setAlignmentX(java.awt.Component.LEFT_ALIGNMENT);
        boutonLancerSimultane.setAlignmentX(java.awt.Component.LEFT_ALIGNMENT);
        boutonReinitialiser.setAlignmentX(java.awt.Component.LEFT_ALIGNMENT);
        boutonNouveauLabyrinthe.setAlignmentX(java.awt.Component.LEFT_ALIGNMENT);

        this.add(boutonLancer);
        this.add(javax.swing.Box.createVerticalStrut(8));
        this.add(boutonLancerSimultane);
        this.add(javax.swing.Box.createVerticalStrut(8));
        this.add(boutonReinitialiser);
        this.add(javax.swing.Box.createVerticalStrut(8));
        this.add(boutonNouveauLabyrinthe);

        this.add(javax.swing.Box.createVerticalStrut(30));

        // Titre de la légende
        JLabel titreLegende = new JLabel("LÉGENDE");
        titreLegende.setForeground(new Color(150, 170, 200));
        titreLegende.setFont(new java.awt.Font("SansSerif", java.awt.Font.BOLD, 13));
        titreLegende.setAlignmentX(java.awt.Component.LEFT_ALIGNMENT);

        this.add(titreLegende);
        this.add(javax.swing.Box.createVerticalStrut(12));

        // Éléments de la légende
        JLabel depart = new JLabel("■  Départ");
        JLabel arrivee = new JLabel("■  Arrivée");
        JLabel chemin = new JLabel("■  Chemin le plus court");

        depart.setForeground(new Color(60, 190, 100));
        arrivee.setForeground(new Color(220, 70, 70));
        chemin.setForeground(new Color(190, 145, 45));

        depart.setAlignmentX(java.awt.Component.LEFT_ALIGNMENT);
        arrivee.setAlignmentX(java.awt.Component.LEFT_ALIGNMENT);
        chemin.setAlignmentX(java.awt.Component.LEFT_ALIGNMENT);

        this.add(depart);
        this.add(javax.swing.Box.createVerticalStrut(6));
        this.add(arrivee);
        this.add(javax.swing.Box.createVerticalStrut(6));
        this.add(chemin);
    }
}
