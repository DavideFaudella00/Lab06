package it.polito.tdp.meteo.model;

import java.util.ArrayList;
import java.util.List;

import it.polito.tdp.meteo.DAO.MeteoDAO;

public class Model {
	MeteoDAO dao = new MeteoDAO();
	private List<Citta> best;
	List<Citta> cit = dao.getAllLocalita();
	double bestCosto = 0.0;
	private final static int COST = 100;
	private final static int NUMERO_GIORNI_CITTA_CONSECUTIVI_MIN = 3;
	private final static int NUMERO_GIORNI_CITTA_MAX = 6;
	private final static int NUMERO_GIORNI_TOTALI = 15;

	public Model() {
	}

	public String getUmiditaMedia(int mese) {
		List<Media> lista = dao.getUmiditaMedia(mese);
		String s = "";
		for (Media m : lista) {
			s += m.toString() + "\n";
		}
		return s;
	}

	public List<Citta> trovaSequenza(int mese) {
		List<Citta> parziale = new ArrayList<>();
		this.best = null;
		for (Citta c : cit) {
			c.setRilevamenti(dao.getAllRilevamentiLocalitaMese(mese, c.getNome()));
		}
		cerca(parziale, 0);

		return best;
	}

	public void cerca(List<Citta> parziale, int livello) {
		if (livello == NUMERO_GIORNI_TOTALI) {
			if (best == null || calcolaCosto(parziale) < calcolaCosto(best)) {
				best = new ArrayList<>(parziale);
				bestCosto = calcolaCosto(parziale);
			}
		} else {
			for (Citta c : cit) {
				if (isValid(parziale, c)) {
					parziale.add(c);
					cerca(parziale, livello + 1);
					parziale.remove(parziale.size() - 1);
				}
			}
		}
	}

	private double calcolaCosto(List<Citta> parziale) {
		double costo = 0.0;
		int i = 0;
		for (Citta c : parziale) {
			costo += c.getRilevamenti().get(i++).getUmidita();
			if (c.getNome().equals(parziale.get(i - 1).getNome()) == false) {
				costo += COST;
			}
		}
		return costo;
	}

	private boolean isValid(List<Citta> parziale, Citta prova) {
		//verifica giorni massimi
		//contiamo quante volte la città 'prova' era già apparsa nell'attuale lista costruita fin qui
		int conta = 0;
		for (Citta c : parziale) {
			if (c.equals(prova)) {
				conta++;
			}
		}
		//se supero giorni max sono out
		if (conta >= NUMERO_GIORNI_CITTA_MAX) {
			return false;
		}
		// verifica dei giorni minimi
		if (parziale.size() == 0) { //primo giorno posso inserire qualsiasi città
			return true;
		}
		if (parziale.size() == 1 || parziale.size() == 2) {
			//siamo al secondo o terzo giorno, non posso cambiare
			//quindi l'aggiunta è valida solo se la città di prova coincide con la sua precedente
			return parziale.get(parziale.size() - 1).equals(prova);
		}
		//nel caso generale, se ho già passato i controlli sopra, non c'è nulla che mi vieta di rimanere nella stessa città
		//quindi per i giorni successivi ai primi tre posso sempre rimanere
		if (parziale.get(parziale.size() - 1).equals(prova)) {
			return true;
		}
		// se cambio città mi devo assicurare che nei tre giorni precedenti sono rimasto fermo 
		if (parziale.get(parziale.size() - 1).equals(parziale.get(parziale.size() - 2))
				&& parziale.get(parziale.size() - 2).equals(parziale.get(parziale.size() - 3))) {
			return true;
		}
		return false;
	}

	public double getBestCosto() {
		return bestCosto;
	}

}
