package dev.southware.cashcard.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import dev.southware.cashcard.model.CashCard;
import dev.southware.cashcard.repository.CashCardRepository;

@RestController
@RequestMapping("/api/cashcards")
public class CashCardController {
	
	private final CashCardRepository cashCardRepository;
	
	CashCardController(CashCardRepository cashCardRepository) {
        this.cashCardRepository = cashCardRepository;
    }
	
	@GetMapping("")
	List<CashCard> findAll() {
		return cashCardRepository.findAll();
	}
	
	@GetMapping("/{id}")
	CashCard findById(@PathVariable Long id) {
		return cashCardRepository.findById(id).orElse(null);
	}
}
