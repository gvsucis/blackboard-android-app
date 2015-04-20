package edu.gvsu.bbmobile.MyBB.actionbarmenu;

/**
 * Created by romeroj on 10/11/13.
 */
public class ActionBarMenuItem {

    private String id;
    private String label;

    public ActionBarMenuItem(){

    }

    public static ActionBarMenuItem create(String id, String label){
        ActionBarMenuItem barMenu = new ActionBarMenuItem();
        barMenu.setId(id);
        barMenu.setLabel(label);
        return barMenu;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    @Override
    public String toString(){
        return label;
    }
}
