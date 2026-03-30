export default class AdminSettingsModerationController {

    load() {
        document.getElementById('admin-setting-content-moderation')
                .innerHTML = `<revoice-moderation-panel id="admin-moderation-panel" server-id="APP">
                              </revoice-moderation-panel>`
    }
}