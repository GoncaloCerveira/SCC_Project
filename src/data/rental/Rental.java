package data.rental;

public class Rental {

    private String id;
    private String houseId;
    private String userId;
    private int startDate;    // Para podermos verificar se uma casa está livre num certo período de tempo
                              // Caso não seja necessário fica como estava antes
                              // Concatenar ano + mês + dia para ser mais fácil comparar na base de dados
    private int endDate;

    // total price
    private int price;

    public Rental(String id, String houseId, String userId, int startDate, int endDate, int price) {
        this.id = id;
        this.houseId = houseId;
        this.userId = userId;
        this.startDate = startDate;
        this.endDate = endDate;
        this.price = price;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getHouseId() {
        return houseId;
    }

    public void setHouseId(String houseId) {
        this.houseId = houseId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public int getStartDate() {
        return startDate;
    }

    public void setStartDate(int startDate) {
        this.startDate = startDate;
    }

    public int getEndDate() {
        return endDate;
    }

    public void setEndDate(int endDate) {
        this.endDate = endDate;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }
}
