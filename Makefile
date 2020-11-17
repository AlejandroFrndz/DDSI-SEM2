#//////////////////////////////////////////////////////////////////
#	Joaquín García Venegas                                       
#	 Francisco José Aparicio Martos                               
#	Joaquín Alejandro España Sánchez                             
#	Alejandro Fernández Alcaide  
#	Victor Díaz Bustos                                           
# 
#	SEMINARIO 2 DE LA ASIGNATURA DDSI                  
#	 Curso: 3ºA/A1- Real Betis Balompie                                              
# 	Fichero: Makefile			                                 
#//////////////////////////////////////////////////////////////////

all: compilar usuario

#####################################################
# Regla que muestra información sobre el proyecto 
# realizado
#####################################################
informacion:
	@echo "#############################################################"
	@echo Seminario 2 DDSI
	@echo
	@echo Joaquín García Venegas
	@echo Francisco José Aparicio Martos
	@echo Joaquín Alejandro España Sánchez
	@echo Alejandro Fernández Alcaide
	@echo Victor Díaz Bustos 
	@echo "#############################################################"


#####################################################
# Compilar los archivos fuente
#####################################################
compilar:
	@echo ---Compilacion---
	javac ./*.java 


#####################################################
# Ejecutar el programa
#####################################################
usuario:
	@echo 
	@echo ---Ejecucion del programa-----
	java -cp ojdbc8.jar:. Usuario	


#####################################################
# Limpieza
clean:
	-rm ./*.class