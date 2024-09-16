package project.dailyge.app.common.data;

import jakarta.persistence.Entity;
import jakarta.persistence.EntityManager;
import jakarta.persistence.metamodel.EntityType;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import project.dailyge.core.cache.user.UserCache;
import project.dailyge.entity.task.MonthlyTaskJpaEntity;
import static project.dailyge.entity.task.MonthlyTasks.createMonthlyTasks;
import static project.dailyge.entity.user.Role.NORMAL;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Slf4j
@Component
@Profile({"local", "dev"})
@RequiredArgsConstructor
public class OperationDataInitializer implements CommandLineRunner {

    private static final String LOCAL = "local";
    private static final String DEV = "dev";

    @Value("${profile.email}")
    private String email;

    @Value("${env}")
    private String env;

    private final Set<String> tableNames;
    private final EntityManager entityManager;
    private final JdbcTemplate jdbcTemplate;

    private final OperationDataWriteDao operationWriteDao;

    @Override
    @Transactional
    public void run(final String... args) {
        if (isValidEnv(env)) {
            clearData();
            initData();
            log.info("Data initialized success.");
        }
    }

    private void clearData() {
        final Set<String> tableNames = entityManager.getMetamodel()
            .getEntities().stream()
            .filter(isEntityType())
            .map(toLowerCase())
            .collect(Collectors.toSet());
        this.tableNames.addAll(tableNames);
        for (final String table : this.tableNames) {
            final String query = String.format("TRUNCATE %s", table);
            jdbcTemplate.execute(query);
        }
    }

    @NotNull
    private Predicate<EntityType<?>> isEntityType() {
        return entityType -> entityType.getJavaType().getAnnotation(Entity.class) != null;
    }

    @NotNull
    private Function<EntityType<?>, String> toLowerCase() {
        return entityType -> entityType.getName().toLowerCase();
    }

    private boolean isValidEnv(final String env) {
        return LOCAL.equals(env)
            || DEV.equals(env);
    }

    @Transactional
    public void initData() {
        try {
            final Long initUserId = 1L;
            final UserCache userCache = new UserCache(initUserId, email, email, "", NORMAL.name());
            operationWriteDao.save(userCache);
            operationWriteDao.save(email);

            final List<MonthlyTaskJpaEntity> monthlyTasks = createMonthlyTasks(initUserId, LocalDate.now().getYear());
            for (final MonthlyTaskJpaEntity monthlyTaskJpa : monthlyTasks) {
                entityManager.persist(monthlyTaskJpa);
            }
        } catch (Exception ex) {
            log.error("Data initialization failed: {}", ex.getMessage());
        }
    }
}
