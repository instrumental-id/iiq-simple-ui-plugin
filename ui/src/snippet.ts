import {PluginService} from "./PluginService";

export class IIDSimpleUIPluginSnippet {
    private pluginService: PluginService;

    constructor() {
        this.pluginService = new PluginService();
    }

    async render() {
        // TODO: Use pluginService to fetch data and render the UI accordingly
    }

}

const idwAboutPluginSnippet = new IIDSimpleUIPluginSnippet()
idwAboutPluginSnippet.render().then(() => {
    console.info("Snippet rendered successfully")
}).catch(error => {
    console.error("Error rendering snippet", error);
})