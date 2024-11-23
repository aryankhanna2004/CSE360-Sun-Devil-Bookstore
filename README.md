Sun Devil Bookstore Project - README
Project Overview
The Sun Devil Bookstore is a JavaFX-based desktop application designed for buyers and sellers to trade used books in a streamlined and organized manner. It aims to provide students and community members with an affordable platform to buy and sell books with ease. The application includes separate interfaces for buyers and sellers, allowing tailored functionality for each user type.
________________________________________
Features
General Features
•	User Authentication: 
o	Login and Sign-Up functionalities for both buyers and sellers.
o	Password toggle (show/hide feature).
•	Persistent Data Storage: 
o	Book listings, user credentials, and transaction records are stored locally in text files.
Seller Features
•	Sell Books: 
o	Add book details, such as title, category, condition, and price.
o	Auto-generate a recommended buying price based on the condition of the book.
•	Manage Listings: 
o	View and delete active book listings.
•	Sales History: 
o	View sales history (currently under construction).
Buyer Features
•	Browse Books: 
o	Filter books by category and condition.
o	Add books to a cart for purchase.
•	Cart Management: 
o	View items in the cart and remove duplicates.
•	Purchase Books: 
o	Record purchases and update listings automatically.
________________________________________
Architecture
1. JavaFX GUI:
•	Custom layouts for buyer and seller views, designed using JavaFX's VBox and HBox components.
•	Consistent and visually appealing UI styles defined in application.css.
2. Core Classes:
•	Main.java: 
o	Manages navigation between views and initializes the application.
•	LoginPage.java: 
o	Handles user login, authentication, and sign-up functionalities.
•	SellerView.java: 
o	Provides functionalities for sellers, including selling books, managing listings, and viewing sales history.
•	BuyerView.java: 
o	Provides functionalities for buyers, including browsing books, filtering, cart management, and purchasing books.
•	Book.java: 
o	Represents a book object, encapsulating details like title, category, condition, and prices.
3. Data Storage:
•	userdata.txt: Stores user credentials and roles.
•	booklistings.txt: Stores book details for sale.
•	transactions.txt: Records transaction history for purchases.
________________________________________
Setup Instructions
1.	Requirements:
o	Java Development Kit (JDK) 8 or higher.
o	JavaFX SDK (if not included in your JDK).
o	A compatible IDE (e.g., IntelliJ IDEA, Eclipse).
2.	Project Setup:
o	Import the provided .java files into your JavaFX project in your IDE.
o	Ensure that the bookstore_logo.png is placed in the appropriate directory to display the application logo.
3.	Run the Application:
o	Execute the Main.java class to launch the application.
4.	File Setup:
o	Ensure userdata.txt, booklistings.txt, and transactions.txt exist in the application's working directory. The application will create these files if they do not exist.
________________________________________
Usage Instructions
Login and Sign-Up
1.	Login: 
o	Enter email and password to log in.
o	Based on the user role, the application will navigate to the appropriate dashboard (Buyer/Seller).
2.	Sign-Up: 
o	Create a new account by providing an email, password, and selecting a role (Buyer/Seller).
Seller Workflow
1.	Navigate to the "Sell Book" page.
2.	Fill in book details and generate a suggested price.
3.	Submit the listing.
4.	Manage active listings and delete any as needed.
Buyer Workflow
1.	Use filters to browse books by category and condition.
2.	Add books to the cart.
3.	Purchase books and automatically update the listings.
________________________________________
File Descriptions
•	application.css: 
o	Custom CSS rules for styling the application UI.
•	Book.java: 
o	Encapsulates book details and methods for creating, displaying, and comparing book objects.
•	BuyerView.java: 
o	Defines the Buyer interface and functionalities like filtering, cart management, and purchasing.
•	SellerView.java: 
o	Defines the Seller interface and functionalities like book listing, managing listings, and sales history.
•	LoginPage.java: 
o	Handles user login, sign-up, and navigation to other views.
•	Main.java: 
o	Entry point for the JavaFX application, managing navigation and primary stages.
________________________________________
Future Enhancements
1.	Improved Sales History: 
o	Fully implement the sales history page for sellers.
2.	Enhanced UI: 
o	Refine UI elements and responsiveness for better user experience.
3.	Database Integration: 
o	Replace file-based storage with a database for scalability.
4.	Security: 
o	Enhance password encryption and authentication mechanisms.
________________________________________
Project Contributors
•	Developer: Aryan, Devin, Atharv, Zain, Jayanth


