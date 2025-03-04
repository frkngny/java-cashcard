package dev.southware.cashcard.controller;

import java.net.URI;
import java.security.Principal;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

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
	private Iterable<CashCard> findAll(Pageable pageable, Principal principal) {
		Page<CashCard> page = cashCardRepository.findByOwner(
				principal.getName(),
				PageRequest.of(
						pageable.getPageNumber(),
						pageable.getPageSize(),
						pageable.getSortOr(Sort.by(Sort.Direction.DESC, "amount"))
				)
		);
		return page.getContent();
	}

	@GetMapping("/{id}")
	private ResponseEntity<CashCard> findById(@PathVariable Integer id, Principal principal) {
		Optional<CashCard> optionalCashCard = Optional.ofNullable(cashCardRepository.findByIdAndOwner(id, principal.getName()));
		if (optionalCashCard.isPresent()) {
			return ResponseEntity.ok(optionalCashCard.get());
		}
		return ResponseEntity.notFound().build();
	}

	@ResponseStatus(HttpStatus.CREATED)
	@PostMapping("")
	private ResponseEntity<Void> create(@RequestBody CashCard cashCard,
			UriComponentsBuilder uriBuilder,
			Principal principal) {
		CashCard cashCardWithOwner = new CashCard(null, cashCard.amount(), principal.getName());
		CashCard savedCashCard = cashCardRepository.save(cashCardWithOwner);
		URI cardURI = uriBuilder.path("/api/cashcards/{id}").buildAndExpand(savedCashCard.id()).toUri();
		return ResponseEntity.created(cardURI).build();
	}
}
