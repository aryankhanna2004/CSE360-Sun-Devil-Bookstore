package application;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class Main extends Application {

    private static final String DATA_FILE = "userdata.txt";
    private static final String BOOK_LISTINGS_FILE = "booklistings.txt";
    private static final String SALES_HISTORY_FILE = "saleshistory.txt";
    private static final String BUYING_RECORDS_FILE = "buyingrecords.txt";

    private VBox layout;
    private Stage primaryStage;
    private double generatedBuyingPrice = -1;

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        primaryStage.setTitle("Sun Devil Bookstore");

        layout = new VBox(10);
        layout.setPadding(new Insets(20));
        layout.setAlignment(Pos.CENTER);
        layout.setStyle("-fx-background-color: #F7B05B;");

        showLoginPage();

        Scene scene = new Scene(layout, 800, 600);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private HBox createNavigationButtons(String currentScreen, String sellerID) {
        HBox navigationBox = new HBox(10);
        navigationBox.setAlignment(Pos.CENTER);

        if (!currentScreen.equals("login")) {
            Button backButton = new Button("Back");
            backButton.setOnAction(e -> {
                switch (currentScreen) {
                    case "signup":
                        showLoginPage();
                        break;
                    case "sellBook":
                    case "manageListings":
                    case "salesHistory":
                        showSellerMenu(sellerID);
                        break;
                    default:
                        break;
                }
            });

            Button logoutButton = new Button("Logout");
            logoutButton.setOnAction(e -> showLoginPage());

            navigationBox.getChildren().addAll(backButton, logoutButton);
        }

        return navigationBox;
    }

    private void showLoginPage() {
        layout.getChildren().clear();
        primaryStage.setTitle("Sun Devil Bookstore - Login");

        // Logo
        ImageView logoImageView = new ImageView();
        try {
            String imagePath = "file:/Users/abc/eclipse-workspace-2/javafxproject/src/application/bookstore_logo.png";
            Image logoImage = new Image(imagePath);
            logoImageView.setImage(logoImage);
            logoImageView.setFitWidth(200);
            logoImageView.setPreserveRatio(true);
        } catch (Exception e) {
            System.out.println("Error loading image: " + e.getMessage());
        }

        // Email and Password fields
        Label emailLabel = new Label("Email address:");
        TextField emailField = new TextField();
        emailField.setPrefWidth(300);

        Label passwordLabel = new Label("Password:");
        PasswordField passwordField = new PasswordField();
        passwordField.setPrefWidth(260);

        TextField passwordFieldVisible = new TextField();
        passwordFieldVisible.setPrefWidth(260);
        passwordFieldVisible.setManaged(false);
        passwordFieldVisible.setVisible(false);

        ToggleButton viewPasswordButton = new ToggleButton("Show");
        viewPasswordButton.setOnAction(e -> {
            if (viewPasswordButton.isSelected()) {
                passwordFieldVisible.setText(passwordField.getText());
                passwordFieldVisible.setVisible(true);
                passwordFieldVisible.setManaged(true);
                passwordField.setVisible(false);
                passwordField.setManaged(false);
                viewPasswordButton.setText("Hide");
            } else {
                passwordField.setText(passwordFieldVisible.getText());
                passwordField.setVisible(true);
                passwordField.setManaged(true);
                passwordFieldVisible.setVisible(false);
                passwordFieldVisible.setManaged(false);
                viewPasswordButton.setText("Show");
            }
        });

        HBox passwordBox = new HBox(5, passwordField, passwordFieldVisible, viewPasswordButton);
        passwordBox.setAlignment(Pos.CENTER);

        Text errorMessage = new Text();
        errorMessage.setFill(Color.RED);

        Button loginButton = new Button("Log in");
        loginButton.setPrefWidth(300);
        loginButton.setOnAction(e -> {
            String email = emailField.getText();
            String password = passwordField.isVisible() ? passwordField.getText() : passwordFieldVisible.getText();
            if (authenticateUser(email, password)) {
                errorMessage.setText("Login successful!");
                errorMessage.setFill(Color.GREEN);
                showSellerMenu(email);
            } else {
                errorMessage.setText("Invalid email or password.");
                errorMessage.setFill(Color.RED);
            }
        });

        Label forgotPasswordLabel = new Label("Forgot password?");
        forgotPasswordLabel.setStyle("-fx-underline: true; -fx-text-fill: blue;");

        Button signUpButton = new Button("Sign Up");
        signUpButton.setPrefWidth(300);
        signUpButton.setOnAction(e -> showSignUpPage());

        layout.getChildren().addAll(
            logoImageView,
            emailLabel, emailField,
            passwordLabel, passwordBox,
            errorMessage,
            forgotPasswordLabel,
            loginButton,
            signUpButton
        );
    }

    private void showSignUpPage() {
        layout.getChildren().clear();
        primaryStage.setTitle("Sun Devil Bookstore - Sign Up");

        Label emailLabel = new Label("Email address:");
        TextField emailField = new TextField();
        emailField.setPrefWidth(300);

        Label passwordLabel = new Label("Password:");
        PasswordField passwordField = new PasswordField();
        passwordField.setPrefWidth(300);

        Button signUpButton = new Button("Sign Up");
        signUpButton.setPrefWidth(300);
        signUpButton.setOnAction(e -> {
            String email = emailField.getText();
            String password = passwordField.getText();
            saveUserData(email, password);
            System.out.println("User registered successfully!");
            showLoginPage();
        });

        // Add navigation buttons
        HBox navigationBox = createNavigationButtons("signup", null);

        layout.getChildren().addAll(
            emailLabel, emailField,
            passwordLabel, passwordField,
            signUpButton,
            navigationBox
        );
    }

    private void showSellerMenu(String sellerID) {
        layout.getChildren().clear();
        primaryStage.setTitle("Sun Devil Bookstore - Seller Menu");

        Button sellBookButton = new Button("Sell Book");
        sellBookButton.setPrefWidth(300);
        sellBookButton.setOnAction(e -> showSellBookPage(sellerID));

        Button manageListingsButton = new Button("Manage Active Listings");
        manageListingsButton.setPrefWidth(300);
        manageListingsButton.setOnAction(e -> showManageListingsPage(sellerID));

        Button viewSalesHistoryButton = new Button("View Sales History");
        viewSalesHistoryButton.setPrefWidth(300);
        viewSalesHistoryButton.setOnAction(e -> showSalesHistoryPage(sellerID));

        // Add logout button for main menu
        Button logoutButton = new Button("Logout");
        logoutButton.setPrefWidth(300);
        logoutButton.setOnAction(e -> showLoginPage());

        layout.getChildren().addAll(
            sellBookButton,
            manageListingsButton,
            viewSalesHistoryButton,
            logoutButton
        );
    }

    private void showSellBookPage(String sellerID) {
        layout.getChildren().clear();
        primaryStage.setTitle("Sun Devil Bookstore - Sell Book");

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

        Label generatedPriceLabel = new Label("Suggested Buying Price: Not Generated");

        Button generatePriceButton = new Button("Generate Suggested Price");
        Button submitButton = new Button("List My Book at " + (generatedBuyingPrice != -1 ? generatedBuyingPrice : "") + " $");
        submitButton.setDisable(true);

        generatePriceButton.setOnAction(e -> {
            String category = categoryComboBox.getValue();
            String condition = conditionComboBox.getValue();
            if (category != null && condition != null && !priceField.getText().isEmpty()) {
                double originalPrice = Double.parseDouble(priceField.getText());
                generatedBuyingPrice = generateBuyingPrice(category, condition, originalPrice);
                generatedPriceLabel.setText("Suggested Buying Price: " + generatedBuyingPrice + " $");
                submitButton.setText("List My Book at " + generatedBuyingPrice + " $");
                submitButton.setDisable(false);
            } else {
                generatedPriceLabel.setText("Please fill in all fields to generate a price.");
                submitButton.setDisable(true);
            }
        });

        submitButton.setOnAction(e -> {
            String category = categoryComboBox.getValue();
            String condition = conditionComboBox.getValue();
            if (generatedBuyingPrice != -1) {
                double originalPrice = Double.parseDouble(priceField.getText());
                saveBookListing(sellerID, category, condition, originalPrice, generatedBuyingPrice);
                showSellerMenu(sellerID);
            }
        });

        // Add navigation buttons
        HBox navigationBox = createNavigationButtons("sellBook", sellerID);

        layout.getChildren().addAll(
            categoryLabel, categoryComboBox,
            conditionLabel, conditionComboBox,
            priceLabel, priceField,
            generatePriceButton,
            generatedPriceLabel,
            submitButton,
            navigationBox
        );
    }

    private void showManageListingsPage(String sellerID) {
        layout.getChildren().clear();
        primaryStage.setTitle("Sun Devil Bookstore - Manage Listings");

        List<String> listings = getBookListingsBySeller(sellerID);
        
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
                String listing = listings.get(i);
                String[] parts = listing.split(",");
                String listingInfo = "Category: " + parts[1] + ", Condition: " + parts[2] + ", Price: " + parts[4] + " $";
                checkBoxes[i] = new CheckBox(listingInfo);
                listingsGrid.add(checkBoxes[i], 0, i);
            }

            Button deleteButton = new Button("Delete Selected Listings");
            deleteButton.setOnAction(e -> {
                for (int i = 0; i < checkBoxes.length; i++) {
                    if (checkBoxes[i].isSelected()) {
                        deleteBookListing(listings.get(i));
                    }
                }
                showManageListingsPage(sellerID);
            });

            contentBox.getChildren().addAll(listingsGrid, deleteButton);
        }

        // Add navigation buttons
        HBox navigationBox = createNavigationButtons("manageListings", sellerID);
        
        layout.getChildren().addAll(contentBox, navigationBox);
    }

    private void showSalesHistoryPage(String sellerID) {
        layout.getChildren().clear();
        primaryStage.setTitle("Sun Devil Bookstore - Sales History");

        Label underConstructionLabel = new Label("Sales History Page - Under Construction");
        
        // Add navigation buttons
        HBox navigationBox = createNavigationButtons("salesHistory", sellerID);

        layout.getChildren().addAll(underConstructionLabel, navigationBox);
    }

    // Rest of the utility methods remain unchanged
    private void saveBookListing(String sellerID, String category, String condition, double originalPrice, double buyingPrice) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(BOOK_LISTINGS_FILE, true))) {
            writer.write(sellerID + "," + category + "," + condition + "," + originalPrice + "," + buyingPrice);
            writer.newLine();
        } catch (IOException e) {
            System.out.println("Error saving book listing: " + e.getMessage());
        }
    }

    private List<String> getBookListingsBySeller(String sellerID) {
        List<String> listings = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(BOOK_LISTINGS_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.startsWith(sellerID + ",")) {
                    listings.add(line);
                }
            }
        } catch (IOException e) {
            System.out.println("Error reading book listings: " + e.getMessage());
        }
        return listings;
    }

    private void deleteBookListing(String listingToDelete) {
        List<String> updatedListings = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(BOOK_LISTINGS_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (!line.equals(listingToDelete)) {
                    updatedListings.add(line);
                }
            }
        } catch (IOException e) {
            System.out.println("Error reading book listings: " + e.getMessage());
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(BOOK_LISTINGS_FILE))) {
            for (String listing : updatedListings) {
                writer.write(listing);
                writer.newLine();
            }
        } catch (IOException e) {
            System.out.println("Error updating book listings: " + e.getMessage());
        }
    }

    private double generateBuyingPrice(String category, String condition, double originalPrice) {
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

    private void saveUserData(String email, String password) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(DATA_FILE, true))) {
            writer.write(email + "," + password);
            writer.newLine();
        } catch (IOException e) {
            System.out.println("Error saving user data: " + e.getMessage());
        }
    }

    private boolean authenticateUser(String email, String password) {
        try (BufferedReader reader = new BufferedReader(new FileReader(DATA_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 2 && parts[0].equals(email) && parts[1].equals(password)) {
                    return true;
                }
            }
        } catch (IOException e) {
            System.out.println("Error reading user data: " + e.getMessage());
        }
        return false;
    }

    public static void main(String[] args) {
        launch(args);
    }
}
