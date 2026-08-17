const BASE_URL = '/api';

async function request(path, options = {}) {
  const res = await fetch(`${BASE_URL}${path}`, {
    headers: { 'Content-Type': 'application/json' },
    ...options
  });
  if (res.status === 204) return null;
  const data = await res.json().catch(() => null);
  if (!res.ok) {
    throw new Error(data?.error || `Erreur HTTP ${res.status}`);
  }
  return data;
}

export const api = {
  // Chambres
  getChambres: () => request('/chambres'),
  getChambre: (id) => request(`/chambres/${id}`),
  createChambre: (data) => request('/chambres', { method: 'POST', body: JSON.stringify(data) }),
  updateChambre: (id, data) => request(`/chambres/${id}`, { method: 'PUT', body: JSON.stringify(data) }),
  deleteChambre: (id) => request(`/chambres/${id}`, { method: 'DELETE' }),
  getChambresDisponibles: (dateEntree, nbrJour) =>
    request(`/chambres/disponibles?dateEntree=${dateEntree}&nbrJour=${nbrJour}`),

  // Réservations
  getReservations: () => request('/reservations'),
  createReservation: (data) => request('/reservations', { method: 'POST', body: JSON.stringify(data) }),
  updateReservation: (id, data) => request(`/reservations/${id}`, { method: 'PUT', body: JSON.stringify(data) }),
  annulerReservation: (id) => request(`/reservations/${id}/annuler`, { method: 'PUT' }),
  deleteReservation: (id) => request(`/reservations/${id}`, { method: 'DELETE' }),

  // Occupations
  getOccupations: () => request('/occupations'),
  createOccupation: (idReserv) => request('/occupations', { method: 'POST', body: JSON.stringify({ idReserv }) }),
  deleteOccupation: (id) => request(`/occupations/${id}`, { method: 'DELETE' }),

  // Séjours
  getSejours: () => request('/sejours'),
  createSejour: (data) => request('/sejours', { method: 'POST', body: JSON.stringify(data) }),
  updateSejour: (id, data) => request(`/sejours/${id}`, { method: 'PUT', body: JSON.stringify(data) }),
  deleteSejour: (id) => request(`/sejours/${id}`, { method: 'DELETE' }),
  getRecuUrl: (id) => `${BASE_URL}/sejours/${id}/recu`,

  // Solde
  getSolde: () => request('/solde')
};
