# OpenDS MCP Server

OpenDS includes a built-in [Model Context Protocol (MCP)](https://modelcontextprotocol.io/) server that exposes robot status data to AI assistants like GitHub Copilot in VS Code and Claude Desktop. This gives your AI tools real-time read access to your robot's state — battery voltage, connection status, CAN bus metrics, and more.

## Quick Start

1. **Build and run OpenDS** as normal:
   ```bash
   mvn package -q
   java -jar target/open-ds-*-jar-with-dependencies.jar
   ```

2. The MCP server starts automatically on **port 8765**. You'll see this in the log:
   ```
   MCP server started on port 8765
   ```

3. Connect your AI client (see setup sections below).

## Available Tools

| Tool | Description |
|------|-------------|
| `get_robot_status` | Enabled state, operating mode (Teleop/Auto/Test), estop, robot code status, battery voltage |
| `get_battery_voltage` | Current battery voltage as a string and numeric value |
| `get_robot_stats` | Disk space, RAM, CPU %, CAN bus metrics, RoboRIO/WPILib versions, disable faults, rail faults |
| `get_connection_info` | Robot and FMS connection status, simulated vs real, USB mode |
| `get_match_info` | Alliance color/number, team number, protocol year, match time, game data |
| `get_button_map` | Returns the button/axis label mapping from `button-map.json` — call this first to learn what each button does |
| `press_button` | Press or release a button on a virtual joystick (params: `joystick`, `button`, `pressed`) |
| `set_axis` | Set an axis value on a virtual joystick (params: `joystick`, `axis`, `value`) |

The first five tools are **read-only**. The last three allow **virtual joystick control** — they create a virtual gamepad that sends inputs to the robot just like a real USB controller.

## Button Map Configuration

OpenDS supports a `button-map.json` file in the working directory that labels joystick buttons and axes with human-readable descriptions. These labels appear on the virtual gamepad UI and are returned by the `get_button_map` MCP tool.

### Example `button-map.json`

```json
{
  "joysticks": {
    "0": {
      "name": "Driver Controller",
      "buttons": {
        "1": "Toggle Floor Intake",
        "2": "Auto Feed (fires ball into shooter)",
        "8": "Spin Up Shooter (max velocity)"
      },
      "axes": {
        "2": "Limelight Aim Trigger (>0.5 activates)"
      },
      "pov": {
        "up": "Climber Up",
        "down": "Climber Down"
      },
      "notes": "To fire: button 8 to spin up, wait ~2s, button 2 to feed"
    }
  }
}
```

Button numbers are **1-based** to match WPILib convention. Joystick indices are **0-based** (0–5).

### AI Agent Workflow

1. Call `get_button_map` to learn what each button/axis does on the connected robot.
2. Use `press_button` and `set_axis` to control the robot through the virtual joystick.

```bash
# Learn the controls
curl -s -X POST http://localhost:8765/mcp \
  -H 'Content-Type: application/json' \
  -d '{"jsonrpc":"2.0","id":1,"method":"tools/call","params":{"name":"get_button_map","arguments":{}}}'

# Spin up the shooter (button 8)
curl -s -X POST http://localhost:8765/mcp \
  -H 'Content-Type: application/json' \
  -d '{"jsonrpc":"2.0","id":2,"method":"tools/call","params":{"name":"press_button","arguments":{"joystick":0,"button":8,"pressed":true}}}'

# Feed the game piece (button 2)
curl -s -X POST http://localhost:8765/mcp \
  -H 'Content-Type: application/json' \
  -d '{"jsonrpc":"2.0","id":3,"method":"tools/call","params":{"name":"press_button","arguments":{"joystick":0,"button":2,"pressed":true}}}'

# Release buttons when done
curl -s -X POST http://localhost:8765/mcp \
  -H 'Content-Type: application/json' \
  -d '{"jsonrpc":"2.0","id":4,"method":"tools/call","params":{"name":"press_button","arguments":{"joystick":0,"button":2,"pressed":false}}}'
```

## VS Code Setup (GitHub Copilot)

1. Open your project in VS Code.

2. Create or edit `.vscode/mcp.json` in your workspace:
   ```json
   {
     "servers": {
       "opends": {
         "type": "http",
         "url": "http://localhost:8765/mcp"
       }
     }
   }
   ```

3. Make sure OpenDS is running.

4. In Copilot Chat (Agent mode), the OpenDS tools will appear automatically. You can ask things like:
   - *"What's the robot's battery voltage?"*
   - *"Is the robot connected?"*
   - *"Show me the CAN bus statistics"*

> **Tip:** You can also add this to your **User** settings (`~/Library/Application Support/Code/User/mcp.json` on macOS) so it's available across all workspaces.

## Claude Desktop Setup

1. Open Claude Desktop settings:
   - macOS: `Claude` → `Settings` → `Developer` → `Edit Config`
   - This opens `~/Library/Application Support/Claude/claude_desktop_config.json`

2. Add the OpenDS server:
   ```json
   {
     "mcpServers": {
       "opends": {
         "url": "http://localhost:8765/mcp"
       }
     }
   }
   ```

3. Restart Claude Desktop.

4. Make sure OpenDS is running, then you'll see the OpenDS tools available in Claude's tool list (look for the hammer icon).

## CLI Options

| Flag | Description |
|------|-------------|
| `--disable-mcp` | Don't start the MCP server |
| `--mcp-port <port>` | Use a custom port (default: `8765`) |

Example with custom port:
```bash
java -jar target/open-ds-*-jar-with-dependencies.jar --mcp-port 9000
```

If using a custom port, update the URL in your client config accordingly.

## Protocol Details

The MCP server uses the **Streamable HTTP** transport:

- **Endpoint:** `POST http://localhost:8765/mcp`
- **Protocol:** JSON-RPC 2.0
- **MCP Version:** `2025-03-26`

### Example Request

```bash
curl -s -X POST http://localhost:8765/mcp \
  -H 'Content-Type: application/json' \
  -d '{"jsonrpc":"2.0","id":1,"method":"tools/call","params":{"name":"get_robot_status","arguments":{}}}'
```

### Example Response

```json
{
  "jsonrpc": "2.0",
  "id": 1,
  "result": {
    "content": [{
      "type": "text",
      "text": "{\"robotConnected\":true,\"enabled\":false,\"mode\":\"Teleoperated\",\"estop\":false,\"batteryVoltage\":\"12.54 V\",\"robotCodeRunning\":true,\"robotCodeStatus\":\"Running\"}"
    }],
    "isError": false
  }
}
```

## Troubleshooting

- **"Address already in use"** — Another instance of OpenDS (or another process) is using port 8765. Kill it or use `--mcp-port` to pick a different port.
- **Connection refused** — Make sure OpenDS is running before connecting your AI client.
- **Tools not appearing in VS Code** — Verify the `.vscode/mcp.json` file is correctly formatted and restart VS Code.
- **Tools not appearing in Claude** — Restart Claude Desktop after editing the config. Check for JSON syntax errors in the config file.
