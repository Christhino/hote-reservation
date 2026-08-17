import { useEffect, useState } from 'react';
import { api } from '../api.js';

const empty = { numChambre: '', nbrJour: 1, nomClient: '', telephone: '' };

export default function Sejours() {
  const [sejours, setSejours] = useState([]);
  const [chambres, setChambres] = useState([]);
  const [form, setForm] = useState(empty);
  const [msg, setMsg] = useState(null);

  const load = () => {
    api.getSejours().then(setSejours).catch(err => setMsg({ type: 'error', text: err.message }));
    api.getChambres().then(setChambres);
  };

  useEffect(() => { load(); }, []);

  const handleChange = (e) => setForm({ ...form, [e.target.name]: e.target.value });

  const handleSubmit = async (e) => {
    e.preventDefault();
    try {
      const payload = { ...form, nbrJour: Number(form.nbrJour) };
      await api.createSejour(payload);
      setMsg({ type: 'success', text: 'Séjour enregistré. Le solde a été mis à jour.' });
      setForm(empty);
      load();
    } catch (err) {
      setMsg({ type: 'error', text: err.message });
    }
  };

  const handleDelete = async (id) => {
    if (!confirm('Supprimer ce séjour ?')) return;
    try {
      await api.deleteSejour(id);
      load();
    } catch (err) {
      setMsg({ type: 'error', text: err.message });
    }
  };

  return (
    <div>
      <h2>Séjours (clients sans réservation préalable)</h2>
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
            <label>Nombre de jours</label>
            <input type="number" min="1" name="nbrJour" value={form.nbrJour} onChange={handleChange} required />
          </div>
          <div className="field">
            <label>Nom du client</label>
            <input name="nomClient" value={form.nomClient} onChange={handleChange} required />
          </div>
          <div className="field">
            <label>Téléphone</label>
            <input name="telephone" value={form.telephone} onChange={handleChange} required />
          </div>
          <button type="submit">Enregistrer le séjour</button>
        </form>
      </div>

      <div className="card">
        <table>
          <thead>
            <tr><th>ID</th><th>Chambre</th><th>Date entrée</th><th>Jours</th><th>Client</th><th>Téléphone</th><th>Actions</th></tr>
          </thead>
          <tbody>
            {sejours.map(s => (
              <tr key={s.idSejour}>
                <td>{s.idSejour}</td>
                <td>{s.numChambre}</td>
                <td>{s.dateEntreeSejour}</td>
                <td>{s.nbrJour}</td>
                <td>{s.nomClient}</td>
                <td>{s.telephone}</td>
                <td className="actions">
                  <a href={api.getRecuUrl(s.idSejour)} target="_blank" rel="noreferrer">
                    <button className="success" type="button">Reçu PDF</button>
                  </a>
                  <button className="danger" onClick={() => handleDelete(s.idSejour)}>Supprimer</button>
                </td>
              </tr>
            ))}
            {sejours.length === 0 && (
              <tr><td colSpan="7">Aucun séjour enregistré.</td></tr>
            )}
          </tbody>
        </table>
      </div>
    </div>
  );
}
