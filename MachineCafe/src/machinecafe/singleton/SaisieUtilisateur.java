package machinecafe.singleton;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Classe sur le pattern singleton, on crée un seul objet SAISIE que l'on appelle à chaque fois pour
 * demandé des saisie à l'utilisateur.
 * Ces demandes de saisies sont vérifié et les erreurs sont toutes géré dans la classe
 * Les méthodes doivent en principes ne retourner que des valeurs attendues
 */
public class SaisieUtilisateur {
    public final static SaisieUtilisateur SAISIE = new SaisieUtilisateur();
    private Scanner scan;
    private BufferedReader buffer;
    
    private SaisieUtilisateur(){
        this.scan = new Scanner(System.in);
        this.buffer = new BufferedReader (new InputStreamReader (System.in)); 
    }
    
    /**
     * Méthode qui demande à l'utilisateur de saisie un entier
     * Elle ne tolère pas que l'utilisateur ne fasse aucune saisie
     * Ni que celui-ci saisissent un entier négatif
     * @return un entier supérieur ou égal à zéro 
     */
    public int nextIntNotBlank(){
        boolean correct = false;
        int valeur = 0;
        String line;
        while(!correct){
            try {
                line = this.buffer.readLine();
                while (line.isEmpty()){
                    System.out.println("Allez.. Un petit effort, écrivez quelque chose !");
                    line = this.buffer.readLine();   
                }
                valeur = Integer.valueOf(line);
                if (valeur < 0){
                    throw new NumberFormatException();
                }else{
                    correct = true;
                }
            } catch (NumberFormatException numE){
                correct = false;
                valeur = 0;
                System.out.println("Mauvaise saisie, retentez votre chance");
            } catch (IOException ex) {
                Logger.getLogger(SaisieUtilisateur.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return valeur;
    }
    
    /**
     * Méthode qui demande à l'utilisateur de saisie un entier
     * On tolère que l'utilisateur ne saisisse aucune valeur
     * Mais pas que celui-ci saisissent un entier négatif
     * 
     * @return -1 Si l'utilisateur n'entre rien et sinon entier supérieur ou égal à 0
     */
    public int nextIntBlank(){
        boolean correct = false;
        int valeur = -1;
        String line;
        while (!correct){
            try {
                line = this.buffer.readLine();
                if(line.isEmpty()){
                    valeur = -1;
                    correct = true;
                }else{
                    valeur = Integer.valueOf(line);
                    if (valeur < 0){
                        throw new NumberFormatException();
                    }else{
                        correct = true;
                    }
                }    
            } catch (NumberFormatException numE){
                correct = false;
                System.out.println("Mauvaise saisie, retentez votre chance");
            } catch (IOException ex) {
                Logger.getLogger(SaisieUtilisateur.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return valeur;
    }
    
    /**
     * Méthode qui demande à l'utilisateur de saisir un entier compris en min et max
     * Elle ne tolère pas que l'utilisateur ne fasse aucune saisie
     * 
     * @param min la valeur minimum que l'utilisateur peut entrer
     * @param max la valeur max que l'utilisateur peut entrer
     * @return le valeur qu'à saisie l'utilsateur entre min et max
     */
    public int nextIntWithRangeNotBlank(int min, int max){
        int valeur = -1;
        boolean correct = false;
        String line;
        while(!correct){
            try {
                line = this.buffer.readLine();
                if (!line.isEmpty()){
                    valeur = Integer.valueOf(line);
                    if ((valeur >= min)&&(valeur <= max)){
                        correct = true;
                    }else{
                        correct = false;
                        System.out.println("La valeur doit-être comprise entre "+min+" et "+max);
                    }
                }else{
                    valeur = -1;
                    correct = false;
                    System.out.println("Allez.. Un petit effort, écrivez quelque chose !");
                }
            } catch (NumberFormatException numE){
                    correct = false;
                    System.out.println("Mauvaise saisie, retentez votre chance");    
            } catch (IOException ex) {
                Logger.getLogger(SaisieUtilisateur.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return valeur;
    }
    
    /**
     * Méthode qui demande à l'utilisateur de saisir un entier compris en min et max
     * Elle tolère que l'utilisateur de fasse aucune saisi
     * 
     * @param min la valeur minimum que l'utilisateur peut entrer
     * @param max la valeur max que l'utilisateur peut entrer
     * @return -1 si l'utilisateur ne saisie rien sinon la valeur qu'il a tapé compris entre min et max
     */
    public int nextIntWithRange(int min, int max){
        int valeur = -1;
        boolean correct = false;
        String line;
        while(!correct){
            try {
                line = this.buffer.readLine();
                if (!line.isEmpty()){
                    valeur = Integer.valueOf(line);
                    if ((valeur >= min)&&(valeur <= max)){
                        correct = true;
                    }else{
                        correct = false;
                        System.out.println("La valeur doit-être comprise entre "+min+" et "+max);
                    }
                }else{
                    valeur = -1;
                    correct = true;
                    System.out.println("Vous avez laissé le même nombre de sucre");
                }
            } catch (NumberFormatException numE){
                    correct = false;
                    System.out.println("Mauvaise saisie, retentez votre chance");    
            } catch (IOException ex) {
                Logger.getLogger(SaisieUtilisateur.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return valeur;
    }
    
    /**
     * Demande à l'utilisateur une saisie quelconque
     * Elle ne tolère pas qu'il ne saisissent rien
     * 
     * @return la ligne qu'à saisie l'utilisateur
     */
    public String nextLine(){
        String line = "";
        try {
            line = this.buffer.readLine ();
            while (line.isEmpty()){
                System.out.println("Allez.. Un petit effort, écrivez quelque chose !");
                line = this.buffer.readLine();   
            }
        } catch (IOException ex) {
            Logger.getLogger(SaisieUtilisateur.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return line;
    }
}
