package br.com.autocarehub.domain;

public record Address(
    String street,
    String number,
    String complement,
    String neighborhood,
    String city,
    String state,
    String zipCode) {

  public Address {
    street = requireText(street, "Street is required");
    number = requireText(number, "Number is required");
    neighborhood = requireText(neighborhood, "Neighborhood is required");
    city = requireText(city, "City is required");
    state = requireText(state, "State is required").toUpperCase();
    zipCode = requireText(zipCode, "Zip code is required").replaceAll("\\D", "");
    if (state.length() != 2) {
      throw new DomainException("State must have two characters");
    }
    if (zipCode.length() != 8) {
      throw new DomainException("Zip code must have eight digits");
    }
  }

  private static String requireText(String value, String message) {
    if (value == null || value.isBlank()) {
      throw new DomainException(message);
    }
    return value.trim();
  }
}
