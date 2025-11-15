import {requireAuth, logout} from '../core/auth.js';
import {bindLogout} from '../core/ui.js';
import {transactionApi} from '../api/transactionApi.js';
import {httpClient} from '../core/httpClient.js';

requireAuth();
bindLogout('logoutBtn', logout);

const tbody = document.querySelector('#transactionsTable tbody');

(async function init() {
    const tx = await transactionApi.myTransactions().catch(() => []);
    tbody.innerHTML = '';
    tx.forEach(t => {
        tbody.insertAdjacentHTML('beforeend', `
            <tr>
                <td>${t.createdAt || ''}</td>
                <td>${t.accountId || ''}</td>
                <td>${t.description || ''}</td>
                <td>${t.direction === 'OUT' ? '-' : '+'}${t.amount} ${t.currency}</td>
                <td>${t.status}</td>
                <td><button class="receipt-btn btn btn--secondary" data-id="${t.id}">Чек</button></td>
            </tr>
        `);
    });

    tbody.addEventListener('click', async (e) => {
        if (e.target.classList.contains('receipt-btn')) {
            const id = e.target.dataset.id;
            try {
                const receiptJson = await httpClient.get(`/transactions/${id}/receipt`);
                const blob = new Blob([receiptJson], { type: 'application/json' });
                const url = URL.createObjectURL(blob);
                const a = document.createElement('a');
                a.href = url;
                a.download = `receipt_${id}.json`;
                a.click();
                URL.revokeObjectURL(url);
            } catch (err) {
                console.error('Failed to fetch receipt', err);
                alert('Не удалось получить чек');
            }
        }
    });
})();
