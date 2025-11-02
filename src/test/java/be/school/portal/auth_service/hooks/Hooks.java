package be.school.portal.auth_service.hooks;

import org.flywaydb.core.Flyway;

public class Hooks {

  private final Flyway flyway;

  public Hooks(Flyway flyway) {
    this.flyway = flyway;
  }


}
