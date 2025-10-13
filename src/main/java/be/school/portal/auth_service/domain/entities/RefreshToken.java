package be.school.portal.auth_service.domain.entities;

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
  private Long id; // DB primary key

  @Column(nullable = false)
  private ZonedDateTime issuedAt;

  @Column(nullable = false)
  private ZonedDateTime expiresAt;

  @Column(length = 255)
  private String deviceInfo; // optional, e.g., "iPhone 15"

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id", nullable = false)
  private UserAccount user; // reference to User aggregate root

  private RefreshToken(RefreshTokenBuilder builder) {
    this.id = builder.id;
    this.issuedAt = builder.issuedAt;
    this.expiresAt = builder.expiresAt;
    this.deviceInfo = builder.deviceInfo;
    this.user = builder.user;
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
