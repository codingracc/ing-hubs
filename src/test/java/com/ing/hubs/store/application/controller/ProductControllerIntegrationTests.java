package com.ing.hubs.store.application.controller;

import com.ing.hubs.store.application.dto.ProductResponse;
import com.ing.hubs.store.application.utils.ProductRequestMother;
import com.ing.hubs.store.domain.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureRestTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.client.RestTestClient;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Base64;
import java.util.Objects;

import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles("test")
@AutoConfigureRestTestClient
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ProductControllerIntegrationTests {

    @Autowired
    private RestTestClient notAuthenticatedClient;
    @Autowired
    private ProductRepository productRepository;

    private RestTestClient adminClient;
    private RestTestClient userClient;

    @BeforeEach
    void setUp() {
        productRepository.deleteAll();
        adminClient = withBasicAuth(notAuthenticatedClient, "admin", "admin");
        userClient = withBasicAuth(notAuthenticatedClient, "user", "user");
    }

    private static RestTestClient withBasicAuth(RestTestClient base, String username, String password) {
        String token = Base64.getEncoder().encodeToString((username + ":" + password).getBytes(StandardCharsets.UTF_8));
        return base.mutate()
                .defaultHeaders(headers -> headers.set(HttpHeaders.AUTHORIZATION, "Basic " + token))
                .build();
    }

    @Test
    void givenNoAuth_whenGetAll_thenReturnUnauthorized() {
        // given / when / then
        notAuthenticatedClient.get()
                .uri("/products")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isUnauthorized();
    }

    @Test
    void givenUserAuth_whenCreate_thenReturnForbidden() {
        // given / when / then
        userClient.post()
                .uri("/products")
                .contentType(MediaType.APPLICATION_JSON)
                .body(ProductRequestMother.aCreateProductRequest("Milk"))
                .exchange()
                .expectStatus().isForbidden();
    }

    @Test
    void givenValidCreateRequest_whenCreate_thenReturnCreated() {
        // given / when / then
        adminClient.post()
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
                adminClient.post()
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
        userClient.get()
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
        userClient.get()
                .uri("/products/{id}", 9999)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    void givenExistingProduct_whenGetByName_thenReturnOk() {
        // given
        ProductResponse created =
                adminClient.post()
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
        userClient.get()
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
        userClient.get()
                .uri("/products/by-name/{name}", "Missing")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    void givenTwoProducts_whenGetAll_thenReturnList() {
        // given
        ProductResponse p1 =
                adminClient.post()
                        .uri("/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(ProductRequestMother.aCreateProductRequest("Milk"))
                        .exchange()
                        .expectStatus().isCreated()
                        .expectBody(ProductResponse.class)
                        .returnResult()
                        .getResponseBody();

        ProductResponse p2 =
                adminClient.post()
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
                userClient.get()
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
                adminClient.post()
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
        adminClient.patch()
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
                adminClient.post()
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
        adminClient.patch()
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
                adminClient.post()
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
        adminClient.delete()
                .uri("/products/{id}", id)
                .exchange()
                .expectStatus().isNoContent();

        assertThat(productRepository.existsById(id)).isFalse();
    }

    @Test
    void givenMissingProduct_whenDeleteById_thenReturnNoContent() {
        // given / when / then
        adminClient.delete()
                .uri("/products/{id}", 9999)
                .exchange()
                .expectStatus().isNoContent();
    }

    @Test
    void givenExistingProduct_whenDeleteByName_thenReturnNoContent() {
        // given
        ProductResponse created =
                adminClient.post()
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
        adminClient.delete()
                .uri("/products/by-name/{name}", "Bread")
                .exchange()
                .expectStatus().isNoContent();

        assertThat(productRepository.existsById(created.id())).isFalse();
    }

    @Test
    void givenMissingProduct_whenDeleteByName_thenReturnNoContent() {
        // given / when / then
        adminClient.delete()
                .uri("/products/by-name/{name}", "Missing")
                .exchange()
                .expectStatus().isNoContent();
    }

    @Test
    void givenTwoProducts_whenDeleteAll_thenReturnNoContent() {
        // given
        adminClient.post()
                .uri("/products")
                .contentType(MediaType.APPLICATION_JSON)
                .body(ProductRequestMother.aCreateProductRequest("Milk"))
                .exchange()
                .expectStatus().isCreated();

        adminClient.post()
                .uri("/products")
                .contentType(MediaType.APPLICATION_JSON)
                .body(ProductRequestMother.aCreateProductRequest("Bread"))
                .exchange()
                .expectStatus().isCreated();

        assertThat(productRepository.count()).isEqualTo(2);

        // when / then
        adminClient.delete()
                .uri("/products")
                .exchange()
                .expectStatus().isNoContent();

        assertThat(productRepository.count()).isZero();
    }
}