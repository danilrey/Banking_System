import {httpClient} from '../core/httpClient.js';

export const cardApi = {
    myCards: () => httpClient.get('/api/cards').then(list => list.map(c => ({
        id: c.id,
        cardNumber: c.maskedCardNumber,
        expiryMonth: c.expiryMonth,
        expiryYear: c.expiryYear,
        status: c.status
    })))
};
