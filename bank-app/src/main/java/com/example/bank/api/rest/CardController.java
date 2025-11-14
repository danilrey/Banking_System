package com.example.bank.api.rest;

import com.example.bank.domain.card.factory.CardFactory;
import com.example.bank.domain.card.model.Card;
import com.example.bank.domain.card.repository.CardRepository;
import com.example.bank.domain.card.service.DebitCardService;
import lombok.Data;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Controller
@RequiredArgsConstructor
public class CardController {
    private final CardFactory debitCardFactory;
    private final CardRepository cardRepository;
    private final DebitCardService debitCardService;

    @PostMapping("/api/cards")
    @PreAuthorize("hasRole('USER')")
    @ResponseBody
    public ResponseEntity<CardResponse> createCard(@RequestBody CreateCardRequest request){
        Card card = debitCardFactory.createCard(request.getAccountId());
        Card saved = cardRepository.save(card);
        CardResponse response = new CardResponse();
        response.setId(saved.getId());
        response.setMaskedCardNumber(mask(saved.getCardNumber()));
        response.setExpiryMonth(saved.getExpiryMonth());
        response.setExpiryYear(saved.getExpiryYear());
        response.setStatus(saved.getStatus());
        return ResponseEntity.status(201).body(response);
    }

    @GetMapping("/api/cards")
    @PreAuthorize("hasRole('USER')")
    @ResponseBody
    public ResponseEntity<List<CardResponse>> getAllCards(){
        List<Card> cards = debitCardService.getAllCards();
        List<CardResponse> responses = cards.stream().map(card -> {
            CardResponse r = new CardResponse();
            r.setId(card.getId());
            r.setMaskedCardNumber(mask(card.getCardNumber()));
            r.setExpiryMonth(card.getExpiryMonth());
            r.setExpiryYear(card.getExpiryYear());
            r.setStatus(card.getStatus());
            return r;
        }).toList();
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/api/cards/{id}")
    @PreAuthorize("hasRole('USER')")
    @ResponseBody
    public ResponseEntity<CardResponse> getCard(@PathVariable Long id){
        Optional<Card> cardOpt = debitCardService.getCard(id);
        if (cardOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        Card card = cardOpt.get();
        CardResponse response = new CardResponse();
        response.setId(card.getId());
        response.setMaskedCardNumber(mask(card.getCardNumber()));
        response.setExpiryMonth(card.getExpiryMonth());
        response.setExpiryYear(card.getExpiryYear());
        response.setStatus(card.getStatus());
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/api/cards/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @ResponseBody
    public ResponseEntity<Void> deleteCard(@PathVariable Long id){
        debitCardService.removeCard(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/ui/cards")
    @PreAuthorize("hasRole('USER')")
    public String getCardsPage(Model model) {
        List<Card> cards = debitCardService.getAllCards();
        List<CardDto> cardDtos = cards.stream().map(this::toDto).toList();
        model.addAttribute("cards", cardDtos);
        return "cards";
    }

    @PostMapping("/ui/cards")
    @PreAuthorize("hasRole('USER')")
    public String createCard(@RequestParam Long accountId) {
        Card card = debitCardService.createCard(accountId);
        debitCardService.addCard(card);
        return "redirect:/ui/cards";
    }

    private CardDto toDto(Card card) {
        CardDto dto = new CardDto();
        dto.setId(card.getId());
        dto.setMaskedCardNumber(mask(card.getCardNumber()));
        dto.setAccountId(card.getAccount().getId());
        dto.setStatus(card.getStatus());
        dto.setExpiryMonth(card.getExpiryMonth());
        dto.setExpiryYear(card.getExpiryYear());
        return dto;
    }

    private static String mask(String pan) {
        if (pan == null) return null;
        int len = pan.length();
        if (len <= 4) return pan;
        String last4 = pan.substring(len - 4);
        return "************" + last4;
    }

    @Data
    public static class CreateCardRequest{
        private Long accountId;
        private String cardType;

    }

    @Data
    public static class CardResponse{
        private Long id;
        private String maskedCardNumber;
        private int expiryMonth;
        private int expiryYear;
        private String status;

    }

    @Setter
    @Getter
    public static class CardDto {
        private Long id;
        private String maskedCardNumber;
        private Long accountId;
        private String status;
        private int expiryMonth;
        private int expiryYear;

    }
}
