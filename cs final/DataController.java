import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

public class DataController {

    public static void load(UserController userController) throws IOException {
        String fileName = "save.txt";
        File file = new File(fileName);
        file.createNewFile();
        Scanner reader = new Scanner(file);
        while (reader.hasNextLine()) {
            String line = reader.nextLine();
            String[] parts = line.split(",");
            String name = parts[0];
            String password = parts[1];
            ActionController actionController = new ActionController();
            ArrayList<Content> contents = new ArrayList<>();
            for (int i = 2; i < parts.length; i+=2) {
                String contentName = parts[i];
                double contentRating = Double.parseDouble(parts[i+1]);
                contents.add(new Content(contentName, contentRating));
            }
            actionController.addAll(contents);
            userController.addUser(new User(name, password, actionController));
        }
    }

    public static void save(UserController userController) throws IOException {
        String fileName = "save.txt";
        File file = new File(fileName);
        FileWriter writer = new FileWriter(file);
        for (User user : userController.getUsers()) {
            writer.write(generateSaveString(user) + "\n");
        }
        writer.close();
    }
    private static String generateSaveString(User user) {
        String main = user.getName() + "," + user.getPassword();
        for (Content content : user.actionController.getContents()) {
            main += "," + content.getName() + "," + content.getRating();
        }
        return main;
    }
}
