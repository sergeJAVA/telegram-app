let tg = window.Telegram.WebApp;
let userToken = null;

tg.ready();
tg.expand();

document.body.style.backgroundColor = tg.themeParams.bg_color || '#ffffff';
document.body.style.color = tg.themeParams.text_color || '#000000';

const userInfo = tg.initDataUnsafe.user;
const userInfoDiv = document.getElementById('user-info');

if (userInfo) {
    userInfoDiv.innerHTML = `
        <h3>Информация о пользователе:</h3>
        <p><strong>ID:</strong> ${userInfo.id}</p>
        <p><strong>Имя:</strong> ${userInfo.first_name} ${userInfo.last_name || ''}</p>
        <p><strong>Username:</strong> @${userInfo.username || 'не указан'}</p>
        <p><strong>Язык:</strong> ${userInfo.language_code || 'не определён'}</p>
    `;
} else {
    userInfoDiv.innerHTML = '<p>Данные пользователя недоступны</p>';
}

// Инициализация при загрузке приложения
window.addEventListener('load', async () => {
    await initializeApp();
});

async function initializeApp() {
    const resultDiv = document.getElementById('result');
    resultDiv.innerHTML = '<p>Инициализация приложения...</p>';

    try {
        // Шаг 1: Получение JWT токена
        const tokenResponse = await fetch('/api/auth', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({
                initData: tg.initData
            })
        });

        if (!tokenResponse.ok) {
            const errorData = await tokenResponse.text();
            throw new Error(`Ошибка при получении токена: ${errorData}`);
        }

        const tokenData = await tokenResponse.json();
        userToken = tokenData.token;
        console.log('Token получен:', userToken);

        // Шаг 2: Проверка наличия пользователя в БД
        const validateResponse = await fetch('/api/users/validate-presence', {
            method: 'GET',
            headers: {
                'Authorization': `Bearer ${userToken}`,
                'Content-Type': 'application/json'
            }
        });

        const isPresent = validateResponse.ok ? (await validateResponse.json()) : false;
        console.log('Результат проверки:', isPresent);

        // Шаг 3: Отображение результата
        if (isPresent) {
            // Пользователь найден в БД
            resultDiv.innerHTML = `
                <h3>✅ Добро пожаловать!</h3>
                <p>Вы уже зарегистрированы в нашем приложении.</p>
            `;
            document.getElementById('btn').style.display = 'none';
            tg.showAlert('Вы успешно авторизованы!');
        } else {
            // Пользователь не найден - показываем кнопку регистрации
            resultDiv.innerHTML = `
                <h3>👋 Добро пожаловать!</h3>
                <p>Похоже, вы еще не зарегистрированы.</p>
            `;
            document.getElementById('btn').textContent = 'Зарегистрироваться';
            document.getElementById('btn').addEventListener('click', registerUser);
        }
    } catch (error) {
        resultDiv.innerHTML = `<p style="color: red;">Ошибка при инициализации: ${error.message}</p>`;
        tg.showAlert(`Ошибка: ${error.message}`);
        console.error('Ошибка инициализации:', error);
    }
}

async function registerUser() {
    const resultDiv = document.getElementById('result');
    const btn = document.getElementById('btn');
    btn.disabled = true;
    resultDiv.innerHTML = '<p>Регистрация...</p>';

    try {
        if (!userToken) {
            throw new Error('Токен не найден. Попробуйте перезагрузить приложение.');
        }

        const response = await fetch('/api/auth/signUp', {
            method: 'POST',
            headers: {
                'Authorization': `Bearer ${userToken}`,
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({
                initData: tg.initData
            })
        });

        if (!response.ok) {
            const errorData = await response.text();
            throw new Error(errorData);
        }

        const result = await response.json();

        resultDiv.innerHTML = `
            <h3>✅ Успешная регистрация!</h3>
            <p>${result.message}</p>
            <p><strong>Статус:</strong> ${result.status}</p>
        `;

        tg.showAlert('Вы успешно зарегистрированы!');
        btn.style.display = 'none';
        console.log('Пользователь зарегистрирован:', result);
    } catch (error) {
        resultDiv.innerHTML = `<p style="color: red;">Ошибка при регистрации: ${error.message}</p>`;
        tg.showAlert(`Ошибка: ${error.message}`);
        console.error('Ошибка регистрации:', error);
        btn.disabled = false;
    }
}

tg.MainButton.setText('Закрыть приложение');
tg.MainButton.show();
tg.MainButton.onClick(() => {
    tg.close();
});

tg.BackButton.show();
tg.BackButton.onClick(() => {
    tg.close();
});