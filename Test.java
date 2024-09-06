package Logistic;

import org.moeaframework.algorithm.NSGAII;
import org.moeaframework.core.NondominatedSortingPopulation;
import org.moeaframework.core.Solution;
import org.moeaframework.core.operator.TournamentSelection;
import org.moeaframework.core.operator.real.SBX;
import org.moeaframework.core.operator.real.PolynomialMutation;
import org.moeaframework.util.TypedProperties;

public class Test {

    public static void main(String[] args) {
        // Khởi tạo bài toán tối ưu hóa chuỗi cung ứng
        SupplyChainOptimization problem = new SupplyChainOptimization();

        // Thiết lập quần thể không bị chi phối (Pareto)
        NondominatedSortingPopulation population = new NondominatedSortingPopulation();

        // Các thuộc tính cấu hình cho thuật toán
        TypedProperties properties = new TypedProperties();

        // Sử dụng thuật toán NSGA-II với các toán tử di truyền SBX và đột biến PolynomialMutation
        NSGAII algorithm = new NSGAII(
                population,
                new TournamentSelection(2),   // Tournament selection với số lượng là 2
                new SBX(1.0, 25.0),           // SBX crossover với xác suất và phân bố
                new PolynomialMutation(20.0), // Đột biến đa thức
                problem,                      // Bài toán tối ưu hóa chuỗi cung ứng
                properties.getInt("populationSize", 100) // Kích thước quần thể là 100
        );

        // Chạy thuật toán tối ưu hóa trong 500 thế hệ
        int maxGenerations = 500;
        for (int generation = 0; generation < maxGenerations; generation++) {
            algorithm.step(); // Thực hiện một bước của thuật toán
        }

        // In ra các giải pháp Pareto-optimal
        System.out.println("Các giải pháp Pareto tối ưu:");
        for (Solution solution : population) {
            System.out.println("Chi phí: " + solution.getObjective(0) +
                    " | Thời gian giao hàng: " + solution.getObjective(1) +
                    " | Độ phủ: " + (-solution.getObjective(2)));  // Phủ nhận để hiển thị đúng
        }

        // Kết thúc thuật toán
        algorithm.terminate();

        // Kiểm tra xem có bao nhiêu giải pháp Pareto đã được tìm thấy
        if (population.size() > 0) {
            System.out.println("Bài toán đã chạy thành công và tìm thấy các giải pháp Pareto.");
        } else {
            System.out.println("Không tìm thấy giải pháp nào.");
        }
    }
}
