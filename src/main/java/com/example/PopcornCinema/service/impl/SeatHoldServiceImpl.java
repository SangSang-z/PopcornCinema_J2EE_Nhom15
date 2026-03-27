package com.example.PopcornCinema.service.impl;

import com.example.PopcornCinema.entity.SeatHold;
import com.example.PopcornCinema.repository.SeatHoldRepository;
import com.example.PopcornCinema.service.SeatHoldService;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class SeatHoldServiceImpl implements SeatHoldService {

    private final SeatHoldRepository seatHoldRepository;

    public SeatHoldServiceImpl(SeatHoldRepository seatHoldRepository) {
        this.seatHoldRepository = seatHoldRepository;
    }

    @Override
    @Transactional
    public void holdSeats(Long showtimeId, Long userId, List<Long> seatIds) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime expiresAt = now.plusMinutes(5);

        seatHoldRepository.deleteExpired(now);

        for (Long seatId : seatIds) {
            var ownHold = seatHoldRepository.findOwnActiveHold(showtimeId, seatId, userId, now);

            if (ownHold.isPresent()) {
                SeatHold hold = ownHold.get();
                hold.setExpiresAt(expiresAt);
                seatHoldRepository.save(hold);
                continue;
            }

            var activeHolds = seatHoldRepository.findActiveHolds(showtimeId, seatId, now);

            boolean heldByOther = activeHolds.stream()
                    .anyMatch(h -> !h.getUserId().equals(userId));

            if (heldByOther) {
                throw new RuntimeException("Ghế đang được người khác giữ");
            }

            SeatHold newHold = new SeatHold();
            newHold.setShowtimeId(showtimeId);
            newHold.setSeatId(seatId);
            newHold.setUserId(userId);
            newHold.setExpiresAt(expiresAt);
            newHold.setCreatedAt(now);

            seatHoldRepository.save(newHold);
        }
    }

    @Override
    @Transactional
    public void releaseSeats(Long showtimeId, Long userId, List<Long> seatIds) {
        seatHoldRepository.deleteHeldSeats(showtimeId, userId, seatIds);
    }

    @Override
    public Set<Long> getHeldSeatIdsByOtherUsers(Long showtimeId, Long userId) {
        LocalDateTime now = LocalDateTime.now();

        return seatHoldRepository.findAllActiveByShowtimeId(showtimeId, now)
                .stream()
                .filter(h -> !h.getUserId().equals(userId))
                .map(SeatHold::getSeatId)
                .collect(Collectors.toSet());
    }
}