package br.com.autocarehub.infrastructure.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "demo_leads")
public class DemoLeadJpaEntity {

  @Id private UUID id;

  @Column(name = "contact_name", nullable = false, length = 120)
  private String contactName;

  @Column(name = "company_name", nullable = false, length = 120)
  private String companyName;

  @Column(name = "demo_profile", nullable = false, length = 40)
  private String demoProfile;

  @Column(nullable = false, length = 160)
  private String email;

  @Column(nullable = false, length = 30)
  private String phone;

  @Column(nullable = false, length = 40)
  private String cnpj;

  @Column(name = "created_at", nullable = false)
  private LocalDateTime createdAt;

  public UUID getId() {
    return id;
  }

  public void setId(UUID id) {
    this.id = id;
  }

  public String getContactName() {
    return contactName;
  }

  public void setContactName(String contactName) {
    this.contactName = contactName;
  }

  public String getCompanyName() {
    return companyName;
  }

  public void setCompanyName(String companyName) {
    this.companyName = companyName;
  }

  public String getDemoProfile() {
    return demoProfile;
  }

  public void setDemoProfile(String demoProfile) {
    this.demoProfile = demoProfile;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public String getPhone() {
    return phone;
  }

  public void setPhone(String phone) {
    this.phone = phone;
  }

  public String getCnpj() {
    return cnpj;
  }

  public void setCnpj(String cnpj) {
    this.cnpj = cnpj;
  }

  public LocalDateTime getCreatedAt() {
    return createdAt;
  }

  public void setCreatedAt(LocalDateTime createdAt) {
    this.createdAt = createdAt;
  }
}
