import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Locale;

/** Система учета оплат и точка входа. */
public class CloudManager {
  private static int totalOrdersCount = 0;
  private static double totalRevenue = 0.0;

  /** Один вызов соответствует одной отдельной успешной оплате. */
  public static void registerPayment(double amount) {
    if (!Double.isFinite(amount) || amount < 0) {
      throw new IllegalArgumentException("Сумма оплаты должна быть конечной и неотрицательной.");
    }
    double newRevenue = BigDecimal.valueOf(totalRevenue).add(BigDecimal.valueOf(amount))
        .setScale(2, RoundingMode.HALF_UP).doubleValue();
    if (!Double.isFinite(newRevenue) || totalOrdersCount == Integer.MAX_VALUE) {
      throw new IllegalStateException("Превышен предел статистики.");
    }
    totalRevenue = newRevenue;
    totalOrdersCount++;
  }

  public static int getTotalOrdersCount() {
    return totalOrdersCount;
  }

  public static double getTotalRevenue() {
    return totalRevenue;
  }

  public static void printGlobalStats() {
    System.out.println("\n=== Общая статистика ===");
    System.out.println("Оплаченных заказов: " + totalOrdersCount);
    System.out.printf(Locale.ROOT, "Суммарная выручка: %.2f руб.%n", totalRevenue);
  }

  public static void main(String[] args) {
    BackupOption backup = new BackupOption("Резервное копирование", 10.0);
    CloudStorage basic = new CloudStorage("Личные файлы", 100.0);
    CloudStorage medium = new CloudStorage("Учебные проекты", 100.0, "512 ГБ");
    CloudStorage large = new CloudStorage("Архив команды", 100.0, "2 ТБ");

    System.out.println("Вариант 17. Облачное хранилище. Расчет за один день.");
    double basicPrice = basic.calculateFinalPrice();
    double mediumPrice = medium.calculateFinalPrice(backup);
    double largePrice = large.calculateFinalPrice(backup, 3);

    System.out.printf(Locale.ROOT, "%s [%s]: %.2f руб.%n",
        basic.getName(), basic.getTariff(), basicPrice);
    System.out.printf(Locale.ROOT, "%s [%s], бэкап 1 раз/день: %.2f руб.%n",
        medium.getName(), medium.getTariff(), mediumPrice);
    System.out.printf(Locale.ROOT, "%s [%s], бэкап 3 раза/день: %.2f руб.%n",
        large.getName(), large.getTariff(), largePrice);

    // Демонстрация названий методов из таблицы. Расчеты не являются оплатами.
    System.out.printf(Locale.ROOT, "enableBackup(backup): %.2f руб.%n",
        medium.enableBackup(backup));
    System.out.printf(Locale.ROOT, "enableBackup(backup, 3): %.2f руб.%n",
        large.enableBackup(backup, 3));

    registerPayment(basicPrice);
    registerPayment(mediumPrice);
    registerPayment(largePrice);
    printGlobalStats();
  }
}
