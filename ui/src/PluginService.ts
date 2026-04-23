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

export class PluginService {

    private getCookie(name: string): string | null {
        const match = document.cookie.match(new RegExp('(?:^|;)\\s*' + name + '=([^;]*)'));
        return match ? decodeURIComponent(match[1]) : null;
    }

    private formatUrl(path: string): string {
        return PluginHelper.getPluginRestUrl("simple-ui-plugin/" + path);
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
            console.error("Error response from web service", response.status, response.statusText);
            throw new Error(`Failed to fetch configuration: ${response.status} ${response.statusText}`);
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
            console.error("Error response from web service", response.status, response.statusText);
            throw new Error(`Failed to perform refresh: ${response.status} ${response.statusText}`);
        }
    }
}