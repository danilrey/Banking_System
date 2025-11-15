import {httpClient} from '../core/httpClient.js';

export const creditApi = {
    getAllCredits: () => httpClient.get('/credits'),
    createCredit: (data) => {
        console.log('Sending credit data:', data);
        return httpClient.post('/credits', data);
    },
    closeCredit: (id) => httpClient.post(`/credits/${id}/close`)
};
