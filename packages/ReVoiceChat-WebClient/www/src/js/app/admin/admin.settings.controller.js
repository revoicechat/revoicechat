import {AdminSettingsEmoteController} from "./admin.settings.emote.controller.js";
import AdminSettingsInvitationController from "./admin.settings.invitation.controller.js";
import AdminSettingsMembersController from "./admin.settings.members.controller.js";
import AdminSettingsModerationController from "./admin.settings.moderation.controller.js";
import AdminSettingsOverviewController from "./admin.settings.overview.controller.js";
import AdminSettingsServersController from "./admin.settings.servers.controller.js";

export default class AdminSettingsController {
    currentTab;
    modal;

    constructor(user) {
        this.overview = new AdminSettingsOverviewController();
        this.servers = new AdminSettingsServersController();
        this.invitation = new AdminSettingsInvitationController();
        this.members = new AdminSettingsMembersController(user);
        this.moderation = new AdminSettingsModerationController();
        this.emote = new AdminSettingsEmoteController();
    }

    async load(){
        this.#eventHandler();
        await this.overview.load();
        await this.servers.load();
        this.invitation.load();
        this.members.load();
        this.moderation.load();
        this.select('overview');
    }

    select(name) {
        if (this.currentTab) {
            document.getElementById(`admin-setting-tab-${this.currentTab}`).classList.remove("active");
            document.getElementById(`admin-setting-content-${this.currentTab}`).classList.add("hidden");
        }

        this.currentTab = name;
        document.getElementById(`admin-setting-tab-${this.currentTab}`).classList.add('active');
        document.getElementById(`admin-setting-content-${this.currentTab}`).classList.remove('hidden');
    }

    #eventHandler(){
        document.getElementById('admin-setting-tab-overview').onclick = () => this.select('overview');
        document.getElementById('admin-setting-tab-servers').onclick = () => this.select('servers');
        document.getElementById('admin-setting-tab-emotes').onclick = () => this.select('emotes');
        document.getElementById('admin-setting-tab-members').onclick = () => this.select('members');
        document.getElementById('admin-setting-tab-moderation').onclick = () => this.select('moderation');
        document.getElementById('admin-setting-tab-invitations').onclick = () => this.select('invitations');
    }
}