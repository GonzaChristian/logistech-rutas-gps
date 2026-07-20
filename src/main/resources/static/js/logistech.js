(() => {
    const body = document.body;
    const openButton = document.querySelector('[data-sidebar-open]');
    const closeButtons = document.querySelectorAll('[data-sidebar-close]');

    const openSidebar = () => body.classList.add('sidebar-open');
    const closeSidebar = () => body.classList.remove('sidebar-open');

    openButton?.addEventListener('click', openSidebar);
    closeButtons.forEach((button) => button.addEventListener('click', closeSidebar));
    document.addEventListener('keydown', (event) => {
        if (event.key === 'Escape') closeSidebar();
    });

    document.querySelectorAll('.side-nav a').forEach((link) => {
        link.addEventListener('click', closeSidebar);
    });
})();
