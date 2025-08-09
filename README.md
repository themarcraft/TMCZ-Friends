# TMCZ Friends

![https://v4.themarcraft.de/images/friends/version.png](https://v4.themarcraft.de/images/friends/version.png)

## Funktionen

* Serverübergreifender Privater Chat (
  /msg).![https://v4.themarcraft.de/images/friends/msg.png](https://v4.themarcraft.de/images/friends/msg.png)
* Benachrichtigung, wenn Spieler joint/server wechselt an
  Freunde. ![https://v4.themarcraft.de/images/friends/stats.png](https://v4.themarcraft.de/images/friends/stats.png)
* Antworten auf Nachrichten (/reply).
* Speicherung via SQL Datenbank.
* Spieler Einstellungen (Benachrichtigung).
* Intelligenter Tab Completer.
* Einstellungen des Präfixes und anderen Nachrichten (Noch nicht ganz fertig).
* Freundschafts-Anfragen interaktiv im Chat
  annehmen. ![https://v4.themarcraft.de/images/friends/newrequest.png](https://v4.themarcraft.de/images/friends/newrequest.png)

## Befehle

* /msg - Versende über den ganzen Proxy Server hinweg Private Nachrichten.
* /r - Antworte auf Eingehende Private Nachrichten oder auf Ausgehende Nachrichten.
* /freunde hinzufügen - Sende eine Freundschafts-Anfrage an einen Spieler.
* /freunde annehmen/ablehnen - Nehme eine Freundschafts-Anfrage an oder lehne sie ab.
* /freunde einstellungen antworten - Stelle ein, ob du auf einkommende Nachrichten Antwortest, oder ob du an die Person
  schreibst, der du zuletzt geschrieben hast.
* /freunde einstellungen msg - Stelle ein, wer dir Private Nachrichten senden darf, alle, freunde oder niemand.
* /freunde einstellungen freundeOnline - Stelle ein, ob du eine Benachrichtigung bekommst, wenn ein Freund den Server
  betritt.
* /freunde einstellungen freundeServerWechsel - Stelle ein, ob du eine Benachrichtigung bekommst, wenn ein Freund den
  Server wechselt.
* /freunde anfragen - Siehe dir offene Anfragen an, die du bekommen hast.
* /freunde freunde - Siehe dir deine Freunde an.

## Permissions

* /freunde - themarcraft.friends
* /r - themarcraft.friends.reply
* /msg - themrcraft.friends.msg
* Maximale Anzahl von Freunden - themarcraft.friends.<Anzahl>

## Intelligenter Tab Completer

* Für jeden Befehl gibt es Tab Completer.
  ![https://v4.themarcraft.de/images/friends/autocomplete.png](https://v4.themarcraft.de/images/friends/autocomplete.png)
* Wenn du was eingibst, zeigt der Tab Completer nur Sachen an, die deine Eingabe enthalten.
* Bei /freunde hinzufügen werden Freunde nicht angezeigt.
* Bei /freunde entfernen werden nur Freunde angezeigt, die du auch entfernen kannst.
* Bei /freunde annehmen/ablehnen werden nur offene Freundschafts-Anfragen angezeigt.
* Bei /freunde einstellungen werden für die jeweilige Einstellung verfügbare Werte
  angezeigt. ![https://v4.themarcraft.de/images/friends/autocomplete2.png](https://v4.themarcraft.de/images/friends/autocomplete2.png)

## Config.yml

```yml
config:
  addon:
    gui: true
  prefix:
  friends: '&b&lFREUNDE &8» &r'
  log: '&a&lTheMarCraft.de &7» &r'
  database:
    host: 'DeinSqlHost.de'
    database: 'deineSQLDatenbank'
    user: 'DeinBenutzername'
    passwd: 'Dein SQL Passwort'
  messages:
    playerOnly: '&cDieser Befehl kann nur von einem Spieler ausgeführt werden'
    invalidPlayer: '&cBitte gebe einen gültigen Spielernamen an'
```

## Offizielle Add-Ons

### [TMCZ Friends GUI]('/projects/tmcz-friends-gui')

Dir sind die ganzen Befehle zu viel? - Das TMCZ Friends GUI vereinfacht die Einstellungen, annehmen und entfernen von
Freunden, Ansehen von Freunden und noch vieles mehr!

Deine Freunde: (vgl. /freunde freunde)
Offene Anfragen: (vgl. /freunde anfragen)
Anfrage annehmen/ablehnen: (vgl. /freunde annehmen/ablehnen)
Einstellungen: (vgl. /freunde einstellungen)

Du hast das Plugin heruntergeladen? Dann aktiviere es in der config.yml Datei des BungeeCord Plugins:)