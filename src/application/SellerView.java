package application;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.beans.property.SimpleStringProperty;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class SellerView {

    private Main mainApp;
    private Stage primaryStage;
    private static final String BOOK_LISTINGS_FILE = "booklistings.txt";
    private static final String TRANSACTIONS_FILE = "transactions.txt";

    public SellerView(Main mainApp, Stage primaryStage) {
        this.mainApp = mainApp;
        this.primaryStage = primaryStage;
    }

    
    public VBox getSellerMenu(String sellerID) {
        VBox layout = new VBox(10);
        layout.setPadding(new Insets(20));
        layout.setAlignment(Pos.CENTER);
        layout.setStyle("-fx-background-color: #F7B05B;");

        primaryStage.setTitle("Sun Devil Bookstore - Seller Menu");

        Label welcomeLabel = new Label("Welcome, " + sellerID + "!");
        welcomeLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        Button sellBookButton = new Button("Sell Book");
        sellBookButton.setPrefWidth(300);
        sellBookButton.setOnAction(e -> mainApp.showSellBookPage(sellerID));

        Button manageListingsButton = new Button("Manage Active Listings");
        manageListingsButton.setPrefWidth(300);
        manageListingsButton.setOnAction(e -> mainApp.showManageListingsPage(sellerID));

        Button viewSalesHistoryButton = new Button("View Sales History");
        viewSalesHistoryButton.setPrefWidth(300);
        viewSalesHistoryButton.setOnAction(e -> mainApp.showSalesHistoryPage(sellerID));

        // Add logout button for main menu
        Button logoutButton = new Button("Logout");
        logoutButton.setPrefWidth(300);
        logoutButton.setOnAction(e -> mainApp.showLoginPage());

        layout.getChildren().addAll(
                welcomeLabel,
                sellBookButton,
                manageListingsButton,
                viewSalesHistoryButton,
                logoutButton
        );

        return layout;
    }

    
    public VBox getSellBookPage(String sellerID) {
        VBox layout = new VBox(10);
        layout.setPadding(new Insets(20));
        layout.setAlignment(Pos.CENTER);
        layout.setStyle("-fx-background-color: #F7B05B;");

        primaryStage.setTitle("Sun Devil Bookstore - Sell Book");

        Label titleLabel = new Label("Book Title:");
        TextField titleField = new TextField();
        titleField.setPromptText("Enter the book title");

        Label categoryLabel = new Label("Book Category:");
        ComboBox<String> categoryComboBox = new ComboBox<>();
        categoryComboBox.getItems().addAll(
                "Natural Science Books",
                "Computer Books",
                "Math Books",
                "English Language Books",
                "Other Books (e.g., Novels, Sci-Fi, Arts)"
        );

        Label conditionLabel = new Label("Book Condition:");
        ComboBox<String> conditionComboBox = new ComboBox<>();
        conditionComboBox.getItems().addAll(
                "Used Like New",
                "Moderately Used",
                "Heavily Used"
        );

        Label priceLabel = new Label("Original Price in dollars($)");
        TextField priceField = new TextField();
        priceField.setPromptText("Enter the original price");

        Label generatedPriceLabel = new Label("Suggested Buying Price: Not Generated");

        Button generatePriceButton = new Button("Generate Suggested Price");
        Button submitButton = new Button("List My Book at $0.00");
        submitButton.setDisable(true);

        generatePriceButton.setOnAction(e -> {
            String title = titleField.getText().trim();
            String category = categoryComboBox.getValue();
            String condition = conditionComboBox.getValue();
            String priceText = priceField.getText().trim();

            if (!title.isEmpty() && category != null && condition != null && !priceText.isEmpty()) {
                try {
                    double originalPrice = Double.parseDouble(priceText);
                    double buyingPrice = generateBuyingPrice(condition, originalPrice);
                    generatedPriceLabel.setText("Suggested Buying Price: $" + String.format("%.2f", buyingPrice));
                    submitButton.setText("List My Book at $" + String.format("%.2f", buyingPrice));
                    submitButton.setDisable(false);
                } catch (NumberFormatException ex) {
                    generatedPriceLabel.setText("Please enter a valid number for the price.");
                    submitButton.setDisable(true);
                }
            } else {
                generatedPriceLabel.setText("Please fill in all fields to generate a price.");
                submitButton.setDisable(true);
            }
        });

        submitButton.setOnAction(e -> {
            String title = titleField.getText().trim();
            String category = categoryComboBox.getValue();
            String condition = conditionComboBox.getValue();
            String priceText = priceField.getText().trim();

            if (!title.isEmpty() && category != null && condition != null && !priceText.isEmpty()) {
                try {
                    double originalPrice = Double.parseDouble(priceText);
                    double buyingPrice = generateBuyingPrice(condition, originalPrice);
                    Book newBook = new Book(sellerID, title, category, condition, originalPrice, buyingPrice);
                    saveBookListing(newBook);
                    showAlert(Alert.AlertType.INFORMATION, "Success", "Your book has been listed successfully!");
                    mainApp.showSellerMenu(sellerID);
                } catch (NumberFormatException ex) {
                    showAlert(Alert.AlertType.ERROR, "Invalid Input", "Please enter a valid number for the price.");
                }
            } else {
                showAlert(Alert.AlertType.WARNING, "Incomplete Data", "Please fill in all fields.");
            }
        });

        // Add navigation buttons
        HBox navigationBox = createNavigationButtons("sellBook", sellerID);

        layout.getChildren().addAll(
                titleLabel, titleField,
                categoryLabel, categoryComboBox,
                conditionLabel, conditionComboBox,
                priceLabel, priceField,
                generatePriceButton,
                generatedPriceLabel,
                submitButton,
                navigationBox
        );

        return layout;
    }

    
    public VBox getManageListingsPage(String sellerID) {
        VBox layout = new VBox(10);
        layout.setPadding(new Insets(20));
        layout.setAlignment(Pos.CENTER);
        layout.setStyle("-fx-background-color: #F7B05B;");

        primaryStage.setTitle("Sun Devil Bookstore - Manage Listings");

        List<Book> listings = getBookListingsBySeller(sellerID);

        VBox contentBox = new VBox(10);
        contentBox.setAlignment(Pos.CENTER);

        if (listings.isEmpty()) {
            Label noListingsLabel = new Label("You have no active listings.");
            contentBox.getChildren().add(noListingsLabel);
        } else {
            GridPane listingsGrid = new GridPane();
            listingsGrid.setPadding(new Insets(10));
            listingsGrid.setHgap(10);
            listingsGrid.setVgap(10);
            listingsGrid.setAlignment(Pos.CENTER);

            CheckBox[] checkBoxes = new CheckBox[listings.size()];
            for (int i = 0; i < listings.size(); i++) {
                Book book = listings.get(i);
                String listingInfo = "Title: " + book.getTitle() + ", Category: " + book.getCategory() + ", Condition: " + book.getCondition()
                        + ", Price: $" + String.format("%.2f", book.getBuyingPrice());
                checkBoxes[i] = new CheckBox(listingInfo);
                listingsGrid.add(checkBoxes[i], 0, i);
            }

            Button deleteButton = new Button("Delete Selected Listings");
            deleteButton.setOnAction(e -> {
                boolean anySelected = false;
                for (int i = 0; i < checkBoxes.length; i++) {
                    if (checkBoxes[i].isSelected()) {
                        deleteBookListing(listings.get(i));
                        anySelected = true;
                    }
                }
                if (anySelected) {
                    showAlert(Alert.AlertType.INFORMATION, "Deleted", "Selected listings have been deleted.");
                } else {
                    showAlert(Alert.AlertType.WARNING, "No Selection", "Please select at least one listing to delete.");
                }
                mainApp.showManageListingsPage(sellerID);
            });

            contentBox.getChildren().addAll(listingsGrid, deleteButton);
        }

        // Add navigation buttons
        HBox navigationBox = createNavigationButtons("manageListings", sellerID);

        layout.getChildren().addAll(contentBox, navigationBox);

        return layout;
    }

    
    public VBox getSalesHistoryPage(String sellerID) {
        VBox layout = new VBox(10);
        layout.setPadding(new Insets(20));
        layout.setAlignment(Pos.TOP_CENTER);
        layout.setStyle("-fx-background-color: #F7B05B;");

        primaryStage.setTitle("Sun Devil Bookstore - Sales History");

        Label titleLabel = new Label("Sales History");
        titleLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");

        // Create TableView
        TableView<ObservableList<String>> tableView = new TableView<>();
        tableView.setPrefWidth(700);
        tableView.setPrefHeight(400);

        // Define columns
        TableColumn<ObservableList<String>, String> categoryCol = new TableColumn<>("Category");
        categoryCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().get(0)));
        categoryCol.setPrefWidth(150);

        TableColumn<ObservableList<String>, String> buyerIDCol = new TableColumn<>("Buyer ID");
        buyerIDCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().get(1)));
        buyerIDCol.setPrefWidth(100);

        TableColumn<ObservableList<String>, String> amountCol = new TableColumn<>("Amount ($)");
        amountCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().get(3)));
        amountCol.setPrefWidth(100);

        TableColumn<ObservableList<String>, String> timestampCol = new TableColumn<>("Timestamp");
        timestampCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().get(4)));
        timestampCol.setPrefWidth(200);

        TableColumn<ObservableList<String>, String> statusCol = new TableColumn<>("Status");
        statusCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().get(5)));
        statusCol.setPrefWidth(150);

        tableView.getColumns().addAll(categoryCol, buyerIDCol, amountCol, timestampCol, statusCol);

        // Load transactions and populate the table
        ObservableList<ObservableList<String>> transactions = FXCollections.observableArrayList(getSalesHistory1(sellerID));
        tableView.setItems(transactions);

        if (transactions.isEmpty()) {
            Label noSalesLabel = new Label("You have no sales history.");
            layout.getChildren().addAll(titleLabel, noSalesLabel);
        } else {
            layout.getChildren().addAll(titleLabel, tableView);
        }

        // Add navigation buttons
        HBox navigationBox = createNavigationButtons("salesHistory", sellerID);
        layout.getChildren().add(navigationBox);

        return layout;
    }

    public ObservableList<ObservableList<String>> getSalesHistory1(String sellerID) {
        ObservableList<ObservableList<String>> transactions = FXCollections.observableArrayList();

        try (BufferedReader br = new BufferedReader(new FileReader("transactions.txt"))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] fields = line.split(",");
                if (fields.length == 6 && fields[2].equals(sellerID)) {
                    ObservableList<String> transaction = FXCollections.observableArrayList(fields);
                    transactions.add(transaction);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return transactions;
    }



    
    private void saveBookListing(Book book) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(BOOK_LISTINGS_FILE, true))) {
            writer.write(book.getSellerID() + "," + book.getTitle() + "," + book.getCategory() + "," + book.getCondition() + ","
                    + book.getOriginalPrice() + "," + book.getBuyingPrice());
            writer.newLine();
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to save the book listing.");
            System.out.println("Error saving book listing: " + e.getMessage());
        }
    }

    
    private List<Book> getBookListingsBySeller(String sellerID) {
        List<Book> listings = new ArrayList<>();
        File file = new File(BOOK_LISTINGS_FILE);

        if (!file.exists()) {
            System.out.println("Book listings file not found. It will be created upon adding a new listing.");
            return listings;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 6 && parts[0].equals(sellerID)) {
                    String title = parts[1];
                    String category = parts[2];
                    String condition = parts[3];
                    double originalPrice = Double.parseDouble(parts[4]);
                    double buyingPrice = Double.parseDouble(parts[5]);
                    Book book = new Book(sellerID, title, category, condition, originalPrice, buyingPrice);
                    listings.add(book);
                }
            }
        } catch (FileNotFoundException e) {
            // File does not exist yet; return empty list
            System.out.println("Book listings file not found. It will be created upon adding a new listing.");
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to read book listings.");
            System.out.println("Error reading book listings: " + e.getMessage());
        }

        return listings;
    }

    
    private void deleteBookListing(Book bookToDelete) {
        List<Book> updatedListings = new ArrayList<>();
        File file = new File(BOOK_LISTINGS_FILE);

        if (!file.exists()) {
            showAlert(Alert.AlertType.ERROR, "Error", "Book listings file does not exist.");
            return;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 6) {
                    String sellerID = parts[0];
                    String title = parts[1];
                    String category = parts[2];
                    String condition = parts[3];
                    double originalPrice = Double.parseDouble(parts[4]);
                    double buyingPrice = Double.parseDouble(parts[5]);

                    Book book = new Book(sellerID, title, category, condition, originalPrice, buyingPrice);
                    if (!book.equals(bookToDelete)) {
                        updatedListings.add(book);
                    }
                }
            }
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to read book listings.");
            System.out.println("Error reading book listings: " + e.getMessage());
            return;
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(BOOK_LISTINGS_FILE))) {
            for (Book book : updatedListings) {
                writer.write(book.getSellerID() + "," + book.getTitle() + "," + book.getCategory() + "," + book.getCondition() + ","
                        + book.getOriginalPrice() + "," + book.getBuyingPrice());
                writer.newLine();
            }
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to update book listings.");
            System.out.println("Error updating book listings: " + e.getMessage());
        }
    }

    
    private double generateBuyingPrice(String condition, double originalPrice) {
        double conditionFactor;
        switch (condition) {
            case "Used Like New":
                conditionFactor = 0.8;
                break;
            case "Moderately Used":
                conditionFactor = 0.6;
                break;
            case "Heavily Used":
                conditionFactor = 0.4;
                break;
            default:
                conditionFactor = 0.5;
                break;
        }
        return originalPrice * conditionFactor;
    }

    
    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    
    public HBox createNavigationButtons(String currentPage, String sellerID) {
        HBox navigationBox = new HBox(10);
        navigationBox.setAlignment(Pos.CENTER);

        // Back to Menu Button
        Button backButton = new Button("Back to Menu");
        backButton.setOnAction(e -> {
            mainApp.showSellerMenu(sellerID);
        });

        // Logout Button
        Button logoutButton = new Button("Logout");
        logoutButton.setOnAction(e -> {
            mainApp.showLoginPage();
        });

        navigationBox.getChildren().addAll(backButton, logoutButton);
        return navigationBox;
    }

    
    private List<ObservableList<String>> getSalesHistory(String sellerID) {
        List<ObservableList<String>> salesHistory = new ArrayList<>();
        File file = new File(TRANSACTIONS_FILE);

        if (!file.exists()) {
            System.out.println("Transactions file not found.");
            return salesHistory;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                // Assuming the format: Category,UserID,Amount,Timestamp,Status
                String[] parts = line.split(",");
                if (parts.length == 5) {
                    String category = parts[0].trim();
                    String userID = parts[1].trim();
                    String amount = parts[2].trim();
                    String timestamp = parts[3].trim();
                    String status = parts[4].trim();

                    if (userID.equals(sellerID)) {
                        ObservableList<String> transaction = FXCollections.observableArrayList();
                        transaction.add(category);
                        transaction.add(userID);
                        transaction.add(amount);
                        transaction.add(timestamp);
                        transaction.add(status);
                        salesHistory.add(transaction);
                    }
                }
            }
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to read transactions.");
            System.out.println("Error reading transactions: " + e.getMessage());
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Invalid number format in transactions.");
            System.out.println("Number format error: " + e.getMessage());
        }

        return salesHistory;
    }
}
