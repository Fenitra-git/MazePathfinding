/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package vue;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import modele.Labyrinthe;
import modele.MoteurJeu;
import modele.ResultatRecherche;

/**
 *
 * @author User
 */
public class frame extends JFrame {

    private JButton startButton;
    private JLabel label;

    public frame() {
        setTitle("Find the path");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setResizable(false);
        this.setLayout(new BorderLayout());

        //Texte en haut
        label = new JLabel("<html><div style='text-align: center;'>" + "Recherche le chemin plus court <br>" + "Comparaison des 3 algorithmes"
                + "</div></html>", SwingConstants.CENTER);
        label.setFont(new Font("Ariel", Font.BOLD, 28));
        this.add(label, BorderLayout.NORTH);

        JPanel panel = new JPanel();
        panel.setLayout(new GridBagLayout());

        startButton = new JButton("START");
        startButton.setFont(new Font("Arial", Font.BOLD, 25));
        startButton.setPreferredSize(new Dimension(200, 60));

        panel.add(startButton);
        this.add(panel, BorderLayout.CENTER);

        //action
        startButton.addActionListener(e -> {
            this.dispose();
            Labyrinthe labyrinthe = new Labyrinthe();
            MoteurJeu moteurJeu = new MoteurJeu(labyrinthe);

            FenetreJeu fenetre = new FenetreJeu(moteurJeu);
            PanneauJeu panneauJeu = fenetre.getPanneauJeu();

            ResultatRecherche resultat = moteurJeu.obtenirIndice();
            
            //Animation de la recherche
            panneauJeu.animerRecherche(MoteurJeu.Algorithme.DIJKSTRA, resultat.getOrdreExploration(), resultat.getChemin(), resultat, fenetre, null);
        });

        this.setSize(615, 550);
        this.setLocationRelativeTo(null);
        this.setVisible(true);

    }
}
