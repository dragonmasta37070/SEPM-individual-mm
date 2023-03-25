package at.ac.tuwien.sepm.assignment.individual.service.impl;

import org.springframework.stereotype.Component;

import java.util.List;
import java.util.regex.Pattern;

@Component
public class BaseValidator {

  /**
   * validates if a string is present and has the right length
   *
   * @param validationErrors list of errors to add to
   * @param toVal            String to validate
   * @param maxLen           Max length of String
   * @param stringName       Name of the String for error message
   * @param nullValid        If the String can be null
   */
  public void validateString(List<String> validationErrors, String toVal, int maxLen, String stringName, Boolean nullValid) {
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
  }

  /**
   * validates the format of an email
   *
   * @param validationErrors list of errors to add to
   * @param email            email to validate
   */
  public static void vaildateEmail(List<String> validationErrors, String email) {
    if (email != null && !email.isBlank()) {
      String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\."
          + "[a-zA-Z0-9_+&*-]+)*@"
          + "(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
      Pattern pattern = Pattern.compile(emailRegex);
      if (!pattern.matcher(email).matches()) {
        validationErrors.add("invalid email-format");
      }
    }
  }
}