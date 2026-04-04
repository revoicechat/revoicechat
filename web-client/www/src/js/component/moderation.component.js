import MediaServer from "../app/media/media.server.js";
import CoreServer from "../app/core/core.server.js";
import Modal from "./modal.component.js";
import {i18n} from "../lib/i18n.js";

const SANCTION_TYPES = {
    BAN: {label: "moderation.ban", icon: "moderation-hammer", color: "var(--active-ban-color)"},
    TEXT_TIME_OUT: {label: "moderation.text.timeout", icon: "pencil", color: "var(--text-timeouts-color)"},
    VOICE_TIME_OUT: {label: "moderation.voice.timeout", icon: "speaker-x", color: "var(--voice-timeouts-color)"},
};

const REQUEST_STATUSES = {
    CREATED: {label: "moderation.request.pending", color: "var(--text-timeouts-color)"},
    ACCEPTED: {label: "moderation.request.accepted", color: "var(--total-moderation-color)"},
    REJECTED: {label: "moderation.request.rejected", color: "var(--active-ban-color)"},
};

function fmtDate(dt) {
    return dt
            ? new Date(dt).toLocaleString("en-GB", {
                day: "2-digit", month: "short", year: "numeric",
                hour: "2-digit", minute: "2-digit",
            })
            : i18n.translateOne("moderation.ban.permanent");
}

function parseHtml(html) {
    const parser = new DOMParser();
    const doc = parser.parseFromString(html, "text/html");
    return doc.body.firstElementChild;
}


class ModerationPanel extends HTMLElement {
    /** @type {string|null} */
    #serverId
    /** @type SanctionRepresentation[] */
    #sanctions
    /** @type SanctionRevocationRequestRepresentation[] */
    #requests
    /** @type {Object} */
    #userCache
    /** @type {string} */
    #tab
    /** @type {string} */
    #filter
    #search
    #loading
    #expandedIds

    static get observedAttributes() {
        return ["server-id"];
    }

    constructor() {
        super();
        this.shadow = this.attachShadow({mode: "open"});
        this.#serverId = null;
        this.#sanctions = [];
        this.#requests = [];
        this.#userCache = {};
        this.#tab = "sanctions";
        this.#filter = "ALL";
        this.#search = "";
        this.#loading = false;
        this.#expandedIds = new Set();
    }

    connectedCallback() {
        this.#render();
        if (this.#serverId) {
            void this.#load();
        }
    }

    attributeChangedCallback(name, old, value) {
        if (name === "server-id") {
            this.#serverId = value || null;
            this.#sanctions = [];
            this.#requests = [];
            this.#expandedIds = new Set();
            this.#render();
            if (this.#serverId) {
                void this.#load();
            }
        }
    }

