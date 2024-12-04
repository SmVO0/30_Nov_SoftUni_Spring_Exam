package softuni.exam.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import softuni.exam.models.entity.Visitor;

import java.util.Optional;

@Repository
public interface VisitorRepository extends JpaRepository<Visitor, Long> {
    Optional<Visitor> findByFirstNameAndLastName(String firstName, String lastName);

    Optional<Visitor> findByPersonalDataCardNumber(String personalDataCardNumber);

    Visitor findByFirstNameAndLastNameAndPersonalDataId(String firstName, String lastName, Long personalDataId);
}
