package cryveck;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map.Entry;
//renvoie des graphes particuliers
//taille pour grapheAztec(n) : 4n^2, grapheFeuille ou grapheNeighbour, avalanche, n^2
//stabsuivi taille+1
public class Graphes {

	public static Hashtable<Integer, ArrayList<Integer>> grapheAztec(int n) {
		int c = 4 * n * n;
		Hashtable<Integer, ArrayList<Integer>> g = new Hashtable<Integer, ArrayList<Integer>>();
		boolean[] L = new boolean[c + 1];
		for (int i = 0; i < n; i++)
			for (int j = i * 2 * n + n - i; j < i * 2 * n + 2 * n + i - n + 2; j++) {
				L[j] = true;
				L[c - j + 1] = true;
			}
		for (int i = 1; i < c + 1; i++) {
			g.put(i, new ArrayList<Integer>());
			if (L[i]) {
				cond(i, i + 1, L, g);
				cond(i, i - 1, L, g);
				if (i + 2 * n < c + 1) {
					cond(i, i + 2 * n, L, g);
				} else {
					g.get(i).add(0);
				}
				if (i - 2 * n >= 0) {
					cond(i, i - 2 * n, L, g);
				} else {
					g.get(i).add(0);
				}
			} else {
				g.get(i).add(0);
			}
		}
		for (int i = 1; i < c +1; i++) {
			while (g.get(i).size() < 4) {
				g.get(i).add(0);
			}
		}
		return g;
	}

	public static void cond(int index, int verif, boolean[] L, Hashtable<Integer, ArrayList<Integer>> g) {
		if (L[verif])
			g.get(index).add(verif);
		else
			g.get(index).add(0);
	}
	
	public static Hashtable<Integer, ArrayList<Integer>> graphe3NeighbourDN(int n, int m) {
		Hashtable<Integer, ArrayList<Integer>> g = new Hashtable<Integer, ArrayList<Integer>>();
		for (int i = 0; i < m; i++) {
			g.put(n* i +1,  toTab(n*(i+1)+1, n*(i+1) + n, n*(i+1)+2));
			for (int j = 2; j < n+1; j++)
				g.put(n * i + j, toTab(n * (i + 1) + j, n * (i + 1) + ((j - 2) % n) + 1, n * (i + 1) + ((j) % n + 1)));
		}
		for (int i = n * (m - 1)+1; i < n * m+1; i++)
			g.put(i, toTab(0, 0, 0));
		return g;
	}
	
	public static Hashtable<Integer, ArrayList<Integer>> graphe2NeighbourDN(int n, int m) {
		Hashtable<Integer, ArrayList<Integer>> g = new Hashtable<Integer, ArrayList<Integer>>();
		for (int i = 0; i < m; i++) {
			g.put(n* i +1,  toTab(n*(i+1) + n, n*(i+1)+2));
			for (int j = 2; j < n+1; j++)
				g.put(n * i + j, toTab(n * (i + 1) + ((j - 2) % n) + 1, n * (i + 1) + ((j) % n + 1)));
		}
		for (int i = n * (m - 1)+1; i < n * m+1; i++)
			g.put(i, toTab(0, 0));
		return g;
	}
	
	public static Hashtable<Integer, ArrayList<Integer>> graphe3NeighbourDNBIS(int n, int m) {
		Hashtable<Integer, ArrayList<Integer>> g = new Hashtable<Integer, ArrayList<Integer>>();
		for (int i = 0; i < m; i++) {
			g.put(n* i +1,  toTab(n*(i+1)+1, n*(i+1)+1, n*(i+1) + n, n*(i+1)+2));
			for (int j = 2; j < n+1; j++)
				g.put(n * i + j, toTab(n * (i + 1) + j,n * (i + 1) + j, n * (i + 1) + ((j - 2) % n) + 1, n * (i + 1) + ((j) % n + 1)));
		}
		for (int i = n * (m - 1)+1; i < n * m+1; i++)
			g.put(i, toTab(0, 0, 0, 0));
		return g;
	}

	public static int[] coordonnees(int n, int sn) {
		return new int[] {(int)(n-1)/sn+1, (n-1)%sn+1};
	}

	public static Hashtable<Integer, ArrayList<Integer>> grapheFeuille(int n) {
		int c = n * n;
		Hashtable<Integer, ArrayList<Integer>> g = new Hashtable<Integer, ArrayList<Integer>>();
		g.put(c, toTab(0, 0, c - 1, c - n));
		g.put(c - n + 1, toTab(0, 0, c - n + 2, c - 2 * n + 1));
		g.put(1, toTab(0, 0, 2, n + 1));
		g.put(n, toTab(0, 0, n - 1, 2 * n));
		g.put(n - 1, toTab(0, n - 2, n, 2 * n - 1));
		g.put(c - n + 2, toTab(0, c - n + 1, c - n + 3, c - 2 * n + 2));
		for (int k = 1; k < n - 1; k++) {
			if (k >= 2) {
				g.put(k, toTab(0, k - 1, k + 1, k + n));
				g.put(c - k + 1, toTab(0, c - k + 2, c - k, c - k - n + 1));
			}
			g.put(k * n + 1, toTab(0, (k - 1) * n + 1, (k + 1) * n + 1, k * n + 2));
			g.put((k + 1) * n, toTab(0, k * n, (k + 2) * n, (k + 1) * n - 1));
			for (int l = 2; l < n; l++)
				g.put(k * n + l, toTab((k - 1) * n + l, (k + 1) * n + l, k * n + l - 1, k * n + l + 1));
		}
		return g;
	}
	
