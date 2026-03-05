package com.boomaa.opends.mcp;

import com.boomaa.opends.util.Debug;
import com.boomaa.opends.util.EventSeverity;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.Executors;

/**
 * MCP (Model Context Protocol) server for OpenDS.
 * Exposes robot status data via JSON-RPC 2.0 over HTTP (Streamable HTTP transport).
 */
public class McpServer {
    public static final String SERVER_NAME = "opends-mcp";
    public static final String SERVER_VERSION = "0.1.0";
    public static final String MCP_PROTOCOL_VERSION = "2025-03-26";

    private final HttpServer server;
    private final int port;

    public McpServer(int port) throws IOException {
        this.port = port;
        this.server = HttpServer.create(new InetSocketAddress(port), 0);
        this.server.setExecutor(Executors.newFixedThreadPool(4));
        this.server.createContext("/mcp", this::handleMcp);
    }

    public void start() {
        server.start();
        Debug.println("MCP server started on port " + port, EventSeverity.INFO, false, true);
    }

    public void stop() {
        server.stop(0);
        Debug.println("MCP server stopped", EventSeverity.INFO, false, true);
    }

    private void handleMcp(HttpExchange exchange) throws IOException {
        if ("POST".equalsIgnoreCase(exchange.getRequestMethod())) {
            handlePost(exchange);
        } else if ("OPTIONS".equalsIgnoreCase(exchange.getRequestMethod())) {
            setCorsHeaders(exchange);
            exchange.sendResponseHeaders(204, -1);
            exchange.close();
        } else {
            sendError(exchange, 405, "Method Not Allowed");
        }
    }

    private void handlePost(HttpExchange exchange) throws IOException {
        setCorsHeaders(exchange);
        String body = readBody(exchange.getRequestBody());
        Debug.println("MCP request: " + body, EventSeverity.INFO, true);

        try {
            JsonMap request = JsonParser.parseObject(body);
            String method = request.getString("method");
            Object id = request.get("id");
            JsonMap params = request.getMap("params");

            String response;
            if (method == null) {
                response = JsonRpc.errorResponse(id, -32600, "Invalid Request: missing method");
            } else {
                response = handleMethod(method, params, id);
            }

            byte[] responseBytes = response.getBytes(StandardCharsets.UTF_8);
            exchange.getResponseHeaders().set("Content-Type", "application/json");
            exchange.sendResponseHeaders(200, responseBytes.length);
            OutputStream os = exchange.getResponseBody();
            os.write(responseBytes);
            os.close();
        } catch (Exception e) {
            Debug.println("MCP error: " + e.getMessage(), EventSeverity.ERROR, true);
            String errorResp = JsonRpc.errorResponse(null, -32700, "Parse error: " + e.getMessage());
            byte[] errorBytes = errorResp.getBytes(StandardCharsets.UTF_8);
            exchange.getResponseHeaders().set("Content-Type", "application/json");
            exchange.sendResponseHeaders(200, errorBytes.length);
            OutputStream os = exchange.getResponseBody();
            os.write(errorBytes);
            os.close();
        }
    }

    private String handleMethod(String method, JsonMap params, Object id) {
        switch (method) {
            case "initialize":
                return handleInitialize(id);
            case "notifications/initialized":
                return JsonRpc.resultResponse(id, new JsonMap());
            case "tools/list":
                return handleToolsList(id);
            case "tools/call":
                return handleToolsCall(params, id);
            case "ping":
                return JsonRpc.resultResponse(id, new JsonMap());
            default:
                return JsonRpc.errorResponse(id, -32601, "Method not found: " + method);
        }
    }

    private String handleInitialize(Object id) {
        JsonMap capabilities = new JsonMap();
        capabilities.put("tools", new JsonMap());

        JsonMap serverInfo = new JsonMap();
        serverInfo.put("name", SERVER_NAME);
        serverInfo.put("version", SERVER_VERSION);

        JsonMap result = new JsonMap();
        result.put("protocolVersion", MCP_PROTOCOL_VERSION);
        result.put("capabilities", capabilities);
        result.put("serverInfo", serverInfo);

        return JsonRpc.resultResponse(id, result);
    }

    private String handleToolsList(Object id) {
        JsonList tools = McpTools.getToolDefinitions();
        JsonMap result = new JsonMap();
        result.put("tools", tools);
        return JsonRpc.resultResponse(id, result);
    }

    private String handleToolsCall(JsonMap params, Object id) {
        if (params == null) {
            return JsonRpc.errorResponse(id, -32602, "Invalid params: missing params");
        }
        String toolName = params.getString("name");
        if (toolName == null) {
            return JsonRpc.errorResponse(id, -32602, "Invalid params: missing tool name");
        }
        JsonMap toolArgs = params.getMap("arguments");
        if (toolArgs == null) {
            toolArgs = new JsonMap();
        }

        try {
            JsonMap toolResult = McpTools.callTool(toolName, toolArgs);
            return JsonRpc.resultResponse(id, toolResult);
        } catch (Exception e) {
            return JsonRpc.errorResponse(id, -32603, "Tool execution error: " + e.getMessage());
        }
    }

    private void setCorsHeaders(HttpExchange exchange) {
        exchange.getResponseHeaders().set("Access-Control-Allow-Origin", "*");
        exchange.getResponseHeaders().set("Access-Control-Allow-Methods", "POST, OPTIONS");
        exchange.getResponseHeaders().set("Access-Control-Allow-Headers", "Content-Type");
    }

    private void sendError(HttpExchange exchange, int code, String message) throws IOException {
        byte[] bytes = message.getBytes(StandardCharsets.UTF_8);
        exchange.sendResponseHeaders(code, bytes.length);
        OutputStream os = exchange.getResponseBody();
        os.write(bytes);
        os.close();
    }

    private String readBody(InputStream is) throws IOException {
        byte[] buf = new byte[4096];
        StringBuilder sb = new StringBuilder();
        int bytesRead;
        while ((bytesRead = is.read(buf)) != -1) {
            sb.append(new String(buf, 0, bytesRead, StandardCharsets.UTF_8));
        }
        return sb.toString();
    }
}
