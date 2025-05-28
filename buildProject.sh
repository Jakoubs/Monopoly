#!/bin/bash

# Projektverzeichnis
PROJECT_DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"

# Kompilieren mit sbt
echo "Kompiliere das Projekt..."
cd "$PROJECT_DIR"
sbt clean compile package

# Prüfe, ob die Kompilierung erfolgreich war
if [ $? -ne 0 ]; then
    echo "Fehler bei der Kompilierung!"
    exit 1
fi

# Finde die generierte JAR-Datei (angepasster Suchpfad)
JAR_FILE=$(find "$PROJECT_DIR/target/scala-3.3.5" -name "monopoly*.jar" -o -name "Monopoly*.jar" 2>/dev/null | head -n 1)

# Wenn keine JAR gefunden wurde, suche in anderen möglichen Verzeichnissen
if [ -z "$JAR_FILE" ]; then
    echo "Suche JAR-Datei in alternativen Verzeichnissen..."
    JAR_FILE=$(find "$PROJECT_DIR/target" -name "*.jar" 2>/dev/null | head -n 1)
fi

if [ -z "$JAR_FILE" ]; then
    echo "Keine JAR-Datei gefunden!"
    echo "Inhalt des target-Verzeichnisses:"
    ls -R "$PROJECT_DIR/target"
    exit 1
fi

echo "JAR-Datei gefunden: $JAR_FILE"

# Erstelle Desktop-Datei
DESKTOP_FILE="$HOME/.local/share/applications/monopoly.desktop"
ICON_PATH="$PROJECT_DIR/src/main/resources/icon.png"

# Prüfe, ob ein Icon existiert, ansonsten verwende ein Standard-Icon
if [ ! -f "$ICON_PATH" ]; then
    echo "Kein Icon unter $ICON_PATH gefunden, verwende Standard-Java-Icon"
    ICON_PATH="/usr/share/icons/hicolor/48x48/apps/java.png"
fi

# Erstelle .desktop Datei
cat > "$DESKTOP_FILE" << EOF
[Desktop Entry]
Version=1.0
Type=Application
Name=Monopoly
Comment=Ein Monopoly Spiel
Exec=java -jar "$JAR_FILE"
Icon=$ICON_PATH
Terminal=false
Categories=Game;
EOF

# Mache .desktop Datei ausführbar
chmod +x "$DESKTOP_FILE"

echo "Installation abgeschlossen!"
echo "Die Desktop-Verknüpfung wurde erstellt unter: $DESKTOP_FILE"