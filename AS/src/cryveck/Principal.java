package cryveck;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Random;
import java.util.Scanner;
import java.util.Set;
public class Principal {
	//prefixe pour le titre des images de rendu
	public static final String PREFIXE = "avalancheXS"; 
	public static final int MAX_SIZE_LIST_IMAGE_BEFORE_PROCESS = 10;
	public static final int SAVE_IMAGE_PIXEL_SIZE = 1;
	public static final ColorProcess cp = (double param) -> param;// (double param) -> Math.pow(param,
	// 0.2) ; (double param) -> param
	// renvoie Id (i.e la fonction
	// getColor)
	public static final boolean DEMANDE_GO = false; 
	public static final int N = 50;
	public static final int M = 400;

	public static Rendu rendu = new Rendu(MAX_SIZE_LIST_IMAGE_BEFORE_PROCESS, SAVE_IMAGE_PIXEL_SIZE, cp);

	public static void main(String[] args) {
		Scanner sc;
		if (DEMANDE_GO) {
			sc = new Scanner(System.in);
			while (!sc.nextLine().equals("go"))
				;
		}
		long t0 = System.currentTimeMillis();
		run();
		System.out.println("Temps total d'execution : " + (System.currentTimeMillis() - t0));
		if (DEMANDE_GO)
			sc.close();
	}

	public static void run() {

		rendu.setColorRange(3, 0);// Definition de l'intervalle pour le calcul des couleurs, a choisir plus grand que le flux sortant max
		
		Hashtable<Integer, Integer> resultat = histogramme3(10000, M,N);
		Set<Integer> keys = resultat.keySet();
		for (int j : keys) {
			System.out.print("{"+ j + ", "+resultat.get(j)+"/10000}, ");			//rendu pour mathematica
		}
		
		//float[] f = fluxcarremoyen3neighbours(10000, 100, 1000);
		//for (int i = 0; i<=1000; i++) {
		//	System.out.print("{" + i + ", "+f[i]+"}, ");
		//}

		//for (int n = 270; n<=300; n+=30) {
		//	long t0 = System.currentTimeMillis();
		//	calculIdentiteDict(Graphes.grapheFeuille(n), false, false);
		//	System.out.println("Temps total d'execution : " + (System.currentTimeMillis() - t0));
		//}
		
		//Calcul et rendu des identites sur graphecercle pour r variant entre 0.5 et 3
		//for (int k=0; k<6; k++) {
		//	Hashtable <Integer, ArrayList<Integer>> gP = Graphes.grapheCercle(N/2-1, Graphes.grapheFeuille(N), (k+1)*0.5);
		//	int[] configuration = calculIdentiteDict(gP, false, false);
		//	rendu.save(PREFIXE + "--" + String.format("%03d", k), configuration, N, N);
		//}
		
		//Calcul et rendu de la matrice sur un graphe quelconque
		//Hashtable <Integer, ArrayList<Integer>> gP = Graphes.grapheFeuille(N); //Definition du graphe
		//int[] configuration = calculIdentiteDict(gP, false, false); //premier booleen : suivi, second : regularite
		//rendu.save(PREFIXE + "--" + String.format("%03d",  1), configuration, 2*N, 2*N);

		//Hashtable <Integer, ArrayList<Integer>> gP = Graphes.grapheHyperbolique(10,Graphes.grapheFeuille(N));
		//int[] configuration = calculIdentiteDict(gP, false, false);
		//rendu.save(PREFIXE + "--" + String.format("%03d", 0), configuration, N,N);
		
		//Calcul et rendu de la matrice identite sur le graphe grille
		//int[] configuration = calculIdentiteDictOGF(N, false);
		//rendu.save(PREFIXE + "--" + String.format("%03d", 0), configuration, N, N);

		//Hashtable <Integer, ArrayList<Integer>> gP = Graphes.grapheFeuille(N);
		//int[] configuration = calculIdentiteDict(gP, false, true);
		//rendu.save(PREFIXE + "--" + String.format("%03d", 0), configuration, N, N);
	}

	// OGF : pour graphe feuille, i.e graphe regulier avec valeur sur la diagonale = -4, et 1 vers ses 4 voisins immediats
	// DN : pour un graphe quelconque regulier (diagonale = diag), et 1 vers ses diag voisins immediats
	// Suivi : renvoie des images a chaque stabilisation

