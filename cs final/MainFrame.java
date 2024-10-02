import javax.swing.*;
import javax.swing.border.BevelBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;

public class MainFrame extends JFrame {
    private final UserController userController;

    public MainFrame(UserController userController) throws IOException {
        super("Content Rater!");
        this.userController = userController;
        DataController.load(userController);
        if (userController.size() < 1) {
            add(registerPanel());
        } else {
            add(userPanel(userController.getUsers()));
        }
        setSize(600, 500);
        setResizable(false);
        setVisible(true);
    }

    private JPanel userPanel(ArrayList<User> users) {
        JPanel panel = new JPanel(new GridLayout(0, 1));
        users.forEach(user -> {
            JPanel userPanel = new JPanel(new GridLayout(1, 3));
            userPanel.add(new JLabel(user.getName(), JLabel.CENTER));
            JButton viewButton = new JButton("View Content");
            viewButton.setBorder(new BevelBorder(BevelBorder.RAISED));
            viewButton.addActionListener(e -> {
                userController.setActiveUser(users.indexOf(user) + 1);
                getContentPane().removeAll();
                getContentPane().add(contentPanel(false));
                revalidate();
                repaint();
            });
            JButton loginButton = new JButton("Login");
            loginButton.addActionListener(e -> {
                userController.setActiveUser(users.indexOf(user) + 1);
                getContentPane().removeAll();
                getContentPane().add(loginPanel());
                revalidate();
            });
            userPanel.add(viewButton);
            userPanel.add(loginButton);
            panel.add(userPanel);
        });
        JButton button = new JButton("Register");
        button.addActionListener(e -> {
            getContentPane().removeAll();
            try {
                getContentPane().add(registerPanel());
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
            revalidate();
        });
        panel.add(button);
        return panel;
    }

    private JPanel contentPanel(boolean editingMode) {
        DecimalFormat df = new DecimalFormat("#.##");
        ArrayList<Content> contents = userController.getActiveUser().actionController.getContents();
        JPanel panel = new JPanel(new GridLayout(0, 1));
        AtomicInteger i = new AtomicInteger();
        contents.forEach(content -> {
            JLabel label = new JLabel(i.get() + 1 + ". " + content.getName() + " - " + df.format(content.getRating()) + "/10");
            panel.add(label);
            i.getAndIncrement();
        });
        if (!editingMode) {
            JButton button = new JButton("Return to user");
            button.addActionListener(e -> {
                getContentPane().removeAll();
                getContentPane().add(userPanel(userController.getUsers()));
                revalidate();
            });
            panel.add(button);
        }

        return panel;
    }

    private JPanel registerPanel() throws IOException {
        JPanel panel = new JPanel(new GridLayout(0, 1));
        panel.add(new JLabel("Name:"));
        JTextField name = new JTextField();
        panel.add(name);
        panel.add(new JLabel("Password:"));
        JTextField password = new JTextField();
        panel.add(password);
        JButton button = new JButton("Register");
        button.addActionListener(e -> {
            if (name.getText().isEmpty() || password.getText().isEmpty()) {
                return;
            }
            for (User user : userController.getUsers()) {
                if (user.getName().equalsIgnoreCase(name.getText())) {
                    return;
                }
            }
            userController.addUser(new User(name.getText(), password.getText()));
            try {
                DataController.save(userController);
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
            getContentPane().removeAll();
            getContentPane().add(userPanel(userController.getUsers()));
            revalidate();
        });
        panel.add(button);
        return panel;
    }

    private JPanel loginPanel() {
        final boolean[] wrong = {false};
        JPanel panel = new JPanel(new GridLayout(0, 1));
        panel.add(new JLabel("Name: " + userController.getActiveUser().getName()));
        JPanel smallPanel = new JPanel(new GridLayout(0, 2));
        JTextField password = new JTextField();
        smallPanel.add(new JLabel("Password:"));
        smallPanel.add(password);
        panel.add(smallPanel);
        JButton button = new JButton("Login");
        button.addActionListener(e -> {
            if (password.getText().isEmpty()) {
                return;
            } else if (password.getText().equalsIgnoreCase(userController.getActiveUser().getPassword())) {
                getContentPane().removeAll();
                getContentPane().add(editingPanel());
                revalidate();
            } else {
                if (wrong[0]) {
                    return;
                }
                wrong[0] = true;
                JPanel miniPanel = new JPanel(new GridLayout(1, 2));
                miniPanel.add(new JLabel("Incorrect name or password"));
                JButton button1 = new JButton("Return");
                button1.addActionListener(e1 -> {
                    getContentPane().removeAll();
                    getContentPane().add(userPanel(userController.getUsers()));
                    revalidate();
                });
                miniPanel.add(button1);
                panel.add(miniPanel);
                revalidate();
                return;
            }
        });
        panel.add(button);
        return panel;
    }

    private JPanel editingPanel() {
        JPanel panel = new JPanel(new GridLayout(0, 1));
        panel.add(contentPanel(true));
        JPanel miniPanel = new JPanel(new GridLayout(1, 5));
        JButton addButton = new JButton("Add");
        JButton deleteButton = new JButton("Delete");
        JButton undoButton = new JButton("Undo");
        JButton logoutButton = new JButton("Logout");
        JTextField IndexField1 = new JTextField();
        JTextField IndexField2 = new JTextField();

        addButton.addActionListener(e -> {
            if (IndexField1.getText().isEmpty() || IndexField2.getText().isEmpty()) {
                return;
            }
            double num = Double.parseDouble(IndexField2.getText().strip());
            if (num  > 10 || num < 0) {
                return;
            }
            userController.getActiveUser().actionController.add(IndexField1.getText(), Double.parseDouble(IndexField2.getText()));
            IndexField1.setText("");
            IndexField2.setText("");
            panel.remove(0);
            panel.add(contentPanel(true), 0);
            revalidate();
            try {
                DataController.save(userController);
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        });
        deleteButton.addActionListener(e -> {
            if (IndexField1.getText().isEmpty()) {
                return;
            }
            userController.getActiveUser().actionController.remove(Integer.parseInt(IndexField1.getText()) - 1);
            IndexField1.setText("");
            panel.remove(0);
            panel.add(contentPanel(true), 0);
            revalidate();
            try {
                DataController.save(userController);
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        });

        undoButton.addActionListener(e -> {
            userController.getActiveUser().actionController.undo();
            panel.remove(0);
            panel.add(contentPanel(true), 0);
            revalidate();
            try {
                DataController.save(userController);
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        });

        logoutButton.addActionListener(e -> {
            getContentPane().removeAll();
            getContentPane().add(userPanel(userController.getUsers()));
            revalidate();
        });
        miniPanel.add(addButton);
        miniPanel.add(deleteButton);
        miniPanel.add(undoButton);
        miniPanel.add(logoutButton);
        panel.add(miniPanel);

        JPanel smallerPanel = new JPanel(new GridLayout(2, 2));
        smallerPanel.add(new JLabel("Content Name or Index to Delete", JLabel.CENTER));
        smallerPanel.add(new JLabel("Rating", JLabel.CENTER));
        smallerPanel.add(IndexField1);
        smallerPanel.add(IndexField2);
        panel.add(smallerPanel);
        return panel;
    }
}


