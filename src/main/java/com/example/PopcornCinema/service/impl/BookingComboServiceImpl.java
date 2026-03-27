package com.example.PopcornCinema.service.impl;

import com.example.PopcornCinema.dto.BookingComboItemDto;
import com.example.PopcornCinema.dto.SaveBookingCombosRequest;
import com.example.PopcornCinema.dto.SelectedComboDto;
import com.example.PopcornCinema.entity.BookingCombo;
import com.example.PopcornCinema.entity.Combo;
import com.example.PopcornCinema.repository.BookingComboRepository;
import com.example.PopcornCinema.repository.ComboRepository;
import com.example.PopcornCinema.service.BookingComboService;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class BookingComboServiceImpl implements BookingComboService {

    private final BookingComboRepository bookingComboRepository;
    private final ComboRepository comboRepository;

    public BookingComboServiceImpl(BookingComboRepository bookingComboRepository,
                                   ComboRepository comboRepository) {
        this.bookingComboRepository = bookingComboRepository;
        this.comboRepository = comboRepository;
    }

    @Override
    @Transactional
    public void saveSelectedCombos(Long showtimeId, SaveBookingCombosRequest request) {
        Long userId = request.getUserId();

        if (request.getItems() == null || request.getItems().isEmpty()) {
            bookingComboRepository.deleteByShowtimeIdAndUserIdAndBookingIdIsNull(showtimeId, userId);
            return;
        }

        for (BookingComboItemDto item : request.getItems()) {
            if (item.getComboId() == null) {
                continue;
            }

            var existingOpt = bookingComboRepository
                    .findByShowtimeIdAndUserIdAndComboIdAndBookingIdIsNull(showtimeId, userId, item.getComboId());

            int quantity = item.getQuantity() == null ? 0 : item.getQuantity();

            if (existingOpt.isPresent()) {
                BookingCombo existing = existingOpt.get();

                if (quantity <= 0) {
                    bookingComboRepository.delete(existing);
                } else {
                    existing.setQuantity(quantity);
                    bookingComboRepository.save(existing);
                }
            } else {
                if (quantity <= 0) {
                    continue;
                }

                BookingCombo bookingCombo = new BookingCombo();
                bookingCombo.setShowtimeId(showtimeId);
                bookingCombo.setUserId(userId);
                bookingCombo.setComboId(item.getComboId());
                bookingCombo.setQuantity(quantity);
                bookingCombo.setBookingId(null);

                bookingComboRepository.save(bookingCombo);
            }
        }
    }

    @Override
    public List<SelectedComboDto> getSelectedCombos(Long showtimeId, Long userId) {
        List<BookingCombo> selected =
                bookingComboRepository.findByShowtimeIdAndUserIdAndBookingIdIsNullOrderByIdAsc(showtimeId, userId);

        return mapToSelectedComboDtos(selected);
    }

    @Override
    @Transactional
    public void clearSelectedCombos(Long showtimeId, Long userId) {
        bookingComboRepository.deleteByShowtimeIdAndUserIdAndBookingIdIsNull(showtimeId, userId);
    }

    @Override
    @Transactional
    public void moveTempCombosToBooking(Long showtimeId, Long userId, Long bookingId) {
        List<BookingCombo> tempCombos =
                bookingComboRepository.findByShowtimeIdAndUserIdAndBookingIdIsNullOrderByIdAsc(showtimeId, userId);

        if (tempCombos.isEmpty()) {
            return;
        }

        // Idempotent: nếu finalize lại thì xóa combo booking cũ rồi chèn lại
        bookingComboRepository.deleteByBookingId(bookingId);

        for (BookingCombo temp : tempCombos) {
            BookingCombo booked = new BookingCombo();
            booked.setBookingId(bookingId);
            booked.setShowtimeId(null);
            booked.setUserId(null);
            booked.setComboId(temp.getComboId());
            booked.setQuantity(temp.getQuantity());

            bookingComboRepository.save(booked);
        }

        bookingComboRepository.deleteAll(tempCombos);
    }

    @Override
    public List<SelectedComboDto> getBookedCombos(Long bookingId) {
        List<BookingCombo> booked = bookingComboRepository.findByBookingIdOrderByIdAsc(bookingId);
        return mapToSelectedComboDtos(booked);
    }

    private List<SelectedComboDto> mapToSelectedComboDtos(List<BookingCombo> items) {
        if (items == null || items.isEmpty()) {
            return new ArrayList<>();
        }

        Map<Long, Combo> comboMap = comboRepository.findAllById(
                items.stream().map(BookingCombo::getComboId).toList()
        ).stream().collect(Collectors.toMap(Combo::getId, c -> c));

        return items.stream().map(item -> {
            Combo combo = comboMap.get(item.getComboId());
            BigDecimal price = combo != null ? combo.getPrice() : BigDecimal.ZERO;
            BigDecimal subtotal = price.multiply(BigDecimal.valueOf(item.getQuantity()));

            return new SelectedComboDto(
                    item.getComboId(),
                    combo != null ? combo.getName() : "Combo",
                    price,
                    item.getQuantity(),
                    subtotal
            );
        }).toList();
    }
}