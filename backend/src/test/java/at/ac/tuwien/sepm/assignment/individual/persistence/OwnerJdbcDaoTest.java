package at.ac.tuwien.sepm.assignment.individual.persistence;

import at.ac.tuwien.sepm.assignment.individual.dto.OwnerCreateDto;
import at.ac.tuwien.sepm.assignment.individual.dto.OwnerSearchDto;
import at.ac.tuwien.sepm.assignment.individual.entity.Owner;
import at.ac.tuwien.sepm.assignment.individual.exception.NotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ActiveProfiles({"test", "datagen"})
// enable "test" spring profile during test execution in order to pick up configuration from application-test.yml
@SpringBootTest
public class OwnerJdbcDaoTest {

  @Autowired
  OwnerDao dao;

  @Test
  void getOwnerByExistingId() throws NotFoundException {
    Owner owner = dao.getById(-1L);
    assertThat(owner)
        .extracting(Owner::getFirstName, Owner::getLastName, Owner::getEmail)
        .contains("Eragon", "Schattentoeter", "erscha@gmail.com");
  }

  @Test
  void getOwnerByNotExistingIdThrowsError() {
    assertThrows(NotFoundException.class,
        () -> dao.getById(-187L));
  }

  @Test
  void getByMail() {
    Optional<Owner> owner = dao.getByMail("erscha@gmail.com");
    assertThat(owner).get()
        .extracting(Owner::getFirstName, Owner::getLastName, Owner::getEmail)
        .contains("Eragon", "Schattentoeter", "erscha@gmail.com");
  }

  @Test
  @DirtiesContext
  void createOwnerWithValidData() throws NotFoundException {
    Owner owner = dao.create(new OwnerCreateDto("VorName", "NachName", "krasse@mail.com"));
    assertThat(owner)
        .extracting(Owner::getFirstName, Owner::getLastName, Owner::getEmail)
        .contains("VorName", "NachName", "krasse@mail.com");
    Owner dbOwner = dao.getById(owner.getId());
    assertThat(dbOwner)
        .extracting(Owner::getFirstName, Owner::getLastName, Owner::getEmail)
        .contains("VorName", "NachName", "krasse@mail.com");
  }

  @Test
  void getAllThreeOwnersByIds() {
    List<Long> ids = new LinkedList<>();
    ids.add(-1L);
    ids.add(-2L);
    ids.add(-3L);

    var owners = dao.getAllById(ids);
    assertThat(owners).size().isEqualTo(3);
    assertThat(owners)
        .extracting(Owner::getFirstName, Owner::getLastName, Owner::getEmail)
        .contains(tuple("Eragon", "Schattentoeter", "erscha@gmail.com"))
        .contains(tuple("Enekin", "Skywalker", "lowground@imperium.com"))
        .contains(tuple("Obiwan", "Kenobus", "highground@imperium.com"));
  }

  @Test
  void searchOwnerForNameAndGetTheSearchedOne() {
    Collection<Owner> owner = dao.search(new OwnerSearchDto("Eragon", 1));
    assertThat(owner)
        .extracting(Owner::getFirstName, Owner::getLastName, Owner::getEmail)
        .contains(tuple("Eragon", "Schattentoeter", "erscha@gmail.com"));
  }
}