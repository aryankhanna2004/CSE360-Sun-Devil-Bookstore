package application;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class BuyerView {

    private Main mainApp;
    private Stage primaryStage;
    private ObservableList<Book> allBooks;
    private ObservableList<Book> filteredBooks;
    private ObservableList<Book> cart;

    private final String BOOK_LISTINGS_FILE = "booklistings.txt";
    private final String TRANSACTION_FILE = "transactions.txt";

    public BuyerView(Main mainApp, Stage primaryStage) {
        this.mainApp = mainApp;
        this.primaryStage = primaryStage;
        this.allBooks = FXCollections.observableArrayList();
        this.filteredBooks = FXCollections.observableArrayList();
        this.cart = FXCollections.observableArrayList();
        
    }

    public void refreshBooks() {
        allBooks.clear();
        filteredBooks.clear();
        cart.clear();
        loadBooks();
        // After loading, apply any existing filters or defaults
    }

 
    public VBox getBuyerMenu(String buyerID) {
        // Refresh books each time the Buyer View is opened
        refreshBooks();

        VBox layout = new VBox(15);
        layout.setPadding(new Insets(20));
        layout.setAlignment(Pos.TOP_CENTER);
        layout.setStyle("-fx-background-color: #AED581;");

        // Welcome Label
        Label welcomeLabel = new Label("Welcome, Buyer: " + buyerID);
        welcomeLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        // Filters
        HBox filtersBox = new HBox(10);
        filtersBox.setAlignment(Pos.CENTER);

        // Category Filter
        ComboBox<String> categoryComboBox = new ComboBox<>();
        categoryComboBox.getItems().addAll(
                "All Categories",
                "Natural Science Books",
                "Computer Books",
                "Math Books",
                "English Language Books",
                "Other Books (e.g., Novels, Sci-Fi, Arts)"
        );
        categoryComboBox.setValue("All Categories");

        // Condition Filter
        ComboBox<String> conditionComboBox = new ComboBox<>();
        conditionComboBox.getItems().addAll(
                "All Conditions",
                "Used Like New",
                "Moderately Used",
                "Heavily Used"
        );
        conditionComboBox.setValue("All Conditions");

        filtersBox.getChildren().addAll(
                new Label("Category:"), categoryComboBox,
                new Label("Condition:"), conditionComboBox
        );

        // Books Table
        TableView<Book> booksTable = new TableView<>();
        booksTable.setItems(filteredBooks);
        booksTable.setPrefHeight(300);

        TableColumn<Book, String> sellerCol = new TableColumn<>("Seller ID");
        sellerCol.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getSellerID()));

        TableColumn<Book, String> titleCol = new TableColumn<>("Title"); // New Column for Title
        titleCol.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getTitle()));
        titleCol.setPrefWidth(200); // Adjust width as needed

        TableColumn<Book, String> categoryCol = new TableColumn<>("Category");
        categoryCol.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getCategory()));
        categoryCol.setPrefWidth(150);

        TableColumn<Book, String> conditionCol = new TableColumn<>("Condition");
        conditionCol.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getCondition()));
        conditionCol.setPrefWidth(150);

        TableColumn<Book, String> originalPriceCol = new TableColumn<>("Original Price ($)");
        originalPriceCol.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(String.format("%.2f", data.getValue().getOriginalPrice())));
        originalPriceCol.setPrefWidth(120);

        TableColumn<Book, String> buyingPriceCol = new TableColumn<>("Buying Price ($)");
        buyingPriceCol.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(String.format("%.2f", data.getValue().getBuyingPrice())));
        buyingPriceCol.setPrefWidth(120);

        booksTable.getColumns().addAll(sellerCol, titleCol, categoryCol, conditionCol, originalPriceCol, buyingPriceCol);

        // Add to Cart Button
        Button addToCartButton = new Button("Add to Cart");
        addToCartButton.setOnAction(e -> {
            Book selectedBook = booksTable.getSelectionModel().getSelectedItem();
            if (selectedBook != null) {
                if (!cart.contains(selectedBook)) { // Prevent duplicates
                    cart.add(selectedBook);
                    showAlert(Alert.AlertType.INFORMATION, "Cart Update", "Book added to cart.");
                } else {
                    showAlert(Alert.AlertType.WARNING, "Duplicate Entry", "This book is already in your cart.");
                }
            } else {
                showAlert(Alert.AlertType.WARNING, "No Selection", "Please select a book to add to cart.");
            }
        });

        // Cart ListView
        ListView<Book> cartListView = new ListView<>(cart);
        cartListView.setPrefHeight(150);
        cartListView.setCellFactory(param -> new ListCell<Book>() {
            @Override
            protected void updateItem(Book item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null || item.getTitle() == null) {
                    setText(null);
                } else {
                    setText("Title: " + item.getTitle() + " - $" + String.format("%.2f", item.getBuyingPrice()));
                }
            }
        });

        // Buy Now Button
        Button buyNowButton = new Button("Buy Now");
        buyNowButton.setOnAction(e -> {
            if (!cart.isEmpty()) {
                processPurchase(FXCollections.observableArrayList(cart), buyerID); 
                cart.clear();
                showAlert(Alert.AlertType.INFORMATION, "Purchase Successful", "Your purchase has been processed.");
            } else {
                showAlert(Alert.AlertType.WARNING, "Empty Cart", "Your cart is empty.");
            }
        });

        // Logout Button
        Button logoutButton = new Button("Logout");
        logoutButton.setOnAction(e -> mainApp.showLoginPage());

        // Arrange Buttons
        HBox buttonsBox = new HBox(10, addToCartButton, buyNowButton, logoutButton);
        buttonsBox.setAlignment(Pos.CENTER);

        // Assemble Layout
        layout.getChildren().addAll(
                welcomeLabel,
                filtersBox,
                booksTable,
                buttonsBox,
                new Label("Your Cart:"),
                cartListView
        );

        // Initial Filter
        applyFilters("All Categories", "All Conditions");
        categoryComboBox.setOnAction(e -> applyFilters(categoryComboBox.getValue(), conditionComboBox.getValue()));
        conditionComboBox.setOnAction(e -> applyFilters(categoryComboBox.getValue(), conditionComboBox.getValue()));

        return layout;
    }

    private void loadBooks() {
        try (BufferedReader reader = new BufferedReader(new FileReader(BOOK_LISTINGS_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 6) { // Expecting 6 fields
                    String sellerID = parts[0];
                    String title = parts[1];
                    String category = parts[2];
                    String condition = parts[3];
                    double originalPrice = Double.parseDouble(parts[4]);
                    double buyingPrice = Double.parseDouble(parts[5]);
                    Book book = new Book(sellerID, title, category, condition, originalPrice, buyingPrice);
                    allBooks.add(book);
                } else {
                    System.out.println("Invalid book listing format: " + line);
                }
            }
        } catch (FileNotFoundException e) {
            // File does not exist yet; it will be created when a seller lists a book
            System.out.println("Book listings file not found. It will be created upon adding a new listing.");
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to load book listings.");
            System.out.println("Error reading book listings: " + e.getMessage());
        }
    }

    private void applyFilters(String category, String condition) {
        filteredBooks.clear();

        for (Book book : allBooks) {
            boolean matchesCategory = category.equals("All Categories") || book.getCategory().equals(category);
            boolean matchesCondition = condition.equals("All Conditions") || book.getCondition().equals(condition);

            if (matchesCategory && matchesCondition) {
                filteredBooks.add(book);
            }
        }
    }


    private void processPurchase(ObservableList<Book> purchasedBooks, String buyerID) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(TRANSACTION_FILE, true))) {
            for (Book book : purchasedBooks) {
                String transaction = String.format("%s,%s,%s,%.2f,%s,%s",
                        book.getCategory(),
                        buyerID,
                        book.getSellerID(),
                        book.getBuyingPrice(),
                        LocalDateTime.now().toString(),
                        "Processed"); // Placeholder for Payment Details
                writer.write(transaction);
                writer.newLine();
            }
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to record the transaction.");
            System.out.println("Error writing to transaction file: " + e.getMessage());
        }

        // Now delete the purchased books from booklistings.txt
        for (Book book : purchasedBooks) {
            deleteBookListing(book);
        }

        // Remove from allBooks and filteredBooks
        allBooks.removeAll(purchasedBooks);
        filteredBooks.removeAll(purchasedBooks);
    }


    private void deleteBookListing(Book bookToDelete) {
        List<Book> updatedListings = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(BOOK_LISTINGS_FILE))) {
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

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
