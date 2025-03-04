package dev.southware.cashcard;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import java.net.URI;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;

import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;

import dev.southware.cashcard.model.CashCard;
import net.minidev.json.JSONArray;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class CashcardApplicationTests {
	
    @Autowired
    TestRestTemplate restTemplate;

    @Test
    void shouldReturnACashCardWhenDataIsSaved() {
        ResponseEntity<String> response = restTemplate.withBasicAuth("fg", "abc123")
        		.getForEntity("/api/cashcards/1", String.class);
        DocumentContext documentContext = JsonPath.parse(response.getBody());
        Number id = documentContext.read("$.id");
        Double amount = documentContext.read("$.amount");
        
        assertEquals(response.getStatusCode(), HttpStatus.OK);
        assertEquals(1, id);
        assertEquals(123.45, amount);
    }

    @Test
    void shouldNotReturnACashCardWithAnUnknownId() {
        ResponseEntity<String> response = restTemplate.withBasicAuth("fg", "abc123")
        		.getForEntity("/api/cashcards/1000", String.class);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertThat(response.getBody()).isBlank();
    }

    
    @DirtiesContext
    void shouldCreateANewCashCard() {
        CashCard newCashCard = new CashCard(null, 250.00, "fg");
        ResponseEntity<Void> createResponse = restTemplate.withBasicAuth("fg", "abc123")
        		.postForEntity("/api/cashcards", newCashCard, Void.class);
        assertEquals(HttpStatus.CREATED, createResponse.getStatusCode());

        URI locationOfNewCashCard = createResponse.getHeaders().getLocation();
        ResponseEntity<String> getResponse = restTemplate.withBasicAuth("fg", "abc123").getForEntity(locationOfNewCashCard, String.class);
        assertEquals(HttpStatus.OK, getResponse.getStatusCode());

        DocumentContext documentContext = JsonPath.parse(getResponse.getBody());
        Number id = documentContext.read("$.id");
        Double amount = documentContext.read("$.amount");

        assertNotEquals(null, id);
        assertEquals(250.00, amount);
    }

    @Test
    void shouldReturnAllCashCardsWhenListIsRequested() {
        ResponseEntity<String> response = restTemplate.withBasicAuth("fg", "abc123")
        		.getForEntity("/api/cashcards", String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        DocumentContext documentContext = JsonPath.parse(response.getBody());
        int cashCardCount = documentContext.read("$.length()");
        assertThat(cashCardCount).isEqualTo(3);

        JSONArray ids = documentContext.read("$..id");
        assertThat(ids).containsExactlyInAnyOrder(1, 2, 3);

        JSONArray amounts = documentContext.read("$..amount");
        assertThat(amounts).containsExactlyInAnyOrder(123.45, 1.00, 150.00);
    }

    @Test
    void shouldReturnAPageOfCashCards() {
        ResponseEntity<String> response = restTemplate.withBasicAuth("fg", "abc123")
        		.getForEntity("/api/cashcards?page=0&size=1", String.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());

        DocumentContext documentContext = JsonPath.parse(response.getBody());
        JSONArray page = documentContext.read("$[*]");
        assertThat(page.size()).isEqualTo(1);
    }

    @Test
    void shouldReturnASortedPageOfCashCards() {
        ResponseEntity<String> response = restTemplate.withBasicAuth("fg", "abc123")
        		.getForEntity("/api/cashcards?page=0&size=1&sort=amount,desc", String.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());

        DocumentContext documentContext = JsonPath.parse(response.getBody());
        JSONArray read = documentContext.read("$[*]");
        assertEquals(1, read.size());

        double amount = documentContext.read("$[0].amount");
        assertEquals(150.00, amount);
    }

    @Test
    void shouldReturnASortedPageOfCashCardsWithNoParametersAndUseDefaultValues() {
        ResponseEntity<String> response = restTemplate.withBasicAuth("fg", "abc123")
        		.getForEntity("/api/cashcards", String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        DocumentContext documentContext = JsonPath.parse(response.getBody());
        JSONArray page = documentContext.read("$[*]");
        assertThat(page.size()).isEqualTo(3);

        JSONArray amounts = documentContext.read("$..amount");
        assertThat(amounts).containsExactly(150.00, 123.45, 1.00);
    }
    
    @Test
	void shouldNotReturnACashCardWithoutAuthentication() {
		ResponseEntity<String> response = restTemplate.getForEntity("/api/cashcards/1", String.class);
		assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
	}
    
    @Test
    void shouldNotReturnACashCardWithBadCredentials() {
    	ResponseEntity<String> response = restTemplate.withBasicAuth("asd", "wqe").getForEntity("/api/cashcards/1", String.class);
		assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }
    
    @Test
    void shouldRejectUsersWhoAreNotCardOwners() {
        ResponseEntity<String> response = restTemplate
          .withBasicAuth("temp", "abc123")
          .getForEntity("/api/cashcards/1", String.class);
        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
    }
    
    @Test
    void shouldNotAllowAccessToCashCardsTheyDoNotOwn() {
        ResponseEntity<String> response = restTemplate.withBasicAuth("fg", "abc123")
        		.getForEntity("/api/cashcards/4", String.class);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }
}
