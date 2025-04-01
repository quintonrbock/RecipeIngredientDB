package com.yourname.recipedb.view;

import java.awt.BorderLayout;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

import com.yourname.recipedb.DataFetcher;
import com.yourname.recipedb.DataUpdater;
import com.yourname.recipedb.controller.ActiveUserManager;
import com.yourname.recipedb.model.User;

public class UserPanel extends JPanel {

    private final JTable userTable;
    private final DefaultTableModel userTableModel;
    private final JButton addUserButton, editUserButton, deleteUserButton;

    public UserPanel() {
        setLayout(new BorderLayout());

        // Table setup
        String[] columnNames = {"User ID", "First Name", "Last Name", "Username", "Password"};
        userTableModel = new DefaultTableModel(columnNames, 0);
        userTable = new JTable(userTableModel);
        initialize();

        // Buttons setup
        addUserButton = new JButton("Add User");
        editUserButton = new JButton("Edit User");
        deleteUserButton = new JButton("Delete User");

        addUserButton.addActionListener(e -> addUser());
        editUserButton.addActionListener(e -> editUser());
        deleteUserButton.addActionListener(e -> deleteUser());

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(addUserButton);
        buttonPanel.add(editUserButton);
        buttonPanel.add(deleteUserButton);

        // Show buttons only if admin
        buttonPanel.setVisible(ActiveUserManager.isAdmin());

        // Add components
        add(new JScrollPane(userTable), BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void initialize() {
        loadUsers();
    }

    public void loadUsers() {
        userTableModel.setRowCount(0);
        List<User> users = DataFetcher.getAllUsers();
        for (User user : users) {
            userTableModel.addRow(new Object[]{
                user.getId(), user.getFirstName(), user.getLastName(), user.getUsername(), user.getPassword()
            });
        }
        userTable.revalidate();
        userTable.repaint();
    }

    private void addUser() {
        JTextField firstNameField = new JTextField();
        JTextField lastNameField = new JTextField();
        JTextField usernameField = new JTextField();
        JPasswordField passwordField = new JPasswordField();

        Object[] fields = {"First Name:", firstNameField, "Last Name:", lastNameField, "Username:", usernameField, "Password:", passwordField};


        int option = JOptionPane.showConfirmDialog(this, fields, "Add User", JOptionPane.OK_CANCEL_OPTION);
        if (option == JOptionPane.OK_OPTION) {
            String firstName = firstNameField.getText().trim();
            String lastName = lastNameField.getText().trim();
            String username = usernameField.getText().trim();
            String password = new String(passwordField.getPassword()).trim();
            if (!username.isEmpty()) {
                DataUpdater.addUser(firstName, lastName, username, password);
                loadUsers();
            } else {
                JOptionPane.showMessageDialog(this, "Username cannot be empty.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void editUser() {
        int selectedRow = userTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Select a user to edit.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int userId = (int) userTableModel.getValueAt(selectedRow, 0);
        String currentFirstName = (String) userTableModel.getValueAt(selectedRow, 1);
        String currentLastName = (String) userTableModel.getValueAt(selectedRow, 2);
        String currentUsername = (String) userTableModel.getValueAt(selectedRow, 3);
        String currentPassword = (String) userTableModel.getValueAt(selectedRow, 4);

        JTextField firstNameField = new JTextField(currentFirstName);
        JTextField lastNameField = new JTextField(currentLastName);
        JTextField usernameField = new JTextField(currentUsername);
        JPasswordField passwordField = new JPasswordField(currentPassword);

        Object[] fields = {"First Name:", firstNameField, "Last Name:", lastNameField, "Username:", usernameField, "Password:", passwordField};

        int option = JOptionPane.showConfirmDialog(this, fields, "Edit User", JOptionPane.OK_CANCEL_OPTION);
        if (option == JOptionPane.OK_OPTION) {
            String newFirstName = firstNameField.getText().trim();
            String newLastName = lastNameField.getText().trim();
            String newUsername = usernameField.getText().trim();
            String newPassword = new String(passwordField.getPassword()).trim();
            if (!newUsername.isEmpty()) {
                DataUpdater.updateUser(userId, newFirstName, newLastName, newUsername, newPassword);
                loadUsers();
            } else {
                JOptionPane.showMessageDialog(this, "Username cannot be empty.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void deleteUser() {
        int selectedRow = userTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Select a user to delete.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int userId = (int) userTableModel.getValueAt(selectedRow, 0);
        int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete this user?", "Delete User", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            DataUpdater.deleteUser(userId);
            loadUsers();
        }
    }

    public JTable getTable() {
        return userTable;
    }
    
    public DefaultTableModel getTableModel() {
        return userTableModel;
    }
    
}
