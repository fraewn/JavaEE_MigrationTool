package core.repositories;

import core.beans.batch.CleanReport;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CleanReportRepository extends JpaRepository<CleanReport, String> {
}
