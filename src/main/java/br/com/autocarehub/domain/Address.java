package br.com.autocarehub.domain;

import org.springframework.lang.Nullable;

public record Address(
    String street,
    String number,
    @Nullable String complement,
    String neighborhood,
    String city,
    String state,
    String zipCode) {

  public Address {
    street = DomainValidation.requireText(street, "Street is required", 120);
    number = DomainValidation.requireText(number, "Number is required", 20);
    complement = DomainValidation.optionalText(complement);
    neighborhood = DomainValidation.requireText(neighborhood, "Neighborhood is required", 80);
    city = DomainValidation.requireText(city, "City is required", 80);
    state = DomainValidation.requireState(state);
    zipCode =
        DomainValidation.requireText(zipCode, "Zip code is required", 9).replaceAll("\\D", "");
    if (zipCode.length() != 8) {
      throw new DomainException("Zip code must have eight digits");
    }
  }
}
