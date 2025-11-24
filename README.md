# Megaferia GUI
Integrantes del grupo:  
Santiago Florez Nrc: 2461  
Jairo Molina Nrc: 2461  
Jonathan Calles Nrc: 2461  

Se aplicó el análisis del Principio de Sustitución de Liskov (LSP) y se reconoció que las implementaciones del método copiar() no son completamente sustituibles; sin embargo, esta decisión fue intencional y justificada por razones técnicas, ya que existen referencias circulares entre entidades (como Publisher y Stand) que provocarían recursión infinita y errores de tipo StackOverflowError si se utilizara una copia profunda en todos los casos, por lo que se optó por realizar copias parciales controladas según el contexto de uso, priorizando la estabilidad, el rendimiento y la funcionalidad del sistema sobre la aplicación estricta del principio en este caso específico.
