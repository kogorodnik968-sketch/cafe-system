import { fetchUtils } from 'react-admin';

const apiUrl = '${process.env.REACT_APP_API_URL}';
const httpClient = fetchUtils.fetchJson;

export default {
    getList: async (resource) => {
        const url = `${apiUrl}/${resource}`;
        const { json } = await httpClient(url);
        return {
            data: json,
            total: json.length,
        };
    },

    getOne: async (resource, { id }) => {
        const url = `${apiUrl}/${resource}/${id}`;
        const { json } = await httpClient(url);
        return { data: json };
    },

    create: async (resource, { data }) => {
        const url = `${apiUrl}/${resource}`;
        const { json } = await httpClient(url, {
            method: 'POST',
            body: JSON.stringify(data),
            headers: new Headers({ 'Content-Type': 'application/json' }),
        });
        return { data: json };
    },

    update: async (resource, { id, data }) => {
        const url = `${apiUrl}/${resource}/${id}`;
        const { json } = await httpClient(url, {
            method: 'PUT',
            body: JSON.stringify(data),
            headers: new Headers({ 'Content-Type': 'application/json' }),
        });
        return { data: json };
    },

    delete: async (resource, { id }) => {
        const url = `${apiUrl}/${resource}/${id}`;
        const { json } = await httpClient(url, { method: 'DELETE' });
        return { data: json };
    },
};