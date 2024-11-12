package org.example.ecom.ManagedBeans;


import org.example.ecom.Service.ProductDaoImpl;
import org.example.ecom.model.Category;
import org.example.ecom.model.Product;
import org.primefaces.model.chart.BarChartModel;
import org.primefaces.model.chart.ChartSeries;
import org.primefaces.model.chart.PieChartModel;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import java.util.List;
import java.util.Map;

@ManagedBean(name = "adminStatistics")
@SessionScoped
public class AdminStatistics {

    private PieChartModel salesChart;
    private BarChartModel categoryChart;
    private ProductDaoImpl productService = new ProductDaoImpl(); // Assuming ProductService handles DB operations

    @PostConstruct
    public void init() {
        createSalesChart();
        createCategoryChart();
    }

    public PieChartModel getSalesChart() {
        return salesChart;
    }

    public BarChartModel getCategoryChart() {
        return categoryChart;
    }

    private void createSalesChart() {
        salesChart = new PieChartModel();

        List<Product> products = productService.getAll();
        for (Product product : products) {
            salesChart.set(product.getName(), product.getQuantity().doubleValue()); // Assuming quantity represents sales
        }

        // Customize the Pie Chart (optional)
        salesChart.setTitle("Product Sales Distribution");
        salesChart.setLegendPosition("w");
        salesChart.setShowDataLabels(true);
    }

    private void createCategoryChart() {
        categoryChart = new BarChartModel();

        ChartSeries categories = new ChartSeries();
        categories.setLabel("Categories");

        // Example: Group by categories and count products per category
        Map<Category, Long> categoryCounts = productService.countProductsByCategory();
        for (Map.Entry<Category, Long> entry : categoryCounts.entrySet()) {
            categories.set(entry.getKey(), entry.getValue());
        }

        categoryChart.addSeries(categories);
        categoryChart.setTitle("Products per Category");
//        categoryChart.setLegendPosition("ne");
        categoryChart.setAnimate(true);
    }
}
