import {IPluginHelper} from "./IIQ.shim";

declare var PluginHelper: IPluginHelper;

const CSRF_COOKIE_NAME = 'CSRF-TOKEN';

export interface ButtonInfo {
    // TODO
}

export class PluginService {

    private getCookie(name: string): string | null {
        const match = document.cookie.match(new RegExp('(?:^|;)\\s*' + name + '=([^;]*)'));
        return match ? decodeURIComponent(match[1]) : null;
    }

    private formatUrl(path: string): string {
        return PluginHelper.getPluginRestUrl("about-plugin/" + path);
    }


}