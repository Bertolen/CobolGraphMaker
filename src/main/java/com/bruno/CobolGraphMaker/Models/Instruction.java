package com.bruno.CobolGraphMaker.Models;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class Instruction implements Cloneable{
    private boolean columnA;
    private String text;
    private ArrayList<String> words;

    /**
     * Constructeur. Prends au minimum un texte en entrée
     * @param text texte de l'instruction
     */
    public Instruction(String text) {
        setText(text);
    }

    public void setText(String text){
        this.text = text.toUpperCase(); // passage en majuscules

        columnA = this.text.charAt(0) != ' '; // on détermine si c'est une instruction de colonne A ou pas

        words = new ArrayList<>(List.of(this.text.split(" "))); // découpe de l'instruction en mots.
        while(words.remove("")) {continue;} // on retire tous les string vides
    }

    /**
     * Ajoute un nouveau morceau d'instruction à la fin de l'instruction déjà présente.
     * @param appendedText nouveau morceau d'instruction à ajouter
     */
    public void append(String appendedText){
        this.text = this.text + appendedText.toUpperCase();

        ArrayList<String> newWords = new ArrayList<>(List.of(appendedText.toUpperCase().split(" ")));// découpe du nouveau bout de l'instruction en mots.
        while(newWords.remove("")) {continue;} // on retire tous les string vides
        words.addAll(newWords); // ajout des nouveaux mots à montre liste
    }

    public Instruction clone(){
        try {
            return (Instruction) super.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        return null;
    }
}
