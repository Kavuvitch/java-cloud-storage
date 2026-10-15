import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

/** Облачное хранилище. Все цены относятся к одному расчетному дню. */
public class CloudStorage {
  private static final String DEFAULT_TARIFF = "100 ГБ";
  private static final int DEFAULT_FREQUENCY = 1;
  private static final int BASE_MULTIPLIER = 1;
  private static final int MEDIUM_MULTIPLIER = 2;
  private static final int LARGE_MULTIPLIER = 4;

  private final String name;
  private final double basePrice;
  private final String tariff;
  private BackupOption appliedOption = null;
  private int backupFrequency = 0;

  public CloudStorage(String name, double basePrice) {
    this(name, basePrice, DEFAULT_TARIFF);
  }

  public CloudStorage(String name, double basePrice, String tariff) {
    this(name, basePrice, tariff, null);
  }

  public CloudStorage(
      String name, double basePrice, String tariff, BackupOption appliedOption) {
    if (name == null || name.isBlank()) {
      throw new IllegalArgumentException("Название хранилища не должно быть пустым.");
    }
    if (!Double.isFinite(basePrice) || basePrice < 0) {
      throw new IllegalArgumentException("Базовая цена должна быть конечной и неотрицательной.");
    }
    Objects.requireNonNull(tariff, "Тариф не должен быть null.");
    tariffMultiplier(tariff);
    this.name = name;
    this.basePrice = BigDecimal.valueOf(basePrice).setScale(2, RoundingMode.HALF_UP)
        .doubleValue();
    this.tariff = tariff;
    if (appliedOption != null) {
      enableBackup(appliedOption);
    }
  }

  /** Базовая стоимость с учетом тарифа, без дополнительной услуги. */
  public double calculateFinalPrice() {
    return storagePrice().doubleValue();
  }

  /** Подключение опции с одним копированием в день. */
  public double calculateFinalPrice(BackupOption option) {
    return calculateFinalPrice(option, DEFAULT_FREQUENCY);
  }

  /** Подключение опции; доплата = цена одного копирования * частота в день. */
  public double calculateFinalPrice(BackupOption option, int frequency) {
    Objects.requireNonNull(option, "Опция не должна быть null.");
    if (frequency <= 0) {
      throw new IllegalArgumentException("Частота копирования должна быть больше нуля.");
    }
    double result = storagePrice().add(
        BigDecimal.valueOf(option.getPrice()).multiply(BigDecimal.valueOf(frequency)))
        .setScale(2, RoundingMode.HALF_UP).doubleValue();
    if (!Double.isFinite(result)) {
      throw new IllegalArgumentException("Итоговая стоимость слишком велика.");
    }
    appliedOption = option;
    backupFrequency = frequency;
    return result;
  }

  // Имена из таблицы варианта 17. Расчет делегируется единой реализации.
  public double enableBackup(BackupOption option) {
    return calculateFinalPrice(option);
  }

  public double enableBackup(BackupOption option, int frequency) {
    return calculateFinalPrice(option, frequency);
  }

  private BigDecimal storagePrice() {
    BigDecimal result = BigDecimal.valueOf(basePrice)
        .multiply(BigDecimal.valueOf(tariffMultiplier(tariff)));
    if (!Double.isFinite(result.doubleValue())) {
      throw new IllegalArgumentException("Стоимость хранилища слишком велика.");
    }
    return result.setScale(2, RoundingMode.HALF_UP);
  }

  private static int tariffMultiplier(String tariff) {
    return switch (tariff) {
      case "100 ГБ" -> BASE_MULTIPLIER;
      case "512 ГБ" -> MEDIUM_MULTIPLIER;
      case "2 ТБ" -> LARGE_MULTIPLIER;
      default -> throw new IllegalArgumentException("Неизвестный тариф: " + tariff);
    };
  }

  public String getName() {
    return name;
  }

  public double getBasePrice() {
    return basePrice;
  }

  public String getTariff() {
    return tariff;
  }

  public BackupOption getAppliedOption() {
    return appliedOption;
  }

  public int getBackupFrequency() {
    return backupFrequency;
  }
}
