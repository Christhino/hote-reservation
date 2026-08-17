import { useEffect, useState } from 'react';
import { api } from '../api.js';

const empty = { numChambre: '', design: '', type: 'Simple', prixNuite: '' };

export default function Chambres() {
  const [chambres, setChambres] = useState([]);
  const [form, setForm] = useState(empty);
  const [editing, setEditing] = useState(false);
  const [msg, setMsg] = useState(null);
  const [search, setSearch] = useState('');

  const load = () => api.getChambres().then(setChambres).catch(err => setMsg({ type: 'error', text: err.message }));

  useEffect(() => { load(); }, []);

  const handleChange = (e) => setForm({ ...form, [e.target.name]: e.target.value });

  const handleSubmit = async (e) => {
    e.preventDefault();
    try {
      const payload = { ...form, prixNuite: Number(form.prixNuite) };
      if (editing) {
        await api.updateChambre(form.numChambre, payload);
        setMsg({ type: 'success', text: 'Chambre modifiée.' });
      } else {
        await api.createChambre(payload);
        setMsg({ type: 'success', text: 'Chambre créée.' });
      }
      setForm(empty);
      setEditing(false);
      load();
    } catch (err) {
      setMsg({ type: 'error', text: err.message });
    }
  };

  const handleEdit = (ch) => {
    setForm(ch);
    setEditing(true);
  };

  const handleDelete = async (numChambre) => {
    if (!confirm(`Supprimer la chambre ${numChambre} ?`)) return;
    try {
      await api.deleteChambre(numChambre);
      load();
    } catch (err) {
      setMsg({ type: 'error', text: err.message });
    }
  };

  const term = search.trim().toLowerCase();
  const chambresFiltrees = term === ''
    ? chambres
    : chambres.filter(ch =>
        ch.numChambre.toLowerCase().includes(term) ||
        ch.design.toLowerCase().includes(term)
      );

  return (
    <div>
      <h2>Gestion des chambres</h2>
      {msg && <div className={`msg ${msg.type}`}>{msg.text}</div>}

      <div className="card">
        <form className="inline-form" onSubmit={handleSubmit}>
          <div className="field">
            <label>N° Chambre</label>
            <input name="numChambre" value={form.numChambre} onChange={handleChange}
                   disabled={editing} required />
          </div>
          <div className="field">
            <label>Désignation</label>
            <input name="design" value={form.design} onChange={handleChange} required />
          </div>
          <div className="field">
            <label>Type</label>
            <select name="type" value={form.type} onChange={handleChange}>
              <option>Simple</option>
              <option>Double</option>
              <option>Suite</option>
              <option>Familiale</option>
            </select>
          </div>
          <div className="field">
            <label>Prix / nuitée</label>
            <input type="number" name="prixNuite" value={form.prixNuite} onChange={handleChange} required />
          </div>
          <button type="submit">{editing ? 'Modifier' : 'Ajouter'}</button>
          {editing && (
            <button type="button" className="secondary" onClick={() => { setForm(empty); setEditing(false); }}>
              Annuler
            </button>
          )}
        </form>
      </div>

      <div className="card">
        <div className="field" style={{ marginBottom: 14, maxWidth: 320 }}>
          <label>Rechercher (n° chambre ou désignation)</label>
          <input
            type="text"
            placeholder="Ex : 102 ou Suite..."
            value={search}
            onChange={e => setSearch(e.target.value)}
          />
        </div>

        <table>
          <thead>
            <tr><th>N° Chambre</th><th>Désignation</th><th>Type</th><th>Prix/nuitée</th><th>Actions</th></tr>
          </thead>
          <tbody>
            {chambresFiltrees.map(ch => (
              <tr key={ch.numChambre}>
                <td>{ch.numChambre}</td>
                <td>{ch.design}</td>
                <td>{ch.type}</td>
                <td>{ch.prixNuite} Ar</td>
                <td className="actions">
                  <button onClick={() => handleEdit(ch)}>Modifier</button>
                  <button className="danger" onClick={() => handleDelete(ch.numChambre)}>Supprimer</button>
                </td>
              </tr>
            ))}
            {chambresFiltrees.length === 0 && (
              <tr><td colSpan="5">{chambres.length === 0 ? 'Aucune chambre enregistrée.' : 'Aucun résultat pour cette recherche.'}</td></tr>
            )}
          </tbody>
        </table>
      </div>
    </div>
  );
}