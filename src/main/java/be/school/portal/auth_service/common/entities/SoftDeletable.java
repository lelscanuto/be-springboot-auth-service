package be.school.portal.auth_service.common.entities;

public interface SoftDeletable {

  /**
   * Checks if the entity is soft-deleted.
   *
   * @return true if the entity is marked as deleted, false otherwise.
   */
  boolean isDeleted();

  /** Marks the entity as deleted (soft delete). */
  void delete();

  /** Reverts the deletion, restoring the entity. */
  void revoke();
}
