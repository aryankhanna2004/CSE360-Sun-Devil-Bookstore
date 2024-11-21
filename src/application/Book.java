package application;

public class Book {
    private String sellerID;
    private String title;
    private String category;
    private String condition;
    private double originalPrice;
    private double buyingPrice;

    public Book(String sellerID, String title, String category, String condition, double originalPrice, double buyingPrice) {
        this.sellerID = sellerID;
        this.title = title;
        this.category = category;
        this.condition = condition;
        this.originalPrice = originalPrice;
        this.buyingPrice = buyingPrice;
    }

    // Getters
    public String getSellerID() {
        return sellerID;
    }

    public String getTitle() {
        return title;
    }

    public String getCategory() {
        return category;
    }

    public String getCondition() {
        return condition;
    }

    public double getOriginalPrice() {
        return originalPrice;
    }

    public double getBuyingPrice() {
        return buyingPrice;
    }

    // Setters (if needed)
    public void setTitle(String title) {
        this.title = title;
    }

    public void setBuyingPrice(double buyingPrice) {
        this.buyingPrice = buyingPrice;
    }

    @Override
    public String toString() {
        return "Title: " + title + ", Category: " + category + ", Condition: " + condition + ", Price: $" + String.format("%.2f", buyingPrice);
    }

    // Override equals and hashCode for proper comparison (useful in deletion)
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Book)) return false;
        Book other = (Book) obj;
        return sellerID.equals(other.sellerID) &&
               title.equals(other.title) &&
               category.equals(other.category) &&
               condition.equals(other.condition) &&
               originalPrice == other.originalPrice &&
               buyingPrice == other.buyingPrice;
    }

    @Override
    public int hashCode() {
        return sellerID.hashCode() + title.hashCode() + category.hashCode() + condition.hashCode()
               + Double.hashCode(originalPrice) + Double.hashCode(buyingPrice);
    }
}