	public static Hashtable<Integer, Integer> histogramme3(int Nbrdessais, int N, int M) {
		Hashtable <Integer, ArrayList<Integer>> gP = Graphes.graphe3NeighbourDN(N, M);
		Hashtable<Integer, Hashtable<Integer, Integer>> lap = laplacienneDict(gP, N*M+1);
		Hashtable<Integer, Integer> resultat = new Hashtable<Integer, Integer>();
		for (int i = 0; i<Nbrdessais; i++) {
			int configuration[] = configurationAleatoire(N*M+1, 2); //max
			configuration[N/2+1] = 3; //max + 1
			ArrayList<Integer> si = sitesInstablesDictDN(lap, configuration, 0, N, 3);
			int k = 0;
			while (si.size()>0 && k<M) {
				k+=1;
				unStabDict(gP, configuration, lap, true, 3, si);
				si = sitesInstablesDictDN(lap, configuration, k, N, 3);
			}
			if (resultat.get(k) == null) {
				resultat.put(k, 1);
			} else {
				resultat.put(k, resultat.get(k)+1);
			}
		}
		return resultat;
	}
	
	//renvoie un tableau 1xn d'entiers dans [1, max]
	public static int[] configurationAleatoire(int n, int max) { 
		int[] result = new int[n];
		Random rand = new Random();
		for (int i = 0; i < n; i++)
			result[i] = rand.nextInt(max + 1);
		return result;
	}

	//renvoie une configuration constante C
	public static int[] configurationComplete(int lg, int C) { 
		int[] configuration = new int[lg + 1];
		for (int i = 1; i < lg + 1; i++)
			configuration[i] = C;
		return configuration;
	}

	//renvoie la Laplacienne sous la forme d'une Hashtable (dictionnaire) (evite le stockage de 0.., tres utile en complexite spatiale)
	public static Hashtable<Integer, Hashtable<Integer, Integer>> laplacienneDict(
			Hashtable<Integer, ArrayList<Integer>> graphe, int n) { 
		Hashtable<Integer, Hashtable<Integer, Integer>> M = new Hashtable<Integer, Hashtable<Integer, Integer>>();
		for (int i = 0; i < n; i++)
			if (graphe.containsKey(i)) {
				if (M.get(i) == null)
					M.put(i, new Hashtable<Integer, Integer>());
				M.get(i).put(i, graphe.get(i).size());
				for (int j : graphe.get(i))
					if (M.get(i) == null) {
						M.put(i, new Hashtable<Integer, Integer>());
						M.get(i).put(i, -1);
					} else {
						if (M.get(i).containsKey(j))
							M.get(i).put(j, M.get(i).get(j) - 1);
						else
							M.get(i).put(j, -1);
					}
			}
		return M;
	}

	//verifie si le graphe encode par la Laplacienne du graphe sous la forme d'un dictionnaire est regulier : 
	// i.e si le flux de sortie non nul d'un voisin i est constant pour tout i (valeur diagonale de la Laplacienne) 
	// (on fait pas le test mais on suppose que la qte recue par chaque voisin = 1)
	public static boolean estReguliere(Hashtable<Integer, Hashtable<Integer, Integer>> lap) {//
		boolean reguliere = true;
		int v0 = lap.get(1).get(1);
		int i = 1;
		while (reguliere == true && i < lap.size()) {
			if (v0 != lap.get(i).get(i)) {
				reguliere = false;
			}
			i++;
		}
		return reguliere;
	}

	//calcul de l'identite sur un graphe feuille de taille nxn
	public static int[] calculIdentiteDictOGF(int n, boolean suivi) {
		Hashtable<Integer, ArrayList<Integer>> gP = Graphes.grapheFeuille(n);
		return calculIdentiteDict(gP, suivi, true);
	}

