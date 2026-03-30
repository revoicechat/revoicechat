import {SpinnerOnButton} from "../../component/button.spinner.component.js";
import {i18n} from "../../lib/i18n.js";
import CoreServer from "../core/core.server.js";
import Modal from "../../component/modal.component.js";

export class ServerSettingsRoomController {

    #modalRisks = new Modal("custom-modal-risks");

    #popupData = {
        name: null,
        type: null
    };
    #structureData = { items: [] };
    /** @type {Record<string, RoomRepresentation>} */
    #roomsData = {};
    #roomsNotRendered = [];
    #draggedElement = null;


    /**
     * @param {ServerSettingsController} serverSettings
     */
    constructor(serverSettings) {
        this.serverSettings = serverSettings
    }

    async roomLoad() {
        /** @type {RoomRepresentation[]} */
        const roomResult = await CoreServer.fetch(`/server/${this.serverSettings.server.id}/room`, 'GET');
        if (roomResult) {
            this.#roomsData = {};
            for (const room of roomResult) {
                this.#roomsData[room.id] = room;
            }
            this.#render();
        }
    }

    /**
     * @param {string[]} flattenRisks
     * @param {boolean} isAdmin
     */
    handleRisks(isAdmin, flattenRisks) {
        const roomRisks = new Set(['SERVER_ROOM_UPDATE', 'SERVER_ROOM_DELETE']);
        if (isAdmin || flattenRisks.some(elem => roomRisks.has(elem))) {
            void this.#structureLoad();
            void this.roomLoad();
            this.#addRoomEventHandler();
        } else {
            this.#removeRoomEventHandler();
            if (this.serverSettings.currentTab === "rooms") {
                this.serverSettings.select('overview');
            }
        }
    }

    #addRoomEventHandler() {
        document.getElementById(`server-setting-structure-save`).onclick = () => this.#structureSave();
        document.getElementById(`server-setting-room-add`).onclick = () => this.#roomAdd();
        document.getElementById(`server-setting-category-add`).onclick = () => this.#categoryAdd();
    }

    #removeRoomEventHandler() {
        document.getElementById(`server-setting-structure-save`).onclick = null;
        document.getElementById(`server-setting-room-add`).onclick = null;
        document.getElementById(`server-setting-category-add`).onclick = null;
    }

    async #structureLoad() {
        /** @type {ServerStructure} */
        const struct = await CoreServer.fetch(`/server/${this.serverSettings.server.id}/structure`, 'GET');
        if (struct) {
            this.#structureData = struct;
            this.#render();
        }
    }

    async #roomAdd() {
        this.#popupData.name = 'New room';
        this.#popupData.type = 'TEXT';

        Modal.toggle({
            title: i18n.translateOne("server.structure.room.add"),
            showCancelButton: true,
            focusConfirm: false,
            confirmButtonText: i18n.translateOne("modal.add"),
            width: "30rem",
            html: `
            <form id="popup-new-room" class='popup'>
                <label data-i18n="server.structure.room.name">Room name</label>
                <input type='text' id='popup-name'>
                <br/>
                <br/>
                <label data-i18n="server.structure.room.type">Room type</label>
                <select id='popup-type'>
                    <option value='TEXT' data-i18n="server.structure.room.type.text">Text</option>
                    <option value='VOICE' data-i18n="server.structure.room.type.voice">Voice (Built-in)</option>
                </select>
            </form>`,
            didOpen: () => {
                document.getElementById('popup-name').oninput = () => { this.#popupData.name = document.getElementById('popup-name').value };
                document.getElementById('popup-type').oninput = () => { this.#popupData.type = document.getElementById('popup-type').value };
                i18n.translatePage(document.getElementById("popup-new-room"))
            }
        }).then(async (result) => {
            if (result.isConfirmed) {
                await CoreServer.fetch(`/server/${this.serverSettings.server.id}/room`, 'PUT', this.#popupData);
                await this.roomLoad();
            }
        });
    }

    /**
     * @param {ServerRoom} item
     * @return {Promise<void>}
     */
    async #roomEdit(item) {
        const data = this.#roomsData[item.id];
        this.#popupData.name = data.name;

        Modal.toggle({
            title: i18n.translateOne("server.structure.room.edit", [data.name]),
            showCancelButton: true,
            focusConfirm: false,
            confirmButtonText: i18n.translateOne("modal.edit"),
            width: "30rem",
            html: `
            <form id="popup-new-room" class='popup'>
                <label data-i18n="server.structure.room.name">Room name</label>
                <input type='text' id='popup-name' value='${data.name}'>
            </form>`,
            didOpen: () => {
                document.getElementById('popup-name').oninput = () => { this.#popupData.name = document.getElementById('popup-name').value };
                i18n.translatePage(document.getElementById("popup-new-room"))
            }
        }).then(async (result) => {
            if (result.isConfirmed) {
                await CoreServer.fetch(`/room/${data.id}`, 'PATCH', this.#popupData);
                await this.roomLoad();
            }
        });
    }

    /**
     * @param {string} idi
     * @return {Promise<void>}
     */
    async #roomRisksEdit(id) {
        const data = this.#roomsData[id];
        this.#popupData.name = data.name;

        this.#modalRisks.fire({
            title: i18n.translateOne("server.structure.room.roles", [data.name]),
            showCancelButton: false,
            focusConfirm: false,
            confirmButtonText: "Close",
            confirmButtonClass: "danger",
            width: "70rem",
            html: `<revoice-server-roles server-id="${this.serverSettings.server.id}" entity="${id}"></revoice-server-roles>`
        }).then(async () => {/* do nothing */});
    }

    /**
     * @param {ServerRoom} item
     * @return {Promise<void>}
     */
    async #roomDelete(item) {
        const data = this.#roomsData[item.id];
        Modal.toggle({
            title: i18n.translateOne("server.structure.room.delete.title", [data.name]),
            showCancelButton: true,
            focusCancel: true,
            confirmButtonText: i18n.translateOne("server.structure.room.delete"),
            confirmButtonClass: "danger",
        }).then(async (result) => {
            if (result.isConfirmed) {
                await CoreServer.fetch(`/room/${data.id}`, 'DELETE');
                await this.roomLoad();
            }
        });
    }

    #categoryAdd(parentItems = null) {
        const newCategory = {
            type: "CATEGORY",
            name: "New category",
            items: []
        };

        if (parentItems) {
            parentItems.push(newCategory);
        } else {
            this.#structureData.items.push(newCategory);
        }

        this.#render();
    }

    /**
     * @param {ServerCategory} item
     */
    #categoryEdit(item) {
        this.#popupData.name = item.name;

        Modal.toggle({
            title: i18n.translateOne("server.structure.category.edit", [item.name]),
            showCancelButton: true,
            focusConfirm: false,
            confirmButtonText: i18n.translateOne("modal.edit"),
            html: `
            <form id="popup-new-category" class='popup'>
                <label data-i18n="server.structure.category.name">Category name</label>
                <input type='text' id='popup-name' value='${item.name}'>
            </form>`,
            didOpen: () => {
                document.getElementById('popup-name').oninput = () => { this.#popupData.name = document.getElementById('popup-name').value };
                i18n.translatePage(document.getElementById("popup-new-category"))
            }
        }).then(async (result) => {
            if (result.isConfirmed) {
                item.name = this.#popupData.name;
                this.#render();
            }
        });
    }

    /**
     * @param {ServerCategory} item
     * @param {ServerItem[]} parentItems
     */
    #categoryDelete(item, parentItems) {
        Modal.toggle({
            title: i18n.translateOne("server.structure.category.delete", [item.name]),
            showCancelButton: true,
            focusCancel: true,
            confirmButtonText: i18n.translateOne("server.structure.category.delete.confirm"),
            confirmButtonClass: "danger",
            allowOutsideClick: false,
        }).then(async (result) => {
            if (result.isConfirmed) {
                const index = parentItems.indexOf(item);
                if (index > -1) {
                    parentItems.splice(index, 1);
                }
                this.#render();
            }
        });
    }

    async #structureSave() {
        const spinner = new SpinnerOnButton("server-setting-structure-save")
        spinner.run()
        this.#structureClean(this.#structureData.items);

        try {
            await CoreServer.fetch(`/server/${this.serverSettings.server.id}/structure`, 'PATCH', this.#structureData);
            spinner.success()
        }
        catch (error) {
            spinner.error();
            await Modal.toggleError("Updating structure failed");
        }
    }

    #structureClean(parent) {
        if (!parent) {
            return;
        }

        for (const item of parent) {
            if (item.type === 'CATEGORY') {
                this.#structureClean(item.items);
            }
            if (item.type === 'ROOM') {
                if (this.#roomsData[item.id] === undefined) {
                    const index = parent.indexOf(item);
                    if (index > -1) {
                        parent.splice(index, 1);
                    }
                }
            }
        }
    }

    #handleDragStart(e, item) {
        if (!this.#draggedElement) {
            this.#draggedElement = {
                item: item,
                sourceParent: this.#findParent(item)
            };
            e.target.classList.add('dragging');
            const dropZones = document.querySelectorAll('.server-structure-drop-zone');
            for (const dropZone of dropZones) {
                dropZone.classList.add('active');
            }
        }
    }

    #handleDragEnd(e) {
        e.target.classList.remove('dragging');
        const dropZones = document.querySelectorAll('.server-structure-drop-zone');
        for (const dropZone of dropZones) {
            dropZone.classList.remove('active');
        }
    }

    #handleDragOver(e) {
        e.preventDefault();
        e.currentTarget.classList.add('drag-over');
    }

    #handleDragLeave(e) {
        e.currentTarget.classList.remove('drag-over');
    }

    #handleDrop(e, targetParentItems, position) {
        e.preventDefault();
        e.currentTarget.classList.remove('drag-over');

        if (!this.#draggedElement) return;

        const { item, sourceParent } = this.#draggedElement;

        // Supprimer de la source
        if (sourceParent) {
            const sourceIndex = sourceParent.indexOf(item);
            if (sourceIndex > -1) {
                if (targetParentItems === sourceParent) {
                    position -= 1
                }
                sourceParent.splice(sourceIndex, 1);
            }
        }

        // Ajouter à la destination
        targetParentItems.splice(position, 0, item);
        this.#draggedElement = null;
        this.#render();
    }

    #findParent(targetItem, items = this.#structureData.items) {
        for (let item of items) {
            if (item === targetItem) {
                return this.#structureData.items;
            }
            if (item.type === 'CATEGORY' && item.items) {
                if (item.items.includes(targetItem)) {
                    return item.items;
                }
                const found = this.#findParent(targetItem, item.items);
                if (found) return found;
            }
        }
        return null;
    }

    /**
     *
     * @param {ServerItem} item
     * @param {ServerItem[]} parentItems
     * @param {number} level
     * @return {HTMLDivElement|null}
     */
    #renderItem(item, parentItems, level = 0) {
        const itemDiv = document.createElement('div');
        itemDiv.className = `server-structure-tree-item server-structure-${item.type.toLowerCase()}`;
        itemDiv.draggable = true;

        itemDiv.addEventListener('dragstart', (e) => this.#handleDragStart(e, item));
        itemDiv.addEventListener('dragend', this.#handleDragEnd);

        const headerDiv = document.createElement('div');
        headerDiv.className = 'server-structure-item-header';

        const updateHidden = this.serverSettings.flattenRisks.includes('SERVER_ROOM_UPDATE') ? "" : "hidden";
        const deleteHidden = this.serverSettings.flattenRisks.includes('SERVER_ROOM_DELETE') ? "" : "hidden";

        switch (item.type) {
            case 'ROOM': {
                const serverRoom = /** @type {ServerRoom} */ (item)
                // Remove room being rendered from list of not render
                this.#roomsNotRendered = this.#roomsNotRendered.filter((id) => id !== serverRoom.id);

                const room = this.#roomsData[serverRoom.id];
                if (room === null || room === undefined) {
                    return null;
                }

                const icon = room.type === 'TEXT'
                    ? '<revoice-icon-chat-bubble class="size-small" ></revoice-icon-chat-bubble>'
                    : '<revoice-icon-phone class="size-small"></revoice-icon-phone>';
                headerDiv.innerHTML = `
                <span class="server-structure-item-icon">${icon}</span>
                <div class="server-structure-item-content">
                    <span class="server-structure-item-name">${room.name}</span>
                    <span class="server-structure-item-id">${room.id}</span>
                </div>
                <div class="server-structure-item-actions">
                    <button class="server-structure-btn btn-edit-risks ${updateHidden}" data-item='${JSON.stringify(item)}'><revoice-icon-role class="size-smaller"></revoice-icon-role></button>
                    <button class="server-structure-btn btn-edit ${updateHidden}" data-item='${JSON.stringify(item)}'><revoice-icon-pencil class="size-smaller"></revoice-icon-pencil></button>
                    <button class="server-structure-btn btn-delete ${deleteHidden}" data-item='${JSON.stringify(item)}' data-parent='${JSON.stringify(parentItems)}'><revoice-icon-trash class="size-smaller"></revoice-icon-trash></button>
                </div>`;
                break;
            }
            case 'CATEGORY': {
                const serverCategory = /** @type {ServerCategory} */ (item)
                headerDiv.innerHTML = `
                <span class="server-structure-item-icon"><revoice-icon-folder class="size-small"></revoice-icon-folder></span>
                <div class="server-structure-item-content">
                    <span class="server-structure-item-name">${serverCategory.name}</span>
                </div>
                <div class="server-structure-item-actions">
                    <button class="server-structure-btn btn-edit" data-item='${JSON.stringify(item)}'><revoice-icon-pencil class="size-smaller"></revoice-icon-pencil></button>
                    <button class="server-structure-btn btn-delete" data-item='${JSON.stringify(item)}' data-parent='${JSON.stringify(parentItems)}'><revoice-icon-trash class="size-smaller"></revoice-icon-trash></button>
                    <button class="server-structure-btn btn-add" onclick="event.stopPropagation(); categoryAdd(arguments[0].items)"><revoice-icon-folder-plus class="size-smaller"></revoice-icon-folder-plus></button>
                </div>`;
                break;
            }
        }

        const editBtn = headerDiv.querySelector('.btn-edit');
        const editRisksBtn = headerDiv.querySelector('.btn-edit-risks');
        const deleteBtn = headerDiv.querySelector('.btn-delete');

        editBtn.onclick = (e) => this.#editServerItem(e, item);
        if (editRisksBtn) {
            editRisksBtn.onclick = (e) => this.#editRisks(e, item);
        }
        deleteBtn.onclick = (e) => this.#deleteServerItem(e, item, parentItems);

        if (item.type === 'CATEGORY') {
            const serverCategory =  /** @type {ServerCategory} */ (item)
            const categoryAddBtn = headerDiv.querySelector('.btn-add');
            categoryAddBtn.onclick = (e) => {
                e.stopPropagation();
                this.#categoryAdd(serverCategory.items);
            };
        }

        itemDiv.appendChild(headerDiv);

        if (item.type === 'CATEGORY') {
            this.#renderCategory(item, level, itemDiv);
        }

        return itemDiv;
    }

    #renderCategory(item, level, itemDiv) {
        const serverCategory = /** @type {ServerCategory} */ (item)
        if (serverCategory.items && serverCategory.items.length > 0) {
            if (serverCategory.items && serverCategory.items.length > 0) {
                const childrenDiv = document.createElement('div');
                childrenDiv.className = 'server-structure-item-children';
                childrenDiv.appendChild(this.#renderDropZone(item, 0));
                let posSubCategory = 1
                for (const childItem of serverCategory.items) {
                    const renderedItem = this.#renderItem(childItem, serverCategory.items, level + 1)
                    if (renderedItem) {
                        childrenDiv.appendChild(renderedItem);
                    }
                    childrenDiv.appendChild(this.#renderDropZone(item, posSubCategory++));
                }

                itemDiv.appendChild(childrenDiv);
            } else {
                // Catégorie vide
                const emptyDiv = document.createElement('div');
                emptyDiv.className = 'server-structure-item-children';
                emptyDiv.appendChild(this.#renderDropZone(item, 0));
                itemDiv.appendChild(emptyDiv);
            }
        }
    }

    #deleteServerItem(e, item, parentItems) {
        e.stopPropagation();
        switch (item.type) {
            case 'ROOM': {
                const serverRoom =  /** @type {ServerRoom} */ (item)
                void this.#roomDelete(serverRoom);
                break;
            }
            case 'CATEGORY': {
                const serverCategory =  /** @type {ServerCategory} */ (item)
                this.#categoryDelete(serverCategory, parentItems);
                break;
            }
        }
    }

    #editServerItem(e, item) {
        e.stopPropagation();
        switch (item.type) {
            case 'ROOM': {
                const serverRoom = /** @type {ServerRoom} */ (item)
                void this.#roomEdit(serverRoom);
                break;
            }
            case 'CATEGORY': {
                const serverCategory = /** @type {ServerCategory} */ (item)
                this.#categoryEdit(serverCategory);
                break;
            }
        }
    }

    #editRisks(e, item) {
        e.stopPropagation();
        switch (item.type) {
            case 'ROOM': {
                const serverRoom = /** @type {ServerRoom} */ (item)
                void this.#roomRisksEdit(serverRoom.id);
                break;
            }
            case 'CATEGORY': {
                throw new Error("category has currently no risks")
            }
        }
    }

    #renderDropZone(item, position, classNames = "") {
        const dropZone = document.createElement('div');
        dropZone.className = 'server-structure-drop-zone ' + classNames;
        dropZone.addEventListener('dragover', this.#handleDragOver);
        dropZone.addEventListener('dragleave', this.#handleDragLeave);
        dropZone.addEventListener('drop', (e) => this.#handleDrop(e, item.items, position));
        return dropZone;
    }

    #render() {
        const container = document.getElementById('treeContainer');
        // Clear existing items but keep root drop zone
        const existingItems = container.querySelectorAll('.server-structure-tree-item, .server-root-zone');
        for (const item of existingItems) {
            item.remove();
        }

        // Setup root drop zone
        const rootDropZone = document.getElementById('rootDropZone');
        rootDropZone.addEventListener('dragover', this.#handleDragOver);
        rootDropZone.addEventListener('dragleave', this.#handleDragLeave);
        rootDropZone.addEventListener('drop', (e) => this.#handleDrop(e, this.#structureData.items, 0));

        // Build items list from this.#roomsData
        for (const key in this.#roomsData) {
            this.#roomsNotRendered.push(key);
        }

        // Render all items in structure
        let posMain = 1
        for (const item of this.#structureData.items) {
            const renderedItem = this.#renderItem(item, this.#structureData.items)
            if (renderedItem) {
                container.appendChild(renderedItem);
            }
            container.appendChild(this.#renderDropZone(this.#structureData, posMain++, "server-root-zone"));
        }

        // Render remaining rooms
        for (const room of this.#roomsNotRendered) {
            container.appendChild(this.#renderItem({ id: room, type: 'ROOM' }, null));
        }
    }
}