package com.bruno.CobolGraphMaker.Models;

import lombok.Data;

import java.util.ArrayList;

/**
 * Objet représentant un paragraphe, contient des instructions et un nom
 */
@Data
public class Paragraph {
    private String name;
    private ArrayList<Instruction> instructions;
    private int nbCalls;

    /**
     * Constructeur de base
     * @param name nom du paragraphe
     */
    public Paragraph(String name){
        this.name = name;
        instructions = new ArrayList<>();
    }

    public boolean isNamed(String n){
        if(n.endsWith(".") && !this.name.endsWith("."))
            return (this.name + ".").equals(n);

        if(!n.endsWith(".") && this.name.endsWith("."))
            return this.name.equals(n + ".");

        return this.name.equals(n);
    }
}
