package com.bruno.CobolGraphMaker.Frames;

import com.bruno.CobolGraphMaker.DataParsing.DataParser;
import com.bruno.CobolGraphMaker.Panels.FileSelectPanel;
import com.bruno.CobolGraphMaker.Panels.StructuredDataPanel;

import javax.swing.*;

public class MainWindow extends JFrame {
    /**
     * @author BGIRON
     * Public Constructor
     */
    public MainWindow() {
        // Titre de la fenêtre
        this.setTitle("Cobol Graph Maker");
        // Taille de la fenêtre
        this.setSize(800,800);
        // Position de la fenêtre (null = centrée)
        this.setLocationRelativeTo(null);
        // Termine le processus quand on clique sur la croix rouge
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Création des panneaux
        FileSelectPanel filesSelectionPanel = new FileSelectPanel();
        StructuredDataPanel dataPanel = new StructuredDataPanel();

        // Ajout de panneaux à la fenêtre
        this.getContentPane().setLayout(new BoxLayout(this.getContentPane(), BoxLayout.PAGE_AXIS));
        this.getContentPane().add(filesSelectionPanel);
        this.getContentPane().add(dataPanel);

        // Création du contrôle
        DataParser dataParser = new DataParser();

        // Connection entre les composants
        filesSelectionPanel.setDataParser(dataParser);
        dataParser.setDataPanel(dataPanel);

        // Affichage de la fenêtre
        this.setVisible(true);
    }
}
