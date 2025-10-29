package be.school.portal.auth_service.account.domain.entities;

import jakarta.persistence.*;
import java.time.ZonedDateTime;
import lombok.*;

@Entity
@Table(name = "refresh_token")
@NoArgsConstructor
@Getter
@Setter
public class RefreshToken {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Setter(AccessLevel.NONE)
  private Long id;

  @Column(name = "issued_at", nullable = false)
  private ZonedDateTime issuedAt;

  @Column(name = "expires_at", nullable = false)
  private ZonedDateTime expiresAt;

  @Column(name = "device_info", nullable = false)
  private String deviceInfo;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id", nullable = false)
  private UserAccount user;

  @Column(name = "revoked", nullable = false)
  @Getter(AccessLevel.NONE)
  @Setter(AccessLevel.NONE)
  private Boolean revoked = false;

  private RefreshToken(RefreshTokenBuilder builder) {
    this.id = builder.id;
    this.issuedAt = builder.issuedAt;
    this.expiresAt = builder.expiresAt;
    this.deviceInfo = builder.deviceInfo;
    this.user = builder.user;
  }

  public boolean isActive() {
    return !this.revoked;
  }

  public void revoke() {
    this.revoked = true;
  }

  public static final class RefreshTokenBuilder {
    private Long id;
    private ZonedDateTime issuedAt;
    private ZonedDateTime expiresAt;
    private String deviceInfo;
    private UserAccount user;

    private RefreshTokenBuilder() {}

    public static RefreshTokenBuilder aRefreshToken() {
      return new RefreshTokenBuilder();
    }

    public RefreshTokenBuilder withId(Long id) {
      this.id = id;
      return this;
    }

    public RefreshTokenBuilder withIssuedAt(ZonedDateTime issuedAt) {
      this.issuedAt = issuedAt;
      return this;
    }

    public RefreshTokenBuilder withExpiresAt(ZonedDateTime expiresAt) {
      this.expiresAt = expiresAt;
      return this;
    }

    public RefreshTokenBuilder withDeviceInfo(String deviceInfo) {
      this.deviceInfo = deviceInfo;
      return this;
    }

    public RefreshTokenBuilder withUser(UserAccount user) {
      this.user = user;
      return this;
    }

    public RefreshToken build() {
      return new RefreshToken(this);
    }
  }
}
