package com.ing.hubs.store.application.controller;

import com.ing.hubs.store.application.dto.ProductResponse;
import com.ing.hubs.store.application.utils.ProductRequestMother;
import com.ing.hubs.store.domain.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureRestTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.client.RestTestClient;

import java.util.Arrays;
import java.util.Objects;

import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureRestTestClient
class ProductControllerIntegrationTests {

    @Autowired
    private RestTestClient restTestClient;

    @Autowired
    private ProductRepository productRepository;

    @BeforeEach
    void setUp() {
        productRepository.deleteAll();
    }


    @Test
    void givenValidCreateRequest_whenCreate_thenReturnCreated() {
        // given / when / then
        restTestClient.post()
                .uri("/products")
                .contentType(MediaType.APPLICATION_JSON)
                .body(ProductRequestMother.aCreateProductRequest("Milk"))
                .exchange()
                .expectStatus().isCreated()
                .expectBody(ProductResponse.class)
                .value(p -> {
                    assertThat(p.id()).isNotNull();
                    assertThat(p.name()).isEqualTo("Milk");
                });
    }

    @Test
    void givenExistingProduct_whenGetById_thenReturnOk() {
        // given
        ProductResponse created =
                restTestClient.post()
                        .uri("/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(ProductRequestMother.aCreateProductRequest("Milk"))
                        .exchange()
                        .expectStatus().isCreated()
                        .expectBody(ProductResponse.class)
                        .returnResult()
                        .getResponseBody();

        assertThat(created).isNotNull();
        Long id = created.id();

        // when / then
        restTestClient.get()
                .uri("/products/{id}", id)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(ProductResponse.class)
                .isEqualTo(created);
    }

    @Test
    void givenMissingProduct_whenGetById_thenReturnNotFound() {
        // given / when / then
        restTestClient.get()
                .uri("/products/{id}", 9999)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    void givenExistingProduct_whenGetByName_thenReturnOk() {
        // given
        ProductResponse created =
                restTestClient.post()
                        .uri("/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(ProductRequestMother.aCreateProductRequest("Bread"))
                        .exchange()
                        .expectStatus().isCreated()
                        .expectBody(ProductResponse.class)
                        .returnResult()
                        .getResponseBody();

        assertThat(created).isNotNull();

        // when / then
        restTestClient.get()
                .uri("/products/by-name/{name}", "Bread")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(ProductResponse.class)
                .isEqualTo(created);
    }

    @Test
    void givenMissingProduct_whenGetByName_thenReturnNotFound() {
        // given / when / then
        restTestClient.get()
                .uri("/products/by-name/{name}", "Missing")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    void givenTwoProducts_whenGetAll_thenReturnList() {
        // given
        ProductResponse p1 =
                restTestClient.post()
                        .uri("/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(ProductRequestMother.aCreateProductRequest("Milk"))
                        .exchange()
                        .expectStatus().isCreated()
                        .expectBody(ProductResponse.class)
                        .returnResult()
                        .getResponseBody();

        ProductResponse p2 =
                restTestClient.post()
                        .uri("/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(ProductRequestMother.aCreateProductRequest("Bread"))
                        .exchange()
                        .expectStatus().isCreated()
                        .expectBody(ProductResponse.class)
                        .returnResult()
                        .getResponseBody();

        assertThat(p1).isNotNull();
        assertThat(p2).isNotNull();

        // when
        ProductResponse[] all =
                restTestClient.get()
                        .uri("/products")
                        .accept(MediaType.APPLICATION_JSON)
                        .exchange()
                        .expectStatus().isOk()
                        .expectBody(ProductResponse[].class)
                        .returnResult()
                        .getResponseBody();

        // then
        assertThat(all).isNotNull();
        assertThat(Arrays.stream(all).anyMatch(p -> Objects.equals(p.id(), p1.id()))).isTrue();
        assertThat(Arrays.stream(all).anyMatch(p -> Objects.equals(p.id(), p2.id()))).isTrue();
    }

    @Test
    void givenExistingProduct_whenUpdatePrice_thenReturnUpdated() {
        // given
        ProductResponse created =
                restTestClient.post()
                        .uri("/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(ProductRequestMother.aCreateProductRequest("Milk"))
                        .exchange()
                        .expectStatus().isCreated()
                        .expectBody(ProductResponse.class)
                        .returnResult()
                        .getResponseBody();

        assertThat(created).isNotNull();
        Long id = created.id();

        // when / then
        restTestClient.patch()
                .uri("/products/{id}/price", id)
                .contentType(MediaType.APPLICATION_JSON)
                .body(ProductRequestMother.anUpdatePriceRequest(12.5))
                .exchange()
                .expectStatus().isOk()
                .expectBody(ProductResponse.class)
                .value(p -> {
                    assertThat(p.id()).isEqualTo(id);
                    assertThat(p.price()).isEqualTo(12.5);
                });
    }

    @Test
    void givenExistingProduct_whenUpdateQuantity_thenReturnUpdated() {
        // given
        ProductResponse created =
                restTestClient.post()
                        .uri("/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(ProductRequestMother.aCreateProductRequest("Milk"))
                        .exchange()
                        .expectStatus().isCreated()
                        .expectBody(ProductResponse.class)
                        .returnResult()
                        .getResponseBody();

        assertThat(created).isNotNull();
        Long id = created.id();

        // when / then
        restTestClient.patch()
                .uri("/products/{id}/quantity", id)
                .contentType(MediaType.APPLICATION_JSON)
                .body(ProductRequestMother.anUpdateQuantityRequest(25))
                .exchange()
                .expectStatus().isOk()
                .expectBody(ProductResponse.class)
                .value(p -> {
                    assertThat(p.id()).isEqualTo(id);
                    assertThat(p.quantity()).isEqualTo(25);
                });
    }

    @Test
    void givenExistingProduct_whenDeleteById_thenReturnNoContent() {
        // given
        ProductResponse created =
                restTestClient.post()
                        .uri("/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(ProductRequestMother.aCreateProductRequest("Milk"))
                        .exchange()
                        .expectStatus().isCreated()
                        .expectBody(ProductResponse.class)
                        .returnResult()
                        .getResponseBody();

        assertThat(created).isNotNull();
        Long id = created.id();

        // when / then
        restTestClient.delete()
                .uri("/products/{id}", id)
                .exchange()
                .expectStatus().isNoContent();

        assertThat(productRepository.existsById(id)).isFalse();
    }

    @Test
    void givenMissingProduct_whenDeleteById_thenReturnNoContent() {
        // given / when / then
        restTestClient.delete()
                .uri("/products/{id}", 9999)
                .exchange()
                .expectStatus().isNoContent();
    }

    @Test
    void givenExistingProduct_whenDeleteByName_thenReturnNoContent() {
        // given
        ProductResponse created =
                restTestClient.post()
                        .uri("/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(ProductRequestMother.aCreateProductRequest("Bread"))
                        .exchange()
                        .expectStatus().isCreated()
                        .expectBody(ProductResponse.class)
                        .returnResult()
                        .getResponseBody();

        assertThat(created).isNotNull();

        // when / then
        restTestClient.delete()
                .uri("/products/by-name/{name}", "Bread")
                .exchange()
                .expectStatus().isNoContent();

        assertThat(productRepository.existsById(created.id())).isFalse();
    }

    @Test
    void givenMissingProduct_whenDeleteByName_thenReturnNoContent() {
        // given / when / then
        restTestClient.delete()
                .uri("/products/by-name/{name}", "Missing")
                .exchange()
                .expectStatus().isNoContent();
    }

    @Test
    void givenTwoProducts_whenDeleteAll_thenReturnNoContent() {
        // given
        restTestClient.post()
                .uri("/products")
                .contentType(MediaType.APPLICATION_JSON)
                .body(ProductRequestMother.aCreateProductRequest("Milk"))
                .exchange()
                .expectStatus().isCreated();

        restTestClient.post()
                .uri("/products")
                .contentType(MediaType.APPLICATION_JSON)
                .body(ProductRequestMother.aCreateProductRequest("Bread"))
                .exchange()
                .expectStatus().isCreated();

        assertThat(productRepository.count()).isEqualTo(2);

        // when / then
        restTestClient.delete()
                .uri("/products")
                .exchange()
                .expectStatus().isNoContent();

        assertThat(productRepository.count()).isZero();
    }
}
