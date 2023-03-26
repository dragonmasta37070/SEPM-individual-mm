package at.ac.tuwien.sepm.assignment.individual.service.impl;

import at.ac.tuwien.sepm.assignment.individual.dto.HorseCreateDto;
import at.ac.tuwien.sepm.assignment.individual.dto.HorseDetailDto;
import at.ac.tuwien.sepm.assignment.individual.dto.HorseListDto;
import at.ac.tuwien.sepm.assignment.individual.exception.ConflictException;
import at.ac.tuwien.sepm.assignment.individual.exception.ValidationException;
import at.ac.tuwien.sepm.assignment.individual.type.Sex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.lang.invoke.MethodHandles;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Component
public class HorseValidator extends BaseValidator {
  private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

  /**
   * Validates the attributes of a horse, which is to be updated
   *
   * @param horse the horse to bew validated
   * @throws ValidationException if one of the validations fails
   */
  public void validateForUpdate(HorseDetailDto horse) throws ValidationException, ConflictException {
    LOG.trace("validateForUpdate({})", horse);
    List<String> validationErrors = new ArrayList<>();
    // TODO: validate on gender change if still valid
    if (horse.id() == null) {
      validationErrors.add("No ID given");
    }

    validateString(validationErrors, horse.name(), 255, "Horse name", false);
    validateString(validationErrors, horse.description(), 4095, "Horse description", true);
    validateDateOfBirth(validationErrors, horse.dateOfBirth(), horse.father(), horse.mother());
    validateSex(validationErrors, horse.sex(), horse.father(), horse.mother());

    if (!validationErrors.isEmpty()) {
      throw new ValidationException("Validation of horse for update failed", validationErrors);
    }
  }

  /**
   * Validates the attributes of a horse, which is to be inserted in the DB
   *
   * @param horse  the horse to bew validated
   * @param father the horse which should be the father of the horse
   * @param mother the horse which should be the mother of the horse
   * @throws ValidationException if one of the validations fails
   */
  public void validateNewHorse(HorseCreateDto horse, HorseListDto father, HorseListDto mother) throws ValidationException {
    LOG.trace("validateNewHorse({})", horse);
    List<String> validationErrors = new ArrayList<>();

    validateString(validationErrors, horse.name(), 255, "New horse name", false);
    validateString(validationErrors, horse.description(), 4095, "New horse description", true);
    validateDateOfBirth(validationErrors, horse.dateOfBirth(), father, mother);
    validateSex(validationErrors, horse.sex(), father, mother);

    if (!validationErrors.isEmpty()) {
      throw new ValidationException("Validation of horse for create failed", validationErrors);
    }
  }

  /**
   * validates if date of birth of parents is after the date of birth of the child
   *
   * @param validationErrors list of errors to add to
   * @param dateOfBirthHorse date of Birth of horse
   * @param father           HorseListDte of father
   * @param mother           HorseListDte of mother
   */
  private void validateDateOfBirth(List<String> validationErrors, LocalDate dateOfBirthHorse, HorseListDto father, HorseListDto mother) {
    if (dateOfBirthHorse == null) {
      validationErrors.add("Date of birth of horse missing");
    } else if (dateOfBirthHorse.isAfter(LocalDate.now())) {
      validationErrors.add("Date of birth of horse is after the current date");
    }
    if (father != null) {
      if (father.dateOfBirth().isAfter(dateOfBirthHorse)) {
        validationErrors.add("Date of birth of Father is after date of birth of child");
      }
    }
    if (mother != null) {
      if (mother.dateOfBirth().isAfter(dateOfBirthHorse)) {
        validationErrors.add("Date of birth of Father is after date of birth of child");
      }
    }
  }

  /**
   * validates sex horse and its parents if present
   *
   * @param validationErrors list of errors to add to
   * @param horseSex         sex of horse
   * @param father           HorseListDto of father
   * @param mother           HorseListDto of mother
   */
  private void validateSex(List<String> validationErrors, Sex horseSex, HorseListDto father, HorseListDto mother) {
    if (horseSex == null) {
      validationErrors.add("Sex of horse missing");
    }
    if (father != null && father.sex() != Sex.MALE) {
      validationErrors.add("Sex of father is not male");
    }
    if (mother != null && mother.sex() != Sex.FEMALE) {
      validationErrors.add("Sex of father is not female");
    }
  }
}
