import { i18n } from "../lib/i18n.js";
import MediaServer from "../app/media/media.server.js";
import CoreServer from "../app/core/core.server.js";
import Modal from "./modal.component.js";

class ServerRolesWebComponent extends HTMLElement {

    constructor() {
        super();
        this.attachShadow({ mode: 'open' });
        this.serverId = null
    }

    static get observedAttributes() {
        return ['server-id'];
    }

    attributeChangedCallback(name, oldValue, newValue) {
        if (name === 'server-id' && oldValue !== newValue) {
            this.#connectedCallback(newValue);
        }
    }

    connectedCallback() {
        const serverId = this.getAttribute("server-id")
        this.#connectedCallback(serverId);
    }

    #connectedCallback(serverId) {
        if (serverId) {
            this.serverId = serverId
            this.render();
            this.loadData();
        } else {
            this.shadowRoot.innerHTML = ``
        }
    }

    async loadData() {
        try {
            await Promise.all([
                this.fetchRoles(),
                this.fetchUsers(),
                this.fetchRisks()
            ]);
            this.renderRoles();
        } catch (error) {
            console.error('Error loading data:', error);
            this.showError('server.roles.error.load');
        }
    }

    async fetchRoles() {
        this.roles = await CoreServer.fetch(`/server/${this.serverId}/role`);
    }

    async fetchUsers() {
        const result = await CoreServer.fetch(`/server/${this.serverId}/user`);
        this.availableUsers = [...result].sort((a, b) => {
            return a.displayName.localeCompare(b.displayName);
        });
    }

    async fetchRisks() {
        this.availableServerRisks = await CoreServer.fetch('/risk/server');
        this.availableRoomRisks = await CoreServer.fetch('/risk/room');
    }

    async createRoleAPI(roleData) {
        return await CoreServer.fetch(`/server/${this.serverId}/role`, 'PUT', roleData);
    }

    async patchRoleAPI(roleId, roleData) {
        return await CoreServer.fetch(`/role/${roleId}`, 'PATCH', roleData);
    }

    async updateRoleRisk(roleId, riskName, status) {
        await CoreServer.fetch(`/role/${roleId}/risk/${riskName}`, 'PATCH', {
            mode: status.toUpperCase(),
            entity: this.#entity
        });
    }

    render() {
        this.shadowRoot.innerHTML = `
            <link href="src/css/main.css" rel="stylesheet" />
            <link href="src/css/emoji.css" rel="stylesheet" />
            <link href="src/css/themes.css" rel="stylesheet" />
            <link href="src/js/component/server.roles.component.css" rel="stylesheet" />
            <div class="config config-right">            
                <div class="role-settings-main">
                    <div class="role-settings-sidebar">
                        <div class="config-buttons">
                            <button class="button" id="createRoleBtn"><revoice-icon-circle-plus></revoice-icon-circle-plus> <span data-i18n="server.roles.new">New</span></button>
                        </div>
                        <div class="sidebar-room-container" id="rolesList"></div>
                    </div>
                    <div class="risk-room">
                        <div class="room-container">
                            <div class="room-content" id="roleDetails">
                                <div class="empty-state">
                                    <h3 data-i18n="server.roles.select.title">Select a role</h3>
                                    <p  data-i18n="server.roles.select.body" >Choose a role from the list to view and manage its risks</p>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        `;
        this.setupEventListeners();
    }

    setupEventListeners() {
        const createBtn = this.shadowRoot.getElementById('createRoleBtn');
        createBtn.addEventListener('click', () => this.#newRole());
    }

    renderRoles() {
        const rolesList = this.shadowRoot.getElementById('rolesList');
        rolesList.innerHTML = this.roles
            .sort((a, b) => a.priority - b.priority)
            .map(role => `
                <div class="config-item ${this.selectedRoleId === role.id ? 'active' : ''}" data-role-id="${role.id}">
                    <div class="icon" style="background: ${role.color}"></div>
                    <div style="flex: 1;">
                        <div class="name">${role.name}</div>
                        <div class="role-priority"><span data-i18n="server.roles.priority">Priority:</span> ${role.priority}</div>
                    </div>
                </div>
            `).join('');

        // Add click listeners
        for (const item of rolesList.querySelectorAll('.config-item')) {
            item.addEventListener('click', () => {
                const roleId = item.dataset.roleId;
                this.selectRole(roleId);
            });
        }
        i18n.translatePage(rolesList)
    }

    /** @param {string} roleId */
    selectRole(roleId) {
        this.selectedRoleId = roleId;
        this.renderRoles();
        this.renderRoleDetails();
    }

    renderRoleDetails() {
        const role = this.roles.find(r => r.id === this.selectedRoleId);
        if (!role) return;
        const roleDetails = this.shadowRoot.getElementById('roleDetails');
        roleDetails.innerHTML = `
            <div class="detail-header">
                <div class="icon" style="background: ${role.color}"></div>
                <h2>${role.name}</h2>

                <div class="config-buttons">
                    <button class="button" id="role-member-add" data-i18n="server.roles.add.members">Add user</button>
                    <button class="button" id="role-edit" data-i18n="server.roles.edit">Edit</button>
                    <button class="button" id="role-delete" data-i18n="server.roles.delete">Delete</button>
                </div>
            </div>

            <div class="risk-management-section">
                <div class="config-section" id="members-section">
                    <h2 data-i18n="server.roles.members">Members</h2>
                    <br/>
                    <div id="role-member-list" class="config-members-list"></div>
                </div >
                <div class="config-section" id="auth-section">
                    ${this.#renderServerCategory(role)}
                    ${this.#renderRoomCategory(role)}
                </div>
            </div>`;

        // Add event listeners for risk toggles
        for (const btn of roleDetails.querySelectorAll('.toggle-btn')) {
            btn.addEventListener('click', async () => {
                const roleId = btn.dataset.roleId;
                const risk = btn.dataset.risk;
                const status = btn.dataset.status;
                await this.toggleRisk(roleId, risk, status);
            });
        }

        // Add event listeners for user toggles
        for (const item of roleDetails.querySelectorAll('.assigned-user-item')) {
            item.addEventListener('click', () => {
                const checkbox = item.querySelector("input")
                checkbox.checked = !checkbox.checked;
                if (checkbox.checked) {
                    item.classList.add("selected");
                } else {
                    item.classList.remove("selected");
                }
            });
        }

        // Add event listener
        this.shadowRoot.getElementById("role-member-add").addEventListener('click', () => this.#assignedUser(role));
        this.shadowRoot.getElementById("role-edit").addEventListener('click', () => this.#edit(role));
        this.shadowRoot.getElementById("role-delete").addEventListener('click', () => this.#delete(role));

        // Populate role member
        const memberList = this.shadowRoot.getElementById("role-member-list");
        const members = this.availableUsers.filter((member) => { return role.members.includes(member.id) })

        for (const member of members) {
            memberList.appendChild(this.#memberItemList(member, role.id));
        }
        
        i18n.translatePage(roleDetails);
    }

    #renderServerCategory(role) {
        if (this.#entity === null) {
            return `<div class="risk-big-category-header">Risk specific for server</div>
                    ${this.availableServerRisks.map(category => this.#renderRisk(category, role)).join('')}`;
        } else {
            return ""
        }
    }

    #renderRoomCategory(role) {
        return `<div class="risk-big-category-header">Risk specific for room</div>
                ${this.availableRoomRisks.map(category => this.#renderRisk(category, role)).join('')}`;
    }

    #renderRisk(category, role) {
        return `<div class="risk-category">
                    <div class="risk-category-header">${category.title}</div>
                    <div class="risk-container">
                        ${category.risks.map(risk => `
                            <div class="risk-item">
                                <div class="risk-name">${risk.title}</div>
                                <div class="risk-toggle">
                                    <button class="toggle-btn ${this.#findRisk(role, risk)?.mode === 'ENABLE' ? 'background-green' : ''}" 
                                            data-role-id="${role.id}" data-risk="${risk.type}" data-status="ENABLE"
                                            data-i18n="server.roles.risk.enable">
                                        Enabled
                                    </button>
                                    <button class="toggle-btn ${this.#findRisk(role, risk)?.mode === 'DISABLE' ? 'background-red' : ''}" 
                                            data-role-id="${role.id}" data-risk="${risk.type}" data-status="DISABLE"
                                            data-i18n="server.roles.risk.disabled">
                                        Disabled
                                    </button>
                                    <button class="toggle-btn ${(this.#findRisk(role, risk)?.mode === 'DEFAULT' || !this.#findRisk(role, risk)) ? 'background-gray' : ''}" 
                                            data-role-id="${role.id}" data-risk="${risk.type}" data-status="DEFAULT"
                                            data-i18n="server.roles.risk.default">
                                        Inherited
                                    </button>
                                </div>
                            </div>
                        `).join('')}
                    </div>
                </div>`;
    }

    #memberItemList(data, roleId) {
        const DIV = document.createElement('div');
        DIV.className = `config-item risk-member-item`;

        const profilePicture = MediaServer.profiles(data.id);

        DIV.innerHTML = `
            <div class="relative">
                <img src="${profilePicture}" alt="PFP" class="icon ring-2" data-id="${data.id}" />
            </div>
            <div class="user">
                <div class="name" id="user-name">
                    ${data.displayName}
                    <div class="login">${data.login}</div>
                </div>
            </div>
        `;

        // Context menu
        const DIV_CM = document.createElement('div');
        DIV_CM.className = "context-menu";
        DIV_CM.appendChild(this.#createContextMenuButton("icon", "<revoice-icon-circle-x></revoice-icon-circle-x>", () => this.#updateRoleOfMember(roleId, [data.id], 'DELETE')));
        DIV.appendChild(DIV_CM);

        return DIV;
    }

    #findRisk(role, risk) {
        return role.risks.find(item => item.type === risk.type && item.entity === this.#entity);
    }

    async toggleRisk(roleId, riskName, status) {
        try {
            await this.updateRoleRisk(roleId, riskName, status);
            await this.fetchRoles();
            this.renderRoleDetails();
        } catch (error) {
            console.error('Error updating risk:', error);
            this.showError('server.roles.error.update.risk');
        }
    }

    async #newRole() {
        Modal.toggle({
            title: 'Create New Role',
            showCancelButton: true,
            focusConfirm: false,
            confirmButtonText: i18n.translateOne("modal.add"),
            width: "30rem",
            html: `
            <form id="new-role-popup" class='popup'>
                <div class="server-structure-form-group">
                    <label for="roleName" data-i18n="server.roles.new.name">Role Name</label>
                    <input type="text" id="roleName" data-i18n-placeholder="server.roles.new.name.placeholder" placeholder="Enter role name">
                </div>
                <div class="server-structure-form-group">
                    <label for="roleColor" data-i18n="server.roles.new.color">Color</label>
                    <input style="height: 2.5rem; padding: 0" type="color" id="roleColor" value="#5e8c61">
                </div>
                <div class="server-structure-form-group">
                    <label for="rolePriority" data-i18n="server.roles.new.priority">Priority</label>
                    <input type="number" id="rolePriority" placeholder="1" min="1">
                </div>
            </form > `,
            didOpen: () => {
                i18n.translatePage(document.getElementById("new-role-popup"))
            },
            preConfirm: () => {
                const name = document.getElementById('roleName').value;
                const color = document.getElementById('roleColor').value;
                const priority = document.getElementById('rolePriority').value;
                return { name: name, color: color, priority: Number.parseInt(priority) };
            }
        }).then(async (result) => {
            if (result.isConfirmed) {
                const newRole = await this.createRoleAPI(result.data);
                await this.fetchRoles()
                this.renderRoles();
                this.selectRole(newRole.id);
            }
        });
    }

    async #assignedUser(role) {
        Modal.toggle({
            title: i18n.translateOne("server.roles.add.members"),
            showCancelButton: true,
            focusConfirm: false,
            confirmButtonText: i18n.translateOne("modal.save"),
            width: "30rem",
            preConfirm: () => {
                // Get form values
                let users = Array.from(document.querySelectorAll('.assigned-user-item'));
                users = users.filter(elt => elt.querySelector("input:checked"))
                users = users.map(item => item.dataset.userId)
                return users;
            },
            html: `
            <link href="src/js/component/server.roles.component.popup.css" rel="stylesheet" />
            <form id="add-members-popup" class='popup'>
                <input type="text" placeholder="Search..." id="add-members-search">
                <div id="add-members-list" class="members-list">
                    ${this.availableUsers.filter(user => !role.members.includes(user.id)).map(user => this.#memberItem(role, user)).join('')}
                </div>
            </form>`,
            didOpen: async () => {
                await this.fetchUsers();

                const filterMemberList = (elt, value) => {
                    const items = Array.from(elt.querySelectorAll(".assigned-user-item"))
                    for (const item of items) {
                        item.classList.remove("hide")
                        if (!item.dataset.userLogin.toLowerCase().includes(value.toLowerCase())
                            && !item.dataset.userDisplayName.toLowerCase().includes(value.toLowerCase())) {
                            item.classList.add("hide")
                        }
                    }
                }

                document.getElementById("add-members-search").addEventListener('input', (e) => {
                    const elt = document.querySelector("#add-members-list")
                    filterMemberList(elt, e.target.value)
                });
                i18n.translatePage(document.getElementById("add-members-popup"))
            }
        }).then(async (result) => {
            if (result.isConfirmed) {
                const roleId = role.id
                await this.#updateRoleOfMember(roleId, result.data, 'PUT');
            }
        });
    }

    async #updateRoleOfMember(roleId, user, type) {
        try {
            await CoreServer.fetch(`/role/${roleId}/user`, type, user)
            await this.fetchRoles();
            this.renderRoleDetails();
        } catch (error) {
            console.error('Error updating user:', error);
            this.showError('server.roles.error.update.user');
        }
    }

    #memberItem(role, user) {
        return `<div class="assigned-user-item"
                     data-role-id="${role.id}"
                     data-user-id="${user.id}"
                     data-user-login="${user.login}"
                     data-user-display-name="${user.displayName}">
                    <div class="assigned-user-checkbox">
                        <input type="checkbox" ${role.members.includes(user.id) ? 'checked' : ''} readonly>
                    </div>
                    <div>${user.displayName}</div>
                </div>`;
    }

    #createContextMenuButton(className, innerHTML, onclick, title = "") {
        const DIV = document.createElement('div');
        DIV.className = className;
        DIV.innerHTML = innerHTML;
        DIV.onclick = onclick;
        DIV.title = title;
        return DIV;
    }

    /** @param {string} keyMessage */
    showError(keyMessage) {
        const roleDetails = this.shadowRoot.getElementById('roleDetails');
        const errorDiv = document.createElement('div');
        errorDiv.className = 'error-message';
        errorDiv.textContent = i18n.translateOne(keyMessage);
        roleDetails.prepend(errorDiv);
        setTimeout(() => errorDiv.remove(), 3000);
    }

    get #entity() {
        const entity = this.getAttribute("entity");
        if (entity === "") {
            return null;
        }
        return entity;
    }

    #edit(role) {
        Modal.toggle({
            title: i18n.translateOne("server.roles.edit.title"),
            showCancelButton: true,
            focusConfirm: false,
            confirmButtonText: i18n.translateOne("modal.save"),
            width: "30rem",
            html: `
            <form id="new-role-popup" class='popup'>
                <div class="server-structure-form-group">
                    <label for="roleName" data-i18n="server.roles.new.name">Role Name</label>
                    <input type="text" id="roleName" data-i18n-placeholder="server.roles.new.name.placeholder" placeholder="Enter role name" value="${role.name}">
                </div>
                <div class="server-structure-form-group">
                    <label for="roleColor" data-i18n="server.roles.new.color">Color</label>
                    <input style="height: 2.5rem; padding: 0" type="color" id="roleColor" value="${role.color}">
                </div>
                <div class="server-structure-form-group">
                    <label for="rolePriority" data-i18n="server.roles.new.priority">Priority</label>
                    <input type="number" id="rolePriority" placeholder="1" min="1" value="${role.priority}">
                </div>
            </form > `,
            didOpen: () => {
                i18n.translatePage(document.getElementById("new-role-popup"))
            },
            preConfirm: () => {
                const name = document.getElementById('roleName').value;
                const color = document.getElementById('roleColor').value;
                const priority = document.getElementById('rolePriority').value;
                return { name: name, color: color, priority: Number.parseInt(priority) };
            }
        }).then(async (result) => {
            if (result.isConfirmed) {
                await this.patchRoleAPI(role.id, result.data);
                await this.fetchRoles();
                this.renderRoles();
                this.selectRole(role.id);
            }
        });
    }

    #delete(role) {
        Modal.toggle({
            title: i18n.translateOne("server.roles.delete.title"),
            focusConfirm: false,
            confirmButtonText: i18n.translateOne("modal.delete"),
            confirmButtonClass: "background-red",
            showCancelButton: true,
            cancelButtonText: i18n.translateOne("modal.cancel"),
            width: "30rem",
        }).then(async (result) => {
            if (result.isConfirmed) {
                console.warn("Not implemented in API");
            }
        });
    }
}

customElements.define('revoice-server-roles', ServerRolesWebComponent);
