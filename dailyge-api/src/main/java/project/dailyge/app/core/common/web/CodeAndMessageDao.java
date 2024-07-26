package project.dailyge.app.core.common.web;

import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import project.dailyge.entity.codeandmessage.CodeAndMessageEntityReadRepository;
import project.dailyge.entity.codeandmessage.CodeAndMessageEntityWriteRepository;
import project.dailyge.entity.codeandmessage.CodeAndMessageJpaEntity;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CodeAndMessageDao implements CodeAndMessageEntityReadRepository, CodeAndMessageEntityWriteRepository {

    private static final String SELECT_ALL = "SELECT c FROM code_and_message c";
    private final EntityManager entityManager;

    @Override
    public List<CodeAndMessageJpaEntity> saveAll(final List<CodeAndMessageJpaEntity> codeAndMessages) {
        for (final CodeAndMessageJpaEntity entity : codeAndMessages) {
            entityManager.persist(entity);
        }
        return entityManager.createQuery(SELECT_ALL, CodeAndMessageJpaEntity.class)
            .getResultList();
    }

    @Override
    public List<CodeAndMessageJpaEntity> findAll() {
        return entityManager.createQuery(SELECT_ALL, CodeAndMessageJpaEntity.class)
            .getResultList();
    }
}
