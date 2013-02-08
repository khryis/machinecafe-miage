/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package machinecafe;

import java.sql.*;
import java.util.Iterator;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Boisson implements Cloneable{
    
    /**
     * Nom de la boisson
     */
    private String nom;
    
    /**
     * HashMap qui associe un nom(clé) à un objet Ingredient(value)
     */
    private IngredientsMap quantitesIngredients;
    
    /**
     * Prix de la boisson
     */
    private int prix;
    
    private Machine machine;
    
    /**
     * Permet de constructruire un objet boisson, on doit obligaoirement renseigné un nom, un prix
     * et la machine pour laquelle est créé la boisson
     * 
     * @param nom nom de la boisson
     * @param prix prix de la boisson
     * @param m objet Machine pour laquelle la boisson est crée
     */
    public Boisson(String nom, int prix, Machine m){
        this.nom = nom;
        this.prix = prix;
        this.machine = m;
        this.quantitesIngredients = new IngredientsMap();
        initIngredientsPossible(m);
    }
    
    /**
     * Permet d'initialisé la liste des ingrédients possible pour une boisson dans une certaine machine
     * 
     * @param m Machine pour laquelle cette boisson est crée     
     */
    @SuppressWarnings("unchecked")
    private void initIngredientsPossible(Machine m){
        TreeSet<String> set = m.getIngredientsSet();
        String nomIngredient;
        for (Iterator<String> it = set.iterator(); it.hasNext();) {
            nomIngredient = it.next();
            this.quantitesIngredients.ajouterIngredients(nomIngredient);
        }
    }
    
    public String getNom() {
        return nom;
    }

    public int getPrix() {
        return prix;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public void setPrix(int prix) {
        this.prix = prix;
    }
    
    /**
     * Permet de changer la quantité à utuliser d'un certain ingrédient pour cette boisson
     * 
     * @param val la quantité d'ingrédient pour cette boisson
     * @param i l'ingrédient pour lequelle on veut changer la quantité à utilisé pour cette boisson
     */
    public void setIngredientValeur(int val, Ingredient i){
        Ingredient ingredient = this.quantitesIngredients.get(i.getNom());
        ingredient.setValeur(val);
    }
    
    /**
     * Ici on récupère simplement l'ensemble des noms d'ingrédients pour une boisson
     * 
     * @return un ensemble arbre
     */
    public TreeSet getIngredientsSet(){
        TreeSet<String> set = new TreeSet<String>(this.quantitesIngredients.keySet());
        return set;
    } 
    
    /**
     * On récupère un ingrédient par sont nom (clé de la HashMap quantitesIngredients)
     * 
     * @param nom d'un ingrédient
     * @return l'ingrédient correpondant au nom donné
     */
    public Ingredient getIngredient(String nom){
        return this.quantitesIngredients.get(nom);
    }
    
    public static void listBoissons() throws SQLException{
        Connection conn = MySQLConnect.getDefaultConnection();
        // create new connection and statement
        Statement st = conn.createStatement();

        String query = "SELECT * FROM boissons";
        ResultSet rs = st.executeQuery(query);

        while (rs.next()) {
            System.out.printf("%-20s | %-20s | %3d\n", //
                    rs.getInt(1), rs.getString("nom"), rs.getInt(3));
        }   
    }
    
    // TODO l'ordre des insertion
    public boolean insert(){
        boolean works;
        try {
            Connection conn = MySQLConnect.getDefaultConnection();
            Statement st = conn.createStatement();
            Statement st2 = conn.createStatement();
            String query = "INSERT INTO boissons(nom,prix) " + "VALUES ('" + this.nom + "', " + this.prix + ")";
            int nb = st.executeUpdate(query), nb2 = 0;
            
            query = "SELECT id FROM boissons WHERE nom = '"+this.nom+"'";
            ResultSet rs = st.executeQuery(query);
            rs.next();
            int idBoisson = rs.getInt("id");
            
            query = "SELECT id, nom FROM ingredients";
            rs = st.executeQuery(query);
            int quantite;
            while(rs.next()){
                if (this.quantitesIngredients.get(rs.getString("nom")).getValeur() != 0){
                    quantite = this.quantitesIngredients.get(rs.getString("nom")).getValeur();
                    query = "INSERT INTO ingredientsBoisson(idBoisson,idIngredient,quantite) "
                            + "VALUES ("+idBoisson+", "+rs.getInt("id")+", "+quantite+")";
                    nb2 = st2.executeUpdate(query);
                }
            }
            
            System.out.println(nb + " ligne(s) insérée(s) dans boisson");
            System.out.println(nb2 + " ligne(s) insérée(s) dans ingredientsBoisson");
            st.close();  
            works = true;
        } catch (SQLException ex) {
            Logger.getLogger(Boisson.class.getName()).log(Level.SEVERE, null, ex);
            works = false;
        }
        return works;
    }
    
    
    public boolean update(){
        boolean works;
        try {
            Connection conn = MySQLConnect.getDefaultConnection();
            Statement st = conn.createStatement();
            
            //On metà jour le prix de la boisson dans la base
            String query = "UPDATE boissons SET prix = '"+this.prix+"' "
                    + "WHERE nom = '"+this.nom+"'";
            int nb = st.executeUpdate(query);
            
            //On récupère l'id de la boisson dans la base
            query = "SELECT id FROM boissons WHERE nom='"+this.nom+"'";
            ResultSet rs = st.executeQuery(query);
            int idBoisson = -1, idIngredient = -1;
            while (rs.next()) {
                idBoisson = rs.getInt("id");
            }  
            
            // Query de mise à jour de la quantité pour un ingrédient, patron de Query
            PreparedStatement pst = conn.prepareStatement(
                "UPDATE ingredientsBoisson SET quantite = ? " +
                "WHERE idBoisson = " + idBoisson + " AND idIngredient = ? "
            );

            // On boucle sur tous les ingrédients pour remettre à jour les quantité
            for(String nomIngredient:this.quantitesIngredients.keySet()) {
                //on ajoute la bonne quantité dans le prepareStatement
                pst.setInt(1, this.quantitesIngredients.get(nomIngredient).getValeur());
                
                // on récupère l'id de l'ingrédient dans la base
                query = "SELECT id FROM ingredients WHERE nom='"+this.quantitesIngredients.get(nomIngredient).getNom()+"'";
                rs = st.executeQuery(query);
                while (rs.next()) {
                    idIngredient = rs.getInt("id");
                }
                
                // on set cette id d'ingrédient dans le prépare statement puis on execute la query
                pst.setInt(2, idIngredient);
                pst.execute();
            }
            
            st.close();  
            pst.close();
            works = true;
        } catch (SQLException ex) {
            Logger.getLogger(Boisson.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println("Insertion dans la base de données corrompue...");
            works = false;
        }
        return works;
    }
    
    public boolean delete(){
        boolean works;
        try {
            Connection conn = MySQLConnect.getDefaultConnection();
            Statement st = conn.createStatement();
            
            String query = "SELECT id FROM boissons WHERE nom ='" + this.nom + "'";
            ResultSet rs = st.executeQuery(query);
            rs.next();
            int idBoisson = rs.getInt("id");
            
            query = "DELETE FROM ingredientsBoisson WHERE idBoisson ='" + idBoisson + "'";
            int nb2 = st.executeUpdate(query);
            
            query = "DELETE FROM boissons WHERE id ='" + idBoisson + "'";
            int nb = st.executeUpdate(query);
            
            System.out.println(nb + " ligne(s) supprimée(s) dans la table boissons");
            System.out.println(nb2 + " ligne(s) supprimée(s) dans la table ingredientsBoisson");
            st.close();  
            works = true;
        } catch (SQLException ex) {
            works = false;
            Logger.getLogger(Boisson.class.getName()).log(Level.SEVERE, null, ex);
        }
        return works;
    }
    
    @Override
    public Boisson clone(){
        return new Boisson(this.getNom(), this.getPrix(), this.machine);
    }
}
