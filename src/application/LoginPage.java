package application;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.*;

public class LoginPage {

    private VBox layout;
    private Stage primaryStage;
    private Main mainApp;
    private final String DATA_FILE = "userdata.txt";

    public LoginPage(Main mainApp, Stage primaryStage) {
        this.mainApp = mainApp;
        this.primaryStage = primaryStage;
    }

    public VBox getLoginPage() {
        layout = new VBox(10);
        layout.setPadding(new Insets(20));
        layout.setAlignment(Pos.CENTER);
        layout.setStyle("-fx-background-color: #F7B05B;");

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
            String role = authenticateUser(email, password);
            if (role != null) {
                errorMessage.setText("Login successful!");
                errorMessage.setFill(Color.GREEN);
                if ("seller".equalsIgnoreCase(role)) {
                    mainApp.showSellerMenu(email);
                } else if ("buyer".equalsIgnoreCase(role)) {
                    mainApp.showBuyerMenu(email);
                } else {
                    errorMessage.setText("Unknown user role.");
                    errorMessage.setFill(Color.RED);
                }
            } else {
                errorMessage.setText("Invalid email or password.");
                errorMessage.setFill(Color.RED);
            }
        });

        Label forgotPasswordLabel = new Label("Forgot password?");
        forgotPasswordLabel.setStyle("-fx-underline: true; -fx-text-fill: blue;");

        Button signUpButton = new Button("Sign Up");
        signUpButton.setPrefWidth(300);
        signUpButton.setOnAction(e -> mainApp.showSignUpPage());

        layout.getChildren().addAll(
                logoImageView,
                emailLabel, emailField,
                passwordLabel, passwordBox,
                errorMessage,
                forgotPasswordLabel,
                loginButton,
                signUpButton
        );

        return layout;
    }

    public VBox getSignUpPage() {
        VBox layout = new VBox(10);
        layout.setPadding(new Insets(20));
        layout.setAlignment(Pos.CENTER);
        layout.setStyle("-fx-background-color: #F7B05B;");

        primaryStage.setTitle("Sun Devil Bookstore - Sign Up");

        Label emailLabel = new Label("Email address:");
        TextField emailField = new TextField();
        emailField.setPrefWidth(300);

        Label passwordLabel = new Label("Password:");
        PasswordField passwordField = new PasswordField();
        passwordField.setPrefWidth(300);

        Label roleLabel = new Label("Sign up as:");
        ToggleGroup roleGroup = new ToggleGroup();
        RadioButton buyerRadioButton = new RadioButton("Buyer");
        buyerRadioButton.setToggleGroup(roleGroup);
        buyerRadioButton.setSelected(true);
        RadioButton sellerRadioButton = new RadioButton("Seller");
        sellerRadioButton.setToggleGroup(roleGroup);
        HBox roleBox = new HBox(10, buyerRadioButton, sellerRadioButton);
        roleBox.setAlignment(Pos.CENTER);

        Button signUpButton = new Button("Sign Up");
        signUpButton.setPrefWidth(300);
        signUpButton.setOnAction(e -> {
            String email = emailField.getText();
            String password = passwordField.getText();
            String role = buyerRadioButton.isSelected() ? "buyer" : "seller";
            if (saveUserData(email, password, role)) {
                System.out.println("User registered successfully as " + role + "!");
                mainApp.showLoginPage();
            } else {
                // Handle user registration failure (e.g., email already exists)
                System.out.println("User registration failed.");
            }
        });

        // Add navigation buttons
        HBox navigationBox = mainApp.createNavigationButtons("signup", null);

        layout.getChildren().addAll(
                emailLabel, emailField,
                passwordLabel, passwordField,
                roleLabel, roleBox,
                signUpButton,
                navigationBox
        );

        return layout;
    }


    private String authenticateUser(String email, String password) {
        try (BufferedReader reader = new BufferedReader(new FileReader(DATA_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 3 && parts[0].equals(email) && parts[1].equals(password)) {
                    String role = parts[2].trim();
                    return role;
                }
            }
        } catch (IOException e) {
            System.out.println("Error reading user data: " + e.getMessage());
        }
        return null;
    }

    
    private boolean saveUserData(String email, String password, String role) {
        // Check if the user already exists
        try (BufferedReader reader = new BufferedReader(new FileReader(DATA_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length >= 1 && parts[0].equals(email)) {
                    // User already exists
                    return false;
                }
            }
        } catch (FileNotFoundException e) {
            // File does not exist yet; it will be created
        } catch (IOException e) {
            System.out.println("Error reading user data: " + e.getMessage());
            return false;
        }

        // Save the new user
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(DATA_FILE, true))) {
            writer.write(email + "," + password + "," + role);
            writer.newLine();
            return true;
        } catch (IOException e) {
            System.out.println("Error saving user data: " + e.getMessage());
            return false;
        }
    }
}
