package dev.southware.cashcard.controller;

import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;


import com.fasterxml.jackson.databind.ObjectMapper;

import dev.southware.cashcard.model.CashCard;
import dev.southware.cashcard.repository.CashCardRepository;

@WebMvcTest(CashCardController.class)
//@DirtiesContext(classMode = ClassMode.AFTER_EACH_TEST_METHOD)
public class CashCardControllerTest {

	@Autowired
	MockMvc mockMvc;

	@Autowired
	ObjectMapper objectMapper;

	@MockitoBean
	CashCardRepository repository;

	private final List<CashCard> cards = new ArrayList<>();

	@BeforeEach
	void setup() {
		cards.clear();
		cards.add(new CashCard(1, 100.0, "fg"));
		cards.add(new CashCard(2, 200.0, "fg"));
	}

	@Test
	void shouldFindAllCashCards() throws Exception {
		when(repository.findAll(PageRequest.of(0, 2, Sort.by(Sort.Direction.DESC, "amount"))).getContent())
				.thenReturn(cards);
		mockMvc.perform(get("/api/cashcards?page=0&size=1&sort=amount,desc")).andExpect(status().isOk())
				.andExpect(jsonPath("$.size()", is(1)));

	}

	@Test
	void shouldFindCashCardById() throws Exception {
		CashCard card = cards.get(0);
		when(repository.findById(1)).thenReturn(Optional.of(card));
		mockMvc.perform(get("/api/cashcards/1")).andExpect(status().isOk()).andExpect(jsonPath("$.id", is(card.id())))
				.andExpect(jsonPath("$.amount", is(card.amount())));
	}

	@Test
	void shouldCreateCashCard() throws Exception {
		CashCard card = new CashCard(null, 300.0, "fg");
		mockMvc.perform(
				post("/api/cashcards").contentType("application/json").content(objectMapper.writeValueAsString(card))
		).andExpect(status().isCreated());
	}

	@Test
	void shouldReturnPaginatedCashCards() throws Exception {
		when(repository.findAll(PageRequest.of(0, 1, Sort.by(Sort.Direction.DESC, "amount"))).getContent())
				.thenReturn(cards);
		mockMvc.perform(get("/api/cashcards?page=0&size=1&sort=amount,desc")).andExpect(status().isOk())
				.andExpect(jsonPath("$.size()", is(1)));
	}
}
