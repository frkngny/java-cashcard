package dev.southware.cashcard.loader;

import java.io.IOException;
import java.io.InputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import dev.southware.cashcard.model.CashCards;
import dev.southware.cashcard.repository.CashCardRepository;


@Component
public class CashCardLoader implements CommandLineRunner {
	private static final Logger log = LoggerFactory.getLogger(CashCardLoader.class);
	private final CashCardRepository repository;
	private final ObjectMapper objectMapper;

	public CashCardLoader(CashCardRepository repository, ObjectMapper objectMapper) {
		this.repository = repository;
		this.objectMapper = objectMapper;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void run(String... args) throws Exception {
		if (repository.count() == 0) {
			try (InputStream inputStream = TypeReference.class.getResourceAsStream("/data/cashCards.json")) {
				CashCards cards = objectMapper.readValue(inputStream, CashCards.class);
				repository.saveAll((Iterable) cards.cashCards());
				log.info("Loaded {} cards from data/cards.json", cards.cashCards().size());
			} catch (IOException e) {
				log.error("Failed to load cards from data/cards.json", e);
			}
		}
	}
}
