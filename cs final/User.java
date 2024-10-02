import javax.swing.*;
import java.util.ArrayList;

public class User {
    private String name;
    private String password;
    ActionController actionController;
    public User(String name, String password) {
        actionController = new ActionController();
        this.name = name;
        this.password = password;
    }
    public User(String name, String password, ActionController actionController) {
        this.name = name;
        this.password = password;
        this.actionController = actionController;
    }
    public String getName() {
        return name;
    }

    public String getPassword() {
        return password;
    }
    public void run(){
        System.out.println("hi");
    }
    public void view(User activeUser){
        System.out.println("=========================");
        if (actionController.getContents().size() == 0) {
            System.out.println("List is empty");
            return;
        }
        System.out.println(activeUser.getName() + "'s Contents:");
        actionController.getContents().forEach(content -> System.out.println("Name: " +content.getName() + " - Rating: " + content.getRating() + "/10"));
    }
}
