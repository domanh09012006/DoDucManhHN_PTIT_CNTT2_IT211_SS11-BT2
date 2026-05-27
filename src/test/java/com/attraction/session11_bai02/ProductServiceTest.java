package com.attraction.session11_bai02;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductService.ProductRepository productRepository;

    @InjectMocks
    private ProductService productService;

    @Test
    void updateStock_addValidQuantity_updatesAndSaves() {
        ProductService.Product product = new ProductService.Product("P1", 10);
        when(productRepository.findById("P1")).thenReturn(Optional.of(product));
        when(productRepository.save(product)).thenReturn(product);

        int newStock = productService.updateStock("P1", 5);

        assertThat(newStock).isEqualTo(15);
        assertThat(product.getStockQuantity()).isEqualTo(15);
        verify(productRepository).save(product);
    }

    @Test
    void updateStock_subtractValidQuantity_updatesAndSaves() {
        ProductService.Product product = new ProductService.Product("P2", 10);
        when(productRepository.findById("P2")).thenReturn(Optional.of(product));
        when(productRepository.save(product)).thenReturn(product);

        int newStock = productService.updateStock("P2", -3);

        assertThat(newStock).isEqualTo(7);
        assertThat(product.getStockQuantity()).isEqualTo(7);
        verify(productRepository).save(product);
    }

    @Test
    void updateStock_subtractTooMuch_throwsAndDoesNotSave() {
        ProductService.Product product = new ProductService.Product("P3", 2);
        when(productRepository.findById("P3")).thenReturn(Optional.of(product));

        assertThatThrownBy(() -> productService.updateStock("P3", -5))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("negative");

        verify(productRepository, never()).save(product);
    }

    @Test
    void updateStock_productNotFound_throwsAndDoesNotSave() {
        when(productRepository.findById("P4")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> productService.updateStock("P4", 1))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Product not found");

        verify(productRepository, never()).save(org.mockito.Mockito.any());
    }

    @Test
    void updateStock_callsSaveWithUpdatedStock() {
        ProductService.Product product = new ProductService.Product("P5", 4);
        when(productRepository.findById("P5")).thenReturn(Optional.of(product));
        when(productRepository.save(product)).thenReturn(product);

        productService.updateStock("P5", 6);

        ArgumentCaptor<ProductService.Product> captor = ArgumentCaptor.forClass(ProductService.Product.class);
        verify(productRepository).save(captor.capture());
        assertThat(captor.getValue().getStockQuantity()).isEqualTo(10);
    }
}

