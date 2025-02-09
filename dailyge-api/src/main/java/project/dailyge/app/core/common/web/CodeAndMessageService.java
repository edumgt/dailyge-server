package project.dailyge.app.core.common.web;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import project.dailyge.entity.codeandmessage.CodeAndMessageEntityReadRepository;
import project.dailyge.entity.codeandmessage.CodeAndMessageEntityWriteRepository;
import project.dailyge.entity.codeandmessage.CodeAndMessageEntityWriteService;
import project.dailyge.entity.codeandmessage.CodeAndMessageJpaEntity;
import project.dailyge.entity.codeandmessage.CodeAndMessageReadService;
import project.dailyge.entity.codeandmessage.CodeAndMessages;

import java.util.List;

@Service
public class CodeAndMessageService implements CodeAndMessageReadService, CodeAndMessageEntityWriteService {

    private final CodeAndMessageEntityReadRepository codeAndMessageReadRepository;
    private final CodeAndMessageEntityWriteRepository codeAndMessageWriteRepository;

    public CodeAndMessageService(
        final CodeAndMessageEntityReadRepository codeAndMessageReadRepository,
        final CodeAndMessageEntityWriteRepository codeAndMessageWriteRepository
    ) {
        this.codeAndMessageReadRepository = codeAndMessageReadRepository;
        this.codeAndMessageWriteRepository = codeAndMessageWriteRepository;
    }

    @Override
    @Transactional
    public void saveAll(final CodeAndMessages codeAndMessages) {
        final CodeAndMessages findCodeAndMessages = new CodeAndMessages(codeAndMessageReadRepository.findAll());
        findCodeAndMessages.updateAll(codeAndMessages.getCodeAndMessages());
        codeAndMessageWriteRepository.saveAll(findCodeAndMessages.getCodeAndMessages());
    }

    @Override
    public List<CodeAndMessageJpaEntity> findAll() {
        return codeAndMessageReadRepository.findAll();
    }
}
