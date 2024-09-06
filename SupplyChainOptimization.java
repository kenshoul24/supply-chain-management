package Logistic;

import org.moeaframework.algorithm.NSGAII;
import org.moeaframework.core.NondominatedSortingPopulation;
import org.moeaframework.core.Solution;
import org.moeaframework.core.variable.RealVariable;
import org.moeaframework.problem.AbstractProblem;
import org.moeaframework.core.operator.TournamentSelection;
import org.moeaframework.core.operator.real.SBX;
import org.moeaframework.core.operator.real.PolynomialMutation;
import org.moeaframework.util.TypedProperties;

import java.util.Random;

public class SupplyChainOptimization extends AbstractProblem {

    private static final int NUM_CUSTOMERS = 10;
    private static final int NUM_DISTRIBUTION_CENTERS = 5;

    private Customer[] customers = new Customer[NUM_CUSTOMERS];
    private DistributionCenter[] distributionCenters = new DistributionCenter[NUM_DISTRIBUTION_CENTERS];

    public SupplyChainOptimization() {
        super(NUM_DISTRIBUTION_CENTERS * 2 + NUM_CUSTOMERS, 3);

        // Khởi tạo vị trí ngẫu nhiên cho khách hàng
        Random random = new Random();
        for (int i = 0; i < NUM_CUSTOMERS; i++) {
            customers[i] = new Customer(random.nextDouble() * 100, random.nextDouble() * 100);
        }

        // Khởi tạo vị trí ngẫu nhiên cho trung tâm phân phối
        for (int i = 0; i < NUM_DISTRIBUTION_CENTERS; i++) {
            distributionCenters[i] = new DistributionCenter(random.nextDouble() * 100, random.nextDouble() * 100);
        }
    }

    @Override
    public void evaluate(Solution solution) {
        // Lấy các biến quyết định từ solution (vị trí trung tâm phân phối và phân bổ khách hàng)
        for (int i = 0; i < NUM_DISTRIBUTION_CENTERS; i++) {
            double x = ((RealVariable) solution.getVariable(i * 2)).getValue();
            double y = ((RealVariable) solution.getVariable(i * 2 + 1)).getValue();
            distributionCenters[i] = new DistributionCenter(x, y);
        }

        double[] customerAssignments = new double[NUM_CUSTOMERS];
        for (int i = NUM_DISTRIBUTION_CENTERS * 2; i < solution.getNumberOfVariables(); i++) {
            customerAssignments[i - NUM_DISTRIBUTION_CENTERS * 2] = ((RealVariable) solution.getVariable(i)).getValue();
        }

        // Tính chi phí vận chuyển, thời gian giao hàng và độ phủ mạng lưới
        double totalCost = 0;
        double totalTime = 0;
        double coverage = 0;

        for (int i = 0; i < NUM_CUSTOMERS; i++) {
            int assignedCenter = (int) customerAssignments[i];
            double distance = distributionCenters[assignedCenter].calculateDistance(customers[i].getX(), customers[i].getY());
            totalCost += distance * 10;
            totalTime += distance / 50;
        }

        // Độ phủ mạng lưới
        coverage = NUM_CUSTOMERS / (double) NUM_CUSTOMERS;

        // Đặt các giá trị mục tiêu vào solution
        solution.setObjective(0, totalCost);
        solution.setObjective(1, totalTime);
        solution.setObjective(2, -coverage);
    }

    @Override
    public Solution newSolution() {
        Solution solution = new Solution(NUM_DISTRIBUTION_CENTERS * 2 + NUM_CUSTOMERS, 3);
        for (int i = 0; i < NUM_DISTRIBUTION_CENTERS * 2; i++) {
            solution.setVariable(i, new RealVariable(0, 100));
        }
        for (int i = NUM_DISTRIBUTION_CENTERS * 2; i < solution.getNumberOfVariables(); i++) {
            solution.setVariable(i, new RealVariable(0, NUM_DISTRIBUTION_CENTERS - 1));
        }
        return solution;
    }

    public static void main(String[] args) {
        // Thiết lập thuật toán NSGA-II
        SupplyChainOptimization problem = new SupplyChainOptimization();
        NondominatedSortingPopulation population = new NondominatedSortingPopulation();
        TypedProperties properties = new TypedProperties();

        // Sử dụng SBX crossover và Polynomial mutation
        NSGAII algorithm = new NSGAII(
                population,
                new TournamentSelection(2),
                new SBX(1.0, 25.0),
                new PolynomialMutation(20.0),
                problem,
                properties.getInt("populationSize", 100)
        );

        // Chạy tối ưu hóa trong 1000 thế hệ
        int maxGenerations = 1000;
        for (int generation = 0; generation < maxGenerations; generation++) {
            algorithm.step();
        }

        // In ra các giải pháp Pareto front
        System.out.println("Các giải pháp Pareto tối ưu:");
        for (Solution solution : population) {
            System.out.println("Chi phí: " + solution.getObjective(0) + " | Thời gian giao hàng: " + solution.getObjective(1) + " | Độ phủ: " + (-solution.getObjective(2)));
        }

        algorithm.terminate();
    }
}
