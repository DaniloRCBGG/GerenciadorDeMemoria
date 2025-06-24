# Gerenciador de Memória Virtual — Trabalho SO 1/2025

Um simulador de gerenciamento de memória virtual com paginação, desenvolvido em Java com interface gráfica utilizando Swing, como parte do curso de Sistemas Operacionais.

# Descrição

Este projeto simula o funcionamento de um Gerenciador de Memória (GM), incluindo:

Tabela de Páginas por processo

TLB (Translation Lookaside Buffer) com mapeamento associativo

Políticas de substituição de página que possui 2 implementações: LRU e Relógio (Clock) com bit de uso

Controle de memória física e secundária

Interface gráfica interativa para acompanhar processos, acessos e substituições em tempo real

# Como executar

# Clone o repositório
git clone git@github.com:DaniloRCBGG/GerenciadorDeMemoria.git

# Acesse a pasta
cd GerenciadorDeMemoria

# Compile o projeto (supondo uso de linha de comando)
javac src/*.java

# Execute
java -cp src Main

# Ou, abra o projeto em uma IDE e execute pela interface gráfica.

# Tecnologias Utilizadas

Java 17

Swing (interface gráfica)

Estruturas de dados personalizadas para tabela de páginas e TLB

Entrada simulada por arquivos de script de processos

# Funcionalidades

[x] Simulação de criação e término de processos

[x] Página de entrada/saída por arquivos

[x] Visualização da MP e MS em tempo real

[x] Implementação de políticas de substituição (LRU e Clock)

[x] Registro de faltas de página

[x] Interface amigável para simulação

# Preview da Interface

![Captura de tela 2025-06-24 195148](https://github.com/user-attachments/assets/9197da0e-75d6-4810-ac01-11ae6386b81a)


# Integrantes

Danilo Rios

Renan Martins

Ismael Firmiano

João Cecim

Breno

# Licença

Este projeto foi desenvolvido como parte do curso de Sistemas Operacionais e é distribuído apenas para fins educacionais.
