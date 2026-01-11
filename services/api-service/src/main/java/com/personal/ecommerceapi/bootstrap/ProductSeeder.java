package com.personal.ecommerceapi.bootstrap;

import com.personal.ecommerceapi.entity.Product;
import com.personal.ecommerceapi.repository.ProductRepository;
import com.personal.ecommerceapi.util.Brand;
import com.personal.ecommerceapi.util.Category;
import com.personal.ecommerceapi.util.Color;
import com.personal.ecommerceapi.util.Gender;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.math.BigDecimal;
import java.util.List;

@Configuration
public class ProductSeeder {

    @Bean
    public org.springframework.boot.CommandLineRunner seedProducts(ProductRepository productRepository) {
        return args -> {

            if (productRepository.count() > 0) {
                return;
            }

            List<Product> products = List.of(

                    new Product(null, Category.SNEAKERS, "https://example.com/ultraboost.jpg",
                            Brand.ADIDAS, 42, Color.BLACK, Gender.MALE,
                            "Adidas Ultraboost 1.0", "Running shoe premium",
                            BigDecimal.valueOf(699.99), 30, true, null, null),

                    new Product(null, Category.SNEAKERS, "https://example.com/ultraboost-light.jpg",
                            Brand.ADIDAS, 39, Color.WHITE, Gender.FEMALE,
                            "Adidas Ultraboost Light", "Ultra light running",
                            BigDecimal.valueOf(649.99), 25, true, null, null),

                    new Product(null, Category.SNEAKERS, "https://example.com/samba.jpg",
                            Brand.ADIDAS, 41, Color.WHITE, Gender.MALE,
                            "Adidas Samba OG", "Classic street sneaker",
                            BigDecimal.valueOf(499.99), 40, true, null, null),

                    new Product(null, Category.SNEAKERS, "https://example.com/gazelle.jpg",
                            Brand.ADIDAS, 38, Color.BLUE, Gender.FEMALE,
                            "Adidas Gazelle", "Retro lifestyle sneaker",
                            BigDecimal.valueOf(459.99), 35, true, null, null),

                    new Product(null, Category.SNEAKERS, "https://example.com/stansmith.jpg",
                            Brand.ADIDAS, 43, Color.WHITE, Gender.MALE,
                            "Adidas Stan Smith", "Minimal classic design",
                            BigDecimal.valueOf(479.99), 50, true, null, null),

                    new Product(null, Category.SNEAKERS, "https://example.com/nmd.jpg",
                            Brand.ADIDAS, 44, Color.BLACK, Gender.MALE,
                            "Adidas NMD R1", "Urban running-inspired",
                            BigDecimal.valueOf(599.99), 20, true, null, null),

                    new Product(null, Category.SNEAKERS, "https://example.com/forum.jpg",
                            Brand.ADIDAS, 37, Color.WHITE, Gender.FEMALE,
                            "Adidas Forum Low", "Basketball heritage",
                            BigDecimal.valueOf(529.99), 30, true, null, null),

                    new Product(null, Category.RUNNERS, "https://example.com/supernova.jpg",
                            Brand.ADIDAS, 42, Color.GREY, Gender.MALE,
                            "Adidas Supernova", "Daily running comfort",
                            BigDecimal.valueOf(579.99), 25, true, null, null),

                    new Product(null, Category.RUNNERS, "https://example.com/adizero.jpg",
                            Brand.ADIDAS, 43, Color.RED, Gender.MALE,
                            "Adidas Adizero Boston", "Performance runner",
                            BigDecimal.valueOf(749.99), 15, true, null, null),

                    new Product(null, Category.RUNNERS, "https://example.com/solar.jpg",
                            Brand.ADIDAS, 40, Color.BLACK, Gender.FEMALE,
                            "Adidas Solar Glide", "Stability running shoe",
                            BigDecimal.valueOf(689.99), 18, true, null, null),

                    new Product(null, Category.BOOTS, "https://example.com/terrex.jpg",
                            Brand.ADIDAS, 44, Color.BROWN, Gender.MALE,
                            "Adidas Terrex AX4", "Hiking outdoor shoe",
                            BigDecimal.valueOf(629.99), 22, true, null, null),

                    new Product(null, Category.BOOTS, "https://example.com/terrex-swift.jpg",
                            Brand.ADIDAS, 39, Color.GREEN, Gender.FEMALE,
                            "Adidas Terrex Swift", "Trail performance",
                            BigDecimal.valueOf(659.99), 20, true, null, null),

                    new Product(null, Category.SANDALS, "https://example.com/adilette.jpg",
                            Brand.ADIDAS, 42, Color.BLACK, Gender.MALE,
                            "Adidas Adilette Comfort", "Comfort slides",
                            BigDecimal.valueOf(199.99), 60, true, null, null),

                    new Product(null, Category.SANDALS, "https://example.com/aqua.jpg",
                            Brand.ADIDAS, 38, Color.BLUE, Gender.FEMALE,
                            "Adidas Adilette Aqua", "Beach slides",
                            BigDecimal.valueOf(179.99), 55, true, null, null),

                    new Product(null, Category.SANDALS, "https://example.com/yeezy-slide.jpg",
                            Brand.ADIDAS, 43, Color.GREY, Gender.MALE,
                            "Adidas Yeezy Slide", "Minimalist slides",
                            BigDecimal.valueOf(349.99), 10, true, null, null),

                    new Product(null, Category.SNEAKERS, "https://example.com/predator.jpg",
                            Brand.ADIDAS, 44, Color.BLACK, Gender.MALE,
                            "Adidas Predator Accuracy", "Football precision",
                            BigDecimal.valueOf(899.99), 12, true, null, null),

                    new Product(null, Category.SNEAKERS, "https://example.com/x-speed.jpg",
                            Brand.ADIDAS, 42, Color.SILVER, Gender.MALE,
                            "Adidas X Speedportal", "Speed football boot",
                            BigDecimal.valueOf(879.99), 10, true, null, null),

                    new Product(null, Category.SNEAKERS, "https://example.com/campus.jpg",
                            Brand.ADIDAS, 37, Color.GREY, Gender.FEMALE,
                            "Adidas Campus 00s", "Modern retro",
                            BigDecimal.valueOf(459.99), 28, true, null, null),

                    new Product(null, Category.SNEAKERS, "https://example.com/zx22.jpg",
                            Brand.ADIDAS, 41, Color.BLACK, Gender.MALE,
                            "Adidas ZX 22 Boost", "Modern cushioning",
                            BigDecimal.valueOf(619.99), 24, true, null, null),

                    new Product(null, Category.RUNNERS, "https://example.com/eq21.jpg",
                            Brand.ADIDAS, 39, Color.WHITE, Gender.FEMALE,
                            "Adidas EQ21 Run", "Entry running shoe",
                            BigDecimal.valueOf(399.99), 45, true, null, null)
            );

            productRepository.saveAll(products);
            System.out.println("Seeded " + products.size() + " Adidas products");
        };
    }
}
