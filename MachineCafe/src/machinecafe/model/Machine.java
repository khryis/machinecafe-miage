package machinecafe.model;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import machinecafe.Run;
import machinecafe.singleton.MySQLConnect;
import machinecafe.singleton.SaisieUtilisateur;

public class Machine {
    
    /**
     * Nombre de boisson max que l'on peut mettre dans la machine
     */
    private int nbBoissonsMax;
    
    /**
     * Le stock de chaque ingrédients dans la machine
     */
    private IngredientsMap stocksIngredients;
    
    /**
     * La Map de toute les boissons disponible pour cette machine
     */
    private HashMap<String,Boisson> boissons;
    
    public Machine(int nbBoissonsMax){
        this.nbBoissonsMax = nbBoissonsMax;
        this.stocksIngredients = new IngredientsMap();
        this.boissons = new HashMap<String,Boisson>(nbBoissonsMax);
    }
    
    public Machine(int nbBoissonsMax, IngredientsMap stocksIngredients){
        this.nbBoissonsMax = nbBoissonsMax;
        this.stocksIngredients = stocksIngredients;
        this.boissons = new HashMap<String,Boisson>(nbBoissonsMax);
    }
    
    /**
     * Méthode qui interroge la base de données et qui remplit les attributs 
     * - stocksIngredients
     * - boissons
     * A utiliser de au démarrage de la machine
     * 
     * @throws SQLException 
     */
    public void initIngredientEtBoissonDansMachine() throws SQLException{
        
        Connection conn = MySQLConnect.getDefaultConnection();
        
        // create new connection and statement
        Statement st = conn.createStatement(), stI = conn.createStatement();

        // On va rechercher tout les types d'ingrédients
        // Et on reprend les valeur des stocks en base de données
        String query = "SELECT * FROM ingredients";
        ResultSet rs = st.executeQuery(query);
        while (rs.next()) {
            this.ajouterIngredient(rs.getString("nom"));
            this.stocksIngredients.get(rs.getString("nom")).setValeur(rs.getInt("quantite"));
        }
        
        // On va recherché toute les boissons stockées en base de données
        query = "SELECT * FROM boissons";
        rs = st.executeQuery(query);
        ResultSet rsI;
        Boisson b;
        int idBoisson;
        while(rs.next()){
            b = new Boisson(rs.getString("nom"), rs.getInt("prix"), this);
            idBoisson = rs.getInt("id");
            
            query = "SELECT iB.idBoisson as idBoisson, iB.idIngredient as idIngredient, iB.quantite as quantite, i.nom as nom "
                    + "FROM ingredientsBoisson iB, ingredients i "
                    + "WHERE iB.idIngredient = i.id "
                    + "AND iB.idBoisson = "+idBoisson;
            rsI = stI.executeQuery(query);
            
            while(rsI.next()){
                b.setIngredientValeur(rsI.getInt("quantite"), new Ingredient(rsI.getString("nom")));
            }
            
            this.ajouterBoissonFast(b);
        }
    }
    
    /**
     * Permet à un utilisateur d'ajouter une boisson à la machine
     * La création de cette boisson est reporté dans sur la base de données
     */
    public void ajouterBoisson(){
        // Initialisation
        String newNom;
        Boisson boisson = new Boisson("",0,this);
        
        // Vérifie si on a pas déjà atteind le nombre max de boissons de la machine
        if(this.boissons.size() < this.nbBoissonsMax){
            
            System.out.println("Saisir un nom de boisson : ");
            //string = scan.nextLine();
            newNom = SaisieUtilisateur.SAISIE.nextLine();
            // Vérifie si la boisson existe déjà dans la machine
            if (!this.boissons.containsKey(newNom)){
                boisson.setNom(newNom);
                
                // ajouter prix
                System.out.println("Prix de la boisson : ");
                boisson.setPrix(SaisieUtilisateur.SAISIE.nextIntNotBlank());
                // quantité de tout les ingrédients pour cette boisson
                Ingredient ingredient;
                for (Iterator<Ingredient> it = this.stocksIngredients.values().iterator(); it.hasNext();) {
                    ingredient = it.next();
                    System.out.println("nombre de "+ingredient.getNom()+" : ");
                    boisson.setIngredientValeur(SaisieUtilisateur.SAISIE.nextIntNotBlank(), ingredient);
                }
                
                // On ajouter la boisson à la liste des boisson une fois que tout est bien setté
                this.boissons.put(boisson.getNom(), boisson);
                
                // Vérifie que la boisson a bien été ajouté
                if(this.boissons.containsKey(boisson.getNom())){
                    if (!boisson.insert()){
                        this.boissons.remove(boisson.getNom());
                        System.out.println("Ajout de la boisson annulé");
                    }else{
                        System.out.println("Ajout de la boisson réussi");
                    }
                }else{
                    System.out.println("Ajout échoué");
                }
            }else{
                System.out.println("Cette boisson est déjà présente");
            }
        }else{
            System.out.println("Déjà "+this.nbBoissonsMax+" boissons, c'est la max!!");
        }
    }
     
