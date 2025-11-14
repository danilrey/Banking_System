import {httpClient} from '../core/httpClient.js';

export const depositApi = {
    myDeposits: () => httpClient.get('/api/deposits')
};
