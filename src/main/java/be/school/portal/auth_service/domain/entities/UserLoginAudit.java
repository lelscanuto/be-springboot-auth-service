package be.school.portal.auth_service.domain.entities;

import be.school.portal.auth_service.domain.enums.LoginAction;
import jakarta.persistence.*;
import java.time.ZonedDateTime;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Table(name = "user_login_audit")
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
@EntityListeners(AuditingEntityListener.class)
public class UserLoginAudit {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Setter(AccessLevel.NONE)
  private Long id;

  @Column(name = "username", nullable = false)
  private String username;

  @Column(name = "action", nullable = false)
  @Enumerated(EnumType.STRING)
  private LoginAction action;

  @CreatedDate
  @Column(name = "attempted_at", nullable = false, updatable = false)
  private ZonedDateTime attemptedAt;

  private UserLoginAudit(UserLoginAuditBuilder builder) {
    this.username = builder.username;
    this.action = builder.action;
  }

  public static UserLoginAuditBuilder builder() {
    return new UserLoginAuditBuilder();
  }

  public static final class UserLoginAuditBuilder {
    private String username;
    private LoginAction action;

    private UserLoginAuditBuilder() {}

    public UserLoginAuditBuilder withUsername(String username) {
      this.username = username;
      return this;
    }

    public UserLoginAuditBuilder withAction(LoginAction action) {
      this.action = action;
      return this;
    }

    public UserLoginAudit build() {
      UserLoginAudit userLoginAudit = new UserLoginAudit();
      userLoginAudit.setUsername(username);
      userLoginAudit.setAction(action);
      return new UserLoginAudit(this);
    }
  }
}
