package machinecafe;

import java.util.HashMap;

public class IngredientsMap extends HashMap<String,Ingredient>{
    private static final long serialVersionUID = 1L;
    
    public void ajouterIngredients(String nom){
       Ingredient i = new Ingredient(nom);
       this.put(nom, i);
   } 
}
