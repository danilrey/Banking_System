import {httpClient} from '../core/httpClient.js';

export const bonusApi = {
    getBalance: () => httpClient.get('/bonuses/account'),
    addBonus: (category) => httpClient.post(`/bonuses/add/${category}`),
    applyCashback: (data) => httpClient.post('/bonuses/apply', data)
};
