package ua.com.kisit.chernykhnazarcourse2026np.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ua.com.kisit.chernykhnazarcourse2026np.entity.BoardingPassenger;
import ua.com.kisit.chernykhnazarcourse2026np.entity.Ticket;

import java.util.List;
import java.util.Optional;

public interface BoardingPassengerRepository
        extends JpaRepository<BoardingPassenger, Long> {

    // Пошук пасажира за квитком (для видалення при поверненні)
    Optional<BoardingPassenger> findByTicket(Ticket ticket);

    // Пошук всіх пасажирів за ID відомості (замість findAll() + фільтрації)
    List<BoardingPassenger> findByBoardingListId(Long boardingListId);

}