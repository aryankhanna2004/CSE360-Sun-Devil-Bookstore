package application;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.beans.property.SimpleStringProperty;

import java.io.*;

public class AdminView {

    private Main mainApp;
    private Stage primaryStage;

    public AdminView(Main mainApp, Stage primaryStage) {
        this.mainApp = mainApp;
        this.primaryStage = primaryStage;
    }

    public VBox getAdminView() {
        VBox layout = new VBox(10);
        layout.setPadding(new Insets(20));
        layout.setAlignment(Pos.CENTER);
        layout.setStyle("-fx-background-color: #F7B05B;");

        primaryStage.setTitle("Admin View");

        // Create Tabs for each file
        TabPane tabPane = new TabPane();

        Tab usersTab = new Tab("User Data");
        usersTab.setContent(getTableView("userdata.txt", new String[]{"Email", "Password", "Role"}));
        usersTab.setClosable(false);

        Tab listingsTab = new Tab("Book Listings");
        listingsTab.setContent(getTableView("booklistings.txt", new String[]{"Seller Email", "Book Title", "Category", "Condition", "Price", "Final Price"}));
        listingsTab.setClosable(false);

        Tab transactionsTab = new Tab("Transactions");
        transactionsTab.setContent(getTableView("transactions.txt", new String[]{"Book Title", "Buyer Email", "Seller Email", "Price", "Date", "Status"}));
        transactionsTab.setClosable(false);

        tabPane.getTabs().addAll(usersTab, listingsTab, transactionsTab);

        // Add a logout button
        Button logoutButton = new Button("Logout");
        logoutButton.setOnAction(e -> mainApp.showLoginPage1());
        logoutButton.setStyle("-fx-background-color: #8B0000; -fx-text-fill: white;");

        layout.getChildren().addAll(tabPane, logoutButton);

        return layout;
    }

    private Node getTableView(String filename, String[] columnNames) {
        TableView<ObservableList<String>> tableView = new TableView<>();

        // Define columns
        for (int i = 0; i < columnNames.length; i++) {
            final int colIndex = i;
            TableColumn<ObservableList<String>, String> column = new TableColumn<>(columnNames[i]);
            column.setCellValueFactory(cellData -> {
                ObservableList<String> rowValues = cellData.getValue();
                if (colIndex < rowValues.size()) {
                    return new SimpleStringProperty(rowValues.get(colIndex));
                } else {
                    return new SimpleStringProperty("");
                }
            });
            tableView.getColumns().add(column);
        }

        // Populate data
        ObservableList<ObservableList<String>> data = FXCollections.observableArrayList();

        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = reader.readLine()) != null) {
                
                String[] parts = line.split(",", -1); 
                
                ObservableList<String> row = FXCollections.observableArrayList();
                for (int i = 0; i < columnNames.length; i++) {
                    if (i < parts.length) {
                        row.add(parts[i]);
                    } else {
                        row.add("");
                    }
                }
                data.add(row);
            }
        } catch (FileNotFoundException e) {
            System.out.println("File not found: " + filename);
        } catch (IOException e) {
            System.out.println("Error reading " + filename + ": " + e.getMessage());
        }

        tableView.setItems(data);
        tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        tableView.setPrefHeight(400);

        return tableView;
    }
}
