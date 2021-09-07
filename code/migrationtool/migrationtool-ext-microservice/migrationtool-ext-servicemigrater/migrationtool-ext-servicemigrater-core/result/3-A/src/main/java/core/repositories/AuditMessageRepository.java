package core.repositories;

import core.beans.messages.AuditMessage;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuditMessageRepository extends JpaRepository<AuditMessage, String> {
}
