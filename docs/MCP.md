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

All tools are **read-only** — they report current state but do not control the robot.

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
