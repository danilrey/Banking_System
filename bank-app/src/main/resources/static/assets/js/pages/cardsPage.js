import {requireAuth, logout} from '../core/auth.js';
import {bindLogout} from '../core/ui.js';
import {cardApi} from '../api/cardApi.js';
import {httpClient} from '../core/httpClient.js';

requireAuth();
bindLogout('logoutBtn', logout);

const tbody = document.querySelector('#cardsTable tbody');

(async function init() {
    loadCards();
})();

async function loadCards() {
    const cards = await cardApi.myCards().catch(() => []);
    tbody.innerHTML = '';
    cards.forEach(c => {
        tbody.insertAdjacentHTML('beforeend', `
            <tr>
                <td>**** **** **** ${String(c.cardNumber).slice(-4)}</td>
                <td>${c.accountId}</td>
                <td><span class="badge badge--${c.status === 'ACTIVE' ? 'success' : 'muted'}">${c.status}</span></td>
                <td>${c.expiryMonth}/${c.expiryYear}</td>
            </tr>
        `);
    });
}

document.getElementById('createCardForm').addEventListener('submit', async (e) => {
    e.preventDefault();
    const accountId = document.getElementById('accountId').value;
    try {
        await httpClient.post('/api/cards', { accountId: parseInt(accountId) });
        loadCards();
    } catch (err) {
    }
});
