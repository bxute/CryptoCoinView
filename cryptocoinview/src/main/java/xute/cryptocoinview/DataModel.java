package xute.cryptocoinview;

public class DataModel {
  private String name;
  private String symbol;
  private int rank;
  private double price;
  private long volume;
  private long marketcap;
  private double percentageChange;

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getSymbol() {
    return symbol;
  }

  public void setSymbol(String symbol) {
    this.symbol = symbol;
  }

  public int getRank() {
    return rank;
  }

  public void setRank(int rank) {
    this.rank = rank;
  }

  public double getPrice() {
    return price;
  }

  public void setPrice(double price) {
    this.price = price;
  }

  public long getVolume() {
    return volume;
  }

  public void setVolume(long volume) {
    this.volume = volume;
  }

  public long getMarketcap() {
    return marketcap;
  }

  public void setMarketcap(long marketcap) {
    this.marketcap = marketcap;
  }

  public double getPercentageChange() {
    return percentageChange;
  }

  public void setPercentageChange(double percentageChange) {
    this.percentageChange = percentageChange;
  }

  @Override
  public String toString() {
    return "DataModel{" +
      "name='" + name + '\'' +
      ", symbol='" + symbol + '\'' +
      ", rank=" + rank +
      ", price=" + price +
      ", volume=" + volume +
      ", marketcap=" + marketcap +
      ", percentageChange=" + percentageChange +
      '}';
  }
}
