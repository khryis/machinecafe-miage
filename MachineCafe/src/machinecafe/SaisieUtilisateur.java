/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package machinecafe;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Chris
 */
public class SaisieUtilisateur {
    public final static SaisieUtilisateur SAISIE = new SaisieUtilisateur();
    private Scanner scan;
    private BufferedReader buffer;
    
    private SaisieUtilisateur(){
        this.scan = new Scanner(System.in);
        this.buffer = new BufferedReader (new InputStreamReader (System.in)); 
    }
    
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
     * Tant que l'on a pas saisie un bon format d'integer on recommence la saisie ou alors si on ne tape rien,
     * on sort de la méthode
     * 
     * @return -1 Si l'utilisateur n'entre rien
     * @throws NumberFormatException 
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
