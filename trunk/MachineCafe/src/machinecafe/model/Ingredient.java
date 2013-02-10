/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package machinecafe.model;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;
import machinecafe.singleton.MySQLConnect;

public class Ingredient{
    
    private String nom;
    
    private int valeur;
    
    public Ingredient(){
        this.nom = null;
        this.valeur = 0;
    }
    
    public Ingredient(String nom){
        this.nom = nom;
        this.valeur = 0;
    }
    
    public Ingredient(String nom, int valeur){
        this.nom = nom;
        this.valeur = valeur;
    }

    public void ajouterValeurIngredients(int quantite){
        this.valeur += quantite;
    }
    
    public void enleverValeurIngredient(int quantite){
        this.valeur -= quantite;
    }
    
    public String getNom() {
        return nom;
    }
    
    public int getValeur(){
        return this.valeur;
    }
    
    public void setValeur(int valeur){
        this.valeur = valeur;
    }
    
    /**
     * Permet d'insérer un objet Ingredient dans la base de données
     * @return vrai si l'insertion s'est bien passé, sinon false
     */
    public boolean insert(){
        boolean works;
        try {
            Connection conn = MySQLConnect.getDefaultConnection();
            Statement st = conn.createStatement();
            
            String query = "INSERT INTO ingredients(nom,quantite) " + "VALUES ('" + this.nom + "', " + this.valeur + ")";
            int nb = st.executeUpdate(query);
            
            System.out.println(nb + " ligne(s) insérée(s) dans boisson");
            st.close();  
            works = true;
        } catch (SQLException ex) {
            Logger.getLogger(Boisson.class.getName()).log(Level.SEVERE, null, ex);
            works = false;
        }
        return works;
    }
    
    /**
     * Permet de mettre à jour un objet Ingredient dans la base de données
     * @return vrai si la modificatin s'est bien passé, sinon false
     */
    public boolean update(){
        boolean works;
        try {
            Connection conn = MySQLConnect.getDefaultConnection();
            Statement st = conn.createStatement();
            
            //On metà jour le prix de la boisson dans la base
            String query = "UPDATE ingredients SET quantite = '"+this.valeur+"' "
                    + "WHERE nom = '"+this.nom+"'";
            int nb = st.executeUpdate(query);
            
            st.close();  
            works = true;
        } catch (SQLException ex) {
            Logger.getLogger(Boisson.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println("Insertion dans la base de données corrompue...");
            works = false;
        }
        return works;
    }
    
    /**
     * Permet de supprimer un objet Ingredient dans la base de données
     * @return vrai si la suppression s'est bien passé, sinon false
     */
    public boolean delete(){
        boolean works;
        try {
            Connection conn = MySQLConnect.getDefaultConnection();
            Statement st = conn.createStatement();
            
            String query = "SELECT id FROM ingredients WHERE nom ='" + this.nom + "'";
            ResultSet rs = st.executeQuery(query);
            rs.next();
            int idIngredient = rs.getInt("id");
            
            query = "DELETE FROM ingredients WHERE id ='" + idIngredient + "'";
            int nb = st.executeUpdate(query);

            System.out.println(nb + " ligne(s) supprimée(s) dans la table boissons");
            st.close();  
            works = true;
        } catch (SQLException ex) {
            works = false;
            Logger.getLogger(Boisson.class.getName()).log(Level.SEVERE, null, ex);
        }
        return works;
    }
}
