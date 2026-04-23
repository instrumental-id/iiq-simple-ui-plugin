import {PluginService} from "./PluginService";

// This will be available at runtime
declare var jQuery: any;

export class IIDSimpleUIPluginSnippet {
    private pluginService: PluginService;

    constructor() {
        this.pluginService = new PluginService();
    }

    async findIdentityId() {
        // The identity ID will be the 'id' query parameter in the URL, e.g. https://host/identity?id=12345
        const urlParams = new URLSearchParams(window.location.search);
        const identityId = urlParams.get("id");

        if (!identityId) {
            throw new Error("No identity ID found in URL");
        }

        return identityId;
    }

    async render() {
        const identityId = await this.findIdentityId();
        const configuration = await this.pluginService.getConfiguration();

        console.log("Retrieved button configuration", configuration);

        if (configuration.buttons && configuration.buttons.length > 0) {
            const elements = document.getElementById("appTable")?.getElementsByTagName("tr")
            if (elements) {
                const insertionPoint = elements[0]
                const buttonRow = document.createElement("tr");
                const buttonCell = document.createElement("td");
                buttonCell.colSpan = 5

                const buttonDiv = document.createElement("div");
                buttonDiv.className = "sup-plugin-button-container";

                buttonRow.appendChild(buttonCell);
                buttonCell.appendChild(buttonDiv);

                configuration.buttons.forEach(buttonInfo => {
                    const button = document.createElement("button");
                    const buttonIcon = document.createElement("i");
                    const buttonLabel = document.createTextNode(buttonInfo.label);
                    buttonIcon.className = buttonInfo.icon;

                    button.className = "btn btn-sm btn-secondary";
                    button.appendChild(buttonIcon);
                    button.appendChild(buttonLabel);

                    button.addEventListener("click", () => {
                        this.pluginService.performRefresh(buttonInfo.type, identityId).then(() => {
                            console.info(`Successfully performed refresh for type ${buttonInfo.type} and identity ${identityId}`);
                        }).catch(error => {
                            console.error("Error performing refresh", error);
                        });
                    })

                    buttonDiv.appendChild(button);
                })

                // Append to the insertion point's parent after the insertion point
                if (insertionPoint.parentNode) {
                    insertionPoint.parentNode.insertBefore(buttonRow, insertionPoint.nextElementSibling);
                } else {
                    console.warn("Unable to find parent node for button insertion point, buttons will not be rendered");
                }
            }
         }
    }
}

const idwAboutPluginSnippet = new IIDSimpleUIPluginSnippet()
idwAboutPluginSnippet.render().then(() => {
    console.info("Snippet rendered successfully")
}).catch(error => {
    console.error("Error rendering snippet", error);
})