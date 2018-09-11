# Beispiel Quest

Hier sollte in ein bis zwei kurzen Sätzen der Kern der Quest erzählt werden, so dass man schnell weiß um was es in der Quest geht.

> Die `README` Dateien für Quests können auch von den Quest Schreibern entworfen werden um den Quest Entwicklern die Arbeit zu erleichtern.

## Ablauf

Ein kurzer Überblick über den schematischen Ablauf der Quest wie sie vom Quest Schreiber geplant wurde.

1. Start: Spieler kommt in die Nähe von Max
2. [NPC Max](#max) verwickelt den Spieler in ein Gespräch über Minecraft
3. Der Spieler soll folgendes erledigen:
    1. 10 Dirt abbauen
    2. 5 [Kieselsteine](#kieselsteine) sammeln (Custom Item das beim Abbau von Dirt droppt)
    3. Die Kieselsteine zu [Max](#max) bringen
4. Beim Abbau der Kieselsteine können [Kiesflöhe](#kiesfloehe) spawnen die den Spieler angreifen.

## NPCs

Hier kann eine kurze Auflistung der in der Quest vorkommenden NPCs stehen.

### Max

Beschreibung des NPCs in beliebiger Ausführung. Am besten mit Standardsatz, Charakterzügen etc. um ein wenig Hintergrund zu bekommen.

#### Ausrüstung

```yml
# Kann auch weggelassen werden
# Namen gibt es auf: https://minecraft-ids.grahamedgecombe.com/
hand: HAY_BLOCK
leggings: LEATHER_LEGGINGS
boots: LEATHER_BOOTS
chestplate: LEATHER_CHESTPLATE
# Base64 Encoded "Other Value" von der Seite: https://minecraft-heads.com/custom/heads
head: eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMjRkMTk0NDE5MTYxOTY4MWViNjg4MWE0OGFlMmM5NTRkNzViNDMzNmQ0YjUzODhhOGVlOWRlNTZiMTg0YjhjMiJ9fX0=
```

#### Standort

```yml
x: 1337
y: 64
z: -1337
world: test
```

## Items

Hier eine kurze Auflistung der in der Quest verwendeten Items geben und ob das Item nur für die Quest oder Global verfügbar ist.

### Kieselsteine

