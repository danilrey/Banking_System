import {requireAuth, logout} from '../core/auth.js';
import {bindLogout} from '../core/ui.js';
import {bonusApi} from '../api/bonusApi.js';

requireAuth();
bindLogout('logoutBtn', logout);

const balanceEl = document.getElementById('bonusBalance');

(async function init() {
    try {
        const balance = await bonusApi.getBalance();
        balanceEl.textContent = `${Number(balance.balance || 0).toLocaleString('ru-RU')} ₸`;
    } catch {}
})();

// Обработчик формы добавления бонуса
document.getElementById('addBonusForm').addEventListener('submit', async (e) => {
    e.preventDefault();
    const category = document.getElementById('bonusCategory').value;
    try {
        await bonusApi.addBonus(category);
        alert('Бонус добавлен для категории: ' + category);
    } catch (error) {
        console.error('Error adding bonus:', error);
        alert('Ошибка при добавлении бонуса');
    }
});

// Обработчик формы применения кешбека
document.getElementById('applyCashbackForm').addEventListener('submit', async (e) => {
    e.preventDefault();
    const amount = parseFloat(document.getElementById('cashbackAmount').value);
    const category = document.getElementById('cashbackCategory').value;
    try {
        const result = await bonusApi.applyCashback({ amount, category });
        alert(`Кешбек применён: ${result.cashbackAmount} ₸`);
        // Обновить баланс
        const balance = await bonusApi.getBalance();
        balanceEl.textContent = `${Number(balance.balance || 0).toLocaleString('ru-RU')} ₸`;
    } catch (error) {
        console.error('Error applying cashback:', error);
        alert('Ошибка при применении кешбека');
    }
});
