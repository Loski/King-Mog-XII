export GUROBI_HOME="/opt/gurobi605/linux64"
export PATH="${PATH}:${GUROBI_HOME}/bin"
export LD_LIBRARY_PATH="${LD_LIBRARY_PATH}:${GUROBI_HOME}/lib"
java -Djava.library.path=./MOG_lib -jar MOG.jar
