package com.bruno.CobolGraphMaker.DataParsing;

import com.bruno.CobolGraphMaker.Models.Instruction;
import com.bruno.CobolGraphMaker.Models.Paragraph;
import com.bruno.CobolGraphMaker.Panels.StructuredDataPanel;

import java.io.*;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class DataParser {

    private StructuredDataPanel dataPanel = null;
    private File file = null;
    private Instruction instruction = null;
    private DivisionTypeEnum divType = DivisionTypeEnum.OTHER;
    private ArrayList<Paragraph> paragraphs = new ArrayList<>();
    private static final List<String> instructionKeywordList = List.of(
            "ADD","SUBTRACT","MULTIPLY","DIVIDE","COMPUTE",
            "IF","ELSE","END-IF","EVALUATE","CASE","END-EVALUATE",
            "PERFORM","END-PERFORM","CALL","END-CALL",
            "STOP","GOBACK",
            "MOVE","SET","CONTINUE","NEXT",
            "EXEC","END-EXEC",
            "CLOSE","OPEN","READ","END-READ","WRITE",
            "DISPLAY",
            "STRING","END-STRING","UNSTRING","END-UNSTRING",
            "SEARCH","WHEN","END-SEARCH"
    );

    /**
     * Indique le panneau de données pour afficher le résultat
     * @param dataPanel panneau de données qui va afficher le résultat
     */
    public void setDataPanel(StructuredDataPanel dataPanel) {
        this.dataPanel = dataPanel;
    }

    /**
     * Met à jour le chemin du fichier à interpréter
     * @param file chemin du fichier à interpréter
     */
    public void setFile(File file) {

        // interprétation des données
        if(file.isDirectory()){
            for(String f : Objects.requireNonNull(file.list())){
                this.file = new File(file.toString() + '\\' + f);
                parseData();
            }
        }else {
            this.file = file;
            parseData();
        }

    }

    /**
     * Lis la copy et le fichier de données pour déterminer les valeurs du fichier de données
     */
    public void parseData(){

        try {
            // Ouverture du fichier de données
            BufferedReader dataReader = new BufferedReader(new InputStreamReader(new FileInputStream(file)));

            // initiatlisation des variables
            Paragraph paragraphe = null;

            // Boucle sur chaque instruction
            Instruction instruction = getNextInstruction(dataReader);
            while (instruction != null){

                // récupération des mots de l'instruction
                ArrayList<String> words = instruction.getWords();

                // on vérifie si la ligne est une déclaration de division
                if(words.size() == 2 && words.get(1).equals("DIVISION.")){
                    // Le cas échéant, on met à jour divType
                    switch (words.get(0)){
                        case "DATA":
                            divType = DivisionTypeEnum.DATA;
                            break;

                        case "PROCEDURE":
                            divType = DivisionTypeEnum.PROCEDURE;
                            break;

                        default:
                            divType = DivisionTypeEnum.OTHER;
                            break;
                    }
                }

                // Quand on est en Procedure Division
                if(divType == DivisionTypeEnum.PROCEDURE){
                    // création de nouveau paragraphe
                    if(instruction.isColumnA()){
                        if(words.size() == 1) {
                            String name = instruction.getText().trim();
                            name = name.substring(0, name.length() - 1);
                            paragraphe = new Paragraph(name);
                            paragraphs.add(paragraphe);
                        }
                    } else {
                        assert paragraphe != null;
                        paragraphe.getInstructions().add(instruction);
                    }
                }

                // lecture de la prochaine instruction
                instruction = getNextInstruction(dataReader);
            }

            // fermeture du fichier
            dataReader.close();

        } catch (FileNotFoundException e) {
            // TODO envoyer un message d'erreur
            e.printStackTrace();
        } catch (IOException e) {
            // TODO envoyer un message d'erreur
            e.printStackTrace();
        }

        // envoi des données au panneau de données pour affichage
        dataPanel.setStructuredData(paragraphs);
    }

    /**
     * Récupère la prochaine instruction cobol d'un fichier cobol
     * @param reader lecteur du fichier cobol
     * @return la prochaine instruction cobol
     * @throws IOException en cas d'erreur de lecture
     */
    private Instruction getNextInstruction(BufferedReader reader) throws IOException {

        // Lecture de la prochaine ligne cobol
        String cobolLine = getNextCobolLine(reader);
        ArrayList<String> words = null;

        // boucle sur les lignes cobol tant que l'instruction n'est pas terminée et qu'on a encore des lignes à lire
        while (cobolLine != null) {

            // découpe de la ligne cobol en mots
            words = new ArrayList<>(List.of(cobolLine.split(" ")));
            while(words.remove("")) {continue;} // on retire tous les string vides

            // si la ligne contient une fin d'instruction, on renvoi l'instruction qu'on a déjà et on garde en mémoire ce début d'instruction
            if(instruction != null && words.size() > 0 && finInstruction(words)) {
                String inst = instruction.toString();
                instruction = new Instruction(cobolLine + ' ');
                return instruction;
            } else {
                // sinon, ajoute la ligne à l'instruction
                if(instruction == null) {
                    instruction = new Instruction(cobolLine);
                } else {
                    instruction.append(cobolLine);
                }
            }

            // si on est ici c'est qu'on a terminé de lire la ligne cobol et que l'instruction n'est pas terminée
            // Alors on lis la prochaine instruction
            cobolLine = getNextCobolLine(reader);
            // et on ajoute un espace pour représenter l'aller à la ligne
            instruction.append(" ");
        }

        // si on arrive ici c'est qu'on a terminé le fichier

        // si on a une instruction en cours
        if(instruction != null){

            // découpe de l'instruction en mots
            if(words == null) {
                // découpe de la ligne cobol en mots
                words = instruction.getWords();
            }

            // Si l'instruction se termine (normalement il doit y avoir un point à la fin) alors on la renvoie
            if(words.size() > 0 && finInstruction(words)) {
                Instruction inst = instruction.clone();
                instruction = null;
                return inst;
            }
            else { // si l'instruction ne se termine pas alors le programme est mal foutu
                // TODO : renvoyer une erreur
                System.out.println("ERREUR : on a une instruction mal foutue dans le fichier " + file.getName() + " : " + instruction.getText());
                return null;
            }
        }

        // fin normale, on n'a plus d'instruction
        return null;
    }

    /**
     * Fonction qui détermine si on a atteint la fin de l'instruction
     * @param words tableau de string qui contient tous les mots de la ligne cobol en cours de lecture
     * @return true si l'instruction est terminée, false autrement
     */
    protected boolean finInstruction(List<String> words) {
        boolean fi = false;

        // vérification si ce mot est une instruction
        for(String w : words) {
            if (instructionKeywordList.contains(w)) {
                fi = true;
                break;
            }
        }

        // Les points sont toujours des fins d'instruction
        String lastWord = words.get(words.size() - 1);
        char lastChar = lastWord.charAt(lastWord.length() - 1);
        if(lastChar == '.') fi = true;

        return fi;
    }

    /**
     * Lis un fichier cobol en entrée
     * @param reader lecteur du fichier cobol en entrée
     * @return prochaine ligne d'instructions utiles
     * @throws IOException en cas de lecture
     */
    private String getNextCobolLine(BufferedReader reader) throws IOException {


        // Lecture de la prochaine ligne
        String copyLine = reader.readLine();

        // condition d'arrêt : Si la ligne lue est vide, alors on renvoie l'instruction en cours
        if (copyLine == null) return null;

        while (copyLine.length() <= 6 // on ignore les lignes trop courtes
                || copyLine.charAt(6) == '*' // on ignore les lignes de commentaire
                || copyLine.charAt(6) == '%' // ça, je ne sais pas ce que c'est, mais on ignore aussi
                || copyLine.substring(7).equals(" ") // on ignore les lignes vides
        ) {
            copyLine = reader.readLine();

            // condition d'arrêt : Si la ligne lue est vide, alors on renvoie l'instruction en cours
            if (copyLine == null) return null;
        }

        // on conserve uniquement les informations utiles et on retire les espaces en trop
        return copyLine.substring(7,Math.min(copyLine.length(),72));
    }
}
