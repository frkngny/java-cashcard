package dev.southware.cashcard.repository;

import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.ListCrudRepository;

import dev.southware.cashcard.model.CashCard;


public interface CashCardRepository extends ListCrudRepository<CashCard, Long> {

	@Query("INSERT INTO CASHCARD (id, amount) VALUES (:id, :amount)")
	void create(Integer id, Double amount);
}
