package org.eclipse.copilot.ui.extensions;

/**
 * Example implementation of IMcpRegistrationProvider.
 * This demonstrates how to contribute MCP server configurations via the extension point.
 */
public class ExampleMcpRegistrationProvider implements IMcpRegistrationProvider {

  @Override
  public String getMcpServerConfigurations() {
    return """
        {
          "servers": {
            "memory": {
              "command": "npx",
              "args": [
                "-y",
                "@modelcontextprotocol/server-memory"
              ]
            }
          }
        }
        """;
  }

  @Override
  public String getProviderId() {
    return "com.microsoft.copilot.eclipse.ui.exampleMcpRegistrationProvider";
  }
}
