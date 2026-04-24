import {IPluginHelper} from "./IIQ.shim";

declare var PluginHelper: IPluginHelper;

const CSRF_COOKIE_NAME = 'CSRF-TOKEN';

export interface Configuration {
    buttons: ButtonInfo[]
}

export interface ButtonInfo {
    label: string;
    type: string;
    icon: string;
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

    private async parseErrorResponse(response: Response): Promise<string | ErrorObject> {
        const text = await response.text(); // always safe; returns "" if body is empty

        if (!text) {
            return `HTTP ${response.status}: ${response.statusText}`;
        }

        try {
            return JSON.parse(text); // your rich error object, if it is one
        } catch {
            return text; // plain text fallback
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
            let errorObject = await this.parseErrorResponse(response);
            console.error("Error response from web service", response.status, response.statusText, errorObject);
            if (typeof errorObject === "object" && "message" in errorObject) {
                // If it's an ErrorObject, we can throw a more specific error
                throw new Error(`Failed to fetch configuration: ${response.status} ${errorObject.message}`);
            } else {
                throw new Error(`Failed to fetch configuration: ${response.status} ${errorObject}`);
            }
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
            let errorObject = await this.parseErrorResponse(response);
            console.error("Error response from web service", response.status, response.statusText, errorObject);
            if (typeof errorObject === "object" && "message" in errorObject) {
                // If it's an ErrorObject, we can throw a more specific error
                throw new Error(`Failed to perform refresh: ${response.status} ${errorObject.message}`);
            } else {
                throw new Error(`Failed to perform refresh: ${response.status} ${errorObject}`);
            }
        }
    }
}