package dev.southware.cashcard.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import jakarta.validation.constraints.PositiveOrZero;

@Table("cashcard")
public record CashCard(
		@Id
		Long id,
		@PositiveOrZero
		Double amount
		) {

}