	//calcul de l'identite sur un graphe quelconque avec un booleen sur la regularite
	public static int[] calculIdentiteDict(Hashtable<Integer, ArrayList<Integer>> graphe, boolean suivi, boolean reguliereDiag) {
		int s = graphe.size();
		Hashtable<Integer, Hashtable<Integer, Integer>> lap = laplacienneDict(graphe, s + 1);
		int[] configuration = configurationCritiqueDict(lap, s + 1);
		int[] tmp = Maths.multMatInt(configuration, 2);
		stabDict(graphe, tmp, lap, s + 1, reguliereDiag);
		tmp = Maths.subMatMat(Maths.multMatInt(configuration, 2), tmp);
		if (!suivi)
			stabDict(graphe, tmp, lap, s + 1, reguliereDiag);
		else {
			int taille = (int) Math.sqrt(s);
			stabSuiviDict(graphe, tmp, lap, s + 1, reguliereDiag, taille, taille);
		}
		return tmp;
	}
	//stabilise pour le graphe feuille (exactement pareil que stabDictDN avec comme valeur de la diagonale = -4)
	public static int[] stabDictOGF(Hashtable<Integer, ArrayList<Integer>> graphe, int[] configuration,
			Hashtable<Integer, Hashtable<Integer, Integer>> lap, int s) {
		return stabDict(graphe, configuration, lap, s, true);
	}
	
	//calcul de la stabilisation d'une configuration avec un booleen sur la regularite
	public static int[] stabDict(Hashtable<Integer, ArrayList<Integer>> graphe, int[] configuration,
			Hashtable<Integer, Hashtable<Integer, Integer>> lap, int n, boolean reguliereDiag) {
		int diag = lap.get(1).get(1);
		ArrayList<Integer> si = sitesInstablesDict(lap, configuration, n, reguliereDiag, diag);
		int k = 0;
		while (si.size()>0) {
			k+=1;
			unStabDict(graphe, configuration, lap, reguliereDiag, diag, si);
			si = sitesInstablesDict(lap, configuration, n, reguliereDiag, diag);
		}
		System.out.println(k);
		return configuration;
	}

	//stabilise pour le graphe feuille avec un rendu image
	public static int[] stabSuiviDictOGF(Hashtable<Integer, ArrayList<Integer>> graphe, int[] configuration,
			Hashtable<Integer, Hashtable<Integer, Integer>> lap, int n) {
		return stabSuiviDict(graphe, configuration, lap, n, true, n, n);
	}

	public static int[] stabSuiviDict(Hashtable<Integer, ArrayList<Integer>> graphe, int[] configuration,
			Hashtable<Integer, Hashtable<Integer, Integer>> lap, int n, boolean reguliereDiag, int w, int h) {
		int couleurmaximale = Integer.MIN_VALUE;
		for (int i = 0; i < configuration.length; i++)
			couleurmaximale = Math.max(couleurmaximale, configuration[i]);
		int r = 1;
		rendu.setColorRange(couleurmaximale, 0);
		rendu.save(PREFIXE + "-" + String.format("%05d", 0), configuration, w, h);
		int diag = lap.get(1).get(1);
		ArrayList<Integer> si = sitesInstablesDict(lap, configuration, n, reguliereDiag, diag);
		while (si.size()>0) {
			unStabDict(graphe, configuration, lap, reguliereDiag, diag, si);
			si = sitesInstablesDict(lap, configuration, n, reguliereDiag, diag);
			rendu.save(PREFIXE + "-" + String.format("%05d", r++), configuration, w, h);
		}
		return configuration;
	}

	public static float[] fluxcarremoyen3neighbours(int Nbrdessais, int N, int M) {
		float[] resultat = new float[M+1];
		Hashtable <Integer, ArrayList<Integer>> gP = Graphes.graphe3NeighbourDN(N, M);
		Hashtable<Integer, Hashtable<Integer, Integer>> lap = laplacienneDict(gP, N*M+1);
		int diag = 3;
		for (int i = 0; i < Nbrdessais; i++) {
			int k = 0;
			int configuration[] = configurationAleatoire(N*M+1, 2); //max
			configuration[N/2 + 1] = 3; //destabilise la configuration
			ArrayList<Integer> si = sitesInstablesDictDN(lap, configuration, 0, N, diag);
			while (si.size()>0 & k < M) {
				unStabDict(gP, configuration, lap, true, diag, si);
				k++;
				si = sitesInstablesDictDN(lap, configuration, k, N, diag);
				resultat[k] += si.size()*si.size();
			}
		}
		for (int k = 0; k< M; k++) {
			resultat[k] = resultat[k]/Nbrdessais;
		}
		return resultat;
	}

