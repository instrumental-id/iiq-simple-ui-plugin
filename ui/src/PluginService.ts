import {IPluginHelper} from "./IIQ.shim";

declare var PluginHelper: IPluginHelper;

const CSRF_COOKIE_NAME = 'CSRF-TOKEN';

export interface Configuration {
    buttons: ButtonInfo[]
    xmlVisible: boolean;
}

export interface ButtonInfo {
    label: string;
    type: string;
    icon: string;
}

export interface XMLResponse {
    strippedXml: string;
    xml: string;
    identityId: string;
}

/**
 * Corresponds to ErrorResponse on the server side
 */
export interface ErrorObject {
    message: string;
    exceptionClass: string;
    stackTrace?: string;
}

export class PluginService {

    private getCookie(name: string): string | null {
        const match = document.cookie.match(new RegExp('(?:^|;)\\s*' + name + '=([^;]*)'));
        return match ? decodeURIComponent(match[1]) : null;
    }

    private formatUrl(path: string): string {
        return PluginHelper.getPluginRestUrl("simple-ui-plugin/" + path);
    }

    private async parseErrorResponse(response: Response): Promise<never> {
        let text = await response.text(); // always safe; returns "" if body is empty

        if (!text) {
            text = response.statusText;
        }

        let errorObject;

        try {
            errorObject = JSON.parse(text); // your rich error object, if it is one
        } catch {
            errorObject = text; // plain text fallback
        }

        console.error("Error response from web service", response.status, response.statusText, errorObject);
        if (typeof errorObject === "object" && "message" in errorObject) {
            // If it's an ErrorObject, we can throw a more specific error
            throw new Error(`Failed to fetch identity XML: ${response.status} ${errorObject.message}`);
        } else {
            throw new Error(`Failed to fetch identity XML: ${response.status} ${errorObject}`);
        }
    }

    async fetchTemplateContent(filename: string): Promise<string> {
        const sanitizedFilename = encodeURIComponent(filename);
        let url = PluginHelper.getPluginFileUrl("IIDSimpleUIPlugin", "ui/templates/" + sanitizedFilename);
        const response = await fetch(url, {
            method: "GET",
            headers: {
                "X-XSRF-TOKEN": this.getCookie(CSRF_COOKIE_NAME) ?? ""
            }
        });

        if (response.ok) {
            return await response.text();
        } else {
            return await this.parseErrorResponse(response);
        }
    }

    async getConfiguration(): Promise<Configuration> {
        const url = this.formatUrl("configuration");
        const response = await fetch(url, {
            method: "GET",
            headers: {
                "X-XSRF-TOKEN": this.getCookie(CSRF_COOKIE_NAME) ?? ""
            }
        });

        if (response.ok) {
            return await response.json() as Configuration;
        } else {
            return await this.parseErrorResponse(response);
        }
    }

    async getIdentityXML(identityId: string): Promise<XMLResponse> {
        const sanitizedIdentityId = encodeURIComponent(identityId);
        const url = this.formatUrl("xml/" + sanitizedIdentityId + "?stripIdentifiers=true");
        const response = await fetch(url, {
            method: "GET",
            headers: {
                "X-XSRF-TOKEN": this.getCookie(CSRF_COOKIE_NAME) ?? ""
            }
        });
        if (response.ok) {
            return await response.json() as XMLResponse;
        } else {
            return await this.parseErrorResponse(response);
        }
    }

    async performRefresh(type: string, targetIdentityId: string): Promise<void> {
        const sanitizedType = encodeURIComponent(type);
        const sanitizedTargetIdentityId = encodeURIComponent(targetIdentityId);
        const url = this.formatUrl("refresh/" + sanitizedType + "/" + sanitizedTargetIdentityId);
        const response = await fetch(url, {
            method: "POST",
            headers: {
                "X-XSRF-TOKEN": this.getCookie(CSRF_COOKIE_NAME) ?? ""
            }
        });

        if (!response.ok) {
            return await this.parseErrorResponse(response);
        }
    }
}