    async #load() {
        this.#loading = true;
        this.#render();
        try {
            const [sanctions, requests] = await Promise.all([
                await CoreServer.fetch(`${this.#apiRoute}`),
                await CoreServer.fetch(`${this.#apiRoute}/revocation-requests`),
            ]);
            this.#sanctions = sanctions;
            this.#requests = requests;
            await this.#resolveUsers();
        } catch (e) {
            console.error("[moderation-panel] load error", e);
        } finally {
            this.#loading = false;
            this.#render();
        }
        i18n.translatePage(this.shadow)
    }

    async #resolveUsers() {
        const ids = new Set();
        for (const s of this.#sanctions) {
            ids.add(s.targetedUser.id);
            ids.add(s.issuedBy.id);
            if (s.revokedBy) ids.add(s.revokedBy.id);
        }
        const missing = [...ids].filter(id => id && !this.#userCache[id]);
        await Promise.allSettled(missing.map(async id => {
            try {
                this.#userCache[id] = await CoreServer.fetch(`/user/${id}`)
            } catch {
                this.#userCache[id] = {name: id.slice(0, 8) + "…"};
            }
        }));
    }

    #userName(user) {
        return this.#userCache[user.id]?.displayName ?? (user.displayName ? user.displayName.slice(0, 8) + "…" : "Unknown");
    }

    get #filtered() {
        return this.#sanctions.filter(s => {
            const matchType =
                    this.#getMatchType(s);
            if (!matchType) return false;
            if (!this.#search) return true;
            const q = this.#search.toLowerCase();
            return this.#userName(s.targetedUser).toLowerCase().includes(q)
                    || (s.reason ?? "").toLowerCase().includes(q);
        });
    }

    #getMatchType(s) {
        switch (this.#filter) {
            case "ALL":
                return true;
            case "ACTIVE":
                return s.active;
            case "INACTIVE":
                return !s.active;
            default:
                return s.type === this.#filter;
        }
    }

    get #pendingCount() {
        return this.#requests.filter(r => r.status === "CREATED").length;
    }

    // ── Actions ────────────────────────────────────────────────────────────────

    async #issueSanction(payload) {
        try {
            const s = await CoreServer.fetch(`${this.#apiRoute}`, 'POST', payload)
            this.#sanctions.push(s);
        } catch (e) {
            console.error("[moderation-panel] issue error", e);
        }
    }

    async #revokeSanction(sanctionId) {
        try {
            const s = await CoreServer.fetch(`${this.#apiRoute}/${sanctionId}`, 'DELETE')
            this.#sanctions = this.#sanctions.map(x => x.id === s.id ? s : x);
        } catch (e) {
            console.error("[moderation-panel] revoke error", e);
        }
    }

    async #reviewRequest(requestId, status) {
        try {
            const r = await CoreServer.fetch(`/api/sanctions/revocation-requests/${requestId}/review`, 'POST', status)
            this.#requests = this.#requests.map(x => x.id === r.id ? r : x);
            if (status === "ACCEPTED") {
                const req = this.#requests.find(x => x.id === requestId);
                if (req) await this.#revokeSanction(req.sanctionId);
            }
        } catch (e) {
            console.error("[moderation-panel] review error", e);
        }
    }

    #buildAvatar(user, size) {
        return parseHtml(`
                         <img class="mp-avatar"
                              style="width:${size}px; height:${size}px; font-size: ${Math.round(size * 0.33)}px"
                              alt="PFP"
                              src="${MediaServer.profiles(user.id)}"/>`)
    }

    #buildBadge(icon, label, color) {
        return parseHtml(`<div class="mp-badge" style="background:${color}">
                              <revoice-icon-${icon}></revoice-icon-${icon}><span data-i18n="${label}">${label}</span>
                          </div>`)
    }

    #buildStats() {
        const active = this.#sanctions.filter(s => s.active);
        const activeBans = active.filter(s => s.type === "BAN").length
        const textTimeouts = active.filter(s => s.type === "TEXT_TIME_OUT").length
        const voiceTimeouts = active.filter(s => s.type === "VOICE_TIME_OUT").length
        const total = this.#sanctions.length
        return parseHtml(`
                <div class="mp-stats">
                    <div class="mp-stat mp-active-ban">
                        <div class="mp-stat-value">${activeBans}</div><div class="mp-stat-label" data-i18n="moderation.active.ban">Active Bans</div>
                    </div>
                    <div class="mp-stat mp-text-timeouts">
                        <div class="mp-stat-value">${textTimeouts}</div><div class="mp-stat-label" data-i18n="moderation.text.timeout">Text Timeouts</div>
                    </div>
                    <div class="mp-stat mp-voice-timeouts">
                        <div class="mp-stat-value">${voiceTimeouts}</div><div class="mp-stat-label" data-i18n="moderation.voice.timeout">Voice Timeouts</div>
                    </div>
                    <div class="mp-stat mp-total-moderation">
                        <div class="mp-stat-value">${total}</div><div class="mp-stat-label" data-i18n="moderation.ban.total">Total</div>
                    </div>
                </div>`)
    }

    #buildTabs() {
        const container = document.createElement("div");
        container.className = "mp-tabs";

        const pending = this.#pendingCount;
        const tabDefs = [
            {key: "sanctions", label: "moderation.tab.sanctions"},
            {key: "requests", label: "moderation.tab.revocation.requests" + (pending ? ` (${pending})` : "")},
        ];

        for (const def of tabDefs) {
            const btn = document.createElement("button");
            btn.className = "mp-tab" + (this.#tab === def.key ? " active" : "");
            btn.textContent = def.label;
            btn.dataset.i18n = def.label;
            btn.addEventListener("click", () => {
                this.#tab = def.key;
                this.#render();
            });
            container.appendChild(btn);
        }
        return container;
    }

    #buildToolbar() {
        const container = document.createElement("div");
        container.className = "mp-toolbar";

        const search = document.createElement("input");
        search.className = "mp-search";
        search.type = "text";
        search.placeholder = "Search user or reason…";
        search.value = this.#search;
        search.addEventListener("input", e => {
            this.#search = e.target.value;
            this.#rerenderList();
        });
        container.appendChild(search);

        const filterDefs = [
            {key: "ALL", icon: '', label: "moderation.filter.all"},
            {key: "ACTIVE", icon: '', label: "moderation.filter.active"},
            {key: "BAN", icon: 'moderation-hammer', label: "moderation.filter.bans"},
            {key: "TEXT_TIME_OUT", icon: 'pencil', label: "moderation.filter.text"},
            {key: "VOICE_TIME_OUT", icon: 'speaker-x', label: "moderation.filter.voice"},
            {key: "INACTIVE", icon: '', label: "moderation.filter.inactive"},
        ];

        const filtersWrap = document.createElement("div");
        filtersWrap.className = "mp-filters";

        for (const def of filterDefs) {
            const btn = document.createElement("button");
            btn.className = "mp-filter" + (this.#filter === def.key ? " active" : "");
            const icon = def.icon ? `<revoice-icon-${def.icon}></revoice-icon-${def.icon}>` : ''
            btn.innerHTML = `${icon}<span data-i18n="${def.label}">${def.label}</span>`;
            btn.dataset.key = def.key;
            btn.addEventListener("click", () => {
                this.#filter = def.key;
                this.#rerenderList();
            });
            filtersWrap.appendChild(btn);
        }

        container.appendChild(filtersWrap);
        return container;
    }

    #buildSanctionRow(sanction) {
        const tc = SANCTION_TYPES[sanction.type] ?? {label: sanction.type, icon: "?", color: "#555"};
        const request = this.#requests.find(r => r.sanctionId === sanction.id);
        const expanded = this.#expandedIds.has(sanction.id);

        // ── card wrapper
        const card = document.createElement("div");
        card.className = "mp-card";
        card.style.borderLeft = "3px solid " + tc.color;
        card.style.opacity = sanction.active ? "1" : "0.65";

        // ── main row
        const rowMain = document.createElement("div");
        rowMain.className = "mp-row-main";

        rowMain.appendChild(this.#buildAvatar(sanction.targetedUser, 32));

        const meta = document.createElement("div");
        meta.className = "mp-row-meta";

        const nameLine = document.createElement("div");
        nameLine.className = "mp-row-name-line";

        const username = document.createElement("span");
        username.className = "mp-row-username";
        username.textContent = this.#userName(sanction.targetedUser);
        nameLine.appendChild(username);
        nameLine.appendChild(this.#buildBadge(tc.icon, tc.label, tc.color));
        if (sanction.revokedAt) nameLine.appendChild(this.#buildBadge('span', "Revoked", "#22c55e"));
        if (!sanction.active && !sanction.revokedAt) nameLine.appendChild(this.#buildBadge('span', "moderation.satus.expired", "#71717a"));
        if (request?.status === "CREATED") nameLine.appendChild(this.#buildBadge('span', "moderation.satus.appeal", "#fb883c"));
        meta.appendChild(nameLine);

        const sub = document.createElement("div");
        sub.className = "mp-row-sub";
        const byStrong = document.createElement("strong");
        byStrong.textContent = this.#userName(sanction.issuedBy);
        sub.appendChild(document.createTextNode("By "));
        sub.appendChild(byStrong);
        sub.appendChild(document.createTextNode(
                " · " + fmtDate(sanction.startAt) + " → " + fmtDate(sanction.expiresAt)
        ));
        meta.appendChild(sub);
        rowMain.appendChild(meta);

        // ── action buttons
        const actions = document.createElement("div");
        actions.className = "mp-row-actions";

        if (sanction.active) {
            const revokeBtn = document.createElement("button");
            revokeBtn.className = "mp-btn mp-btn-danger mp-btn-sm";
            revokeBtn.title = "Revoke";
            revokeBtn.innerHTML = "<revoice-icon-trash></revoice-icon-trash>";
            revokeBtn.addEventListener("click", () => this.#openRevokeModal(sanction));
            actions.appendChild(revokeBtn);
        }

        const expandBtn = document.createElement("button");
        expandBtn.className = "mp-btn mp-btn-ghost mp-btn-sm";
        expandBtn.textContent = expanded ? "▲" : "▼";
        expandBtn.addEventListener("click", () => {
            if (this.#expandedIds.has(sanction.id)) this.#expandedIds.delete(sanction.id);
            else this.#expandedIds.add(sanction.id);
            this.#rerenderList();
        });
        actions.appendChild(expandBtn);

        rowMain.appendChild(actions);
        card.appendChild(rowMain);

        // ── expanded section
        const expandSection = document.createElement("div");
        expandSection.className = "mp-expand";
        if (expanded) {
            const sectionLabel = document.createElement("div");
            sectionLabel.className = "mp-section-label";
            sectionLabel.dataset.i18n = "moderation.sanction.reason"
            sectionLabel.textContent = "Reason";
            expandSection.appendChild(sectionLabel);

            const reasonP = document.createElement("p");
            reasonP.className = "mp-reason";
            reasonP.textContent = sanction.reason ?? "—";
            expandSection.appendChild(reasonP);

            if (sanction.revokedAt) {
                const revokedP = document.createElement("p");
                revokedP.className = "mp-revoked-info";

                const revokedBy = document.createElement("span");
                revokedBy.dataset.i18n = "moderation.revoked.by"
                revokedBy.dataset.i18nValue = this.#userName(sanction.revokedBy)
                revokedBy.textContent = "Revoked by " + this.#userName(sanction.revokedBy)

                const revokedOn = document.createElement("span");
                revokedOn.dataset.i18n = "moderation.revoked.on"
                revokedOn.dataset.i18nValue = fmtDate(sanction.revokedAt)
                revokedOn.textContent = " on " + fmtDate(sanction.revokedAt)

                revokedP.appendChild(revokedBy);
                revokedP.appendChild(revokedOn);
                expandSection.appendChild(revokedP);
            }

            if (request) {
                expandSection.appendChild(this.#buildAppealBlock(request));
            }
        } else {
            expandSection.hidden = true;
        }

        card.appendChild(expandSection);
        return card;
    }

    #buildAppealBlock(request) {
        const sc = REQUEST_STATUSES[request.status] ?? {label: request.status, color: "#555"};

        const block = document.createElement("div");
        block.className = "mp-appeal";

        const header = document.createElement("div");
        header.className = "mp-appeal-header";

        const lbl = document.createElement("span");
        lbl.className = "mp-appeal-label";
        lbl.dataset.i18n = "moderation.revocation.appeal"
        lbl.textContent = "Revocation Appeal";
        header.appendChild(lbl);
        header.appendChild(this.#buildBadge('span', sc.label, sc.color));
        block.appendChild(header);

        const msg = document.createElement("p");
        msg.className = "mp-appeal-msg";
        msg.textContent = '"' + request.message + '"';
        block.appendChild(msg);

        const date = document.createElement("div");
        date.className = "mp-appeal-date";
        date.dataset.i18n = "moderation.revocation.request.at"
        date.dataset.i18nValue = fmtDate(request.requestAt)
        date.textContent = "Requested " + fmtDate(request.requestAt);
        block.appendChild(date);

        if (request.status === "CREATED") {
            const actionsRow = document.createElement("div");
            actionsRow.className = "mp-appeal-actions";

            const approveBtn = document.createElement("button");
            approveBtn.className = "mp-btn mp-btn-success mp-btn-sm";
            approveBtn.dataset.i18n = "moderation.revocation.approuve"
            approveBtn.textContent = "✓ Approve";
            approveBtn.addEventListener("click", async () => {
                approveBtn.disabled = true;
                await this.#reviewRequest(request.id, "ACCEPTED");
                this.#render();
            });

            const rejectBtn = document.createElement("button");
            rejectBtn.className = "mp-btn mp-btn-danger mp-btn-sm";
            rejectBtn.dataset.i18n = "moderation.revocation.reject"
            rejectBtn.textContent = "✕ Reject";
            rejectBtn.addEventListener("click", async () => {
                rejectBtn.disabled = true;
                await this.#reviewRequest(request.id, "REJECTED");
                this.#render();
            });

            actionsRow.appendChild(approveBtn);
            actionsRow.appendChild(rejectBtn);
            block.appendChild(actionsRow);
        }

        return block;
    }

    #buildRequestCard(request) {
        const sanction = this.#sanctions.find(s => s.id === request.sanctionId);
        const tc = sanction ? SANCTION_TYPES[sanction.type] : null;
        const sc = REQUEST_STATUSES[request.status] ?? {label: request.status, color: "#555"};

        const card = document.createElement("div");
        card.className = "mp-card";
        card.style.padding = "0.75rem";

        const rowMain = document.createElement("div");
        rowMain.className = "mp-row-main";
        rowMain.style.alignItems = "flex-start";
        rowMain.style.padding = "0";

        if (sanction) rowMain.appendChild(this.#buildAvatar(sanction.targetedUser, 32));

        const meta = document.createElement("div");
        meta.className = "mp-row-meta";

        const nameLine = document.createElement("div");
        nameLine.className = "mp-row-name-line";

        const username = document.createElement("span");
        username.className = "mp-row-username";
        username.textContent = sanction ? this.#userName(sanction.targetedUser) : "Unknown";
        nameLine.appendChild(username);
        if (tc) nameLine.appendChild(this.#buildBadge(tc.icon, tc.label, tc.color));
        nameLine.appendChild(this.#buildBadge('span', sc.label, sc.color));
        meta.appendChild(nameLine);

        const sub = document.createElement("div");
        sub.className = "mp-row-sub";
        sub.textContent = "Requested " + fmtDate(request.requestAt);
        meta.appendChild(sub);

        const msgP = document.createElement("p");
        msgP.className = "mp-appeal-msg";
        msgP.style.marginTop = "0.5rem";
        msgP.style.marginBottom = "0";
        msgP.textContent = '"' + request.message + '"';
        meta.appendChild(msgP);

        if (sanction) {
            const originalReason = document.createElement("div");
            originalReason.className = "mp-row-sub";
            originalReason.style.marginTop = "0.35rem";
            originalReason.textContent = "Original reason: ";
            const em = document.createElement("em");
            em.textContent = sanction.reason ?? "—";
            originalReason.appendChild(em);
            meta.appendChild(originalReason);
        }

        if (request.status === "CREATED") {
            const actionsRow = document.createElement("div");
            actionsRow.className = "mp-appeal-actions";

            const approveBtn = document.createElement("button");
            approveBtn.className = "mp-btn mp-btn-success mp-btn-sm";
            approveBtn.textContent = "✓ Approve & Revoke";
            approveBtn.addEventListener("click", async () => {
                approveBtn.disabled = true;
                await this.#reviewRequest(request.id, "ACCEPTED");
                this.#render();
            });

            const rejectBtn = document.createElement("button");
            rejectBtn.className = "mp-btn mp-btn-danger mp-btn-sm";
            rejectBtn.textContent = "✕ Reject";
            rejectBtn.addEventListener("click", async () => {
                rejectBtn.disabled = true;
                await this.#reviewRequest(request.id, "REJECTED");
                this.#render();
            });

            actionsRow.appendChild(approveBtn);
            actionsRow.appendChild(rejectBtn);
            meta.appendChild(actionsRow);
        }

        rowMain.appendChild(meta);
        card.appendChild(rowMain);
        return card;
    }

    #buildListContainer() {
        const wrap = document.createElement("div");
        wrap.className = "mp-list-container";

        if (this.#tab === "sanctions") {
            const items = this.#filtered;
            if (items.length === 0) {
                const empty = document.createElement("div");
                empty.className = "mp-empty";

                const title = document.createElement("div");
                title.className = "mp-empty-title";
                title.dataset.i18n = "moderation.no.sanctions.found"
                title.textContent = "No sanctions found";

                const sub = document.createElement("div");
                sub.className = "mp-empty-sub";
                sub.dataset.i18n = "moderation.filters.adjusting"
                sub.textContent = "Try adjusting your filters";

                empty.appendChild(title);
                empty.appendChild(sub);
                wrap.appendChild(empty);
            } else {
                const list = document.createElement("div");
                list.className = "mp-list";
                for (const sanction of items) {
                    list.appendChild(this.#buildSanctionRow(sanction));
                }
                wrap.appendChild(list);
            }
        } else if (this.#requests.length === 0) {
            const empty = document.createElement("div");
            empty.className = "mp-empty";

            const title = document.createElement("div");
            title.className = "mp-empty-title";
            title.dataset.i18n = "moderation.no.revocation.requests"
            title.textContent = "No revocation requests";

            empty.appendChild(title);
            wrap.appendChild(empty);
        } else {
            const list = document.createElement("div");
            list.className = "mp-list";
            for (const req of this.#requests) {
                list.appendChild(this.#buildRequestCard(req));
            }
            wrap.appendChild(list);
        }
        return wrap;
    }

    // ── Modal ──────────────────────────────────────────────────────────────────

    #openNewModal() {
        const formData = {targetedUser: "", type: "BAN", expiresAt: "", reason: ""}
        Modal.toggle({
            title: 'Issue New Sanction',
            showCancelButton: true,
            html: this.#buildSanctionForm(formData)
        }).then(async (result) => {
            if (result.isConfirmed) {
                const payload = {...formData, expiresAt: formData.expiresAt || null};
                console.log(payload)
                await this.#issueSanction(payload);
                void this.#load();
            }
        })
    }

    #openRevokeModal(sanction) {
        Modal.toggle({
            title: 'Revoke Sanction',
            showCancelButton: true,
            text: "Are you sure you want to revoke the sanction ?"
        }).then(async (result) => {
            if (result.isConfirmed) {
                await this.#revokeSanction(sanction.id);
                void this.#load();
            }
        })
    }

    #buildSanctionForm(formData) {
        const body = document.createElement("div");
        body.className = "mp-modal-body";

        // ── Target user field
        const targetField = document.createElement("div");
        targetField.className = "mp-field";

        const targetLabel = document.createElement("label");
        targetLabel.className = "mp-label";
        targetLabel.textContent = "Target User";
        targetField.appendChild(targetLabel);

        const targetUser = document.createElement("select");
        targetUser.className = "mp-input";
        targetUser.placeholder = "User UUID";
        targetUser.value = formData.targetedUser;
        targetUser.addEventListener("change", e => {
            formData.targetedUser = e.target.value.trim();
        });
        targetField.appendChild(targetUser);
        this.fetchApiUser().then(users => {
            users.forEach(user => {
                const userOption = document.createElement("option");
                userOption.value = user.id;
                userOption.textContent = user.displayName;
                targetUser.appendChild(userOption);
            })
        })

        body.appendChild(targetField);

        // ── Sanction type field
        const typeField = document.createElement("div");
        typeField.className = "mp-field";

        const typeLabel = document.createElement("label");
        typeLabel.className = "mp-label";
        typeLabel.textContent = "Sanction Type";
        typeField.appendChild(typeLabel);

        const typeBtns = document.createElement("div");
        typeBtns.className = "mp-type-btns";

        const typeKeys = Object.keys(SANCTION_TYPES);
        const typeButtonEls = [];

        const applyTypeStyles = () => {
            for (let i = 0; i < typeKeys.length; i++) {
                const key = typeKeys[i];
                const btn = typeButtonEls[i];
                const tc = SANCTION_TYPES[key];
                const active = formData.type === key;
                btn.style.background = active ? tc.color : "var(--ter-bg-color, #202024)";
                btn.style.color = active ? "white" : "var(--pri-placeholder-color, #9ca3af)";
                btn.style.border = "1px solid " + (active ? tc.color : "var(--pri-bd-color, #43434d)");
            }
        };

        for (const key of typeKeys) {
            const tc = SANCTION_TYPES[key];
            const btn = document.createElement("button");
            btn.className = "mp-type-btn";
            btn.type = "button";
            btn.innerHTML = `
                <revoice-icon-${tc.icon}></revoice-icon-${tc.icon}>
                <span data-i18n="${tc.label}" style="text-wrap: nowrap">${tc.label}</span>
            `;
            btn.addEventListener("click", () => {
                formData.type = key;
                applyTypeStyles();
            });
            typeButtonEls.push(btn);
            typeBtns.appendChild(btn);
        }

        applyTypeStyles();
        typeField.appendChild(typeBtns);
        body.appendChild(typeField);

        // ── Expires At field
        const expiresField = document.createElement("div");
        expiresField.className = "mp-field";

        const expiresLabel = document.createElement("label");
        expiresLabel.className = "mp-label";
        expiresLabel.appendChild(document.createTextNode("Expires At "));
        const hint = document.createElement("span");
        hint.className = "mp-label-hint";
        hint.textContent = "(leave blank for permanent)";
        expiresLabel.appendChild(hint);
        expiresField.appendChild(expiresLabel);

        const expiresInput = document.createElement("input");
        expiresInput.className = "mp-input";
        expiresInput.type = "datetime-local";
        expiresInput.value = formData.expiresAt;
        expiresInput.style.colorScheme = "dark";
        expiresInput.addEventListener("input", e => {
            formData.expiresAt = e.target.value;
        });
        expiresField.appendChild(expiresInput);
        body.appendChild(expiresField);

        // ── Reason field
        const reasonField = document.createElement("div");
        reasonField.className = "mp-field";

        const reasonLabel = document.createElement("label");
        reasonLabel.className = "mp-label";
        reasonLabel.textContent = "Reason";
        reasonField.appendChild(reasonLabel);

        const reasonInput = document.createElement("textarea");
        reasonInput.className = "mp-textarea";
        reasonInput.rows = 3;
        reasonInput.placeholder = "Describe why this sanction is being issued…";
        reasonInput.value = formData.reason;
        reasonInput.addEventListener("input", e => {
            formData.reason = e.target.value;
        });
        reasonField.appendChild(reasonInput);
        body.appendChild(reasonField);
        i18n.translatePage(body)
        return body;
    }

    #rerenderList() {
        const existing = this.shadow.querySelector(".mp-list-container");
        if (!existing) {
            this.#render();
            return;
        }
        existing.replaceWith(this.#buildListContainer());

        // re-sync filter buttons
        this.shadow.querySelectorAll(".mp-filter").forEach(btn => {
            btn.classList.toggle("active", btn.dataset.key === this.#filter);
        });
        i18n.translatePage(this.shadow)
    }

    #render() {
        this.shadow.innerHTML = "";

        const link = document.createElement("link");
        link.href = "src/js/component/moderation.component.css";
        link.rel = "stylesheet";
        this.shadow.appendChild(link);

        const root = document.createElement("div");
        root.className = "mp-root";

        if (this.#loading) {
            this.shadow.innerHTML = `
                <link href="src/js/component/moderation.component.css" rel="stylesheet">
                <div class="mp-root">
                    <div class="mp-loading">
                        <div class="mp-spinner"></div>
                        <span data-i18n="moderation.loading">Loading…</span>
                    </div>
                </div>`
        } else {
            this.shadow.innerHTML = `
                <link href="src/js/component/moderation.component.css" rel="stylesheet">
                <div class="mp-root">
                    <div class="mp-inner" id="mp-inner">
                        <div class="mp-header">
                            <div>
                                <h1 data-i18n="moderation.title">Moderation</h1>
                                <div class="mp-subtitle" data-i18n="moderation.subtitle">Server-wide sanction management</div>
                            </div>
                            <button id="issue-new-sanction" class="mp-btn mp-btn-primary">
                                <revoice-icon-circle-plus></revoice-icon-circle-plus><span data-i18n="moderation.issue.new">Issue Sanction</span>
                            </button>
                        </div>
                    </div>
                </div>`
            const mpInner = this.shadow.getElementById('mp-inner');
            mpInner.appendChild(this.#buildStats())
            mpInner.appendChild(this.#buildTabs())
            if (this.#tab === "sanctions") {
                mpInner.appendChild(this.#buildToolbar())
            }
            mpInner.appendChild(this.#buildListContainer())
            this.shadow.getElementById('issue-new-sanction').addEventListener('click', () => this.#openNewModal())
        }
        i18n.translatePage(this.shadow)
    }

    get #apiRoute() {
        return this.#serverId === "APP"
                ? '/sanctions'
                : `/servers/${this.#serverId}/sanctions`;
    }

    /** @return {Promise<UserRepresentation[]>} */
    async fetchApiUser() {
        return this.#serverId === "APP"
                ? CoreServer.fetch(`/user`)
                : CoreServer.fetch(`/server/${this.#serverId}/user`);
    }
}

customElements.define("revoice-moderation-panel", ModerationPanel);