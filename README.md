# Door Lock Monitoring System

A JavaFX desktop app that simulates monitoring the lock status of multiple doors across a building. Users can define expected lock states per mode, track real-time status, and create custom monitoring modes.

---

## Requirements

- **Java 25** (JDK 25)
- **Maven 3.9+**
- Internet access on first run (Maven downloads JavaFX 25.0.1 automatically)

---

## How to Run

Open a terminal in the `final/` folder, then run:

```
mvn javafx:run
```

> If `mvn javafx:run` fails with "goal not found", make sure your Maven settings include the OpenJFX plugin group. Create or edit `C:\Users\<you>\.m2\settings.xml` and add:
>
> ```xml
> <settings>
>   <pluginGroups>
>     <pluginGroup>org.openjfx</pluginGroup>
>   </pluginGroups>
> </settings>
> ```

---

## How to Use

### Modes (left panel)
Each button switches the system into a preset monitoring mode. When you click a mode, the **Expected Status** for every door is set automatically and all doors are checked immediately.

|Example Modes| Description                                              |
|-------------|----------------------------------------------------------|
| AM Mode     | Morning hours - main doors unlocked, back doors locked   |
| PM Mode     | Evening hours - most doors locked                        |
| Lockdown    | All doors must be locked                                 |
| Fire Alarm  | All doors must be unlocked for evacuation                |
| Maintenance | Service access - most doors unlocked, office door locked |

### Custom Modes
Click **+ Add Mode** at the bottom of the left panel to create your own mode:
1. Enter a name for the mode
2. Choose `Locked` or `Unlocked` for each door
3. Click **Save Mode** - the button appears in the panel and works like any built-in mode

To **delete** a custom mode, click the red **×** button next to it. Built-in modes cannot be deleted.

To **rename** a custom mode, **double-click** its button, type the new name, and press Enter.

### Door Table
| Column          | Description                                              |
|-----------------|----------------------------------------------------------|
| Door Name       | Double-click to rename a door inline                     |
| Expected Status | ComboBox - choose what this door *should* be in the current mode. Changes are saved to that mode |
| Current Status  | Read-only - represents what the physical hardware reports |
| Result          | `Safe` (green) if current matches expected, `Issue` (red) if not |

### Reset
Click **Reset** to restore all doors to their default statuses and clear the event log. Your saved custom modes and any mode customizations are kept.

### Event Log
Every status change and mode switch is recorded in the log at the bottom of the main panel.

---

## Project Structure

```
final/
├── pom.xml
└── src/
    └── main/
        ├── java/doorlock/
        │   ├── javaFX.java     # Main application
        │   └── Door.java       # Door model
        └── resources/doorlock/
            └── style.css       # All visual styling
```

---

## Notes

- The app does not connect to real hardware - **Current Status** must be updated manually (or extended to read from a hardware API)
- Custom modes added during a session are not saved to disk; they reset when the app is closed
