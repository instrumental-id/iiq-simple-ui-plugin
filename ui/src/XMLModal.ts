import {XMLResponse} from "./PluginService";

export class XMLModal {
    constructor(private parent: HTMLElement) {

    }

    showModal(xmlResponse: XMLResponse) {
        // Create modal elements
        const modalOverlay = document.createElement("div");
        modalOverlay.style.position = "fixed";
        modalOverlay.style.top = "0";
        modalOverlay.style.left = "0";
        modalOverlay.style.width = "100%";
        modalOverlay.style.height = "100%";
        modalOverlay.style.backgroundColor = "rgba(0, 0, 0, 0.5)";
        modalOverlay.style.display = "flex";
        modalOverlay.style.justifyContent = "center";
        modalOverlay.style.alignItems = "center";
        modalOverlay.style.zIndex = "10000";

        const modalContent = document.createElement("div");
        modalContent.style.backgroundColor = "#fff";
        modalContent.style.borderRadius = "8px";
        modalContent.style.maxWidth = "80%";
        modalContent.style.maxHeight = "80%";
        modalContent.style.display = "flex";
        modalContent.style.flexDirection = "column";
        modalContent.style.overflow = "hidden";

        // Header
        const modalHeader = document.createElement("div");
        modalHeader.style.display = "flex";
        modalHeader.style.alignItems = "center";
        modalHeader.style.justifyContent = "space-between";
        modalHeader.style.padding = "12px 20px";
        modalHeader.style.borderBottom = "1px solid #ddd";
        modalHeader.style.flexShrink = "0";

        const modalTitle = document.createElement("span");
        modalTitle.textContent = "XML Response";
        modalTitle.style.fontWeight = "bold";
        modalTitle.style.fontSize = "16px";

        const headerButtons = document.createElement("div");
        headerButtons.style.display = "flex";
        headerButtons.style.gap = "8px";

        const copyButton = document.createElement("button");
        copyButton.textContent = "Copy to Clipboard";
        copyButton.style.cursor = "pointer";
        copyButton.addEventListener("click", () => {
            navigator.clipboard.writeText(xmlResponse.strippedXml).then(() => {
                copyButton.textContent = "Copied!";
                setTimeout(() => { copyButton.textContent = "Copy to Clipboard"; }, 2000);
            });
        });

        const closeButton = document.createElement("button");
        closeButton.textContent = "Close";
        closeButton.style.cursor = "pointer";
        closeButton.addEventListener("click", () => {
            this.parent.removeChild(modalOverlay);
        });

        headerButtons.appendChild(copyButton);
        headerButtons.appendChild(closeButton);
        modalHeader.appendChild(modalTitle);
        modalHeader.appendChild(headerButtons);

        // Body
        const modalBody = document.createElement("div");
        modalBody.style.padding = "20px";
        modalBody.style.overflowY = "auto";

        const pre = document.createElement("pre");
        pre.textContent = xmlResponse.xml;
        pre.style.whiteSpace = "pre";
        pre.style.overflowX = "scroll";
        pre.style.margin = "0";

        // Clicking outside the modal content should also close the modal
        modalOverlay.addEventListener("click", (event) => {
            if (event.target === modalOverlay) {
                this.parent.removeChild(modalOverlay);
            }
        });

        modalBody.appendChild(pre);
        modalContent.appendChild(modalHeader);
        modalContent.appendChild(modalBody);
        modalOverlay.appendChild(modalContent);
        this.parent.appendChild(modalOverlay);
    }
}