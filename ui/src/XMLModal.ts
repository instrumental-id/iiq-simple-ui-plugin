import {PluginService, XMLResponse} from "./PluginService";

export type CloseCallback = () => void;

export class XMLModal {
    private pluginService: PluginService;

    private _open: boolean;

    private openModalElement: HTMLElement | null;

    constructor(private parent: HTMLElement) {
        this.pluginService = new PluginService();
        this._open = false;

        this.openModalElement = null;
    }

    private getElement(parent: HTMLElement, elementClass: string): HTMLElement | null {
        const elements = parent.getElementsByClassName(elementClass)
        if (elements.length > 0) {
            return elements[0] as HTMLElement
        } else {
            return null;
        }
    }

    get open(): boolean {
        return this._open;
    }

    private close(closeCallback: CloseCallback | null = null) {
        if (this.openModalElement) {
            this.parent.removeChild(this.openModalElement);
            this.openModalElement = null;
            this._open = false;

            if (closeCallback) {
                closeCallback();
            }
        }
    }

    async showModal(xmlResponse: XMLResponse, closeCallback: CloseCallback | null = null) {
        // Create modal elements
        // ARIA guidelines: https://www.w3.org/WAI/ARIA/apg/patterns/dialog-modal/

        // The gray background overlay
        this.openModalElement = document.createElement("div");
        this.openModalElement.classList.add("sup-modal-overlay");
        this.openModalElement.role = "presentation";

        // The actual modal content container. Must be ARIA role "dialog" and have aria-modal="true"
        // for assistive technologies to recognize it as a modal dialog. It also must be
        // described by something (which is set later).
        const modalContent = document.createElement("div");
        modalContent.classList.add("sup-modal-content");
        modalContent.role = "dialog";
        modalContent.ariaLive = "polite";
        modalContent.ariaModal = "true";

        // Substitute the template content into the modal content container
        modalContent.innerHTML = await this.pluginService.fetchTemplateContent("xml-modal.html");

        // Gross modification of the template follows

        // Required for assistive tech to announce the dialog properly
        const modalTitleElement = this.getElement(modalContent, "sup-modal-title");
        if (modalTitleElement) {
            modalContent.ariaDescribedByElements = [modalTitleElement];
        }

        // Add the event handler to the copy button
        const copyButton = this.getElement(modalContent, "sup-copy-button");
        if (copyButton) {
            copyButton.addEventListener("click", () => {
                navigator.clipboard.writeText(xmlResponse.strippedXml).then(() => {
                    copyButton.textContent = "Copied!";
                    setTimeout(() => { copyButton.textContent = "Copy to Clipboard"; }, 2000);
                });
            });
        }

        // Add the event handler to the close button
        const closeButton = this.getElement(modalContent, "sup-close-button");
        if (closeButton) {
            closeButton.addEventListener("click", () => {
                this.close(closeCallback);
            });
        }

        // Inject the XML content into the <pre> element in the modal
        const pre = this.getElement(modalContent, "sup-xml-modal-content") as HTMLPreElement;
        if (pre) {
            pre.textContent = xmlResponse.xml;
        }

        // Show it!
        this.openModalElement.appendChild(modalContent);
        this.parent.appendChild(this.openModalElement);

        this._open = true;

        // Clicking outside the modal content should also close the modal
        this.openModalElement.addEventListener("click", (event) => {
            if (event.target === this.openModalElement) {
                this.close(closeCallback);
            }
        });

        // Allow closing the modal with the Escape key
        const closeOnEscape = (event: KeyboardEvent) => {
            if (event.key === "Escape") {
                document.removeEventListener("keydown", closeOnEscape);
                this.close(closeCallback);
                event.preventDefault()
            }
        };
        document.addEventListener("keydown", closeOnEscape);

    }
}