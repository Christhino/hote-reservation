import { useEffect, useState } from 'react';
import { api } from '../api.js';

const empty = { numChambre: '', dateEntree: '', nbrJour: 1, nomClient: '', mail: '' };

export default function Reservations() {
  const [reservations, setReservations] = useState([]);
  const [chambres, setChambres] = useState([]);
  const [form, setForm] = useState(empty);
  const [msg, setMsg] = useState(null);

  const [searchClient, setSearchClient] = useState('');
  const [filterStatut, setFilterStatut] = useState('TOUS');
  const [filterDateDebut, setFilterDateDebut] = useState('');
  const [filterDateFin, setFilterDateFin] = useState('');

  const load = () => {
    api.getReservations().then(setReservations).catch(err => setMsg({ type: 'error', text: err.message }));
    api.getChambres().then(setChambres);
  };

  useEffect(() => { load(); }, []);

  const handleChange = (e) => setForm({ ...form, [e.target.name]: e.target.value });

  const handleSubmit = async (e) => {
    e.preventDefault();
    try {
      const payload = { ...form, nbrJour: Number(form.nbrJour) };
      await api.createReservation(payload);
      setMsg({ type: 'success', text: 'Réservation créée. Un email de confirmation a été envoyé au client.' });
      setForm(empty);
      load();
    } catch (err) {
      setMsg({ type: 'error', text: err.message });
    }
  };

  const handleAnnuler = async (id) => {
    if (!confirm('Annuler cette réservation ? La chambre redeviendra libre.')) return;
    try {
      await api.annulerReservation(id);
      load();
    } catch (err) {
      setMsg({ type: 'error', text: err.message });
    }
  };

  const handleDelete = async (id) => {
    if (!confirm('Supprimer définitivement cette réservation ?')) return;
    try {
      await api.deleteReservation(id);
      load();
    } catch (err) {
      setMsg({ type: 'error', text: err.message });
    }
  };

  const term = searchClient.trim().toLowerCase();

  const reservationsFiltrees = reservations.filter(r => {
    // Filtre par nom du client
    if (term !== '' && !r.nomClient.toLowerCase().includes(term)) return false;

    // Filtre par statut
    if (filterStatut !== 'TOUS' && r.statut !== filterStatut) return false;

    // Filtre par période (date d'entrée / date de sortie calculée)
    const dateSortie = new Date(r.dateEntree);
    dateSortie.setDate(dateSortie.getDate() + r.nbrJour);

    if (filterDateDebut && new Date(r.dateEntree) < new Date(filterDateDebut)) return false;
    if (filterDateFin && dateSortie > new Date(filterDateFin)) return false;

    return true;
  });

  return (
    <div>
      <h2>Réservations</h2>
      {msg && <div className={`msg ${msg.type}`}>{msg.text}</div>}

      <div className="card">
        <form className="inline-form" onSubmit={handleSubmit}>
          <div className="field">
            <label>Chambre</label>
            <select name="numChambre" value={form.numChambre} onChange={handleChange} required>
              <option value="">-- choisir --</option>
              {chambres.map(c => <option key={c.numChambre} value={c.numChambre}>{c.numChambre} - {c.design}</option>)}
            </select>
          </div>
          <div className="field">
            <label>Date d'entrée</label>
            <input type="date" name="dateEntree" value={form.dateEntree} onChange={handleChange} required />
          </div>
          <div className="field">
            <label>Nombre de jours</label>
            <input type="number" min="1" name="nbrJour" value={form.nbrJour} onChange={handleChange} required />
          </div>
          <div className="field">
            <label>Nom du client</label>
            <input name="nomClient" value={form.nomClient} onChange={handleChange} required />
          </div>
          <div className="field">
            <label>Email</label>
            <input type="email" name="mail" value={form.mail} onChange={handleChange} required />
          </div>
          <button type="submit">Réserver</button>
        </form>
      </div>

      <div className="card">
        <form className="inline-form" style={{ marginBottom: 16 }} onSubmit={e => e.preventDefault()}>
          <div className="field">
            <label>Rechercher par client</label>
            <input
              type="text"
              placeholder="Nom du client..."
              value={searchClient}
              onChange={e => setSearchClient(e.target.value)}
            />
          </div>
          <div className="field">
            <label>Statut</label>
            <select value={filterStatut} onChange={e => setFilterStatut(e.target.value)}>
              <option value="TOUS">Tous</option>
              <option value="ACTIVE">ACTIVE</option>
              <option value="OCCUPEE">OCCUPEE</option>
              <option value="ANNULEE">ANNULEE</option>
            </select>
          </div>
          <div className="field">
            <label>Date d'entrée (à partir de)</label>
            <input
              type="date"
              value={filterDateDebut}
              onChange={e => setFilterDateDebut(e.target.value)}
            />
          </div>
          <div className="field">
            <label>Date de sortie (jusqu'à)</label>
            <input
              type="date"
              value={filterDateFin}
              onChange={e => setFilterDateFin(e.target.value)}
            />
          </div>
          {(searchClient || filterStatut !== 'TOUS' || filterDateDebut || filterDateFin) && (
            <button
              type="button"
              className="secondary"
              onClick={() => {
                setSearchClient('');
                setFilterStatut('TOUS');
                setFilterDateDebut('');
                setFilterDateFin('');
              }}
            >
              Réinitialiser
            </button>
          )}
        </form>

        <table>
          <thead>
            <tr>
              <th>ID</th><th>Chambre</th><th>Date réserv.</th><th>Date entrée</th>
              <th>Jours</th><th>Client</th><th>Email</th><th>Statut</th><th>Actions</th>
            </tr>
          </thead>
          <tbody>
            {reservationsFiltrees.map(r => (
              <tr key={r.idReserv}>
                <td>{r.idReserv}</td>
                <td>{r.numChambre}</td>
                <td>{r.dateReserv}</td>
                <td>{r.dateEntree}</td>
                <td>{r.nbrJour}</td>
                <td>{r.nomClient}</td>
                <td>{r.mail}</td>
                <td><span className={`badge ${r.statut}`}>{r.statut}</span></td>
                <td className="actions">
                  {r.statut === 'ACTIVE' && (
                    <button className="danger" onClick={() => handleAnnuler(r.idReserv)}>Annuler</button>
                  )}
                  <button className="secondary" onClick={() => handleDelete(r.idReserv)}>Supprimer</button>
                </td>
              </tr>
            ))}
            {reservationsFiltrees.length === 0 && (
              <tr>
                <td colSpan="9">
                  {reservations.length === 0 ? 'Aucune réservation.' : 'Aucun résultat pour ces filtres.'}
                </td>
              </tr>
            )}
          </tbody>
        </table>
      </div>
    </div>
  );
}