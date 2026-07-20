package com.baranproject.backendmastery.service;

import com.baranproject.backendmastery.dto.ProductDTO;
import com.baranproject.backendmastery.entity.Product;
import com.baranproject.backendmastery.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * ProductService Unit Test Sınıfı
 * 
 * @ExtendWith(MockitoExtension.class): Mockito kütüphanesini JUnit 5 test sürecine entegre eder.
 * Sınıf bazlı mock tanımlamalarının çalışmasını sağlar.
 */
@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    /**
     * @Mock: Test edilen sınıfın bağımlı olduğu sınıfların sahte (mock) versiyonlarını üretir.
     * Bu sayede veritabanına bağlanmadan, sadece servis mantığını izole şekilde test edebiliriz.
     */
    @Mock
    private ProductRepository productRepository;

    /**
     * @InjectMocks: Yukarıda tanımlanan mock nesneleri otomatik olarak bu servise enjekte eder (Constructor injection ile).
     */
    @InjectMocks
    private ProductService productService;

    private Product dummyProduct;
    private ProductDTO dummyProductDTO;

    @BeforeEach
    void setUp() {
        dummyProduct = Product.builder()
                .id(1L)
                .name("MacBook Pro")
                .description("M3 Chip laptop")
                .price(new BigDecimal("85000.00"))
                .stockQuantity(10)
                .imageUrl("http://localhost:9000/product-images/macbook.jpg")
                .build();

        dummyProductDTO = ProductDTO.builder()
                .id(1L)
                .name("MacBook Pro")
                .description("M3 Chip laptop")
                .price(new BigDecimal("85000.00"))
                .stockQuantity(10)
                .imageUrl("http://localhost:9000/product-images/macbook.jpg")
                .build();
    }

    @Test
    @DisplayName("Tüm ürünleri getirme - Başarılı Senaryo")
    void getAllProducts_ShouldReturnProductDTOList() {
        // Given (Koşullar & Davranış tanımları)
        // Repository.findAll() çağrıldığında sahte olarak oluşturduğumuz listeyi dönecek şekilde ayarla.
        when(productRepository.findAll()).thenReturn(List.of(dummyProduct));

        // When (Eylem)
        List<ProductDTO> result = productService.getAllProducts();

        // Then (Sonuç doğrulamaları)
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).isEqualTo("MacBook Pro");
        assertThat(result.get(0).getPrice()).isEqualByComparingTo("85000.00");

        // Repository metodunun gerçekten 1 kez çağrıldığını doğrula
        verify(productRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("ID ile ürün bulma - Başarılı Senaryo")
    void getProductById_WhenProductExists_ShouldReturnProductDTO() {
        // Given
        when(productRepository.findById(1L)).thenReturn(Optional.of(dummyProduct));

        // When
        ProductDTO result = productService.getProductById(1L);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getName()).isEqualTo("MacBook Pro");

        verify(productRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("ID ile ürün bulma - Bulunamadı Hatası (Exception) Senaryosu")
    void getProductById_WhenProductDoesNotExist_ShouldThrowRuntimeException() {
        // Given
        when(productRepository.findById(99L)).thenReturn(Optional.empty());

        // When & Then
        // Metod çağrıldığında RuntimeException fırlatılması beklendiğini doğrula.
        assertThrows(RuntimeException.class, () -> productService.getProductById(99L));

        verify(productRepository, times(1)).findById(99L);
    }

    @Test
    @DisplayName("Ürün kaydetme - Başarılı Senaryo")
    void createProduct_ShouldSaveAndReturnProductDTO() {
        // Given
        when(productRepository.save(any(Product.class))).thenReturn(dummyProduct);

        // When
        ProductDTO result = productService.createProduct(dummyProductDTO);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getName()).isEqualTo("MacBook Pro");

        verify(productRepository, times(1)).save(any(Product.class));
    }
}
