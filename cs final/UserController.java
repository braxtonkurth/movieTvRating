import java.util.ArrayList;

public class UserController {
    private ArrayList<User> users = new ArrayList<>();
    private User activeUser;
    public void addUser(User user){
        users.add(user);
    }
    public void setActiveUser(int index){
        activeUser = users.get(index - 1);
    }
    public User getActiveUser(){
        return activeUser;
    }
    public int size(){
        return users.size();
    }
    public ArrayList<User> getUsers(){
        return users;
    }
    public void setUsers(ArrayList<User> users){
        this.users = users;
    }
}
