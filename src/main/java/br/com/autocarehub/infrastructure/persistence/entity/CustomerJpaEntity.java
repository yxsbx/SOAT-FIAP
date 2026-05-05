package br.com.autocarehub.infrastructure.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "customers")
public class CustomerJpaEntity {

    @Id
    private UUID id;

    @Column(nullable = false, length = 120)
    private String name;

    @Column(name = "document_type", nullable = false, length = 10)
    private String documentType;

    @Column(name = "document_value", nullable = false, length = 14, unique = true)
    private String documentValue;

    @Column(nullable = false, length = 20)
    private String phone;

    @Column(nullable = false, length = 120)
    private String email;

    @Column(name = "address_street", length = 120)
    private String addressStreet;

    @Column(name = "address_number", length = 20)
    private String addressNumber;

    @Column(name = "address_complement", length = 80)
    private String addressComplement;

    @Column(name = "address_neighborhood", length = 80)
    private String addressNeighborhood;

    @Column(name = "address_city", length = 80)
    private String addressCity;

    @Column(name = "address_state", length = 2)
    private String addressState;

    @Column(name = "address_zip_code", length = 8)
    private String addressZipCode;

    @Column(nullable = false)
    private boolean active;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    public CustomerJpaEntity() {
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDocumentType() {
        return documentType;
    }

    public void setDocumentType(String documentType) {
        this.documentType = documentType;
    }

    public String getDocumentValue() {
        return documentValue;
    }

    public void setDocumentValue(String documentValue) {
        this.documentValue = documentValue;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAddressStreet() {
        return addressStreet;
    }

    public void setAddressStreet(String addressStreet) {
        this.addressStreet = addressStreet;
    }

    public String getAddressNumber() {
        return addressNumber;
    }

    public void setAddressNumber(String addressNumber) {
        this.addressNumber = addressNumber;
    }

    public String getAddressComplement() {
        return addressComplement;
    }

    public void setAddressComplement(String addressComplement) {
        this.addressComplement = addressComplement;
    }

    public String getAddressNeighborhood() {
        return addressNeighborhood;
    }

    public void setAddressNeighborhood(String addressNeighborhood) {
        this.addressNeighborhood = addressNeighborhood;
    }

    public String getAddressCity() {
        return addressCity;
    }

    public void setAddressCity(String addressCity) {
        this.addressCity = addressCity;
    }

    public String getAddressState() {
        return addressState;
    }

    public void setAddressState(String addressState) {
        this.addressState = addressState;
    }

    public String getAddressZipCode() {
        return addressZipCode;
    }

    public void setAddressZipCode(String addressZipCode) {
        this.addressZipCode = addressZipCode;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
