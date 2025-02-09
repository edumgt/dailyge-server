package project.dailyge.app.core.task.persistence;

import com.querydsl.jpa.impl.JPAQueryFactory;
import java.sql.Date;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import project.dailyge.app.common.exception.CommonException;
import project.dailyge.entity.task.MonthlyTaskEntityReadRepository;
import project.dailyge.entity.task.MonthlyTaskJpaEntity;
import project.dailyge.entity.task.TaskEntityReadRepository;
import project.dailyge.entity.task.TaskJpaEntity;

import project.dailyge.entity.task.TaskLabelEntityReadRepository;
import project.dailyge.entity.task.TaskLabelJpaEntity;
import static project.dailyge.app.codeandmessage.CommonCodeAndMessage.DATA_ACCESS_EXCEPTION;
import static project.dailyge.entity.retrospect.QRetrospectJpaEntity.retrospectJpaEntity;
import static project.dailyge.entity.task.QMonthlyTaskJpaEntity.monthlyTaskJpaEntity;
import static project.dailyge.entity.task.QTaskJpaEntity.taskJpaEntity;
import static project.dailyge.entity.task.QTaskLabelJpaEntity.taskLabelJpaEntity;

import static project.dailyge.app.codeandmessage.CommonCodeAndMessage.DATA_ACCESS_EXCEPTION;
import static project.dailyge.entity.retrospect.QRetrospectJpaEntity.retrospectJpaEntity;
import static project.dailyge.entity.task.QMonthlyTaskJpaEntity.monthlyTaskJpaEntity;
import static project.dailyge.entity.task.QTaskJpaEntity.taskJpaEntity;

@Repository
class TaskReadDao implements TaskEntityReadRepository, MonthlyTaskEntityReadRepository, TaskLabelEntityReadRepository {

    private final JdbcTemplate jdbcTemplate;
    private final JPAQueryFactory queryFactory;

    public TaskReadDao(
        final JdbcTemplate jdbcTemplate,
        final JPAQueryFactory queryFactory
    ) {
        this.jdbcTemplate = jdbcTemplate;
        this.queryFactory = queryFactory;
    }

    public Long findTotalCount(final Long userId) {
        return queryFactory
            .select(retrospectJpaEntity.id.count())
            .from(retrospectJpaEntity)
            .where(
                retrospectJpaEntity.userId.eq(userId)
                    .and(retrospectJpaEntity._deleted.eq(false))
            )
            .fetchOne();
    }

    @Override
    public Optional<TaskJpaEntity> findTaskById(final Long taskId) {
        return Optional.ofNullable(
            queryFactory.selectFrom(taskJpaEntity)
                .where(
                    taskJpaEntity.id.eq(taskId)
                        .and(taskJpaEntity._deleted.eq(false))
                )
                .fetchFirst()
        );
    }

    @Override
    public Optional<MonthlyTaskJpaEntity> findMonthlyTaskByUserIdAndDate(
        final Long userId,
        final LocalDate now
    ) {
        return Optional.ofNullable(
            queryFactory.selectFrom(monthlyTaskJpaEntity)
                .where(
                    monthlyTaskJpaEntity.userId.eq(userId)
                        .and(monthlyTaskJpaEntity.year.eq(now.getYear()))
                        .and(monthlyTaskJpaEntity.month.eq(now.getMonthValue()))
                        .and(monthlyTaskJpaEntity._deleted.eq(false))
                )
                .fetchFirst()
        );
    }

    @Override
    public Optional<MonthlyTaskJpaEntity> findMonthlyTaskById(final Long monthlyTaskId) {
        return Optional.ofNullable(
            queryFactory.selectFrom(monthlyTaskJpaEntity)
                .where(
                    monthlyTaskJpaEntity.id.eq(monthlyTaskId)
                        .and(monthlyTaskJpaEntity._deleted.eq(false))
                )
                .fetchFirst()
        );
    }

    @Override
    public boolean existsMonthlyPlanByUserIdAndDate(
        final Long userId,
        final LocalDate date
    ) {
        final String sql = "SELECT 1 FROM monthly_tasks WHERE user_id = ? AND year = ?";
        try {
            final Integer count = jdbcTemplate.query(sql, new Object[]{userId, date.getYear()}, rs -> {
                if (rs.next()) {
                    return rs.getInt(1);
                } else {
                    return 0;
                }
            });
            return count != null && count > 0;
        } catch (DataAccessException ex) {
            throw CommonException.from(ex.getMessage(), DATA_ACCESS_EXCEPTION);
        }
    }

    @Override
    public List<TaskJpaEntity> findTasksByMonthlyTaskIdAndDates(
        final Long userId,
        final Set<Long> monthlyTaskIds,
        final LocalDate startDate,
        final LocalDate endDate
    ) {
        return queryFactory.selectFrom(taskJpaEntity)
            .where(
                taskJpaEntity.monthlyTaskId.in(monthlyTaskIds)
                    .and(taskJpaEntity.userId.eq(userId))
                    .and(taskJpaEntity.date.between(startDate, endDate))
                    .and(taskJpaEntity._deleted.eq(false))
            ).fetch();
    }

