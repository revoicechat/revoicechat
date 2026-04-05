/**
 * @param {string|null} serverId
 * @param {SanctionRepresentation[]} sanctions
 */
export function getVocalSanction(serverId, sanctions) {
    return getSanction(serverId, sanctions, "VOICE_TIME_OUT")
}

/**
 * @param {string|null} serverId
 * @param {SanctionRepresentation[]} sanctions
 */
export function getTextSanction(serverId, sanctions) {
    return getSanction(serverId, sanctions, "TEXT_TIME_OUT")
}

/**
 * @param {string|null} serverId
 * @param {SanctionRepresentation[]} sanctions
 * @param {"TEXT_TIME_OUT"|"VOICE_TIME_OUT"} type
 */
function getSanction(serverId, sanctions, type) {
    if (sanctions && sanctions.length > 0) {
        for (const sanction of sanctions) {
            const isVoiceTimeout = sanction.type === type || sanction.type === "BAN"
            const forServer = (sanction.server === serverId || sanction.server === null)
            if (isVoiceTimeout && forServer && sanction.active) {
                return sanction
            }
        }
    }
    return null;
}