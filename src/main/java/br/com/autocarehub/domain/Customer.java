package br.com.autocarehub.domain;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

public class Customer {

  private final UUID id;
  private final Document document;
  private final LocalDateTime createdAt;
  private String name;
  private String phone;
  private String email;
  private Address address;
  private boolean active;

  public Customer(String name, Document document, String phone, String email, Address address) {
    this(UUID.randomUUID(), name, document, phone, email, address, true, LocalDateTime.now());
  }

  public Customer(
      UUID id,
      String name,
      Document document,
      String phone,
      String email,
      Address address,
      boolean active,
      LocalDateTime createdAt) {
    this.id = Objects.requireNonNull(id, "id is required");
    this.name = DomainValidation.requireText(name, "Name is required", 120);
    this.document = Objects.requireNonNull(document, "document is required");
    this.phone = DomainValidation.requirePhone(phone);
    this.email = DomainValidation.requireEmail(email);
    this.address = address;
    this.active = active;
    this.createdAt = Objects.requireNonNull(createdAt, "createdAt is required");
  }

  public void rename(String name) {
    this.name = DomainValidation.requireText(name, "Name is required", 120);
  }

  public void updateContact(String phone, String email) {
    this.phone = DomainValidation.requirePhone(phone);
    this.email = DomainValidation.requireEmail(email);
  }

  public void updateAddress(Address address) {
    this.address = address;
  }

  public void activate() {
    this.active = true;
  }

  public void deactivate() {
    this.active = false;
  }

  public UUID id() {
    return id;
  }

  public String name() {
    return name;
  }

  public Document document() {
    return document;
  }

  public String phone() {
    return phone;
  }

  public String email() {
    return email;
  }

  public Address address() {
    return address;
  }

  public boolean active() {
    return active;
  }

  public LocalDateTime createdAt() {
    return createdAt;
  }
}
