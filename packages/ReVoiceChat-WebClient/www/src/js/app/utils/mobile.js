export default class MobileController {

  static load() {
    const toggleSidebar = document.getElementById('toggleSidebar');
    const toggleUsers = document.getElementById('toggleUsers');
    const sidebarServers = document.querySelector('.sidebar.instances-list');
    const sidebarRooms = document.querySelector('.sidebar.server');
    const sidebarRight = document.getElementById('sidebar-users');
    const overlay = document.getElementById('overlay');

    toggleSidebar.addEventListener('click', () => {
      sidebarServers.classList.toggle('show');
      sidebarRooms.classList.toggle('show');
      sidebarRight.classList.remove('show');
      overlay.classList.toggle('show');
    });

    toggleUsers.addEventListener('click', () => {
      sidebarRight.classList.toggle('show');
      sidebarServers.classList.remove('show');
      sidebarRooms.classList.remove('show');
      overlay.classList.toggle('show');
    });

    overlay.addEventListener('click', () => {
      sidebarServers.classList.remove('show');
      sidebarRooms.classList.remove('show');
      sidebarRight.classList.remove('show');
      overlay.classList.remove('show');
    });
  }
}