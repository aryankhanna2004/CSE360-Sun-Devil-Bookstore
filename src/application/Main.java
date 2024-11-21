package application;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import javafx.scene.control.*;

public class Main extends Application {

    private VBox mainLayout;
    private Stage primaryStage;
    private Scene scene;
    private LoginPage loginPage;
    private SellerView sellerView;
    private BuyerView buyerView;

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        primaryStage.setTitle("Sun Devil Bookstore");

        mainLayout = new VBox();
        mainLayout.setPadding(new Insets(20));
        mainLayout.setAlignment(Pos.CENTER);
        mainLayout.setStyle("-fx-background-color: #F7B05B;");

        scene = new Scene(mainLayout, 800, 600);
        primaryStage.setScene(scene);
        primaryStage.show();

        loginPage = new LoginPage(this, primaryStage);
        sellerView = new SellerView(this, primaryStage);
        buyerView = new BuyerView(this, primaryStage);

        showLoginPage();
    }

    public void showLoginPage() {
        VBox loginLayout = loginPage.getLoginPage();
        mainLayout.getChildren().clear();
        mainLayout.getChildren().add(loginLayout);
    }

    public void showSignUpPage() {
        VBox signUpLayout = loginPage.getSignUpPage();
        mainLayout.getChildren().clear();
        mainLayout.getChildren().add(signUpLayout);
    }

    public void showSellerMenu(String sellerID) {
        VBox sellerMenuLayout = sellerView.getSellerMenu(sellerID);
        mainLayout.getChildren().clear();
        mainLayout.getChildren().add(sellerMenuLayout);
    }

    public void showSellBookPage(String sellerID) {
        VBox sellBookLayout = sellerView.getSellBookPage(sellerID);
        mainLayout.getChildren().clear();
        mainLayout.getChildren().add(sellBookLayout);
    }

    public void showManageListingsPage(String sellerID) {
        VBox manageListingsLayout = sellerView.getManageListingsPage(sellerID);
        mainLayout.getChildren().clear();
        mainLayout.getChildren().add(manageListingsLayout);
    }

    public void showSalesHistoryPage(String sellerID) {
        VBox salesHistoryLayout = sellerView.getSalesHistoryPage(sellerID);
        mainLayout.getChildren().clear();
        mainLayout.getChildren().add(salesHistoryLayout);
    }

    public HBox createNavigationButtons(String currentScreen, String sellerID) {
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

            navigationBox.getChildren().add(backButton);

            if (!currentScreen.equals("signup")) {
                Button logoutButton = new Button("Logout");
                logoutButton.setOnAction(e -> showLoginPage());
                navigationBox.getChildren().add(logoutButton);
            }
        }

        return navigationBox;
    }

    public void showBuyerMenu(String buyerID) {
        VBox buyerMenuLayout = buyerView.getBuyerMenu(buyerID);
        mainLayout.getChildren().clear();
        mainLayout.getChildren().add(buyerMenuLayout);
    }

    public VBox getMainLayout() {
        return mainLayout;
    }

    public static void main(String[] args) {
        launch(args);
    }
}
