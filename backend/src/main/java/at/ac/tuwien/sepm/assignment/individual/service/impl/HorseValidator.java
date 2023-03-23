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
public class HorseValidator {
  private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());


  public void validateForUpdate(HorseDetailDto horse, HorseListDto father, HorseListDto mother) throws ValidationException, ConflictException {
    LOG.trace("validateForUpdate({})", horse);
    List<String> validationErrors = new ArrayList<>();

    if (horse.id() == null) {
      validationErrors.add("No ID given");
    }
    validationErrors.addAll(validateString(horse.name(), 255, "Horse name", false));
    validationErrors.addAll(validateString(horse.description(), 4095, "Horse description", true));
    validationErrors.addAll(validateDateOfBirth(horse.dateOfBirth(), father, mother));
    validationErrors.addAll(validateSex(horse.sex(), father, mother));

    if (!validationErrors.isEmpty()) {
      throw new ValidationException("Validation of horse for update failed", validationErrors);
    }
  }

  public void validateNewHorse(HorseCreateDto horse, HorseListDto father, HorseListDto mother) throws ValidationException {
    LOG.trace("validateNewHorse({})", horse);
    List<String> validationErrors = new ArrayList<>();

    validationErrors.addAll(validateString(horse.name(), 255, "New horse name", false));
    validationErrors.addAll(validateString(horse.description(), 4095, "New horse description", true));
    validationErrors.addAll(validateDateOfBirth(horse.dateOfBirth(), father, mother));
    validationErrors.addAll(validateSex(horse.sex(), father, mother));

    if (!validationErrors.isEmpty()) {
      throw new ValidationException("Validation of horse for create failed", validationErrors);
    }
  }

  /**
   * validates if a string is present and has the right length
   *
   * @param toVal      String to validate
   * @param maxLen     Max length of String
   * @param stringName Name of the String for error message
   * @param nullValid  If the String can be null
   * @return possible errors
   */
  private List<String> validateString(String toVal, int maxLen, String stringName, Boolean nullValid) {
    List<String> validationErrors = new ArrayList<>();

    if (toVal == null) {
      if (!nullValid) {
        validationErrors.add(stringName + " is missing");
      }
    } else {
      if (toVal.isBlank()) {
        validationErrors.add(stringName + " is given but blank");
      }
      if (toVal.length() > maxLen) {
        validationErrors.add(stringName + " too long: longer than 4095 characters");
      }
    }
    return validationErrors;
  }

  /**
   * validates if date of birth of parents is after the date of birth of the child
   *
   * @param dateOfBirthHorse date of Birth of horse
   * @param father           HorseListDte of father
   * @param mother           HorseListDte of mother
   * @return possible errors
   */
  private static List<String> validateDateOfBirth(LocalDate dateOfBirthHorse, HorseListDto father, HorseListDto mother) {
    List<String> validationErrors = new ArrayList<>();

    if (dateOfBirthHorse == null) {
      validationErrors.add("Date of birth of horse missing");
    } else if (dateOfBirthHorse.isAfter(LocalDate.now())) {
      validationErrors.add("Date of birth of horse is after the current date");
    }
    if (father != null) {
      if (father.dateOfBirth().isBefore(dateOfBirthHorse)) {
        validationErrors.add("Date of birth of Father is before date of birth of child");
      }
    }
    if (mother != null) {
      if (mother.dateOfBirth().isBefore(dateOfBirthHorse)) {
        validationErrors.add("Date of birth of Father is before date of birth of child");
      }
    }

    return validationErrors;
  }

  /**
   * validates sex horse and its parents if present
   *
   * @param horseSex sex of horse
   * @param father   HorseListDto of father
   * @param mother   HorseListDto of mother
   * @return possible errors
   */
  private static List<String> validateSex(Sex horseSex, HorseListDto father, HorseListDto mother) {
    List<String> validationErrors = new ArrayList<>();

    if (horseSex == null) {
      validationErrors.add("Sex of horse missing");
    }
    if (father != null && father.sex() != Sex.MALE) {
      validationErrors.add("Sex of father is not male");
    }
    if (mother != null && mother.sex() != Sex.FEMALE) {
      validationErrors.add("Sex of father is not female");
    }

    return validationErrors;
  }
}
