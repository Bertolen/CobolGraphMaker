package com.bruno.CobolGraphMaker.Panels;

import com.bruno.CobolGraphMaker.Models.Instruction;
import com.bruno.CobolGraphMaker.Models.Paragraph;
import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.view.mxGraph;

import javax.swing.*;
import java.util.ArrayList;

public class StructuredDataPanel extends mxGraphComponent {

    private final int vertexWidth = 180;
    private final int vertexHeight = 30;
    private final int widthSpacing = 10;
    private final int heightSpacing = 30;
    private final int initialX = 20;
    private final int initialY = 20;
    private int currentX = 20;

    private ArrayList<Paragraph> paragraphs;

    public StructuredDataPanel() {
        super(new mxGraph());
    }

    /**
     * Définit la nouvelle structure à afficher
     * @param paragraphs liste des paragraphe à afficher
     */
    public void setStructuredData(ArrayList<Paragraph> paragraphs){

        // sauvegarde des données
        this.paragraphs = paragraphs;

        // Initialisation des variables
        mxGraph g = new mxGraph();
        currentX = initialX;

        // affichage des paragraphes en commençant par le premier
        displayParagraph(paragraphs.get(0), initialX, initialY, g);

        // affichage du graphique obtenu
        this.setGraph(g);
    }

    /**
     * Affichage d'un paragraphe p aux coordonnées X, Y. Fait des appels récursifs pour afficher les paragraphes appelés
     * @param p Paragraphe à appeler
     * @param X Position en ordonnée du paragraphe à afficher
     * @param Y Position en abscisse du paragraphe à afficher
     * @return L'objet nouvellement affiché
     */
    private Object displayParagraph(Paragraph p, int X, int Y, mxGraph g) {
        Object parent = g.getDefaultParent();
        boolean hasChild = false;

        // affiche le paragraphe actuel
        Object vertex = g.insertVertex(parent, null, p.getName(), X, Y, vertexWidth, vertexHeight);

        // recherche des performs faits
        for(Instruction inst : p.getInstructions()) { // boucle sur toutes les instructions
            if(inst.getWords().get(0).equals("PERFORM")) { // si l'instruction est un PERFORM

                // Recherche du paragraphe appelé
                Paragraph p2 = findParagraphByName(inst.getWords().get(1), paragraphs);

                // Si on le trouve
                if(p2 != null){

                    // affichage du paragraphe appelé en dessous
                    Object child = displayParagraph(p2, currentX, Y + heightSpacing + vertexHeight, g);

                    // ajout du lien entre les deux paragraphes
                    g.insertEdge(parent, null, "", vertex, child);
                    hasChild = true;
                }
            }
        }

        // si ce paragraphe ne fait pas d'appel on se décale un peu vers la droite pour l'affichage du prochain
        if(!hasChild) currentX += widthSpacing + vertexWidth;

        // renvoi du vertex qu'on a créé
        return vertex;
    }

    /**
     * Recherche un paragraphe dans une liste à partir de son nom
     * @param name nom du paragraphe à chercher
     * @param paragraphs liste de paragraphe
     * @return l'objet paragraphe si trouvé, null sinon
     */
    private Paragraph findParagraphByName(String name, ArrayList<Paragraph> paragraphs) {
        for(Paragraph p : paragraphs){
            if(p.getName().equals(name)) return p;
        }
        return null;
    }
}
