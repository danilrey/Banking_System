import {requireAuth, logout} from '../core/auth.js';
import {bindLogout} from '../core/ui.js';
import { creditApi } from '../api/creditApi.js';

requireAuth();
bindLogout('logoutBtn', logout);

document.addEventListener('DOMContentLoaded', () => {
    loadCredits();

    const form = document.querySelector('form');
    if (form) {
        form.addEventListener('submit', async (e) => {
            e.preventDefault();
            if (!validateForm()) return;
            const formData = new FormData(form);
            const data = {
                principalAmount: parseFloat(formData.get('principalAmount')),
                currency: formData.get('currency'),
                interestRateAnnual: parseFloat(formData.get('interestRateAnnual')),
                termMonths: parseInt(formData.get('termMonths')),
                creditType: formData.get('creditType')
            };
            try {
                await creditApi.createCredit(data);
                loadCredits();
                form.reset();
                applyLimits('PERSONAL');
            } catch (error) {
                console.error('Error creating credit:', error);}
        });
    }

    document.addEventListener('click', async (e) => {
        if (e.target.classList.contains('close-btn')) {
            const id = e.target.dataset.id;
            if (confirm('Вы уверены, что хотите закрыть кредит?')) {
                try {
                    await creditApi.closeCredit(id);
                    loadCredits();
                } catch (error) {
                    console.error('Error closing credit:', error);
                }
            }
        }
    });
});

const limits = {
    PERSONAL: { minRate: 5.0, maxAmount: 1_000_000, maxTerm: 60 },
    BUSINESS: { minRate: 7.0, maxAmount: 5_000_000, maxTerm: 120 },
    FAMILY:   { minRate: 4.5, maxAmount: 2_000_000, maxTerm: 84 },
    MORTGAGE: { minRate: 3.5, maxAmount: 10_000_000, maxTerm: 360 },
    FARMER:   { minRate: 6.0, maxAmount: 3_000_000, maxTerm: 180 }
};

function applyLimits(type) {
    const { minRate, maxAmount, maxTerm } = limits[type] || limits.PERSONAL;
    minRateSpan.textContent = String(minRate);
    document.getElementById('interestRateAnnual').min = String(minRate);

    document.getElementById('maxAmount').textContent = String(maxAmount.toLocaleString());
    const principalEl = document.getElementById('principalAmount');
    principalEl.max = String(maxAmount);

    document.getElementById('maxTerm').textContent = String(maxTerm);
    const termEl = document.getElementById('termMonths');
    termEl.max = String(maxTerm);
}

creditTypeSelect.addEventListener('change', () => applyLimits(creditTypeSelect.value));
applyLimits(creditTypeSelect.value || 'PERSONAL');

function showError(input, msg) {
    input.classList.add('input--error');
    let err = input.nextElementSibling;
    if (!err || !err.classList.contains('error-text')) {
        err = document.createElement('div');
        err.className = 'error-text';
        input.after(err);
    }
    err.textContent = msg;
}

function clearError(input) {
    input.classList.remove('input--error');
    const err = input.nextElementSibling;
    if (err && err.classList.contains('error-text')) err.remove();
}

function validateForm() {
    const type = creditTypeSelect.value;
    const { minRate, maxAmount, maxTerm } = limits[type] || limits.PERSONAL;

    const principalEl = document.getElementById('principalAmount');
    const rateEl = document.getElementById('interestRateAnnual');
    const termEl = document.getElementById('termMonths');

    let ok = true;
    const principal = parseFloat(principalEl.value);
    if (isNaN(principal) || principal <= 0 || principal > maxAmount) {
        ok = false; showError(principalEl, `Сумма должна быть > 0 и ≤ ${maxAmount.toLocaleString()}`);
    } else { clearError(principalEl); }

    const rate = parseFloat(rateEl.value);
    if (isNaN(rate) || rate < minRate) {
        ok = false; showError(rateEl, `Ставка должна быть ≥ ${minRate}%`);
    } else { clearError(rateEl); }

    const term = parseInt(termEl.value, 10);
    if (isNaN(term) || term < 1 || term > maxTerm) {
        ok = false; showError(termEl, `Срок должен быть от 1 до ${maxTerm} мес.`);
    } else { clearError(termEl); }

    return ok;
}

async function loadCredits() {
    try {
        const credits = await creditApi.getAllCredits();
        renderCredits(credits);
    } catch (error) {
        console.error('Error loading credits:', error);
    }
}

function renderCredits(credits) {
    const tbody = document.querySelector('tbody');
    if (!tbody) return;
    tbody.innerHTML = credits.map(credit => `
        <tr>
            <td>${credit.id}</td>
            <td>${credit.principalAmount}</td>
            <td>${credit.currency}</td>
            <td>${credit.interestRateAnnual}%</td>
            <td>${credit.termMonths}</td>
            <td>${credit.status}</td>
            <td>${new Date(credit.createdAt).toLocaleDateString()}</td>
            <td>
                <button class="close-btn btn btn--danger" data-id="${credit.id}">Закрыть</button>
            </td>
        </tr>
    `).join('');
}
