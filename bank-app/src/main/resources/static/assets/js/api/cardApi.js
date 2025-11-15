import {httpClient} from '../core/httpClient.js';

export const cardApi = {
    myCards: () => httpClient.get('/cards').then(list => list.map(c => ({
        ...c,
        maskedCardNumber: c.maskedCardNumber || '**** **** **** ' + c.cardNumber.slice(-4)
    }))),
    getCard: (id) => httpClient.get(`/cards/${id}`)
};
