package core.repositories;

import core.beans.messages.JMSMessage;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JMSMessageRepository extends JpaRepository<JMSMessage, String> {
}
