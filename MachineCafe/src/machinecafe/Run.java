package machinecafe;

import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Run {
    
    // TODO envoyer à operrin@loria.fr et sujet : [M1ACSI]noms
    
    public static void main(String[] args){
        
        // Machine pouvant faire 5 boissons
        Machine machine = new Machine(3);
        
        // On va chercher dans la base de données les différentes boissons  et ingrésients gardé en mémoire
        // quand on allume la machine
        try {
            machine.initIngredientEtBoissonDansMachine();
        } catch (SQLException ex) {
            Logger.getLogger(Run.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println("C'est le bordel !!");
        }
        
        int choix = 0;
        
        while(choix != -1){
            System.out.println("Bonjour, veuillez faire un choix");
            System.out.println("[1] Acheter une boisson");
            System.out.println("[2] Ajouter une boisson");
            System.out.println("[3] Modifier une boisson");
            System.out.println("[4] Supprimer une boisson");
            System.out.println("[5] Ajouter ingrédient");
            System.out.println("[6] Vérifier Stocks");
            System.out.println("[7] Eteindre la machine");
            //choix  = s.nextInt();
            choix  = SaisieUtilisateur.SAISIE.nextIntNotBlank();
            switch(choix){
                case 1:
                    machine.acheterBoisson();
                    break;
                case 2:
                    machine.ajouterBoisson();
                    break;
                case 3:
                    machine.modifierBoisson();
                    break;
                case 4:
                    machine.supprimerBoisson();
                    break;
                case 5:
                    machine.ajouterStocksIngredients();
                    break;
                case 6:
                    machine.verifierStock();
                    break;
                case 7:
                    choix = -1;
                    break;    
                default:
                    choix = 0;
                    System.out.println("Vous avez décidé de ne rien taper ou n'importe quoi");
                    break;
            }
            if (choix != -1){
                System.out.println("Machine disponible dans 1 secondes, wait please...");
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException ex) {
                    Logger.getLogger(Run.class.getName()).log(Level.SEVERE, null, ex);
                }
                System.out.println();System.out.println();
            }    
        }
    }
}