    /**
     * Permet à un utilisateur d'acheter une boisson
     * On vérifie la monnaie qu'insère l'utilisateur
     * ON vérifie que les quantité en stock pour la boisson a faire sont disponibles
     * Cette méthode appelle une méthode privée interne "faireCafe()"
     */
    public void acheterBoisson(){
        // Sorti prématuré si aucune boisson programmé
        if(this.boissons.isEmpty()){
            System.out.println("Pas de boissons programmées");
        }else{
            //initialisation
            int monnaie;
            
            afficheBoissons();
            // L'utilsateur insère sa monnaie
            System.out.println("Veuillez insérer votre monnaie");
            
            monnaie = SaisieUtilisateur.SAISIE.nextIntNotBlank();
            System.out.println("Crédit : "+monnaie);
            
            // L'utilsateur choisir sa boisson
            Boisson boisson;
            boisson = choixBoissonUtilisateur();
            
            //Pour le choix du sucre
            int qteSucre = boisson.getIngredient("sucre").getValeur(), saisieUtil;
            System.out.println("Actuellement pour le "+boisson.getNom()+" il y a "+boisson.getIngredient("sucre").getValeur()+" sucres");
            System.out.println("Veuillez choisir la quantité de sucre que vous désirez (taper juste entré si garder cette quantité)");
            saisieUtil = SaisieUtilisateur.SAISIE.nextIntWithRange(0, 5);
            if (saisieUtil == -1){
                System.out.println("Pas de modificatinon pour la quantité de sucre");
            }else{
                qteSucre = saisieUtil;
            }
            
            // Vérification quantité de produit en stock pour cette boisson
            // On compare les stocks de chaque ingrédients avec ce que demande une boisson en ingrédients
            // Cas spécial pour le sucre
            boolean manqueStock = false;
            String nom = "";
            Ingredient stockIngredient;
            for (Iterator<Ingredient> it = this.stocksIngredients.values().iterator(); it.hasNext();) {
                stockIngredient = it.next();
                nom = stockIngredient.getNom();
                if (stockIngredient.getNom().equals("sucre")){
                    if (stockIngredient.getValeur() < qteSucre){
                        manqueStock = true;
                        break;
                    }
                }else if (stockIngredient.getValeur() < boisson.getIngredient(nom).getValeur()){
                    manqueStock = true;
                    break;
                }
            }

            if(!manqueStock){
                // On peut demander de payer
                if(monnaie == boisson.getPrix()){
                    faireCafe(boisson);
                }else if(monnaie > boisson.getPrix()){
                    faireCafe(boisson);
                    System.out.println( "...et votre monnaie : "+( monnaie - boisson.getPrix() ) );
                }else{
                    System.out.println("Pas assez de monnaie, Veuillez récupérer votre monnaie : "+monnaie);
                }
            }else{
                System.out.println("Il n'y plus assez d'ingredient ("+nom+") pour cette article");
                System.out.println("Veuillez récupérez votre monnaie : "+monnaie);
            }
        }
    }
    
    /**
     * Permet de décrémenté les quantité en stock pour la boisson que l'on fait
     * Ces modifications sont reportées sur la base de données
     * @param b la boisson à faire
     */
    private void faireCafe(Boisson b){
        System.out.println("Attendez svp...");
        
        // On enleve du stock tout les ingrédients nécessaire pour une boisson
        String nom;
        Ingredient stockIngredient;
        for (Iterator<Ingredient> it = this.stocksIngredients.values().iterator(); it.hasNext();) {
            stockIngredient = it.next();
            nom = stockIngredient.getNom();
            this.stocksIngredients.get(nom).enleverValeurIngredient(b.getIngredient(nom).getValeur());
            // TODO gérer le rollback !!!
            this.stocksIngredients.get(nom).update();
        }
        try {
            Thread.sleep(2000);
        } catch (InterruptedException ex) {
            Logger.getLogger(Run.class.getName()).log(Level.SEVERE, null, ex);
        }
        System.out.println("Récupérer votre boisson");
    }
    
