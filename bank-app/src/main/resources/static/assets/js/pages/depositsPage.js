import {logout} from '../core/auth.js';
import {bindLogout} from '../core/ui.js';

bindLogout('logoutBtn', logout);

document.addEventListener('click', (e) => {
    if (e.target.classList.contains('close-btn')) {
        const id = e.target.dataset.id;
        if (confirm('Вы уверены, что хотите закрыть депозит?')) {
            closeDeposit(id);
        }
    }
});

async function closeDeposit(id) {
    try {
        const response = await fetch(`/api/deposits/${id}/close`, {
            method: 'POST',
            headers: {
                'Authorization': `Bearer ${localStorage.getItem('jwtToken') || ''}`
            }
        });
        if (response.ok) {
            alert('Депозит закрыт!');
            location.reload();
        } else {
            alert('Ошибка: ' + response.status);
        }
    } catch (err) {
        alert('Ошибка: ' + err.message);
    }
}
