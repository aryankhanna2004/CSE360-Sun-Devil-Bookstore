# Sun Devil Bookstore Project - README

## Project Overview
The **Sun Devil Bookstore** is a JavaFX-based desktop application designed for buyers and sellers to trade used books in a streamlined and organized manner. It aims to provide students and community members with an affordable platform to buy and sell books with ease. The application includes separate interfaces for buyers and sellers, allowing tailored functionality for each user type.

---

## Features

### General Features
- **User Authentication**:
  - Login and Sign-Up functionalities for both buyers and sellers.
  - Password toggle (show/hide feature).
- **Persistent Data Storage**:
  - Book listings, user credentials, and transaction records are stored locally in text files.

### Seller Features
- **Sell Books**:
  - Add book details, such as title, category, condition, and price.
  - Auto-generate a recommended buying price based on the condition of the book.
- **Manage Listings**:
  - View and delete active book listings.
- **Sales History**:
  - View sales history (currently under construction).

### Buyer Features
- **Browse Books**:
  - Filter books by category and condition.
  - Add books to a cart for purchase.
- **Cart Management**:
  - View items in the cart and remove duplicates.
- **Purchase Books**:
  - Record purchases and update listings automatically.

---

## Architecture

### 1. JavaFX GUI
- Custom layouts for buyer and seller views, designed using JavaFX's `VBox` and `HBox` components.
- Consistent and visually appealing UI styles defined in `application.css`.

### 2. Core Classes
- **Main.java**:
  - Manages navigation between views and initializes the application.
- **LoginPage.java**:
  - Handles user login, authentication, and sign-up functionalities.
- **SellerView.java**:
  - Provides functionalities for sellers, including selling books, managing listings, and viewing sales history.
- **BuyerView.java**:
  - Provides functionalities for buyers, including browsing books, filtering, cart management, and purchasing books.
- **Book.java**:
  - Represents a book object, encapsulating details like title, category, condition, and prices.

### 3. Data Storage
- **userdata.txt**:
  - Stores user credentials and roles.
- **booklistings.txt**:
  - Stores book details for sale.
- **transactions.txt**:
  - Records transaction history for purchases.

---

## Setup Instructions

### Prerequisites
- **Java Development Kit (JDK)** 8 or higher.
- **JavaFX SDK** (if not included in your JDK).
- A compatible IDE (e.g., IntelliJ IDEA, Eclipse).

### Steps
1. Clone the repository:
   ```bash
   git clone <https://github.com/aryankhanna2004/CSE360-Sun-Devil-Bookstore>
   ```
2. Navigate to the project folder:
   ```bash
   cd <https://github.com/aryankhanna2004/CSE360-Sun-Devil-Bookstore>
   ```
3. Import the project into your IDE.
4. Ensure the `bookstore_logo.png` is placed in the correct directory for the logo display.
5. Run the `Main.java` class to start the application.

---

## Usage Instructions

### Login and Sign-Up
1. **Login**:
   - Enter your email and password to log in.
   - The application redirects you to the respective dashboard (Buyer/Seller) based on your role.
2. **Sign-Up**:
   - Create an account by providing your email, password, and selecting your role (Buyer/Seller).

### Seller Workflow
1. Navigate to the "Sell Book" page.
2. Enter book details, generate a suggested price, and list your book.
3. Use the "Manage Listings" page to view and delete active listings.

### Buyer Workflow
1. Use filters to browse available books by category and condition.
2. Add desired books to your cart.
3. Purchase books directly, which removes them from active listings.

---

## Future Enhancements
1. **Enhanced Sales History**:
   - Fully implement the Sales History page for sellers.
2. **Improved UI**:
   - Further refine the user interface for better accessibility and design consistency.
3. **Database Integration**:
   - Replace file-based storage with a database for scalability.
4. **Security**:
   - Enhance user authentication with password encryption.

---

## Contributors
- **Primary Developer**: Aryan, Devin, Atharv, Zain, Jayanth

