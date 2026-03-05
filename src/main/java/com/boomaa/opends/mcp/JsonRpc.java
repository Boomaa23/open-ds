package com.boomaa.opends.mcp;

/**
 * JSON-RPC 2.0 response builder for MCP protocol.
 */
public final class JsonRpc {
    private static final String JSONRPC_VERSION = "2.0";

    private JsonRpc() {
    }

    public static String resultResponse(Object id, Object result) {
        JsonMap response = new JsonMap();
        response.put("jsonrpc", JSONRPC_VERSION);
        response.put("id", id);
        response.put("result", result);
        return response.toJson();
    }

    public static String errorResponse(Object id, int code, String message) {
        JsonMap error = new JsonMap();
        error.put("code", code);
        error.put("message", message);

        JsonMap response = new JsonMap();
        response.put("jsonrpc", JSONRPC_VERSION);
        response.put("id", id);
        response.put("error", error);
        return response.toJson();
    }
}
