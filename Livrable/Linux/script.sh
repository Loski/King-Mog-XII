export GUROBI_HOME="/opt/gurobi605/linux64"
export PATH="${PATH}:${GUROBI_HOME}/bin"
export LD_LIBRARY_PATH="${LD_LIBRARY_PATH}:${GUROBI_HOME}/lib"
if [[ -z $1 ]]
	then java -Djava.library.path=./RushHourSolver_lib -jar RushHourSolver.jar
else
	java -Djava.library.path=./RushHourSolver_lib -jar RushHourSolver.jar "$1"
fi