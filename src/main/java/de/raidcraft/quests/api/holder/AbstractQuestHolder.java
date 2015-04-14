package de.raidcraft.quests.api.holder;

import de.raidcraft.api.quests.QuestException;
import de.raidcraft.quests.api.quest.Quest;
import de.raidcraft.quests.api.quest.QuestTemplate;
import de.raidcraft.quests.ui.QuestInventory;
import de.raidcraft.util.CaseInsensitiveMap;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * @author Silthus
 */
@Data
@ToString(exclude = "activeQuests")
@EqualsAndHashCode(of = "id")
public abstract class AbstractQuestHolder implements QuestHolder {

    private final int id;
    private final UUID player;
    private final Map<String, Quest> activeQuests = new CaseInsensitiveMap<>();
    private final QuestInventory questInventory;

    public AbstractQuestHolder(int id, UUID player) {

        this.id = id;
        this.player = player;
        this.questInventory = new QuestInventory(this);
    }

    @Override
    public UUID getPlayerId() {

        return getPlayer() == null ? player : getPlayer().getUniqueId();
    }

    @Override
    public Player getPlayer() {

        return Bukkit.getPlayer(player);
    }

    @Override
    public boolean hasQuest(String quest) {

        return activeQuests.containsKey(quest)
                || getAllQuests().stream().filter(q -> q.getFullName().equals(quest)).findAny().isPresent();
    }

    @Override
    public boolean hasActiveQuest(String name) {

        return activeQuests.containsKey(name) && activeQuests.get(name).isActive();
    }

    @Override
    public Optional<Quest> getQuest(QuestTemplate questTemplate) {

        return getQuest(questTemplate.getName());
    }

    @Override
    public Optional<Quest> getQuest(String name) {

        if (activeQuests.containsKey(name)) {
            return Optional.of(activeQuests.get(name));
        }
        List<Quest> foundQuests = getAllQuests().stream()
                .filter(quest -> quest.getFriendlyName().toLowerCase().contains(name.toLowerCase()))
                .map(quest -> quest).collect(Collectors.toList());
        if (foundQuests.isEmpty() || foundQuests.size() > 1) {
            return Optional.empty();
        }
        return Optional.of(foundQuests.get(0));
    }

    @Override
    public List<Quest> getCompletedQuests() {

        return getAllQuests().stream()
                .filter(Quest::isCompleted)
                .map(quest -> quest)
                .collect(Collectors.toList());
    }

    @Override
    public List<Quest> getActiveQuests() {

        return getAllQuests().stream()
                .filter(Quest::isActive)
                .map(quest -> quest)
                .collect(Collectors.toList());
    }

    @Override
    public Quest startQuest(QuestTemplate template) throws QuestException {

        if (template.isLocked() && !getPlayer().hasPermission("rcquests.admin")) {
            throw new QuestException("Diese Quest ist aktuell gesperrt und kann nicht angenommen werden.");
        }
        return null;
    }

    @Override
    public void addQuest(Quest quest) {

        if (quest.isActive()) {
            activeQuests.put(quest.getFullName(), quest);
        }
    }

    @Override
    public void removeQuest(Quest quest) {

        activeQuests.remove(quest.getFullName());
    }
}