	//construit la propriete boolenne (tableau de booleens indexe par i) a partir d'une condition sur les sites i 
	//puis utilise MasqueGeometrique
	public static Hashtable<Integer, ArrayList<Integer>> grapheConditionnel (Hashtable<Integer, ArrayList<Integer>> g, Condition condition) {
		int c = g.size();
		int sn = (int) Math.sqrt(c);
		boolean[] L = new boolean[c+1];
		for(int i = 1; i<L.length; i++) {
			int[] cp = coordonnees(i, sn);
			if (condition.cond(cp, sn))//La condition que l'on met en parametre est utilisee ici
				L[i] = true;
		}
		return masqueGeometrique(g, L);
	}

	public static Hashtable<Integer, ArrayList<Integer>> grapheCercle(int l, Hashtable<Integer, ArrayList<Integer>> g, double r) {
		return grapheConditionnel(g,
				(int[] cp, int sn) -> Math.pow(Math.abs(cp[1]-(sn)/2-1/2),r)+Math.pow(Math.abs(cp[0]-(sn)/2-1/2),  r)<=Math.pow(l, r));
	}
	
	public static Hashtable<Integer, ArrayList<Integer>> grapheDiagonale(int l, Hashtable<Integer, ArrayList<Integer>> g) {
		return grapheConditionnel(g,
				(int[] cp, int sn) -> Math.abs((-cp[1]+cp[0]))<=l);
	}
	
	public static Hashtable<Integer, ArrayList<Integer>> grapheTriangle(Hashtable<Integer, ArrayList<Integer>> g) {
		return grapheConditionnel(g,
				(int[] cp, int sn) -> Math.abs((cp[0]))<=cp[1]);
	}
	
	public static Hashtable<Integer, ArrayList<Integer>> grapheHyperbolique(int l, Hashtable<Integer, ArrayList<Integer>> g) {		
		return grapheConditionnel(g,
				(int[] cp, int sn) -> cp[0]*cp[1]<=l*sn);
	}
	
	public static Hashtable<Integer, ArrayList<Integer>> grapheRectangulaire(int gauche, int droite, Hashtable<Integer, ArrayList<Integer>> g) {		
		return grapheConditionnel(g,
				(int[] cp, int sn) -> cp[1]>=gauche & cp[1]<=droite);
	}
	
	//prend un graphe g et une proprete booleenne L, renvoie un graphe g' ou les sites i ont les memes proprietes sur g si L(i) est vraie
	// sinon i est deconnecte de tout ses voisins
	public static Hashtable<Integer, ArrayList<Integer>> masqueGeometrique(Hashtable<Integer, ArrayList<Integer>> g, boolean[] L) {
		for(Iterator<Entry<Integer, ArrayList<Integer>>> it = g.entrySet().iterator(); it.hasNext(); ) {
		    Entry<Integer, ArrayList<Integer>> entry = it.next();
			if (L[entry.getKey()]) {
				ArrayList<Integer> values = entry.getValue();
				for (int i = 0; i < values.size(); i++)
					if (!L[values.get(i)]) 
						values.set(i, 0);
			} else {
				//it.remove();
				ArrayList<Integer> values = entry.getValue();
				for (int i = 0; i < values.size(); i++)
					values.set(i,  0);
			}
		}
		return g;
	}
	
	public static Hashtable<Integer, ArrayList<Integer>> grapheAvalancheOriente(int profondeur, int n) {
		Hashtable<Integer, ArrayList<Integer>> g = new Hashtable<Integer, ArrayList<Integer>>();
		int dn = n / 2;
		g.put(dn, toTab(0, dn - 1, dn + 1, dn + n));
		g.put(n * (n - 1) + dn, toTab(0, n * (n - 1) + dn - 1, n * (n - 1) + dn + 1, n * (n - 2) + dn));
		for (int i = 0; i < n - 2; i++) {
			g.put(i * n + dn, toTab(Math.max(0, (i - 1) * n + dn), (i + 1) * n + dn, i * n + dn + 1, i * n + dn - 1));
			for (int l = 1; l < profondeur; l++) {
				if ((i - 1) * n + dn > 0)
					g.put(i * n + dn + l, toTab(0, 0, i * n + dn - l, i * n + dn + l));
			}
		}
		for (int i = 1; i < n * n + 1; i++) {
			if (!g.containsKey(i))
				g.put(i, toTab(0, 0, 0, 0));
		}
		return g;
	}

	public static ArrayList<Integer> toTab(Integer... values) {
		ArrayList<Integer> l = new ArrayList<Integer>();
		l.addAll(Arrays.asList(values));
		return l;
	}
	
}

interface Condition {
	public boolean cond(int[] cp, int sn);
}
