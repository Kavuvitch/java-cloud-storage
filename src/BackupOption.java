/** Дополнительная услуга резервного копирования. */
public class BackupOption {
  private final String name;
  private double price;

  public BackupOption(String name, double price) {
    if (name == null || name.isBlank()) {
      throw new IllegalArgumentException("Название опции не должно быть пустым.");
    }
    this.name = name;
    setPrice(price);
  }

  public String getName() {
    return name;
  }

  public double getPrice() {
    return price;
  }

  /** Цена одного резервного копирования за расчетный день. */
  public void setPrice(double price) {
    if (!Double.isFinite(price) || price < 0) {
      throw new IllegalArgumentException("Цена опции должна быть конечной и неотрицательной.");
    }
    this.price = java.math.BigDecimal.valueOf(price)
        .setScale(2, java.math.RoundingMode.HALF_UP).doubleValue();
  }
}
