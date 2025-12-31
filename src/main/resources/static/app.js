let tg = window.Telegram.WebApp;

// Инициализация
tg.ready();
tg.expand();

// Применяем цветовую схему Telegram
document.body.style.backgroundColor = tg.themeParams.bg_color || '#ffffff';
document.body.style.color = tg.themeParams.text_color || '#000000';

// Показываем информацию о пользователе
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

// Обработка кнопки - запрос к API того же сервера
document.getElementById('btn').addEventListener('click', async () => {
    const resultDiv = document.getElementById('result');
    resultDiv.innerHTML = '<p>Загрузка...</p>';

    try {
        // Запрос идёт на тот же домен, откуда загружен фронтенд
        const response = await fetch('/api/data', {
            method: 'GET',
            headers: {
                'Content-Type': 'application/json'
            }
        });

        if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status}`);
        }

        const data = await response.json();

        resultDiv.innerHTML = `
            <h3>Ответ от сервера:</h3>
            <p>${data.message}</p>
            <p><small>Timestamp: ${data.timestamp}</small></p>
        `;

        // Можно также использовать нативные уведомления Telegram
        tg.showAlert('Данные успешно получены!');

    } catch (error) {
        resultDiv.innerHTML = `<p style="color: red;">Ошибка: ${error.message}</p>`;
        tg.showAlert(`Ошибка: ${error.message}`);
    }
});

// Пример отправки данных на сервер с информацией от Telegram
async function sendUserData() {
    try {
        const response = await fetch('/api/user', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({
                initData: tg.initData, // Строка с подписанными данными от Telegram
                userData: tg.initDataUnsafe.user
            })
        });

        const result = await response.json();
        console.log('User data saved:', result);
    } catch (error) {
        console.error('Error saving user data:', error);
    }
}

// Настройка главной кнопки Telegram
tg.MainButton.setText('Закрыть приложение');
tg.MainButton.show();
tg.MainButton.onClick(() => {
    tg.close();
});

// Настройка кнопки "Назад"
tg.BackButton.show();
tg.BackButton.onClick(() => {
    tg.close();
});