    @Override
    public Long findMonthlyTaskIdByUserIdAndDate(
        final Long userId,
        final LocalDate date
    ) {
        return queryFactory.select(monthlyTaskJpaEntity.id)
            .from(monthlyTaskJpaEntity)
            .where(
                monthlyTaskJpaEntity.userId.eq(userId)
                    .and(monthlyTaskJpaEntity.year.eq(date.getYear()))
                    .and(monthlyTaskJpaEntity.month.eq(date.getMonthValue()))
                    .and(monthlyTaskJpaEntity._deleted.eq(false))
            )
            .limit(1)
            .fetchOne();
    }

    @Override
    public Set<Long> findMonthlyTasksByUserIdAndDates(
        final Long userId,
        final LocalDate startDate,
        final LocalDate endDate
    ) {
        final int startYear = startDate.getYear();
        final int startMonth = startDate.getMonthValue();
        final int endYear = endDate.getYear();
        final int endMonth = endDate.getMonthValue();
        return new HashSet<>(queryFactory.select(monthlyTaskJpaEntity.id)
            .from(monthlyTaskJpaEntity)
            .where(
                monthlyTaskJpaEntity.userId.eq(userId),
                monthlyTaskJpaEntity.year.eq(startYear)
                    .and(monthlyTaskJpaEntity.month.goe(startMonth))
                    .or(monthlyTaskJpaEntity.year.gt(startYear)),
                monthlyTaskJpaEntity.year.eq(endYear)
                    .and(monthlyTaskJpaEntity.month.loe(endMonth))
                    .or(monthlyTaskJpaEntity.year.lt(endYear)),
                monthlyTaskJpaEntity._deleted.eq(false)
            )
            .fetch());
    }

    @Override
    public long countMonthlyTask(
        final Long userId,
        final LocalDate date
    ) {
        final Long count = queryFactory.select(monthlyTaskJpaEntity.count())
            .from(monthlyTaskJpaEntity)
            .where(
                monthlyTaskJpaEntity.userId.eq(userId)
                    .and(monthlyTaskJpaEntity.year.eq(date.getYear()))
                    .and(monthlyTaskJpaEntity._deleted.eq(false))
            ).fetchOne();
        if (count == null) {
            return 0;
        }
        return count;
    }

    @Override
    public List<TaskJpaEntity> findMonthlyTasksByIdAndDate(
        final Long monthlyTaskId,
        final LocalDate date
    ) {
        return queryFactory.selectFrom(taskJpaEntity)
            .where(
                taskJpaEntity.monthlyTaskId.eq(monthlyTaskId)
                    .and(taskJpaEntity.year.eq(date.getYear()))
                    .and(taskJpaEntity.year.eq(date.getYear()))
                    .and(taskJpaEntity.month.eq(date.getMonthValue()))
                    .and(taskJpaEntity._deleted.eq(false))
            ).fetch();
    }

    @Override
    public long countTodayTask(
        final Long userId,
        final LocalDate date
    ) {
        final String sql = "SELECT COUNT(*) FROM tasks WHERE user_id = ? AND date = ? AND deleted = false";
        try {
            final Date dbDate = Date.valueOf(date);
            final Long result = jdbcTemplate.queryForObject(sql, new Object[]{userId, dbDate}, Long.class);
            if (result != null) {
                return result;
            }
            return 0L;
        } catch (DataAccessException ex) {
            throw CommonException.from(ex.getMessage(), DATA_ACCESS_EXCEPTION);
        }
    }

    @Override
    public Map<YearMonth, Long> findMonthlyTasksMapByUserIdAndDates(
        final Long userId,
        final LocalDate startDate,
        final LocalDate endDate
    ) {
        final int startYear = startDate.getYear();
        final int startMonth = startDate.getMonthValue();
        final int endYear = endDate.getYear();
        final int endMonth = endDate.getMonthValue();
        final Map<YearMonth, Long> map = new HashMap<>();
        final List<MonthlyTaskJpaEntity> monthlyTasks = queryFactory.select(monthlyTaskJpaEntity)
            .from(monthlyTaskJpaEntity)
            .where(
                monthlyTaskJpaEntity.userId.eq(userId),
                monthlyTaskJpaEntity.year.eq(startYear)
                    .and(monthlyTaskJpaEntity.month.goe(startMonth))
                    .or(monthlyTaskJpaEntity.year.gt(startYear)),
                monthlyTaskJpaEntity.year.eq(endYear)
                    .and(monthlyTaskJpaEntity.month.loe(endMonth))
                    .or(monthlyTaskJpaEntity.year.lt(endYear)),
                monthlyTaskJpaEntity._deleted.eq(false)
            )
            .fetch();
        for (final MonthlyTaskJpaEntity monthlyTask : monthlyTasks) {
            map.put(YearMonth.of(monthlyTask.getYear(), monthlyTask.getMonth()), monthlyTask.getId());
        }
        return map;
    }

    @Override
    public Optional<TaskLabelJpaEntity> findTaskLabelById(final Long id) {
        return Optional.ofNullable(
            queryFactory.selectFrom(taskLabelJpaEntity)
                .where(taskLabelJpaEntity.id.eq(id))
                .fetchOne()
        );
    }

    @Override
    public long countTaskLabel(final Long userId) {
        final String sql = "SELECT COUNT(*) FROM task_labels WHERE user_id = ?";
        try {
            return jdbcTemplate.queryForObject(sql, Long.class, userId);
        } catch (DataAccessException ex) {
            throw CommonException.from(ex.getMessage(), DATA_ACCESS_EXCEPTION);
        }
    }
}
