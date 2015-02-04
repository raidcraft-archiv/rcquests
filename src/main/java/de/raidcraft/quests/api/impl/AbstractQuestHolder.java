package de.raidcraft.quests.api.impl;

import de.raidcraft.quests.api.Quest;
import de.raidcraft.quests.api.QuestException;
import de.raidcraft.quests.api.QuestHolder;
import de.raidcraft.quests.api.QuestTemplate;
import de.raidcraft.util.CaseInsensitiveMap;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * @author Silthus
 */
@Data
@ToString(exclude = "allQuests")
@EqualsAndHashCode(of = "id")
public abstract class AbstractQuestHolder implements QuestHolder {

    private final int id;
    private final UUID player;
    private final Map<String, Quest> allQuests = new CaseInsensitiveMap<>();

    public AbstractQuestHolder(int id, UUID player) {

        this.id = id;
        this.player = player;
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

        return allQuests.containsKey(quest);
    }

    @Override
    public boolean hasActiveQuest(String name) {

        return hasQuest(name) && allQuests.get(name).isActive();
    }

    @Override
    public Quest getQuest(QuestTemplate questTemplate) {

        return allQuests.get(questTemplate.getId());
    }

    @Override
    public Quest getQuest(String name) throws QuestException {

        if (allQuests.containsKey(name)) {
            return allQuests.get(name);
        }
        List<Quest> foundQuests = allQuests.values().stream()
                .filter(quest -> quest.getFriendlyName().toLowerCase().contains(name.toLowerCase()))
                .map(quest -> quest).collect(Collectors.toList());
        if (foundQuests.isEmpty()) {
            throw new QuestException("Du hast keine Quest mit dem Namen: " + name);
        }
        if (foundQuests.size() > 1) {
            throw new QuestException("Du hast mehrere Quests mit dem Namen " + name + ": " + StringUtils.join(foundQuests, ", "));
        }
        return foundQuests.get(0);
    }

    @Override
    public List<Quest> getAllQuests() {

        return new ArrayList<>(allQuests.values());
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

        allQuests.put(quest.getFullName(), quest);
    }

    @Override
    public void abortQuest(Quest quest) {

        Quest remove = allQuests.remove(quest.getFullName());
        if (remove != null) {
            remove.abort();
        }
    }
}