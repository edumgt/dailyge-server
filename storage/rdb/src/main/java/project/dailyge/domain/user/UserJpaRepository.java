package project.dailyge.domain.user;

import org.springframework.data.jpa.repository.*;

import java.nio.file.*;

public interface UserJpaRepository extends JpaRepository<User, LinkOption> {
}
