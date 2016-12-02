Pour lancer le programme sur Windows (Dossier Windows) :

	Ouvrir un terminal dans le dossier contenant RushHourSolver.jar et faire :

		java -jar MOGPL.jar

	On peut aussi double-clic sur le fichier jar


Pour lancer le programme sur Linux (Dossier Linux) (testé sur les PC de l'université) :

	Lancer le script sh avec :
		
		. script.h


La résolution par Gurobi se base sur la lib GurobiJni70 pour Windows et GurobiJni60 pour Linux. 
Pour changer de librairie, il faut changer le .jar du dossier RushHourSolver_lib/
Pour Linux, il faut aussi changer la valeur de la variable GUROBI_HOME pour spécifier la nouvelle librairie


Les sources du programme se trouve dans src/



*L'affichage des véhicules se fait en chargeant les fichiers du dossier cars/{THEME}/
Si on ne précise pas de thème, les véhicules affichés seront les véhicules du dossier default/
Si des fichiers n'existent pas ou si le thème n'existe pas, des carrés de couleur remplaceront les images

Pour charger un theme, on ajoute son nom de dossier en paramètre

exemple :

	java -jar MOGPL.jar Pkm

	. script.h Pkm