    /**
     * Méthode privée et interne appelé par les méthodes de Machine qui liste les boissons de la machine
     * et demande à l'utilisateur d'en choisir une en lui demandant de saisir son choix
     * Cette méthode fait appelle à la classe SaisieUtilisateur
     * @return la boisson sélectionné par l'utilisateur
     */
    private Boisson choixBoissonUtilisateur(){
        int choix, compteur = 0;
        Boisson boisson;
        List<Boisson> list = new ArrayList<Boisson>(this.nbBoissonsMax);

        //on liste les boissons
        for (Iterator<Boisson> it = this.boissons.values().iterator(); it.hasNext();) {
            boisson = it.next();
            System.out.println("["+compteur+"] "+boisson.getNom());
            list.add(boisson);
            compteur++;
        }
        
        // on demande à l'utilisateur de choisir un numéro de boisson dans la liste affiché en passant par la list intremédiaire
        choix = SaisieUtilisateur.SAISIE.nextIntWithRangeNotBlank(0, list.size()-1);
        
        boisson = list.get(choix);
        System.out.println("Vous avez choisi un "+boisson.getNom());
        return boisson;
    }
    
    /**
     * Méthode similaire à choixBoisson mais pour les ingrédients
     * @return un ingrédients choisi
     */
    private Ingredient choixIngredientUtilisateur(){
        //Initialisation
        int choix, compteur = 0;
        Ingredient ingredient;
        List<Ingredient> list = new ArrayList<Ingredient>(this.nbBoissonsMax);
        
        // Listing des ingrédients
        for (Iterator<Ingredient> it = this.stocksIngredients.values().iterator(); it.hasNext();) {
            ingredient = it.next();
            System.out.println("["+compteur+"] "+ingredient.getNom());
            list.add(ingredient);
            compteur++;
        }
        //choix = s.nextInt();
        choix = SaisieUtilisateur.SAISIE.nextIntWithRangeNotBlank(0, list.size());
        ingredient = list.get(choix);
        System.out.println("Vous avez choisi un "+ingredient.getNom());
        return ingredient;
    }
    
    /**
     * Permet de modifier le prix et la composition d'une boisson de la machine
     * 
     * Ces modifications sont reportés sur la base de données
     */
    public void modifierBoisson(){
        // Sorti prématuré si aucune boisson programmé
        if(this.boissons.isEmpty()){
            System.out.println("Pas de boissons programmées");       
        }else{        
            // Initialisation
            Boisson boisson;
            boisson = choixBoissonUtilisateur();
            Boisson backupBoisson = boisson.clone();
            
            //modification (pas le droit de modifier le nom)
            int saisieUtil;
            System.out.println("Modification de la boisson (entrée sans saisie pour laisser identique)");
            
            // Saisie de l'utilisateur pour le prix de la boisson
            System.out.println("Prix de la boisson (actuel : "+boisson.getPrix()+") : ");
            saisieUtil = SaisieUtilisateur.SAISIE.nextIntBlank();
            // Si ne tape rien on laisse tel quelle et saisieUtil == -1
            if (saisieUtil == -1){
                System.out.println("Pas de modificatinon pour le prix (valeur : "+boisson.getPrix()+")");
            }else{
                boisson.setPrix(saisieUtil);
            }

            // Changement pour tout la liste des ingrédients
            String nom;
            Ingredient stockIngredient;
            for (Iterator<Ingredient> it = this.stocksIngredients.values().iterator(); it.hasNext();) {
                stockIngredient = it.next();
                nom = stockIngredient.getNom();
                System.out.println("nombre de "+nom+" (actuel : "+boisson.getIngredient(nom).getValeur()+") : ");
                // Si l'utilisateur de tape rien on laisse les quantités actuelles
                saisieUtil = SaisieUtilisateur.SAISIE.nextIntBlank();
                if (saisieUtil == -1){
                    System.out.println("Pas de modificatinon pour le cafe (valeur : "+boisson.getIngredient(nom).getValeur() +")");
                }else{
                    boisson.setIngredientValeur(saisieUtil, stockIngredient);
                }
            }
            
            // Rollback si on arrive pas à faire les maj dans la base
            System.out.println();
            if (!boisson.update()){
                System.out.println("Aucune modification faite car conflit dans la Base de données, rollback..");
                this.boissons.remove(boisson.getNom());
                this.boissons.put(backupBoisson.getNom(), backupBoisson);
            }else{
                System.out.println("Toutes les modificatins ont été apporté à cette boisson");
            }
        }    
    }
    
