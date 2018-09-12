# Quest Entwickler

Als Quest Entwickler transferiert man die fertig entworfenen Quests der Quest Schreiber in Konfigurationen. Diese Konfigurationen werden dann vom [RCQuests](../README.md) Plugin geladen und ermöglichen es Spielern die Quests im Spiel zu erleben.

## Getting Started

Alle Quests werden in [YAML](https://de.wikipedia.org/wiki/YAML) geschrieben, daher wird ein ordentlicher Editor, wie z.B. [Visual Studio Code](https://code.visualstudio.com/) oder [Notepad++](https://notepad-plus-plus.org/) empfohlen.
Außerdem werden alle Quest Configs mit dem [Versionskontroll-System Git](https://git-scm.com/downloads) verwaltet.

Um die Quest Configs auschecken und bearbeiten zu können wird ein Account im [Gitlab von Faldoria](https://git.faldoria.de/) benötigt. Für einen Zugang bitte an `xanily` wenden.

Als erstes muss das Git Repository mit den Quest Configs ausgecheckt werden.

```sh
git clone https://git.faldoria.de/plugin-configs/quests.git
```

Anschließend den gesamten Config Ordner z.B. mit VS Code öffnen.

```sh
cd quests
code .
```

### Ordner Struktur

Der Übersicht halber sollte alle Dateien die zu einer Quest gehören in einem Ordner und Sub-Ordnern gespeichert werden. NPCs die mehrere Quests verwenden sollten auf der höchst möglichen Ebene abgelegt werden. Das ermöglicht den Quests einen leichten Zugriff durch die Nutzung von [relativen Pfad Angaben](#relative-pfade).

Auch [Mobs](https://git.faldoria.de/raidcraft/rcmobs) und [Custom Items](https://git.faldoria.de/raidcraft/rcitems) sollten, wenn sie nur von der Quest verwendet werden, in dem jweiligen Quest Ordner abgelegt werden.

> Die eindeutige ID einer Quest besteht aus dem Ordner Pfad und dem Quest Namen.
> z.B. `ankanor.hauptquest.1-quest-name.quest-name`

```text
quests
└── ankanor
    ├── einfache-npcs
    │   └── ...
    ├── hauptquest
    │   ├── 1-quest-name
    │   │   ├── README.md
    │   │   ├── quest-name.quest.yml
    │   │   ├── npc-name.host.yml
    │   │   ├── npc-name.default.conv.yml
    │   │   ├── unterhaltung.conv.yml
    │   │   ├── quest-item.item.yml
    │   │   ├── quest-mob.mob.yml
    │   │   └── quest-mobgruppe.mob-group.yml
    │   ├── 2-quest-name
    │   │   └── ...
    │   └── quest-übergreifender-npc.host.yml
    └── nebenquest
        └── ...
```

### Bestandteile

Eine Quest besteht aus mehreren YAML Dateien (Endung `.yml`) die alle verschiedene Funktionen innerhalb der Quest übernehmen. Eine Quest kann dabei folgende Bestandteile haben.

| Bestandteil | Datei-Endung | Beschreibung |
| ----------- | ------------ | ------------ |
| Quest | `.quest.yml` | Die Hauptdatei einer Quest in welcher der Ablauf und die Aufgaben definiert werden. |
| Host/NPC | `.host.yml` | NPCs die beim Laden der Quest gespawnt werden und mit denen interagiert werden kann. |
| Conversation | `.conv.yml` | Eine Unterhaltung die während der Quest durch einen NPC oder anderweitig ausgeführt wird. |
| Default Conversation | `host-name.default.conv.yml` | Eine Standard Unterhaltung für den entsprechenden NPC die aufgerufen wird wenn keine andere Unterhaltung aktiv ist. |
| [Custom Items](https://git.faldoria.de/raidcraft/rcitems/blob/master/docs/ADMIN.md#config-dateien) | `.item.yml` | Ein Custom Item das temporär für die Quest existiert. Belohnungen sollten als normale Items über das Webinterface angelegt werden. |
| Mobs | `.mob.yml` | Custom Mobs die nur für die Quest relevant sind. |
| Mob Gruppen | `.mob-group.yml` | Eine Gruppierung von Custom Mobs um das Tracking und Spawnen zu erleichtern. |
| Loot-Tabellen | `.loot.yml` | Eine Loot Tabelle speziell für die Quest um z.B. die definierten Custom Items zu droppen wenn die Quest aktiv ist. |

> Klicke auf den Namen der Quest Komponente in der Tabelle um Details zur Konfiguration des jeweiligen Bestandteils zu erhalten.

## Quest Config

Jede Quest benötigt mindestens eine Quest Config Datei die mit `.quest.yml` endet. In dieser Config wird folgendes definiert:

* **Start der Quest** - Was führt dazu, dass die Quest startet/gestartet werden kann.
* **Aufgaben** - Was muss der Spieler für Aufgaben erfüllen, damit die Quest abgeschlossen wird.
* **Ende der Quest** - Was führt dazu, dass die Quest beendet wird.
* **Belohnungen** - Was für Belohnungen erhält der Spieler für das Beenden der Quest.

In den anschließenden Erklärungen wird folgende [Beispiel Quest](./example-quest/) verwendet. Die dort verwendete `README.md` sollte bei allen Quests von den Quest Schreibern als Vorlage verwendet werden.

Die gesamte Config der Beispiel Quest befindet sich im [example-quest/](./example-quest) Ordner.