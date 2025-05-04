package com.example.demo.dto;

// Імпортуємо Lombok для гетерів/сетерів та конструкторів
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.example.demo.model.Product;
import com.example.demo.model.ProductType; // Імпортуємо ProductType

import java.util.List;
import java.util.stream.Collectors;
// Можливо, знадобляться інші імпорти, якщо DTO включає ID творця тощо.


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

    // *** Замість повного об'єкта User creator, можемо додати ID або ім'я творця ***
    private Long creatorId; // ID творця
    private String creatorName; // Ім'я творця (опціонально, якщо потрібне на фронтенді)

    // Ми НЕ включаємо сюди lists коментарів, користувачів, що додали в обране,
    // щоб уникнути циклів та передавати лише потрібні дані.
    // Якщо коментарі потрібні, їх краще отримувати окремим запитом або мати спеціальний DTO для детального перегляду продукту.
    // *** ДОДАНО: Статичний метод для мапінгу Product -> ProductDto ***
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
            // Перевірка, чи creator ініціалізований та не є просто порожнім проксі
            // (Хоча статичний метод мапінгу зазвичай викликається після отримання сутності,
            // якщо Creator LAZY і не JOIN FETCH, product.getCreator() може повернути проксі.
            // Jackson зазвичай справляється з ініціалізованими проксі, але чистий DTO кращий).
            // В даному випадку, оскільки ми перейшли на DTO, проксі не буде серіалізуватися напряму.
            // Просто перевіряємо на null.
            dto.setCreatorId(product.getCreator().getId());
            // Отримати ім'я творця з LAZY зв'язку може спричинити N+1 проблему, якщо не використовується JOIN FETCH.
            // Якщо ім'я творця часто потрібне в списках, розгляньте JOIN FETCH у репозиторії
            // для методів типу findAll або findByType.
            dto.setCreatorName(product.getCreator().getName()); // Отримуємо ім'я творця
        }

        return dto;
    }

    // *** Можна додати метод для мапінгу List<Product> -> List<ProductDto> ***
    public static List<ProductDto> fromEntityList(List<Product> products) {
        if (products == null) {
            return null;
        }
        return products.stream()
                .map(ProductDto::fromEntity) // Використовуємо посилання на метод fromEntity
                .collect(Collectors.toList());
    }

}
