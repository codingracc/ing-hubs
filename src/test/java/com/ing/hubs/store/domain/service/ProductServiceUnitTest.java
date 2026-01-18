package com.ing.hubs.store.domain.service;

import com.ing.hubs.store.domain.entity.Product;
import com.ing.hubs.store.domain.exception.Conflict;
import com.ing.hubs.store.domain.exception.NotFound;
import com.ing.hubs.store.domain.repository.ProductRepository;
import com.ing.hubs.store.domain.utils.ProductMother;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;


@ExtendWith(MockitoExtension.class)
public class ProductServiceUnitTest {

    @Mock
    private ProductRepository repository;
    @InjectMocks
    private ProductService service;

    @Test
    void getProductById_whenProductExists_returnsProduct() {
        // given
        final Long id = 1L;
        final Product product = ProductMother.aProductEntity(id);
        given(repository.findById(id)).willReturn(Optional.of(product));

        // when
        final Product result = service.getProductById(id);

        // then
        assertThat(result).isSameAs(product);
        then(repository).should().findById(id);
        then(repository).shouldHaveNoMoreInteractions();
    }

    @Test
    void getProductById_whenMissing_throwsNotFound() {
        // given
        final Long id = 1L;
        given(repository.findById(id)).willReturn(Optional.empty());

        // when / then
        assertThatThrownBy(() -> service.getProductById(id))
                .isInstanceOf(NotFound.class)
                .hasMessageContaining("Product not found with id: " + id);

        then(repository).should().findById(id);
        then(repository).shouldHaveNoMoreInteractions();
    }

    @Test
    void createProduct_whenNameAlreadyExists_throwsConflict() {
        // given
        final Product product = ProductMother.aProductEntity(null, "Milk");
        given(repository.existsByName("Milk")).willReturn(true);

        // when / then
        assertThatThrownBy(() -> service.createProduct(product))
                .isInstanceOf(Conflict.class)
                .hasMessageContaining("Product already exists with name: Milk");

        then(repository).should().existsByName("Milk");
        then(repository).shouldHaveNoMoreInteractions();
    }

    @Test
    void createProduct_whenValid_savesAndReturnsCreatedEntity() {
        // given
        final Product toCreate = ProductMother.aProductEntity(null, "Milk");
        final Product created = ProductMother.aProductEntity(10L, "Milk");

        given(repository.existsByName("Milk")).willReturn(false);
        given(repository.save(toCreate)).willReturn(created);

        // when
        final Product result = service.createProduct(toCreate);

        // then
        assertThat(result).isSameAs(created);
        then(repository).should().existsByName("Milk");
        then(repository).should().save(toCreate);
        then(repository).shouldHaveNoMoreInteractions();
    }

    @Test
    void updateProductPrice_whenProductExists_updatesUsingWithAndSaves() {
        // given
        final Long id = 1L;
        final Product existing = ProductMother.aProductEntity(id).withPrice(5.0);
        given(repository.findById(id)).willReturn(Optional.of(existing));
        given(repository.save(any(Product.class))).willAnswer(inv -> inv.getArgument(0));

        // when
        final Product result = service.updateProductPrice(id, 12.5);

        // then
        assertThat(result.getId()).isEqualTo(id);
        assertThat(result.getPrice()).isEqualTo(12.5);

        ArgumentCaptor<Product> captor = ArgumentCaptor.forClass(Product.class);
        then(repository).should().findById(id);
        then(repository).should().save(captor.capture());
        assertThat(captor.getValue().getPrice()).isEqualTo(12.5);
        then(repository).shouldHaveNoMoreInteractions();
    }

    @Test
    void updateProductQuantity_whenProductExists_updatesUsingWithAndSaves() {
        // given
        final Long id = 1L;
        final Product existing = ProductMother.aProductEntity(id).withQuantity(10);
        given(repository.findById(id)).willReturn(Optional.of(existing));
        given(repository.save(any(Product.class))).willAnswer(inv -> inv.getArgument(0));

        // when
        final Product result = service.updateProductQuantity(id, 25);

        // then
        assertThat(result.getId()).isEqualTo(id);
        assertThat(result.getQuantity()).isEqualTo(25);

        then(repository).should().findById(id);
        then(repository).should().save(any(Product.class));
        then(repository).shouldHaveNoMoreInteractions();
    }

    @Test
    void deleteProductById_whenDoesNotExist_doesNothing() {
        // given
        final Long id = 1L;
        given(repository.existsById(id)).willReturn(false);

        // when
        service.deleteProductById(id);

        // then
        then(repository).should().existsById(id);
        then(repository).shouldHaveNoMoreInteractions();
    }

    @Test
    void deleteProductById_whenExists_deletes() {
        // given
        final Long id = 1L;
        given(repository.existsById(id)).willReturn(true);

        // when
        service.deleteProductById(id);

        // then
        then(repository).should().existsById(id);
        then(repository).should().deleteById(id);
        then(repository).shouldHaveNoMoreInteractions();
    }
}
