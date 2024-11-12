package org.example.ecom.Service;

import org.example.ecom.model.Product;

import java.util.List;

public class StatisticsService {

    private ProductDaoImpl productService = new ProductDaoImpl();

    // Get the total number of products
    public Long getTotalProducts() {
        List<Product> products = productService.getAll();
        return (long) products.size();
    }

    // Get the total sales (assuming you have a field 'sales' in Product model)
    public Double getTotalSales() {
        List<Product> products = productService.getAll();
        return products.stream()
                .mapToDouble(p -> p.getPrice() * p.getQuantity()) // Sales = price * quantity
                .sum();
    }

    // Get the average price of products
    public Double getAveragePrice() {
        List<Product> products = productService.getAll();
        return products.stream()
                .mapToDouble(Product::getPrice)
                .average()
                .orElse(0);
    }
}