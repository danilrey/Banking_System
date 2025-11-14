package com.example.bank.domain.card.service;

import com.example.bank.domain.card.factory.DebitCardFactory;
import com.example.bank.domain.card.model.Card;
import com.example.bank.domain.card.model.CardType;
import com.example.bank.domain.card.repository.CardRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class DebitCardService implements CardService {
    private final CardRepository cardRepository;
    private final DebitCardFactory debitCardFactory;

    @Transactional(readOnly = true)
    public List<Card> getAllCards() {
        return cardRepository.findAll();
    }

    @Transactional
    public Card addCard(Card card) {
        if (card != null) {
            return cardRepository.save(card);
        }
        return null;
    }

    @Transactional
    public void removeCard(Long cardId) {
        cardRepository.deleteById(cardId);
    }

    @Transactional(readOnly = true)
    public Optional<Card> getCard(Long cardId) {
        return cardRepository.findById(cardId);
    }

    public CardType getType() {
        return CardType.DEBIT;
    }

    public Card createCard(Long accountId) {
        return debitCardFactory.createCard(accountId);
    }
}
