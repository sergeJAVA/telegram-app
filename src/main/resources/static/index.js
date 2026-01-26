let tg = window.Telegram.WebApp;
let cartCount = 0;
let currentCategory = 'all';

tg.ready();
tg.expand();

// Применение темы
document.body.style.backgroundColor = tg.themeParams.bg_color || '#ffffff';

// Получение информации о пользователе
const userInfo = tg.initDataUnsafe.user;
const userProfileDiv = document.getElementById('userProfile');

if (userInfo) {
    document.getElementById('userName').textContent = `${userInfo.first_name} ${userInfo.last_name || ''}`.trim();
    document.getElementById('userUsername').textContent = `@${userInfo.username || 'user'}`;
    userProfileDiv.style.display = 'flex';
}

// Пример товаров
const products = [
    { id: 1, name: 'Классическая рубашка', price: 2490, category: 'shirts', emoji: '👔' },
    { id: 2, name: 'Чёрные джинсы', price: 3990, category: 'pants', emoji: '👖' },
    { id: 3, name: 'Летнее платье', price: 1990, category: 'dresses', emoji: '👗' },
    { id: 4, name: 'Белые кроссовки', price: 5990, category: 'shoes', emoji: '👟' },
    { id: 5, name: 'Синяя рубашка', price: 2290, category: 'shirts', emoji: '👔' },
    { id: 6, name: 'Серые штаны', price: 3590, category: 'pants', emoji: '👖' },
    { id: 7, name: 'Красное платье', price: 2490, category: 'dresses', emoji: '👗' },
    { id: 8, name: 'Чёрные ботинки', price: 6990, category: 'shoes', emoji: '👢' },
];

// Отрисовка товаров
function renderProducts() {
    const container = document.getElementById('productsContainer');
    const emptyState = document.getElementById('emptyState');

    const filtered = currentCategory === 'all'
        ? products
        : products.filter(p => p.category === currentCategory);

    if (filtered.length === 0) {
        container.style.display = 'none';
        emptyState.style.display = 'block';
        return;
    }

    container.style.display = 'grid';
    emptyState.style.display = 'none';
    container.innerHTML = filtered.map(product => `
        <div class="product-card">
            <div class="product-image">${product.emoji}</div>
            <div class="product-info">
                <div class="product-name">${product.name}</div>
                <div class="product-price">${product.price}₽</div>
                <button class="add-to-cart-btn" onclick="addToCart(${product.id}, '${product.name}', ${product.price})">
                    Добавить в корзину
                </button>
            </div>
        </div>
    `).join('');
}

// Фильтрация по категориям
document.querySelectorAll('.category-btn').forEach(btn => {
    btn.addEventListener('click', () => {
        document.querySelectorAll('.category-btn').forEach(b => b.classList.remove('active'));
        btn.classList.add('active');
        currentCategory = btn.dataset.category;
        renderProducts();
    });
});

// Добавление в корзину
function addToCart(id, name, price) {
    cartCount++;
    document.getElementById('cartBadge').textContent = cartCount;
    document.getElementById('cartBadge').style.display = 'flex';
    tg.showAlert(`"${name}" добавлен в корзину!`);
}

// Переход на профиль
document.getElementById('profileIcon').addEventListener('click', () => {
    window.location.href = '/profile.html';
});

// Переход в корзину
document.getElementById('cartIcon').addEventListener('click', () => {
    tg.showAlert(`В корзине ${cartCount} товаров`);
    // window.location.href = '/cart'; // Раскомментируй для перехода
});

// Первая отрисовка
renderProducts();