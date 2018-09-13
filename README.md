# RCQuests

Das RCQuests Plugin ermöglicht es durch das Schreiben von Konfigurations Dateien auf einfache und schnelle Art Quests zu erstellen.

Durch die Nutzung der [ART API](https://git.faldoria.de/raidcraft/raidcraft-api) ist es Entwicklern möglich ohne direkte Abhängigkeit das Quest Plugin um viele Aktionen, Voraussetzungen und Trigger zu erweitern.

## Getting Started

* [Project Details](https://git.faldoria.de/raidcraft/rcquests)
* [Source Code](https://git.faldoria.de/raidcraft/rcquests/tree/master)
* [Latest Stable Download](https://ci.faldoria.de/view/RaidCraft/job/RCQuests/lastStableBuild)
* [Issue Tracker](https://git.faldoria.de/raidcraft/rcquests/issues)
* [Quest Developer Documentation](https://git.faldoria.de/plugin-configs/quests/tree/master/docs/QUEST-DEVELOPER.md)
* [Developer Documentation](docs/DEVELOPER.md)
* [Admin Documentation](docs/ADMIN.md)

### Prerequisites

Das `RCQuests` Plugin benötigt eine Verbindung zu einer MySQL Datenbank und ist von folgenden Plugins abhängig.

| Plugin | Optional | Beschreibung |
| ------ | -------- | ------------ |
| [RaidCraft-API](https://git.faldoria.de/raidcraft/raidcraft-api) | Nein | Stellt die ART API und weitere Basis Funktionen zur Verfügung. |
| [RCItems](https://git.faldoria.de/raidcraft/rcitems) | Ja | Ermöglicht die Nutzung von Custom Items in Quests. |
| [RCMobs](https://git.faldoria.de/raidcraft/rcmobs) | Ja | Ermöglicht die Nutzung von Custom Mobs in Quests. |
| [RCConversations](https://git.faldoria.de/raidcraft/conversations) | Ja | Ermöglicht es in Quests Unterhaltungen zu führen. |
| [RCSkills](https://git.faldoria.de/raidcraft/rcskills) | Ja | Ermöglicht die Vergabe von EXP. |

### Installation

Beim ersten Start des Servers wird eine `database.yml` und eine `config.yml` angelegt. Am besten den Server direkt nochmal stoppen und die Angaben in der `database.yml` bearbeiten.

Die `config.yml` enthält folgende defaults:

```yaml
# Der Ordner aus dem alle Quests geladen werden
quests-base-folder: quests
# Die maximale Anzahl an Quests die ein Spieler annehmen kann
max-quests: 27
# Verzögerung in Ticks bis die Quests nach dem Login geladen werden
quest-load-delay: 30
# Verzögerung in Ticks bis tägliche Quests geladen werden
quest-pool-delay: 100
```