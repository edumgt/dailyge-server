package project.dailyge.app.core.anniversary.facade;

import project.dailyge.app.common.annotation.FacadeLayer;
import project.dailyge.app.core.anniversary.application.AnniversaryWriteService;
import project.dailyge.app.core.anniversary.application.command.AnniversaryCreateCommand;
import project.dailyge.app.core.anniversary.application.command.AnniversaryUpdateCommand;
import project.dailyge.app.core.common.auth.DailygeUser;
import project.dailyge.app.core.emoji.application.EmojiReadService;

@FacadeLayer(value = "AnniversaryFacade")
public class AnniversaryFacade {

    private final EmojiReadService emojiReadService;
    private final AnniversaryWriteService anniversaryWriteService;

    public AnniversaryFacade(
        final EmojiReadService emojiReadService,
        final AnniversaryWriteService anniversaryWriteService
    ) {
        this.emojiReadService = emojiReadService;
        this.anniversaryWriteService = anniversaryWriteService;
    }

    public Long save(
        final DailygeUser dailygeUser,
        final AnniversaryCreateCommand command
    ) {
        if (command.emojiId() != null) {
            emojiReadService.validateExists(command.emojiId());
        }
        return anniversaryWriteService.save(dailygeUser, command);
    }

    public void update(
        final DailygeUser dailygeUser,
        final Long anniversaryId,
        final AnniversaryUpdateCommand command
    ) {
        if (command.emojiId() != null) {
            emojiReadService.validateExists(command.emojiId());
        }
        anniversaryWriteService.update(dailygeUser, anniversaryId, command);
    }
}