    /**
     * Permet à l'utilisateur de retirer une boisson de la liste des boissons
     * Cette suprression est reportés sur la base de données
     */
    public void supprimerBoisson(){
        // Sorti prématuré si aucune boisson programmé
        if(this.boissons.isEmpty()){
            System.out.println("Pas de boissons programmées");
        }else{
            // Initialisation
            Boisson boisson;
            boisson = choixBoissonUtilisateur();
            
            if (boisson.delete()){
                //suppression de la boisson
                this.boissons.remove(boisson.getNom());
                // message
                System.out.println("La boisson a été enleve du systèmes et de la BD");
            }else{
                System.out.println("La boisson n'a pas pu être retiré du système");
            }    
        }    
    }
    
    /**
     * Permet de renflouer les stocks d'ingrédients de la machine
     * Les stocks sont également changés sur la base de données
     */
    public void ajouterStocksIngredients(){
        // choix de l'ingredient
        int quantite;
        Ingredient ingredient;
        ingredient = choixIngredientUtilisateur();
        
        // quantite à ajouter
        System.out.println("Veuillez ecrire la quantite que vous ajoutez");
        quantite = SaisieUtilisateur.SAISIE.nextIntNotBlank();
        ingredient.ajouterValeurIngredients(quantite);
        
        //Rollback si
        if (!ingredient.update()){
            System.out.println("Annulation car ajout échoué dans la base");
            ingredient.setValeur(-quantite);
        }

        System.out.println("Attente du rechargement, 1 seconde..");
        try {
            Thread.sleep(1000);
        } catch (InterruptedException ex) {
            Logger.getLogger(Run.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        //message
        System.out.println("Vous avez ajoutez "+quantite+" de "+ingredient.getNom());
    }
    
    /**
     * Fonction qui affiche simplement les stocks d'ingrédients de la machine
     */
    public void verifierStock(){
        // liste le stock des stocksIngredients
        Ingredient ingredient;
        System.out.println("Les stocks sont ceux-ci : ");
        for (Iterator<Ingredient> it = this.stocksIngredients.values().iterator(); it.hasNext();) {
            ingredient = it.next();
            System.out.println(ingredient.getNom()+" : "+ingredient.getValeur()); 
        }
    }
    
    /**
     * Ceci permet d'ajouter un nouvel ingrédient dans la machine en renseignant son nom
     * @param nom 
     */
    public void ajouterIngredient(String nom){
        Ingredient i = new Ingredient(nom);
        this.stocksIngredients.put(i.getNom(), i);
    }
    
    /**
     * Méthode qui permet d'ajouter rapidement une boisson sans connaitre sa composition, juste son nom
     * @param b 
     */
    public void ajouterBoissonFast(Boisson b){
        this.boissons.put(b.getNom(), b);
    }
    
    /** Recharge de la même quantité pour tout les ingrédients
     *  Permet d'ajouter la quantités d'ingrédient indiqué en paramètre à tout les ingrésients
     * Ces modifications sont reportés sur la base de données
     * @param val 
     */
    // TODO à voir le rollback
    public void rechargeIngredientGeneral(int val){
        Ingredient ingredient;
        for (Iterator<Ingredient> it = this.stocksIngredients.values().iterator(); it.hasNext();) {
            ingredient = it.next();
            if (ingredient.update()){
                ingredient.ajouterValeurIngredients(val);
            }else{
                System.out.println("Mise à jour des quantité de "+ingredient.getNom()+" a échoué");
            }
        }
    }
    
    /**
     * Retourne un ensemble contenant les nom de tout les ingrédients présent dans la machine
     * @return 
     */
    public TreeSet getIngredientsSet(){
        TreeSet<String> set = new TreeSet<String>(this.stocksIngredients.keySet());
        return set;
    }  
    
    /**
     * Retourne un objet ingrédients sachant son nom
     * @param nom
     * @return 
     */
    public Ingredient getIngredient(String nom){
        return this.stocksIngredients.get(nom);
    }
    
    /**
     * Méthode qui affiche simplement les boissons que possède la machine (avec leur prix)
     */
    public void afficheBoissons(){
        Boisson boisson;
        System.out.println("Aperçu des boissons : ");
        for (Iterator<Boisson> it = this.boissons.values().iterator(); it.hasNext();) {
            boisson = it.next();
            System.out.println(""+boisson.getNom()+" "+boisson.getPrix()+"€");
        }
    }
}
