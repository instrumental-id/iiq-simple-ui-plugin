import {PluginService} from "./PluginService";
import Toastify from "toastify-js";
import {XMLModal} from "./XMLModal";

// This will be available at runtime
declare var jQuery: any;

export class IIDSimpleUIPluginSnippet {
    private readonly pluginService: PluginService;
    private readonly xmlModalService: XMLModal;

    constructor() {
        this.pluginService = new PluginService();
        this.xmlModalService = new XMLModal(document.body)
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

        if (configuration.xmlVisible) {
            // Listen for backtick keypress
            document.addEventListener("keydown", (event) => {
                if (event.key === "`") {
                    event.preventDefault();
                    this.pluginService.getIdentityXML(identityId).then(xmlResponse => {
                        this.xmlModalService.showModal(xmlResponse);
                    }).catch(error => {
                        console.error("Error fetching identity XML", error);
                        Toastify({
                            text: `An error occurred while fetching identity XML: ${error.message}`,
                            duration: 5000,
                            newWindow: true,
                            close: false,
                            gravity: "top", // `top` or `bottom`
                            position: "left", // `left`, `center` or `right`
                            stopOnFocus: true,
                            style: {
                                "background": "#4a0606",
                                "color": "#ddd"
                            }
                        }).showToast();
                    });
                }
            });
        }

        if (configuration.buttons && configuration.buttons.length > 0) {
            console.log("Retrieved button configuration", configuration.buttons);
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
                            Toastify({
                                text: `Successfully refreshed user! (type: ${buttonInfo.label})`,
                                duration: 5000,
                                newWindow: true,
                                close: false,
                                gravity: "top", // `top` or `bottom`
                                position: "left", // `left`, `center` or `right`
                                stopOnFocus: true,
                                style: {
                                    "background": "#073018",
                                    "border": "1px solid #555",
                                }
                            }).showToast();
                        }).catch(error => {
                            console.error("Error performing refresh", error);
                            Toastify({
                                text: `An error occurred while refreshing: ${error.message})`,
                                duration: 5000,
                                newWindow: true,
                                close: false,
                                gravity: "top", // `top` or `bottom`
                                position: "left", // `left`, `center` or `right`
                                stopOnFocus: true,
                                style: {
                                    "background": "#4a0606",
                                }
                            }).showToast();
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