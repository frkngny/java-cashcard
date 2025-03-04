package dev.southware.cashcard.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

import dev.southware.cashcard.model.CashCard;


public interface CashCardRepository extends ListCrudRepository<CashCard, Integer>, PagingAndSortingRepository<CashCard, Integer> {

	@Query("INSERT INTO CASHCARD (id, amount) VALUES (:id, :amount)")
	void create(Integer id, Double amount);
	
	CashCard findByIdAndOwner(Integer id, String owner);
	Page<CashCard> findByOwner(String owner, PageRequest pageRequest);
}
