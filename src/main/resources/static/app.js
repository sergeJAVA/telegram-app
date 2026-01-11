let tg = window.Telegram.WebApp;

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

// Обработка кнопки
document.getElementById('btn').addEventListener('click', async () => {
    await sendUserData(); // Вызываем функцию
});

async function sendUserData() {
    const resultDiv = document.getElementById('result');
    resultDiv.innerHTML = '<p>Загрузка...</p>';

    try {
        const response = await fetch('/api/user', {
            method: 'POST',
            headers: {
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
            <h3>Ответ от сервера:</h3>
            <p>${result.message}</p>
            <p><strong>Статус:</strong> ${result.status}</p>
        `;

        tg.showAlert('Данные успешно отправлены!');
        console.log('User data saved:', result);
    } catch (error) {
        resultDiv.innerHTML = `<p style="color: red;">Ошибка: ${error.message}</p>`;
        tg.showAlert(`Ошибка: ${error.message}`);
        console.error('Error saving user data:', error);
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