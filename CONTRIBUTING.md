# Contributing to Eclipse plug-in for Copilot

Thank you for your interest in contributing to Eclipse plug-in for Copilot!

There are many ways in which you can contribute, beyond writing code. Please read the following document to check how you can get involved.

## Questions and Feedback
Have questions or feedback? Feel free to let us know! You can share your thoughts in the Discussion channel: [Discussion](https://github.com/eclipse-copilot/eclipse-copilot/discussions)

## Reporting Issues
You can report issues whenever:
- Identify a reproducible problem within the extension
- Have a feature request

### Looking for an Existing Issue
Before creating a new issue, please do a search to see if the issue or feature request has already been filed.

If you find your issue already exists, make relevant comments and add your reaction:
- 👍 - upvote
- 👎 - downvote
 
### Writing Good Bug Reports and Feature Requests
In order to let us know better about the issue, please make sure the following items are included with each issue:
- The version of Eclipse and the plugin
- Your operating system
- Reproducible steps
- What you expected to see, versus what you actually saw
- Images, animations, or a link to a video showing the issue occurring
- A code snippet that demonstrates the issue or a link to a code repository the developers can easily pull down to recreate the issue locally
- Errors from the Error Log View (open from the menu: Window > Show View > Error Log)
 
## Contributing Fixes
If you are interested in writing code to fix issues, please check the following content to see how to set up the developing environment.

### Overview
The plug-in has two major modules:
- Copilot Core - The core part of the plug-in, including the Copilot Language Server, which enables Eclipse to integrate with GitHub Copilot via the language server protocol.
- Copilot UI - Handles all user interface components and interactions, including displaying Copilot suggestions, providing Chat view with ask and agnet mode and integrating Copilot features into the Eclipse IDE's UI.

### Setup
1. Fork and clone the repository: `git clone https://github.com/eclipse-copilot/eclipse-copilot.git`
2. Install Copilot Language Server: `cd org.eclipse.copilot.core/copilot-agent && npm i`
3. In Eclipse, click `File` > `Import...` > `Maven` > `Existing Maven Projects`, and select the root folder of the repo and import.

### Debugging
You can create a new `Eclipse Application` launch configuration for debugging purpose.

### Format
The project uses Checkstyle to check for code format, Checkstyle rules can be found at `checkstyle.xml` at project root.

### Build
To build the plugin, run `./mvnw clean package`. The zip file can be found at `org.eclipse.copilot.repository/target/org.eclipse.copilot.repository-*.zip`