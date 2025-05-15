package com.example.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.example.demo.model.Product;
import com.example.demo.model.ProductType; // Імпортуємо ProductType
import java.util.List;
import java.util.stream.Collectors;

@Data // Генерує гетери, сетери, toString, equals, hashCode
@NoArgsConstructor // Конструктор без аргументів
@AllArgsConstructor // Конструктор з усіма аргументами
public class ProductDto {

    private Long id; // Часто корисно включати ID
    private String originalTitle;
    private ProductType type;
    private String description;
    private int reviewCount;
    private int averageRating;

    private Long creatorId; // ID творця
    private String creatorName; // Ім'я творця (опціонально, якщо потрібне на фронтенді)

    public static ProductDto fromEntity(Product product) {
        if (product == null) {
            return null;
        }
        ProductDto dto = new ProductDto();
        dto.setId(product.getId());
        dto.setOriginalTitle(product.getOriginalTitle());
        dto.setType(product.getType());
        dto.setDescription(product.getDescription());
        dto.setReviewCount(product.getReviewCount());
        dto.setAverageRating(product.getAverageRating());

        // Мапимо інформацію про творця
        if (product.getCreator() != null) {
            dto.setCreatorId(product.getCreator().getId());
            dto.setCreatorName(product.getCreator().getName()); // Отримуємо ім'я творця
        }

        return dto;
    }

    public static List<ProductDto> fromEntityList(List<Product> products) {
        if (products == null) {
            return null;
        }
        return products.stream()
                .map(ProductDto::fromEntity) // Використовуємо посилання на метод fromEntity
                .collect(Collectors.toList());
    }

}