	//effectue un topple de tous les sites sur un graphe quelconque avec un booleen sur la regularite
	public static void unStabDict(Hashtable<Integer, ArrayList<Integer>> graphe, int[] configuration,
			Hashtable<Integer, Hashtable<Integer, Integer>> lap, boolean reguliereDiag, int diag, ArrayList<Integer> si) {
		if (reguliereDiag = false) {
			for (int x : si) {
				if (lap.get(x) != null) {
					configuration[x] -= lap.get(x).get(x);
					for (int y : graphe.get(x))
						configuration[y] -= lap.get(x).get(y);
				}
			}
		} else {
			for (int x : si) {
				if (lap.get(x) != null) {
					configuration[x] -= diag;
					for (int y : graphe.get(x))
						configuration[y] -= -1;
				}
			}
		}
	}

	//renvoie les sites instables pour un graphe regulier i.e de hauteur >=diag
	public static ArrayList<Integer> sitesInstablesDictDN(Hashtable<Integer, Hashtable<Integer, Integer>> lap,
			int[] configuration, int h, int n, int diag) {
		ArrayList<Integer> J = new ArrayList<Integer>();

		for (int i = 1; i < n+1; i++)
			if (lap.get(h*n + i) != null) {
				if (configuration[h*n+i] >= diag)
					J.add(h*n+i);
			} else {
				if (configuration[i] >= 0)
					J.add(i);
			}

		return J;
	}

	//calcul de la configuration critique pour un graphe quelconque
	//i.e chaque site est a sa hauteur maximale de stabilite (=flux sortant max de i -1) 
	public static int[] configurationCritiqueDict(Hashtable<Integer, Hashtable<Integer, Integer>> lap, int lg) {
		int[] configuration = new int[lg];
		for (int i = 1; i < lg; i++)
			if (lap.get(i) != null)
				configuration[i] = lap.get(i).get(i) - 1;
			else
				configuration[i] = -1;
		return configuration;
	}

	//verifie si une configuration sur un graphe quelconque est stable
	public static boolean estStableDict(Hashtable<Integer, Hashtable<Integer, Integer>> lap, int[] configuration, int n,
			boolean reguliereDiag, int diag) {
		if (reguliereDiag = false) {
			for (int i = 1; i < n; i++)
				if (lap.get(i) != null) {
					if (configuration[i] >= lap.get(i).get(i))
						return false;
				} else if (configuration[i] >= 0)
					return false;
			return true;
		} else {
			for (int i = 1; i < n; i++)
				if (lap.get(i) != null) {
					if (configuration[i] >= diag)
						return false;
				} else if (configuration[i] >= 0)
					return false;
			return true;
		}
	}

	//calcul des sites instables sur un graphe quelconque avec booleen sur la regularite
	public static ArrayList<Integer> sitesInstablesDict(Hashtable<Integer, Hashtable<Integer, Integer>> lap,
			int[] configuration, int n, boolean reguliereDiag, int diag) {
		ArrayList<Integer> J = new ArrayList<Integer>();
		if (reguliereDiag = false) {
			for (int i = 1; i < n; i++)
				if (lap.get(i) != null) {
					if (configuration[i] >= lap.get(i).get(i))
						J.add(i);
				} else {
					if (configuration[i] >= 0)
						J.add(i);
				}
		} else {
			for (int i = 1; i < n; i++)
				if (lap.get(i) != null) {
					if (configuration[i] >= diag)
						J.add(i);
				} else {
					if (configuration[i] >= 0)
						J.add(i);
				}
		}
		return J;
	}

	//renvoie les sites instables pour le graphe feuille i.e de hauteur >=4
	public static ArrayList<Integer> sitesInstablesDictOGF(Hashtable<Integer, Hashtable<Integer, Integer>> lap,
			int[] configuration, int n) {
		ArrayList<Integer> J = new ArrayList<Integer>();

		for (int i = 1; i < n; i++)
			if (lap.get(i) != null) {
				if (configuration[i] >= 4)
					J.add(i);
			} else {
				if (configuration[i] >= 0)
					J.add(i);
			}

		return J;
	}
}
